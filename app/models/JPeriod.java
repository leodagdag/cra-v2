package models;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.github.jmkgreen.morphia.annotations.PostLoad;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Transient;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import utils.serializer.LocalTimeSerializer;

import java.util.Date;

/**
 * @author f.patin
 */
@Embedded
public class JPeriod {

	public ObjectId missionId;
	@Transient
	@JsonSerialize(using = LocalTimeSerializer.class)
	public LocalTime startTime;
	@Transient
	@JsonSerialize(using = LocalTimeSerializer.class)
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
}
