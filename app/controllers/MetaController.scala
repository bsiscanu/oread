package controllers

import javax.inject.{ Inject, Singleton }
import play.api.libs.json.Json
import play.api.mvc._
import services.{ AuthService, MetaService }

import scala.concurrent.ExecutionContext


@Singleton
class MetaController @Inject()(cc: ControllerComponents, ms: MetaService, as: AuthService)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) {


  def health() = Action { Ok("Active") }

  def source(catalog: String, component: String, version: String) = Action {
    Ok(Json.toJson(
      ms.list("domy_test", Seq(catalog, component, version))
    )) // dpkg
  }

  def output(catalog: String, component: String, version: String) = Action {
    Ok(Json.toJson(
      ms.list("dbin", Seq(catalog, component, version))
    ))
  }
}
