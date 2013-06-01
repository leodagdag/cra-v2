package models

import java.util.Locale
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
 * @author f.patin
 */
package object models {

  private val dtf = DateTimeFormat.forPattern("dd/MM/yyyy").withLocale(Locale.FRANCE)

  def toDateTime(date: String): DateTime = {
    DateTime.parse(date, dtf)
  }

}
