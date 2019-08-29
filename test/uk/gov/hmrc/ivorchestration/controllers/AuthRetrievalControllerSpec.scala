/*
 * Copyright 2019 HM Revenue & Customs
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

import akka.stream.Materializer
import cats.instances.future._
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import play.api.inject.Injector
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.authorise.EmptyPredicate
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.config.MongoDBClient
import uk.gov.hmrc.ivorchestration.connectors.AuthConnector
import uk.gov.hmrc.ivorchestration.handlers.AuthRetrievalRequestHandler
import uk.gov.hmrc.ivorchestration.model.{AuthRetrieval, UnexpectedState}
import uk.gov.hmrc.ivorchestration.persistence.ReactiveMongoConnector
import uk.gov.hmrc.ivorchestration.services.AuthRetrievalDBService
import uk.gov.hmrc.ivorchestration.{BaseSpec, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AuthRetrievalControllerSpec extends BaseSpec with GuiceOneAppPerSuite with MongoDBClient with BeforeAndAfterEach with MockFactory {
  implicit val hc = HeaderCarrier()

  "returns a 201 Created when a valid AuthRetrieval request" in {
    val controller = new AuthRetrievalController(authConnector, stubControllerComponents()) {
      override val requestsHandler: AuthRetrievalRequestHandler[Future] = handler
      override  def authorised(): AuthorisedFunction = new AuthorisedFunction(EmptyPredicate) {
        override def apply[A](body: => Future[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = body
      }
    }
    
    val result = controller.ivSessionData()(FakeRequest("POST", "/iv-sessiondata").withBody(sampleAuthRetrieval))
    val actual = contentAsJson(result).as[AuthRetrieval]

    val expectedRetrieval = sampleAuthRetrieval.copy(journeyId = actual.journeyId, loginTimes = actual.loginTimes, dateOfbirth = actual.dateOfbirth)

    status(result) mustBe CREATED
    actual mustBe expectedRetrieval
  }

  "returns a 401 UNAUTHORIZED if not authorised" in {
    val controller = new AuthRetrievalController(authConnector, stubControllerComponents()) {
      override val requestsHandler: AuthRetrievalRequestHandler[Future] = handler
      override  def authorised(): AuthorisedFunction = new AuthorisedFunction(EmptyPredicate) {
        override def apply[A](body: => Future[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = Future.failed(UnexpectedState("wrong"))
      }
    }

    val result = controller.ivSessionData()(FakeRequest("POST", "/iv-sessiondata").withBody(sampleAuthRetrieval))

    status(result) mustBe UNAUTHORIZED
  }

  "returns a 400 BAD_REQUEST for an invalid AuthRetrieval request" in {
    val result = controller.ivSessionData()(FakeRequest("POST", "/iv-sessiondata")
      .withBody(Json.parse("""{ "k": "v"}"""))
        .withHeaders("Content-Type" -> "application/json")
    )
    status(result) mustBe BAD_REQUEST
  }

  private val service = new AuthRetrievalDBService(ReactiveMongoConnector(mongoConnector))
  private val handler = new AuthRetrievalRequestHandler[Future](service)
  private val authConnector = mock[AuthConnector]

  private val controller = new AuthRetrievalController(authConnector, stubControllerComponents()) {
    override val requestsHandler: AuthRetrievalRequestHandler[Future] = handler
  }

  private def injector: Injector = app.injector
  implicit lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
  implicit lazy val materializer: Materializer = app.materializer

  override def beforeEach(): Unit = await(service.removeAll())
  override def afterEach(): Unit = await(service.removeAll())
}