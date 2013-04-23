package export

import models.{JHalfDay, JDay, JMission, JCra}
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import scala.collection.immutable.{TreeMap, SortedMap, List}
import constants.{MissionTypeColor, MissionType}
import utils.time.TimeUtils
import scala.collection.convert.WrapAsJava._
import scala.collection.convert.WrapAsScala._

/**
 * @author f.patin
 */
case class Total(cra: JCra, mission: Option[JMission] = None) extends PDFTableTools {

  val days = mission match {
    case Some(m) => JDay.find(cra.id, cra.userId, cra.year, cra.month, m.id).toList
    case None => JDay.find(cra.id, cra.userId, cra.year, cra.month, false).toList
  }

  def compose: PdfPTable = {
    val cells: List[PdfPCell] = mission match {
      case Some(m) => {
        noBorderCell(s"Total : ${totalForMission.toString()}", boldUnderlineFont) :: Nil
      }
      case None => {
        totalForEmployee.flatMap {
          t =>
            val missionType: MissionType = MissionType.valueOf(t._1)
            val color = MissionTypeColor.by(missionType)
            val title: PdfPCell = noBorderCell(missionType.label, boldUnderlineFont)
            val value: PdfPCell = noBorderCell(t._2.toString(), frontColor = color.frontColor, backgroundColor = color.backgroundColor)
            title :: value :: Nil
        }.toList :+ noBorderCell("Nb jours ouvrés", boldUnderlineFont) :+ noBorderCell(TimeUtils.nbWorkingDaysInMonth(cra.year, cra.month).toString)
      }
    }
    val table = new PdfPTable(cells.size)
    table.setHorizontalAlignment(LEFT)
    table.setWidthPercentage(100f)
    cells.foreach(table.addCell(_))
    table
  }

  private lazy val totalForMission: BigDecimal = {

    def totalHalfDay(halfDay: JHalfDay) = {
      if (halfDay == null) Zero
      else {
        if (!halfDay.missionIds().contains(mission.get.id)) Zero
        else {
          if (halfDay.isSpecial) ???
          else ZeroPointFive
        }
      }
    }

    def totalDay(day: JDay) = totalHalfDay(day.morning) + totalHalfDay(day.afternoon)

    days.foldLeft(Zero) {
      (acc, day) =>
        if (day.missionIds().contains(mission.get.id)) acc + totalDay(day)
        else acc
    }
  }

  private lazy val totalForEmployee: SortedMap[String, BigDecimal] = {

    val halDays: List[JHalfDay] = days
      .filter(d => d != null && !d.missionIds.isEmpty)
      .flatMap(day => List(day.morning, day.afternoon))
      .filter(hd => hd != null)

    val missionIds = halDays.flatMap(hds => hds.missionIds()).toList

    val missions = JMission.codeAndMissionType(missionIds)

    implicit val ordering: Ordering[MissionType] = Ordering.fromLessThan(_.ordinal > _.ordinal)
    TreeMap(halDays.groupBy(hd => missions.get(hd.missionId).missionType).toList: _*)
      .mapValues(hds => ZeroPointFive * hds.size)

  }
}



