package utils.time;

import com.google.common.collect.Lists;
import constants.MomentOfDay;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import play.libs.F;

import java.util.List;

/**
 * @author f.patin
 */

public class JTimeUtils {

	public static List<F.Tuple3<DateTime, Boolean, Boolean>> extractDatesInYearMonth(final Integer year, final Integer month, final DateTime startDate, final DateTime endDate, final Integer dayOfWeek, final String momentOfDay, final Integer frequency) {
		final List<F.Tuple3<DateTime, Boolean, Boolean>> result = Lists.newArrayList();
		final F.Tuple<Boolean, Boolean> mod = MomentOfDay.to(momentOfDay);
		final MutableDateTime curr = startDate.toMutableDateTime();
		final DateTime firstDayOfMonth = TimeUtils.firstDateOfMonth(year, month);
		while(curr.isBefore(firstDayOfMonth)) {
			curr.addWeeks(frequency);
		}
		final DateTime lastDayOfMonth = endDate != null ? endDate : TimeUtils.lastDateOfMonth(firstDayOfMonth);
		while(!curr.isAfter(lastDayOfMonth)) {
			if(curr.getDayOfWeek() == dayOfWeek && TimeUtils.isNotDayOff(curr.toDateTime())) {
				result.add(F.Tuple3(curr.toDateTime(), mod._1, mod._2));
			}
			curr.addWeeks(frequency);
		}
		return result;
	}

}
