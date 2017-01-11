name := """good-old-play-framework"""
organization := "org.reactivecouchbase"
version := "1.0.1"
scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.play" %% "play" % "2.5.10"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.5.10"
libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.5.10"
libraryDependencies += "com.typesafe.play" %% "play-jdbc" % "2.5.10"
libraryDependencies += "com.typesafe.play" %% "play-cache" % "2.5.10"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.14"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.4.14"

val local: Project.Initialize[Option[sbt.Resolver]] = version { (version: String) =>
  val localPublishRepo = "./repository"
  if(version.trim.endsWith("SNAPSHOT"))
    Some(Resolver.file("snapshots", new File(localPublishRepo + "/snapshots")))
  else Some(Resolver.file("releases", new File(localPublishRepo + "/releases")))
}

publishTo <<= local
publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }
pomExtra := (
  <url>https://github.com/mathieuancelin/good-old-play-framework</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:mathieuancelin/good-old-play-framework.git</url>
    <connection>scm:git:git@github.com:mathieuancelin/good-old-play-framework.git</connection>
  </scm>
  <developers>
    <developer>
      <id>mathieu.ancelin</id>
      <name>Mathieu ANCELIN</name>
      <url>https://github.com/mathieuancelin</url>
    </developer>
  </developers>
)
