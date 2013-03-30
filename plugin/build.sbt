name:="scala-language-import"

organization:="org.github.rssh"

scalaVersion := "2.11.0-SNAPSHOT"

scalaOrganization := "org.scala-lang.macro-paradise"

resolvers += Resolver.sonatypeRepo("snapshots")

scalacOptions ++= Seq("-unchecked","-deprecation")

libraryDependencies += "org.scala-lang.macro-paradise" % "scala-reflect" % "2.11.0-SNAPSHOT"

libraryDependencies += "org.scala-lang.macro-paradise" % "scala-compiler" % "2.11.0-SNAPSHOT"

version:="0.0.1-SNAPSHOT"

