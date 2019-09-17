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

import org.scalamock.scalatest.MockFactory
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.testsuite.BaseSpec
import play.api.test.Helpers._


class DocumentationControllerSpec extends BaseSpec with GuiceOneAppPerSuite with MockFactory {
  implicit val hc = HeaderCarrier()


  "provide definition endpoint and documentation endpoint for each api" in new Setup {
    def normalizeEndpointName(endpointName: String): String = endpointName.replaceAll(" ", "-")

    def verifyDocumentationPresent(version: String, endpointName: String) {
      withClue(s"Getting documentation version '$version' of endpoint '$endpointName'") {
        val documentationResult = documentationController.documentation(version, endpointName)(request)
        status(documentationResult) mustBe OK
      }
    }

    val result = documentationController.definition()(request)
    status(result) mustBe OK

    val jsonResponse = contentAsJson(result)

    val versions: Seq[String] = (jsonResponse \\ "version") map (_.as[String])
    val endpointNames: Seq[Seq[String]] = (jsonResponse \\ "endpoints").map(_ \\ "endpointName").map(_.map(_.as[String]))

    versions.zip(endpointNames).flatMap {
      case (version, endpoint) => endpoint.map(endpointName => (version, endpointName))
    }.foreach { case (version, endpointName) => verifyDocumentationPresent(version, endpointName) }
  }

  "provide raml documentation" in new Setup {
    val result = documentationController.raml("1.0", "application.raml")(request)

    status(result) mustBe OK
    contentAsString(result) must startWith("#%RAML 1.0")
  }


//  private val mockAssets = mock[Assets]

  trait Setup {
    implicit def mat: akka.stream.Materializer = app.injector.instanceOf[akka.stream.Materializer]
    val documentationController = app.injector.instanceOf[DocumentationController]
    val request = FakeRequest()
  }
}
