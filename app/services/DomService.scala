package services

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class DomService @Inject()(js: JetcdService, ns: NetcdService)(implicit ec: ExecutionContext) {

  def get(dir: String, node: String) = {
    js.get("lib", dir, node);
  }

  def set(dir: String, node: String, content: String): Future[String] = {
    js.set("lib", dir, node, content);
  }

  def del(dir: String, node: String) = {
    js.del("lib", dir, node);
  }

  def has(dir: String, node: String): Future[String] = {
    ns.has("dom", dir, node);
  }

  def modularize(origin: Option[String], content: String) : String = {
    if (origin.isDefined && origin.get == "web") {
      content.replace("module.exports=", "return ")
    } else {
      content
    }
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

