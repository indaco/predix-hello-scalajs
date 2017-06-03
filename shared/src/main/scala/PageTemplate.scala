import com.twitter.finagle.http.Response
import com.twitter.io.{Buf}
import scalatags.Text.all._
import scalatags.Text.tags2.{title, nav}
import predix.hello.scala.backend.build.BuildInfo

object HtmlTemplates {
  def hello(name: String): String = {
    "<!DOCTYPE html>" + html(
      head(
        base(href := "/"),
        meta(charset := "utf-8"),
        title("Predix-Hello-ScalaJS"),
        link(rel := "stylesheet", href := "/assets/lib/bootstrap/css/bootstrap.min.css"),
        link(rel := "stylesheet", href := "/assets/lib/font-awesome/css/font-awesome.min.css"),
        link(rel := "stylesheet", href := "/assets/css/index.css")
      ),
      body(
        div(cls := "jumbotron",
          div(cls := "container",
            img(src := "/assets/imgs/scala-js-logo.png", height := "100px", width := "200px"),
            span(" ON "),
            img(src := "/assets/imgs/Predix_Logo.png", height := "auto", width := "280px"),
            h2("Predix.io Says Hello To ScalaJS")
          )
        ),
        div(cls := "container",
          div(cls := "row",
            div(cls := "col-xs-12",
              div( id:= "main",
                h2(s"${name.capitalize}!"),
                div(
                  p(cls:="lead","This is a basic attempt to develop a web app on Predix.io using Scala and ScalaJS."),
                  p("This sample app leverages on:"),
                  ul(cls := "fa-ul",
                    li(i(cls:="fa-li fa fa-check-square"),"Scala as unique programming language both for the backend and the frontend"),
                    li(i(cls:="fa-li fa fa-check-square"),"ScalaJS, the Scala to JavaScript compiler"),
                    li(i(cls:="fa-li fa fa-check-square"),"SBT, a build tool for Scala, Java, and more"),
                    li(i(cls:="fa-li fa fa-check-square"),"Finagle, the Twitter's RPC system"),
                    li(i(cls:="fa-li fa fa-check-square"),"Finch as Scala library for building Finagle HTTP services"),
                    li(i(cls:="fa-li fa fa-check-square"),"Circe as JSON library for Scala"),
                    li(i(cls:="fa-li fa fa-check-square"),"heroku-buildpack-scala for Cloud Foundry"),
                    li(i(cls:="fa-li fa fa-check-square"),"JQuery"),
                    li(i(cls:="fa-li fa fa-check-square"),"Bootstrap")
                  ),
                  div(
                    p("If you have any suggestions or improvements feel free to collaborate with me on ",
                    a(i(cls := "fa fa-github", attr("aria-hidden") := true), href := "https://github.com/indaco/predix-hello-scalajs", target := "_blank"))
                  ),
                  br,
                  p(cls:="lead", "Do you wanna test me?")
                )
              ),
              br,
              div(id:="success-panel", cls := "panel panel-success hidden",
                div(cls := "panel-heading", "Predix.io says..."),
                div(cls := "panel-body",
                  div(id := "results")
                )
              ),
              div(id:="error-panel", cls := "panel panel-danger hidden",
                div(cls := "panel-heading", "Predix.io says..."),
                div(cls := "panel-body",
                  div(id := "errors")
                )
              )
            )
          )
        ),
        footer(cls := "footer",
          div(cls := "container",
            p(cls := "text-muted text-center",
              s"""Running on Scala ${BuildInfo.scalaVersion}, ScalaJS ${BuildInfo.scalajsVersion},
              Finch ${BuildInfo.finchVersion} and Finagle ${BuildInfo.finagleVersion}"""
            ),
            p(cls := "text-muted text-center",
              a("Predix, Predix.io and the Predix Logo ", href := "https://www.predix.io", target := "_blank"),
              " are ",
              a("trademarks", href :="https://trademarks.justia.com/860/43/predix-86043990.html", target := "_blank"),
              " of the ",
              a("General Electric Company.", href:="http://www.ge.com/", target := "_blank")
            ),
            p(cls := "text-muted text-center",
              a("Scala.js", href := "https://www.scala-js.org/", target := "_blank"),
              " is distributed under the ",
              a("Scala License.", href :="http://www.scala-lang.org/license.html", target := "_blank")
            )
          )
        ),
        script(`type` := "text/javascript", src := "/assets/lib/jquery/jquery.min.js"),
        script(`type` := "text/javascript", src := "/assets/client-opt.js"),
        script(`type` := "text/javascript", src := "/assets/client-jsdeps.min.js"),
        script(`type` := "text/javascript", src := "/assets/client-fastopt.js")
      )
    )
  }.toString()
}

object HtmlUtils {
  def htmlResponse(document: String): Response = {
    val rep = Response()
    rep.content = Buf.Utf8(document)
    rep.contentType = "text/html"
    rep
  }
}
