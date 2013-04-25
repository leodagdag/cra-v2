package export

import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import com.itextpdf.text.{Rectangle, Element, Phrase}

/**
 * @author f.patin
 */
object Signature extends PDFTableTools {
  lazy val signatures = {
    val table = new PdfPTable(3)
    table.setWidthPercentage(100f)
    table.getDefaultCell.setHorizontalAlignment(CENTER)
    table.getDefaultCell.setBorder(NO_BORDER)
    table.addCell(customerSignature)
    table.addCell(employeeSignature)
    table.addCell(genesisSignature)
    table
  }

  private val _3colsCell = colspanCell(3)

  private lazy val customerSignature = newCell(new Phrase("Signature Client", boldUnderlineFont))
  private lazy val employeeSignature = newCell(new Phrase("Signature Collaborateur", boldUnderlineFont))
  private lazy val genesisSignature = newCell(new Phrase("Signature GENESIS", boldUnderlineFont))

  private def newCell(phrase: Phrase) = {
    val cell = new PdfPCell(phrase)
    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
    cell.setBorder(Rectangle.NO_BORDER)
    cell
  }
}
