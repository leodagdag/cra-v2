import com.itextpdf.text.Rectangle
import models.Day

/**
 * @author f.patin
 */
package object export {
  type Alignment = Int
  type Border = Int
  val BOTTOM_LEFT: Border = Rectangle.BOTTOM + Rectangle.LEFT
  val BOTTOM_RIGHT: Border = Rectangle.BOTTOM + Rectangle.RIGHT
  val BOTTOM: Border = Rectangle.BOTTOM
  val NO_BORDER: Border = Rectangle.NO_BORDER


  val Zero = BigDecimal(java.math.BigDecimal.ZERO)


}
