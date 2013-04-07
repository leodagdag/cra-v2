package utils

import play.api.Play.current


/**
 * @author f.patin
 */
object Version {
  lazy val version = current.configuration.getString("version").getOrElse(throw current.configuration.reportError("version", "version needs to be set in application.conf"))
}
