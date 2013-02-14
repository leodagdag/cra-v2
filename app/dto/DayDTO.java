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

	public DayDTO(final JDay jDay, final ImmutableMap<ObjectId, JMission> jMissions) {
		this.id = jDay.id;
		this.date = jDay.date;
		this.morning = HalfDayDTO.of(jDay.morning,jMissions);
		this.afternoon = HalfDayDTO.of(jDay.afternoon,jMissions);
		this.comment = jDay.comment;
		this.isSaturday = jDay.isSaturday();
		this.isSunday = jDay.isSunday();
		this.isDayOff = jDay.isDayOff();
	}

	public static List<DayDTO> of(final List<JDay> jDays, final ImmutableMap<ObjectId, JMission> jMissions) {
		return Lists.newArrayList(Collections2.transform(jDays, new Function<JDay, DayDTO>() {
			@Nullable
			@Override
			public DayDTO apply(@Nullable final JDay jDay) {
				return new DayDTO(jDay,jMissions);
			}
		}));
	}
}
