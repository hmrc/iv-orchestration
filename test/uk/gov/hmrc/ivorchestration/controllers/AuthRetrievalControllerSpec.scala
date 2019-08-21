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
import org.joda.time.{DateTime, LocalDate}
import org.scalamock.scalatest.MockFactory
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import play.api.inject.Injector
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.retrieve.{GGCredId, ItmpAddress}
import uk.gov.hmrc.ivorchestration.BaseSpec
import uk.gov.hmrc.ivorchestration.model.AuthRetrieval

class AuthRetrievalControllerSpec extends BaseSpec with MockFactory with GuiceOneAppPerSuite {

  "validate returns a 200 OK when a valid AuthRetrieval has been parse" in {
    val result = controller.ivSessionData()(FakeRequest("POST", "/verification").withBody(authRetrieval))
    status(result) mustBe OK
    contentAsJson(result) mustBe Json.toJson(authRetrieval)
  }

  "validate returns a 400 BAD_REQUEST when a invalid AuthRetrieval has not been parse" in {
    val result = controller.ivSessionData()(FakeRequest("POST", "/verification")
      .withBody(Json.parse("""{ "k": "v"}"""))
        .withHeaders("Content-Type" -> "application/json")
    )
    status(result) mustBe BAD_REQUEST
  }


  private val controller = new AuthRetrievalController(stubControllerComponents())

  private def injector: Injector = app.injector
  implicit lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
  implicit lazy val materializer: Materializer = app.materializer

  val itmpAddress = ItmpAddress(Some("5 Street"),Some("Worthing"),Some("West Sussex"),None,None,Some("BN13 3AS"),Some("England"),Some("44"))
  val authRetrieval = AuthRetrieval(GGCredId("777"), Some("123455"),200,
    Some(DateTime.now),Some("123"),Some(itmpAddress),Some("BN13 3AS"),Some("Matt"),Some("Groom"), Some(LocalDate.now), ttl = 60)
}