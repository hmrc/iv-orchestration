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

import akka.stream.Materializer
import cats.instances.future._
import com.olegpy.meow.hierarchy._
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import play.api.inject.Injector
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.SessionRecordNotFound
import uk.gov.hmrc.auth.core.authorise.EmptyPredicate
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.config.MongoDBClient
import uk.gov.hmrc.ivorchestration.connectors.AuthConnector
import uk.gov.hmrc.ivorchestration.handlers.{IvSessionDataRequestHandler, UriPrefix}
import uk.gov.hmrc.ivorchestration.model.DatabaseError
import uk.gov.hmrc.ivorchestration.model.api.{ErrorResponses, IvSessionDataSearchRequest, IvSessionDataSearchResponse}
import uk.gov.hmrc.ivorchestration.model.core.{CredId, IvSessionDataCore, JourneyId}
import uk.gov.hmrc.ivorchestration.persistence.ReactiveMongoConnector
import uk.gov.hmrc.ivorchestration.repository.IvSessionDataRepository
import uk.gov.hmrc.ivorchestration.testsuite.{BaseSpec, TestData}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class IvSessionDataControllerSpec extends BaseSpec with GuiceOneAppPerSuite with MongoDBClient with BeforeAndAfterEach with MockFactory with TestData {
  implicit val hc = HeaderCarrier()

  "returns a 201 Created when a valid AuthRetrieval request" in {
    val result = stubAuthoriseController().ivSessionData()(FakeRequest("POST", "/iv-sessiondata/")
      .withBody(Json.toJson(sampleIvSessionData)))

    header("Location", result).get must include("/iv-orchestration/iv-sessiondata/")
    status(result) mustBe CREATED
  }

  "returns a 200 with session data response for a given existing journeyId & credId" in {
    val core: IvSessionDataCore = await(service.insertIvSessionData(sampleIvSessionDataCore))

    val result = stubAuthoriseController().searchIvSessionData()(FakeRequest("POST", s"${UriPrefix.uriPrefix}search/")
      .withBody(Json.toJson(IvSessionDataSearchRequest(core.journeyId, core.ivSessionData.credId)))
      .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json"))

    status(result) mustBe OK
    contentAsJson(result) mustBe Json.toJson(IvSessionDataSearchResponse.fromIvSessionDataCore(core))
  }

  "returns a 401 UNAUTHORIZED if not authorised" in {
    val controller = new IvSessionDataController(authConnector, headerValidator, stubComponent) {
      override val requestsHandler: IvSessionDataRequestHandler[Future] = handler
        override  def authorised(): AuthorisedFunction = new AuthorisedFunction(EmptyPredicate) {
          override def apply[A](body: => Future[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = Future.failed(SessionRecordNotFound("wrong"))
        }
      }

    val result = controller.ivSessionData()(FakeRequest("POST", "/iv-sessiondata").withBody(Json.toJson(sampleIvSessionData)))

    status(result) mustBe UNAUTHORIZED
    contentAsJson(result) mustBe Json.toJson(ErrorResponses.unAuthorized)
  }

  "returns a 500 for unexpected error" in {
    val core: IvSessionDataCore = await(service.insertIvSessionData(sampleIvSessionDataCore))

    val controller = new IvSessionDataController(authConnector, headerValidator, stubComponent) {
      override val requestsHandler: IvSessionDataRequestHandler[Future] = handler
        override  def authorised(): AuthorisedFunction = new AuthorisedFunction(EmptyPredicate) {
          override def apply[A](body: => Future[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = Future.failed(DatabaseError)
        }
      }

    val result = controller.ivSessionData()(FakeRequest("POST", "/iv-sessiondata")
      .withBody(Json.toJson(IvSessionDataSearchRequest(core.journeyId, core.ivSessionData.credId))))

    status(result) mustBe INTERNAL_SERVER_ERROR
    contentAsJson(result) mustBe Json.toJson(ErrorResponses.internalServerError)
  }

  "returns a 400 BAD_REQUEST for an invalid AuthRetrieval request" in {
    val result = stubAuthoriseController.ivSessionData()(FakeRequest("POST", "/iv-sessiondata")
      .withBody(Json.parse("""{ "k": "v"}"""))
        .withHeaders("Content-Type" -> "application/json"))


    status(result) mustBe BAD_REQUEST
    contentAsString(result) must include("Invalid IvSessionData payload")
  }

  "returns a 400 BAD_REQUEST if the body is invalid" in {
    val result = stubAuthoriseController().searchIvSessionData()(FakeRequest("POST", "/iv-orchestration/session/search/")
      .withBody(Json.parse("""{ "k": "v"}"""))
      .withHeaders("Content-Type" -> "application/json", "Accept" -> "application/vnd.hmrc.1.0+json"))

    status(result) mustBe BAD_REQUEST
    contentAsJson(result) mustBe Json.toJson(ErrorResponses.badRequest)
  }

  "returns a 404 NOT_FOUND if not found in mongo" in {
    val result = stubAuthoriseController().searchIvSessionData()(FakeRequest("POST", "/iv-orchestration/session/search/")
      .withBody(Json.toJson(IvSessionDataSearchRequest(JourneyId("123"), CredId("456"))))
      .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json"))

    status(result) mustBe NOT_FOUND
    contentAsJson(result) mustBe Json.toJson(ErrorResponses.notFound)
  }

  private val service = new IvSessionDataRepository(ReactiveMongoConnector(mongoConnector))
  private val handler = new IvSessionDataRequestHandler[Future](service)
  private val authConnector = mock[AuthConnector]

  private def injector: Injector = app.injector
  implicit lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
  implicit lazy val materializer: Materializer = app.materializer

  override def beforeEach(): Unit = await(service.removeAll())
  override def afterEach(): Unit = await(service.removeAll())

  private val stubComponent = stubControllerComponents()

  private val headerValidator =  new HeaderValidator(stubComponent)

  def stubAuthoriseController(): IvSessionDataController = new IvSessionDataController(authConnector, headerValidator, stubComponent) {
    override val requestsHandler: IvSessionDataRequestHandler[Future] = handler
    override  def authorised(): AuthorisedFunction = new AuthorisedFunction(EmptyPredicate) {
      override def apply[A](body: => Future[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = body
    }
  }
}