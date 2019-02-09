package services

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext


@Singleton
class UserService @Inject()(es: EtcdService) {

  def sub(email: String) = {
    es.set(Seq("news", email), email)
  }

  def get(user: String) = {
    Map(
      "user" -> es.get(Seq("usr", user, "user")),
      "pass" -> es.get(Seq("usr", user, "pass")),
      "mail" -> es.get(Seq("usr", user, "mail"))
    )
  }


  def set(user: String, pass: String, mail: String) = {
    List(
      es.set(Seq("usr", user, "user"), user),
      es.set(Seq("usr", user, "pass"), pass),
      es.set(Seq("usr", user, "mail"), mail)
    )
  }


  def del(user: String)= {
    List(
      es.del(Seq("usr", user, "user")),
      es.del(Seq("usr", user, "pass")),
      es.del(Seq("usr", user, "mail"))
    )
  }
}
