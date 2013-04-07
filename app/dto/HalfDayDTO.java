package dto;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import models.JHalfDay;
import models.JMission;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import utils.serializer.ObjectIdSerializer;

import java.util.List;

/**
 * @author f.patin
 */
public class HalfDayDTO {

	public String code;
	public String label;
    @JsonSerialize(using = ObjectIdSerializer.class)
    public ObjectId  missionId;
	public String missionType;
	public List<PeriodDTO> periods = Lists.newArrayList();
	public Boolean isSpecial = Boolean.FALSE;

	@SuppressWarnings({"unused"})
	public HalfDayDTO() {
	}

	public HalfDayDTO(final JHalfDay halfDay, ImmutableMap<ObjectId, JMission> missions) {
		this.isSpecial = halfDay.isSpecial();
		if (this.isSpecial) {
			this.periods.addAll(PeriodDTO.of(halfDay.periods, missions));
			this.label = "special";
		} else {
            final JMission mission = missions.get(halfDay.missionId);
			this.code = mission.code;
			this.label = mission.label;
			this.missionId = halfDay.missionId;
			this.missionType = mission.missionType;
		}
	}

	public static HalfDayDTO of(final JHalfDay halfDay, final ImmutableMap<ObjectId, JMission> missions) {
		return (halfDay != null) ? new HalfDayDTO(halfDay,  missions) : null;
	}
}
