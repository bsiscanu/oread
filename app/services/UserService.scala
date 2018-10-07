package services

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserService @Inject()(st: StoreService)(implicit ec: ExecutionContext) {

  def get(app: String): Future[String] = {
    st.get("usr", st.encode(app), "owner")
  }

  def set(app: String, key: String): Future[String] = {
    st.set("usr", st.encode(app), "owner", key)
  }

  def del(app: String, key: String): Unit = {}
}
