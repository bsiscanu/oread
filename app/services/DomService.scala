package services

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class DomService @Inject()(st: StoreService)(implicit ec: ExecutionContext) {


  def get(app: String, path: String): Future[Seq[String]] = {
    st.getDom("dom", st.encode(app), prepare(divide(path)))
  }

  def set(app: String, path: String, content: String): Future[Boolean] = {
    st.setDom("dom", st.encode(app), prepare(divide(path)), content)
  }

  def has(app: String, path: String): Future[Boolean] = {
    st.hasDom("dom", st.encode(app), prepare(divide(path)))
  }

  def del(app: String, path: String): Future[Boolean] = {
    st.delDom("dom", st.encode(app), prepare(divide(path)))
  }

  def divide(path: String): Array[String] = path.split("/")
  def prepare(arr: Array[String]): String = arr.map(st.encode(_)).reduce(_ + "/" + _)
}
