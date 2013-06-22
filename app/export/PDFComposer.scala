package export

import com.itextpdf.text.Document
import java.io.ByteArrayOutputStream
import com.itextpdf.text.pdf.PdfWriter

/**
 * @author f.patin
 */
trait PDFComposer[T] extends BaseReportBuilder {
protected def document(): Document

  def generate(obj: T): Array[Byte] = {
    compose(obj, content)
  }

  private def compose(cra: T, f: (Document, T) => Unit): Array[Byte] = {
    val doc = document()
    val os = new ByteArrayOutputStream()
    val pdfWriter = PdfWriter.getInstance(doc, os)
    pdfWriter.setPageEvent(this)
    doc.open()
    doc.addCreationDate()

    f(doc, cra)

    doc.close()
    os.toByteArray
  }

  def content(doc: Document, obj: T)
}
