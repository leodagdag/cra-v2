package export

import models.{JDay, JCra}
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.Element
import utils._
import scala.collection.convert.WrapAsJava._
import scala.collection.convert.WrapAsScala._

/**
 * @author f.patin
 */
case class Comment(cra: JCra) extends PDFTableTools with PDFFont {

  lazy val days = JDay.find(cra.id, cra.userId, cra.year, cra.month, false).toList

  def compose() = {

    val table = new PdfPTable(2)
    table.setHorizontalAlignment(Element.ALIGN_LEFT)
    table.setSpacingAfter(10f)
    if(cra.comment != null){
      table.addCell(noBorderCell("Commentaire (mensuel)",boldUnderlineFont, vAlign = Element.ALIGN_TOP))
      table.addCell(noBorderCell(cra.comment, vAlign = Element.ALIGN_TOP))
    }

    days.foreach {
      d =>
        if (d.comment != null) {
          table.addCell(noBorderCell(s"Commentaire du ${`dd/MM/yyyy`.print(d.date)}", boldUnderlineFont))
          table.addCell(noBorderCell(d.comment, vAlign = Element.ALIGN_TOP))
        }
    }
    table
  }
}
