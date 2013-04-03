package export

import com.itextpdf.text.pdf.codec.PngImage
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import com.itextpdf.text._
import java.net.URL
import play.api.Play.current
import scala.collection.JavaConverters._
import models.{JAbsence, JUser, JMission}
import scala.List
import org.bson.types.ObjectId
import utils.business.AbsenceUtils

/**
 * @author f.patin
 */


trait PDFTools {





  protected def document(): Document

  def phraseln(phrase: String): Phrase = {
    new Phrase(phrase + "\n")
  }

  def phrase(phrase: String): Phrase = {
    new Phrase(phrase)
  }

  def phrase(phrase: String, font: Font): Phrase = {
    new Phrase(phrase, font)
  }

  def phraseln(p: String, font: Font): Phrase = {
    phrase(p + "\n", font)
  }

  def phraseln(p: Phrase*): Phrase = {
    val ps = new Phrase()
    ps.addAll((p :+ blankLine).asJavaCollection)
    ps
  }

  val blankLine: Phrase = new Phrase("\n")

  private lazy val logo: Image = {
    val url: URL = current.resource("/public/images/genesis_logo_export.png").get
    val img = PngImage.getImage(url)
    img.setBorder(0)
    img.scaleAbsolute(248f, 73f)
    img
  }

  protected def pageHeader(title: Paragraph): PdfPTable = {
    val table = new PdfPTable(2)
    table.getDefaultCell.setBorder(Rectangle.NO_BORDER)
    table.setWidthPercentage(100f)
    // Logo
    val img = new PdfPCell(logo)
    img.setRowspan(4)
    img.setBorder(0)
    // Title
    table.addCell(img)
    table.addCell(title)
    table.getDefaultCell.setColspan(2)
    table.addCell(" ")
    table.addCell(" ")
    table
  }
}

trait PDFFont {

  private val baseFont: Font = new Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)

  val boldFont: Font = {
    val f = new Font(baseFont)
    f.setStyle(Font.BOLD)
    f
  }

  private val tableBaseFont: Font = {
    val f = new Font(baseFont)
    f
  }

  val tableBodyFont: Font = {
    val f = new Font(tableBaseFont)
    f
  }

  val tableHeaderFont: Font = {
    val f = new Font(tableBaseFont)
    f.setStyle(Font.BOLD)
    f.setSize(12f)
    f
  }

  val tableFooterFont: Font = {
    val f = new Font(tableBaseFont)
    f.setStyle(Font.BOLD)
    f
  }

  val titleFont: Font = {
    val f = new Font(boldFont)
    f.setSize(15f)
    f
  }
  val headerFont: Font = {
    val f = new Font(boldFont)
    f.setSize(12f)
    f
  }
  val headerFontBold: Font = {
    val f = new Font(headerFont)
    f.setStyle(Font.BOLD)
    f
  }
}

trait PDFTableTools extends  PDFFont {

  protected def defaultCell(phrase: Phrase) = {
    val cell = new PdfPCell(phrase)
    cell.setPadding(5f)
    cell.setPaddingBottom(10f)
    cell
  }

  protected def headerCell(text: String) = {
    val cell = defaultCell(new Phrase(text, tableHeaderFont))
    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
    cell
  }

  protected def bodyCell(text: String, alignment: Alignment) = {
    val cell = defaultCell(new Phrase(text, tableBodyFont))
    cell.setHorizontalAlignment(alignment)
    cell
  }

  protected def footerCell(text: String, alignment: Alignment, extendedBorder: Border = BOTTOM, colspan: Int = 1) = {
    val cell = defaultCell(new Phrase(text, tableFooterFont))
    cell.setHorizontalAlignment(alignment)
    cell.setBorder(extendedBorder)
    cell.setColspan(colspan)
    cell
  }
}
trait PDFAbsenceTools extends PDFTableTools with PDFTools with PDFFont {

  private lazy val missions = JMission.getAbsencesMissions.asScala

  private val title = "Demande de congés"

  protected def pageHeader(userId: ObjectId): PdfPTable = {
    val § = new Paragraph()
    val user = JUser.identity(userId)
    §.addAll(
      phraseln(
        phraseln(title, titleFont),
        blankLine,
        phraseln(
          phrase("Collaborateur : ", headerFont),
          phrase(s"${user.fullName()}", headerFontBold)
        ),
        blankLine
      )
    )
    super.pageHeader(§)
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

trait PDFCraTools extends PDFTools {



}
