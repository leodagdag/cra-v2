package models;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.PostLoad;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Transient;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import constants.AbsenceType;
import exceptions.AbsenceAlreadyExistException;
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import utils.time.TimeUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
	public Boolean startMorning;
	public Boolean startAfternoon;
	@Transient
	public DateTime endDate;
	public Boolean endMorning;
	public Boolean endAfternoon;
	@Transient
	public BigDecimal nbDays;
	public String comment;
	private Date _startDate;
	private Date _endDate;
	private String _nbDays;

	@SuppressWarnings({"unused"})
	public JAbsence() {
	}

	public static JAbsence delete(final String userId, final String id) {
		final JAbsence absence = MorphiaPlugin.ds().findAndDelete(queryToFindMe(ObjectId.massageToObjectId(id)));
		final List<DateTime> dates = TimeUtils.datesBetween(absence.startDate, absence.endDate, false);
		JDay.deleteAbsenceDays(dates, userId, absence.startMorning, absence.endAfternoon);
		return absence;
	}

	private static Query<JAbsence> queryToFindMe(final ObjectId id) {
		return MorphiaPlugin.ds().createQuery(JAbsence.class)
			       .field(Mapper.ID_KEY).equal(id);
	}

	public static JAbsence create(final JAbsence absence) throws AbsenceAlreadyExistException {

		// newAbsenceStartDate < dbAbsenceStartDate || dbAbsenceEndDate < newAbsenceEndDate
		final Query<JAbsence> dateQuery = MorphiaPlugin.ds().createQuery(JAbsence.class);
		dateQuery.or(
			            dateQuery.and(
				                         dateQuery.criteria("_startDate").greaterThanOrEq(absence.startDate.toDate()),
				                         dateQuery.criteria("_startDate").lessThanOrEq(absence.endDate.toDate())
			            ),
			            dateQuery.and(
				                         dateQuery.criteria("_endDate").greaterThanOrEq(absence.startDate.toDate()),
				                         dateQuery.criteria("_endDate").lessThanOrEq(absence.endDate.toDate())
			            )
		);
		if (dateQuery.asKeyList().size() > 0) {
			throw new AbsenceAlreadyExistException();
		}
		// newAbsenceStartDate < dbAbsence < newAbsenceEndDate
		Query<JAbsence> innerQuery = MorphiaPlugin.ds().createQuery(JAbsence.class);
		innerQuery.and(
			              innerQuery.criteria("_startDate").greaterThan(absence.startDate.toDate()),
			              innerQuery.criteria("_endDate").lessThan(absence.endDate.toDate())
		);
		if (innerQuery.asKeyList().size() > 0) {
			throw new AbsenceAlreadyExistException();
		}
		// dbStartDate < newAbsence < dbEndDate
		Query<JAbsence> outerQuery = MorphiaPlugin.ds().createQuery(JAbsence.class);
		outerQuery.and(
			              outerQuery.criteria("_startDate").lessThan(absence.startDate.toDate()),
			              outerQuery.criteria("_endDate").greaterThan(absence.endDate.toDate())
		);
		if (outerQuery.asKeyList().size() > 0) {
			throw new AbsenceAlreadyExistException();
		}
		absence.insert();
		JDay.addAbsenceDay(absence);
		return absence;
	}

	public static List<JAbsence> fetch(final String userId, final AbsenceType absenceType) {
		return fetch(userId, absenceType, null, null, null, null);
	}

	public static List<JAbsence> fetch(final String userId, final AbsenceType absenceType, final Integer startYear, final Integer startMonth, final Integer endYear, final Integer endMonth) {
		final Query<JAbsence> q = queryToFindMeByUser(ObjectId.massageToObjectId(userId));
		if (startYear != null && startMonth != null) {
			q.field("_startDate").greaterThanOrEq(TimeUtils.getFirstDayOfMonth(startYear, startMonth).toDate());
		}
		if (endYear != null && endMonth != null) {
			q.field("_endDate").lessThanOrEq(TimeUtils.getLastDateOfMonth(endYear, endMonth).toDate());
		}
		if (!AbsenceType.ALL.equals(absenceType)) {
			q.field("missionId").in(JMission.getAbsencesMissionIds(absenceType));
		}
		return q.asList();
	}

	private static Query<JAbsence> queryToFindMeByUser(final ObjectId id) {
		return MorphiaPlugin.ds().createQuery(JAbsence.class)
			       .field("userId").equal(id);
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
		if (nbDays == null) {
			_nbDays = computeNbDays();
		} else {
			_nbDays = nbDays.toPlainString();
		}
	}

	private String computeNbDays() {
		return new BigDecimal(TimeUtils.datesBetween(this.startDate, this.endDate, false).size())
			       .subtract(Boolean.FALSE.equals(this.startMorning) ? new BigDecimal("0.5") : BigDecimal.ZERO)
			       .subtract(Boolean.FALSE.equals(this.endAfternoon) ? new BigDecimal("0.5") : BigDecimal.ZERO)
			       .toPlainString();
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
		if (_nbDays != null) {
			nbDays = new BigDecimal(_nbDays);
		}
	}

	@Override
	public ObjectId id() {
		return this.id;
	}
}
