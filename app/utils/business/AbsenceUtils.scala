package utils.business

import models.JAbsence
import org.joda.time.{Period, LocalTime, DateTime}
import play.libs.F
import scala.collection.JavaConverters._
import utils.time.TimeUtils

/**
 * @author f.patin
 */
object AbsenceUtils {

  def halfDays(start: DateTime, end: DateTime): java.util.List[DateTime] = {
    _halfDays(start, end).asJava
  }

  private def _halfDays(start: DateTime, end: DateTime): List[DateTime] = {
    def add(dt: DateTime, xs: List[DateTime]): List[DateTime] = {
      if (dt.isAfter(end) || dt.isEqual(end)) {
        xs
      } else if (TimeUtils.isDayOffOrWeekEnd(dt)) {
        add(dt.plusHours(12), xs)
      } else {
        add(dt.plusHours(12), xs :+ dt)
      }
    }
    add(start, List.empty[DateTime])
  }

  def nbDaysBetween(start: DateTime, end: DateTime): java.math.BigDecimal = (BigDecimal(_halfDays(start, end).size) / 2).bigDecimal

  def extractDays(start: DateTime, end: DateTime): java.util.Map[DateTime, play.libs.F.Tuple[java.lang.Boolean, java.lang.Boolean]] = {

    def toTuple(xs: List[DateTime]): F.Tuple[java.lang.Boolean, java.lang.Boolean] = {
      xs.size match {
        case 0 => F.Tuple(java.lang.Boolean.TRUE, java.lang.Boolean.FALSE)
        case 2 => F.Tuple(java.lang.Boolean.TRUE, java.lang.Boolean.TRUE)
        case _ => {
          if (xs.head.toLocalTime.isEqual(LocalTime.MIDNIGHT)) {
            F.Tuple(java.lang.Boolean.TRUE, java.lang.Boolean.FALSE)
          } else {
            F.Tuple(java.lang.Boolean.FALSE, java.lang.Boolean.TRUE)
          }
        }
      }
    }
    _halfDays(start, end)
      .groupBy(hd => hd.withTimeAtStartOfDay())
      .map(k => (k._1, toTuple(k._2)))
      .asJava
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


}
