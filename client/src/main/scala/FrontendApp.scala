// FrontendApp.scala

import cats.data.EitherT
import cats.instances.all._
import io.circe.generic.auto._
import io.circe.scalajs._
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.experimental._
import org.scalajs.jquery.{jQuery, JQueryXHR, JQueryAjaxSettings}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Thenable.Implicits._

import models.{PredixAPI, FetchError}

object FrontendApp extends js.JSApp {

  val resultNode = document.getElementById("results")
  val errorNode = document.getElementById("errors")
  val successPanel = document.getElementById("success-panel")
  val errorPanel = document.getElementById("error-panel")

  def main(): Unit = {
    jQuery(() => setupUI())
  }

  def setupUI(): Unit = {
    jQuery("""<button type="button" class="btn btn-primary">Click me!</button>""")
      .click(() => addClickedMessage())
      .appendTo(jQuery("#main"))
  }

  def addClickedMessage(): Boolean = {
    getPredixInfo().onComplete {
      case scala.util.Success(Right(results)) => renderResults(results)
      case scala.util.Success(Left(err)) => renderError(err)
      case scala.util.Failure(err) => renderError(FetchError(err.getMessage))
    }

    false
  }

  def getPredixInfo(): Future[Either[FetchError, PredixAPI]] =
    (for {
      response <- EitherT.right(
        Fetch
          .fetch("/predix",
                 RequestInit(
                   method = HttpMethod.GET
                 ))
          .toFuture)
      json <- EitherT(parseJson(response))
      result <- EitherT
        .fromEither[Future](decodeJs[PredixAPI](json))
        .leftMap(e => FetchError(e.getMessage))
    } yield result).value

  def parseJson(response: Response): Future[Either[FetchError, js.Any]] = {
    if (response.ok) response.json().map(Right.apply)
    else Future.successful(Left(FetchError(response.statusText)))
  }

  def renderResults(result: PredixAPI): Unit = {
    renderTemplate(resultNode, result)
  }

  def renderTemplate(targetNode: dom.Node, result: PredixAPI): Unit = {
    jQuery(targetNode).empty()
    jQuery(targetNode).append(
      s"""
        My name is <b>${result.description}</b>
        and the API Version number is <b> ${result.version}</b>
      """)
    jQuery(successPanel).removeClass("panel panel-success hidden").addClass("panel panel-success")
  }

  def renderError(fetchError: FetchError): Unit = {
    jQuery(errorNode).append(fetchError.toString)
    jQuery(errorPanel).removeClass("panel panel-danger hidden").addClass("panel panel-danger")
  }

}
