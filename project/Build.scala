import sbt._
import Keys._


object SliBuild extends Build
{

   lazy val root = Project(id="sli",
                           base=file(".")
                          ).aggregate("plugin","runtime","example-library", "example-application")


   lazy val plugin = Project(id="plugin",
                             base=file("plugin"),
                             settings=pluginSettings)

   lazy val runtime = Project(id="runtime",
                             base=file("runtime"),
                             settings=runtimeSettings)

   lazy val exampleLibrary = Project(id="example-library",
                              base=file("example/library"),
                              settings=exampleLibrarySettings) dependsOn (plugin, runtime)

   lazy val exampleApplication = Project(id="example-application",
                              base=file("example/application"),
                              settings=exampleApplicationSettings) dependsOn (plugin, runtime, exampleLibrary)


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
 
   lazy val exampleLibrarySettings = Project.defaultSettings ++ commonSettings ++ Seq(
                               name := "sli-example-library"
                             )

   lazy val exampleApplicationSettings = Project.defaultSettings ++ commonSettings ++ Seq(
                               name := "sli-example-application",
                               autoCompilerPlugins := true,
                               addCompilerPlugin("org.github.rssh" %% "sli-plugin" % "0.0.1-SNAPSHOT"),
                               scalacOptions += "-Xplugin:plugin/target/scala-2.11/sli-plugin_2.11-0.0.1-SNAPSHOT.jar",
                               scalacOptions += "-Xprint:macrohook",
                               scalacOptions += "-Ydebug",
                               scalacOptions += "-Yshow-trees"
                             )


}
