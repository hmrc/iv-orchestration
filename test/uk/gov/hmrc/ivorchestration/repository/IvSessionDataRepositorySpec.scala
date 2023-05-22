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

package uk.gov.hmrc.ivorchestration.repository

import org.mongodb.scala.MongoCollection
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.ivorchestration.config.AppConfig
import uk.gov.hmrc.ivorchestration.model.DuplicatedRecord
import uk.gov.hmrc.ivorchestration.model.core.{CredId, IvSessionDataCore, JourneyId}
import uk.gov.hmrc.ivorchestration.testsuite._
import uk.gov.hmrc.mongo.MongoComponent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IvSessionDataRepositorySpec extends BaseSpec with BeforeAndAfterEach with ScalaFutures with TestData with GuiceOneAppPerSuite {

  val mongoComponent: MongoComponent = app.injector.instanceOf[MongoComponent]
  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  val service = new IvSessionDataRepository(mongoComponent, appConfig)

  "can Add and retrieve AuthRetrieval entity" in {
    val eventualData: Future[Seq[IvSessionDataCore]] = for {
      _    <- service.insertIvSessionData(buildIvSessionDataCore(sampleIvSessionData))
      data <- service.retrieveAll()
    } yield data

    val actual = await[Seq[IvSessionDataCore]](eventualData).head.ivSessionData

    actual mustBe sampleIvSessionData.copy(loginTimes = actual.loginTimes, dateOfBirth = actual.dateOfBirth)
  }

  "returns a failure with duplicate DB exception when adding with same key" in {
    val duplicatedEntry: IvSessionDataCore =
      sampleIvSessionDataCore.copy(ivSessionData = sampleIvSessionData.copy(credId= Some(CredId("123"))), journeyId = JourneyId("111"))

    await(service.insertIvSessionData(duplicatedEntry))

    intercept[DuplicatedRecord.type] {
      await(service.insertIvSessionData(duplicatedEntry))
    }
  }

  "returns a failure with duplicate DB exception when adding with same key with no credId" in {
    val duplicatedEntry: IvSessionDataCore =
      sampleIvSessionDataCore.copy(ivSessionData = sampleIvSessionData.copy(credId= None), journeyId = JourneyId("111"))

    await(service.insertIvSessionData(duplicatedEntry))

    intercept[DuplicatedRecord.type] {
      await(service.insertIvSessionData(duplicatedEntry))
    }
  }

  "can Add and retrieve AuthRetrieval entity by journeyId & credId" in {
    await(service.collection.drop().toFuture())
    val entry: IvSessionDataCore =
      sampleIvSessionDataCore.copy(ivSessionData = sampleIvSessionData.copy(credId = Some(CredId("123"))), journeyId = JourneyId("111"))
    val persistedEntry: IvSessionDataCore = entry.copy(journeyId = JourneyId("333"))
    val eventualData: Future[Option[IvSessionDataCore]] = for {
      persisted    <- service.insertIvSessionData(entry)
      _            <- service.insertIvSessionData(persistedEntry)
      data         <- service.findByJourneyId(persisted.journeyId)
    } yield data

    val actual = await[Option[IvSessionDataCore]](eventualData).get
    import actual.ivSessionData._

    actual mustBe sampleIvSessionDataCore.copy(journeyId = actual.journeyId, ivSessionData = sampleIvSessionData.copy(credId = credId, loginTimes = loginTimes, dateOfBirth = dateOfBirth))

    await(service.collection.drop().toFuture())
  }

  "Returns a failure with DatabaseError for any DB exception" in {
    lazy val stubFailingService = new IvSessionDataRepository(mongoComponent, appConfig) {
      override lazy val collection: MongoCollection[IvSessionDataCore] =
        throw new Exception("BOOM!")
    }

    intercept[Exception] {
      await(stubFailingService.insertIvSessionData(buildIvSessionDataCore(sampleIvSessionData)))
    }
  }

}
