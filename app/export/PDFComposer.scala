package export

import com.itextpdf.text.{PageSize, Phrase, Document}
import java.io.ByteArrayOutputStream
import com.itextpdf.text.pdf.{PdfPTable, PdfWriter}
import models.{JCra, JAbsence}

/**
 * @author f.patin
 */
trait PDFComposer[T] {

  protected def document(): Document

  def apply(obj: T): Array[Byte]={
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

object PDFAbsence extends PDFComposer[List[JAbsence]] with  PDFAbsenceTools {

  override protected def document(): Document = new Document(PageSize.A4)

  def content(doc: Document, absences: List[JAbsence]) {
    // Header
    val firstAbsence = absences.head
    doc.add(pageHeader(firstAbsence.userId))
    // Body
    val body = new PdfPTable(5)
    body.setWidthPercentage(100f)
    // Body Header
    setTableHeader(body)
    // Body Content
    setTableBody(body, absences)
    // Body Footer
    val footer = absences.foldLeft(Zero)((acc, cur) => acc + cur.nbDays)
    setTableFooter(body, footer)
    doc.add(body)
  }
}

object PDFCra extends PDFComposer[JCra] with PDFCraTools {

  override protected def document(): Document = new Document(PageSize.A4.rotate())

  def content(doc: Document, cra: JCra) {
    doc.add(new Phrase(s"Cra ${cra.year} ${cra.month}"))
  }
}
