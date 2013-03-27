package models;

import com.github.jmkgreen.morphia.Key;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.PostLoad;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Transient;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.UpdateOperations;
import com.github.jmkgreen.morphia.query.UpdateResults;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mongodb.WriteConcern;
import leodagdag.play2morphia.MorphiaPlugin;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * @author f.patin
 */
@Entity("PartTime")
public class JPartTime {

	@Id
	public ObjectId id;
	public ObjectId userId;
	@Transient
	public DateTime startDate;
	@Transient
	public DateTime endDate;
	public Integer dayOfWeek;
	public String momentOfDay;
	public Integer frequency;
	public Boolean active = Boolean.TRUE;
	private Date _startDate;
	private Date _endDate;

	public JPartTime(final ObjectId userId, final DateTime startDate, final Integer frequency) {
		this.userId = userId;
		this.startDate = startDate;
		this.frequency = frequency;
	}

	@SuppressWarnings({"unused"})
	public JPartTime() {
	}

	private static UpdateResults<JPartTime> deactivateAll(final ObjectId userId) {
		UpdateOperations<JPartTime> uop = MorphiaPlugin.ds().createUpdateOperations(JPartTime.class).set("active", false);
		Query<JPartTime> q = MorphiaPlugin.ds().createQuery(JPartTime.class)
			                     .field("userId").equal(userId)
			                     .field("active").equal(true);
		return MorphiaPlugin.ds().update(q, uop, false, WriteConcern.ACKNOWLEDGED);

	}

	private static Query<JPartTime> q() {
		return MorphiaPlugin.ds().createQuery(JPartTime.class);
	}

	private static Query<JPartTime> queryToFindMe(final ObjectId id) {
		return q().field(Mapper.ID_KEY).equal(id);
	}

	private static Query<JPartTime> byUser(final ObjectId userId) {
		return q().field("userId").equal(userId);
	}

	public static ImmutableList<JPartTime> addPartTimes(ImmutableList<JPartTime> pts) {
		Preconditions.checkArgument(CollectionUtils.isNotEmpty(pts));
		deactivateAll(pts.get(0).userId);
		final Iterable<Key<JPartTime>> keys = MorphiaPlugin.ds().save(pts, WriteConcern.ACKNOWLEDGED);
		return ImmutableList.copyOf(MorphiaPlugin.ds().getByKeys(JPartTime.class, keys));
	}

	public static ImmutableList<JPartTime> byUser(final String userId) {
		return ImmutableList.copyOf(
			                           byUser(ObjectId.massageToObjectId(userId))
				                           .asList()
		);
	}

	public static ImmutableList<JPartTime> activeByUser(final String userId) {
		return activeByUser(ObjectId.massageToObjectId(userId));
	}

	public static ImmutableList<JPartTime> activeByUser(final ObjectId userId) {
		return ImmutableList.copyOf(
			                           byUser(userId)
				                           .field("active").equal(true)
				                           .asList()
		);
	}

	public static void deactivate(final String id) {
		final Query<JPartTime> q = queryToFindMe(ObjectId.massageToObjectId(id));
		final UpdateOperations<JPartTime> upo = MorphiaPlugin.ds().createUpdateOperations(JPartTime.class)
			                                        .set("active", false);
		MorphiaPlugin.ds().update(q, upo, false, WriteConcern.ACKNOWLEDGED);
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
