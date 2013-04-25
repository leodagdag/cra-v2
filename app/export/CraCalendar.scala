package export

import models.{JHalfDay, JDay, JMission, JCra}
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import utils.time.TimeUtils
import org.joda.time.Period
import scala.collection.immutable.{List, TreeMap, TreeSet}
import utils._
import com.itextpdf.text.{BaseColor, Rectangle, Font, Element, Phrase}
import constants.{MissionTypeColor, MissionType}
import scala.collection.convert.WrapAsScala._
import scala.collection.convert.WrapAsJava._
import org.bson.types.ObjectId
import play.libs.F

/**
 * @author f.patin
 */
trait Calendar {

  private val baseFont: Font = new Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)
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
    val last = TimeUtils.getSundayOfDate(TimeUtils.lastDateOfMonth(first))
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


  protected def computeTotal(week: List[JDay]): Option[PdfPTable] = {
    val total = week.foldLeft(Zero)((acc, cur) => {
      acc + cur.inGenesisHour()
    })
    val table = newTable(1)
    table.addCell(newCell(total.toString()))
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

  protected def toHalfDay(halfDay: JHalfDay): PdfPCell

  protected def newEmptyHalfDayCell = newCell(dummyContent)

  protected def newCalendarTable(numColumns: Int) = {
    val table = newTable(numColumns)
    table.setWidthPercentage(100f)
    table.getDefaultCell.setPadding(0f)
    table.getDefaultCell.setBorder(Rectangle.BOX)
    table.setSpacingAfter(5f)
    table
  }

  protected def newDayTable(day: JDay): PdfPTable = {
    val table = newTable(1)

    val title = if (day.date.getMonthOfYear != month) newCell(dummyContent)
    else if (TimeUtils.isDayOff(day.date)) newCell(`EEE dd`.print(day.date), Rectangle.BOTTOM, Some(dayOffHeaderColor))
    else if (TimeUtils.isSaturdayOrSunday(day.date)) newCell(`EEE dd`.print(day.date), Rectangle.BOTTOM, Some(saturdayOrSundayHeaderColor))
    else newCell(`EEE dd`.print(day.date), Rectangle.BOTTOM)

    table.addCell(title)
    table
  }

  private def newTable(numColumns: Int): PdfPTable = {
    val table = new PdfPTable(numColumns)
    table.getDefaultCell.setVerticalAlignment(Element.ALIGN_MIDDLE)
    table.getDefaultCell.setHorizontalAlignment(Element.ALIGN_CENTER)
    table.getDefaultCell.setBorder(Rectangle.NO_BORDER)
    table
  }

  protected def newCell(text: String, border: Int = Rectangle.NO_BORDER, colors: Option[F.Tuple[BaseColor, BaseColor]] = None) = {
    val font = new Font(baseFont)
    font.setColor(colors.map(_._1).getOrElse(BaseColor.BLACK))

    val phrase = if (text.length > 10) new Phrase(text.substring(0, 9), font)
    else new Phrase(text, font)

    val cell = new PdfPCell(phrase)
    cell.setBorder(border)
    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
    cell.setBackgroundColor(colors.map(_._2).getOrElse(BaseColor.WHITE))
    cell.setPaddingBottom(5f)
    cell
  }
}

case class EmployeeCalendar(cra: JCra) extends Calendar {

  protected def toHalfDay(halfDay: JHalfDay): PdfPCell = {
    halfDay match {
      case null => newEmptyHalfDayCell
      case hd if hd.isSpecial => ???
      case hd => {
        missions.get(hd.missionId) match {
          case Some(m) => {
            val missionType = MissionType.valueOf(m.missionType)
            val colors = Some(MissionTypeColor.by(missionType).colors)

            if (MissionType.customer.equals(missionType)) newCell(m.code, colors = colors)
            else newCell(m.code, colors = colors)
          }
          case None => ???
        }
      }
    }
  }
}

case class ProductionCalendar(cra: JCra, currentMission: JMission) extends Calendar {

  protected def toHalfDay(halfDay: JHalfDay): PdfPCell = {
    halfDay match {
      case null => newEmptyHalfDayCell
      case hd if hd.isSpecial => ???
      case hd => {
        missions.get(hd.missionId) match {
          case None => ???
          case Some(m) => {
            val missionType: MissionType = MissionType.valueOf(m.missionType)
            val colors = Some(MissionTypeColor.by(missionType).colors)

            if (currentMission.id.equals(hd.missionId))
              newCell(missionType.genesisHour.toPlainString, colors = colors)
            else if (MissionType.customer.equals(missionType))
              newCell("AC", colors = colors)
            else newCell(m.code, colors = colors)
          }
        }
      }
    }
  }
}

case class MissionCalendar(cra: JCra, currentMission: JMission) extends Calendar {

  override protected val table = newCalendarTable(7)

  override protected def computeTotal(week: List[JDay]): Option[PdfPTable] = None

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

