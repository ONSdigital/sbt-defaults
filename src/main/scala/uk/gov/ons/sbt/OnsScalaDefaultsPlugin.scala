package uk.gov.ons.sbt

import sbt.Keys.{logLevel, scalacOptions, _}
import sbt._
import sbt.plugins.JvmPlugin
import sbtassembly.AssemblyPlugin.autoImport.assembly
import sbtrelease.ReleasePlugin.autoImport.{releaseCommitMessage, releaseIgnoreUntrackedFiles, releaseTagComment}

object OnsScalaDefaultsPlugin extends AutoPlugin {

  override def trigger = allRequirements
  override def requires = JvmPlugin

  override lazy val projectSettings = {
    resolverSettings ++
    sbtExecutionSettings ++
    scalaSettings ++
    organizationSettings ++
    testSettings ++
    libraryDependencySettings ++
    publishSettings ++
    releaseSettings ++
    Seq(
      logLevel := Level.Warn
    )
  }

  private[this] lazy val organizationSettings = Seq(
    organization := "uk.gov.ons",
    organizationName := "ons",
    developers := List(Developer("Adrian Harris (Tech Lead)", "SBR", "ons-sbr-team@ons.gov.uk", new java.net.URL(s"https:///v1/home"))),
    version := (version in ThisBuild).value,
    licenses := Seq("MIT-License" -> url("https://github.com/ONSdigital/sbr-control-api/blob/master/LICENSE")),
    startYear := Some(2017),
    homepage := Some(url("https://SBR-UI-HOMEPAGE.gov.uk"))
  )

  private[this] lazy val scalaSettings = Seq(
    scalaVersion := "2.11.11",
    scalacOptions in ThisBuild ++= scalacOptionSettings
  )

  private[this] lazy val scalacOptionSettings = Seq(
    "-language:experimental.macros",
    "-target:jvm-1.8",
    "-encoding", "UTF-8",
    "-language:reflectiveCalls",
    "-language:experimental.macros",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:postfixOps",
    "-deprecation", // warning and location for usages of deprecated APIs
    "-feature", // warning and location for usages of features that should be imported explicitly
    "-unchecked", // additional warnings where generated code depends on assumptions
    "-Xlint", // recommended additional warnings
    "-Xcheckinit", // runtime error when a val is not initialized due to trait hierarchies (instead of NPE somewhere else)
    "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
    //"-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver
    "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
    "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures
    "-Ywarn-dead-code", // Warn when dead code is identified
    "-Ywarn-unused", // Warn when local and private vals, vars, defs, and types are unused
    "-Ywarn-unused-import", //  Warn when imports are unused (don't want IntelliJ to do it automatically)
    "-Ywarn-numeric-widen" // Warn when numerics are widened
  )

  private[this] lazy val resolverSettings = Seq(
    resolvers ++= Seq(
      //    "Typesafe hbase.repository" at "http://repo.typesafe.com/typesafe/releases/",
      "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
      Resolver.typesafeRepo("releases")
    )
  )

  private[this] lazy val sbtExecutionSettings: Seq[Def.Setting[_]] = Seq(
    crossPaths := false,
    parallelExecution := false,
    // Java heap memory memory allocation - lots of deps
    javaOptions += "-Xmx2G",
    exportJars := true
  )

  private[this] lazy val testSettings = Seq(
    testOptions in Test := Seq(
      Tests.Argument(TestFrameworks.JUnit, "-a"),
      Tests.Argument("-oG")
    ),
    fork in test := true,
    logBuffered in Test := false
  )

  private[this] lazy val libraryDependencySettings = Seq(
    libraryDependencies ++= testDependencySettings
  )

  private[this] lazy val testDependencySettings = Seq(
    "junit"          %  "junit"           %   "4.12"      %  Test,
    "org.scalatest"  %% "scalatest"       %   "3.0.4"     %  Test,
    "org.mockito"    %  "mockito-core"    %   "2.10.0"    %  Test,
    "com.novocode"   %  "junit-interface" %   "0.11"      %  Test
  )

  private[this] lazy val publishSettings = {
    val artifactoryHost = System.getenv("ARTIFACTORY_HOST")

    Seq(
      publishArtifact := false,
      publishMavenStyle := false,
      checksums in publish := Nil,
      publishArtifact in Test := false,
      publishArtifact in (Compile, packageBin) := false,
      publishArtifact in (Compile, packageSrc) := false,
      publishArtifact in (Compile, packageDoc) := false,
      publishTo := {
        if (isSnapshot.value)
          Some("ONS Snapshots" at s"https://$artifactoryHost/content/repositories/snapshots")
        else
          Some("ONS Releases" at s"https://$artifactoryHost/content/repositories/releases")
      },
      artifact in (Compile, assembly) ~= { art =>
        art.copy(`type` = "jar", `classifier` = Some("assembly"))
      },
      artifactName := { (sv: ScalaVersion, module: ModuleID, artefact: Artifact) =>
        module.organization + "_" + artefact.name + "-" + artefact.classifier.getOrElse("package") + "-" + module.revision + "." + artefact.extension
      },
      credentials += Credentials("Artifactory Realm", artifactoryHost, System.getenv("ARTIFACTORY_USER"), System.getenv("ARTIFACTORY_PASSWORD"))
    )
  }

  private[this] lazy val releaseSettings = Seq(
    releaseTagComment := s"Releasing $name ${(version in ThisBuild).value}",
    releaseCommitMessage := s"Setting Release tag to ${(version in ThisBuild).value}",
    // no commit - ignore zip and other package files
    releaseIgnoreUntrackedFiles := true
  )

}
