package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import models.JMission;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import utils.serializer.ObjectIdSerializer;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author f.patin
 */
public class MissionDTO {

    @JsonSerialize(using = ObjectIdSerializer.class)
    public ObjectId id;
	public String code;
	public String description;

	public MissionDTO() {
	}

	public MissionDTO(final JMission mission) {
		this.id = mission.id;
		this.code = mission.code;
		this.description = mission.description;
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

	public static List<MissionDTO> of(ImmutableCollection<JMission> missions) {
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
