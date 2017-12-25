package uk.gov.ons.sbt

import sbt.Keys.{logLevel, scalacOptions, _}
import sbt._
import sbt.plugins.JvmPlugin
import sbtbuildinfo.BuildInfoPlugin.autoImport.{buildInfoKeys, buildInfoOptions, buildInfoPackage}
import sbtbuildinfo.{BuildInfoKey, BuildInfoOption}
import com.typesafe.sbt.SbtGit.git

//TODO: Check if Artifactory settings required
object OnsDefaultsPlugin extends AutoPlugin {

  override def trigger = allRequirements
  override def requires = JvmPlugin

  override lazy val projectSettings = {
    resolverSettings ++
    sbtExecutionSettings ++
    scalaSettings ++
    organizationSettings ++
    testSettings ++
    libraryDependencySettings ++
    buildInfoConfig ++
    Seq(
      logLevel := Level.Warn
    )
  }

  lazy val Constant = new {
    val projectStage = "alpha"
    val team = "sbr"
    val local = "mac"
    val repoName = "admin-data"
  }

  private[this] val organizationSettings = Seq(
    organization := "uk.gov.ons",
    organizationName := "ons",
    developers := List(Developer("Adrian Harris (Tech Lead)", "SBR", "ons-sbr-team@ons.gov.uk", new java.net.URL(s"https:///v1/home"))),
    version := (version in ThisBuild).value,
    licenses := Seq("MIT-License" -> url("https://github.com/ONSdigital/sbr-control-api/blob/master/LICENSE")),
    startYear := Some(2017),
    homepage := Some(url("https://SBR-UI-HOMEPAGE.gov.uk"))
  )

  private[this] val scalaSettings = Seq(
    scalaVersion := "2.11.11",
    scalacOptions in ThisBuild ++= scalacOptionSettings
  )

  private[this] val scalacOptionSettings = Seq(
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

  private[this] val resolverSettings = Seq(
    resolvers ++= Seq(
      //    "Typesafe hbase.repository" at "http://repo.typesafe.com/typesafe/releases/",
      "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
      Resolver.typesafeRepo("releases")
    )
  )

  private[this] val sbtExecutionSettings: Seq[Def.Setting[_]] = Seq(
    crossPaths := false,
    parallelExecution := false,
    // Java heap memory memory allocation - lots of deps
    javaOptions += "-Xmx2G"
  )

  private[this] val testSettings = Seq(
    testOptions in Test := Seq(
      Tests.Argument(TestFrameworks.JUnit, "-a"),
      Tests.Argument("-oG")
    ),
    fork in test := true,
    logBuffered in Test := false
  )

  private[this] val libraryDependencySettings = Seq(
    libraryDependencies ++= testDependencySettings
  )

  private[this] val testDependencySettings = Seq(
    "junit"          %  "junit"           %   "4.12"      %  Test,
    "org.scalatest"  %% "scalatest"       %   "3.0.4"     %  Test,
    "org.mockito"    %  "mockito-core"    %   "2.10.0"    %  Test,
    "com.novocode"   %  "junit-interface" %   "0.11"      %  Test
  )

  private[this] val buildInfoConfig = Seq(
    buildInfoPackage := "controllers",
    // gives us last compile time and tagging info
    buildInfoKeys := Seq[BuildInfoKey](
      organizationName,
      moduleName,
      name,
      description,
      developers,
      version,
      scalaVersion,
      sbtVersion,
      startYear,
      homepage,
      BuildInfoKey.action("gitVersion") {
        git.formattedShaVersion.?.value.getOrElse(Some("Unknown")).getOrElse("Unknown") +"@"+ git.formattedDateVersion.?.value.getOrElse("")
      },
      BuildInfoKey.action("codeLicenses"){ licenses.value },
      BuildInfoKey.action("projectTeam"){ Constant.team },
      BuildInfoKey.action("projectStage"){ Constant.projectStage },
      BuildInfoKey.action("repositoryAddress"){ Some(scmInfo.value.get.browseUrl).getOrElse("REPO_ADDRESS_NOT_FOUND")}
    ),
    buildInfoOptions += BuildInfoOption.ToMap,
    buildInfoOptions += BuildInfoOption.ToJson,
    buildInfoOptions += BuildInfoOption.BuildTime
  )

}
