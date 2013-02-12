package models;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Index;
import com.github.jmkgreen.morphia.annotations.Indexes;
import com.github.jmkgreen.morphia.annotations.PostLoad;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Transient;
import com.google.common.collect.Lists;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.joda.time.DateTime;
import utils.deserializer.DateTimeDeserializer;
import utils.time.TimerHelper;

import java.util.Date;
import java.util.List;

/**
 * @author f.patin
 */
@Entity
@Indexes({
	@Index("craId"),
	@Index("_date"),
	@Index("year, month")
})
public class Day {

	@Id
	public ObjectId id;

	public ObjectId craId;

	@Transient
	@JsonDeserialize(using = DateTimeDeserializer.class)
	public DateTime date;
	private Date _date;

	public Integer year;

	public Integer month;

	@Embedded
	public HalfDay morning;

	@Embedded
	public HalfDay afternoon;

	public String comment;

	public Boolean isSaturday() {
		return date != null && TimerHelper.isSaturday(date);
	}

	public Boolean isSunday() {
		return date != null && TimerHelper.isSunday(date);
	}

	public Boolean isDayOff() {
		return date != null && TimerHelper.isDayOff(date);
	}

	@SuppressWarnings({"unused"})
	@PrePersist
	private void prePersist() {
		_date = date != null ? date.toDate() : null;
		year = _date != null ? new DateTime(_date).getYear() : null;
		month = _date != null ? new DateTime(_date).getMonthOfYear() : null;
	}

	@SuppressWarnings({"unused"})
	@PostLoad
	private void postLoad() {
		date = _date != null ? new DateTime(_date.getTime()) : null;
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

	public static List<Day> find(final ObjectId userId, final Integer year, final Integer month) {
		return MorphiaPlugin.ds().createQuery(Day.class)
			.field("craId").equal(userId)
			.field("year").equal(year)
			.field("month").equal(month)
			.asList();
	}
}
