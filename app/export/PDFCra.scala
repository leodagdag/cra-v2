package export


import org.bson.types.ObjectId
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import org.joda.time.{DateTimeConstants, DateTime}
import utils.time.TimeUtils
import scala.collection.JavaConversions._
import collection.immutable.{TreeSet, TreeMap}
import models._
import com.itextpdf.text._
import scala.Some
import scala.List
import scala.collection.JavaConverters._
import com.google.common.collect.ImmutableList
import java.util

/**
 * @author f.patin
 */

abstract class PDFCra[T]() extends PDFComposer[T] {

  override protected def document(): Document = new Document(PageSize.A4.rotate())

}

object PDFEmployeeCra extends PDFCra[JCra] {

  def content(doc: Document, cra: JCra) {
    // Header
    doc.add(PDFCraTools.pageHeader(cra.userId, cra.year, cra.month))
    // Page
    doc.add(PDFCraTools.calendar(cra))
    //doc.add(PDFCraTools.blankLine)

  }
}

object PDFMissionCra extends PDFCra[(JCra, JMission)] {

  def content(doc: Document, obj: (JCra, JMission)) {
    val cra = obj._1
    val mission = obj._2
    // Header
    doc.add(PDFCraTools.pageHeader(cra.userId, cra.year, cra.month, Some(mission)))
    // Page
    doc.add(PDFCraTools.calendar(cra, Some(mission)))
    doc.add(PDFCraTools.blankLine)
    doc.add(Signature.signatures)
  }

}

object PDFCraTools extends PDFTableTools with PDFTools with PDFFont {

  private val title = "Rapport d'Activité"

  def pageHeader(userId: ObjectId, year: Int, month: Int, mission: Option[JMission] = None): PdfPTable = {
    val § = new Paragraph()
    val user = JUser.identity(userId)

    §.addAll(
      List(
        phraseln(title, titleFont),
        blankLine,
        phraseln(phrase("Collaborateur : ", headerFont), phrase(s"${user.fullName()}", headerFontBold)),
        blankLine,
        phraseln(phrase("Période : ", headerFont), phrase(new DateTime(year, month, 1, 0, 0).toString("MMMM YYYY").capitalize, headerFontBold)),
        blankLine,
        mission.map(m => phraseln(phrase("Mission : ", headerFont), phrase(s"${m.label}", headerFontBold))).getOrElse(new Phrase(""))
      )
    )
    super.pageHeader(§)
  }

  def calendar(cra: JCra, mission: Option[JMission] = None): PdfPTable = Calendar(cra, mission).compose


  lazy val signatures = Signature.signatures

}

