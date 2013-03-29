package export

import play.api.Play
import java.net.URL
import com.itextpdf.text._
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import com.itextpdf.text.pdf.codec.PngImage
import models.{JMission, JUser}
import org.bson.types.ObjectId
import org.joda.time.DateTime
import scala.collection.JavaConverters._
import utils.business.AbsenceUtils

/**
 * @author f.patin
 */
trait PDFTools {

  def document(): Document

  def phraseln(phrase: String) = {
    new Phrase(phrase + "\n")
  }

  def phrase(phrase: String) = {
    new Phrase(phrase)
  }

  def phrase(phrase: String, font: Font) = {
    new Phrase(phrase, font)
  }

  def phraseln(p: String, font: Font) = {
    phrase(p + "\n", font)
  }

  def phraseln(p: Phrase*) = {
    val ps = new Phrase()
    ps.addAll((p :+ blankLine).asJavaCollection)
    ps
  }

  val blankLine = new Phrase("\n")

  private val base = new Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)

  val bold: Font = {
    val f = new Font(base)
    f.setStyle(Font.BOLD)
    f
  }
  val tableHeaderFont: Font = {
    val f = new Font(bold)
    f.setSize(12f)
    f
  }

  val headerFont: Font = {
    val f = new Font(base)
    f.setSize(12f)
    f
  }

  val headerFontBold: Font = {
    val f = new Font(headerFont)
    f.setStyle(bold.getStyle)
    f
  }

  val titleFont: Font = {
    val f = new Font(bold)
    f.setSize(15f)
    f
  }

  private lazy val logo: Image = {
    val url: URL = Play.current.resource("/public/images/genesis_logo_export.png").get
    val img = PngImage.getImage(url)
    img.setBorder(0)
    img.scaleAbsolute(248f, 73f)
    img
  }

  protected def header(title: Paragraph) = {
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

trait PDFAbsenceTools extends PDFTools {

  private lazy val missions = JMission.getAbsencesMissions

  private val title = "Demande de congés"

  private def headerCell(text: String) = {
    val cell = new PdfPCell(new Phrase(text, tableHeaderFont))
    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
    cell.setPadding(5f)
    cell.setPaddingBottom(10f)
    cell
  }

  def document(): Document = new Document(PageSize.A4)

  def header(userId: ObjectId, sentDate: Option[DateTime]) = {
    val § = new Paragraph()
    val user = JUser.identity(userId)
    val date = sentDate.map(_.toString("EE dd MMMM YYYY à HH:mm:ss")).getOrElse("")
    §.addAll(
      phraseln(
        phraseln(title, titleFont),
        blankLine,
        phraseln(
          phrase("Collaborateur : ", headerFont),
          phrase(s"${user.lastName.toLowerCase.capitalize} ${user.firstName.toLowerCase.capitalize}", headerFontBold)
        ),
        blankLine,
        phraseln(s"Date d'envoi : $date", headerFont),
        blankLine
      )
    )
    super.header(§)
  }

  def setTableHeader(table: PdfPTable) {
    table.addCell(headerCell("Motif"))
    table.addCell(headerCell("Du"))
    table.addCell(headerCell("Au"))
    table.addCell(headerCell("Nb jours ouvrés"))
    table.addCell(headerCell("Commentaire"))
  }


  def setTableRow(table: PdfPTable, absence: PDFAbsence) {
    table.getDefaultCell.setHorizontalAlignment(Element.ALIGN_LEFT)
    val mission = missions.asScala.find(_.id == absence.missionId)
    val description = mission.map(_.description).getOrElse(s"Erreur (id:${absence.missionId.toString})")
    table.addCell(new Phrase(description))
    table.getDefaultCell.setHorizontalAlignment(Element.ALIGN_CENTER)
    table.addCell(new Phrase(absence.startDate.toString("dd/MM/YYYY")))
    table.addCell(new Phrase(absence.endDate.toString("dd/MM/YYYY")))
    table.addCell(new Phrase(absence.nbDays.toString()))
    table.getDefaultCell.setHorizontalAlignment(Element.ALIGN_LEFT)
    table.addCell(new Phrase(absence.comment))
  }

  def setTableFooter(table: PdfPTable, nbDays: BigDecimal) {
    table.getDefaultCell.setHorizontalAlignment(Element.ALIGN_RIGHT)
    table.getDefaultCell.setBorder(Rectangle.BOTTOM + Rectangle.LEFT)
    table.getDefaultCell.setColspan(3)
    table.addCell("Total")

    table.getDefaultCell.setBorder(Rectangle.BOTTOM)
    table.getDefaultCell.setHorizontalAlignment(Element.ALIGN_CENTER)
    table.getDefaultCell.setColspan(1)
    table.addCell(nbDays.toString())

    table.getDefaultCell.setBorder(Rectangle.BOTTOM + Rectangle.RIGHT)
    table.getDefaultCell.setHorizontalAlignment(Element.ALIGN_LEFT)
    table.addCell("jours(s) ouvré(s)")
  }
}
