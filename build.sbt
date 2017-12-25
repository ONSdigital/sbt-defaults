
organization := "uk.gov.ons"
name := """sbt-defaults"""
version := "0.1-SNAPSHOT"

// SBT 0.13 requires 2.10
// See https://github.com/allenai/sbt-plugins/blob/master/build.sbt
scalaVersion := "2.10.7"

sbtPlugin := true

resolvers += Resolver.sonatypeRepo("releases")

// choose a test framework

// utest
//libraryDependencies += "com.lihaoyi" %% "utest" % "0.4.8" % "test"
//testFrameworks += new TestFramework("utest.runner.Framework")

// ScalaTest
//libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1" % "test"
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

// Specs2
//libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "3.9.1" % "test")
//scalacOptions in Test ++= Seq("-Yrangepos")

initialCommands in console := """import uk.gov.ons.sbt._"""

// set up 'scripted; sbt plugin for testing sbt plugins
scriptedSettings
scriptedLaunchOpts := {
  scriptedLaunchOpts.value ++ Seq(
    "-Xmx1024M", "-Dplugin.version=" + version.value
  )
}
scriptedBufferLog := false

test.in(Test) := {
  test.in(Test).dependsOn(scripted.toTask("")).value
}

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")

// TODO: Check if neeeded
addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.5")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")
