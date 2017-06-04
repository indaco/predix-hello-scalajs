// PredixAPIService.scala
package services

import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.{Http, Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status, Version, Method}
import com.twitter.util.{Await, Future}
import io.circe._, io.circe.parser._
import io.circe.parser.decode, io.circe.generic.semiauto._

import models.PredixAPI
/*
 * A basic HTTP Client to call a REST endpoint on Predix.io
 *
 * version: 0.1
 * copyright: 2017 - Mirco Veltri
 * license: see LICENSE for more details
 */
class PredixAPIService {

  private[this] val PredixAPIEndpointHost = "api.system.aws-usw02-pr.ice.predix.io"
  private[this] val PredixAPIEndpointPort = "443"
  private[this] val PathToCFInfo = "/v2/info"

  implicit val decodePredixAPi: Decoder[PredixAPI] =
    Decoder.forProduct2("description", "api_version")(PredixAPI.apply)

  // compose the Filter with the client:
  private[this] val client =
    new HandleErrors andThen
    ClientBuilder()
      .stack(Http.client)
      .hosts(s"$PredixAPIEndpointHost:$PredixAPIEndpointPort")
      .tls(PredixAPIEndpointHost)
      .hostConnectionLimit(1)
      .build()

  class InvalidRequest extends Exception

  /**
   * Convert HTTP 4xx and 5xx class responses into Exceptions.
   */
  class HandleErrors extends SimpleFilter[Request, Response] {
    def apply(request: Request, service: Service[Request, Response]) = {
      // flatMap asynchronously responds to requests and can "map" them to both
      // success and failure values:
      service(request) flatMap { response =>
        response.status match {
          case Status.Ok => Future.value(response)
          case Status.Forbidden => Future.exception(new InvalidRequest)
          case _ => Future.exception(new Exception(response.status.reason))
        }
      }
    }
  }

  def call(): Future[PredixAPI] = {
    val authorizedRequest = Request(Version.Http11, Method.Get, PathToCFInfo)
    val response: Future[Response] = client(authorizedRequest)
    decodeResponse(response)
  }

  private[this] def decodeResponse(response: Future[Response]): Future[PredixAPI] = {
    var predixApiInfo = Await.result(response.map { res: Response =>
      decode(res.contentString).toOption.get
    })
    Future(predixApiInfo)
  }

}
