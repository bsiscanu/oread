package controllers

import javax.inject._
import play.api.mvc._
import services.{ AuthService, UserService }
import scala.concurrent.{ ExecutionContext, Future }


@Singleton
class UserController @Inject()(cc: ControllerComponents, us: UserService, as: AuthService)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) {

  def login(app: String) = Action.async { request: Request[AnyContent] =>
    val key = request.body.asText.get
    us.get(app)
      .map{ token =>
        if (token.length > 0 && token == key) {
          as.sign(app)
        } else {
          ""
        }
      }
      // you have to use exceptions and recover
      .map(result => Ok{ result })
  }

  // signup form on the website
  def signup(app: String) = Action.async { request: Request[AnyContent] =>
    val key = request.body.asText.get
    us.get(app)
      .flatMap{ token =>
        if (token.isEmpty) {
          us.set(app, key)
        } else {
          Future.successful("Storage name is already in use")
        }
      }
      .map(result => Ok { result })
  }

}
