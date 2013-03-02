package models;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Index;
import com.github.jmkgreen.morphia.annotations.Indexes;
import com.github.jmkgreen.morphia.annotations.PostLoad;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Transient;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import constants.AbsenceType;
import leodagdag.play2morphia.MorphiaPlugin;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import utils.deserializer.DateTimeDeserializer;
import utils.serializer.DateTimeSerializer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author f.patin
 */
@Entity("Mission")
@Indexes({
	         @Index(value = "code", unique = true),
	         @Index("customerId"),
	         @Index("_startDate, _endDate")
})
public class JMission {

	@Id
	public ObjectId id;
	public ObjectId customerId;
	public String code;
	public String description;
	public String missionType;
	public String absenceType;
	public String allowanceType;
	public Boolean isClaim = Boolean.TRUE;
	@Transient
	@JsonSerialize(using = DateTimeSerializer.class)
	@JsonDeserialize(using = DateTimeDeserializer.class)
	public DateTime startDate;
	@Transient
	@JsonSerialize(using = DateTimeSerializer.class)
	@JsonDeserialize(using = DateTimeDeserializer.class)
	public DateTime endDate;
	private Date _startDate;
	private Date _endDate;

	public static ImmutableMap<ObjectId, JMission> codeAndMissionType(final List<ObjectId> missionsIds) {
		if (CollectionUtils.isNotEmpty(missionsIds)) {
			final List<JMission> missions = MorphiaPlugin.ds().createQuery(JMission.class)
				                                .field(Mapper.ID_KEY).in(missionsIds)
				                                .retrievedFields(true, Mapper.ID_KEY, "code", "missionType")
				                                .disableValidation()
				                                .asList();
			return Maps.uniqueIndex(missions, new Function<JMission, ObjectId>() {
				@Nullable
				@Override
				public ObjectId apply(@Nullable final JMission mission) {
					return mission.id;
				}
			});
		} else {
			return new ImmutableMap.Builder<ObjectId, JMission>().build();
		}
	}

	public static JMission codeAndMissionType(final ObjectId missionId) {
		return MorphiaPlugin.ds().createQuery(JMission.class)
			       .field(Mapper.ID_KEY).equal(missionId)
			       .retrievedFields(true, Mapper.ID_KEY, "code", "missionType")
			       .disableValidation()
			       .get();
	}

	public static ImmutableList<JMission> getClaimMissions(final ImmutableSet<ObjectId> ids) {
		return ImmutableList.copyOf(MorphiaPlugin.ds().createQuery(JMission.class)
			                            .field(Mapper.ID_KEY).in(ids)
			                            .field("isClaim").equal(true)
			                            .retrievedFields(true, Mapper.ID_KEY, "code")
			                            .disableValidation()
			                            .asList());
	}

	public static ImmutableList<JMission> getAbsencesMissions() {
		return getAbsencesMissions(null);
	}

	public static ImmutableList<JMission> getAbsencesMissions(final AbsenceType absenceType) {
		final List<String> absenceTypes = AbsenceType.asString(absenceType == null ? new ArrayList<AbsenceType>() : Lists.newArrayList(absenceType));
		return ImmutableList.copyOf(MorphiaPlugin.ds().createQuery(JMission.class)
			                            .field("absenceType").in(absenceTypes)
			                            .retrievedFields(true, Mapper.ID_KEY, "code")
			                            .disableValidation()
			                            .asList());
	}

	public static ImmutableList<ObjectId> getAbsencesMissionIds() {
		return getAbsencesMissionIds(null);
	}

	public static ImmutableList<ObjectId> getAbsencesMissionIds(final AbsenceType absenceType) {
		final List<String> absenceTypes = AbsenceType.asString(absenceType == null ? new ArrayList<AbsenceType>() : Lists.newArrayList(absenceType));
		final List<JMission> missions = MorphiaPlugin.ds().createQuery(JMission.class)
			                                .field("absenceType").in(absenceTypes)
			                                .retrievedFields(true, Mapper.ID_KEY)
			                                .disableValidation()
			                                .asList();

		final List<ObjectId> missionIds = Lists.newArrayList(Collections2.transform(missions, new Function<JMission, ObjectId>() {
			@Nullable
			@Override
			public ObjectId apply(@Nullable final JMission mission) {
				return mission.id;
			}
		}));
		return ImmutableList.copyOf(missionIds);
	}

	@SuppressWarnings({"unused"})
	@PrePersist
	private void prePersist() {
		if (startDate != null) {
			_startDate = startDate.toDate();
		}
		if (endDate != null) {
			_endDate = endDate.toDate();
		}
	}

	@SuppressWarnings({"unused"})
	@PostLoad
	private void postLoad() {
		if (_startDate != null) {
			startDate = new DateTime(_startDate.getTime());
		}
		if (_endDate != null) {
			endDate = new DateTime(_endDate.getTime());
		}
	}

}
