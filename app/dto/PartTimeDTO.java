package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import models.JPartTime;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import utils.serializer.ObjectIdSerializer;

import javax.annotation.Nullable;

/**
 * @author f.patin
 */
public class PartTimeDTO {

    @JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;
	public ObjectId userId;
	public Long startDate;
	public Long endDate;
	public Integer dayOfWeek;
	public String momentOfDay;
	public Integer frequency;
	public Boolean active;

	@SuppressWarnings({"unused"})
	public PartTimeDTO() {
	}

	public PartTimeDTO(final JPartTime partTime) {
		this.id = partTime.id;
		this.userId = partTime.userId;
		this.startDate = partTime.startDate.getMillis();
		this.endDate = partTime.endDate != null ? partTime.endDate.getMillis() : null;
		this.dayOfWeek = partTime.dayOfWeek;
		this.momentOfDay = partTime.momentOfDay;
		this.frequency = partTime.frequency;
		this.active = partTime.active;
	}

	public static ImmutableList<PartTimeDTO> of(ImmutableList<JPartTime> partTimes) {
		return ImmutableList.copyOf(Collections2.transform(partTimes, new Function<JPartTime, PartTimeDTO>() {
			@Nullable
			@Override
			public PartTimeDTO apply(@Nullable final JPartTime partTime) {
				return of(partTime);
			}
		}));
	}

	private static PartTimeDTO of(final JPartTime partTime) {
		return new PartTimeDTO(partTime);
	}
}
