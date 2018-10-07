package controllers

import javax.inject.{ Inject, Singleton }
import services.{ AuthService, DomService, NodeService }
import play.api.mvc._
import scala.concurrent._


@Singleton
class DomController @Inject()(cc: ControllerComponents, ds: DomService, ns: NodeService, as: AuthService)
                             (implicit ec: ExecutionContext) extends AbstractController(cc) {

//  val result = Ok("Hello World!").withHeaders(
//    CACHE_CONTROL -> "max-age=3600",
//    ETAG -> "xx")

  def read(app: String, path: String) = Action.async {
    ds.get(app, path)
      .map(data => data.distinct.map(el => ns.getNode(app, el)))
      .flatMap(data => Future.sequence(data))
      .map{ data =>
        try {
          data.reduceLeft(_ + " \n" + _)
        } catch {
          case e: Exception => ""
        }
      }
      .map(response => Ok{ response })
  }

  def update(app: String, path: String) = Action.async { request: Request[AnyContent] =>
    val jws = request.headers.get("X-domy-token")

    val result = if (jws.isDefined && as.check(jws.get, app)) {
      ds.set(app, path)
    } else {
      Future.failed(new Exception())
    }

    result.map{ data => Ok{ data } }
      .recover{ case thrown => Unauthorized{ "Not Authorized" }}  }

  // delete the entire directory recursively
  //  def delete(app: String, path: String) = Action.async {}

}
