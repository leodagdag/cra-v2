import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

	val appName = "cra-v2"
	val appVersion = "2.0.8-SNAPSHOT"

	val appDependencies = Seq(
		// Add your project dependencies here,
		javaCore,
		// Morphia & MongoDB
		"leodagdag" %% "play2-morphia-plugin" % "0.0.14",
		// security
		"be.objectify" %% "deadbolt-java" % "2.1-SNAPSHOT",
		// email
		"com.typesafe" %% "play-plugins-mailer" % "2.1.0",
		// PDF
		"com.itextpdf" % "itextpdf" % "5.3.4",
		// security
		"be.objectify" %% "deadbolt-scala" % "2.1-SNAPSHOT",
		// MongoDB
		"org.reactivemongo" %% "play2-reactivemongo" % "0.8"
	)

	val main = play.Project(appName, appVersion, appDependencies)
		.settings(
		lessEntryPoints <<= baseDirectory(appLessEntryPoints)
	)
		.settings(
		// Morphia & MongoDB
		resolvers += "LeoDagDag repository" at "http://leodagdag.github.com/repository/",
		// ReactiveMongo
		resolvers += "ReactiveMongo Snapshot" at "https://oss.sonatype.org/content/repositories/snapshots",
		// Deadbolt
		resolvers += Resolver.url("Objectify Play Repository", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
		resolvers += Resolver.url("Objectify Play Snapshot Repository", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns),
		checksums := Nil
	)

	// Only compile the bootstrap bootstrap.less file and any other *.less file in the stylesheets directory
	def appLessEntryPoints(base: File): PathFinder = (
		(base / "app" / "assets" / "stylesheets" / "vendors" / "bootstrap-2.3.0" ** "bootstrap.less") +++
			(base / "app" / "assets" / "stylesheets" / "vendors" / "bootstrap-2.3.0" ** "responsive.less") +++
			(base / "app" / "assets" / "stylesheets" ** "main.less")
		)

}
