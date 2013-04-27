import com.itextpdf.text.{Element, Rectangle}
import java.util.Locale
import org.springframework.format.number.{CurrencyFormatter, NumberFormatter}
import utils.FormatterUtils


/**
 * @author f.patin
 */
package object export {
  type Alignment = Int
  val CENTER: Alignment = Element.ALIGN_CENTER
  val LEFT: Alignment = Element.ALIGN_LEFT
  val RIGHT: Alignment = Element.ALIGN_RIGHT

  type Border = Int
  val BOTTOM_LEFT: Border = Rectangle.BOTTOM + Rectangle.LEFT
  val RIGHT_BOTTOM_LEFT: Border = Rectangle.RIGHT + Rectangle.BOTTOM + Rectangle.LEFT
  val LEFT_TOP_RIGHT: Border = Rectangle.LEFT + Rectangle.TOP + Rectangle.RIGHT
  val BOTTOM_RIGHT: Border = Rectangle.BOTTOM + Rectangle.RIGHT
  val BOTTOM: Border = Rectangle.BOTTOM
  val NO_BORDER: Border = Rectangle.NO_BORDER
  val TOP: Border = Rectangle.TOP



  val dummyContent = " "

  val `3,7` = "3,7"

  def toCurrency(bd: BigDecimal) = FormatterUtils.toCurrency(bd)

  def toKm(bd: BigDecimal) = FormatterUtils.toKm(bd)

  def toDay(bd: BigDecimal) = FormatterUtils.toDay(bd)

  def toHour(bd: BigDecimal) = FormatterUtils.toHour(bd)

  def toEuroByKm(bd: BigDecimal) = FormatterUtils.toEuroByKm(bd)
}
