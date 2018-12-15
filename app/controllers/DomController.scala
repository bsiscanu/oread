package controllers

import javax.inject.{ Inject, Singleton }
import services.{ AuthService, DomService }
import play.api.mvc._
import scala.concurrent._


@Singleton
class DomController @Inject()(cc: ControllerComponents, ds: DomService, as: AuthService)
                             (implicit ec: ExecutionContext) extends AbstractController(cc) {


  def read(dir: String, node: String) = Action.async {
    this.ds.get(dir, node).map{ data =>
      Ok{ data }.withHeaders(CACHE_CONTROL -> "max-age=3600")
    };
  }

  def update(dir: String, node: String) = Action.async { request: Request[AnyContent] =>
    val jws = request.headers.get("X-Domy-Token")

    // username is default dir, here we compare dir with username from the token
    val result = if (jws.isDefined && as.check(jws.get, dir)) {
      val content = request.body.asJson.get.toString();
      this.ds.set(dir, node, content);
    } else {
      Future.failed(
        new Exception("User is not authorized to perform the action")
      );
    }

    result.map{ data => Ok{ result.toString }}
      .recover{ case thrown => Unauthorized{ thrown.getMessage }}
  }

  def remove(dir: String, node: String) = Action.async { request: Request[AnyContent] =>
    val jws = request.headers.get("X-Domy-Token")

    // username is default dir, here we compare dir with username from the token
    val result = if (jws.isDefined && as.check(jws.get, dir)) {
      this.ds.del(dir, node)
    } else {
      Future.failed(
        new Exception("User is not authorized to perform the action")
      );
    }

    result.map{ data => Ok{ result.toString }}
      .recover{ case thrown => Unauthorized{ thrown.getMessage }}
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
