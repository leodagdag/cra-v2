package dto;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import models.JDay;
import models.JMission;
import org.bson.types.ObjectId;
import org.joda.time.DateTimeConstants;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author f.patin
 */
public class WeekDTO {
	public Integer number;
	public List<DayDTO> days = Lists.newArrayList();

	@SuppressWarnings({"unused"})
	public WeekDTO() {
	}

	public WeekDTO(final Integer number, final List<JDay> days, final Map<ObjectId, JMission> missions, final Integer year, final Integer month) {
		this.number = number;
		this.days.addAll(DayDTO.of(days, missions, year, month));
	}

	public static List<WeekDTO> of(final List<JDay> days, final Map<ObjectId, JMission> missions, final Integer year, final Integer month) {
		if(!CollectionUtils.isEmpty(days)) {
			Map<Integer, List<JDay>> weeks = Maps.newTreeMap();
			for(JDay jDay : days) {
				final int weekOfYear = jDay.date.getWeekOfWeekyear();
				if(!weeks.containsKey(weekOfYear)) {
					weeks.put(weekOfYear, new ArrayList<JDay>());
				}
				weeks.get(weekOfYear).add(jDay);
			}
			final List<WeekDTO> result = Lists.newArrayListWithCapacity(weeks.keySet().size());
			for(Integer weekNumber : weeks.keySet()) {
				result.add(new WeekDTO(weekNumber, weeks.get(weekNumber), missions, year, month));
			}
			if(month == DateTimeConstants.DECEMBER) {
				Collections.sort(result, new Comparator<WeekDTO>() {
					@Override
					public int compare(final WeekDTO w1, final WeekDTO w2) {
						if(w1.number == 1) return 1;
						else if(w2.number == 1) return -1;
						else return w1.number.compareTo(w2.number);
					}
				});
			}

			return result;
		} else {
			return Lists.newArrayList();
		}
	}
}
