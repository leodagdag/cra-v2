package export

import models.{JHalfDay, JDay, JMission, JCra}
import org.bson.types.ObjectId
import scala.collection.convert.WrapAsScala._
import scala.collection.convert.WrapAsJava._
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import com.itextpdf.text.{Font, BaseColor, Rectangle, Element, Phrase}
import play.libs.F
import constants.{MissionTypeColor, MissionType}
import utils.time.TimeUtils

/**
 * @author f.patin
 */
trait Total {
  private val blackOnWhite = F.Tuple(BaseColor.BLACK, BaseColor.WHITE)
  private val baseFont: Font = new Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)

  val cra: JCra
  protected val days: List[JDay] = JDay.fetch(cra).toList
  protected val missions: Map[ObjectId, JMission] = Map(days.map(day => JMission.codeAndMissionType(day.missionIds().toList)).flatten: _*)

  protected def newTable(numColumns: Int) = {
    val table = new PdfPTable(numColumns)
    table.setHorizontalAlignment(Element.ALIGN_LEFT)
    table.getDefaultCell.setBorder(Rectangle.NO_BORDER)
    table.setWidthPercentage(100f)
    table
  }

  protected def newCell(text: String, colors: F.Tuple[BaseColor, BaseColor] = blackOnWhite, hAlign: Int = Element.ALIGN_CENTER) = {
    val font = new Font(baseFont)
    font.setColor(colors._1)
    font.setStyle(Font.BOLD)
    val cell = new PdfPCell(new Phrase(text, font))
    cell.setBackgroundColor(colors._2)
    cell.setBorder(Rectangle.NO_BORDER)
    cell.setHorizontalAlignment(hAlign)
    cell
  }

  def compose(): PdfPTable = {
    val cells = total
    val table = newTable(cells.size)
    cells.foreach(table.addCell(_))
    table
  }

  protected def total: List[PdfPCell]
}

case class MissionTotal(cra: JCra, currentMission: JMission) extends Total {

  private def totalHalfDay(halfDay: JHalfDay) = {
    if (halfDay == null) Zero
    else {
      if (!halfDay.missionIds().contains(currentMission.id)) Zero
      else {
        if (halfDay.isSpecial) ???
        else ZeroPointFive
      }
    }
  }

  private def totalDay(day: JDay): BigDecimal = totalHalfDay(day.morning) + totalHalfDay(day.afternoon)

  protected def total: List[PdfPCell] = {
    val t = days.foldLeft(Zero) {
      (acc, day) =>
        if (day.missionIds().contains(currentMission.id)) acc + totalDay(day)
        else acc
    }
    newCell(s"Total : $t", hAlign = Element.ALIGN_LEFT) :: Nil
  }
}

trait GenesisTotal extends Total {
  val cra: JCra
  private val nbWorkingDays: Int = TimeUtils.nbWorkingDaysInMonth(cra.year, cra.month)

  protected def total: List[PdfPCell] = {
    val ds = days
      .flatMap(totalDay(_))
      .filter(!_._1.equals(MissionType.none))
      .groupBy(_._1)
      .map(x => x._1 -> x._2.size * 0.5)
      .toList
      .sortBy(_._1)
      .map(x => newCell(s"${x._1.label.capitalize} : ${x._2}", MissionTypeColor.by(x._1).colors))

    newCell("Total ", hAlign = Element.ALIGN_LEFT) +: ds :+ newCell(s"Total : $nbWorkingDays", hAlign = Element.ALIGN_RIGHT)
  }

  private def totalDay(day: JDay): List[(MissionType, BigDecimal)] = totalHalfDay(day.morning) :: totalHalfDay(day.afternoon) :: Nil

  protected def totalHalfDay(halfDay: JHalfDay): (MissionType, BigDecimal)
}

case class EmployeeTotal(cra: JCra) extends GenesisTotal {

  protected def totalHalfDay(halfDay: JHalfDay): (MissionType, BigDecimal) = {
    halfDay match {
      case null => (MissionType.none, ZeroPointFive)
      case hd if hd.isSpecial => ???
      case hd => (MissionType.valueOf(missions.get(hd.missionId).get.missionType), ZeroPointFive)
    }
  }
}

case class ProductionTotal(cra: JCra, currentMission: JMission) extends GenesisTotal {

  protected def totalHalfDay(halfDay: JHalfDay): (MissionType, BigDecimal) = {
    halfDay match {
      case null => (MissionType.none, ZeroPointFive)
      case hd if hd.isSpecial => ???
      case hd => {
        val mission = missions.get(hd.missionId).get
        val missionType = MissionType.valueOf(mission.missionType)

        if (currentMission.id.equals(hd.missionId)) MissionType.customer -> ZeroPointFive
        else if (missionType.equals(MissionType.customer)) MissionType.other_customer -> ZeroPointFive
        else missionType -> ZeroPointFive
      }
    }
  }
}