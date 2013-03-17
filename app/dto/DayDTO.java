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
	public Boolean inPastOrFuture;
	public Boolean isSpecial;

	public DayDTO() {
	}

	public DayDTO(final JDay day, final ImmutableMap<ObjectId, JMission> missions, final Integer year, final Integer month) {
		this.id = day.id;
		this.date = day.date;
		this.morning = HalfDayDTO.of(day.morning, missions);
		this.afternoon = HalfDayDTO.of(day.afternoon, missions);
		this.comment = day.comment;
		this.isSaturday = day.isSaturday();
		this.isSunday = day.isSunday();
		this.isDayOff = day.isDayOff();
		this.inPastOrFuture = day.inPastOrFuture(year, month);
		this.isSpecial = isSpecial(day);
	}


	public static List<DayDTO> of(final List<JDay> days, final ImmutableMap<ObjectId, JMission> missions, final Integer year, final Integer month) {
		return Lists.newArrayList(Collections2.transform(days, new Function<JDay, DayDTO>() {
			@Nullable
			@Override
			public DayDTO apply(@Nullable final JDay jDay) {
				return new DayDTO(jDay, missions, year, month);
			}
		}));
	}

	public static DayDTO of(final JDay day, final ImmutableMap<ObjectId, JMission> missions, final Integer year, final Integer month) {
		return new DayDTO(day, missions, year, month);
	}

	private Boolean isSpecial(final JDay day){
		return (day.morning != null && day.morning.isSpecial()) || (day.afternoon != null && day.afternoon.isSpecial());
	}
}
