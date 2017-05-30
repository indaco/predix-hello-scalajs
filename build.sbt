name in ThisBuild := "predix-hello-scalajs"
organization in ThisBuild := "com.example"
version in ThisBuild := "0.1"
isSnapshot in ThisBuild := version.value.endsWith("-SNAPSHOT")

val scalaV = "2.12.2"
val finchVersion = "0.14.1"
val finagleVersion = "6.44.0"
val bootstrapVersion = "3.3.7-1"
val twitterServerVersion = "1.29.0"
val circeVersion = "0.7.1"
val scalajsVersion = "0.6.15"

//-------------------------------

lazy val server = (project in file("server"))
  .settings(
    scalaVersion := scalaV,
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    libraryDependencies ++= Seq(
      "com.github.finagle" %% "finch-core" % finchVersion,
      "com.github.finagle" %% "finch-circe" % finchVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "com.lihaoyi" %% "scalatags" % "0.6.5",
      // logging
      "org.slf4j" % "jul-to-slf4j" % "1.7.7",
      "ch.qos.logback" % "logback-core" % "1.1.7",
      "ch.qos.logback" % "logback-classic" % "1.1.7",
      // asset dependencies and handling
      "org.webjars" % "bootstrap" % bootstrapVersion,
      "org.webjars" % "font-awesome" % "4.7.0"
    ),
    // Frontend depdendency configuration
    WebKeys.packagePrefix in Assets := "public/",
    managedClasspath in Runtime += (packageBin in Assets).value,
    // Packaging
    topLevelDirectory := None // Don't add a root folder to the archive
  )
  .enablePlugins(SbtWeb, JavaAppPackaging)
  .dependsOn(sharedJvm)


//-------------------------------

  lazy val client = (project in file("client"))
  .settings(
    scalaVersion := scalaV,
    //persistLauncher := true,
    scalaJSUseMainModuleInitializer := true,
    unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.1",
      "com.lihaoyi" %% "scalatags" % "0.6.5",
      "be.doeraene" %%% "scalajs-jquery" % "0.9.1"
    ),
    jsDependencies ++= Seq(
      "org.webjars" % "jquery" % "2.1.3" / "2.1.3/jquery.js",
      "org.webjars" % "bootstrap" % bootstrapVersion / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js"
    )
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .dependsOn(sharedJs)

//-------------------------------

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      "com.github.finagle" %% "finch-core" % finchVersion,
      "com.lihaoyi" %% "scalatags" % "0.6.5",
      "com.vmunier" %% "scalajs-scripts" % "1.1.0",
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion,
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    ),
    // build info
    buildInfoOptions += BuildInfoOption.BuildTime,
    buildInfoKeys := Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      "finchVersion" -> finchVersion,
      "finagleVersion" -> finagleVersion,
      "scalajsVersion" -> scalajsVersion
    ),
    buildInfoPackage := "predix.hello.scala.backend.build"
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-scalajs" % circeVersion
    )
  )
  .jsConfigure(_ enablePlugins ScalaJSWeb)

//-------------------------------

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the server project at sbt startup
onLoad in Global := (Command
  .process("project server", _: State)) compose (onLoad in Global).value
