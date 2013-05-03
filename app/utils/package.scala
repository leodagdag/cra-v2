import java.util.Locale
import models.JHalfDay
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import java.math.{BigDecimal => JBigDecimal}

/**
 * @author f.patin
 */
package object utils {
  val `dd/MM/yyyy`: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy").withLocale(Locale.FRANCE)
  val `dd/MM`: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM").withLocale(Locale.FRANCE)
  val `dd`: DateTimeFormatter = DateTimeFormat.forPattern("dd").withLocale(Locale.FRANCE)
  val `EEE dd` = DateTimeFormat.forPattern("EEE dd").withLocale(Locale.FRANCE)
  val `yyyy-MM-dd_HH-mm-ss` = DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss").withLocale(Locale.FRANCE)
  val `MMMM yyyy` = DateTimeFormat.forPattern("MMMM yyyy").withLocale(Locale.FRANCE)
  val `dd/MM/yyyy à HH:mm:ss` = DateTimeFormat.forPattern("yyyy/MM/dd à HH:mm:ss").withLocale(Locale.FRANCE)

  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)

  implicit def halfDayOrdering: Ordering[JHalfDay] = Ordering.fromLessThan {
    (hd1, hd2) =>
      if (hd1 == null && hd2 == null) true
      else if (hd1 == null) false
      else if (hd2 == null) true
      else hd1.momentOfDay < hd2.momentOfDay
  }

  val TWO = new JBigDecimal(2)
}
