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
    this.collect(app, path)
      .map(response => Ok{ response })
  }

  private def collect(app: String, path: String): Future[String] = {
    ds.get(app, path)
      .map(data => data.distinct.map(el => {
        if (el.contains("~/")) {
          val data = el.split("/")
          collect(app, data(1))
        } else {
          ns.get(app, el)
        }
      }))
      .flatMap(data => Future.sequence(data))
      .map{ data =>
        try {
          data.reduceLeft(_ + " \n" + _)
        } catch {
          case e: Exception => ""
        }
      }
  }

  def update(app: String, path: String) = Action.async { request: Request[AnyContent] =>
    val jws = request.headers.get("X-domy-token")

    val result = if (jws.isDefined && as.check(jws.get, app)) {
      val arr = ds.divide(path)
      val data = request.body.asText.get

      // todo: Path should contain the name of the struct (last word in the string)
      ds.has(app, data).flatMap(result => {
        var content = ""
        if (result) {
          content = "~/" + data
        } else {
          content = data
        }

        ds.set(app, path, content)
      })

    } else {
      Future.failed(new Exception())
    }

    result.map{ data => Ok{ data.toString } }
      .recover{ case thrown => Unauthorized{ "Not Authorized" }}
  }

  def remove(app: String, path: String) = Action.async { request: Request[AnyContent] =>
    val jws = request.headers.get("X-domy-token")

    val result = if (jws.isDefined && as.check(jws.get, app)) {
      ds.del(app, path)
    } else {
      Future.failed(new Exception())
    }

    result.map{ data => Ok{ data.toString } }
      .recover{ case thrown => Unauthorized{ "Not Authorized" }}
  }

}
