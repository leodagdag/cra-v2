package utils

import play.api.Play.current

/**
 * @author f.patin
 */
object AppConfig {
  lazy val defaultPassword = current.configuration.getString("default.password").getOrElse(throw new RuntimeException("default.password needs to be set in application.conf in order to batch upload user"))
}
