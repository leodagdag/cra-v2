package models;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Index;
import com.github.jmkgreen.morphia.annotations.Indexes;
import com.github.jmkgreen.morphia.annotations.PostLoad;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Transient;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import constants.AbsenceType;
import constants.MissionType;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.springframework.util.CollectionUtils;
import utils.deserializer.DateTimeDeserializer;
import utils.serializer.DateTimeSerializer;

import javax.annotation.Nullable;
import java.util.Arrays;
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

	public static ImmutableMap<ObjectId, JMission> codeAndMissionType(final List<ObjectId> missionsIds, final Boolean withoutHolidayAndCSS) {
		if (!CollectionUtils.isEmpty(missionsIds)) {
			final Query<JMission> q = MorphiaPlugin.ds().createQuery(JMission.class)
				                          .field(Mapper.ID_KEY).in(missionsIds);
			if (withoutHolidayAndCSS) {
				q.filter("missionType !=", "holiday");
			}
			final List<JMission> missions = q
				                                .retrievedFields(true, "code", "missionType")
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

	public static ImmutableList<JMission> getAbsencesMissions() {
		return getAbsencesMissions(null);
	}

	public static ImmutableList<JMission> getAbsencesMissions(final AbsenceType absenceType) {
		List<AbsenceType> criterias = Lists.newArrayList();

		if (absenceType == null) {
			criterias.addAll(Arrays.asList(AbsenceType.values()));
		} else {
			criterias.add(absenceType);
		}

		List<String> absenceTypes = Lists.newArrayList(Collections2.transform(criterias, new Function<AbsenceType, String>() {
			@Nullable
			@Override
			public String apply(@Nullable final AbsenceType absenceType) {
				return absenceType.name();
			}
		}));
		final List<JMission> missions = MorphiaPlugin.ds().createQuery(JMission.class)
			                                .field("absenceType").in(absenceTypes)
			                                .retrievedFields(true, Mapper.ID_KEY)
			                                .disableValidation()
			                                .asList();
		return new ImmutableList.Builder<JMission>().addAll(missions).build();
	}

	public static ImmutableList<ObjectId> getAbsencesMissionIds() {
		return getAbsencesMissionIds(null);
	}

	public static ImmutableList<ObjectId> getAbsencesMissionIds(final AbsenceType absenceType) {
		final List<ObjectId> missionIds = Lists.newArrayList(Collections2.transform(getAbsencesMissions(absenceType), new Function<JMission, ObjectId>() {
			@Nullable
			@Override
			public ObjectId apply(@Nullable final JMission mission) {
				return mission.id;
			}
		}));
		return new ImmutableList.Builder<ObjectId>().addAll(missionIds).build();
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
