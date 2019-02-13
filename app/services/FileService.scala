package services

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext
import com.google.cloud.storage._
import scala.collection.JavaConverters._
import com.google.cloud.storage.Storage.BlobListOption


@Singleton
class FileService @Inject()(implicit ec: ExecutionContext) {

  val storage: Storage = StorageOptions.getDefaultInstance.getService;
  val bucket = "domy"


  def set(pathway: Seq[String], content: Array[Byte]) = {

    val blobInfo: BlobInfo = BlobInfo.newBuilder(trail(pathway))
//      .setContentType("text/plain")
      .setCacheControl("public, max-age=3600")
      .build;

    storage.create(blobInfo, content).getName;
  }


  def get(pathway: Seq[String]) = {
    storage.get(trail(pathway))
      .getContent()
  }


  def del(pathway: String) = {
    storage.delete(
      BlobId.of(bucket, pathway)
    );
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
    val address = "pkg" +: pathway
    BlobId.of(bucket, address.reduce(_ + "/" + _));
  }
}
