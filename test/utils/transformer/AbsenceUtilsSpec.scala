package utils.transformer

import org.joda.time.DateTime
import org.joda.time.DateTimeConstants._
import org.specs2.mutable.Specification

/**
 * @author f.patin
 */
class AbsenceUtilsSpec extends Specification {

	"AbsenceUtils.halfDays" should {
		"31/12/2012->31/12/2012" in {
			val datesOfWeek = AbsenceUtils.halfDays(new DateTime(2012, DECEMBER, 31, 0, 0, 0), new DateTime(2013, JANUARY, 1, 0, 0, 0))
			datesOfWeek.size() must beEqualTo(2)
		}
		"31/12/2012->02/01/2013" in {
			val datesOfWeek = AbsenceUtils.halfDays(new DateTime(2012, DECEMBER, 31, 0, 0, 0), new DateTime(2013, JANUARY, 3, 0, 0, 0))
			datesOfWeek.size() must beEqualTo(4)
		}
		"31/12/2012 afternoon -> 02/01/2013" in {
			val datesOfWeek = AbsenceUtils.halfDays(new DateTime(2012, DECEMBER, 31, 12, 0, 0), new DateTime(2013, JANUARY, 3, 0, 0, 0))
			datesOfWeek.size() must beEqualTo(3)
		}
		"31/12/2012 afternoon -> 02/01/2013 morning" in {
			val datesOfWeek = AbsenceUtils.halfDays(new DateTime(2012, DECEMBER, 31, 12, 0, 0), new DateTime(2013, JANUARY, 2, 12, 0, 0))
			datesOfWeek.size() must beEqualTo(2)
		}
		"01/01/2013->01/01/2013" in {
			val datesOfWeek = AbsenceUtils.halfDays(new DateTime(2013, JANUARY, 1, 0, 0, 0), new DateTime(2013, JANUARY, 2, 0, 0, 0))
			datesOfWeek.size() must beEqualTo(0)
		}
		"01/01/2013->03/01/2013" in {
			val datesOfWeek = AbsenceUtils.halfDays(new DateTime(2013, JANUARY, 1, 0, 0, 0), new DateTime(2013, JANUARY, 4, 0, 0, 0))
			datesOfWeek.size() must beEqualTo(4)
		}
		"01/02/2013->02/02/2013" in {
			val datesOfWeek = AbsenceUtils.halfDays(new DateTime(2013, FEBRUARY, 1, 0, 0, 0), new DateTime(2013, FEBRUARY, 3, 0, 0, 0))
			datesOfWeek.size() must beEqualTo(2)
		}
		"01/02/2013->03/02/2013" in {
			val datesOfWeek = AbsenceUtils.halfDays(new DateTime(2013, FEBRUARY, 1, 0, 0, 0), new DateTime(2013, FEBRUARY, 4, 0, 0, 0))
			datesOfWeek.size() must beEqualTo(2)
		}
		"01/02/2013->04/02/2013" in {
			val datesOfWeek = AbsenceUtils.halfDays(new DateTime(2013, FEBRUARY, 1, 0, 0, 0), new DateTime(2013, FEBRUARY, 5, 0, 0, 0))
			datesOfWeek.size() must beEqualTo(4)
		}
		"04/02/2013->08/02/2013" in {
			val datesOfWeek = AbsenceUtils.halfDays(new DateTime(2013, FEBRUARY, 4, 0, 0, 0), new DateTime(2013, FEBRUARY, 9, 0, 0, 0))
			datesOfWeek.size() must beEqualTo(10)
		}

	}

	"AbsenceUtils.nbDaysBetween" should {
		"26/03/2013->28/03/2013" in {
			val nbDaysBetween = AbsenceUtils.nbDaysBetween(new DateTime(2013, MARCH, 26, 0, 0, 0), new DateTime(2013, MARCH, 29, 0, 0, 0))
			nbDaysBetween must beEqualTo(BigDecimal(3).bigDecimal)
		}
		"26/03/2013->27/03/2013 morning" in {
			val nbDaysBetween = AbsenceUtils.nbDaysBetween(new DateTime(2013, MARCH, 26, 0, 0, 0), new DateTime(2013, MARCH, 27, 12, 0, 0))
			nbDaysBetween must beEqualTo(BigDecimal(1.5).bigDecimal)
		}
		"26/03/2013 afternoon -> 28/03/2013 morning" in {
			val nbDaysBetween = AbsenceUtils.nbDaysBetween(new DateTime(2013, MARCH, 26, 12, 0, 0), new DateTime(2013, MARCH, 28, 12, 0, 0))
			nbDaysBetween must beEqualTo(BigDecimal(2).bigDecimal)
		}
	}
	"AbsenceUtils.days" should {
		"26/03/2013 afternoon -> 28/03/2013 morning" in {
			AbsenceUtils.days(new DateTime(2013, MARCH, 26, 12, 0, 0), new DateTime(2013, MARCH, 28, 12, 0, 0))
			true must beTrue
		}
	}


}
