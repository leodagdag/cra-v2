import com.itextpdf.text.{Element, Rectangle}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter, DateTimeFormatterBuilder}

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


  val Zero = BigDecimal(java.math.BigDecimal.ZERO)
  val ZeroPointFive = BigDecimal("0.5")

  val dummyCellContent = " "

  val `dd/MM/yyyy`: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy")

}
