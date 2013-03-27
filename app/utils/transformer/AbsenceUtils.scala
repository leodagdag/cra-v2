package utils.transformer

import org.joda.time.{LocalTime, DateTime}
import utils.time.TimeUtils
import scala.collection.JavaConverters._
import play.libs.F

/**
 * @author f.patin
 */
object AbsenceUtils {

	def halfDays(start: DateTime, end: DateTime): java.util.List[DateTime] = {
		_halfDays(start, end).asJava
	}

	def _halfDays(start: DateTime, end: DateTime): List[DateTime] = {
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

	def nbDaysBetween(start: DateTime, end: DateTime): java.math.BigDecimal = {
		val a = halfDays(start, end)
		(BigDecimal(a.size()) / 2).bigDecimal
	}

	def toDays(start: DateTime, end: DateTime): java.util.Map[DateTime, F.Tuple[java.lang.Boolean, java.lang.Boolean]] = {

		def toTuple(xs: List[DateTime]): F.Tuple[java.lang.Boolean, java.lang.Boolean] = {
			xs.size match {
				case 0 => F.Tuple(false, false)
				case 2 => F.Tuple(true, true)
				case 1 => {
					if (xs.head.toLocalTime.isEqual(LocalTime.MIDNIGHT)) {
						F.Tuple(true, false)
					} else {
						F.Tuple(false, true)
					}
				}
			}
		}
		val hds: List[DateTime] = _halfDays(start, end)
		val a: Map[DateTime, List[DateTime]] = hds.groupBy(hd => hd.withTimeAtStartOfDay())
		val b = a.map(k => (k._1, toTuple(k._2)))
		b.asJava
	}
}
