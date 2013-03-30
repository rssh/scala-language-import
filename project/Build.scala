import sbt._
import Keys._


object SliBuild extends Build
{

   lazy val root = Project(id="sli",
                           base=file(".")
                          ).aggregate("plugin","runtime","example")


   lazy val plugin = Project(id="plugin",
                             base=file("plugin"),
                             settings=pluginSettings)

   lazy val runtime = Project(id="runtime",
                             base=file("runtime"),
                             settings=runtimeSettings)

   lazy val example = Project(id="example",
                              base=file("example"),
                              settings=exampleSettings) dependsOn (plugin, runtime)

   lazy val commonSettings = Seq(
        organization:="org.github.rssh",
        scalaVersion := "2.11.0-SNAPSHOT",
        scalaOrganization := "org.scala-lang.macro-paradise",
        resolvers += Resolver.sonatypeRepo("snapshots"),
        scalacOptions ++= Seq("-unchecked","-deprecation"),
        libraryDependencies += "org.scala-lang.macro-paradise" % "scala-reflect" % "2.11.0-SNAPSHOT",
        libraryDependencies += "org.scala-lang.macro-paradise" % "scala-compiler" % "2.11.0-SNAPSHOT",
        version:="0.0.1-SNAPSHOT"
   );

   lazy val pluginSettings = Project.defaultSettings ++ commonSettings ++ Seq(
                               name := "sli-plugin"
                             )


   lazy val runtimeSettings = Project.defaultSettings ++ commonSettings ++ Seq(
                               name := "sli-runtime"
                             )
 
   lazy val exampleSettings = Project.defaultSettings ++ commonSettings ++ Seq(
                               name := "sli-example"
                             )

}
