package export

import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.{Element, Paragraph, PageSize, Document}
import models.{JUser, JMission, JAbsence}
import org.bson.types.ObjectId
import scala.collection.convert.WrapAsJava._
import scala.collection.convert.WrapAsScala._
import utils._

/**
 * @author f.patin
 */
object PDFAbsence extends PDFComposer[List[JAbsence]] {

  override protected def document(): Document = new Document(PageSize.A4)

  def content(doc: Document, absences: List[JAbsence]) {
    // Header
    doc.add(PDFAbsenceTools.pageHeader(absences.head.userId))
    // Body
    val body = new PdfPTable(4)
    body.setWidthPercentage(100f)
    // Body Header
    PDFAbsenceTools.setTableHeader(body)
    // Body Content
    PDFAbsenceTools.setTableBody(body, absences)
    // Body Footer
    val footer = absences.foldLeft(Zero)((acc, cur) => acc + cur.nbDays)
    PDFAbsenceTools.setTableFooter(body, footer)
    doc.add(body)
  }
}


object PDFAbsenceTools extends PDFTableTools with PDFTools with PDFFont {

  private lazy val missions = JMission.getAbsencesMissions

  private val title = "Demande de congés"

  def pageHeader(userId: ObjectId): PdfPTable = {
    val § = new Paragraph()
    val user = JUser.identity(userId)
    §.addAll(
      phraseln(
        phraseln(title, titleFont),
        blankLine,
        phraseln(
          phrase("Collaborateur : ", headerFont), phrase(s"${user.fullName()}", headerFontBold)
        ),
        blankLine
      )
    )
    super.pageHeader(§)
  }

  def setTableHeader(table: PdfPTable) {
    table.setHeaderRows(1)
    table.addCell(headerCell("Motif"))
    table.addCell(headerCell("Période"))
    table.addCell(headerCell("Nb jours ouvrés"))
    table.addCell(headerCell("Commentaire"))
  }

  def setTableRow(table: PdfPTable, absence: JAbsence) {
    val mission = missions.find(_.id == absence.missionId)
    val description = mission.map(_.label).getOrElse(s"Erreur (id:${absence.missionId.toString})")
    table.addCell(bodyCell(description, Element.ALIGN_LEFT))
    table.addCell(bodyCell(absence.label(), Element.ALIGN_CENTER))
    table.addCell(bodyCell(absence.nbDays.toString, Element.ALIGN_CENTER))
    table.addCell(bodyCell(absence.comment, Element.ALIGN_LEFT))
  }

  def setTableBody(table: PdfPTable, absences: List[JAbsence]) {
    absences.foreach(setTableRow(table, _))
  }

  def setTableFooter(table: PdfPTable, nbDays: BigDecimal) {
    table.addCell(footerCell("Total", Element.ALIGN_RIGHT, BOTTOM_LEFT, 2))
    table.addCell(footerCell(nbDays.toString(), Element.ALIGN_CENTER))
    table.addCell(footerCell("jours(s) ouvré(s)", Element.ALIGN_LEFT, BOTTOM_RIGHT))
  }


}