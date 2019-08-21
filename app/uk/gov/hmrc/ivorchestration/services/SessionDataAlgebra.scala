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

import play.api.libs.json.{JsString, Json}
import reactivemongo.bson.BSONObjectID
import reactivemongo.core.errors.DatabaseException
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.persistence.ReactiveMongoConnector
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.Future


//TODO change id types & DbConnection etc...

trait SessionDataAlgebra[F[_]] {
  def findSessionData(name: String)(implicit hc: HeaderCarrier): F[List[SessionData]]
  def createSessionData(sessionData: SessionData)(implicit hc: HeaderCarrier): F[Unit]
}


class SessionDataService[F[_]](reactiveMongoComponent: ReactiveMongoConnector, sessionDataAlgebra: SessionDataAlgebra[F]) {
  def findSessionData(name: String)(implicit hc: HeaderCarrier): F[List[SessionData]] =
    sessionDataAlgebra.findSessionData(name)

  def createSessionData(sessionData: SessionData)(implicit hc: HeaderCarrier): F[Unit] =
    sessionDataAlgebra.createSessionData(sessionData)
}


class SessionDataDBService(reactiveMongoComponent: ReactiveMongoConnector)
  extends ReactiveRepository[SessionData, BSONObjectID]("sessionData", reactiveMongoComponent.mongoConnector.db, SessionData.format)
    with SessionDataAlgebra[Future] {

  import scala.concurrent.ExecutionContext.Implicits.global

  override def createSessionData(sessionData: SessionData)(implicit hc: HeaderCarrier): Future[Unit] =
    insert(sessionData).map(_ => ())
      .recoverWith {
        case e: DatabaseException => Future.failed(e)
      }

  override def findSessionData(name: String)(implicit hc: HeaderCarrier): Future[List[SessionData]] =
    find("name" -> JsString(name))
}

case class SessionData(name: String)

object SessionData {
  implicit val format = Json.format[SessionData]
}
