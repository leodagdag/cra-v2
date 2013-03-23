package models;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.github.jmkgreen.morphia.annotations.PostLoad;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Transient;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import utils.serializer.LocalTimeSerializer;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * @author f.patin
 */
@Embedded
public class JPeriod {

	public ObjectId missionId;
	@Transient
	public LocalTime startTime;
	@Transient
	public LocalTime endTime;
	private Date _startTime;
	private Date _endTime;

	public JPeriod() {
	}

	public JPeriod(final ObjectId missionId, final LocalTime startTime, final LocalTime endTime) {
		this.missionId = missionId;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	@SuppressWarnings({"unused"})
	@PrePersist
	private void prePersist() {
		if (startTime != null) {
			_startTime = startTime.toDateTimeToday().toDate();
		}
		if (endTime != null) {
			_endTime = endTime.toDateTimeToday().toDate();
		}
	}

	@SuppressWarnings({"unused"})
	@PostLoad
	private void postLoad() {
		if (_startTime != null) {
			startTime = new DateTime(_startTime.getTime()).toLocalTime();
		}
		if (_endTime != null) {
			endTime = new DateTime(_endTime.getTime()).toLocalTime();
		}
	}

	public static List<ObjectId> missionIds(final List<JPeriod> periods){
		return Lists.newArrayList(Collections2.transform(periods, new Function<JPeriod, ObjectId>() {
			@Nullable
			@Override
			public ObjectId apply(@Nullable final JPeriod p) {
				return p.missionId;
			}
		}));
	}
}
