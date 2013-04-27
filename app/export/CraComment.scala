package export

import models.{JMission, JDay, JCra}
import com.itextpdf.text.pdf.PdfPCell
import utils._
import utils.time.TimeUtils
import scala.collection.convert.WrapAsScala._
import org.apache.commons.lang3.StringUtils
import com.itextpdf.text.{Element, Font}

/**
 * @author f.patin
 */
trait CraComment extends TableTools {
  val cra: JCra
  val widths: Array[Float]

  def days: List[JDay]

  def compose() = {
    val table = newTable(2)
    table.setWidths(widths)
    if (cra.comment != null) {
      table.addCell(newCell(s"Commentaire (${`MMMM yyyy`.print(TimeUtils.firstDateOfMonth(cra.year, cra.month)).capitalize})", FontTools.boldUnderlineFont))
      table.addCell(newCell(cra.comment))
    }
    dailyComments.foreach(table.addCell(_))
    table
  }

  val dailyComments: List[PdfPCell] =
    days
      .filter(d => StringUtils.isNotBlank(d.comment))
      .flatMap(d => newCell(s"Commentaire du ${`dd/MM/yyyy`.print(d.date)}", FontTools.boldUnderlineFont) :: newCell(d.comment) :: Nil)

  private def newCell(text: String) = super.newCell(text = text, maxLength = None, hAlign = Element.ALIGN_LEFT)

  private def newCell(text: String, font: Font) = super.newCell(text = text, font = font, maxLength = None, hAlign = Element.ALIGN_LEFT)
}

case class EmployeeComment(cra: JCra) extends CraComment {
  val widths: Array[Float] = new Array[Float](2)
  widths(0) = 20f
  widths(1) = 80f

  def days = JDay.fetch(cra).toList
}

case class ProductionComment(cra: JCra, currentMission: JMission) extends CraComment {
  val widths: Array[Float] = new Array[Float](2)
  widths(0) = 30f
  widths(1) = 70f

  def days: List[JDay] = {
    val a = JDay.fetch(cra, currentMission).toList
    a
  }
}