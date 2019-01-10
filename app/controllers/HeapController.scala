package controllers

import javax.inject.{ Inject, Singleton }
import services.{ AuthService, NodeService }
import play.api.mvc._
import scala.concurrent._


@Singleton
class HeapController @Inject()(cc: ControllerComponents, ns: NodeService, as: AuthService)
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

      val content = request.body.asRaw.get.asBytes().get.toArray;
      this.ns.set(Seq(catalog, component, version, path), content);

      Ok("done");
    } else {

      Unauthorized("User is not authorized");
    }
  }


  def remove(catalog: String, component: String, version: String, path: String) = Action { request: Request[AnyContent] =>

    val jws = request.headers.get("X-Domy-Token");

    // username is default catalog
    if (jws.isDefined && as.check(jws.get, catalog)) {

      this.ns.del(Seq(catalog, component, version, path));

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