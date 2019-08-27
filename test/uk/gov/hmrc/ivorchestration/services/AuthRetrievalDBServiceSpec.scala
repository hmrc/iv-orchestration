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

package uk.gov.hmrc.ivorchestration.services

import org.scalatest.BeforeAndAfterEach
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.config.MongoDBClient
import uk.gov.hmrc.ivorchestration.model.AuthRetrieval
import uk.gov.hmrc.ivorchestration.persistence.ReactiveMongoConnector
import uk.gov.hmrc.ivorchestration.{BaseSpec, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthRetrievalDBServiceSpec extends BaseSpec with MongoDBClient with BeforeAndAfterEach {

  implicit val hc = HeaderCarrier()

  val service = new AuthRetrievalDBService(ReactiveMongoConnector(mongoConnector))

  "can Add and retrieve AuthRetrieval entity" in {
    val eventualData: Future[List[AuthRetrieval]] = for {
      _    <- service.insertAuthRetrieval(sampleAuthRetrieval)
      data <- service.findAuthRetrievals()
    } yield data

    val actual = await[List[AuthRetrieval]](eventualData).head

    actual mustBe sampleAuthRetrieval.copy(journeyId = actual.journeyId, loginTimes = actual.loginTimes, dateOfbirth = actual.dateOfbirth)
  }

  "can Add and retrieve AuthRetrieval entity by journeyId & GGCredId" in {
    val eventualData: Future[Option[AuthRetrieval]] = for {
      persisted    <- service.insertAuthRetrieval(sampleAuthRetrieval.copy(journeyId = Some("111"), credId = "123"))
      _            <- service.insertAuthRetrieval(sampleAuthRetrieval.copy(journeyId = Some("333"), credId = "9999"))
      data         <- service.findJourneyIdAndCredId(persisted.journeyId.getOrElse(""), persisted.credId)
    } yield data

    val actual = await[Option[AuthRetrieval]](eventualData).get

    actual mustBe sampleAuthRetrieval.copy(journeyId = actual.journeyId, credId= actual.credId, loginTimes = actual.loginTimes, dateOfbirth = actual.dateOfbirth)
  }

  override def beforeEach(): Unit = await(service.removeAll())
  override def afterEach(): Unit = await(service.removeAll())
}
