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

import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType.Ascending
import reactivemongo.bson.{BSONDocument, BSONInteger, BSONObjectID}
import reactivemongo.core.errors.DatabaseException
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.config.MongoConfiguration
import uk.gov.hmrc.ivorchestration.model.AuthRetrieval._
import uk.gov.hmrc.ivorchestration.model.{AuthRetrieval, UnexpectedState}
import uk.gov.hmrc.ivorchestration.persistence.DBConnector
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait AuthRetrievalAlgebra[F[_]] {
  def insertAuthRetrieval(authRetrieval: AuthRetrieval)(implicit hc: HeaderCarrier): F[AuthRetrieval]
  def findAuthRetrievals()(implicit hc: HeaderCarrier): F[List[AuthRetrieval]]
  def findJourneyIdAndCredId(journeyId: String, credId: String)(implicit hc: HeaderCarrier): F[Option[AuthRetrieval]]
}

class AuthRetrievalDBService(mongoComponent: DBConnector)
  extends ReactiveRepository[AuthRetrieval, BSONObjectID]("authretrieval", mongoComponent.mongoConnector.db, AuthRetrieval.format)
    with AuthRetrievalAlgebra[Future] with MongoConfiguration {

  override def indexes: Seq[Index] = {
    Seq(
      Index(
      Seq("expireAt" -> Ascending),
      Option("record-ttl"),
      options = BSONDocument(Seq("expireAfterSeconds" -> BSONInteger(mongoConfig.ttl)))
    ),
      Index(
        Seq("journeyId" -> Ascending,
            "credId" -> Ascending),
            Option("CompositePrimaryKey"),
            unique = true
      )
    )
  }

  override def insertAuthRetrieval(authRetrieval: AuthRetrieval)(implicit hc: HeaderCarrier): Future[AuthRetrieval] =
    insert(authRetrieval).map(_ => authRetrieval)
      .recoverWith {
        case e: DatabaseException if e.code.contains(11000) => Future.failed(UnexpectedState("The record already exists!"))
        case e: Exception => Future.failed(UnexpectedState(e.getMessage))
      }

  override def findAuthRetrievals()(implicit hc: HeaderCarrier): Future[List[AuthRetrieval]] = findAll()

  override def findJourneyIdAndCredId(journeyId: String, credId: String)(implicit hc: HeaderCarrier): Future[Option[AuthRetrieval]] = {
    val query = dbKey(journeyId, credId)
    find(query: _*).map(_.headOption)
  }
}



