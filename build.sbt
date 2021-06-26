name := "search-app"

scalaVersion := "2.13.6"

val monixVersion = "3.4.0"
val fs2Version = "3.0.4"
val catsEffectVersion = "3.1.1"
val circeVersion = "0.14.1"
val circeFs2Version = "0.14.0"
val specs2Version = "4.12.2"

mainClass := Some("com.ff.search.App")

libraryDependencies ++= Seq(
  "org.typelevel"   %%    "cats-effect"             % catsEffectVersion,
  "co.fs2"          %%    "fs2-core"                % fs2Version,
  "co.fs2"          %%    "fs2-io"                  % fs2Version,
  "io.circe"        %%    "circe-core"              % circeVersion,
  "io.circe"        %%    "circe-generic"           % circeVersion,
  "io.circe"        %%    "circe-literal"           % circeVersion,
  "io.circe"        %%    "circe-fs2"               % circeFs2Version,
  "org.typelevel"   %%    "cats-effect-laws"        % catsEffectVersion   % Test,
  "org.specs2"      %%    "specs2-core"             % specs2Version       % Test,
  "org.specs2"      %%    "specs2-cats"             % specs2Version       % Test,
  "org.specs2"      %%    "specs2-matcher-extra"    % specs2Version       % Test
)

scalacOptions ++= Seq(
  "-Xlint:-byname-implicit",
  "-Wdead-code",
  "-Werror",
  "-Wextra-implicit",
  "-Wnumeric-widen",
  "-Woctal-literal",
  "-Wunused",
  "-Wvalue-discard",
  "-Xlint",
  "-deprecation",
  "-feature",
  "-language:higherKinds",
  "-unchecked"
)

Test / scalacOptions --= Seq(
  "-Yrangepos",
  "-Wnumeric-widen",
  "-Wvalue-discard"
)

Compile / console / scalacOptions --= Seq(
  "-Werror",
  "-Wunused",
  "-Xlint"
)

initialCommands := (List(
    "scala.util._",
    "scala.concurrent.ExecutionContext.Implicits.global",
    "fs2._",
    "cats._",
    "cats.data._",
    "cats.effect._",
    "cats.syntax.all._",
    "scala.concurrent.duration._",
    "monix.execution.Scheduler.Implicits.global",
    "monix.eval._",
    "monix.reactive._"
  )).mkString("import ", ", ", "")
