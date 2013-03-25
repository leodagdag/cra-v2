package dto;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import models.JDay;
import models.JMission;
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

	@SuppressWarnings({"unused"})
	public WeekDTO() {
	}

	public WeekDTO(final Integer number, final List<JDay> days, final ImmutableMap<ObjectId, JMission> missions, final Integer year, final Integer month) {
		this.number = number;
		this.days.addAll(DayDTO.of(days, missions, year, month));
	}

	public static List<WeekDTO> of(final List<JDay> days, final ImmutableMap<ObjectId, JMission> missions, final Integer year, final Integer month) {
		if (!CollectionUtils.isEmpty(days)) {
			Map<Integer, List<JDay>> weeks = Maps.newTreeMap();
			for (JDay jDay : days) {
				final int weekOfYear = jDay.date.getWeekOfWeekyear();
				if (!weeks.containsKey(weekOfYear)) {
					weeks.put(weekOfYear, new ArrayList<JDay>());
				}
				weeks.get(weekOfYear).add(jDay);
			}
			final List<WeekDTO> result = Lists.newArrayListWithCapacity(weeks.keySet().size());
			for (Integer weekNumber : weeks.keySet()) {
				result.add(new WeekDTO(weekNumber, weeks.get(weekNumber), missions, year, month));
			}
			return result;
		} else {
			return Lists.newArrayList();
		}
	}
}
