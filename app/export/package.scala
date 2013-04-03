import com.itextpdf.text.Rectangle

/**
 * Created with IntelliJ IDEA.
 * User: patinfr
 * Date: 03/04/13
 * Time: 13:32
 * To change this template use File | Settings | File Templates.
 */
package object export {
  type Alignment = Int
  type Border = Int
  val BOTTOM_LEFT: Border = Rectangle.BOTTOM + Rectangle.LEFT
  val BOTTOM_RIGHT: Border = Rectangle.BOTTOM + Rectangle.RIGHT
  val BOTTOM: Border = Rectangle.BOTTOM

  val Zero = BigDecimal(java.math.BigDecimal.ZERO)
}
