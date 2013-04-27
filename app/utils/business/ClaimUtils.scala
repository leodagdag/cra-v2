package utils.business

import models.JClaim
import java.util.{List => JList}
import java.math.{BigDecimal => JBigDecimal}
import scala.collection.convert.WrapAsScala._
import constants._

/**
 * @author f.patin
 */
object ClaimUtils {

  def totalKm(cs: JList[JClaim]): JBigDecimal = cs.toList.foldLeft(Zero)((acc, cur) => acc + cur.kilometer).bigDecimal

}
