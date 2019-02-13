package controllers

import javax.inject._
import play.api.mvc._
import services.{ AuthService, UserService }
import scala.concurrent.{ ExecutionContext, Future }


@Singleton
class UserController @Inject()(cc: ControllerComponents, us: UserService, as: AuthService)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) {


  def login() = Action { request: Request[AnyContent] =>

    val content = request.body.asJson.get;

    val user = (content \ "user").get.as[String].toString;
    val pass = (content \ "pass").get.as[String].toString;

    val entity = us.get(user);

    if (
      entity.contains("pass") &&
      entity("pass") == pass
    ) {

      Ok(as.sign(user));
    } else {

      Unauthorized("User was not found");
    }
  }


  def signup = Action { request: Request[AnyContent] =>

    val content = request.body.asJson.get;

    val user = (content \ "user").get.as[String]; // toString();
    val pass = (content \ "pass").get.as[String];
    val mail = (content \ "mail").get.as[String];

    val entity = us.get(user);

    if (user.isEmpty || pass.isEmpty || mail.isEmpty) {

      BadRequest("Can't create the account with provided data")
    } else if (entity("user") == user) {

      BadRequest("Username is taken")
    } else {

      us.set(user, pass, mail);
      Ok(as.sign(user));
    }
  }

  def subscribe = Action { request: Request[AnyContent] =>

    val data = request.body.asMultipartFormData.get;
    val content = data.dataParts("email").seq.head;

    us.sub(content);

    Ok("done")
  }
}
