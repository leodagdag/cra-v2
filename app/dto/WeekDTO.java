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

	public WeekDTO() {
	}

	public WeekDTO(final Integer number, final List<JDay> JDays, final ImmutableMap<ObjectId, JMission> missions) {
		this.number = number;
		this.days.addAll(DayDTO.of(JDays,missions));
	}

	public static List<WeekDTO> of(final List<JDay> JDays, final ImmutableMap<ObjectId, JMission> missions) {
		if (!CollectionUtils.isEmpty(JDays)) {
			Map<Integer, List<JDay>> weeks = Maps.newHashMap();
			for (JDay JDay : JDays) {
				final int weekOfYear = JDay.date.getWeekOfWeekyear();
				if (!weeks.containsKey(weekOfYear)) {
					weeks.put(weekOfYear, new ArrayList<JDay>());
				}
				weeks.get(weekOfYear).add(JDay);
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
