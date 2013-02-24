package utils.time

import JodaUtils.dateTimeOrdering
import java.util.Date
import org.joda.time.{Duration, Interval, DateTimeConstants, DateTime}
import scala.collection.JavaConverters._

/**
 * @author f.patin
 */
object TimeUtils {

	def dateTime2Date(dts: java.util.List[DateTime]): java.util.List[Date] = {
		dts.asScala.map(_.toDate).asJava
	}

	def getMondayOfDate(firstDay: DateTime): DateTime = firstDay.minusDays(firstDay.getDayOfWeek - DateTimeConstants.MONDAY)

	def getSundayOfDate(lastDay: DateTime): DateTime = lastDay.plusDays(DateTimeConstants.SUNDAY - lastDay.getDayOfWeek)

	def isDayOff(day: DateTime): java.lang.Boolean = DaysOff.isDayOff(day)

	def isNotDayOff(day: DateTime): Boolean = !isDayOff(day)

	def isSaturdayOrSunday(date: DateTime): Boolean = DaysOff.isSaturdayOrSunday(date)

	def isNotSaturdayOrSunday(date: DateTime): Boolean = !isSaturdayOrSunday(date)

	def isSaturday(date: DateTime): Boolean = DaysOff.isSaturday(date)

	def isSunday(date: DateTime): Boolean = DaysOff.isSunday(date)

	def getEaster(year: Integer): DateTime = DaysOff.getEaster(year)

	def getLastDayOfMonth(year: Integer, month: Integer): Int = new DateTime(year, month, 1, 0, 0).dayOfMonth.withMaximumValue.getDayOfMonth

	def getLastDateOfMonth(year: Integer, month: Integer): DateTime = new DateTime(year, month, 1, 0, 0).dayOfMonth.withMaximumValue

	def getNbDaysOffInMonth(year: Integer, month: Integer): Int = (1 to getLastDayOfMonth(year, month)).filter(day => DaysOff.isDayOff(new DateTime(year, month, day, 0, 0))).size

	def getDaysOfMonth(year: Integer, month: Integer, extended: Boolean = false)  = {
		val firstDay = new DateTime(year, month, 1, 0, 0)
		val current = (1 to getLastDayOfMonth(year, month)).map((day: Int) => new DateTime(year, month, day, 0, 0))
		val result = extended match {
			case false => current.sorted
			case _ => {
				val lastDay = getLastDateOfMonth(year, month)
				val nbPastDays = new Interval(getMondayOfDate(firstDay), firstDay).toPeriod.getDays
				val nbFuturDays = new Interval(lastDay, getSundayOfDate(lastDay)).toPeriod.getDays
				((1 to nbPastDays).map(i => firstDay.minusDays(i)) ++ current ++ (1 to nbFuturDays).map(j => lastDay.plusDays(j))).sorted
			}
		}
		result.asJava

	}

	def datesOfWeek(start: DateTime, end: DateTime, withoutDayOff: Boolean): java.util.List[DateTime] = {
		val duration = new Duration(start, end)
		(0 until duration.toStandardDays.getDays + 1)
			.filter(i => (TimeUtils.isNotSaturdayOrSunday(start.plusDays(i))) && (if (withoutDayOff) TimeUtils.isNotDayOff(start.plusDays(i)) else true))
			.map(i => start.plusDays(i))
			.asJava
	}
}
