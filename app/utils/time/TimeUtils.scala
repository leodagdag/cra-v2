package utils.time

import org.joda.time.{Interval, DateTimeConstants, DateTime}
import JodaUtils.dateTimeOrdering
import scala.collection.JavaConverters._

/**
 * @author f.patin
 */
object TimeUtils {

	def getMondayOfDate(firstDay: DateTime): DateTime = firstDay.minusDays(firstDay.getDayOfWeek - DateTimeConstants.MONDAY)

	def getSundayOfDate(lastDay: DateTime): DateTime = lastDay.plusDays(DateTimeConstants.SUNDAY - lastDay.getDayOfWeek)

	def isDayOff(day: DateTime) = DaysOff.isDayOff(day)

	def isSaturdayOrSunday(date: DateTime) = DaysOff.isSaturdayOrSunday(date)

	def isSaturday(date: DateTime) = DaysOff.isSaturday(date)

	def isSunday(date: DateTime) = DaysOff.isSunday(date)

	def getEaster(year: Integer): DateTime = DaysOff.getEaster(year)

	def getLastDayOfMonth(year: Integer, month: Integer): Int = new DateTime(year, month, 1, 0, 0).dayOfMonth.withMaximumValue.getDayOfMonth

	def getLastDateOfMonth(year: Integer, month: Integer): DateTime = new DateTime(year, month, 1, 0, 0).dayOfMonth.withMaximumValue

	def getNbDaysOffInMonth(year: Integer, month: Integer) = (1 to getLastDayOfMonth(year, month)).filter(day => DaysOff.isDayOff(new DateTime(year, month, day,0,0))).size

	def getDaysOfMonth(year: Integer, month: Integer, extended: Boolean = false) = {
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
		// TODO remove Java conversion
		result.asJavaCollection

	}
}
