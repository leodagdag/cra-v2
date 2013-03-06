package models;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Index;
import com.github.jmkgreen.morphia.annotations.Indexes;
import com.github.jmkgreen.morphia.annotations.PostLoad;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Transient;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.UpdateOperations;
import com.mongodb.WriteConcern;
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

/**
 * @author f.patin
 */
@Entity("Vehicle")
@Indexes({
	         @Index("userId"),
	         @Index(value = "userId, active")
})
public class JVehicle extends Model {

	@Id
	public ObjectId id;
	public ObjectId userId;
	public String vehicleType;
	public String brand;
	public Integer power;
	public String matriculation;
	@Transient
	public DateTime startDate;
	@Transient
	public DateTime endDate;
	private Date _startDate;
	private Date _endDate;
	private Boolean active = Boolean.TRUE;

	public static JVehicle save(final JVehicle vehicle) {
		UpdateOperations<JVehicle> ops = MorphiaPlugin.ds().createUpdateOperations(JVehicle.class)
			                                 .set("active", false)
			                                 .set("_endDate", vehicle.startDate.toDate());
		Query<JVehicle> q = MorphiaPlugin.ds().createQuery(JVehicle.class)
			                    .field("userId").equal(vehicle.userId)
			                    .field("active").equal(Boolean.TRUE);
		MorphiaPlugin.ds().update(q, ops, false, WriteConcern.ACKNOWLEDGED);

		return vehicle.insert();
	}

	public static JVehicle active(final String userId) {
		return active(byUserId(userId));
	}

	private static Query<JVehicle> byUserId(final String userId) {
		return byUserId(ObjectId.massageToObjectId(userId));
	}

	private static Query<JVehicle> byUserId(final ObjectId userId) {
		return MorphiaPlugin.ds().createQuery(JVehicle.class)
			       .field("userId").equal(userId);
	}

	private static JVehicle active(final Query<JVehicle> q) {
		return q
			       .field("active").equal(Boolean.TRUE)
			       .get();
	}

	public static JVehicle active(final ObjectId userId) {
		return active(byUserId(userId));
	}

	public static List<JVehicle> history(final String userId) {
		return byUserId(userId)
			       .field("active").equal(Boolean.FALSE)
			       .asList();
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
	}

}