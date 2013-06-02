package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import models.JMission;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import utils.serializer.ObjectIdSerializer;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * @author f.patin
 */
public class CompleteMissionDTO {

	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;
	public String code;
	public String label;

	@SuppressWarnings({"unused"})
	public CompleteMissionDTO() {
	}

	public CompleteMissionDTO(final JMission mission) {
		this.id = mission.id;
		this.code = mission.code;
		this.label = mission.label;
	}

	public static CompleteMissionDTO of(final JMission mission) {
		return new CompleteMissionDTO(mission);
	}

	public static List<CompleteMissionDTO> of(ImmutableMap<ObjectId, JMission> missions) {
		return Lists.newArrayList(Collections2.transform(missions.values(), new Function<JMission, CompleteMissionDTO>() {
			@Nullable
			@Override
			public CompleteMissionDTO apply(@Nullable final JMission mission) {
				return new CompleteMissionDTO(mission);
			}
		}));
	}

	public static List<CompleteMissionDTO> of(Collection<JMission> missions) {
		return Lists.newArrayList(Collections2.transform(missions, new Function<JMission, CompleteMissionDTO>() {
			@Nullable
			@Override
			public CompleteMissionDTO apply(@Nullable final JMission mission) {
				return new CompleteMissionDTO(mission);
			}
		}));
	}

	public static List<CompleteMissionDTO> of(ImmutableCollection<JMission> missions) {
		return Lists.newArrayList(Collections2.transform(missions, new Function<JMission, CompleteMissionDTO>() {
			@Nullable
			@Override
			public CompleteMissionDTO apply(@Nullable final JMission mission) {
				return new CompleteMissionDTO(mission);
			}
		}));
	}

	public static CompleteMissionDTO of(final ObjectId id) {
		return new CompleteMissionDTO(JMission.codeAndMissionType(id));
	}
}
