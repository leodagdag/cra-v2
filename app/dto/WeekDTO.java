package dto;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import models.Day;
import models.Mission;
import org.bson.types.ObjectId;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author f.patin
 */
public class WeekDTO {
	public Integer number;
	public List<DayDTO> days = Lists.newArrayList();

	public WeekDTO() {
	}

	public WeekDTO(final Integer number, final List<Day> days, final ImmutableMap<ObjectId, Mission> missions) {
		this.number = number;
		this.days.addAll(DayDTO.of(days,missions));
	}

	public static List<WeekDTO> of(final List<Day> days, final ImmutableMap<ObjectId, Mission> missions) {
		if (!CollectionUtils.isEmpty(days)) {
			Map<Integer, List<Day>> weeks = Maps.newHashMap();
			for (Day day : days) {
				final int weekOfYear = day.date.getWeekOfWeekyear();
				if (!weeks.containsKey(weekOfYear)) {
					weeks.put(weekOfYear, new ArrayList<Day>());
				}
				weeks.get(weekOfYear).add(day);
			}
			final List<WeekDTO> result = Lists.newArrayListWithCapacity(weeks.keySet().size());
			for (Integer weekNumber : weeks.keySet()) {
				result.add(new WeekDTO(weekNumber, weeks.get(weekNumber),missions));
			}
			return result;
		} else {
			return Lists.newArrayList();
		}
	}
}
