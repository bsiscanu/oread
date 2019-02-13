package services

import scala.concurrent.{ ExecutionContext, Future }
import com.coreos.jetcd.data.ByteSequence
import javax.inject.{ Inject, Singleton }
import com.coreos.jetcd.Client


@Singleton
class EtcdService @Inject()(implicit ec: ExecutionContext)  {

  // transfer it to config file
  val etcd = "http://localhost:2379";

  val client = Client.builder
    .endpoints(etcd)
    .build
    .getKVClient;


  def get(pathway: Seq[String]): String = {
    val data = client.get(trail(pathway)).get.getKvs;
    if (data.isEmpty) "" else data.get(0).getValue.toStringUtf8;
  }


  def set(pathway: Seq[String], content: String) = {
    val data = ByteSequence.fromString(content);
    client.put(trail(pathway), data).get()
  }


  def del(pathway: Seq[String]) = {
    client.delete(trail(pathway)).get()
  }


  def trail(pathway: Seq[String]): ByteSequence = {
    ByteSequence.fromString(pathway.reduce(_ + "/" + _));
  }
}
