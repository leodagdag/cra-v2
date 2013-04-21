package export

import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text._
import models.{JUser, JMission, JAbsence}
import org.bson.types.ObjectId
import org.joda.time.DateTime
import scala.collection.convert.WrapAsScala._
import scala.collection.convert.WrapAsJava._
import utils._
import scala.List

/**
 * @author f.patin
 */
abstract class PDFAbsence extends PDFComposer[List[JAbsence]] {

  override protected def document(): Document = new Document(PageSize.A4)

  val title: Phrase

  def content(doc: Document, absences: List[JAbsence]) {
    // Header
    doc.add(PDFAbsenceTools.pageHeader(absences.head.userId, title))
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

object PDFAbsence extends PDFAbsence with PDFTools  with PDFFont{
  val title = phrase("Demande de congés",titleFont)
}

object PDFCancelAbsence extends PDFAbsence  with PDFTools  with PDFFont{
  val title = phrase("Annulation de congés",redTitleFont)
}

object PDFAbsenceTools extends PDFTableTools with PDFTools with PDFFont {

  private lazy val missions = JMission.getAbsencesMissions

  def pageHeader(userId: ObjectId, title: Phrase): PdfPTable = {
    val § = new Paragraph()
    val user = JUser.identity(userId)


    §.addAll(
      List(
        phraseln(title),
        blankLine,
        phraseln(phrase("Collaborateur : ", headerFont), phrase(s"${user.fullName()}", headerFontBold)),
        blankLine,
        phraseln(phrase("Généré le : ", headerFont), phrase(s"${`dd/MM/yyyy à HH:mm:ss`.print(DateTime.now)}", headerFontBold)),
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
    table.addCell(footerCell("jour(s) ouvré(s)", Element.ALIGN_LEFT, BOTTOM_RIGHT))
  }


}