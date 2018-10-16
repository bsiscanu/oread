package services

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserService @Inject()(st: StoreService)(implicit ec: ExecutionContext) {

  def get(app: String): Future[String] = {
    st.getNode("usr", st.encode(app), "owner")
  }

  def set(app: String, key: String): Future[String] = {
    st.setNode("usr", st.encode(app), "owner", key)
  }

  def del(app: String, key: String): Unit = {}
}
