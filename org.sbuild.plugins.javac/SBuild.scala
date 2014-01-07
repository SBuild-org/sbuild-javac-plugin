import de.tototec.sbuild._

@version("0.7.1")
@classpath("mvn:org.sbuild:org.sbuild.plugins.sbuildplugin:0.2.1")
class SBuild(implicit _project: Project) {
  
  import org.sbuild.plugins.sbuildplugin._
  Plugin[SBuildPlugin] configure {
    _.copy(
      sbuildVersion = "0.7.1",
      pluginClass = "org.sbuild.plugins.javac.Javac",
      pluginVersion = "0.0.9000",
      deps = Seq()
    )
  }

}
