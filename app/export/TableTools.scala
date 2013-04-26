package export

import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import com.itextpdf.text.{Phrase, Font, BaseColor, Rectangle, Element}
import play.libs.F

/**
 * @author f.patin
 */
trait TableTools {
  private val blackOnWhite = F.Tuple(BaseColor.BLACK, BaseColor.WHITE)
  private val baseFont: Font = new Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)

   protected def newTable(numColumns: Int, spaceAfter: Float = 0f) = {
    val table = new PdfPTable(numColumns)
    table.setHorizontalAlignment(Element.ALIGN_LEFT)
    table.getDefaultCell.setBorder(Rectangle.NO_BORDER)
     table.setSpacingAfter(spaceAfter)
    table.setWidthPercentage(100f)
    table
  }

  protected  def newCell(text: String, border: Int = Rectangle.NO_BORDER, colors: F.Tuple[BaseColor, BaseColor] = blackOnWhite, font: Font = baseFont, hAlign: Int = Element.ALIGN_CENTER, maxLength: Option[Int] = Some(12)) = {
    val f = new Font(font)
    f.setColor(colors._1)

    val phrase = maxLength match {
      case Some(x) => {
        if (text.length > x) new Phrase(text.substring(0, (x - 1)), f)
        else new Phrase(text, f)
      }
      case None => new Phrase(text, f)
    }


    val cell = new PdfPCell(phrase)
    cell.setBorder(border)
    cell.setHorizontalAlignment(hAlign)
    cell.setBackgroundColor(colors._2)
    cell.setPaddingBottom(5f)
    cell
  }

}
