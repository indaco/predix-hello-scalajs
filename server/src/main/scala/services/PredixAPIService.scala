// PredixAPIService.scala

package services

import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.{Http, Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status, Version, Method}
import com.twitter.util.{Await, Future}
import io.circe._, io.circe.parser._

import models.PredixAPI
/*
 * A basic HTTP Client to call a REST endpoint on Predix.io
 *
 * version: 0.1
 * copyright: 2017 - Mirco Veltri
 * license: see LICENSE for more details
 */
class PredixAPIService {

  val PredixAPIEndpointHost = "api.system.aws-usw02-pr.ice.predix.io"
  val PredixAPIEndpointPort = "443"
  val PathToCFInfo = "/v2/info"

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
    val clientWithoutErrorHandling: Service[Request, Response] = ClientBuilder()
      .stack(Http.client)
      .hosts(s"$PredixAPIEndpointHost:$PredixAPIEndpointPort")
      .tls(PredixAPIEndpointHost)
      .hostConnectionLimit(1)
      .build()

    val handleErrors = new HandleErrors

    // compose the Filter with the client:
    val client: Service[Request, Response] = handleErrors andThen clientWithoutErrorHandling

    makeRequest(client)
  }

  def makeRequest(client: Service[Request, Response]): Future[PredixAPI] = {
    val authorizedRequest = Request(Version.Http11, Method.Get, PathToCFInfo)
    val response: Future[Response] = client(authorizedRequest)

    var values = Await.result(response.map { res: Response =>
      val parsedJson: Json = parse(res.contentString).getOrElse(Json.Null)
      val cursor: HCursor = parsedJson.hcursor

      val cfDescription = cursor.downField("description").as[String] match {
        case Left(s) => s
        case Right(s) => s
      }

      val cfAPIVersion = cursor.downField("api_version").as[String] match {
        case Left(s) => s
        case Right(s) => s
      }
      (cfDescription, cfAPIVersion)
    })

    Future(PredixAPI(values._1.toString, values._2.toString))
  }

}
