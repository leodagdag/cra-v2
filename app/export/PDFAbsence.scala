package export

import com.itextpdf.text.pdf.{PdfPTable, PdfWriter}
import java.io.ByteArrayOutputStream
import models.JAbsence

/**
 * @author f.patin
 */
object PDFAbsence extends PDFAbsenceTools {

  def compose(absence: JAbsence): Array[Byte] = {
    compose(List(absence))
  }

  def compose(absences: List[JAbsence]): Array[Byte] = {
    val firstAbsence = absences.head
    val doc = document()
    val os = new ByteArrayOutputStream()
    PdfWriter.getInstance(doc, os)
    doc.open()
    doc.addCreationDate()
    doc.add(header(firstAbsence.userId))

    val body = new PdfPTable(5)
    body.setWidthPercentage(100f)
    setTableHeader(body)
    setTableBody(body, absences)
    val total = absences.foldLeft(Zero)((acc, cur) => acc + cur.nbDays)
    setTableFooter(body, total)
    doc.add(body)

    doc.close()
    os.toByteArray
  }
}
