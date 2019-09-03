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

import cats.{Id, MonadError}
import uk.gov.hmrc.ivorchestration.model.UnexpectedState
import uk.gov.hmrc.ivorchestration.model.core.{CredId, IvSessionDataCore, JourneyId}
import uk.gov.hmrc.ivorchestration.repository.IvSessionDataRepositoryAlgebra
import uk.gov.hmrc.ivorchestration.testsuite.{BaseSpec, TestData}

class IvSessionDataRequestHandlerSpec extends BaseSpec with TestData {

  "Given AuthRetrieval" should {
    "JourneyId is generated for iv-session-data" in new IvSessionDataRequestHandler[Id](algebra) {
      handleCreate(sampleIvSessionData) must include(UriPrefix.uriPrefix)
    }

    "JourneyId is generated and appended to the returned uri" in new IvSessionDataRequestHandler[Id](algebra) {
      buildUri(JourneyId("3456")) mustBe "/iv-orchestration/iv-sessiondata/3456"
    }

    "Given AuthRetrieval the requested IV session data record is created and persisted" in new IvSessionDataRequestHandler[Id](algebra) {
      val authRetrieval = generateIdAndPersist(sampleIvSessionData)
      called.get mustBe true
    }
  }

  val called = new AtomicBoolean(false)

  val algebra = new IvSessionDataRepositoryAlgebra[Id] {
    override def retrieveAll(): Id[List[IvSessionDataCore]] = ???
    override def findByKey(journeyId: JourneyId, credId: CredId): Id[Option[IvSessionDataCore]] = ???
    override def insertIvSessionData(ivSessionDataCore: IvSessionDataCore): Id[IvSessionDataCore] = {
      val persisted = sampleIvSessionDataCore.copy(journeyId = ivSessionDataCore.journeyId)
      ivSessionDataCore must matchPattern {
        case IvSessionDataCore(_, JourneyId(_), _) =>
      }
      called.set(true)
      persisted
    }
  }

  implicit val me = new MonadError[Id, UnexpectedState] {
    override def raiseError[A](e: UnexpectedState): Id[A] = throw e
    override def handleErrorWith[A](fa: Id[A])(f: UnexpectedState => Id[A]): Id[A] = ???
    override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] = f(fa)
    override def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] = ???
    override def pure[A](x: A): Id[A] = x
  }
}
