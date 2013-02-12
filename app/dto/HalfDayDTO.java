package dto;

import com.google.common.collect.ImmutableMap;
import models.HalfDay;
import models.Mission;
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

	public HalfDayDTO(final HalfDay halfDay, final Mission mission) {
		this.label = mission.code;
		this.missionType = mission.missionType;
		this.isSpecial = halfDay.isSpecial();
	}

	public static HalfDayDTO of(final HalfDay halfDay, final ImmutableMap<ObjectId, Mission> missions) {
		return new HalfDayDTO(halfDay, missions.get(halfDay.missionId));
	}
}
