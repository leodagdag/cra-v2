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
	public String missionType;
	public Boolean isSpecial = Boolean.FALSE;

	public HalfDayDTO() {
	}

	public HalfDayDTO(final JHalfDay JHalfDay, final JMission mission) {
		this.label = mission.code;
		this.missionType = mission.missionType;
		this.isSpecial = JHalfDay.isSpecial();
	}

	public static HalfDayDTO of(final JHalfDay JHalfDay, final ImmutableMap<ObjectId, JMission> missions) {
		return new HalfDayDTO(JHalfDay, missions.get(JHalfDay.missionId));
	}
}
