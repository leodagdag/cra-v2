package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import models.JMission;
import org.bson.types.ObjectId;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author f.patin
 */
public class MissionDTO {

	public String id;
	public String code;

	public MissionDTO() {
	}

	public MissionDTO(final JMission mission) {
		this.id = mission.id.toString();
		this.code = mission.code;
	}

	public static MissionDTO of(final JMission mission) {
		return new MissionDTO(mission);
	}

	public static List<MissionDTO> of(ImmutableMap<ObjectId, JMission> missions) {
		return Lists.newArrayList(Collections2.transform(missions.values(), new Function<JMission, MissionDTO>() {
			@Nullable
			@Override
			public MissionDTO apply(@Nullable final JMission mission) {
				return new MissionDTO(mission);
			}
		}));
	}

	public static List<MissionDTO> of(ImmutableList<JMission> missions) {
		return Lists.newArrayList(Collections2.transform(missions, new Function<JMission, MissionDTO>() {
			@Nullable
			@Override
			public MissionDTO apply(@Nullable final JMission mission) {
				return new MissionDTO(mission);
			}
		}));
	}

	public static MissionDTO of(final ObjectId id) {
		return new MissionDTO(JMission.codeAndMissionType(id));
	}
}
