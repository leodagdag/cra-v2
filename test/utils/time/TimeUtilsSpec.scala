package utils.time

import org.joda.time.{Period, DateTime}
import org.joda.time.DateTimeConstants._
import org.specs2.mutable.Specification
import utils.transformer.AbsenceUtils

/**
 * @author f.patin
 */
class TimeUtilsSpec extends Specification {

	"TimeUtils.getDaysOfMonth" should {
		"in January 2013" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, JANUARY)
			daysOfMonth.size must beEqualTo(31)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 1, 1, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 1, 31, 0, 0))
		}
		"in January 2013 extended" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, JANUARY, true)
			daysOfMonth.size must beEqualTo(35)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2012, 12, 31, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 2, 3, 0, 0))
		}
		"in February 2013" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, FEBRUARY)
			daysOfMonth.size must beEqualTo(28)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 2, 1, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 2, 28, 0, 0))
		}
		"in February 2013 extended" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, FEBRUARY, true)
			daysOfMonth.size must beEqualTo(35)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 1, 28, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 3, 3, 0, 0))
		}
		"in March 2013" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, MARCH)
			daysOfMonth.size must beEqualTo(31)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 3, 1, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 3, 31, 0, 0))
		}
		"in March 2013 extended" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, MARCH, true)
			daysOfMonth.size must beEqualTo(35)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 2, 25, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 3, 31, 0, 0))
		}
		"in April 2013" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, APRIL)
			daysOfMonth.size must beEqualTo(30)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 4, 1, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 4, 30, 0, 0))
		}
		"in April 2013 extended" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, APRIL, true)
			daysOfMonth.size must beEqualTo(35)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 4, 1, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 5, 5, 0, 0))
		}
		"in May 2013" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, MAY)
			daysOfMonth.size must beEqualTo(31)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 5, 1, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 5, 31, 0, 0))
		}
		"in May 2013 extended" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, MAY, true)
			daysOfMonth.size must beEqualTo(35)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 4, 29, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 6, 2, 0, 0))
		}
		"in June 2013" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, JUNE)
			daysOfMonth.size must beEqualTo(30)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 6, 1, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 6, 30, 0, 0))
		}
		"in June 2013 extended" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, JUNE, true)
			daysOfMonth.size must beEqualTo(35)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 5, 27, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 6, 30, 0, 0))
		}
		"in July 2013" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, JULY)
			daysOfMonth.size must beEqualTo(31)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 7, 1, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 7, 31, 0, 0))
		}
		"in July 2013 extended" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, JULY, true)
			daysOfMonth.size must beEqualTo(35)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 7, 1, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 8, 4, 0, 0))
		}
		"in August 2013" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, AUGUST)
			daysOfMonth.size must beEqualTo(31)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 8, 1, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 8, 31, 0, 0))
		}
		"in August 2013 extended" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, AUGUST, true)
			daysOfMonth.size must beEqualTo(35)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 7, 29, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 9, 1, 0, 0))
		}
		"in September 2013" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, SEPTEMBER)
			daysOfMonth.size must beEqualTo(30)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 9, 1, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 9, 30, 0, 0))
		}
		"in September 2013 extended" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, SEPTEMBER, true)
			daysOfMonth.size must beEqualTo(42)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 8, 26, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 10, 6, 0, 0))
		}
		"in October 2013" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, OCTOBER)
			daysOfMonth.size must beEqualTo(31)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 10, 1, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 10, 31, 0, 0))
		}
		"in October 2013 extended" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, OCTOBER, true)
			daysOfMonth.size must beEqualTo(35)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 9, 30, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 11, 3, 0, 0))
		}
		"in November 2013" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, NOVEMBER)
			daysOfMonth.size must beEqualTo(30)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 11, 1, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 11, 30, 0, 0))
		}
		"in November 2013 extended" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, NOVEMBER, true)
			daysOfMonth.size must beEqualTo(35)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 10, 28, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 12, 1, 0, 0))
		}
		"in December 2013" in {
			val daysOfMonth = TimeUtils.getDaysOfMonth(2013, DECEMBER)
			daysOfMonth.size must beEqualTo(31)
			daysOfMonth.get(0) must beEqualTo(new DateTime(2013, 12, 1, 0, 0))
			daysOfMonth.get(daysOfMonth.size - 1) must beEqualTo(new DateTime(2013, 12, 31, 0, 0))
		}
	}

	"TimeUtils.getNbDaysOffInMonth" should {
		"in January 2013" in {
			val nbDaysOffInMonth = TimeUtils.getNbDaysOffInMonth(2013, JANUARY)
			nbDaysOffInMonth must beEqualTo(1)
		}
		"in February 2013" in {
			val nbDaysOffInMonth = TimeUtils.getNbDaysOffInMonth(2013, FEBRUARY)
			nbDaysOffInMonth must beEqualTo(0)
		}
		"in March 2013" in {
			val nbDaysOffInMonth = TimeUtils.getNbDaysOffInMonth(2013, MARCH)
			nbDaysOffInMonth must beEqualTo(1)
		}
		"in April 2013" in {
			val nbDaysOffInMonth = TimeUtils.getNbDaysOffInMonth(2013, APRIL)
			nbDaysOffInMonth must beEqualTo(1)
		}
		"in May 2013" in {
			val nbDaysOffInMonth = TimeUtils.getNbDaysOffInMonth(2013, MAY)
			nbDaysOffInMonth must beEqualTo(5)
		}
		"in June 2013" in {
			val nbDaysOffInMonth = TimeUtils.getNbDaysOffInMonth(2013, JUNE)
			nbDaysOffInMonth must beEqualTo(0)
		}
		"in July 2013" in {
			val nbDaysOffInMonth = TimeUtils.getNbDaysOffInMonth(2013, JULY)
			nbDaysOffInMonth must beEqualTo(1)
		}
		"in August 2013" in {
			val nbDaysOffInMonth = TimeUtils.getNbDaysOffInMonth(2013, AUGUST)
			nbDaysOffInMonth must beEqualTo(1)
		}
		"in September 2013" in {
			val nbDaysOffInMonth = TimeUtils.getNbDaysOffInMonth(2013, SEPTEMBER)
			nbDaysOffInMonth must beEqualTo(0)
		}
		"in October 2013" in {
			val nbDaysOffInMonth = TimeUtils.getNbDaysOffInMonth(2013, OCTOBER)
			nbDaysOffInMonth must beEqualTo(0)
		}
		"in November 2013" in {
			val nbDaysOffInMonth = TimeUtils.getNbDaysOffInMonth(2013, NOVEMBER)
			nbDaysOffInMonth must beEqualTo(2)
		}
		"in December 2013" in {
			val nbDaysOffInMonth = TimeUtils.getNbDaysOffInMonth(2013, DECEMBER)
			nbDaysOffInMonth must beEqualTo(1)
		}
	}

	"TimeUtils.dateRange" should {
		"31/12/2012->31/12/2012" in {
			val datesOfWeek: Iterator[DateTime] = TimeUtils.dateRange(new DateTime(2012, DECEMBER, 31, 0, 0, 0), new DateTime(2013, JANUARY, 1, 0, 0, 0), Period.days(1))
			datesOfWeek.length must beEqualTo(2)
		}
		"31/12/2012->02/01/2013" in {
			val datesOfWeek = TimeUtils.dateRange(new DateTime(2012, DECEMBER, 31, 0, 0, 0), new DateTime(2013, JANUARY, 2, 0, 0, 0), Period.days(1))
			datesOfWeek.length must beEqualTo(3)
		}
		"31/12/2012->03/01/2013" in {
			val datesOfWeek = TimeUtils.dateRange(new DateTime(2012, DECEMBER, 31, 0, 0, 0), new DateTime(2013, JANUARY, 3, 0, 0, 0), Period.days(1))
			datesOfWeek.length must beEqualTo(4)
		}
		"01/01/2013->01/01/2013" in {
			val datesOfWeek = TimeUtils.dateRange(new DateTime(2013, JANUARY, 1, 0, 0, 0), new DateTime(2013, JANUARY, 1, 0, 0, 0), Period.days(1))
			datesOfWeek.length must beEqualTo(1)
		}
		"01/01/2013->02/01/2013" in {
			val datesOfWeek = TimeUtils.dateRange(new DateTime(2013, JANUARY, 1, 0, 0, 0), new DateTime(2013, JANUARY, 2, 0, 0, 0), Period.days(1))
			datesOfWeek.length must beEqualTo(2)
		}
		"01/01/2013->03/01/2013" in {
			val datesOfWeek = TimeUtils.dateRange(new DateTime(2013, JANUARY, 1, 0, 0, 0), new DateTime(2013, JANUARY, 3, 0, 0, 0), Period.days(1))
			datesOfWeek.length must beEqualTo(3)
		}
		"01/01/2013->04/01/2013" in {
			val datesOfWeek = TimeUtils.dateRange(new DateTime(2013, JANUARY, 1, 0, 0, 0), new DateTime(2013, JANUARY, 4, 0, 0, 0), Period.days(1))
			datesOfWeek.length must beEqualTo(4)
		}
		"01/02/2013->02/02/2013" in {
			val datesOfWeek = TimeUtils.dateRange(new DateTime(2013, FEBRUARY, 1, 0, 0, 0), new DateTime(2013, FEBRUARY, 2, 0, 0, 0), Period.days(1))
			datesOfWeek.length must beEqualTo(2)
		}
		"01/02/2013->03/02/2013" in {
			val datesOfWeek = TimeUtils.dateRange(new DateTime(2013, FEBRUARY, 1, 0, 0, 0), new DateTime(2013, FEBRUARY, 3, 0, 0, 0), Period.days(1))
			datesOfWeek.length must beEqualTo(3)
		}
		"01/02/2013->03/02/2013" in {
			val datesOfWeek = TimeUtils.dateRange(new DateTime(2013, FEBRUARY, 1, 0, 0, 0), new DateTime(2013, FEBRUARY, 3, 0, 0, 0), Period.days(1))
			datesOfWeek.length must beEqualTo(3)
		}
		"01/02/2013->04/02/2013" in {
			val datesOfWeek = TimeUtils.dateRange(new DateTime(2013, FEBRUARY, 1, 0, 0, 0), new DateTime(2013, FEBRUARY, 4, 0, 0, 0), Period.days(1))
			datesOfWeek.length must beEqualTo(4)
		}
		"04/02/2013->08/02/2013" in {
			val datesOfWeek = TimeUtils.dateRange(new DateTime(2013, FEBRUARY, 4, 0, 0, 0), new DateTime(2013, FEBRUARY, 8, 0, 0, 0), Period.days(1))
			datesOfWeek.length must beEqualTo(5)
		}
		"04/02/2013->09/02/2013" in {
			val datesOfWeek = TimeUtils.dateRange(new DateTime(2013, FEBRUARY, 4, 0, 0, 0), new DateTime(2013, FEBRUARY, 9, 0, 0, 0), Period.days(1))
			datesOfWeek.length must beEqualTo(6)
		}
	}
}
