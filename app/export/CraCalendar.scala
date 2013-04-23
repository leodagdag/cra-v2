package export

import models.{JHalfDay, JDay, JMission, JCra}
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import utils.time.TimeUtils
import org.joda.time.{DateTimeConstants, DateTime}
import scala.collection.immutable.{TreeMap, List, TreeSet}
import utils._
import scala.Some
import com.itextpdf.text.{Element, BaseColor, Phrase}
import constants.{MissionTypeColor, MissionType}
import scala.collection.convert.WrapAsScala._
import scala.collection.convert.WrapAsJava._
import org.bson.types.ObjectId

/**
 * @author f.patin
 */
case class CraCalendar(cra: JCra, mission: Option[JMission] = None) extends PDFTableTools {

  val days = mission match {
    case Some(m) => JDay.find(cra.id, cra.userId, cra.year, cra.month, m.id).toList
    case None => JDay.find(cra.id, cra.userId, cra.year, cra.month, false).toList
  }

  def compose(): PdfPTable = {
    val table = new PdfPTable(7)
    table.setWidthPercentage(100f)
    table.getDefaultCell.setBorder(NO_BORDER)

    val first = TimeUtils.firstDateOfMonth(cra.year, cra.month)
    val last = TimeUtils.lastDateOfMonth(first)
    val weeks = TreeMap(days.groupBy(day => day.date.getWeekOfWeekyear).toList: _*)

    implicit val toOrderingDay: Ordering[JDay] = Ordering.fromLessThan(_.date isBefore _.date)
    for (dayCell <- weeks.flatMap(week => toWeek(first, last, TreeSet(week._2.sortBy(day => day): _*)))) {
      table.addCell(dayCell)
    }
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
    val text = s"${`EEE dd`.print(day.date)} ${if (TimeUtils.isDayOff(day.date)) ("(Férié)") else ("")}".trim
    cell.addElement(new Phrase(text, boldFont))
    cell
  }

  private def morning(day: JDay) = {
    val cell = halfDay(day.morning)
    cell.setBorder(LEFT_TOP_RIGHT)
    cell
  }

  private def afternoon(day: JDay) = {
    val cell = halfDay(day.afternoon)
    cell.setBorder(RIGHT_BOTTOM_LEFT)
    cell
  }

  private def halfDay(halfDay: JHalfDay): PdfPCell = {
    val cell = halfDayContent(halfDay)
    cell.setHorizontalAlignment(CENTER)
    cell.setBorderColor(BaseColor.GRAY)
    cell
  }

  private def halfDayContent(halfDay: JHalfDay): PdfPCell = {
    if (halfDay != null) {
      if (halfDay.isSpecial) noBorderCell("SPECIAL", normal)
      else {
        mission match {
          case Some(m) => noBorderCell("0,5", normal)
          case None => {
            val mission = JMission.fetch(halfDay.missionId)
            val missionType = MissionType.valueOf(mission.missionType)
            val colors = MissionTypeColor.by(missionType)
            val label = if (MissionType.customer.equals(missionType)) mission.label
            else mission.code
            val hour = if (MissionType.customer.equals(missionType)) halfDay.inGenesisHour().toPlainString
            else dummyContent
            val table = new PdfPTable(2)
            val arr = Array(80f, 20f)
            table.setWidths(arr)
            table.setWidthPercentage(100f)
            if (MissionType.customer.equals(missionType)) {
              table.addCell(noBorderCell(label, frontColor = colors.frontColor, backgroundColor = colors.backgroundColor, hAlign = Element.ALIGN_CENTER))
              table.addCell(noBorderCell(hour))
            } else {
              val cell = noBorderCell(label, frontColor = colors.frontColor, backgroundColor = colors.backgroundColor, hAlign = Element.ALIGN_CENTER)
              cell.setColspan(2)
              table.addCell(cell)
            }
            noBorderCell(table)
          }
        }
      }
    } else noBorderCell(dummyContent)
  }
}


case class ProdCalendar(cra: JCra, mission: JMission) {

  val days = JDay.find(cra.id, cra.userId, cra.year, cra.month, mission.id).toList

  def compose() = {
    implicit val toOrderingDay: Ordering[JDay] = Ordering.fromLessThan(_.date isBefore _.date)

    val table = new PdfPTable(7)
    val weeks = TreeMap(days.groupBy(d => (d.year, d.week)).toList: _*)

    for (week <- weeks; day: JDay <- week._2.sortBy(day => day))
    yield table.addCell(toCell(day))

    table.completeRow()
    table
  }

  def toCell(day: JDay) = {
    val table = new PdfPTable(1)
    table.addCell(new PdfPCell(new Phrase(`EEE dd`.print(day.date))))
    table.addCell(new PdfPCell(new Phrase(day.inGenesisHour().toPlainString)))
    table
  }

}


