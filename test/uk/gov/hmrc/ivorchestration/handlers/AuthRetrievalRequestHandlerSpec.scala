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

package uk.gov.hmrc.ivorchestration.handlers

import java.util.concurrent.atomic.AtomicBoolean

import cats.Id
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.model.{AuthRetrievalCore, JourneyId}
import uk.gov.hmrc.ivorchestration.services.AuthRetrievalAlgebra
import uk.gov.hmrc.ivorchestration.{BaseSpec, _}

class AuthRetrievalRequestHandlerSpec extends BaseSpec {
  implicit val hc = HeaderCarrier()

  "Given AuthRetrieval" should {
    "JourneyId is generated for iv-session-data" in new AuthRetrievalRequestHandler[Id](algebra) {
      generateIdAndPersist(sampleAuthRetrieval) must matchPattern {
        case AuthRetrievalCore(_, JourneyId(_), _) =>
      }
    }

    "JourneyId is generated and appended to the returned uri" in new AuthRetrievalRequestHandler[Id](algebra) {
      buildUri(JourneyId("3456"), Map("Raw-Request-URI" -> "/iv-orchestration/iv-sessiondata")) mustBe Some("/iv-orchestration/iv-sessiondata/3456")
    }

    "Given AuthRetrieval the requested IV session data record is created and persisted" in new AuthRetrievalRequestHandler[Id](algebra) {
      val authRetrieval = generateIdAndPersist(sampleAuthRetrieval)
      called.get mustBe true
    }
  }

  val called = new AtomicBoolean(false)

  val algebra = new AuthRetrievalAlgebra[Id] {
    override def findAuthRetrievals()(implicit hc: HeaderCarrier): Id[List[AuthRetrievalCore]] = ???
    override def findJourneyIdAndCredId(journeyId: JourneyId, credId: String)(implicit hc: HeaderCarrier): Id[Option[AuthRetrievalCore]] = ???
    override def insertAuthRetrieval(authRetrievalCore: AuthRetrievalCore)(implicit hc: HeaderCarrier): Id[AuthRetrievalCore] = {
      val persisted = sampleAuthRetrievalCore.copy(journeyId = authRetrievalCore.journeyId)
      authRetrievalCore must matchPattern {
        case AuthRetrievalCore(_, JourneyId(_), _) =>
      }
      called.set(true)
      persisted
    }

  }
}
