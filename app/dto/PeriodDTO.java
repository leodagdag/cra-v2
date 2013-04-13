package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import models.JMission;
import models.JPeriod;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import utils.serializer.ObjectIdSerializer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author f.patin
 */
public class PeriodDTO {
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId missionId;
	public String code;
	public String label;
	public String missionType;
	public Long startTime;
	public Long endTime;
	public String periodType = "special";

	@SuppressWarnings({"unused"})
	public PeriodDTO() {
	}

	public PeriodDTO(final JPeriod period, final Map<ObjectId, JMission> missions) {
		final JMission mission = missions.get(period.missionId);
		this.missionId = period.missionId;
		this.code = mission.code;
		this.label = mission.label;
		this.missionType = mission.missionType;
		this.startTime = period.startTime.toDateTimeToday().getMillis();
		this.endTime = period.endTime.toDateTimeToday().getMillis();
	}

	public static List<PeriodDTO> of(final List<JPeriod> periods, final Map<ObjectId, JMission> missions) {
		return Lists.newArrayList(Collections2.transform(periods, new Function<JPeriod, PeriodDTO>() {
			@Nullable
			@Override
			public PeriodDTO apply(@Nullable final JPeriod p) {
				return new PeriodDTO(p, missions);
			}
		}));
	}
}
