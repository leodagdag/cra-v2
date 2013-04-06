package export

import models.{JDay, JUser, JCra}
import com.itextpdf.text.{Paragraph, PageSize, Document}
import org.bson.types.ObjectId
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import org.joda.time.{DateTimeConstants, DateTime}
import utils.time.TimeUtils
import scala.collection.JavaConversions._
import collection.immutable.{TreeSet, TreeMap}
import controllers.JDays

/**
 * @author f.patin
 */
object PDFCra extends PDFComposer[JCra] {

  override protected def document(): Document = new Document(PageSize.A4.rotate())

  def content(doc: Document, cra: JCra) {
    // Header
    doc.add(PDFCraTools.pageHeader(cra.userId, cra.year, cra.month))
    // Page
    //doc.add(PDFCraTools.calendar(cra))

  }
}

object PDFCraTools extends PDFTableTools with PDFTools with PDFFont {

  private val title = "Rapport d'Activité"

  def pageHeader(userId: ObjectId, year: Int, month: Int): PdfPTable = {
    val § = new Paragraph()
    val user = JUser.identity(userId)
    §.addAll(
      phraseln(
        phraseln(title, titleFont),
        blankLine,
        phraseln(phrase("Collaborateur : ", headerFont), phrase(s"${user.fullName()}", headerFontBold)),
        blankLine,
        phraseln(phrase("Période : ", headerFont), phrase(new DateTime(year, month, 1, 0, 0).toString("MMMM YYYY").capitalize, headerFontBold)),
        blankLine
      )
    )
    super.pageHeader(§)
  }

/*
  def calendar(cra: JCra, extended: Boolean = true) = {
    val table = new PdfPTable(7)
    table.setWidthPercentage(100f)
    table.getDefaultCell.setBorder(NO_BORDER)
    table.setSpacingBefore(15f)


    val days = JDay.find(cra, true)
    val first = TimeUtils.firstDayOfMonth(cra.year, cra.month)
    val last = TimeUtils.lastDateOfMonth(first)
    implicit val toOrderingDay: Ordering[JDay] = Ordering.fromLessThan(_.date isBefore _.date)
    val weeks = TreeMap(days.groupBy(day => day.week).toList: _*)
    for (dayCell <- weeks.map(week => toWeek(first, last, TreeSet(week._2.sortBy(day => day): _*), extended)).flatten) {
      table.addCell(new PdfPCell(dayCell, daytemplate))
    }
    table
  }

  def toWeek(craFirstDate: DateTime, craLastDate: DateTime, days: TreeSet[JDay], extended: Boolean): Seq[PdfPTable]={
    extended match {
      case true =>
        days.toList.map(day => toDay(craFirstDate, craLastDate, day))
      case false => {
        0.until((days.head.date.getDayOfWeek - DateTimeConstants.MONDAY)).map(i => emptyDay) ++
          days.toList.map(day => toDay(craFirstDate, craLastDate, day))
      }
    }
  }

  def toDay(craFirstDate: DateTime, craLastDate: DateTime, day: models.JDay) = {
    val table = new PdfPTable(1)
    table.setWidthPercentage(100f)

    /*table.addCell(header(day.date))
    table.addCell(morning(day.morning, customer))
    table.addCell(afternoon(day.morning, customer))*/
    table
  }
  lazy val emptyDay = {
    val table = new PdfPTable(1)
    table.setWidthPercentage(100f)
    for (i <- 0 until 3) {
      /*table.addCell(new PdfPCell(new Phrase("")))*/
    }
    table
  }
  val daytemplate = {
    val cell = new PdfPCell()
    /*cell.setHorizontalAlignment(Element.ALIGN_CENTER)*/
    cell.setBorder(0)
    cell
  }
  def header(date: DateTime) = {
    val cell = new PdfPCell()
    cell.setBorder(0)
    cell
  }
*/
  /*
  def morning(halfDay: models.HalfDay, customer: Option[models.Customer])={
    val cell = ExportHalfDay(halfDay, customer)
    cell.setBorderWidthBottom(0)
    cell
  }

  def afternoon(halfDay: models.HalfDay, customer: Option[models.Customer])={
    val cell = ExportHalfDay(halfDay, customer)
    cell.setBorderWidthTop(0)
    cell
  }
  */

}
