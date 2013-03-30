package export

import org.bson.types.ObjectId
import org.joda.time.DateTime
import models.JAbsence
import com.itextpdf.text._
import pdf.{PdfPTable, PdfWriter}
import java.io.ByteArrayOutputStream
import utils.business.AbsenceUtils
import com.github.jmkgreen.morphia.Morphia
import leodagdag.play2morphia.MorphiaPlugin

/**
 * @author f.patin
 */
case class PDFAbsence(userId: ObjectId,
                      absenceId: ObjectId,
                      missionId: ObjectId,
                      startDate: DateTime,
                      endDate: DateTime,
                      nbDays: BigDecimal,
                      comment: String,
                      creationDate: DateTime,
                      sentDate: DateTime) extends PDFAbsenceTools {


  def this(abs: JAbsence) =
    this(abs.userId, abs.id, abs.missionId, abs.startDate, AbsenceUtils.getHumanEndDate(abs), abs.nbDays, abs.comment, abs.creationDate, abs.sentDate)


  lazy val header: Element = header(userId, Option(sentDate))

  def body = {
    val table = new PdfPTable(5)
    table.setWidthPercentage(100f)
    table.setHeaderRows(1)
    table.getDefaultCell.setPadding(5f)
    table.getDefaultCell.setPaddingBottom(10f)

    setTableHeader(table)
    setTableRow(table, this)
    setTableFooter(table, this.nbDays)
    table

  }

  def export() = {
    val doc = document()
    val os = new ByteArrayOutputStream()
    PdfWriter.getInstance(doc, os)

    doc.open()
    doc.addCreationDate()
    doc.add(header)
    doc.add(body)

    doc.close()
    os.toByteArray
  }
}

object PDFAbsence {
  def apply(absence: JAbsence) = {
    new PDFAbsence(absence).export()
  }

}
