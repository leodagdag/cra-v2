package models;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.PostLoad;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Transient;
import com.github.jmkgreen.morphia.query.Query;
import com.mongodb.WriteConcern;
import constants.MomentOfDay;
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

/**
 * @author f.patin
 */
@Entity("AbsenceDay")
public class JAbsenceDay extends Model {
	@Id
	public ObjectId id;
	public ObjectId absenceId;
	public ObjectId userId;
	@Transient
	public DateTime date;
	public Date _date;
	public Integer momentOfDay;

	@SuppressWarnings({"unused"})
	public JAbsenceDay() {
	}

	public JAbsenceDay(final JAbsence absence, final DateTime date) {
		this.userId = absence.userId;
		this.date = date;
	}

	private static Query<JAbsenceDay> q() {
		return MorphiaPlugin.ds().createQuery(JAbsenceDay.class);
	}

	private static JAbsenceDay newAbsenceHalfDay(final JAbsence absence, final DateTime date, final MomentOfDay momentOfDay) {
		final JAbsenceDay absenceDay = new JAbsenceDay(absence, date);
		absenceDay.momentOfDay = momentOfDay.index;
		return absenceDay;
	}

	@SuppressWarnings({"unused"})
	@PrePersist
	private void prePersist() {
		if(date != null) {
			_date = date.toDate();
		}
	}

	@SuppressWarnings({"unused"})
	@PostLoad
	private void postLoad() {
		if(_date != null) {
			date = new DateTime(_date.getTime());
		}
	}

	public static JAbsenceDay newMorning(final JAbsence absence, final DateTime date) {
		return newAbsenceHalfDay(absence, date, MomentOfDay.morning);
	}

	public static JAbsenceDay newAfternoon(final JAbsence absence, final DateTime date) {
		return newAbsenceHalfDay(absence, date, MomentOfDay.afternoon);
	}

	public static boolean exist(final List<JAbsenceDay> absenceDays) {
		for(JAbsenceDay absenceDay : absenceDays) {
			final long count = q()
				                   .field("userId").equal(absenceDay.userId)
				                   .field("_date").equal(absenceDay.date.toDate())
				                   .field("momentOfDay").equal(absenceDay.momentOfDay)
				                   .countAll();
			if(count > 0) {
				return true;
			}
		}
		return false;
	}

	public static void save(final JAbsence absence, final List<JAbsenceDay> absenceDays) {
		for(JAbsenceDay absenceDay : absenceDays){
			absenceDay.absenceId = absence.id;
		}
		MorphiaPlugin.ds().save(absenceDays, WriteConcern.ACKNOWLEDGED);
	}

	public static void delete(final JAbsence absence) {
		final Query<JAbsenceDay> q = q().field("absenceId").equal(absence.id);
		MorphiaPlugin.ds().delete(q, WriteConcern.ACKNOWLEDGED);
	}
}
