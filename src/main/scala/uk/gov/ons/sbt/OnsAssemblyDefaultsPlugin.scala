package uk.gov.ons.sbt

import sbt.AutoPlugin
import sbt.Keys.{name, version}
import sbtassembly.AssemblyPlugin.autoImport.{MergeStrategy, assembly, assemblyJarName, assemblyMergeStrategy}
import sbtassembly.{AssemblyPlugin, PathList}

object OnsAssemblyDefaultsPlugin extends AutoPlugin {

  override def trigger = allRequirements
  override def requires = AssemblyPlugin

  override lazy val projectSettings = Seq(
    assemblyJarName in assembly := s"${name.value}-assembly-${version.value}.jar",
    assemblyMergeStrategy in assembly := {
      case PathList("org", "apache", xs@_*) => MergeStrategy.last
      case PathList("META-INF", "io.netty.versions.properties", xs@_ *) => MergeStrategy.last
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )

}
