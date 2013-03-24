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
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.UpdateOperations;
import com.github.jmkgreen.morphia.query.UpdateResults;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import constants.MomentOfDay;
import exceptions.IllegalDayOperation;
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.MorphiaPlugin;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import utils.deserializer.DateTimeDeserializer;
import utils.time.TimeUtils;
import utils.transformer.Transformer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author f.patin
 */
@Entity(JDay.COLLECTION_NAME)
@Indexes({
	         @Index("craId"),
	         @Index("_date"),
	         @Index("year, month")
})
public class JDay extends Model implements MongoModel {

	public static final String COLLECTION_NAME = "Day";
	@Id
	public ObjectId id;
	public ObjectId craId;
	public ObjectId userId;
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
		this.month = date.getMonthOfYear();
		this.year = date.getYear();
	}

	private static Query<JDay> q() {
		return MorphiaPlugin.ds().createQuery(JDay.class);
	}

	public static List<JDay> find(final ObjectId userId, final ObjectId craId, final Integer year, final Integer month, final Boolean withPastAndFuture) {
		final List<DateTime> allDates = Lists.newArrayList(TimeUtils.getDaysOfMonth(year, month, withPastAndFuture));
		final List<JDay> days = Lists.newArrayList();
		if (craId != null) {
			days.addAll(q()
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

	public static void createDays(final ObjectId craId, final List<JDay> days) throws IllegalDayOperation {
		final List<Date> dates = Transformer.extractDates(days);
		// Extract corresponding days in database
		final List<JDay> oldDays = q()
			                           .field("craId").equal(craId)
			                           .field("_date").in(dates)
			                           .asList();
		if (CollectionUtils.isNotEmpty(oldDays)) {
			final List<JDay> holidays = Transformer.extractHolidays(oldDays);
			// Report holidays in new days
			for (JDay h : holidays) {
				for (JDay d : days) {
					if (h.date.isEqual(d.date)) {
						d.morning = (h.morning != null) ? h.morning : d.morning;
						d.afternoon = (h.afternoon != null) ? h.afternoon : d.afternoon;
					}
				}
			}
			// delete existing days (non Holidays)
			final List<ObjectId> oldDaysIds = Transformer.extractObjectId(new ArrayList<MongoModel>(oldDays));
			MorphiaPlugin.ds().delete(q().field(Mapper.ID_KEY).in(oldDaysIds), WriteConcern.ACKNOWLEDGED);
			// delete Mission allowance of old deleted days
			JClaim.deleteMissionAllowance(oldDays);
		}
		// create new days
		MorphiaPlugin.ds().save(Transformer.setCraId(days, craId), WriteConcern.ACKNOWLEDGED);
		JClaim.addMissionAllowance(days);
	}

	public static void addAbsenceDay(final JAbsence absence) {
		final List<DateTime> dts = TimeUtils.datesBetween(absence.startDate, absence.endDate, false);
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
				day.userId = absence.userId;
			}
			day.comment = absence.comment;
			if (dt.isEqual(absence.startDate)) {
				// First Day
				if (Boolean.TRUE.equals(absence.startMorning)) {
					day.morning = new JHalfDay(absence.missionId);
				}
				if (Boolean.TRUE.equals(absence.startAfternoon)) {
					day.afternoon = new JHalfDay(absence.missionId);
				}

			} else if (dt.isEqual(absence.endDate)) {
				// Last Day
				if (Boolean.TRUE.equals(absence.endMorning)) {
					day.morning = new JHalfDay(absence.missionId);
				}
				if (Boolean.TRUE.equals(absence.endAfternoon)) {
					day.afternoon = new JHalfDay(absence.missionId);
				}
			} else {
				day.morning = new JHalfDay(absence.missionId);
				day.afternoon = new JHalfDay(absence.missionId);
			}
			day.insert();
		}
	}

	public static JDay find(final ObjectId craId, final DateTime dts) {
		return q()
			       .field("craId").equal(craId)
			       .field("_date").equal(dts.toDate())
			       .get();
	}

	public static void deleteAbsenceDays(final List<DateTime> dates, final String userId, final Boolean includeStartMorning, final Boolean includeEndAfternoon) {
		final Map<DateTime, Set<DateTime>> m = Maps.newTreeMap(DateTimeComparator.getDateOnlyInstance());
		for (DateTime dt : dates) {
			final DateTime firstDayOfMonth = TimeUtils.getFirstDayOfMonth(dt);
			if (!m.containsKey(firstDayOfMonth)) {
				m.put(firstDayOfMonth, new TreeSet<DateTime>(DateTimeComparator.getDateOnlyInstance()));
			}
			m.get(firstDayOfMonth).add(dt);
		}
		// Retrieve all craIds
		final List<ObjectId> craIds = Lists.newArrayList(Collections2.transform(m.keySet(), new Function<DateTime, ObjectId>() {
			@Nullable
			@Override
			public ObjectId apply(@Nullable final DateTime dt) {
				return JCra.find(ObjectId.massageToObjectId(userId), dt.getYear(), dt.getMonthOfYear()).id;
			}
		}));
		// Remove all days
		for (DateTime dt : m.keySet()) {
			for (DateTime date : m.get(dt)) {
				final Query<JDay> q = q()
					                      .field("userId").equal(ObjectId.massageToObjectId(userId))
					                      .field("_date").equal(date.toDate());
				if (date.isEqual(dates.get(0)) && Boolean.FALSE.equals(includeStartMorning)) {
					UpdateOperations<JDay> uop = MorphiaPlugin.ds().createUpdateOperations(JDay.class).unset("afternoon");
					JDay day = MorphiaPlugin.ds().findAndModify(q, uop);
					if (day.morning == null) {
						MorphiaPlugin.ds().delete(queryToFindMe(day.id));
					}
				} else if (date.isEqual(dates.get(dates.size() - 1)) && Boolean.FALSE.equals(includeEndAfternoon)) {
					UpdateOperations<JDay> uop = MorphiaPlugin.ds().createUpdateOperations(JDay.class).unset("morning");
					JDay day = MorphiaPlugin.ds().findAndModify(q, uop);
					if (day.afternoon == null) {
						MorphiaPlugin.ds().delete(queryToFindMe(day.id));
					}
				} else {
					MorphiaPlugin.ds().delete(q, WriteConcern.ACKNOWLEDGED);
				}
			}
		}

		// Delete cra if necessary
		for (ObjectId craId : craIds) {
			if (!existDays(craId)) {
				JCra.delete(craId);
			}
		}

	}

	private static Query<JDay> queryToFindMe(final ObjectId id) {
		return q()
			       .field(Mapper.ID_KEY).equal(id);
	}

	private static Boolean existDays(final ObjectId craId) {
		return MorphiaPlugin.ds()
			       .getCount(q()
				                 .field("craId").equal(craId)) > 0;
	}

	public static JDay addPartTime(final ObjectId craId, final ObjectId userId, final DateTime date, final Boolean morning, final Boolean afternoon) {
		final ObjectId partTimeId = JMission.getPartTimeId();

		JDay day = q()
			           .field("craId").equal(craId)
			           .field("userId").equal(userId)
			           .field("_date").equal(date.toDate())
			           .get();
		if (day == null) {
			day = new JDay(date);
			day.craId = craId;
			day.userId = userId;
		}
		boolean toSave = false;
		if (day.morning == null && Boolean.TRUE.equals(morning)) {
			day.morning = new JHalfDay(partTimeId);
			toSave = true;
		}
		if (day.afternoon == null && Boolean.TRUE.equals(afternoon)) {
			day.afternoon = new JHalfDay(partTimeId);
			toSave = true;
		}

		if (toSave) {
			MorphiaPlugin.ds().save(day);
		}
		return day;
	}

	public static WriteResult delete(final String craId, final Long date) {
		final Query<JDay> q = q()
			                      .field("craId").equal(ObjectId.massageToObjectId(craId))
			                      .field("_date").equal(new DateTime(date).toDate());
		return MorphiaPlugin.ds().delete(q, WriteConcern.ACKNOWLEDGED);
	}

	public static JDay deleteHalfDay(final ObjectId craId, final DateTime dateTime, final MomentOfDay momentOfDay) {
		final Query<JDay> q = q().field("craId").equal(craId)
			                      .field("_date").equal(dateTime.toDate());
		final UpdateOperations<JDay> uop = MorphiaPlugin.ds().createUpdateOperations(JDay.class)
			                                   .unset(momentOfDay.name());

		return MorphiaPlugin.ds().findAndModify(q, uop, false, false);
	}

	public static JHalfDay findHalfDay(final ObjectId craId, final DateTime dateTime, final MomentOfDay momentOfDay) {
		final JDay day = q()
			                 .field("craId").equal(craId)
			                 .field("_date").equal(dateTime.toDate())
			                 .retrievedFields(true, momentOfDay.name())
			                 .disableValidation()
			                 .get();
		switch (momentOfDay) {
			case morning:
				return day.morning;
			case afternoon:
				return day.afternoon;
			default:
				return null;
		}
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
			if (year == null) {
				year = date.getYear();
			}
			if (month == null) {
				month = date.getMonthOfYear();
			}
		}
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

	public Set<ObjectId> missionIds() {
		final Set<ObjectId> result = Sets.newHashSet();
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
}
