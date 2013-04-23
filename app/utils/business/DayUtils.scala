package utils.business

import models.JDay
import utils.time.TimeUtils
import utils._
import org.joda.time.Period

/**
 * @author f.patin
 */
object DayUtils {

  /*def complete(days :List[JDay]): List[JDay] = {
    val ds = days.sortBy(_.date)
    val first = TimeUtils.firstDateOfMonth(ds.head.date)
    val last = TimeUtils.firstDateOfMonth(ds.head.date)
    val allDates = TimeUtils.dateRange(first, last, Period.days(1))
    allDates.map {
      date =>
        if(ds.exists(d => d.date.isEqual(date)))
    }
  }*/
}
