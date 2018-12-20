package services

import scala.concurrent.{ ExecutionContext, Future }
import com.coreos.jetcd.data.ByteSequence
import javax.inject.{ Inject, Singleton }
import com.coreos.jetcd.Client


@Singleton
class JetcdService @Inject()(implicit ec: ExecutionContext)  {

  val kv = Client.builder.endpoints("http://localhost:2379").build.getKVClient;

  def get(base: String, medium: String, top: String): Future[String] = {
    Future{
      val list = kv.get(path(base, medium, top)).get.getKvs;
      if (list.isEmpty) "" else list.get(0).getValue.toStringUtf8;
    };
  }

  def set(base: String, medium: String, top: String, content: String): Future[String] = {
    val value = ByteSequence.fromString(content);
    Future(
      kv.put(path(base, medium, top), value).get.getPrevKv.getValue.toStringUtf8
    );
  }

  def del(base: String, medium: String, top: String): Future[String] = {
    Future (
      kv.delete(path(base, medium, top)).get.getPrevKvs.get(0).getValue.toStringUtf8
    );
  }

  def path(base: String, medium: String, top: String): ByteSequence =
    ByteSequence.fromString(base + "/" + medium + "/" + top);
}
