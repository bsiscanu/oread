package controllers

import javax.inject.{ Inject, Singleton }
import services.{ AuthService, MetaService, NodeService }
import play.api.mvc._

import scala.concurrent._


@Singleton
class DomyController @Inject()(cc: ControllerComponents, ns: NodeService, as: AuthService, mt: MetaService)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) {


  def read(catalog: String, component: String, version: String, path: String) = Action { request: Request[AnyContent] =>

    val file = this.ns.get(Seq(catalog, component, version, path));

    Ok(file).withHeaders(CACHE_CONTROL -> "max-age=3600");
  }


  def update(catalog: String, component: String, version: String, path: String) = Action { request: Request[AnyContent] =>

    // todo: you have to get the MIME type from request and pass it to the server
    val jws = request.headers.get("X-Domy-Token")

    // username is default catalog
    if (jws.isDefined && as.check(jws.get, catalog)) {

      if (path.contains("web-components.json")) {

        val content = "// App: CommonJS Main".getBytes
        val address = path.replace(
          "web-components.json",
          "index.js"
        );

        this.ns.set(Seq(catalog, component, version, address), content)

        val description = this.mt.describe(
          component,
          version,
          "webcomponent"
        ).getBytes;

        this.ns.set(Seq(catalog, component, version, "package.json"), description)
      }

      val content = request.body.asRaw.get.asBytes().get.toArray;
      this.ns.set(Seq(catalog, component, version, path), content);

      Ok("done");
    } else {

      Unauthorized("User is not authorized");
    }
  }


  def remove(path: String) = Action { request: Request[AnyContent] =>

    val jws = request.headers.get("X-Domy-Token");

    val segments = path.split("/");
    // username is default catalog
    if (jws.isDefined && as.check(jws.get, segments(1))) {

      this.ns.del(path);

      Ok("done");
    } else {

      Unauthorized("User is not authorized");
    }
  }
}


//  def update(app: String, path: String) = Action.async { request: Request[AnyContent] =>
//    val jws = request.headers.get("X-Domy-Token")
//
//    val result = if (jws.isDefined && as.check(jws.get, app)) {
//      val data = request.body.asJson.get.toString()
//
//      val arr = ds.divide(data)
//
//      ds.has(app, data).flatMap(result => {
//        var content = ""
//        if (result) {
//          content = "~/" + data
//        } else {
//          content = data
//        }
//
//        ds.set(app, path + "/" + arr(arr.length - 1), content)
//      })
//
//    } else {
//      Future.failed(new Exception())
//    }
//
//    result.map{ data => Ok{ "true" } }
//      .recover{ case thrown => Unauthorized{ "Not Authorized" }}
//  }
