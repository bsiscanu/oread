package services

import com.coreos.jetcd.Client
import com.coreos.jetcd.data.ByteSequence
import com.google.common.io.BaseEncoding
import javax.inject.{ Inject, Singleton }
import play.api.libs.ws._
import scala.concurrent.{ ExecutionContext, Future }


@Singleton
class StoreService @Inject()(ws: WSClient)(implicit ec: ExecutionContext)  {

  val url: String = "http://localhost:2379/v2/keys/"
  val client = Client.builder.endpoints("http://localhost:2379").build
  val kvClient = client.getKVClient


  def getDom(kind: String, dir: String, name: String): Future[Seq[String]] = {
    ws.url(url + kind + "/" + dir + "/" + name + "?recursive=true").get
      .map{ result =>
        try {
          (result.json \\ "value").map(_.as[String])
        } catch {
          case e: Exception => Seq("")
        }
      }
  }

  def setDom(kind: String, dir: String, name: String, content: Any): Future[Boolean] = {
    ws.url(url + kind + "/" + dir + "/" + name + "/opt")
      .addHttpHeaders("Content-Type" -> "application/x-www-form-urlencoded")
      .put("value=" + content)
      .map(result => (result.json \ "node" \ "modifiedIndex").get.toString)
      .map(result => if (result.toInt > 0) true else false)
  }

  def hasDom(kind: String, dir: String, name: String): Future[Boolean] = {
    ws.url(url + kind + "/" + dir + "/" + name).head
      .map(result => if (result.status == 200) true else false)
  }

  def delDom(kind: String, dir: String, name: String): Future[Boolean] = {
    ws.url(url + kind + "/" + dir + "/" + name + "?recursive=true&dir=true").delete
      .map(result => (result.json \ "node" \ "modifiedIndex").get.toString)
      .map(result => if (result.toInt > 0) true else false)
  }


  def getNode(kind: String, app: String, name: String): Future[String] = {
    val key = ByteSequence.fromString(kind + "/" + app + "/" + name)

    Future{
      val list = kvClient.get(key).get.getKvs

      if (list.isEmpty) {
        ""
      } else {
        list.get(0).getValue.toStringUtf8
      }

    }
  }

  def setNode(kind: String, app: String, name: String, content: String): Future[String] = {
    val key = ByteSequence.fromString(kind + "/" + app + "/" + name)
    val value = ByteSequence.fromString(content)

    Future(
      kvClient.put(key, value).get.getPrevKv.getValue.toStringUtf8
    )
  }

  def delNode(kind: String, app: String, name: String): Future[String] = {
    val key = ByteSequence.fromString(kind + app + name)

    Future (
      kvClient.delete(key).get.getPrevKvs.get(0).getValue.toString()
    )
  }

  def encode(data: String): String = BaseEncoding.base64.encode(data.getBytes)
  def decode(data: String): String = BaseEncoding.base64.decode(data).toString

}
