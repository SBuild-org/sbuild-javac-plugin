import de.tototec.sbuild._

import de.tototec.sbuild.ant._
import de.tototec.sbuild.ant.tasks._

@version("0.7.1")
@classpath(
  "mvn:org.sbuild:org.sbuild.plugins.sbuildplugin:0.3.0",
  "mvn:org.apache.ant:ant:1.8.4",
  "mvn:org.sbuild:org.sbuild.plugins.mavendeploy:0.1.0"
)
class SBuild(implicit _project: Project) {

  val namespace = "org.sbuild.plugins.javac"
  val version = "0.0.9001"
  val url = "https://github.com/SBuild-org/sbuild-javac-plugin"
  val sourcesJar = s"target/${namespace}-${version}-sources.jar"
  val sourcesDir = "src/main/scala"
  val sbuildVersion = "0.7.9010.0-8-0-M1"

  Target("phony:all") dependsOn "jar" ~ sourcesJar

  import org.sbuild.plugins.sbuildplugin._

  val scalaVersion = "2.11.1"
  val scalaBinVersion = "2.11"
  val sbuildPluginVersion = new SBuildVersion {
    override val version: String = sbuildVersion
    override val sbuildClasspath: TargetRefs =
      s"http://sbuild.org/uploads/sbuild/${sbuildVersion}/org.sbuild-${sbuildVersion}.jar"
    override val scalaClasspath: TargetRefs =
      s"mvn:org.scala-lang:scala-library:${scalaVersion}" ~
        s"mvn:org.scala-lang:scala-reflect:${scalaVersion}" ~
        s"mvn:org.scala-lang.modules:scala-xml_${scalaBinVersion}:1.0.1"
    override val scalaCompilerClasspath: TargetRefs =
      s"mvn:org.scala-lang:scala-library:${scalaVersion}" ~
        s"mvn:org.scala-lang:scala-reflect:${scalaVersion}" ~
        s"mvn:org.scala-lang:scala-compiler:${scalaVersion}"
    override val scalaTestClasspath: TargetRefs =
      s"mvn:org.scalatest:scalatest_${scalaBinVersion}:2.2.0"
  }

  Plugin[SBuildPlugin] configure (_.copy(
    sbuildVersion = sbuildPluginVersion,
    pluginClass = s"${namespace}.Javac",
    pluginVersion = version
  ))

  import org.sbuild.plugins.mavendeploy._
  Plugin[MavenDeploy] configure (_.copy(
    groupId = "org.sbuild",
    artifactId = namespace,
    version = version,
    artifactName = Some("SBuild Javac Plugin"),
    description = Some("An SBuild Plugin that provides Javac compiler support."),
    repository = Repository.SonatypeOss,
    scm = Option(Scm(url = url, connection = url)),
    developers = Seq(Developer(id = "TobiasRoeser", name = "Tobias Roeser", email = "le.petit.fou@web.de")),
    gpg = true,
    licenses = Seq(License.Apache20),
    url = Some(url),
    files = Map(
      "jar" -> s"target/${namespace}-${version}.jar",
      "sources" -> s"target/${namespace}-${version}-sources.jar",
      "javadoc" -> "target/fake.jar"
    )
  ))

  Target(sourcesJar) dependsOn s"scan:${sourcesDir}" ~ "LICENSE.txt" exec { ctx: TargetContext =>
    AntZip(destFile = ctx.targetFile.get, fileSets = Seq(
      AntFileSet(dir = Path(sourcesDir)),
      AntFileSet(file = Path("LICENSE.txt"))
    ))
  }

  Target("target/fake.jar") dependsOn "LICENSE.txt" exec { ctx: TargetContext =>
    import de.tototec.sbuild.ant._
    tasks.AntJar(destFile = ctx.targetFile.get, fileSet = AntFileSet(file = "LICENSE.txt".files.head))
  }

}
