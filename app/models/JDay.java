package models;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Index;
import com.github.jmkgreen.morphia.annotations.Indexes;
import com.github.jmkgreen.morphia.annotations.PostLoad;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Transient;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.WriteConcern;
import exceptions.IllegalDayOperation;
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.MorphiaPlugin;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.joda.time.DateTime;
import utils.deserializer.DateTimeDeserializer;
import utils.time.TimeUtils;
import utils.transformer.Transformer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author f.patin
 */
@Entity(JDay.COL_NAME)
@Indexes({
	         @Index("craId"),
	         @Index("_date"),
	         @Index("year, month")
})
public class JDay extends Model implements MongoModel {
	public static final String COL_NAME = "Day";
	@Id
	public ObjectId id;
	public ObjectId craId;
	@Transient
	@JsonDeserialize(using = DateTimeDeserializer.class)
	public DateTime date;
	public Integer year;
	public Integer month;
	@Embedded
	public JHalfDay morning;
	@Embedded
	public JHalfDay afternoon;
	public String comment;
	private Date _date;

	public JDay(final Long date) {
		this.date = new DateTime(date);
	}

	public JDay() {
	}

	public JDay(final DateTime date) {
		this.date = date;
	}

	public static List<JDay> find(final ObjectId craId, final Integer year, final Integer month, final Boolean withPastAndFuture) {
		final List<DateTime> allDates = Lists.newArrayList(TimeUtils.getDaysOfMonth(year, month, withPastAndFuture));
		final List<JDay> days = Lists.newArrayList();
		if (craId != null) {
			days.addAll(MorphiaPlugin.ds().createQuery(JDay.class)
				            .field("craId").equal(craId)
				            .field("_date").in(TimeUtils.dateTime2Date(allDates))
				            .asList());
		} else {
			days.addAll(Collections2.transform(allDates, new Function<DateTime, JDay>() {
				@Nullable
				@Override
				public JDay apply(@Nullable final DateTime dt) {
					return new JDay(dt);
				}
			}));
		}
		for (final DateTime dt : allDates) {
			final JDay existDay = Iterables.find(days, new Predicate<JDay>() {
				@Override
				public boolean apply(@Nullable final JDay jDay) {
					return jDay.date.isEqual(dt);
				}
			}, null);
			if (existDay == null) {
				days.add(new JDay(dt));
			}
		}
		Collections.sort(days, new Comparator<JDay>() {
			@Override
			public int compare(final JDay d1, final JDay d2) {
				return d1.date.compareTo(d2.date);
			}
		});
		return days;
	}

	public static JDay find(final ObjectId craId, final DateTime dts) {
		return MorphiaPlugin.ds().createQuery(JDay.class)
			       .field("craId").equal(craId)
			       .field("_date").equal(dts.toDate())
			       .get();
	}

	public static void create(final ObjectId craId, final List<JDay> days) throws IllegalDayOperation {
		final List<Date> dates = Lists.newArrayList(Collections2.transform(days, new Function<JDay, Date>() {
			@Nullable
			@Override
			public Date apply(@Nullable final JDay d) {
				return d.date.toDate();
			}
		}));
		// Extract corresponding days in database
		final List<JDay> oldDays = MorphiaPlugin.ds().createQuery(JDay.class)
			                           .field("craId").equal(craId)
			                           .field("_date").in(dates)
			                           .asList();
		if (CollectionUtils.isNotEmpty(oldDays)) {
			// Check holidays and remove them
			if(Transformer.extractHolidays(oldDays).size() > 0){
				throw new IllegalDayOperation("Vous ne pouvez pas modifier ces journées. Des congés existent.");
			}
			// delete existing days
			final List<ObjectId> oldDaysIds = Transformer.extractObjectId(new ArrayList<MongoModel>(oldDays));
			MorphiaPlugin.ds().delete(MorphiaPlugin.ds().createQuery(JDay.class).field(Mapper.ID_KEY).in(oldDaysIds), WriteConcern.ACKNOWLEDGED);
		}
		// create new days
		MorphiaPlugin.ds().save(Transformer.setCraId(days, craId), WriteConcern.ACKNOWLEDGED);
	}

	public Boolean isSaturday() {
		return date != null && TimeUtils.isSaturday(date);
	}

	public Boolean isSunday() {
		return date != null && TimeUtils.isSunday(date);
	}

	public Boolean isDayOff() {
		return date != null && TimeUtils.isDayOff(date);
	}

	public Boolean inPastOrFuture(final Integer year, final Integer month) {
		return year != date.getYear() || month != date.getMonthOfYear();
	}

	@SuppressWarnings({"unused"})
	@PrePersist
	private void prePersist() {
		if (date != null) {
			_date = date.toDate();
			year = new DateTime(_date).getYear();
			month = new DateTime(_date).getMonthOfYear();
		}
	}

	@SuppressWarnings({"unused"})
	@PostLoad
	private void postLoad() {
		if (_date != null) {
			date = new DateTime(_date.getTime());
		}
	}

	public List<ObjectId> missionIds() {
		final List<ObjectId> result = Lists.newArrayList();
		if (morning != null) {
			result.addAll(morning.missionIds());
		}
		if (afternoon != null) {
			result.addAll(afternoon.missionIds());
		}
		return result;
	}

	@Override
	public ObjectId id() {
		return id;
	}

	public static void add(final JAbsence absence) {
		List<DateTime> dts = TimeUtils.datesOfWeek(absence.startDate, absence.endDate, true);
		JCra cra = null;

		for (DateTime dt : dts) {
			if (cra == null || (cra.year != dt.getYear() || cra.month != dt.getMonthOfYear())) {
				cra = JCra.getOrCreate(absence.userId, dt.getYear(), dt.getMonthOfYear());
			}
			// Search JDay...
			JDay day = JDay.find(cra.id, dt);
			// ..If not exist we create it.
			if (day == null) {
				day = new JDay(dt);
				day.craId = cra.id;
			}
			day.comment = absence.comment;
			if (dt.isEqual(absence.startDate)) {
				if (Boolean.TRUE.equals(absence.startMorning)) {
					day.morning = new JHalfDay(absence.missionId);
				}
				day.afternoon = new JHalfDay(absence.missionId);
			} else if (dt.isEqual(absence.endDate)) {
				day.morning = new JHalfDay(absence.missionId);
				if (Boolean.TRUE.equals(absence.startAfternoon)) {
					day.afternoon = new JHalfDay(absence.missionId);
				}
			} else {
				day.morning = new JHalfDay(absence.missionId);
				day.afternoon = new JHalfDay(absence.missionId);
			}
			day.insert();
		}
	}

}
