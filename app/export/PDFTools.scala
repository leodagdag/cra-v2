package export

import com.itextpdf.text.pdf.codec.PngImage
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import com.itextpdf.text.{Rectangle, Image, Paragraph, Font, Phrase, Document}
import java.net.URL
import play.api.Play.current
import scala.collection.JavaConverters._

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


