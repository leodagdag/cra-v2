package utils.business

import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import java.util.{List => JList, Map => JMap}
import models.{JAbsenceDay, JAbsence}
import org.joda.time.{Period, LocalTime, DateTime}
import play.libs.F
import play.libs.F.Tuple
import scala.collection.convert.WrapAsJava
import scala.collection.convert.WrapAsJava._
import scala.collection.convert.WrapAsScala._
import utils.time.TimeUtils
import utils._
/**
 * @author f.patin
 */
object AbsenceUtils {

  private def _extractDays(absence: JAbsence): List[DateTime] = {
    TimeUtils.dateRange(absence.startDate, absence.endDate, Period.days(1)).toList
      .filter(dt => TimeUtils.isNotDayOffAndNotWeekEnd(dt))
  }

  def extractDays(absence: JAbsence): JMap[DateTime, F.Tuple[java.lang.Boolean, java.lang.Boolean]] = {

    def toTuple(dt: DateTime): F.Tuple[java.lang.Boolean, java.lang.Boolean] = {
      if (dt.isEqual(absence.startDate) && !dt.isEqual(absence.endDate)) // First day
        if (absence.startMorning) F.Tuple(TRUE, TRUE)
        else F.Tuple(FALSE, TRUE)
      else if (!dt.isEqual(absence.startDate) && dt.isEqual(absence.endDate)) // Last day
        if (absence.endAfternoon) F.Tuple(TRUE, TRUE)
        else F.Tuple(TRUE, FALSE)
      else F.Tuple(TRUE, TRUE) // Other day
    }

    WrapAsJava.mapAsJavaMap {
      if (absence.startDate.isEqual(absence.endDate)) {
        // Only One Day
        val tuple = if (absence.startMorning && absence.endAfternoon)
          F.Tuple(TRUE, TRUE)
        else if (absence.startMorning)
          F.Tuple(TRUE, FALSE)
        else F.Tuple(FALSE, TRUE)
        Map(absence.startDate -> tuple)
      } else {
        _extractDays(absence)
          .map(dt => (dt, toTuple(dt)))
          .toMap
      }
    }
  }

  def containsOnlyWeekEndOrDayOff(start: DateTime, end: DateTime): java.lang.Boolean = {

    val s: DateTime = start.withTimeAtStartOfDay
    val e: DateTime = end.withTimeAtStartOfDay
    if (s.isEqual(e)) {
      // HalfDay
      TimeUtils.isDayOffOrWeekEnd(start)
    } else if (s.isEqual(e.minusDays(1))) {
      // One Day
      TimeUtils.isDayOffOrWeekEnd(start)
    } else {
      TimeUtils.dateRange(s, e, Period.days(1))
        .forall(dt => TimeUtils.isDayOffOrWeekEnd(dt))
    }
  }

  def getHumanEndDate(absence: JAbsence): DateTime = {
    if (absence.endDate.toLocalTime.isEqual(new LocalTime(12, 0, 0, 0)))
      absence.endDate
    else
      TimeUtils.previousWorkingDay(absence.endDate.minusDays(1))
  }

  def extractAbsenceDays(absence: JAbsence): JList[JAbsenceDay] = {
    extractDays(absence)
      .flatMap {
      (elem: (DateTime, Tuple[java.lang.Boolean, java.lang.Boolean])) =>
        if (elem._2._1 && elem._2._2)
          JAbsenceDay.newMorning(absence, elem._1) :: JAbsenceDay.newAfternoon(absence, elem._1) :: Nil
        else if (elem._2._1)
          JAbsenceDay.newMorning(absence, elem._1) :: Nil
        else if (elem._2._2)
          JAbsenceDay.newAfternoon(absence, elem._1) :: Nil
        else
          Nil
    }
      .toList
  }


  def label(absence: JAbsence) = {
    val start = absence.startDate
    val end = absence.endDate
    val sb = new StringBuilder
    if (start.isEqual(end)) {
      // Same Day
      sb.append(s"le ${`dd/MM/yyyy`.print(start)}")
      if (absence.startMorning && absence.endAfternoon) sb.append("")
      else if (absence.startMorning) sb.append(" matin")
      else sb.append(" après-midi")
    } else {
      sb.append("du ")
        .append(`dd/MM/yyyy`.print(start))
      if (!absence.startMorning) sb.append(" après-midi")
      sb.append(" au ")
        .append(`dd/MM/yyyy`.print(end))
      if (!absence.endAfternoon) sb.append(" matin")
    }
    sb.toString()
  }
}
