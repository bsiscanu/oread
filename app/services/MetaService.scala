package services

import javax.inject.Inject
import play.api.libs.json._

class MetaService @Inject()(fs: FileService) {

  def list(bucket: String, pathway: Seq[String]) = {
    val address = "pkg" +: pathway;
    fs.list(bucket, address.reduce(_ + "/" + _) + "/")
  }

  def describe(component: String, version: String, description: String) = {
    Json.stringify(
      JsObject(Seq(
        "name" -> JsString(component),
        "version" -> JsString(version),
        "description" -> JsString(description),
        "private" -> JsFalse,
        "main" -> JsString("latest/dist/index.js"),
        "types" -> JsString("latest/dist/types/index.d.ts"),
        "collection" -> JsString("latest/dist/collection/collection-manifest.json"),
        "files" -> JsArray(IndexedSeq(JsString("latest/dist")))
      ))
    );
  }
}
