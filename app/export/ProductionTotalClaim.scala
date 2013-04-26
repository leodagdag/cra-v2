package export

import scala.collection.convert.WrapAsScala._
import models.{JCra, JMission, JClaim}
import constants.{MissionType, ClaimType, MissionAllowanceType}
import com.itextpdf.text.Rectangle
import utils.time.TimeUtils
import utils._
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import scala.collection.immutable.Iterable

/**
 * @author f.patin
 */
case class ProductionTotalClaim(cra: JCra, currentMission: JMission) extends TableTools {

  def compose(): PdfPTable = {

    val cs = totalFeeByWeek
    val tableByWeek = newTable(cs.size / 2)
    cs.foreach(tableByWeek.addCell(_))


    val tableByMonth = newTable(6)
    totalByMonth.foreach(tableByMonth.addCell(_))

    val tableByMission = newTable(totalFeeByMission.size/2)
    totalFeeByMission.foreach(tableByMission.addCell(_))

    val table = newTable(1)
    table.setHeaderRows(1)
    table.addCell("Synthèse des frais")
    table.addCell(tableByWeek)
    table.addCell(tableByMonth)
    table.addCell(tableByMission)

    table
  }

  protected def newTable(numColumns: Int) = super.newTable(numColumns, 5f)

  private def newCell(text: String) = super.newCell(text, border = Rectangle.BOX, maxLength = None)

  private val currentMissionClaimType: ClaimType = MissionAllowanceType.valueOf(currentMission.allowanceType).claimType

  private val claims = JClaim.synthesis(cra.userId, cra.year, cra.month).toList

  private val currentMissionClaims = claims
    .filter(c => ClaimType.valueOf(c.claimType).equals(currentMissionClaimType))
    .groupBy(c => TimeUtils.getMondayOfDate(c.date))
    .map(claim => claim._1 -> claim._2.foldLeft(Zero)((acc, curr) => acc + curr.amount))
  private val totalCurrentMissionClaims = currentMissionClaims.values.foldLeft(Zero)((acc, curr) => acc + curr)
  private val totalFeeByWeek = {
    (newCell("Semaines") +: currentMissionClaims.map(c => newCell(s"${`dd`.print(c._1)}->${`dd/MM`.print(TimeUtils.getSundayOfDate(c._1))} (${c._1.getWeekOfWeekyear})")).toList :+ newCell("Total")) ++
      (newCell(currentMissionClaimType.label.capitalize) +: currentMissionClaims.map(c => newCell(toCurrency(c._2))).toList :+ newCell(toCurrency(totalCurrentMissionClaims)))
  }

  private val realClaims = claims
    .filterNot(c => ClaimType.valueOf(c.claimType).equals(ClaimType.FIXED_FEE) || ClaimType.valueOf(c.claimType).equals(ClaimType.ZONE_FEE))
    .groupBy(c => TimeUtils.getMondayOfDate(c.date))
    .map(claim => claim._1 -> claim._2.foldLeft(Zero)((acc, curr) => acc + curr.amount))
  private val totalRealClaims = realClaims.values.foldLeft(Zero)((acc, curr) => acc + curr)
  private val totalByMonth = {
    newCell(currentMissionClaimType.label.capitalize) :: newCell(toCurrency(totalCurrentMissionClaims)) ::
      newCell("Frais réel") :: newCell(toCurrency(totalRealClaims)) ::
      newCell("Total") :: newCell(toCurrency(totalCurrentMissionClaims + totalRealClaims)) ::
      Nil
  }

  val claimsByMission: Map[JMission, BigDecimal] = claims
    .groupBy(c => c.missionId)
    .map(c => JMission.codeAndMissionType(c._1) -> c._2.foldLeft(Zero)((acc, curr) => acc + curr.amount))

  val claimsByCustomerMission = claimsByMission.filter(filterByCustomerMissionType).toList

  def filterByCustomerMissionType: ((JMission, BigDecimal)) => Boolean = c => MissionType.valueOf(c._1.missionType).equals(MissionType.customer)

  val totalGenesisMission = claimsByMission
    .filterNot(filterByCustomerMissionType)
    .values.foldLeft(Zero)((acc, curr) => acc + curr)

  val totalMonth = claims.foldLeft(Zero)((acc, curr) => acc + curr.amount)

  val totalFeeByMission = {
    ((claimsByCustomerMission.map(c => newCell(c._1.code)) :+ newCell("Genesis")) :+ newCell("Mois")) ++
      ((claimsByCustomerMission.map(c => newCell(toCurrency(c._2))) :+ newCell(toCurrency(totalGenesisMission))) :+ newCell(toCurrency(totalMonth)))
  }
}