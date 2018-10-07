name := "domy"
 
version := "1.0" 
      
lazy val `domy` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )
libraryDependencies += "com.coreos" % "jetcd-core" % "0.0.2"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  
