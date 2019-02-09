package services

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext


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



// Example of data extraction from the DOM tree
//    this.get(app, node)
//      .map(data =>
//        data.distinct
//          .filter(name => name != node)
//          .map(name => {
//            if (el.contains("~/")) {
//              collect(app, name)
//            } else {
//              ns.get(app, name)
//            }
//          })
//      )
//      .flatMap(data => Future.sequence(data))
//      .map { data =>
//        try {
//          data.reduceLeft(_ + " \n" + _)
//        } catch {
//          case e: Exception => ""
//        }
//      }
//      .flatMap(data => ns.get(app, last)
//        .map(el => data + " \n return " + el)
//      );
//  }

//Example of content replace
//
//def modularize(content: String) : String = {
//  content.replace("module.exports=", "return ")
//}
