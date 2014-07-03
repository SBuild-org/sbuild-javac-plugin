package org.sbuild.plugins.javac

import org.sbuild._
import java.io.File

object Javac {
  def forName(name: String)(implicit project: Project) =
    if (name == "") Javac(
      compileTargetName = "compile",
      cleanTargetName = Some("clean-compile"),
      classpath = TargetRefs(),
      targetDir = Path("target/classes"),
      srcDirs = Seq(Path("src/main/java"))
    )
    else {
      val nameUpper = name.take(1).toUpperCase() + name.drop(1)
      Javac(
        compileTargetName = s"compile-${name}",
        cleanTargetName = Some(s"clean-compile-${name}"),
        classpath = TargetRefs(),
        targetDir = Path("target") / s"${name}-classes",
        srcDirs = Seq(Path("src") / name / "java")
      )
    }
}

case class Javac(
    compileTargetName: String,
    cleanTargetName: Option[String],
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
    dependsOn: TargetRefs = TargetRefs()) {

  override def toString = getClass().getSimpleName() +
    "(compileTargetName=" + compileTargetName +
    ",cleanTargetName=" + cleanTargetName +
    ",classpath=" + classpath +
    ",targetDir=" + targetDir +
    ",compilerClasspath=" + compilerClasspath +
    ",sources=" + sources +
    ",srcDirs=" + srcDirs +
    ",encoding=" + encoding +
    ",deprecation=" + deprecation +
    ",verbose=" + verbose +
    ",source=" + source +
    ",target=" + target +
    ",debugInfo=" + debugInfo +
    ",fork=" + fork +
    ",additionalJavacArgs=" + additionalJavacArgs +
    ",dependsOn=" + dependsOn +
    ")"
}
