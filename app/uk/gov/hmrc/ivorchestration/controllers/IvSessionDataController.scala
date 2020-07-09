/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ivorchestration.controllers

import cats.instances.future._
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json}
import play.api.Logger
import play.api.mvc._
import uk.gov.hmrc.auth.core.{AuthorisedFunctions, NoActiveSession}
import uk.gov.hmrc.ivorchestration.config.MongoDBClient
import uk.gov.hmrc.ivorchestration.connectors.AuthConnector
import uk.gov.hmrc.ivorchestration.handlers.IvSessionDataRequestHandler
import uk.gov.hmrc.ivorchestration.model.api.{IvSessionData, IvSessionDataSearchRequest}
import uk.gov.hmrc.ivorchestration.repository.IvSessionDataRepository
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import uk.gov.hmrc.ivorchestration.model.api.ErrorResponses._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.olegpy.meow.hierarchy._
import uk.gov.hmrc.ivorchestration.model.{DatabaseError, RecordNotFound}

@Singleton()
class IvSessionDataController @Inject()(val authConnector: AuthConnector,
                                        headerValidator: HeaderValidator,
                                        cc: ControllerComponents)
  extends BackendController(cc) with AuthorisedFunctions with MongoDBClient {

  val requestsHandler =
    new IvSessionDataRequestHandler[Future](new IvSessionDataRepository(dbConnector))

  def ivSessionData(): Action[JsValue] =
    controllerAction(parse.json) { implicit request =>
      withJsonBody[IvSessionData] { ivSessionData =>
          requestsHandler.create(ivSessionData)
            .map(loc => Created.withHeaders("Location" -> loc))
      }
    }

  def searchIvSessionData(): Action[JsValue] =
    headerValidator.validateAcceptHeader.async(parse.json) { implicit request =>
      withErrorHandling {
        authorised() {
          request.body.asOpt[IvSessionDataSearchRequest] match {
            case None =>
              Logger.warn(s"Missing IV session data search")
              Future.successful(BadRequest(Json.toJson(badRequest)))
            case Some(ivSessionDataSearch) =>
              requestsHandler.search(ivSessionDataSearch).map { ivSessionData => Ok(Json.toJson(ivSessionData))
              }
          }
        }
      }
    }

  protected def controllerAction[A](bodyParser: BodyParser[A])(block: Request[A] => Future[Result]): Action[A] =
    Action.async(bodyParser) { implicit request =>
      withErrorHandling {
        authorised() {
          block(request)
        }
      }
    }

  private def withErrorHandling(f: => Future[Result]): Future[Result] =
    f.recover {
      case _: NoActiveSession => Unauthorized(Json.toJson(unAuthorized))
      case RecordNotFound     => NotFound(Json.toJson(notFound))
      case DatabaseError      => InternalServerError(Json.toJson(internalServerError))
      case _                  => InternalServerError(Json.toJson(internalServerError))
    }
}
