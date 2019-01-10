package services

import javax.inject.Inject

class MetaService @Inject()(fs: FileService) {

  def list(bucket: String, pathway: Seq[String]) = {
    fs.list(bucket, pathway.reduce(_ + "/" + _) + "/")
  }
}
