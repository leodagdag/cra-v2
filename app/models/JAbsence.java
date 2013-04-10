package models;

import com.github.jmkgreen.morphia.annotations.*;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.UpdateOperations;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.WriteConcern;
import constants.AbsenceType;
import exceptions.AbsenceAlreadyExistException;
import exceptions.AbsenceEndIllegalDateException;
import exceptions.AbsenceStartIllegalDateException;
import exceptions.ContainsOnlyWeekEndOrDayOfException;
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import utils.business.AbsenceUtils;
import utils.time.TimeUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author f.patin
 */
@Entity("Absence")
public class JAbsence extends Model implements  MongoModel {

	@Id
	public ObjectId id;
	public ObjectId userId;
	public ObjectId missionId;
	public ObjectId fileId;
	@Transient
	public DateTime startDate;
	@Transient
	public DateTime endDate;
	@Transient
	public BigDecimal nbDays;
	public String comment;
	@Transient
	public DateTime creationDate;
	@Transient
	public DateTime sentDate;
	private Date _startDate;
	private Date _endDate;
	private String _nbDays;
	private Date _creationDate = DateTime.now().toDate();
	private Date _sentDate;

	@SuppressWarnings({"unused"})
	public JAbsence() {
	}

	private static Query<JAbsence> q() {
		return MorphiaPlugin.ds().createQuery(JAbsence.class);
	}

	private static UpdateOperations<JAbsence> ops() {
		return MorphiaPlugin.ds().createUpdateOperations(JAbsence.class);
	}

	private static Query<JAbsence> queryToFindMe(final ObjectId id) {
		return q()
			       .field(Mapper.ID_KEY).equal(id);
	}

	private static Query<JAbsence> queryToFindMeByUser(final ObjectId id) {
		return q()
			       .field("userId").equal(id);
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
		if(sentDate != null) {
			_sentDate = sentDate.toDate();
		}
		if(nbDays == null) {
			_nbDays = AbsenceUtils.nbDaysBetween(startDate, endDate).toPlainString();
		} else {
			_nbDays = nbDays.toPlainString();
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
		if(_creationDate != null) {
			creationDate = new DateTime(_creationDate.getTime());
		}
		if(_sentDate != null) {
			sentDate = new DateTime(_sentDate.getTime());
		}
		if(_nbDays != null) {
			nbDays = new BigDecimal(_nbDays);
		}
	}

	public static JAbsence delete(final String id) {
		return MorphiaPlugin.ds().findAndDelete(queryToFindMe(ObjectId.massageToObjectId(id)));
	}

	public static JAbsence create(final JAbsence absence) throws AbsenceAlreadyExistException {
		final Query<JAbsence> dateQuery = q();
		final Date start = absence.startDate.toDate();
		final Date end = absence.endDate.toDate();
		dateQuery.or(
			            dateQuery.and(
				                         dateQuery.criteria("_startDate").greaterThanOrEq(start),
				                         dateQuery.criteria("_startDate").lessThan(end)
			            ),
			            dateQuery.and(
				                         dateQuery.criteria("_endDate").greaterThan(start),
				                         dateQuery.criteria("_endDate").lessThanOrEq(end)
			            ),
			            dateQuery.and(
				                         dateQuery.criteria("_startDate").lessThanOrEq(start),
				                         dateQuery.criteria("_endDate").greaterThanOrEq(end)
			            )
		);
		if(dateQuery.countAll() > 0) {
			throw new AbsenceAlreadyExistException(absence);
		}
		return absence.insert();
	}

	public static List<JAbsence> fetch(final String userId, final AbsenceType absenceType) {
		return fetch(userId, absenceType, null, null, null, null);
	}

	public static List<JAbsence> fetch(final String userId, final AbsenceType absenceType, final Integer startYear, final Integer startMonth, final Integer endYear, final Integer endMonth) {
		final Query<JAbsence> q = queryToFindMeByUser(ObjectId.massageToObjectId(userId));


		if(startYear != null && startMonth != null && endYear != null && endMonth != null) {
			final DateTime startFirstDay = TimeUtils.firstDayOfMonth(startYear, startMonth);
			final DateTime endFirstDay = TimeUtils.lastDateOfMonth(endYear, endMonth);
			q.or(
				    q.and(
					         q.criteria("_startDate").greaterThanOrEq(startFirstDay.toDate()),
					         q.criteria("_startDate").lessThanOrEq(endFirstDay.toDate())
				    ),
				    q.and(
					         q.criteria("_endDate").greaterThanOrEq(startFirstDay.toDate()),
					         q.criteria("_endDate").lessThanOrEq(endFirstDay.toDate())
				    )
			);
		}

		if(!AbsenceType.ALL.equals(absenceType)) {
			q.field("missionId").in(JMission.getAbsencesMissionIds(absenceType));
		}
		return q.asList();
	}

	public static JAbsence fetch(final String id) {
		return queryToFindMe(ObjectId.massageToObjectId(id)).get();
	}

	public static void updateFileId(final List<ObjectId> ids, final ObjectId fileId) {
		final UpdateOperations<JAbsence> ops = ops()
			                                       .set("fileId", fileId);
		final Query<JAbsence> q = q()
			                          .field(Mapper.ID_KEY).in(ids);
		MorphiaPlugin.ds().update(q, ops, false, WriteConcern.ACKNOWLEDGED);
	}

	public static JAbsence updateSentDate(final ObjectId id, final DateTime date) {
		final UpdateOperations<JAbsence> ops = ops()
			                                       .set("_sentDate", date.toDate());
		return MorphiaPlugin.ds().findAndModify(queryToFindMe(id), ops, false, false);
	}

	public static void updateSentDate(final List<ObjectId> ids, final DateTime date) {
		final UpdateOperations<JAbsence> ops = ops()
			                                       .set("_sentDate", date.toDate());
		final Query<JAbsence> q = q()
			                          .field(Mapper.ID_KEY).in(ids);
		MorphiaPlugin.ds().update(q, ops, false, WriteConcern.ACKNOWLEDGED);
	}

	public static List<ObjectId> usersToSend() {
		final List<JAbsence> userIds = q()
			                               .field("_sentDate").doesNotExist()
			                               .retrievedFields(true, "userId")
			                               .disableValidation()
			                               .asList();

		return Lists.newArrayList(Sets.newHashSet(Collections2.transform(userIds, new Function<JAbsence, ObjectId>() {
			@Nullable
			@Override
			public ObjectId apply(@Nullable final JAbsence absence) {
				return absence.userId;
			}
		})));
	}

	public static List<JAbsence> byUserToSent(final ObjectId userId) {
		return q()
			       .field("userId").equal(userId)
			       .field("_sentDate").doesNotExist()
			       .order("missionId,_startDate")
			       .asList();
	}

	public static List<JAbsence> byFileId(final ObjectId fileId) {
		return q()
			       .field("fileId").equal(fileId)
			       .order("missionId,_startDate")
			       .asList();
	}

	@Override
	public ObjectId id() {
		return this.id;
	}


}
