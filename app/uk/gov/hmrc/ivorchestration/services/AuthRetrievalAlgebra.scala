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

import reactivemongo.bson.BSONObjectID
import reactivemongo.core.errors.DatabaseException
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.model.AuthRetrieval
import uk.gov.hmrc.ivorchestration.persistence.DBConnector
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


//TODO change id types...

trait AuthRetrievalAlgebra[F[_]] {
  def findAuthRetrievals()(implicit hc: HeaderCarrier): F[List[AuthRetrieval]]
  def insertAuthRetrieval(authRetrieval: AuthRetrieval)(implicit hc: HeaderCarrier): F[AuthRetrieval]
}

class AuthRetrievalDBService(mongoComponent: DBConnector)
  extends ReactiveRepository[AuthRetrieval, BSONObjectID]("authRetrieval", mongoComponent.mongoConnector.db, AuthRetrieval.format)
    with AuthRetrievalAlgebra[Future] {

  override def insertAuthRetrieval(authRetrieval: AuthRetrieval)(implicit hc: HeaderCarrier): Future[AuthRetrieval] =
    insert(authRetrieval).map(_ => authRetrieval)
      .recoverWith {
        case e: DatabaseException => Future.failed(e)
      }

  override def findAuthRetrievals()(implicit hc: HeaderCarrier): Future[List[AuthRetrieval]] = findAll()
}

