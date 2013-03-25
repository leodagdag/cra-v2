import java.lang.String
import java.security.MessageDigest
import org.apache.commons.codec.binary.Base64
import org.joda.time.DateTime

val d = new DateTime(1362092400000L)
println(d)

println(s"lisa:[${new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("lisa".getBytes("UTF-8"))))}]")
println(s"moe:[${new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("moe".getBytes("UTF-8"))))}]")
println(s"ned:[${new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("ned".getBytes("UTF-8"))))}]")
println(s"seymour:[${new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("seymour".getBytes("UTF-8"))))}]")
println(s"marge:[${new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("marge".getBytes("UTF-8"))))}]")
println(s"bart-tpl1:[${new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("bart-tpl1".getBytes("UTF-8"))))}]")
println(s"bart-tpl2:[${new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("bart-tpl2".getBytes("UTF-8"))))}]")
println(s"bart-tpa1:[${new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("bart-tpa1".getBytes("UTF-8"))))}]")
println(s"bart-tpa2:[${new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("bart-tpa2".getBytes("UTF-8"))))}]")
println(s"moe-tpl1:[${new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("moe-tpl1".getBytes("UTF-8"))))}]")
println(s"moe-tpl2:[${new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("moe-tpl2".getBytes("UTF-8"))))}]")
println(s"moe-tpa1:[${new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("moe-tpa1".getBytes("UTF-8"))))}]")
println(s"moe-tpa2:[${new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("moe-tpa2".getBytes("UTF-8"))))}]")
println("")
println(s"true && false = ${true && false}")
println(s"true || false = ${true || false}")
println(s"true && true = ${true && true}")
println(s"true || true = ${true || true}")
println(s"false && false = ${false && false}")
println(s"false || false = ${false || false}")
