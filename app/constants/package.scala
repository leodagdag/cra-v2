import java.math.{BigDecimal => JBigDecimal}

/**
 * @author f.patin
 */
package object constants {
  val Zero: BigDecimal = JBigDecimal.ZERO
  val ZeroPointFive = BigDecimal("0.5")
  val ThreePointSeven: BigDecimal = Util.THREE_POINT_SEVEN

}
