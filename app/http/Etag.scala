package http

import play.api.http.HeaderNames

/**
 * @author f.patin
 */
trait Etag {

  def eTag() = (HeaderNames.ETAG, this.hashCode().toString)

}
