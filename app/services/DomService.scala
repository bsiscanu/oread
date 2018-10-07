package services

import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import models.NodeModel

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class DomService @Inject()(st: StoreService)(implicit ec: ExecutionContext) {


  def get(app: String, path: String): Future[Seq[String]] = {
    st.getDom("dom", st.encode(app), prepare(divide(path)))
  }

  def set(app: String, path: String): Future[String] = {
    val arr = divide(path)
    val name = arr(arr.length - 1)

    st.addDom("dom", st.encode(app), prepare(arr) + "/opt", name)
  }

  def divide(path: String): Array[String] = path.split("/")
  def prepare(arr: Array[String]): String = arr.map(st.encode(_)).reduce(_ + "/" + _)

//  def remove(app: String, path: String): Future[WSResponse] = {}

}
