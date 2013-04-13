package utils

import java.security.MessageDigest
import org.apache.commons.codec.binary.Base64

/**
 * @author f.patin
 */
object MD5 {

  def apply(s: String): String = {
    new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8"))))
  }
}
