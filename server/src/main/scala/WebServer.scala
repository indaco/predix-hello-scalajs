// WebServer.scala

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import com.twitter.finagle.Http
import com.twitter.finagle.http.{Response, Request, Status}
import com.twitter.io.{Buf, Reader}
import com.twitter.util.{Await,Future}

import java.net.InetSocketAddress
import util.Properties

import models._

object WebServer {

  val port = Properties.envOrElse("PORT", "8080").toInt
  val address = new InetSocketAddress(port)

  // services
  val predixAPIService = new services.PredixAPIService

  // ---- Finch Rest API ----
  // "public" because a web-assets jar is generated with a public path configured. See build.sbt
  val static: Endpoint[Buf] = get("assets" :: strings) {
    (segments: Seq[String]) =>
      val path = segments.mkString("/")
      Reader
        .readAll(
          Reader.fromStream(getClass.getResourceAsStream(s"/public/$path")))
        .map { buf =>
          Ok(buf).withHeader(getContentType(path))
        }
  }

  val index: Endpoint[Response] = get(/) {
    val document = HtmlTemplates.hello("Welcome Page")
    HtmlUtils.htmlResponse(document)
  }

  val predix: Endpoint[PredixAPI] = get("predix") {
    predixAPIService.call.map(Ok)
  }

  val apiEndpoints = (index :+: static :+: predix).handle {
    case e: Exception => InternalServerError(e)
  }

  // ---- -------------- ----

  def main(args: Array[String]): Unit = {
    val server = Http.serve(address, apiEndpoints.toService)
    Await.ready(server)
  }

  def getContentType(assetPath: String): (String, String) = {
    val contentType = if (assetPath.endsWith(".js")) {
      "application/javascript"
    } else if (assetPath.endsWith(".css")) {
      "text/css"
    } else {
      "text/plain"
    }
    "Content-Type" -> contentType
  }

}
