// @GENERATOR:play-routes-compiler
// @SOURCE:/home/pasquale/hmrc/dev/iv-orchestration/conf/app.routes
// @DATE:Mon Sep 02 14:23:57 GMT 2019

package app

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
              override val errorHandler: play.api.http.HttpErrorHandler,
              // @LINE:3
              AuthRetrievalController_0: uk.gov.hmrc.ivorchestration.controllers.IvSessionDataController,
              val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:3
    AuthRetrievalController_0: uk.gov.hmrc.ivorchestration.controllers.IvSessionDataController
  ) = this(errorHandler, AuthRetrievalController_0, "/")

  def withPrefix(prefix: String): Routes = {
    app.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, AuthRetrievalController_0, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """iv-sessiondata""", """uk.gov.hmrc.ivorchestration.controllers.AuthRetrievalController.ivSessionData()"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:3
  private[this] lazy val uk_gov_hmrc_ivorchestration_controllers_AuthRetrievalController_ivSessionData0_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("iv-sessiondata")))
  )
  private[this] lazy val uk_gov_hmrc_ivorchestration_controllers_AuthRetrievalController_ivSessionData0_invoker = createInvoker(
    AuthRetrievalController_0.ivSessionData(),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "app",
      "uk.gov.hmrc.ivorchestration.controllers.AuthRetrievalController",
      "ivSessionData",
      Nil,
      "POST",
      this.prefix + """iv-sessiondata""",
      """""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:3
    case uk_gov_hmrc_ivorchestration_controllers_AuthRetrievalController_ivSessionData0_route(params@_) =>
      call { 
        uk_gov_hmrc_ivorchestration_controllers_AuthRetrievalController_ivSessionData0_invoker.call(AuthRetrievalController_0.ivSessionData())
      }
  }
}
