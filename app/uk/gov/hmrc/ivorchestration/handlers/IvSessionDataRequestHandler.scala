/*
 * Copyright 2021 HM Revenue & Customs
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

import java.util.UUID

import cats.MonadError
import cats.syntax.flatMap._
import cats.syntax.functor._
import org.joda.time.DateTime
import play.api.Logger
import uk.gov.hmrc.ivorchestration.model.{BusinessError, RecordNotFound}
import uk.gov.hmrc.ivorchestration.model.api.{IvSessionData, IvSessionDataSearchRequest, IvSessionDataSearchResponse}
import uk.gov.hmrc.ivorchestration.model.core.{IvSessionDataCore, JourneyId}
import uk.gov.hmrc.ivorchestration.repository.IvSessionDataRepositoryAlgebra

class IvSessionDataRequestHandler[F[_]](
    ivSessionDataAlgebra: IvSessionDataRepositoryAlgebra[F])(implicit monadError: MonadError[F, BusinessError]
) {

  def create(ivSessionData: IvSessionData): F[String] =
    generateIdAndPersist(ivSessionData).map(core => buildUri(core.journeyId))

  def search(ivSessionDataSearch: IvSessionDataSearchRequest): F[IvSessionDataSearchResponse] =
    ivSessionDataAlgebra.findByKey(ivSessionDataSearch.journeyId, ivSessionDataSearch.credId).flatMap {
      case None =>
        Logger.warn(s"No IV session data found for journeyId: ${ivSessionDataSearch.journeyId} and credId: ${ivSessionDataSearch.credId}")
        monadError.raiseError(RecordNotFound)
      case Some(r) => {
        val searchResponse = IvSessionDataSearchResponse.fromIvSessionDataCore(r)
        Logger.info(s"Return session data for journeyId: ${ivSessionDataSearch.journeyId} and credId: ${ivSessionDataSearch.credId} (${searchResponse.confidenceLevel}, ${searchResponse.ivFailureReason})")
        monadError.pure(searchResponse)
      }
    }

  protected def generateIdAndPersist(ivSessionData: IvSessionData): F[IvSessionDataCore] =
    persist(IvSessionDataCore(ivSessionData, JourneyId(UUID.randomUUID().toString), new DateTime))

  protected def persist(ivSessionDataCore: IvSessionDataCore): F[IvSessionDataCore] = {
    Logger.info(s"Store IV session data for journeyId: ${ivSessionDataCore.journeyId} and credId: ${ivSessionDataCore.ivSessionData.credId} (${ivSessionDataCore.ivSessionData.confidenceLevel}, ${ivSessionDataCore.ivSessionData.ivFailureReason})")
    ivSessionDataAlgebra.insertIvSessionData(ivSessionDataCore)
  }

  protected def buildUri(journeyId: JourneyId): String = s"${UriPrefix.uriPrefix}${journeyId.value}"
}

object UriPrefix {
  val uriPrefix: String = "/iv-orchestration/iv-sessiondata/"
}

