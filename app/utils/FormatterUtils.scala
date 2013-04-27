package utils

import org.springframework.format.number.{NumberFormatter, CurrencyFormatter}
import java.util.Locale
import java.math.{BigDecimal => JBigDecimal}

/**
 * @author f.patin
 */
object FormatterUtils {

  def toCurrency(s: String): String = toCurrency(BigDecimal(s))

  def toCurrency(bd: JBigDecimal): String = toCurrency(bd: BigDecimal)

  def toCurrency(bd: BigDecimal): String = new CurrencyFormatter().print(bd, Locale.FRANCE)

  def toKm(bd: JBigDecimal): String = toKm(bd: BigDecimal)

  def toKm(bd: BigDecimal): String = new NumberFormatter("0.00 km").print(bd, Locale.FRANCE)

  def toDay(bd: BigDecimal): String = new NumberFormatter("0.00 j").print(bd, Locale.FRANCE)

  def toHour(bd: BigDecimal): String = new NumberFormatter("0.00 h").print(bd, Locale.FRANCE)

  def toEuroByKm(bd: BigDecimal): String = new NumberFormatter("0.00 €/km").print(bd, Locale.FRANCE)
}
