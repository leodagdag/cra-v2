package utils.time

import JodaUtils.dateTimeOrdering
import java.util.Date
import org.joda.time._
import scala.collection.JavaConverters._
import java.util

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
		add(getFirstDayOfMonth(year, month), List.empty[Integer]).asJava
	}

	def getFirstDayOfMonth(dt: DateTime): DateTime = new DateTime(dt.getYear, dt.getMonthOfYear, 1, 0, 0)

	def getFirstDayOfMonth(year: Integer, month: Integer): DateTime = new DateTime(year, month, 1, 0, 0)

	def getLastDayOfMonth(year: Integer, month: Integer): Int = new DateTime(year, month, 1, 0, 0).dayOfMonth.withMaximumValue.getDayOfMonth

	def getLastDateOfMonth(year: Integer, month: Integer): DateTime = new DateTime(year, month, 1, 0, 0).dayOfMonth.withMaximumValue

	def getLastDateOfMonth(dt: DateTime): DateTime = dt.dayOfMonth.withMaximumValue

	def getNbDaysOffInMonth(year: Integer, month: Integer): Int = (1 to getLastDayOfMonth(year, month)).filter(day => DaysOff.isDayOff(new DateTime(year, month, day, 0, 0))).size

	def getDaysOfMonth(year: Integer, month: Integer, extended: Boolean = false): util.List[DateTime] = {
		val current = dateRange(getFirstDayOfMonth(year, month), getLastDateOfMonth(year, month), Period.days(1))
		val result = extended match {
			case false => current.toList.sorted
			case _ => {
				val firstDay = new DateTime(year, month, 1, 0, 0)
				val lastDay = getLastDateOfMonth(year, month)
				val nbPastDays = new Interval(getMondayOfDate(firstDay), firstDay).toPeriod.getDays
				val nbFutureDays = new Interval(lastDay, getSundayOfDate(lastDay)).toPeriod.getDays
				((1 to nbPastDays).map(i => firstDay.minusDays(i))
					++ current
					++ (1 to nbFutureDays).map(j => lastDay.plusDays(j))
					).sorted
			}
		}
		result.asJava

	}



	def dateRange(from: DateTime, to: DateTime, step: Period): Iterator[DateTime] = Iterator.iterate(from)(_.plus(step)).takeWhile(!_.isAfter(to))

	def toNextDayOfWeek(dt: DateTime, dayOfWeek: Integer): DateTime = {
		(dt.getDayOfWeek - dayOfWeek) match {
			case 0 => dt
			case nb if nb > 0 => dt.minusDays(nb).plusWeeks(1)
			case nb => dt.plusDays(math.abs(nb))
		}
	}
}
