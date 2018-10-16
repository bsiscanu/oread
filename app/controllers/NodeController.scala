package controllers

import javax.inject.{ Inject, Singleton }
import play.api.mvc._
import services.{ AuthService, NodeService }
import scala.concurrent.{ ExecutionContext, Future }


@Singleton
class NodeController @Inject() (cc: ControllerComponents, ns: NodeService, as: AuthService)
                               (implicit ec: ExecutionContext) extends AbstractController(cc) {


  def read(app: String, node: String) = Action.async {
    ns.get(app, node).map(result => Ok{ result })
  }

  def update(app: String, node: String) = Action.async { request: Request[AnyContent] =>
    val jws = request.headers.get("X-domy-token")

    val result = if (jws.isDefined && as.check(jws.get, app)) {
      val content = request.body.asText.get
      ns.set(app, node, content)
    } else {
      Future.failed(new Exception())
    }

    result.map{ data => Ok{ data } }
      .recover{ case thrown => Unauthorized{ "Not Authorized" }}
  }

  def remove(app: String, name: String) = Action.async { request: Request[AnyContent] =>
    val jws = request.headers.get("X-domy-token")

    val result = if (jws.isDefined && as.check(jws.get, app)) {
      ns.del(app, name)
    } else {
      Future.failed(new Exception())
    }

    result.map{ data => Ok{ data } }
      .recover{ case thrown => Unauthorized{ "Not Authorized" }}
  }

}
