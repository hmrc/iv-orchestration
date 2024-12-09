/*
 * Copyright 2024 HM Revenue & Customs
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

import org.scalamock.scalatest.MockFactory
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.testsuite.BaseSpec
import play.api.test.Helpers._


class DocumentationControllerSpec extends BaseSpec with GuiceOneAppPerSuite with MockFactory {
  implicit val hc: HeaderCarrier = HeaderCarrier()

  "provide definition endpoint for each api" in new Setup {
    val result = documentationController.definition()(request)

    contentAsString(result) mustNot contain ("whitelistedApplicationIds")
    status(result) mustBe OK
  }

  trait Setup {
    implicit def materializer: org.apache.pekko.stream.Materializer = app.injector.instanceOf[org.apache.pekko.stream.Materializer]
    val documentationController = app.injector.instanceOf[IvOrchestrationDocumentationController]
    val request = FakeRequest()
  }
}