trait Calendar {

  val cra: JCra

  val table: PdfPTable = new PdfPTable(8)
  val days: List[JDay] = JDay.fetch(cra).toList
  val missions: Map[ObjectId, JMission] = Map(days.map(day => JMission.codeAndMissionType(day.missionIds().toList)).flatten: _*)
  val weeks: TreeMap[(Integer, Integer), List[JDay]] = TreeMap(days.groupBy(day => (day.year, day.week)).toSeq: _*)

  def compose(): PdfPTable = {
    val cells = weeks.map {
      week => {
        val weekDays = toDays(week._2)
        computeTotal(week._2) match {
          case None => weekDays
          case Some(t) => weekDays :+ t
        }
      }
    }.flatten
    cells.foreach(table.addCell(_))
    table.completeRow()
    table
  }

  def computeTotal(week: List[JDay]): Option[PdfPTable] = None

  def toDays(days: List[JDay]): List[PdfPTable] = days.sortBy(_.date).map(toDay)

  def toDay(day: JDay): PdfPTable

  protected def newDayTable(day: JDay) = {
    val table = new PdfPTable(1)
    table.getDefaultCell.setVerticalAlignment(Element.ALIGN_MIDDLE)
    table.getDefaultCell.setHorizontalAlignment(Element.ALIGN_CENTER)
    table.addCell(`EEE dd`.print(day.date))
    table
  }
}

trait GenesisCalendar extends Calendar {
  override val table: PdfPTable = new PdfPTable(8)

  override def computeTotal(week: List[JDay]): Option[PdfPTable] = {
    val total = week.foldLeft(Zero)((acc, cur) => {
      acc + cur.inGenesisHour()
    })
    val table = new PdfPTable(1)
    table.addCell(total.toString())
    Some(table)
  }

}

case class EmployeeCalendar(cra: JCra) extends GenesisCalendar {

  def toDay(day: JDay): PdfPTable = {
    val table = newDayTable(day)
    TreeSet(day.morning, day.afternoon)
      .map(toHalfDay(_))
      .foreach(table.addCell(_))
    table
  }

  def toHalfDay(halfDay: JHalfDay): PdfPCell = {
    halfDay match {
      case null => new PdfPCell(new Phrase(dummyContent))
      case hd if hd.isSpecial => ???
      case hd => {
        val pattern = "%s (3,7)"
        missions.get(hd.missionId) match {
          case Some(m) if MissionType.holiday.equals(MissionType.valueOf(m.missionType)) || MissionType.not_paid.equals(MissionType.valueOf(m.missionType)) => new PdfPCell(new Phrase(m.code))
          case Some(m) => new PdfPCell(new Phrase(pattern.format(m.code)))
          case None => ???
        }
      }
    }
  }
}

case class ProductionCalendar(cra: JCra, mission: JMission) extends GenesisCalendar {

  def toDay(day: JDay): PdfPTable = {
    val table = newDayTable(day)
    TreeSet(day.morning, day.afternoon)
      .map(toHalfDay(_, mission))
      .foreach(table.addCell(_))
    table
  }

  def toHalfDay(halfDay: JHalfDay, currentMission: JMission): PdfPCell = {
    halfDay match {
      case null => new PdfPCell(new Phrase(dummyContent))
      case hd if hd.isSpecial => ???
      case hd => {
        if (currentMission.id.equals(hd.missionId)) new PdfPCell(new Phrase(hd.inGenesisHour().toPlainString))
        else {
          val pattern = "%s (3,7)"
          missions.get(hd.missionId) match {
            case Some(m) if MissionType.customer.equals(MissionType.valueOf(m.missionType)) => new PdfPCell(new Phrase(pattern.format("AC")))
            case Some(m) if MissionType.holiday.equals(MissionType.valueOf(m.missionType)) || MissionType.not_paid.equals(MissionType.valueOf(m.missionType)) => new PdfPCell(new Phrase(m.code))
            case Some(m) => new PdfPCell(new Phrase(pattern.format(m.code)))
            case None => ???
          }
        }
      }
    }
  }

}

case class MissionCalendar(cra: JCra, mission: JMission) extends Calendar {

  def toDay(day: JDay): PdfPTable = {
    val table = newDayTable(day)
    TreeSet(day.morning, day.afternoon)
      .map(toHalfDay(_, mission))
      .foreach(table.addCell(_))
    table
  }

  def toHalfDay(halfDay: JHalfDay, currentMission: JMission): PdfPCell = {
    halfDay match {
      case null => new PdfPCell(new Phrase(dummyContent))
      case hd if hd.isSpecial => ???
      case hd => {
        if (currentMission.id.equals(hd.missionId)) new PdfPCell(new Phrase("0,5"))
        else new PdfPCell(new Phrase(dummyContent))
      }
    }
  }
}

