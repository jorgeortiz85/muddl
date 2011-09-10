import sbt._

object GolBuild extends Build {
  lazy val all: Project = Project("all", file(".")) aggregate(compiler, library)

  lazy val compiler = Project("muddl-compiler", file("compiler"), delegates = all :: Nil)
  lazy val library = Project("muddl-library", file("library"), delegates = all :: Nil)
}