case class Calendar(cra: JCra, mission: Option[JMission] = None) extends PDFTableTools {

  val days = mission match {
    case Some(m) => JDay.find(cra.id, cra.userId, cra.year, cra.month, m.id)
    case None => JDay.find(cra.id, cra.userId, cra.year, cra.month, false)
  }

  private val total = {
    val cells = mission match {
      case Some(m) => {
        val label = new Phrase("Total : ", tableHeaderFont)
        val total = new Phrase(totalForMission.toString(), tableHeaderFont)
        List(noBorderCell(label), noBorderCell(total))
      }
      case None => {
        List(defaultCell(new Phrase("test")))
      }
    }
    val table = new PdfPTable(cells.size)
    table.getDefaultCell.setBorder(NO_BORDER)
    cells.foreach(table.addCell(_))

    table
  }

  private lazy val totalForMission: BigDecimal = {

    def totalHalfDay(halfDay: JHalfDay): BigDecimal = {
      if (halfDay == null) Zero
      else {
        if (halfDay.missionIds().contains(mission.get.id)) {
          if (halfDay.isSpecial) ???
          else BigDecimal("0.5")
        } else Zero
      }
    }

    def totalDay(day: JDay) = totalHalfDay(day.morning) + totalHalfDay(day.afternoon)

    days.foldLeft(Zero) {
      (acc, day) =>
        if (day.missionIds().contains(mission.get.id)) acc + totalDay(day)
        else acc
    }
  }

  private lazy val totalForEmployee = {

    val halDays = days.asScala
      .map(day => List(day.morning, day.afternoon))
      .toList
      .flatten
      .filter(hd => hd.missionId != null)
    val missionIds: util.Collection[ObjectId] = halDays.map(_.missionIds()).toList.flatten.asJava

    val missions = JMission.codeAndMissionType(new ImmutableList.Builder[ObjectId]().addAll(missionIds).build)
    val c: Map[String, BigDecimal] = halDays
      .groupBy(hd => missions.get(hd.missionId).missionType)
      .mapValues(hds => ZeroPointFive * hds.size)
  }

  def compose(): PdfPTable = {
    val table = new PdfPTable(7)
    table.setWidthPercentage(100f)
    table.getDefaultCell.setBorder(NO_BORDER)

    val first = TimeUtils.firstDayOfMonth(cra.year, cra.month)
    val last = TimeUtils.lastDateOfMonth(first)
    implicit val toOrderingDay: Ordering[JDay] = Ordering.fromLessThan(_.date isBefore _.date)
    val weeks = TreeMap(days.groupBy(day => day.date.getWeekOfWeekyear).toList: _*)

    for (dayCell <- weeks.flatMap(week => toWeek(first, last, TreeSet(week._2.sortBy(day => day): _*)))) {
      table.addCell(dayCell)
    }
    table.completeRow()
    table.addCell(total)
    table.completeRow()
    table
  }

  private def toWeek(craFirstDate: DateTime, craLastDate: DateTime, days: TreeSet[JDay]): Seq[PdfPTable] = {
    val daysBeforeCra = 0.until((days.head.date.getDayOfWeek - DateTimeConstants.MONDAY)).map(i => emptyDay)
    val daysOfCra = days.toList.map(day => toDay(day))
    daysBeforeCra ++ daysOfCra
  }

  private def toDay(day: JDay) = {
    val table = new PdfPTable(1)
    table.setWidthPercentage(100f)
    table.addCell(dayHeader(day))
    table.addCell(morning(day))
    table.addCell(afternoon(day))
    table
  }

  private lazy val emptyDay = {
    val table = new PdfPTable(1)
    table.setWidthPercentage(100f)
    table
  }

  private def dayHeader(day: JDay) = {
    val cell = new PdfPCell()
    cell.setHorizontalAlignment(CENTER)
    cell.setBorder(NO_BORDER)
    cell.addElement(new Phrase(day.date.toString("EEE dd"), boldFont))
    cell
  }

  private def morning(day: JDay) = {
    val cell = toHalfDay(day.morning)
    cell.setBorder(LEFT_TOP_RIGHT)
    cell
  }

  private def afternoon(day: JDay) = {
    val cell = toHalfDay(day.afternoon)
    cell.setBorder(RIGHT_BOTTOM_LEFT)
    cell
  }

  private def toHalfDay(halfDay: JHalfDay) = {
    val cell = new PdfPCell(new Phrase(halfDayContent(halfDay), normal))
    cell.setHorizontalAlignment(CENTER)
    cell.setBorderColor(BaseColor.GRAY)
    cell
  }

  private def halfDayContent(halfDay: JHalfDay) = {
    if (halfDay != null) {
      if (halfDay.isSpecial) "SPECIAL"
      else {
        mission match {
          case Some(m) => "0,5"
          case None => JMission.fetch(halfDay.missionId).label
        }
      }
    } else " "
  }


}

private object Signature extends PDFTableTools with PDFFont {
  lazy val signatures = {
    val table = new PdfPTable(3)
    table.setWidthPercentage(100f)
    table.getDefaultCell.setHorizontalAlignment(CENTER)
    table.getDefaultCell.setBorder(NO_BORDER)
    table.addCell(customerSignature)
    table.addCell(employeeSignature)
    table.addCell(genesisSignature)
    (0 to 2).foreach(i => table.addCell(_3colsCell))
    table
  }

  private val _3colsCell = colspanCell(3)


  private lazy val customerSignature = newCell(new Phrase("Signature Client", boldUnderlineFont), CENTER)
  private lazy val employeeSignature = newCell(new Phrase("Signature Collaborateur", boldUnderlineFont), CENTER)
  private lazy val genesisSignature = newCell(new Phrase("Signature GENESIS", boldUnderlineFont), CENTER)

  private def newCell(phrase: Phrase, alignment: Alignment = LEFT, border: Border = NO_BORDER) = {
    val cell = new PdfPCell(phrase)
    cell.setHorizontalAlignment(alignment)
    cell.setBorder(border)
    cell
  }


}
