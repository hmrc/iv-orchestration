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

package uk.gov.hmrc.ivorchestration.repository

import com.softwaremill.quicklens._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import reactivemongo.api.commands.WriteResult
import uk.gov.hmrc.ivorchestration.config.MongoDBClient
import uk.gov.hmrc.ivorchestration.model.UnexpectedState
import uk.gov.hmrc.ivorchestration.model.core.{CredId, IvSessionDataCore, JourneyId}
import uk.gov.hmrc.ivorchestration.persistence.ReactiveMongoConnector
import uk.gov.hmrc.ivorchestration.testsuite._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class IvSessionDataRepositorySpec extends BaseSpec with MongoDBClient with BeforeAndAfterEach with ScalaFutures with TestData {

  val service = new IvSessionDataRepository(ReactiveMongoConnector(mongoConnector))

  "can Add and retrieve AuthRetrieval entity" in {
    val eventualData: Future[List[IvSessionDataCore]] = for {
      _    <- service.insertIvSessionData(buildIvSessionDataCore(sampleIvSessionData))
      data <- service.retrieveAll()
    } yield data

    val actual = await[List[IvSessionDataCore]](eventualData).head.ivSessionData

    actual mustBe sampleIvSessionData.copy(loginTimes = actual.loginTimes, dateOfbirth = actual.dateOfbirth)
  }

  "can Add and retrieve AuthRetrieval entity by journeyId & credId" in {
    val eventualData: Future[Option[IvSessionDataCore]] = for {
      persisted    <- service.insertIvSessionData(sampleIvSessionDataCore.modify(_.ivSessionData.credId).setTo(CredId("123")).copy(journeyId = JourneyId("111")))
      _            <- service.insertIvSessionData(persisted.modify(_.journeyId).setTo(JourneyId("333")))
      data         <- service.findByKey(persisted.journeyId, persisted.ivSessionData.credId)
    } yield data

    val actual = await[Option[IvSessionDataCore]](eventualData).get
    import actual.ivSessionData._

    actual mustBe sampleIvSessionDataCore
      .modify(_.journeyId).setTo(actual.journeyId)
      .modify(_.ivSessionData.credId).setTo(credId)
      .modify(_.ivSessionData.loginTimes).setTo(loginTimes)
      .modify(_.ivSessionData.dateOfbirth).setTo(dateOfbirth)
  }

  "returns a Future failure with duplicate DB exception when adding with same key" in {
    val duplicatedEntry = sampleIvSessionDataCore.copy(journeyId = JourneyId("111")).modify(_.ivSessionData.credId).setTo(CredId("123"))

    await(service.insertIvSessionData(duplicatedEntry))

    intercept[UnexpectedState] {
      await(service.insertIvSessionData(duplicatedEntry))
    }
  }

  "Returns a Future failed with UnexpectedState for any DB exception" in {
    val stubFailingService = new IvSessionDataRepository(ReactiveMongoConnector(mongoConnector)) {
      override def insert(entity: IvSessionDataCore)(implicit ec: ExecutionContext): Future[WriteResult] =
        Future.failed(new Exception("BOOM!"))
    }

    intercept[UnexpectedState] {
      await(stubFailingService.insertIvSessionData(buildIvSessionDataCore(sampleIvSessionData)))
    }
  }

  override def beforeEach(): Unit = await(service.removeAll())
  override def afterEach(): Unit = await(service.removeAll())
}
