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
import utils.time.TimeUtils;

import java.util.Date;
import java.util.List;

/**
 * @author f.patin
 */
@Entity("Day")
@Indexes({
	@Index("craId"),
	@Index("_date"),
	@Index("year, month")
})
public class JDay {

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
	public JHalfDay morning;

	@Embedded
	public JHalfDay afternoon;

	public String comment;

	public Boolean isSaturday() {
		return date != null && TimeUtils.isSaturday(date);
	}

	public Boolean isSunday() {
		return date != null && TimeUtils.isSunday(date);
	}

	public Boolean isDayOff() {
		return date != null && TimeUtils.isDayOff(date);
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

	public static List<JDay> find(final ObjectId userId, final Integer year, final Integer month) {
		return MorphiaPlugin.ds().createQuery(JDay.class)
			.field("craId").equal(userId)
			.field("year").equal(year)
			.field("month").equal(month)
			.asList();
	}
}
