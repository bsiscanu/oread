package services

import com.google.common.io.BaseEncoding
import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import play.api.libs.ws._


@Singleton
class NetcdService @Inject()(ws: WSClient)(implicit ec: ExecutionContext){

  val pw: String = "http://localhost:2379/v2/keys/";

  def get(base: String, medium: String, top: String): Future[Seq[String]] = {
    ws.url(pw + encode(base, medium, top) + "?recursive=true").get
      .map{ result =>
        try {
          (result.json \\ "value").map(_.as[String]);
        } catch {
          case e: Exception => Seq("");
        }
      };
  }

  def set(base: String, medium: String, top: String, layer: String): Future[String] = {
    ws.url(pw + encode(base, medium, top) + "/opt")
      .addHttpHeaders("Content-Type" -> "application/x-www-form-urlencoded")
      .put("value=" + layer)
      .map(result => (result.json \ "node" \ "modifiedIndex").get.toString)
      .map(result => if (result.toInt > 0) "true" else "false");
  }

  def has(base: String, medium: String, top: String): Future[String] = {
    ws.url(pw + encode(base, medium, top)).head
      .map(result => if (result.status == 200) "true" else "false");
  }

  def del(base: String, medium: String, top: String): Future[String] = {
    ws.url(pw + encode(base, medium, top) + "?recursive=true&dir=true").delete
      .map(result => (result.json \ "node" \ "modifiedIndex").get.toString)
      .map(result => if (result.toInt > 0) "true" else "false");
  }

  def encode(base: String, medium: String, top: String): String =
    BaseEncoding.base64.encode((base + "/" + medium + "/" + top).getBytes);
  def decode(data: String): String = BaseEncoding.base64.decode(data).toString;
}
