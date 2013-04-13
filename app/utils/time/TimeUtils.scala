package utils.time

import JodaUtils.dateTimeOrdering
import java.util
import java.util.Date
import org.joda.time._
import play.libs.F
import play.libs.F.Tuple
import scala.collection.JavaConverters._


/**
 * @author f.patin
 */
object TimeUtils {

  val startDay = LocalTime.fromMillisOfDay(0)
  val endDay = new LocalTime(23, 59, 59)

  def dateTime2Date(dts: java.util.List[DateTime]): java.util.List[Date] = dts.asScala.map(_.toDate).asJava

  def getMondayOfDate(firstDay: DateTime): DateTime = firstDay.minusDays(firstDay.getDayOfWeek - DateTimeConstants.MONDAY)

  def getSundayOfDate(lastDay: DateTime): DateTime = lastDay.plusDays(DateTimeConstants.SUNDAY - lastDay.getDayOfWeek)

  def isDayOffOrWeekEnd(dt: DateTime): java.lang.Boolean = isDayOff(dt) || isSaturdayOrSunday(dt)

  def isNotDayOffAndNotWeekEnd(dt: DateTime): java.lang.Boolean = !isDayOffOrWeekEnd(dt)

  def isWorkingDay(dt: DateTime): java.lang.Boolean = isNotDayOffAndNotWeekEnd(dt)

  def isDayOff(day: DateTime): java.lang.Boolean = DaysOff.isDayOff(day)

  def isNotDayOff(day: DateTime): Boolean = !isDayOff(day)

  def isSaturdayOrSunday(date: DateTime): Boolean = DaysOff.isSaturdayOrSunday(date)

  def isNotSaturdayOrSunday(date: DateTime): Boolean = !isSaturdayOrSunday(date)

  def isSaturday(date: DateTime): Boolean = DaysOff.isSaturday(date)

  def isSunday(date: DateTime): Boolean = DaysOff.isSunday(date)

  def getEaster(year: Integer): DateTime = DaysOff.getEaster(year)

  def getWeeks(year: Integer, month: Integer): java.util.List[Integer] = {
    def add(curr: DateTime, xs: List[Integer]): List[Integer] = {
      if (curr.getMonthOfYear != month) {
        xs
      } else {
        add(curr.plusWeeks(1), curr.getWeekOfWeekyear :: xs)
      }
    }
    add(firstDateOfMonth(year, month), List.empty[Integer]).asJava
  }

  def firstDateOfMonth(dt: DateTime): DateTime = firstDateOfMonth(dt.getYear, dt.getMonthOfYear)

  def firstDateOfMonth(year: Integer, month: Integer): DateTime = new DateTime(year, month, 1, 0, 0).withTimeAtStartOfDay()

  def lastDayOfMonth(year: Integer, month: Integer): Int = firstDateOfMonth(year, month).dayOfMonth.withMaximumValue.getDayOfMonth

  def lastDateOfMonth(year: Integer, month: Integer): DateTime = firstDateOfMonth(year, month).dayOfMonth.withMaximumValue

  def lastDateOfMonth(dt: DateTime): DateTime = dt.dayOfMonth.withMaximumValue

  def nbDaysOffInMonth(year: Integer, month: Integer): Int = (1 to lastDayOfMonth(year, month)).filter(day => DaysOff.isDayOff(new DateTime(year, month, day, 0, 0))).size

  def nbWeekEndDayInMonth(year: Integer, month: Integer) =
    dateRange(firstDateOfMonth(year, month), lastDateOfMonth(year, month), Period.days(1))
      .filter(isSaturdayOrSunday(_))
      .size

  def nbWorkingDaysInMonth(year: Integer, month: Integer): Int =
    dateRange(firstDateOfMonth(year, month), lastDateOfMonth(year, month), Period.days(1))
      .filter(isNotDayOffAndNotWeekEnd(_))
      .size


  def getDaysOfMonth(year: Integer, month: Integer, extended: Boolean = false): util.List[DateTime] = {
    val current = dateRange(firstDateOfMonth(year, month), lastDateOfMonth(year, month), Period.days(1))
    val result = extended match {
      case false => current.toList.sorted
      case true => {
        val firstDay = firstDateOfMonth(year, month)
        val lastDay = lastDateOfMonth(year, month)
        val nbPastDays = new Interval(getMondayOfDate(firstDay), firstDay).toPeriod.getDays
        val nbFutureDays = new Interval(lastDay, getSundayOfDate(lastDay)).toPeriod.getDays
        ((1 to nbPastDays).map(i => firstDay.minusDays(i))
          ++ current
          ++ (1 to nbFutureDays).map(j => lastDay.plusDays(j)))
          .sorted
      }
    }
    result.asJava
  }

  def dateRange(from: DateTime, to: DateTime, step: Period): Iterator[DateTime] = Iterator.iterate(from)(_.plus(step)).takeWhile(!_.isAfter(to))

  def toNextDayOfWeek(dt: DateTime, dayOfWeek: Int): DateTime = {
    (dt.getDayOfWeek - dayOfWeek) match {
      case 0 => dt
      case nb if nb > 0 => dt.minusDays(nb).plusWeeks(1)
      case nb => dt.plusDays(math.abs(nb))
    }
  }

  def toPreviousDayOfWeek(dt: DateTime, dayOfWeek: Int): DateTime = {
    (dt.getDayOfWeek - dayOfWeek) match {
      case 0 => dt
      case nb if nb < 0 => {
        dt.plusDays(math.abs(nb)).minusWeeks(1)
      }
      case nb => {
        dt.minusDays(nb)
      }
    }
  }

  def nextWorkingDay(date: DateTime): DateTime = {
    def add(dt: DateTime): DateTime = {
      if (isDayOffOrWeekEnd(dt)) add(dt.plusDays(1))
      else dt
    }
    add(date.plusDays(0))
  }

  def previousWorkingDay(date: DateTime): DateTime = {
    def subtract(dt: DateTime): DateTime = {
      if (isDayOffOrWeekEnd(dt)) subtract(dt.minusDays(1))
      else dt
    }
    subtract(date.minusDays(0))
  }

  def getMonthYear(dts: java.util.List[DateTime]): util.Collection[Tuple[Integer, Integer]] = {
    val a: Set[(Int, Int)] = dts.asScala.map(dt => (dt.getYear, dt.getMonthOfYear)).toSet
    a.map(k => F.Tuple(int2Integer(k._1), int2Integer(k._2))).asJavaCollection
  }


  def getMonthYear(start: DateTime, end: DateTime): util.Collection[Tuple[Integer, Integer]] = {
    dateRange(firstDateOfMonth(start), lastDateOfMonth(end), Period.months(1))
      .map(dt => F.Tuple(int2Integer(dt.getYear), int2Integer(dt.getMonthOfYear)))
      .toList.asJavaCollection
  }
}
