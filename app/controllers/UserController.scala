package controllers

import javax.inject._
import play.api.mvc._
import services.{ AuthService, UserService }
import scala.concurrent.{ ExecutionContext, Future }


@Singleton
class UserController @Inject()(cc: ControllerComponents, us: UserService, as: AuthService)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) {


  def login() = Action.async { request: Request[AnyContent] =>

    val bulk = request.body.asJson.get

    val user = (bulk \ "user").get.toString;
    val pass = (bulk \ "pass").get.toString;

    us.check(user)
      .map{ result =>
        if (result.length > 0 && result == pass) {
          as.sign(user);
        } else {
          throw new Exception("The provided password doesn't match");
        }
      }
      .map{ result => Ok{ result }}
      .recover{ case thrown => Unauthorized{ thrown.getMessage } };
  }

  def signup() = Action.async { request: Request[AnyContent] =>

    val bulk = request.body.asJson.get

    val user = (bulk \ "user").get.toString;
    val pass = (bulk \ "pass").get.toString;
    val mail = (bulk \ "mail").get.toString;

    us.check(user)
      .flatMap{ result =>
        if (result.isEmpty) {
          us.create(user, pass, mail)
            .map(result => as.sign(user))
        } else {
          Future.failed(
            new Exception("Username is already in use")
          );
        }
      }
//      .map(data =>  as.sign(user))
      .map{ response => Ok { response }}
      .recover{ case thrown => Unauthorized{ thrown.getMessage } }
  }

}
