package utils.time

import org.joda.time.{DateTime, DateTimeComparator}
import java.util.Comparator

/**
 * @author f.patin
 */
object JodaUtils {
	implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.comparatorToOrdering(DateTimeComparator.getInstance.asInstanceOf[Comparator[DateTime]])
}
