package services

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserService @Inject()(js: JetcdService, ns: NetcdService)(implicit ec: ExecutionContext) {

  def check(user: String): Future[String] = {
    js.get("usr", user, "pass")
  }

  def create(user: String, pass: String, mail: String): Future[Seq[String]] = {

    Future.sequence(Seq(
      js.set("usr", user, "pass", pass),
      js.set("usr", user, "email", mail),
      ns.set("usr", "list", user, user)
    ));
  }

  def del(user: String)= {
    Future.sequence(Seq(
      js.del("usr", user, "pass"),
      js.del("usr", user, "email"),
      ns.del("usr", "list", user)
    ));
  }
}
