package export

import com.itextpdf.text.pdf.codec.PngImage
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import com.itextpdf.text.{PageSize, Element, Rectangle, Image, Paragraph, Font, Phrase, Document}
import java.net.URL
import models.{JAbsence, JMission, JUser}
import org.bson.types.ObjectId
import play.api.Play.current
import scala.collection.JavaConverters._
import utils.time.TimeUtils
import utils.business.AbsenceUtils

/**
 * @author f.patin
 */
trait PDFTools {

  val Zero = BigDecimal(java.math.BigDecimal.ZERO)

  type Alignment = Int
  type Border = Int
  val BOTTOM_LEFT: Border = Rectangle.BOTTOM + Rectangle.LEFT
  val BOTTOM_RIGHT: Border = Rectangle.BOTTOM + Rectangle.RIGHT
  val BOTTOM: Border = Rectangle.BOTTOM

  def document(): Document

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

  protected def header(title: Paragraph): PdfPTable = {
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

object PDFFont {

  private val base: Font = new Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)

  val bold: Font = {
    val f = new Font(base)
    f.setStyle(Font.BOLD)
    f
  }

  private val tableBase: Font = {
    val f = new Font(base)
    f
  }

  val tableBody: Font = {
    val f = new Font(tableBase)
    f
  }

  val tableHeader: Font = {
    val f = new Font(tableBase)
    f.setStyle(Font.BOLD)
    f.setSize(12f)
    f
  }

  val tableFooter: Font = {
    val f = new Font(tableBase)
    f.setStyle(Font.BOLD)
    f
  }

  val title: Font = {
    val f = new Font(bold)
    f.setSize(15f)
    f
  }
  val header: Font = {
    val f = new Font(base)
    f.setSize(12f)
    f
  }
  val headerBold: Font = {
    val f = new Font(header)
    f.setStyle(Font.BOLD)
    f
  }
}
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

  def document(): Document = new Document(PageSize.A4)

  def header(userId: ObjectId): PdfPTable = {
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

  def setTableHeader(table: PdfPTable) {
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

  def setTableBody(table: PdfPTable, absences: List[JAbsence]) {
    absences.foreach(setTableRow(table, _))
  }

  def setTableFooter(table: PdfPTable, nbDays: BigDecimal) {
    table.addCell(footerCell("Total", Element.ALIGN_RIGHT, BOTTOM_LEFT, 3))
    table.addCell(footerCell(nbDays.toString(), Element.ALIGN_CENTER))
    table.addCell(footerCell("jours(s) ouvré(s)", Element.ALIGN_LEFT, BOTTOM_RIGHT))
  }
}
