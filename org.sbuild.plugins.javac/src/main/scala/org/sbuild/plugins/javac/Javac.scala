package org.sbuild.plugins.javac

import de.tototec.sbuild._
import java.io.File

object Javac {
  def forName(name: String)(implicit project: Project) =
    if (name == "") Javac(
      targetName = "compile",
      classpath = "compileCp",
      targetDir = Path("target/classes"),
      srcDirs = Seq(Path("src/main/java"))
    )
    else Javac(
      targetName = s"${name}-compile",
      classpath = s"${name}Cp",
      targetDir = Path("target") / s"$name-classes",
      srcDirs = Seq(Path("src") / name / "java")
    )
}

case class Javac(
  targetName: String,
  classpath: TargetRefs,
  targetDir: File,
  compilerClasspath: Option[TargetRefs] = None,
  sources: Option[TargetRefs] = None,
  srcDirs: Seq[File],
  encoding: String = "UTF-8",
  deprecation: Option[Boolean] = None,
  verbose: Option[Boolean] = None,
  source: Option[String] = None,
  target: Option[String] = None,
  debugInfo: Option[String] = None,
  fork: Boolean = false,
  additionalJavacArgs: Seq[String] = Seq(),
  dependsOn: TargetRefs = TargetRefs())
