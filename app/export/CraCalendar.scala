package export

import models.{JCustomer, JHalfDay, JDay, JMission, JCra}
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import utils.time.TimeUtils
import org.joda.time.Period
import scala.collection.immutable.{List, TreeMap, TreeSet}
import utils._
import com.itextpdf.text.{BaseColor, Rectangle, Font, Element}
import scala.collection.convert.WrapAsScala._
import scala.collection.convert.WrapAsJava._
import org.bson.types.ObjectId
import play.libs.F
import constants._

/**
 * @author f.patin
 */
trait Calendar extends TableTools {


  private val titleFont: Font = new Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)
  private val dayOffHeaderColor = F.Tuple(BaseColor.RED, BaseColor.WHITE)
  private val saturdayOrSundayHeaderColor = F.Tuple(BaseColor.GRAY, BaseColor.WHITE)

  protected val cra: JCra
  protected val table: PdfPTable = newCalendarTable(8)
  protected val year = cra.year
  protected val month = cra.month
  protected val days = completeDays(JDay.fetch(cra).toList).toList
  protected val missions: Map[ObjectId, JMission] = Map(days.map(day => JMission.codeAndMissionType(day.missionIds().toList)).flatten: _*)
  protected val weeks: TreeMap[(Integer, Integer), List[JDay]] = TreeMap(days.groupBy(day => (day.year, day.week)).toSeq: _*)

  private def completeDays(days: List[JDay]) = {
    val first = TimeUtils.getMondayOfDate(TimeUtils.firstDateOfMonth(days.head.date))
    val last = TimeUtils.getSundayOfDate(TimeUtils.lastDateOfMonth(days.head.date))
    TimeUtils.dateRange(first, last, Period.days(1)).map {
      dt =>
        val day = days.find(d => d.date.isEqual(dt))
        day.getOrElse(new JDay(dt))
    }
  }

  def compose(): PdfPTable = {
    val cells = weeks.map {
      week => {
        val weekDays = toDays(week._2)
        computeTotalWeek(week._2) match {
          case None => weekDays
          case Some(t) => weekDays :+ t
        }
      }
    }.flatten
    cells.foreach(table.addCell(_))
    table.completeRow()
    table
  }


  protected def computeTotalWeek(week: List[JDay]): Option[PdfPTable] = {
    val total = week.foldLeft(Zero)((acc, cur) => {
      acc + cur.inGenesisHour()
    })
    val table = newTable(1)
    val weekNumber = week.head.date.getWeekOfWeekyear
    table.addCell(newCell(s"Total (S$weekNumber)", Rectangle.BOTTOM))
    table.addCell(newCell(s"${toHour(total)}"))
    Some(table)
  }

  protected def toDays(days: List[JDay]): List[PdfPTable] = days.sortBy(_.date).map(toDay)

  protected def toDay(day: JDay): PdfPTable = {
    val table = newDayTable(day)
    TreeSet(day.morning, day.afternoon)
      .map(toHalfDay(_))
      .foreach(table.addCell(_))
    table
  }

  protected def title: Option[String] = None

  protected def toHalfDay(halfDay: JHalfDay): PdfPCell

  protected def newEmptyHalfDayCell = newCell(dummyContent)

  protected def newCalendarTable(numColumns: Int) = {
    val table = newTable(numColumns)
    table.getDefaultCell.setPadding(0f)
    table.getDefaultCell.setBorder(Rectangle.BOX)
    table.setSpacingAfter(10f)

    title match {
      case Some(t) => {
        val c = newCell(t, font = titleFont, hAlign = Element.ALIGN_LEFT, maxLength = None)
        c.setColspan(numColumns)
        table.addCell(c)
      }
      case _ =>
    }
    table
  }

  protected def newDayTable(day: JDay): PdfPTable = {
    val table = newTable(1)

    val title = if (day.date.getMonthOfYear != month) newCell(dummyContent)
    else if (TimeUtils.isDayOff(day.date)) newCell(`EEE dd`.print(day.date), Rectangle.BOTTOM, dayOffHeaderColor)
    else if (TimeUtils.isSaturdayOrSunday(day.date)) newCell(`EEE dd`.print(day.date), Rectangle.BOTTOM, saturdayOrSundayHeaderColor)
    else newCell(`EEE dd`.print(day.date), Rectangle.BOTTOM)

    table.addCell(title)
    table
  }
}

case class EmployeeCalendar(cra: JCra) extends Calendar {

  protected def toHalfDay(halfDay: JHalfDay): PdfPCell = {
    halfDay match {
      case null => newEmptyHalfDayCell
      case hd if hd.isSpecial => ???
      case hd => {
        missions.get(hd.missionId) match {
          case None => ???
          case Some(m) => {
            val missionType = MissionType.valueOf(m.missionType)
            val colors = MissionTypeColor.by(missionType).colors

            if (MissionType.customer.equals(missionType)) newCell(m.code, colors = colors)
            else newCell(m.code, colors = colors)
          }

        }
      }
    }
  }

}

case class ProductionCalendar(cra: JCra, currentMission: JMission) extends Calendar {

  override protected def computeTotalWeek(week: List[JDay]): Option[PdfPTable] = {
    val total = week.foldLeft(Zero)((acc, cur) => {
      acc + cur.inGenesisHour()
    })
    val table = newTable(1)
    val weekNumber = week.head.date.getWeekOfWeekyear
    table.addCell(newCell(s"Total (S$weekNumber)", Rectangle.BOTTOM))
    table.addCell(newCell(s"${toHour(total)}"))
    Some(table)
  }

  protected def toHalfDay(halfDay: JHalfDay): PdfPCell = {
    halfDay match {
      case null => newEmptyHalfDayCell
      case hd if hd.isSpecial => ???
      case hd => {
        missions.get(hd.missionId) match {
          case None => ???
          case Some(m) => {
            val missionType: MissionType = MissionType.valueOf(m.missionType)
            val colors = MissionTypeColor.by(missionType.exportMissionType).colors

            if (currentMission.id.equals(hd.missionId)) newCell(missionType.genesisHour.toPlainString, colors = colors)
            else if (MissionType.customer.equals(missionType)) newCell("AC", colors = MissionTypeColor.other_customer.colors)
            else newCell(m.code, colors = colors)
          }
        }
      }
    }
  }

  override protected def title = Some(s"Mission : ${currentMission.code} - Client : ${JCustomer.fetch(currentMission.customerId).name} - Période : ${`MMMM yyyy`.print(TimeUtils.firstDateOfMonth(cra.year, cra.month)).capitalize}")
}

case class MissionCalendar(cra: JCra, currentMission: JMission) extends Calendar {

  override protected val table = newCalendarTable(7)

  override protected def computeTotalWeek(week: List[JDay]): Option[PdfPTable] = None

  protected def toHalfDay(halfDay: JHalfDay): PdfPCell = {
    halfDay match {
      case null => newEmptyHalfDayCell
      case hd if hd.isSpecial => ???
      case hd => {
        if (currentMission.id.equals(hd.missionId)) newCell("0,5")
        else newEmptyHalfDayCell
      }
    }
  }
}

