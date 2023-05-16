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

package uk.gov.hmrc.ivorchestration.handlers

import java.util.concurrent.atomic.AtomicBoolean
import uk.gov.hmrc.ivorchestration.model.core.{IvSessionDataCore, JourneyId}
import uk.gov.hmrc.ivorchestration.repository.IvSessionDataRepositoryAlgebra
import uk.gov.hmrc.ivorchestration.testsuite.{BaseSpec, TestData}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IvSessionDataRequestHandlerSpec extends BaseSpec with TestData {

  val called = new AtomicBoolean(false)

  val algebra: IvSessionDataRepositoryAlgebra = new IvSessionDataRepositoryAlgebra {
    override def retrieveAll(): Future[List[IvSessionDataCore]] = ???
    override def findByJourneyId(journeyId: JourneyId): Future[Option[IvSessionDataCore]] = ???
    override def insertIvSessionData(ivSessionDataCore: IvSessionDataCore): Future[IvSessionDataCore] = {
      val persisted: IvSessionDataCore = sampleIvSessionDataCore.copy(journeyId = ivSessionDataCore.journeyId)
      ivSessionDataCore must matchPattern {
        case IvSessionDataCore(_, JourneyId(_), _) =>
      }
      called.set(true)
      Future.successful(persisted)
    }
  }

  "Given AuthRetrieval" should {
    "JourneyId is generated for iv-session-data" in new IvSessionDataRequestHandler(algebra) {
      create(sampleIvSessionData).map( prefix => prefix mustBe include(UriPrefix.uriPrefix))
    }

    "JourneyId is generated and appended to the returned uri" in new IvSessionDataRequestHandler(algebra) {
      buildUri(JourneyId("3456")) mustBe s"${UriPrefix.uriPrefix}3456"
    }

    "Given AuthRetrieval the requested IV session data record is created and persisted" in new IvSessionDataRequestHandler(algebra) {
      generateIdAndPersist(sampleIvSessionData)
      called.get mustBe true
    }
  }

}
