package utils.time

import org.joda.time.{DateTimeConstants, DateTime}


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

	def getLastDayOfMonth(year: Integer, month: Integer): Int = new DateTime(year, month, 1).dayOfMonth.withMaximumValue.getDayOfMonth

	def getLastDateOfMonth(year: Integer, month: Integer): DateTime = new DateTime(year, month, 1).dayOfMonth.withMaximumValue

	def getNbDaysOffInMonth(year: Int, month: Int) = (1 to getLastDayOfMonth(year, month) filter (day => DaysOff.isDayOff(new DateTime(year, month, day)))).size

}
