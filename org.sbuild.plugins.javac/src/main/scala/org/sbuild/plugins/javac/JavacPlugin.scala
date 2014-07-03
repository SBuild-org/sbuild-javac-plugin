package org.sbuild.plugins.javac

import org.sbuild._

class JavacPlugin(implicit project: Project) extends Plugin[Javac] {

  def create(name: String): Javac = {
    val compileTargetName = s"javac-${name}"
    val cleanTargetName = s"clean-javac-${name}"
    val classpath = TargetRefs()
    val targetDir = Path(s"target/javac-${name}-classes")
    val srcDirs = Seq(Path(s"src/${name}/java"))
    
    Javac(
        compileTargetName = compileTargetName,
        cleanTargetName = Some(cleanTargetName),
        classpath = classpath,
        targetDir = targetDir,
        srcDirs = srcDirs
        )
  }

  def applyToProject(instances: Seq[(String, Javac)]): Unit = instances.foreach {
    case (name, javac) =>

      val sources: TargetRefs = javac.sources match {
        case Some(s) => s
        case None => javac.srcDirs.map(dir => TargetRef(s"scan:$dir;regex=.*\\.java"))
      }

      val compilerClasspath: TargetRefs = javac.compilerClasspath.getOrElse(TargetRefs())
      val dependencies: TargetRefs = javac.dependsOn ~ compilerClasspath ~ javac.classpath ~~ sources

      javac.cleanTargetName.map { cleanTargetName =>
        Target(s"phony:${cleanTargetName}").evictCache(javac.compileTargetName) exec {
          javac.targetDir.deleteRecursive
        }
      }

      Target(s"phony:${javac.compileTargetName}").cacheable dependsOn dependencies exec { ctx: TargetContext =>

        if (sources.files.isEmpty) {
          // TODO: Improve, if for a dedicated error API in SBuild
          // project.monitor.warn("No sources files found.")
          // ctx.error("No source files found.")
          throw new RuntimeException("No source files found.")
        }

        val compiler = new JavacTask(
          classpath = javac.classpath.files,
          sources = sources.files,
          destDir = javac.targetDir,
          encoding = javac.encoding,
          fork = javac.fork,
          additionalJavacArgs = javac.additionalJavacArgs
        )

        javac.compilerClasspath.map { cp => compiler.compilerClasspath = cp.files }
        javac.deprecation.map { d => compiler.deprecation = d }
        javac.verbose.map { d => compiler.verbose = d }
        javac.source.map { d => compiler.source = d }
        javac.target.map { d => compiler.target = d }
        javac.debugInfo.map { d => compiler.debugInfo = d }

        compiler.execute

        javac.targetDir.listFilesRecursive.foreach { f => ctx.attachFile(f) }
      }
  }

}