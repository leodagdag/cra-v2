package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import models.Day;
import models.Mission;
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
	//import org.codehaus.jackson.map.ext.JodaSerializers;
	public DateTime date;
	public HalfDayDTO morning;
	public HalfDayDTO afternoon;
	public String comment;
	public Boolean isSaturday;
	public Boolean isSunday;
	public Boolean isDayOff;

	public DayDTO() {
	}

	public DayDTO(final Day day, final ImmutableMap<ObjectId, Mission> missions) {
		this.id = day.id;
		this.date = day.date;
		this.morning = HalfDayDTO.of(day.morning,missions);
		this.afternoon = HalfDayDTO.of(day.afternoon,missions);
		this.comment = day.comment;
		this.isSaturday = day.isSaturday();
		this.isSunday = day.isSunday();
		this.isDayOff = day.isDayOff();
	}

	public static List<DayDTO> of(final List<Day> days, final ImmutableMap<ObjectId, Mission> missions) {
		return Lists.newArrayList(Collections2.transform(days, new Function<Day, DayDTO>() {
			@Nullable
			@Override
			public DayDTO apply(@Nullable final Day day) {
				return new DayDTO(day,missions);
			}
		}));
	}
}
