package export

import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfPTable
import constants._
import models.{JAffectedMission, JUser, JCra, JMission, JClaim}
import scala.collection.convert.WrapAsScala._
import scala.collection.immutable.TreeMap
import utils._
import utils.time.TimeUtils
import scala.collection.mutable

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

    val table = newTable(1)
    table.setHeaderRows(1)
    table.addCell("Synthèse des frais")
    table.addCell(tableByWeek)
    table.addCell(tableByMonth)

    table
  }

  val user = JUser.fetch(cra.userId)

  protected def newTable(numColumns: Int) = super.newTable(numColumns, 5f)

  private def newCell(text: String) = super.newCell(text, border = Rectangle.BOX, maxLength = None)

  private val currentMissionClaimType: ClaimType = MissionAllowanceType.valueOf {
    user.affectedMissions.filter(_.missionId.equals(currentMission.id)).head.allowanceType
  }.claimType

  private val claims: List[JClaim] = JClaim.synthesis(cra.userId, cra.year, cra.month, currentMission.id).toList

  private val currentMissionClaims = TreeMap(claims
    .filter(c => ClaimType.valueOf(c.claimType).equals(currentMissionClaimType))
    .groupBy(c => TimeUtils.getMondayOfDate(c.date))
    .map(claim => claim._1 -> claim._2.foldLeft(Zero)((acc, curr) => acc + curr.amount))
    .toArray: _*)


  private val totalCurrentMissionClaims = currentMissionClaims.values.foldLeft(Zero)((acc, curr) => acc + curr)
  private val totalFeeByWeek = {
    (newCell("Semaines") +: currentMissionClaims.map(c => newCell(s"${`dd`.print(c._1)}->${`dd/MM`.print(TimeUtils.getSundayOfDate(c._1))} (S${c._1.getWeekOfWeekyear})")).toList :+ newCell("Total")) ++
      (newCell(currentMissionClaimType.label.capitalize) +: currentMissionClaims.map(c => newCell(toCurrency(c._2))).toList :+ newCell(toCurrency(totalCurrentMissionClaims)))
  }

  private val realClaims = claims
    .filterNot(c => ClaimType.valueOf(c.claimType).equals(ClaimType.FIXED_FEE) || ClaimType.valueOf(c.claimType).equals(ClaimType.ZONE_FEE))
    .filterNot(claim => MissionType.valueOf(JMission.codeAndMissionType(claim.missionId).missionType).equals(MissionType.customer))
    .filter(claim => claim.missionId.equals(currentMission.id))
    .groupBy(c => TimeUtils.getMondayOfDate(c.date))
    .map(claim => claim._1 -> claim._2.foldLeft(Zero)((acc, curr) => acc + curr.amount + curr.kilometerAmount))

  private val totalRealClaims = realClaims.values.foldLeft(Zero)((acc, curr) => acc + curr)

  private val totalByMonth = {
    newCell(currentMissionClaimType.label.capitalize) :: newCell(toCurrency(totalCurrentMissionClaims)) ::
      newCell("Frais réel") :: newCell(toCurrency(totalRealClaims)) ::
      newCell("Total") :: newCell(toCurrency(totalCurrentMissionClaims + totalRealClaims)) ::
      Nil
  }

}