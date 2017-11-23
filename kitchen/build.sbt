val ScalatraVersion = "2.5.4"

organization := "com.example"

name := "kitchen"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.3"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  "org.scalatra" % "scalatra-json_2.12" % "2.6.1",
  "org.json4s" %% "json4s-native" % "3.2.11",
  "org.json4s"   %% "json4s-jackson" % "3.5.2",
  "org.mongodb" %% "casbah" % "3.1.1",
  "org.scalatra" %% "scalatra-json" % ScalatraVersion,
//  "net.liftweb" %% "lift-json" % "2.5+"
//  "org.json4s"   %% "json4s-jackson" % "3.5.0",




)

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)
