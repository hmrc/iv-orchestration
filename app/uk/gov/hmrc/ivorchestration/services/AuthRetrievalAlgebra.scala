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

import play.api.libs.json.JsString
import reactivemongo.bson.BSONObjectID
import reactivemongo.core.errors.DatabaseException
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.model.AuthRetrieval
import uk.gov.hmrc.ivorchestration.persistence.ReactiveMongoConnector
import uk.gov.hmrc.mongo.ReactiveRepository
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future


//TODO change id types & DbConnection etc...

trait AuthRetrievalAlgebra[F[_]] {
  def findAuthRetrievals(name: String)(implicit hc: HeaderCarrier): F[List[AuthRetrieval]]
  def insertAuthRetrieval(authRetrieval: AuthRetrieval)(implicit hc: HeaderCarrier): F[Unit]
}


class AuthRetrievalService[F[_]](reactiveMongoComponent: ReactiveMongoConnector, sessionDataAlgebra: AuthRetrievalAlgebra[F]) {
  def findAllAuthRetrievals(name: String)(implicit hc: HeaderCarrier): F[List[AuthRetrieval]] =
    sessionDataAlgebra.findAuthRetrievals(name)

  def insertAuthRetrieval(authRetrieval: AuthRetrieval)(implicit hc: HeaderCarrier): F[Unit] =
    sessionDataAlgebra.insertAuthRetrieval(authRetrieval)
}


class AuthRetrievalDBService(reactiveMongoComponent: ReactiveMongoConnector)
  extends ReactiveRepository[AuthRetrieval, BSONObjectID]("authRetrieval", reactiveMongoComponent.mongoConnector.db, AuthRetrieval.format)
    with AuthRetrievalAlgebra[Future] {

  override def insertAuthRetrieval(authRetrieval: AuthRetrieval)(implicit hc: HeaderCarrier): Future[Unit] =
    insert(authRetrieval).map(_ => ())
      .recoverWith {
        case e: DatabaseException => Future.failed(e)
      }

  override def findAuthRetrievals(name: String)(implicit hc: HeaderCarrier): Future[List[AuthRetrieval]] = findAll()
}

