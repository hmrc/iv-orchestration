/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.ivorchestration.handlers

import java.time.{OffsetDateTime, ZoneOffset}
import play.api.Logging
import uk.gov.hmrc.ivorchestration.model.api.{IvSessionData, IvSessionDataSearchRequest}
import uk.gov.hmrc.ivorchestration.model.core.{IvSessionDataCore, JourneyId}
import uk.gov.hmrc.ivorchestration.model.{CredIdForbidden, RecordNotFound}
import uk.gov.hmrc.ivorchestration.repository.IvSessionDataRepositoryAlgebra

import java.util.UUID
import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IvSessionDataRequestHandler(ivSessionDataAlgebra: IvSessionDataRepositoryAlgebra)(implicit ec: ExecutionContext) extends Logging {

  def create(ivSessionData: IvSessionData): Future[String] =
    generateIdAndPersist(ivSessionData).map(core => buildUri(core.journeyId))

  def search(ivSessionDataSearch: IvSessionDataSearchRequest): Future[IvSessionDataCore] =
    ivSessionDataAlgebra.findByJourneyId(ivSessionDataSearch.journeyId).flatMap {
      case None =>
        logger.warn(s"No IV session data found for journeyId: ${ivSessionDataSearch.journeyId} and credId: ${ivSessionDataSearch.credId}")
        Future.failed(RecordNotFound)
      case Some(r) if r.ivSessionData.credId == ivSessionDataSearch.credId =>
        logger.info(s"Return session data for journeyId: ${ivSessionDataSearch.journeyId} (${r.ivSessionData.confidenceLevel}, ${r.ivSessionData.ivFailureReason})")
        Future.successful(r)
      case Some(_) =>
        logger.info(s"Returned session data for journeyId: ${ivSessionDataSearch.journeyId} does not match the requested credId)")
        Future.failed(CredIdForbidden)
    }

  protected def generateIdAndPersist(ivSessionData: IvSessionData): Future[IvSessionDataCore] =
    persist(IvSessionDataCore(ivSessionData, JourneyId(UUID.randomUUID().toString), OffsetDateTime.now(ZoneOffset.UTC)))

  protected def persist(ivSessionDataCore: IvSessionDataCore): Future[IvSessionDataCore] = {
    logger.info(s"Store IV session data for journeyId: ${ivSessionDataCore.journeyId} and credId: ${ivSessionDataCore.ivSessionData.credId} (${ivSessionDataCore.ivSessionData.confidenceLevel}, ${ivSessionDataCore.ivSessionData.ivFailureReason})")
    ivSessionDataAlgebra.insertIvSessionData(ivSessionDataCore)
  }

  protected def buildUri(journeyId: JourneyId): String = s"${UriPrefix.uriPrefix}${journeyId.value}"
}

object UriPrefix {
  val uriPrefix: String = "/iv-orchestration/iv-sessiondata/"
}

