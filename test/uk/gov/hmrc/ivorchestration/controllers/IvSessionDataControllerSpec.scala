/*
 * Copyright 2023 HM Revenue & Customs
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

import org.joda.time.{DateTime, DateTimeZone, LocalDate}
import akka.stream.Materializer
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
import uk.gov.hmrc.ivorchestration.config.AppConfig
import uk.gov.hmrc.ivorchestration.connectors.AuthConnector
import uk.gov.hmrc.ivorchestration.handlers.{IvSessionDataRequestHandler, UriPrefix}
import uk.gov.hmrc.ivorchestration.model.api.{ErrorResponses, IvSessionData, IvSessionDataSearchRequest, IvSessionDataSearchResponse}
import uk.gov.hmrc.ivorchestration.model.core.{CredId, IvSessionDataCore, JourneyId}
import uk.gov.hmrc.ivorchestration.model.{DatabaseError, StandaloneJourneyType, UpliftJourneyType}
import uk.gov.hmrc.ivorchestration.repository.IvSessionDataRepository
import uk.gov.hmrc.ivorchestration.testsuite.{BaseSpec, TestData}
import uk.gov.hmrc.mongo.MongoComponent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class IvSessionDataControllerSpec extends BaseSpec with GuiceOneAppPerSuite with BeforeAndAfterEach with MockFactory with TestData {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "returns a 201 Created when a valid AuthRetrieval request" in {
    val result = stubAuthoriseController().ivSessionData()(FakeRequest("POST", "/iv-sessiondata/")
      .withBody(Json.toJson(sampleIvSessionData)))

    header("Location", result).get must include("/iv-orchestration/iv-sessiondata/")
    status(result) mustBe CREATED
  }

  "returns a 201 Created when an invalid AuthRetrieval request and journey type is Standalone" in {
    val controller = new IvSessionDataController(authConnector, headerValidator, service, stubComponent) {
      override val requestsHandler: IvSessionDataRequestHandler = handler
      override  def authorised(): AuthorisedFunction = new AuthorisedFunction(EmptyPredicate) {
        override def apply[A](body: => Future[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = Future.failed(SessionRecordNotFound("wrong"))
      }
    }

    val standaloneIvSessionData = sampleIvSessionData.copy(journeyType = StandaloneJourneyType)

    val result = controller.ivSessionData()(FakeRequest("POST", "/iv-sessiondata").withBody(Json.toJson(standaloneIvSessionData)))

    header("Location", result).get must include("/iv-orchestration/iv-sessiondata/")
    status(result) mustBe CREATED
  }

  "returns a 200 with session data response for a given existing journeyId & matching credId" in {
    val core: IvSessionDataCore = await(service.insertIvSessionData(sampleIvSessionDataCore))

    val result = stubAuthoriseController().searchIvSessionData()(FakeRequest("POST", s"${UriPrefix.uriPrefix}search/")
      .withBody(Json.toJson(IvSessionDataSearchRequest(core.journeyId, core.ivSessionData.credId)))
      .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json"))

    status(result) mustBe OK
    contentAsJson(result) mustBe Json.toJson(IvSessionDataSearchResponse.fromIvSessionDataCore(core))
  }

  "returns a 200 with session data response for a given existing journeyId & no credId" in {
    val sampleIvSessionData: IvSessionData = IvSessionData(None, Some("123455"), 200,
      Some(DateTime.now), Some("123"), Some("AA12 3BB"),
      Some("Jim"), Some("Smith"), Some(LocalDate.now), Some(anyAffinityGroup), Some("User failed IV"),
      Some(1), UpliftJourneyType
    )

    val sampleIvSessionDataCore = IvSessionDataCore(sampleIvSessionData, JourneyId("123"), DateTime.now(DateTimeZone.UTC))

    val core: IvSessionDataCore = await(service.insertIvSessionData(sampleIvSessionDataCore))

    val result = stubAuthoriseController().searchIvSessionData()(FakeRequest("POST", s"${UriPrefix.uriPrefix}search/")
      .withBody(Json.toJson(IvSessionDataSearchRequest(core.journeyId, core.ivSessionData.credId)))
      .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json"))

    status(result) mustBe OK
    contentAsJson(result) mustBe Json.toJson(IvSessionDataSearchResponse.fromIvSessionDataCore(core))
  }

  "returns a 200 with session data response for a given existing journeyId, given no authorization, and a journey type of Standalone" in {
    val controller = new IvSessionDataController(authConnector, headerValidator, service, stubComponent) {
      override val requestsHandler: IvSessionDataRequestHandler = handler
      override  def authorised(): AuthorisedFunction = new AuthorisedFunction(EmptyPredicate) {
        override def apply[A](body: => Future[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = Future.failed(SessionRecordNotFound("wrong"))
      }
    }

    val sampleIvSessionData: IvSessionData = IvSessionData(None, Some("123455"), 200,
      Some(DateTime.now), Some("123"), Some("AA12 3BB"),
      Some("Jim"), Some("Smith"), Some(LocalDate.now), Some(anyAffinityGroup), Some("User failed IV"),
      Some(1), StandaloneJourneyType
    )

    val sampleIvSessionDataCore = IvSessionDataCore(sampleIvSessionData, JourneyId("123"), DateTime.now(DateTimeZone.UTC))

    val core: IvSessionDataCore = await(service.insertIvSessionData(sampleIvSessionDataCore))

    val result = controller.searchIvSessionData()(FakeRequest("POST", s"${UriPrefix.uriPrefix}search/")
      .withBody(Json.toJson(IvSessionDataSearchRequest(core.journeyId, core.ivSessionData.credId)))
      .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json"))

    status(result) mustBe OK
    contentAsJson(result) mustBe Json.toJson(IvSessionDataSearchResponse.fromIvSessionDataCore(core))
  }

  "returns a 403 FORBIDDEN for a given existing journeyId & mismatched credId" in {
    val core: IvSessionDataCore = await(service.insertIvSessionData(sampleIvSessionDataCore))

    val result = stubAuthoriseController().searchIvSessionData()(FakeRequest("POST", s"${UriPrefix.uriPrefix}search/")
      .withBody(Json.toJson(IvSessionDataSearchRequest(core.journeyId, Some(CredId("Mismatched")))))
      .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json"))

    status(result) mustBe FORBIDDEN
    contentAsJson(result) mustBe Json.toJson(ErrorResponses.forbidden)
  }

  "returns a 403 FORBIDDEN for a given existing journeyId & credId when credId is not specified in the search" in {
    val core: IvSessionDataCore = await(service.insertIvSessionData(sampleIvSessionDataCore))

    val result = stubAuthoriseController().searchIvSessionData()(FakeRequest("POST", s"${UriPrefix.uriPrefix}search/")
      .withBody(Json.toJson(IvSessionDataSearchRequest(core.journeyId, None)))
      .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json"))

    status(result) mustBe FORBIDDEN
    contentAsJson(result) mustBe Json.toJson(ErrorResponses.forbidden)
  }

  "returns a 403 FORBIDDEN for a given existing journeyId with no credId when credId is specified in the search" in {
    val sampleIvSessionData: IvSessionData = IvSessionData(None, Some("123455"), 200,
      Some(DateTime.now), Some("123"), Some("AA12 3BB"),
      Some("Jim"), Some("Smith"), Some(LocalDate.now), Some(anyAffinityGroup), Some("User failed IV"),
      Some(1), UpliftJourneyType
    )

    val sampleIvSessionDataCore = IvSessionDataCore(sampleIvSessionData, JourneyId("123"), DateTime.now(DateTimeZone.UTC))

    val core: IvSessionDataCore = await(service.insertIvSessionData(sampleIvSessionDataCore))

    val result = stubAuthoriseController().searchIvSessionData()(FakeRequest("POST", s"${UriPrefix.uriPrefix}search/")
      .withBody(Json.toJson(IvSessionDataSearchRequest(core.journeyId, Some(CredId{"Some cred id"}))))
      .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json"))

    status(result) mustBe FORBIDDEN
    contentAsJson(result) mustBe Json.toJson(ErrorResponses.forbidden)
  }

  "returns a 401 UNAUTHORIZED if not authorised" in {
    val controller = new IvSessionDataController(authConnector, headerValidator, service, stubComponent) {
      override val requestsHandler: IvSessionDataRequestHandler = handler
        override  def authorised(): AuthorisedFunction = new AuthorisedFunction(EmptyPredicate) {
          override def apply[A](body: => Future[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = Future.failed(SessionRecordNotFound("wrong"))
        }
      }

    val result = controller.ivSessionData()(FakeRequest("POST", "/iv-sessiondata").withBody(Json.toJson(sampleIvSessionData)))

    status(result) mustBe UNAUTHORIZED
    contentAsJson(result) mustBe Json.toJson(ErrorResponses.unAuthorized)
  }

  "returns a 500 for unexpected error" in {
    val controller = new IvSessionDataController(authConnector, headerValidator, service, stubComponent) {
      override val requestsHandler: IvSessionDataRequestHandler = handler
        override  def authorised(): AuthorisedFunction = new AuthorisedFunction(EmptyPredicate) {
          override def apply[A](body: => Future[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = Future.failed(DatabaseError)
        }
      }

    val result = controller.ivSessionData()(FakeRequest("POST", "/iv-sessiondata")
      .withBody(Json.toJson(sampleIvSessionData)))

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
      .withBody(Json.toJson(IvSessionDataSearchRequest(JourneyId("123"), Some(CredId("456")))))
      .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json"))

    status(result) mustBe NOT_FOUND
    contentAsJson(result) mustBe Json.toJson(ErrorResponses.notFound)
  }

  val mongoComponent: MongoComponent = app.injector.instanceOf[MongoComponent]
  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val service = new IvSessionDataRepository(mongoComponent, appConfig)
  private val handler = new IvSessionDataRequestHandler(service)
  private val authConnector = mock[AuthConnector]

  private def injector: Injector = app.injector
  implicit lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
  implicit lazy val materializer: Materializer = app.materializer

  override def beforeEach(): Unit = await(service.collection.drop().toFuture())
  override def afterEach(): Unit = await(service.collection.drop().toFuture())

  private val stubComponent = stubControllerComponents()

  private val headerValidator =  new HeaderValidator(stubComponent)

  def stubAuthoriseController(): IvSessionDataController = new IvSessionDataController(authConnector, headerValidator, service, stubComponent) {
    override val requestsHandler: IvSessionDataRequestHandler = handler
    override  def authorised(): AuthorisedFunction = new AuthorisedFunction(EmptyPredicate) {
      override def apply[A](body: => Future[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = body
    }
  }
}