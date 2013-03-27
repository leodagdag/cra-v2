package models;

import com.github.jmkgreen.morphia.annotations.*;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import constants.AbsenceType;
import exceptions.AbsenceAlreadyExistException;
import exceptions.AbsenceEndIllegalDateException;
import exceptions.AbsenceStartIllegalDateException;
import exceptions.ContainsOnlyWeekEndOrDayOfException;
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import play.libs.F;
import utils.time.TimeUtils;
import utils.transformer.AbsenceUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author f.patin
 */
@Entity("Absence")
public class JAbsence extends Model implements MongoModel {

	@Id
	public ObjectId id;
	public ObjectId userId;
	public ObjectId missionId;
	@Transient
	public DateTime startDate;
	@Transient
	public DateTime endDate;
	@Transient
	public BigDecimal nbDays;
	public String comment;
	private Date _startDate;
	private Date _endDate;
	private String _nbDays;

	@SuppressWarnings({"unused"})
	public JAbsence() {
	}

	private static Query<JAbsence> queryToFindMe(final ObjectId id) {
		return MorphiaPlugin.ds().createQuery(JAbsence.class)
			       .field(Mapper.ID_KEY).equal(id);
	}

	private static Query<JAbsence> queryToFindMeByUser(final ObjectId id) {
		return MorphiaPlugin.ds().createQuery(JAbsence.class)
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
		if(_nbDays != null) {
			nbDays = new BigDecimal(_nbDays);
		}
	}

	public static JAbsence delete(final String userId, final String id) {
		return MorphiaPlugin.ds().findAndDelete(queryToFindMe(ObjectId.massageToObjectId(id)));
	}

	public static JAbsence create(final JAbsence absence) throws AbsenceAlreadyExistException, ContainsOnlyWeekEndOrDayOfException, AbsenceStartIllegalDateException, AbsenceEndIllegalDateException {
		if(TimeUtils.isDayOffOrWeekEnd(absence.startDate)) {
			throw new AbsenceStartIllegalDateException(absence.startDate);
		}
		if(TimeUtils.isDayOffOrWeekEnd(absence.endDate.minusDays(1))) {
			throw new AbsenceEndIllegalDateException(absence.endDate.minusDays(1));
		}
		if(TimeUtils.containsOnlyWeekEndOrDayOff(absence.startDate.withTimeAtStartOfDay(), absence.endDate.minusDays(1).withTimeAtStartOfDay())) {
			throw new ContainsOnlyWeekEndOrDayOfException(absence);
		}
		final Query<JAbsence> dateQuery = MorphiaPlugin.ds().createQuery(JAbsence.class);
		dateQuery.or(
			            dateQuery.and(
				                         dateQuery.criteria("_startDate").greaterThanOrEq(absence.startDate.toDate()),
				                         dateQuery.criteria("_startDate").lessThan(absence.endDate.toDate())
			            ),
			            dateQuery.and(
				                         dateQuery.criteria("_endDate").greaterThan(absence.startDate.toDate()),
				                         dateQuery.criteria("_endDate").lessThanOrEq(absence.endDate.toDate())
			            ),
			            dateQuery.and(
				                         dateQuery.criteria("_startDate").lessThanOrEq(absence.startDate.toDate()),
				                         dateQuery.criteria("_endDate").greaterThanOrEq(absence.endDate.toDate())
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
		if(startYear != null && startMonth != null) {
			q.field("_startDate").greaterThanOrEq(TimeUtils.getFirstDayOfMonth(startYear, startMonth).toDate());
		}
		if(endYear != null && endMonth != null) {
			q.field("_endDate").lessThanOrEq(TimeUtils.getLastDateOfMonth(endYear, endMonth).toDate());
		}
		if(!AbsenceType.ALL.equals(absenceType)) {
			q.field("missionId").in(JMission.getAbsencesMissionIds(absenceType));
		}
		return q.asList();
	}

	@Override
	public ObjectId id() {
		return this.id;
	}
}
