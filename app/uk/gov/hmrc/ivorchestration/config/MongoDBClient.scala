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

package uk.gov.hmrc.ivorchestration.config

import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{DefaultDB, FailoverStrategy}
import uk.gov.hmrc.ivorchestration.persistence.ReactiveMongoConnector
import uk.gov.hmrc.mongo.MongoConnector

trait MongoDBClient extends MongoConfiguration {

  import mongoConfig._

  protected val mongoDBUri: String = s"$uri&rm.monitorRefreshMS=$monitorRefresh&rm.failover=$failover"

  implicit lazy val mongoConnector: MongoConnector = MongoConnector(mongoDBUri)
  implicit val mongo: () => DefaultDB = mongoConnector.db

  def bsonCollection(name: String)(
    failoverStrategy: FailoverStrategy = mongoConnector.helper.db.failoverStrategy): BSONCollection =
    mongoConnector.helper.db(name, failoverStrategy)


  val dbConnector = ReactiveMongoConnector(mongoConnector)
}
