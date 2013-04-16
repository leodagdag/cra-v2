import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import java.math.{BigDecimal => JBigDecimal}
/**
 * @author f.patin
 */
package object utils {
  val `dd/MM/yyyy`: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy")
  val `EEE dd` = DateTimeFormat.forPattern("EEE dd")
  val `yyyy-MM-dd_HH-mm-ss` = DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss")
  val `MMMM yyyy` = DateTimeFormat.forPattern("MMMM yyyy")


  val TWO = new JBigDecimal(2)
}
