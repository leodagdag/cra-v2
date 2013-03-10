import java.lang.String
import java.security.MessageDigest
import org.apache.commons.codec.binary.Base64
import org.joda.time.DateTime

val d = new DateTime(1362092400000L)
println(d)

println(new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("lisa".getBytes("UTF-8")))))
println(new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("moe".getBytes("UTF-8")))))
println(new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("ned".getBytes("UTF-8")))))
println(new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("seymour".getBytes("UTF-8")))))
println(new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("marge".getBytes("UTF-8")))))
