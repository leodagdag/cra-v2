package utils.business

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
			.map {
			k =>
				println(k)
				(k._1, toTuple(k._2))
		}
			.asJava
	}
}
