package services

import javax.inject.{Inject, Singleton}


@Singleton
class NodeService @Inject()(fs: FileService) {

  def get(options: Seq[String]) = {
    fs.get(options);
  }


  def set(options: Seq[String], content: Array[Byte]): String = {
    fs.set(options, content);
  }


  def del(options: String) = {
    fs.del(options);
  }

}
