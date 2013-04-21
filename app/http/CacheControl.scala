package http

import play.api.http.HeaderNames

/**
 * @author f.patin
 */
object CacheControl {

  val maxAgeO = (HeaderNames.CACHE_CONTROL, "max-age=0")
}
