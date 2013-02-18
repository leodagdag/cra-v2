package dto;

import com.google.common.collect.ImmutableMap;
import models.JHalfDay;
import models.JMission;
import org.bson.types.ObjectId;

/**
 * @author f.patin
 */
public class HalfDayDTO {

	public String label;
	public String missionId;
	public String missionType;
	public Boolean isSpecial = Boolean.FALSE;

	public HalfDayDTO() {
	}

	public HalfDayDTO(final JHalfDay jHalfDay, final JMission jMission) {
		this.label = jMission.code;
		this.missionId = jHalfDay.missionId.toString();
		this.missionType = jMission.missionType;
		this.isSpecial = jHalfDay.isSpecial();
	}

	public static HalfDayDTO of(final JHalfDay jHalfDay, final ImmutableMap<ObjectId, JMission> jMission) {
		return (jHalfDay != null) ? new HalfDayDTO(jHalfDay, jMission.get(jHalfDay.missionId)) : null;
	}
}
