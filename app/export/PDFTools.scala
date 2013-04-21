package export

import com.itextpdf.text._
import com.itextpdf.text.pdf.codec.PngImage
import java.io.ByteArrayOutputStream
import java.net.URL
import pdf.{PdfPCell, PdfPTable, PdfWriter}
import play.api.Play.current
import scala.collection.JavaConverters._

/**
 * @author f.patin
 */


trait PDFTools {

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

  protected def pageHeader(`§`: Paragraph): PdfPTable = {
    val table = new PdfPTable(2)
    table.getDefaultCell.setBorder(Rectangle.NO_BORDER)
    table.setWidthPercentage(100f)
    // Logo
    val img = new PdfPCell(logo)
    img.setRowspan(4)
    img.setBorder(0)
    // Title
    table.addCell(img)
    table.addCell(§)
    table.getDefaultCell.setColspan(2)
    table.addCell(" ")
    table.addCell(" ")
    table
  }
}

trait PDFFont {

  private val baseFont: Font = new Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)

  val normal = baseFont

  val boldFont: Font = {
    val f = new Font(baseFont)
    f.setStyle(Font.BOLD)
    f
  }
  val underlineFont: Font = {
    val f = new Font(baseFont)
    f.setStyle(Font.UNDERLINE)
    f
  }
  val boldUnderlineFont: Font = {
    val f = new Font(baseFont)
    f.setStyle(Font.UNDERLINE + Font.BOLD)
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

  val redTitleFont: Font = {
    val f = new Font(titleFont)
    f.setColor(BaseColor.RED)
    f
  }
  val headerFont: Font = {
    val f = new Font(baseFont)
    f.setSize(12f)
    f
  }
  val headerFontBold: Font = {
    val f = new Font(headerFont)
    f.setStyle(Font.BOLD)
    f
  }
}

trait PDFTableTools extends PDFFont {

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

  protected def colspanCell(colspan: Int) = {
    val cell = new PdfPCell(new Phrase(" "))
    cell.setColspan(colspan)
    cell.setBorder(NO_BORDER)
    cell
  }

  protected def noBorderCell(phrase: String, font: Font = normal, frontColor: BaseColor = BaseColor.BLACK, backgroundColor: BaseColor = BaseColor.WHITE, hAlign: Int = Element.ALIGN_LEFT, vAlign: Int = Element.ALIGN_MIDDLE) = {
    val f = new Font(font)
    f.setColor(frontColor)

    val cell = new PdfPCell(new Phrase(phrase, f))
    cell.setHorizontalAlignment(hAlign)
    cell.setVerticalAlignment(vAlign)
    cell.setBorder(NO_BORDER)
    cell.setBackgroundColor(backgroundColor)
    cell
  }
  protected def noBorderCell(table: PdfPTable) = {
    val cell = new PdfPCell(table)
    cell.setHorizontalAlignment(Element.ALIGN_LEFT)
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE)
    cell.setBorder(NO_BORDER)
    cell
  }

}

trait PDFComposer[T] {

  protected def document(): Document

  def generate(obj: T): Array[Byte] = {
    compose(obj, content)
  }

  def compose(cra: T, f: (Document, T) => Unit): Array[Byte] = {
    val doc = document()
    val os = new ByteArrayOutputStream()
    PdfWriter.getInstance(doc, os)
    doc.open()
    doc.addCreationDate()

    f(doc, cra)

    doc.close()
    os.toByteArray
  }

  def content(doc: Document, obj: T)
}

