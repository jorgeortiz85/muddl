import sbt._

object GolBuild extends Build {
  lazy val all: Project = Project("all", file(".")) aggregate(compiler, library)

  lazy val compiler = Project("gol-compiler", file("compiler"), delegates = all :: Nil)
  lazy val library = Project("gol-library", file("library"), delegates = all :: Nil)
}
