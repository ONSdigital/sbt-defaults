package uk.gov.ons.sbt

import sbt.AutoPlugin
import sbt.Keys._
import sbtbuildinfo.BuildInfoPlugin.autoImport.{buildInfoKeys, buildInfoOptions}
import sbtbuildinfo.{BuildInfoKey, BuildInfoOption, BuildInfoPlugin}
import com.typesafe.sbt.SbtGit.git

import uk.gov.ons.sbt.OnsScalaDefaultsPlugin.constant

object OnsBuildInfoDefaultsPlugin extends AutoPlugin {

  override def trigger = allRequirements
  override def requires = BuildInfoPlugin

  override lazy val projectSettings = Seq(
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
      BuildInfoKey.action("projectTeam"){ constant.team },
      BuildInfoKey.action("projectStage"){ constant.projectStage },
      BuildInfoKey.action("repositoryAddress"){ Some(scmInfo.value.get.browseUrl).getOrElse("REPO_ADDRESS_NOT_FOUND")}
    ),
    buildInfoOptions += BuildInfoOption.ToMap,
    buildInfoOptions += BuildInfoOption.ToJson,
    buildInfoOptions += BuildInfoOption.BuildTime
  )

}
