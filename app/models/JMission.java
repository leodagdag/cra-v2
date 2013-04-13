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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import constants.AbsenceType;
import constants.GenesisMissionCode;
import constants.MissionType;
import leodagdag.play2morphia.MorphiaPlugin;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
	public String label;
	public String description;
	public String missionType;
	public String absenceType;
	public String allowanceType;
	public Boolean isClaimable;
	@Transient
	public DateTime startDate;
	@Transient
	public DateTime endDate;
	@Transient
	public BigDecimal distance;
	public String _distance;
	private Date _startDate;
	private Date _endDate;

	private static Query<JMission> q() {
		return MorphiaPlugin.ds().createQuery(JMission.class);
	}

	private static Query<JMission> queryToFindMe(final ObjectId id) {
		return q().field(Mapper.ID_KEY).equal(id);
	}

	@SuppressWarnings({"unused"})
	@PrePersist
	private void prePersist() {
		if(startDate != null) {
			_startDate = startDate.toDate();
		}
		if(endDate != null) {
			_endDate = endDate.toDate();
		}
		if(distance != null) {
			_distance = distance.toPlainString();
		}
	}

	@SuppressWarnings({"unused"})
	@PostLoad
	private void postLoad() {
		if(_startDate != null) {
			startDate = new DateTime(_startDate.getTime());
		}
		if(_endDate != null) {
			endDate = new DateTime(_endDate.getTime());
		}
		if(_distance != null) {
			distance = new BigDecimal(_distance);
		}
	}

	public static Map<ObjectId, JMission> codeAndMissionType(final List<ObjectId> missionsIds) {
		if(CollectionUtils.isNotEmpty(missionsIds)) {
			final List<JMission> missions = MorphiaPlugin.ds().createQuery(JMission.class)
				                                .field(Mapper.ID_KEY).in(missionsIds)
				                                .retrievedFields(true, Mapper.ID_KEY, "code", "label", "missionType")
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
			       .retrievedFields(true, Mapper.ID_KEY, "code", "label", "missionType")
			       .disableValidation()
			       .get();
	}

	public static List<JMission> getClaimableMissions(final List<ObjectId> ids) {
		return MorphiaPlugin.ds().createQuery(JMission.class)
			       .field(Mapper.ID_KEY).in(ids)
			       .field("isClaimable").equal(true)
			       .retrievedFields(true, Mapper.ID_KEY, "code", "label")
			       .disableValidation()
			       .asList();
	}


	public static List<JMission> craMissions(final List<ObjectId> ids) {
		return MorphiaPlugin.ds().createQuery(JMission.class)
			       .field(Mapper.ID_KEY).in(ids)
			       .field("missionType").in(MissionType.craMissionType)
			       .retrievedFields(true, Mapper.ID_KEY, "code", "label")
			       .disableValidation()
			       .asList();
	}

	public static List<JMission> getAbsencesMissions() {
		return getAbsencesMissions(null);
	}

	public static List<JMission> getAbsencesMissions(final AbsenceType absenceType) {
		final List<String> absenceTypes = newAbsenceTypesList(absenceType);
		return MorphiaPlugin.ds().createQuery(JMission.class)
			       .field("absenceType").in(absenceTypes)
			       .retrievedFields(true, Mapper.ID_KEY, "code", "label")
			       .disableValidation()
			       .asList();
	}

	private static List<String> newAbsenceTypesList(final AbsenceType absenceType) {
		return AbsenceType.asString(absenceType == null ? new ArrayList<AbsenceType>() : Lists.newArrayList(absenceType));
	}

	public static List<ObjectId> getAbsencesMissionIds() {
		return getAbsencesMissionIds(null);
	}

	public static List<ObjectId> getAbsencesMissionIds(final AbsenceType absenceType) {
		final List<String> absenceTypes = newAbsenceTypesList(absenceType);
		final List<JMission> missions = MorphiaPlugin.ds().createQuery(JMission.class)
			                                .field("absenceType").in(absenceTypes)
			                                .retrievedFields(true, Mapper.ID_KEY)
			                                .disableValidation()
			                                .asList();

		return Lists.newArrayList(Collections2.transform(missions, new Function<JMission, ObjectId>() {
			@Nullable
			@Override
			public ObjectId apply(@Nullable final JMission mission) {
				return mission.id;
			}
		}));
	}

	public static ObjectId getPartTimeId() {
		return MorphiaPlugin.ds().createQuery(JMission.class)
			       .field("customerId").equal(JCustomer.genesis().id)
			       .field("code").equal(GenesisMissionCode.TP)
			       .retrievedFields(true, Mapper.ID_KEY)
			       .disableValidation()
			       .get()
			       .id;
	}

	public static JMission fetch(final ObjectId id) {
		return queryToFindMe(id).get();
	}

	public static Boolean isAbsenceMission(final ObjectId missionId) {
		return MorphiaPlugin.ds().getCount(queryToFindMe(missionId)
			                                   .field("missionType").equal(MissionType.holiday.name())) > 0;
	}

	public static Boolean isClaimable(final ObjectId missionId) {
		return Boolean.TRUE.equals(queryToFindMe(missionId).get().isClaimable);
	}


}
