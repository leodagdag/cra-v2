package export

import models.{JHalfDay, JDay, JMission, JCra}
import org.bson.types.ObjectId
import scala.collection.convert.WrapAsScala._
import scala.collection.convert.WrapAsJava._
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import com.itextpdf.text.{BaseColor, Element}
import constants.{MissionTypeColor, MissionType}
import utils.time.TimeUtils
import play.libs.F
import constants._

/**
 * @author f.patin
 */
trait TotalCra extends TableTools {


  val cra: JCra
  protected val days: List[JDay] = JDay.fetch(cra).toList
  protected val missions: Map[ObjectId, JMission] = Map(days.map(day => JMission.codeAndMissionType(day.missionIds().toList)).flatten: _*)


  def compose(): PdfPTable = {
    val cells = total
    val table = newTable(cells.size)
    table.setSpacingAfter(10f)
    cells.foreach(table.addCell(_))
    table
  }

  protected def total: List[PdfPCell]

  def newCell(text: String, hAlign: Int ) = super.newCell(text = text, hAlign = hAlign, maxLength = None)

  def newCell(text: String, colors: F.Tuple[BaseColor, BaseColor] ) = super.newCell(text = text, colors = colors, maxLength = None)
}

case class MissionTotalCra(cra: JCra, currentMission: JMission) extends TotalCra {

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

trait GenesisTotal extends TotalCra {
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
      .map(x => newCell(s"${x._1.label.capitalize}\n${toDay(x._2)}", MissionTypeColor.by(x._1).colors))

    newCell("Total\n(en jour)", Element.ALIGN_LEFT) +: ds :+ newCell(s"Jours ouvrés\n${toDay(nbWorkingDays)}", Element.ALIGN_RIGHT)
  }

  private def totalDay(day: JDay): List[(MissionType, BigDecimal)] = totalHalfDay(day.morning) :: totalHalfDay(day.afternoon) :: Nil

  protected def totalHalfDay(halfDay: JHalfDay): (MissionType, BigDecimal)
}

case class EmployeeTotalCra(cra: JCra) extends GenesisTotal {

  protected def totalHalfDay(halfDay: JHalfDay): (MissionType, BigDecimal) = {
    halfDay match {
      case null => (MissionType.none, ZeroPointFive)
      case hd if hd.isSpecial => ???
      case hd => (MissionType.valueOf(missions.get(hd.missionId).get.missionType), ZeroPointFive)
    }
  }
}

case class ProductionTotalCra(cra: JCra, currentMission: JMission) extends GenesisTotal {

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