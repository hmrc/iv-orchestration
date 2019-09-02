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
import uk.gov.hmrc.ivorchestration.model.core.{IvSessionDataCore, JourneyId}
import uk.gov.hmrc.ivorchestration.services.IvSessionDataRepositoryAlgebra
import uk.gov.hmrc.ivorchestration.{BaseSpec, _}

class IvSessionDataRequestHandlerSpec extends BaseSpec {
  implicit val hc = HeaderCarrier()

  "Given AuthRetrieval" should {
    "JourneyId is generated for iv-session-data" in new IvSessionDataRequestHandler[Id](algebra) {
      generateIdAndPersist(sampleIvSessionData) must matchPattern {
        case IvSessionDataCore(_, JourneyId(_), _) =>
      }
    }

    "JourneyId is generated and appended to the returned uri" in new IvSessionDataRequestHandler[Id](algebra) {
      buildUri(JourneyId("3456"), Map("Raw-Request-URI" -> "/iv-orchestration/iv-sessiondata")) mustBe Some("/iv-orchestration/iv-sessiondata/3456")
    }

    "Given AuthRetrieval the requested IV session data record is created and persisted" in new IvSessionDataRequestHandler[Id](algebra) {
      val authRetrieval = generateIdAndPersist(sampleIvSessionData)
      called.get mustBe true
    }
  }

  val called = new AtomicBoolean(false)

  val algebra = new IvSessionDataRepositoryAlgebra[Id] {
    override def retrieveAll(): Id[List[IvSessionDataCore]] = ???
    override def findByKey(journeyId: JourneyId, credId: String): Id[Option[IvSessionDataCore]] = ???
    override def insertIvSessionData(ivSessionDataCore: IvSessionDataCore): Id[IvSessionDataCore] = {
      val persisted = sampleIvSessionDataCore.copy(journeyId = ivSessionDataCore.journeyId)
      ivSessionDataCore must matchPattern {
        case IvSessionDataCore(_, JourneyId(_), _) =>
      }
      called.set(true)
      persisted
    }

  }
}
