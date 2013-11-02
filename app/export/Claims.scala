package export

import models.{JParameter, JVehicle, JClaim, JMission, JCra}
import java.util.{ArrayList => JArrayList, List => JList, EnumMap => JEnumMap, Map => JMap}
import constants.{MissionType, Zero, ClaimType}
import utils.business.JClaimUtils
import com.itextpdf.text.{Rectangle, Phrase}
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import utils._

import scala.collection.convert.WrapAsScala._
import scala._
import utils.time.TimeUtils
import scala.Some

/**
 * @author f.patin
 */

case class Claims(cra: JCra, mission: Option[JMission] = None) extends PDFTableTools {
  private val claims = JClaim.synthesis(cra.userId, cra.year, cra.month)
  private val _synthesis: JMap[String, JMap[ClaimType, String]] = JClaimUtils.synthesis(cra.year, cra.month, claims)
  private val weeks = _synthesis.keySet()
  private val nbWeeks = weeks.size()

  val title = new Phrase("Frais", headerFontBold)

  def synthesis(): PdfPTable = {

    val table = new PdfPTable(nbWeeks + 1)
    table.setWidthPercentage(100f)
    table.setHeaderRows(1)
    table.setSpacingAfter(10f)
    // Header
    table.addCell(headerCell("Semaine"))
    weeks.foreach {
      w =>
        val content = if (w forall (Character.isDigit(_))) {
          s"${w.toString.substring(0, 4)} - ${w.toString.substring(4)}"
        } else {
          w
        }
        table.addCell(headerCell(s"$content"))
    }
    // Body
    val body: JEnumMap[ClaimType, JList[String]] = new JEnumMap[ClaimType, JList[String]](classOf[ClaimType])
    _synthesis.keySet() foreach {
      week => {
        for (claimKey: (ClaimType, String) <- _synthesis.get(week)) {
          if (!body.containsKey(claimKey._1)) {
            body.put(claimKey._1, new JArrayList[String]())
          }
          body.get(claimKey._1).add {
            if (claimKey._2.contains('/')) claimKey._2.replace('/', '\n')
            else claimKey._2
          }
        }
      }
    }

    body.foreach {
      line =>
        table.addCell(headerCell(line._1.label.capitalize))
        line._2.foreach(amount => table.addCell(bodyCell(amount, CENTER)))
    }

    val result = new PdfPTable(1)
    result.getDefaultCell.setBorder(Rectangle.NO_BORDER)
    result.setWidthPercentage(100f)
    params.foreach(result.addCell(_))
    result.addCell(table)
    result

  }

  def details(): PdfPTable = {
    val table = new PdfPTable(5)
    table.setWidthPercentage(100f)
    table.setHeaderRows(1)
    table.addCell(headerCell("Date"))
    table.addCell(headerCell("Mission"))
    table.addCell(headerCell("Type"))
    table.addCell(headerCell("Montant"))
    table.addCell(headerCell("Commentaire"))


    claims
      .filterNot(c => ClaimType.valueOf(c.claimType).equals(ClaimType.FIXED_FEE) || ClaimType.valueOf(c.claimType).equals(ClaimType.ZONE_FEE))
      .sortBy(c => c.date)
      .foreach {
      c =>
        val claimType = ClaimType.valueOf(c.claimType)
        val claimAmount = if (claimType.equals(ClaimType.JOURNEY)) s"${toCurrency(c.kilometerAmount)} (${toKm(c.kilometer)})"
        else toCurrency(c.amount)
        val label = if (claimType.equals(ClaimType.JOURNEY)) s"${claimType.label.capitalize}\n(${c.journey})"
        else claimType.label.capitalize
        table.addCell(bodyCell(s"${`dd/MM/yyyy`.print(c.date)} (S${c.date.getWeekOfWeekyear})", LEFT))
        table.addCell(bodyCell(JMission.codeAndMissionType(c.missionId).label, LEFT))
        table.addCell(bodyCell(label, LEFT))
        table.addCell(bodyCell(claimAmount, RIGHT))
        table.addCell(bodyCell(c.comment, LEFT))
    }
    table

  }

  def params = {
    val vehicle = JVehicle.fetch(cra.userId, cra.year, cra.month)
    if (vehicle == null) None
    else {
      val date = TimeUtils.lastDateOfMonth(cra.year, cra.month)
      val coeff = JParameter.coefficient(vehicle, date)

      val table = new PdfPTable(5)
      table.setWidthPercentage(100f)
      table.getDefaultCell.setBorder(Rectangle.NO_BORDER)

      table.addCell(headerCell("Véhicule"))
      table.addCell(bodyCell(s"Cylindre / CV fiscaux : ${vehicle.power.toString}", LEFT))
      table.addCell(bodyCell(s"Tarifs kms : ${toEuroByKm(coeff)}", LEFT))
      table.addCell(headerCell("Forfait de zone"))
      table.addCell(bodyCell(toCurrency(JParameter.zoneAmount(date)), CENTER))
      Some(table)
    }
  }

  def filterByCustomerMissionType: ((JMission, BigDecimal)) => Boolean = c => MissionType.valueOf(c._1.missionType).equals(MissionType.customer)

  def totalByCustomerMission(): PdfPTable = {
    val claimsByMission = claims.toList
      .groupBy(_.missionId)
      .map(c => JMission.codeAndMissionType(c._1) -> c._2.foldLeft(Zero)((acc, curr) => acc + curr.amount + curr.kilometerAmount))
    val claimsByCustomerMission = claimsByMission
      .filter(filterByCustomerMissionType)
      .toList
      .sortBy(_._1.code)
    val totalGenesisMission = claimsByMission
      .filterNot(filterByCustomerMissionType)
      .values.foldLeft(Zero)((acc, curr) => acc + curr)
    val totalMonth = claims
      .foldLeft(Zero)((acc, curr) => acc + curr.amount + curr.kilometerAmount)

    val cells: List[PdfPCell] = (claimsByCustomerMission.map(c => headerCell(c._1.code)) :+ headerCell("Genesis") :+ headerCell("Mois")) ++
      (claimsByCustomerMission.map(c => bodyCell(toCurrency(c._2), RIGHT)) :+ bodyCell(toCurrency(totalGenesisMission), RIGHT) :+ bodyCell(toCurrency(totalMonth), RIGHT))

    val table = new PdfPTable(cells.size / 2)
    table.setWidthPercentage(100f)
    table.getDefaultCell.setBorder(Rectangle.NO_BORDER)
    cells.foreach(table.addCell(_))
    table
  }
}


