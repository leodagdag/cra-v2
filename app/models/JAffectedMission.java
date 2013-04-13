package models;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.github.jmkgreen.morphia.annotations.PostLoad;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Transient;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * @author f.patin
 */
@Embedded
public class JAffectedMission {

	@Transient
	public DateTime startDate;
	private Date _startDate;

	@Transient
	public DateTime endDate;
	private Date _endDate;
	public ObjectId missionId;

	@SuppressWarnings({"unused"})
	@PrePersist
	private void prePersist() {
		if(startDate != null) {
			_startDate = startDate.toDate();
		}
		if(endDate != null) {
			_endDate = endDate.toDate();
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
	}
}
