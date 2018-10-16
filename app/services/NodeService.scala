package services

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }


@Singleton
class NodeService @Inject()(st: StoreService)(implicit ec: ExecutionContext) {


  def get(app: String, name: String): Future[String] =  {
    st.getNode("lib", app, name)
  }

  def set(app: String, name: String, content: String): Future[String] = {
    st.setNode("lib", app, name, content)
  }

  def del(app: String, name: String): Future[String] = {
    st.delNode("lib", app, name)
  }

  //todo: you have to keep is one variable or directory all places where the component is used.
  //todo: in this way, on delete it will be removed from all locations. It is not deadly, but it could pollute your dom
}
