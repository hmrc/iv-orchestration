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

import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.{Filters, IndexModel, IndexOptions}
import play.api.Logger
import uk.gov.hmrc.ivorchestration.config.AppConfig
import uk.gov.hmrc.ivorchestration.model.core.{IvSessionDataCore, JourneyId}
import uk.gov.hmrc.ivorchestration.model.{DatabaseError, DuplicatedRecord}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.SECONDS
import scala.concurrent.{ExecutionContext, Future}

trait IvSessionDataRepositoryAlgebra {
  def insertIvSessionData(authRetrievalCore: IvSessionDataCore): Future[IvSessionDataCore]
  def retrieveAll(): Future[Seq[IvSessionDataCore]]
  def findByJourneyId(journeyId: JourneyId): Future[Option[IvSessionDataCore]]
}

@Singleton()
class IvSessionDataRepository @Inject()(mongoComponent: MongoComponent, app: AppConfig)(implicit val ec: ExecutionContext)
  extends PlayMongoRepository[IvSessionDataCore](
    collectionName = "iv-session-data", mongoComponent = mongoComponent, domainFormat = IvSessionDataCore.format,
    indexes = Seq(
      IndexModel(
        ascending("createdAt"),
        indexOptions = IndexOptions().name("expireAfterSeconds").expireAfter(app.mongodbTTL, SECONDS)
      ),
      IndexModel(
        ascending("journeyId", "ivSessionData.credId"), IndexOptions().name("Primary").unique(true)
      )
    ),
    replaceIndexes = app.mongodbReplaceIndexes // only set to true if changing existing indexes
  ) with IvSessionDataRepositoryAlgebra {

  private val playLogger: Logger = Logger(getClass)

  override def insertIvSessionData(ivSessionDataCore: IvSessionDataCore): Future[IvSessionDataCore] = {
    collection.insertOne(ivSessionDataCore).map(_ => ivSessionDataCore).toFuture().map(_.head)
      .recoverWith {
        case e: Exception if e.getMessage.contains("11000") =>
          playLogger.warn(s"Store IV session data failed for journeyId: ${ivSessionDataCore.journeyId} and credId: ${ivSessionDataCore.ivSessionData.credId} with ${e.getMessage}")
          Future.failed(DuplicatedRecord)
        case e: Exception =>
          playLogger.warn(s"Store IV session data failed for journeyId: ${ivSessionDataCore.journeyId} and credId: ${ivSessionDataCore.ivSessionData.credId} with ${e.getMessage}")
          Future.failed(DatabaseError)
      }
  }

  override def retrieveAll(): Future[Seq[IvSessionDataCore]] = {
    collection.find().toFuture()
  }

  override def findByJourneyId(journeyId: JourneyId): Future[Option[IvSessionDataCore]] = {
    collection.find(Filters.eq("journeyId", journeyId.value)).headOption()
  }
}



