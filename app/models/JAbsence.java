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
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import play.Logger;
import utils.business.AbsenceUtils;
import utils.time.TimeUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static constants.Util.TWO;

/**
 * @author f.patin
 */
@Entity("Absence")
public class JAbsence extends Model implements MongoModel {

	@Id
	public ObjectId id;
	public ObjectId userId;
	public ObjectId missionId;
	public ObjectId fileId;
	@Transient
	public DateTime startDate;
	public boolean startMorning;
	@Transient
	public DateTime endDate;
	public boolean endAfternoon;
	@Transient
	public BigDecimal nbDays;
	public String comment;
	@Transient
	public DateTime sentDate;
	private Date _startDate;
	private Date _endDate;
	private String _nbDays;
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
		return q().field(Mapper.ID_KEY).equal(id);
	}

	private static Query<JAbsence> queryToFindMeByUser(final String id) {
		return q().field("userId").equal(ObjectId.massageToObjectId(id));
	}

	private static Query<JAbsence> queryByDate(final String userId, final Integer startYear, final Integer startMonth, final Integer endYear, final Integer endMonth) {
		final Query<JAbsence> q = queryToFindMeByUser(userId);
		if(startYear != null && startMonth != null && endYear != null && endMonth != null) {
			final Date startFirstDay = TimeUtils.firstDateOfMonth(startYear, startMonth).withTimeAtStartOfDay().toDate();
			final Date endFirstDay = TimeUtils.lastDateOfMonth(endYear, endMonth).withTimeAtStartOfDay().toDate();
			q.or(
				    q.and(
					         q.criteria("_startDate").greaterThanOrEq(startFirstDay),
					         q.criteria("_startDate").lessThan(endFirstDay)
				    ),
				    q.and(
					         q.criteria("_endDate").greaterThan(startFirstDay),
					         q.criteria("_endDate").lessThanOrEq(endFirstDay)
				    )
			);
		}
		return q;
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
		if(nbDays != null) {
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
		if(_sentDate != null) {
			sentDate = new DateTime(_sentDate.getTime());
		}
		if(_nbDays != null) {
			nbDays = new BigDecimal(_nbDays);
		}
	}

	public static JAbsence delete(final String id) {
		final JAbsence absence = MorphiaPlugin.ds().findAndDelete(queryToFindMe(ObjectId.massageToObjectId(id)));
		JAbsenceDay.delete(absence);
		return absence;
	}

	public static JAbsence create(final JAbsence absence) throws AbsenceAlreadyExistException {

		final List<JAbsenceDay> absenceDays = AbsenceUtils.extractAbsenceDays(absence);

		if(JAbsenceDay.exist(absenceDays)) {
			throw new AbsenceAlreadyExistException(absence);
		}
		absence.nbDays = new BigDecimal(absenceDays.size()).divide(TWO);
		absence.insert();
		JAbsenceDay.save(absence, absenceDays);

		return absence;
	}

	public static List<JAbsence> fetchALL(final String userId, final Integer year, final Integer month) {
		final List<JAbsence> result = Lists.newArrayList();
		result.addAll(fetchCP(userId, year, month));
		result.addAll(fetchRTT(userId, year, month));
		result.addAll(fetchOTHER(userId, year, month));
		return result;
	}

	public static List<JAbsence> fetchCP(final String userId, final Integer year, final Integer month) {
		return fetchQuery(userId, AbsenceType.CP, year, month, year, DateTimeConstants.JUNE, year+1, DateTimeConstants.MAY)
			       .asList();
	}

	public static List<JAbsence> fetchRTT(final String userId, final Integer year, final Integer month) {
		return fetchQuery(userId, AbsenceType.RTT, year, month, year, DateTimeConstants.JANUARY, year, DateTimeConstants.DECEMBER)
			       .asList();
	}

	public static List<JAbsence> fetchOTHER(final String userId, final Integer year, final Integer month) {
		return fetchQuery(userId, AbsenceType.OTHER, year, month, year, DateTimeConstants.JANUARY, year, DateTimeConstants.DECEMBER)
			       .asList();
	}

	private static Query<JAbsence> fetchQuery(final String userId, final AbsenceType absenceType, final Integer year, final Integer month, final Integer defaultStartYear, final Integer defaultStartMonth, final Integer defaultEndYear, final Integer defaultEndMonth){
		final Query<JAbsence> q;
		if(year == 0) {
			q = queryToFindMeByUser(userId);
		} else if(month == 0) {
			q = queryByDate(userId, defaultStartYear, defaultStartMonth, defaultEndYear, defaultEndMonth);
		} else {
			q = queryByDate(userId, year, month, year, month);
		}
		q.field("missionId").in(JMission.getAbsencesMissionIds(absenceType));
		return q;
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

	public String label() {
		return AbsenceUtils.label(this);
	}

	@Override
	public ObjectId id() {
		return this.id;
	}


}
