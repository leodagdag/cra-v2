package export

import com.itextpdf.text.{PageSize, Document}
import models.JCra
import java.io.ByteArrayOutputStream
import com.itextpdf.text.pdf.PdfWriter

/**
 * @author f.patin
 */

trait PDFCraTools extends PDFTools {

  override protected def document(): Document = new Document(PageSize.A4.rotate())

}

object PDFCra extends PDFCraTools {

  def compose(doc: Document, f: Document => Unit): Array[Byte] = {
    val os = new ByteArrayOutputStream()
    PdfWriter.getInstance(doc, os)
    doc.open()
    doc.addCreationDate()

    f(doc)

    doc.close()
    os.toByteArray
  }


  def content(cra: JCra) {
    val doc = document()
    val os = new ByteArrayOutputStream()
    PdfWriter.getInstance(doc, os)
    doc.open()
    doc.addCreationDate()

    doc.close()
    os.toByteArray
  }
}
