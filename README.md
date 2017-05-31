# Predix Hello ScalaJS

A basic attempt to develop a web app on Predix.io using Scala and ScalaJS.

![alt text][logo]


This sample app leverages on:

- [Scala](http://scala-lang.org/) v. 2.12.2
- [ScalaJS](https://www.scala-js.org/) v. 0.6.16
- [SBT](http://www.scala-sbt.org/) v. 013.15
- [SBT Native Packager](https://github.com/sbt/sbt-native-packager) to build application packages in native format and generate a start script for the project
- [Finagle](https://twitter.github.io/finagle/) as underlying http server and client
- [Finch](https://finagle.github.io/finch/) as Scala library for building Finagle HTTP services
- [Circe](https://circe.github.io/circe/) as JSON library for Scala
- [heroku-buildpack-scala](https://github.com/heroku/heroku-buildpack-scala) for Cloud Foundry
- Other libraries and plugins

## Getting Started

### Install tools
Be sure to have [SBT](http://www.scala-sbt.org/) installed on you machine

### Get the source code

Make a directory for your project. Clone or download and extract the starter in that directory.

```
git clone https://github.com/indaco/predix-hello-scalajs.git
cd predix-hello-scalajs
```

## Running the app locally

```
sbt

> run-main WebServer
```

## Running in Predix Cloud

1. Open the `manifest.yml` file and edit the `name` for your own application
2. Push the app to your Predix account `$cf push`

- - -

### Copyright and License

Licensed under the MIT License, see the LICENSE file.

- - -

#### DISCLAIMER

This is **not** an official development from the GE Digital's Predix Team

[logo]: screenshot.png "App Screenshot"
