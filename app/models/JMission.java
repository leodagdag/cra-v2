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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.springframework.util.CollectionUtils;
import utils.deserializer.DateTimeDeserializer;
import utils.serializer.DateTimeSerializer;

import javax.annotation.Nullable;
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

	public String allowanceType;
	@Transient
	@JsonSerialize(using = DateTimeSerializer.class)
	@JsonDeserialize(using = DateTimeDeserializer.class)
	public DateTime startDate;
	private Date _startDate;

	@Transient
	@JsonSerialize(using = DateTimeSerializer.class)
	@JsonDeserialize(using = DateTimeDeserializer.class)
	public DateTime endDate;
	private Date _endDate;

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
}
