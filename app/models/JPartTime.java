package models;

import com.github.jmkgreen.morphia.Key;
import com.github.jmkgreen.morphia.annotations.*;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.UpdateOperations;
import com.github.jmkgreen.morphia.query.UpdateResults;
import com.google.common.base.Preconditions;
import com.mongodb.WriteConcern;
import leodagdag.play2morphia.MorphiaPlugin;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import utils.time.TimeUtils;

import java.util.Date;
import java.util.List;

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

	public static UpdateResults<JPartTime> deactivateAll(final ObjectId userId) {
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

	private static Query<JPartTime> queryToFindMeByUser(final ObjectId userId) {
		return q().field("userId").equal(userId);
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
	}

	public static List<JPartTime> addPartTimes(List<JPartTime> pts) {
		Preconditions.checkArgument(CollectionUtils.isNotEmpty(pts));
		final Iterable<Key<JPartTime>> keys = MorphiaPlugin.ds().save(pts, WriteConcern.ACKNOWLEDGED);
		return MorphiaPlugin.ds().getByKeys(JPartTime.class, keys);
	}

	public static List<JPartTime> history(final String userId) {
		return queryToFindMeByUser(ObjectId.massageToObjectId(userId))
			                            .asList()                     ;
	}

	public static List<JPartTime> activeByUser(final String userId) {
		return activeByUser(ObjectId.massageToObjectId(userId));
	}

	public static List<JPartTime> activeByUser(final ObjectId userId) {
		return queryToFindMeByUser(userId)
			                            .field("active").equal(true)
			                            .asList();
	}

	public static List<JPartTime> activeByUser(final ObjectId userId, final Integer year, final Integer month) {
		final Date start = TimeUtils.firstDateOfMonth(year, month).toDate();
		final Date end = TimeUtils.lastDateOfMonth(year, month).toDate();
		final Query<JPartTime> q = queryToFindMeByUser(userId)
			                           .field("active").equal(true);
		q.or(
			    q.and(
				         q.criteria("_startDate").lessThanOrEq(end),
				         q.criteria("_endDate").doesNotExist()
			    ),
			    q.and(
				         q.criteria("_startDate").lessThanOrEq(end),
				         q.criteria("_endDate").greaterThanOrEq(start)
			    )
		);
		return q.asList();
	}

	public static JPartTime deactivate(final String id) {
		final Query<JPartTime> q = queryToFindMe(ObjectId.massageToObjectId(id));
		final UpdateOperations<JPartTime> upo = MorphiaPlugin.ds().createUpdateOperations(JPartTime.class)
			                                        .set("active", false);
		return MorphiaPlugin.ds().findAndModify(q, upo, false, false);
	}

	public static Boolean existActive(final ObjectId userId) {
		return MorphiaPlugin.ds().getCount(queryToFindMeByUser(userId)
			                                   .field("active").equal(Boolean.TRUE)) > 0;
	}
}
