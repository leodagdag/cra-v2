package export

import com.itextpdf.text.pdf.{PdfPCell, PdfPTable, PdfWriter}
import java.io.ByteArrayOutputStream
import models.{JUser, JMission, JAbsence}
import com.itextpdf.text._
import org.bson.types.ObjectId
import scala.{Array, List}
import utils.business.AbsenceUtils
import scala.collection.JavaConverters._
/**
 * @author f.patin
 */

trait PDFAbsenceTools extends PDFTools {

  private lazy val missions = JMission.getAbsencesMissions.asScala

  private val title = "Demande de congés"

  private def defaultCell(phrase: Phrase) = {
    val cell = new PdfPCell(phrase)
    cell.setPadding(5f)
    cell.setPaddingBottom(10f)
    cell
  }

  private def headerCell(text: String) = {
    val cell = defaultCell(new Phrase(text, PDFFont.tableHeader))
    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
    cell
  }

  private def bodyCell(text: String, alignment: Alignment) = {
    val cell = defaultCell(new Phrase(text, PDFFont.tableBody))
    cell.setHorizontalAlignment(alignment)
    cell
  }

  private def footerCell(text: String, alignment: Alignment, extendedBorder: Border = BOTTOM, colspan: Int = 1) = {
    val cell = defaultCell(new Phrase(text, PDFFont.tableFooter))
    cell.setHorizontalAlignment(alignment)
    cell.setBorder(extendedBorder)
    cell.setColspan(colspan)
    cell
  }

  override protected def document(): Document = new Document(PageSize.A4)

  protected def header(userId: ObjectId): PdfPTable = {
    val § = new Paragraph()
    val user = JUser.identity(userId)
    §.addAll(
      phraseln(
        phraseln(title, PDFFont.title),
        blankLine,
        phraseln(
          phrase("Collaborateur : ", PDFFont.header),
          phrase(s"${user.fullName()}", PDFFont.headerBold)
        ),
        blankLine
      )
    )
    super.header(§)
  }

  protected def setTableHeader(table: PdfPTable) {
    table.setHeaderRows(1)
    table.addCell(headerCell("Motif"))
    table.addCell(headerCell("Du"))
    table.addCell(headerCell("Au"))
    table.addCell(headerCell("Nb jours ouvrés"))
    table.addCell(headerCell("Commentaire"))
  }

  private def setTableRow(table: PdfPTable, absence: JAbsence) {
    val mission = missions.find(_.id == absence.missionId)
    val description = mission.map(_.label).getOrElse(s"Erreur (id:${absence.missionId.toString})")
    table.addCell(bodyCell(description, Element.ALIGN_LEFT))
    table.addCell(bodyCell(absence.startDate.toString("dd/MM/YYYY"), Element.ALIGN_CENTER))
    table.addCell(bodyCell(AbsenceUtils.getHumanEndDate(absence).toString("dd/MM/YYYY"), Element.ALIGN_CENTER))
    table.addCell(bodyCell(absence.nbDays.toString, Element.ALIGN_CENTER))
    table.addCell(bodyCell(absence.comment, Element.ALIGN_LEFT))
  }

  protected def setTableBody(table: PdfPTable, absences: List[JAbsence]) {
    absences.foreach(setTableRow(table, _))
  }

  protected def setTableFooter(table: PdfPTable, nbDays: BigDecimal) {
    table.addCell(footerCell("Total", Element.ALIGN_RIGHT, BOTTOM_LEFT, 3))
    table.addCell(footerCell(nbDays.toString(), Element.ALIGN_CENTER))
    table.addCell(footerCell("jours(s) ouvré(s)", Element.ALIGN_LEFT, BOTTOM_RIGHT))
  }
}
object PDFAbsence extends PDFAbsenceTools {

  def compose(absences: List[JAbsence]): Array[Byte] = {
    val firstAbsence = absences.head

    val doc = document()
    val os = new ByteArrayOutputStream()
    PdfWriter.getInstance(doc, os)
    doc.open()
    doc.addCreationDate()

    doc.add(header(firstAbsence.userId))
    val body = new PdfPTable(5)
    body.setWidthPercentage(100f)
    setTableHeader(body)
    setTableBody(body, absences)
    val total = absences.foldLeft(Zero)((acc, cur) => acc + cur.nbDays)
    setTableFooter(body, total)
    doc.add(body)

    doc.close()
    os.toByteArray
  }
}
