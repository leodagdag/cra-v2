package export

import models.{JClaim, JMission, JCra}
import java.util.{ArrayList => JArrayList, List => JList, EnumMap => JEnumMap, Map => JMap}
import constants.ClaimType
import utils.business.JClaimUtils
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPTable
import utils._

import scala.collection.convert.WrapAsScala._

/**
 * @author f.patin
 */

case class Claims(cra: JCra, mission: Option[JMission] = None) extends PDFTableTools {
  private lazy val claims = JClaim.synthesis(cra.userId, cra.year, cra.month)
  private lazy val _synthesis: JMap[String, JMap[ClaimType, String]] = JClaimUtils.synthesis(cra.year, cra.month, claims)
  private lazy val weeks = _synthesis.keySet()
  private lazy val nbWeeks = weeks.size()

  lazy val title = new Phrase("Frais", headerFontBold)

  def synthesis() = {
    val table = new PdfPTable(nbWeeks + 1)
    table.setWidthPercentage(100f)
    table.setHeaderRows(1)
    table.setSpacingAfter(10f)
    // Header
    table.addCell(headerCell("Semaine"))
    weeks.foreach(w => table.addCell(headerCell(w)))
    // Body
    val body: JEnumMap[ClaimType, JList[String]] = new java.util.EnumMap[ClaimType, JList[String]](classOf[ClaimType])
    _synthesis.keySet() foreach {
      week => {
        for (claimKey: (ClaimType, String) <- _synthesis.get(week)) {
          if (!body.containsKey(claimKey._1)) {
            body.put(claimKey._1, new JArrayList[String]())
          }
          body.get(claimKey._1).add(claimKey._2)
        }
      }
    }

    body.foreach {
      line =>
        table.addCell(headerCell(line._1.label.capitalize))
        line._2.foreach {
          amount =>
            if ("0".equals(amount)) table.addCell(bodyCell(dummyContent, CENTER))
            else table.addCell(bodyCell(toCurrency(BigDecimal(amount)), CENTER))
        }
    }
    table
  }

  def details() = {
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
        table.addCell(bodyCell(`dd/MM/yyyy`.print(c.date), LEFT))
        table.addCell(bodyCell(JMission.codeAndMissionType(c.missionId).label, LEFT))
        table.addCell(bodyCell(ClaimType.valueOf(c.claimType).label.capitalize, LEFT))
        table.addCell(bodyCell(toCurrency(c.amount), RIGHT))
        table.addCell(bodyCell(c.comment, LEFT))
    }
    table

  }
}


