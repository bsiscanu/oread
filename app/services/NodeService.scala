package services

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class NodeService @Inject()(st: StoreService)(implicit ec: ExecutionContext) {


//  def get(app: String, name: String) = {
//    st.get("lib", st.encode(app), st.encode(name) + "/src")
//  }

  def getNode(app: String, name: String): Future[String] =  {
    st.get("lib", app, name)
  }

  def setNode(app: String, name: String, content: String) = {
    st.set("lib", app, name, content)
  }

//  def set(app: String, name: String, content: String): Future[String] = {
//    st.put("lib", st.encode(app), st.encode(name) + "/src", content)
//  }
//
//  def del(app: String, name: String): Future[String] = {
//    for {
//      d1 <- st.delete("lib", st.encode(app), st.encode(name) + "/src")
//      d2 <- st.delete("lib", st.encode(app), st.encode(name))
//    } yield (d2)
//  }

  // where a component is used
//  def getPoint() = {}
//  def addPoint() = {}
//  def delPoint() = {}
//  def listPoints() = {}
}
