name := "rest_pinboard_api"

version := "1.0"

scalaVersion := "2.11.8"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  ws,
  "com.h2database" % "h2" % "1.4.192",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.2.0",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0"
)