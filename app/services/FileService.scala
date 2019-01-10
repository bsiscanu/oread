package services

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext
import com.google.cloud.storage._
import scala.collection.JavaConverters._
import com.google.cloud.storage.Storage.BlobListOption


@Singleton
class FileService @Inject()(implicit ec: ExecutionContext) {

  val storage: Storage = StorageOptions.getDefaultInstance.getService;
  val bucket = "domy_test"
//  val bucket: String = "dpkg"; // four source files
//  val output: String = "dbin"; // for compiled files


  def set(pathway: Seq[String], content: Array[Byte]) = {

    val blobInfo: BlobInfo = BlobInfo.newBuilder(trail(pathway))
      .setContentType("text/plain")
      .setCacheControl("public, max-age=3600")
      .build;

    storage.create(blobInfo, content).getName;
  }


  def get(pathway: Seq[String]) = {

    storage.get(trail(pathway))
      .getContent()
  }


  def del(pathway: Seq[String]) = {

    storage.delete(trail(pathway));
  }

  def list(bucket: String, pathway: String): Iterable[String] = {

      storage.list(
        bucket,
        BlobListOption.currentDirectory,
        BlobListOption.prefix(pathway)
      )
      .iterateAll()
      .asScala
      .flatMap(element => {
        if (element.isDirectory) {
          this.list(bucket, element.getBlobId.getName())
        } else {
          Array(element.getBlobId.getName())
        }
      });
  }


  def trail(pathway: Seq[String]) = {
    BlobId.of(bucket, pathway.reduce(_ + "/" + _));
  }
}
