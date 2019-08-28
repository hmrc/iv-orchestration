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
import org.scalatest.concurrent.ScalaFutures
import reactivemongo.api.commands.WriteResult
import reactivemongo.core.errors.DatabaseException
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.config.MongoDBClient
import uk.gov.hmrc.ivorchestration.model.{AuthRetrieval, UnexpectedState}
import uk.gov.hmrc.ivorchestration.persistence.ReactiveMongoConnector
import uk.gov.hmrc.ivorchestration.{BaseSpec, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure

class AuthRetrievalDBServiceSpec extends BaseSpec with MongoDBClient with BeforeAndAfterEach with ScalaFutures {

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

  "returns a Future failure with duplicate DB exception when adding with same key" in {
    val duplicatedEntry = service.insertAuthRetrieval(sampleAuthRetrieval.copy(journeyId = Some("111"), credId = "123"))

    await(duplicatedEntry)

    intercept[UnexpectedState] {
      await(service.insertAuthRetrieval(sampleAuthRetrieval.copy(journeyId = Some("111"), credId = "123")))
    }
  }

  "Returns a Future failed with UnexpectedState for any DB exception" in {
    val stubFailingService = new AuthRetrievalDBService(ReactiveMongoConnector(mongoConnector)) {
      override def insert(entity: AuthRetrieval)(implicit ec: ExecutionContext): Future[WriteResult] =
        Future.failed(new Exception("BOOM!"))
    }

    intercept[UnexpectedState] {
      await(stubFailingService.insertAuthRetrieval(sampleAuthRetrieval))
    }
  }

  override def beforeEach(): Unit = await(service.removeAll())
  override def afterEach(): Unit = await(service.removeAll())
}
