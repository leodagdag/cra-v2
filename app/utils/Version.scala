package utils

import java.io.File
import org.apache.commons.io.FileUtils
import scala.collection.JavaConverters._

/**
 * @author f.patin
 */
object Version {
  val version = FileUtils.readLines(new File("conf/version"), "UTF-8").asScala.head
}
