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

import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType.Ascending
import reactivemongo.bson.{BSONDocument, BSONInteger, BSONObjectID}
import reactivemongo.core.errors.DatabaseException
import uk.gov.hmrc.ivorchestration.config.MongoConfiguration
import uk.gov.hmrc.ivorchestration.model.api.IvSessionData._
import uk.gov.hmrc.ivorchestration.model.core.{CredId, IvSessionDataCore, JourneyId}
import uk.gov.hmrc.ivorchestration.model.{DatabaseError, DuplicatedRecord, RecordNotFound}
import uk.gov.hmrc.ivorchestration.persistence.DBConnector
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait IvSessionDataRepositoryAlgebra[F[_]] {
  def insertIvSessionData(authRetrievalCore: IvSessionDataCore): F[IvSessionDataCore]
  def retrieveAll(): F[List[IvSessionDataCore]]
  def findByKey(journeyId: JourneyId, credId: CredId): F[Option[IvSessionDataCore]]
}

class IvSessionDataRepository(mongoComponent: DBConnector)
  extends ReactiveRepository[IvSessionDataCore, BSONObjectID]("iv-session-data", mongoComponent.mongoConnector.db, IvSessionDataCore.format)
    with IvSessionDataRepositoryAlgebra[Future] with MongoConfiguration {

  override def indexes: Seq[Index] = {
    Seq(
      Index(
        Seq("createdAt" -> Ascending),
        options = BSONDocument(Seq("expireAfterSeconds" -> BSONInteger(mongoConfig.ttl)))
    ),
      Index(
        Seq("journeyId" -> Ascending,
            "ivSessionData.credId" -> Ascending),
            Option("Primary"),
            unique = true
      )
    )
  }

  override def insertIvSessionData(ivSessionDataCore: IvSessionDataCore): Future[IvSessionDataCore] =
    insert(ivSessionDataCore).map(_ => ivSessionDataCore)
      .recoverWith {
        case e: DatabaseException if e.code.contains(11000) => Future.failed(DuplicatedRecord)
        case _: Exception => Future.failed(DatabaseError)
      }

  override def retrieveAll(): Future[List[IvSessionDataCore]] = findAll()

  override def findByKey(journeyId: JourneyId, credId: CredId): Future[Option[IvSessionDataCore]] = {
    val query = dbKey(journeyId, credId)
    find(query: _*).map(_.headOption)
  }
}



