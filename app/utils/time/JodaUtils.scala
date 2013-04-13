package utils.time

import java.util.Comparator
import org.joda.time.{DateTime, DateTimeComparator}

/**
 * @author f.patin
 */
object JodaUtils {
  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.comparatorToOrdering(DateTimeComparator.getInstance.asInstanceOf[Comparator[DateTime]])
}
