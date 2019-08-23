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
import uk.gov.hmrc.auth.core.retrieve.GGCredId
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.{BaseSpec, _}
import uk.gov.hmrc.ivorchestration.model.AuthRetrieval
import uk.gov.hmrc.ivorchestration.services.AuthRetrievalAlgebra

class AuthRetrievalRequestHandlerSpec extends BaseSpec {
  implicit val hc = HeaderCarrier()

  "Given AuthRetrieval" should {
    "JourneyId is generated for AuthRetrieval" in new AuthRetrievalRequestHandler[Id](algebra) {
      handleAuthRetrieval(sampleAuthRetrieval).journeyId.isDefined mustBe true
    }

    "Given AuthRetrieval the requested IV session data record is created and persisted" in new AuthRetrievalRequestHandler[Id](algebra) {
      val authRetrieval = handleAuthRetrieval(sampleAuthRetrieval)
      called.get mustBe true
    }
  }

  val called = new AtomicBoolean(false)

  val algebra = new AuthRetrievalAlgebra[Id] {
    override def findAuthRetrievals()(implicit hc: HeaderCarrier): Id[List[AuthRetrieval]] = ???
    override def findJourneyIdAndCredId(journeyId: String, credId: GGCredId)(implicit hc: HeaderCarrier): Id[Option[AuthRetrieval]] = ???
    override def insertAuthRetrieval(authRetrieval: AuthRetrieval)(implicit hc: HeaderCarrier): Id[AuthRetrieval] = {
      val persisted = sampleAuthRetrieval.copy(journeyId = authRetrieval.journeyId)
      authRetrieval mustBe persisted
      called.set(true)
      persisted
    }

  }
}
