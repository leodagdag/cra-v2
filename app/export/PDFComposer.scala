package export

import com.itextpdf.text.Document
import java.io.ByteArrayOutputStream
import com.itextpdf.text.pdf.PdfWriter

/**
 * @author f.patin
 */
trait PDFComposer[T] {

  protected def document(): Document

  def generate(obj: T): Array[Byte] = {
    compose(obj, content)
  }

  private def compose(cra: T, f: (Document, T) => Unit): Array[Byte] = {
    val doc = document()
    val os = new ByteArrayOutputStream()
    PdfWriter.getInstance(doc, os)
    doc.open()
    doc.addCreationDate()

    f(doc, cra)

    doc.close()
    os.toByteArray
  }

  protected def content(doc: Document, obj: T)
}
