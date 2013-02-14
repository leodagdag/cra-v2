package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import models.JDay;
import models.JMission;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import utils.serializer.ObjectIdSerializer;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author f.patin
 */
public class DayDTO {
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;
	public DateTime date;
	public HalfDayDTO morning;
	public HalfDayDTO afternoon;
	public String comment;
	public Boolean isSaturday;
	public Boolean isSunday;
	public Boolean isDayOff;

	public DayDTO() {
	}

	public DayDTO(final JDay JDay, final ImmutableMap<ObjectId, JMission> missions) {
		this.id = JDay.id;
		this.date = JDay.date;
		this.morning = HalfDayDTO.of(JDay.morning,missions);
		this.afternoon = HalfDayDTO.of(JDay.afternoon,missions);
		this.comment = JDay.comment;
		this.isSaturday = JDay.isSaturday();
		this.isSunday = JDay.isSunday();
		this.isDayOff = JDay.isDayOff();
	}

	public static List<DayDTO> of(final List<JDay> JDays, final ImmutableMap<ObjectId, JMission> missions) {
		return Lists.newArrayList(Collections2.transform(JDays, new Function<JDay, DayDTO>() {
			@Nullable
			@Override
			public DayDTO apply(@Nullable final JDay JDay) {
				return new DayDTO(JDay,missions);
			}
		}));
	}
}
