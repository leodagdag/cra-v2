package models;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Index;
import com.github.jmkgreen.morphia.annotations.Indexes;
import com.github.jmkgreen.morphia.annotations.PostLoad;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Transient;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mongodb.WriteConcern;
import constants.VehicleType;
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.MorphiaPlugin;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.joda.time.DateTime;
import utils.deserializer.DateTimeDeserializer;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author f.patin
 */
@Entity("Claim")
@Indexes({
	         @Index("userId, year, month")
})
public class JClaim extends Model implements MongoModel {

	@Id
	public ObjectId id;
	public ObjectId userId;
	public Integer year;
	public Integer month;
	@Transient
	public DateTime date;
	public ObjectId missionId;
	public String claimType;
	@Transient
	public BigDecimal amount = BigDecimal.ZERO;
	@Transient
	public BigDecimal kilometer;
	@Transient
	public BigDecimal kilometerAmount = BigDecimal.ZERO;
	public String journey;
	@Transient
	public BigDecimal totalClaim = BigDecimal.ZERO;
	public String comment;
	public String _amount;
	public String _kilometer;
	private String _kilometerAmount;
	private String _totalClaim;
	private Date _date;

	private static Query<JClaim> q() {
		return MorphiaPlugin.ds().createQuery(JClaim.class);
	}

	public static ImmutableList<JClaim> forUser(final ObjectId userId, final Integer year, final Integer month) {
		return ImmutableList.copyOf(q()
			                            .field("userId").equal(userId)
			                            .field("year").equal(year)
			                            .field("month").equal(month)
			                            .asList());
	}

	public static JClaim create(final JClaim claim) {
		return claim.insert();
	}

	public static String delete(final String id) {
		MorphiaPlugin.ds().delete(queryToFindMe(ObjectId.massageToObjectId(id)), WriteConcern.ACKNOWLEDGED);
		return id;
	}

	private static Query<JClaim> queryToFindMe(final ObjectId id) {
		return q().field(Mapper.ID_KEY).equal(id);
	}

	@SuppressWarnings({"unused"})
	@PrePersist
	private void prePersist() {
		if (date != null) {
			_date = date.toDate();
			year = new DateTime(_date).getYear();
			month = new DateTime(_date).getMonthOfYear();
		}
		if (amount != null) {
			_amount = amount.toPlainString();
		}
		if (kilometer != null) {
			_kilometer = kilometer.toPlainString();
		}
		compute();
	}

	private void compute() {
		if (this.kilometer != null) {
			JVehicle vehicle = JVehicle.active(this.userId);
			final BigDecimal ref;
			if (VehicleType.car.name().equals(vehicle.vehicleType)) {
				final ImmutableMap<Integer, BigDecimal> refs = JParameter.cars(this.date);
				if (vehicle.power >= 11) {
					ref = refs.get(11);
				} else if (vehicle.power >= 8) {
					ref = refs.get(8);
				} else if (vehicle.power >= 5) {
					ref = refs.get(5);
				} else {
					ref = refs.get(0);
				}
			} else {
				final ImmutableMap<Integer, BigDecimal> refs = JParameter.motorcyles(this.date);
				if (vehicle.power < 501) {
					ref = refs.get(0);
				} else {
					ref = refs.get(501);
				}
			}
			kilometerAmount = kilometer.multiply(ref);
			_kilometerAmount = kilometerAmount.toPlainString();
		}
		totalClaim = amount.add(kilometerAmount);
		_totalClaim = totalClaim.toPlainString();
	}

	@SuppressWarnings({"unused"})
	@PostLoad
	private void postLoad() {
		if (_date != null) {
			date = new DateTime(_date.getTime());
		}
		if (StringUtils.isNotBlank(_amount)) {
			amount = new BigDecimal(_amount);
		}
		if (StringUtils.isNotBlank(_kilometer)) {
			kilometer = new BigDecimal(_kilometer);
		}
		if (StringUtils.isNotBlank(_kilometerAmount)) {
			kilometerAmount = new BigDecimal(_kilometerAmount);
		}
		if (StringUtils.isNotBlank(_totalClaim)) {
			totalClaim = new BigDecimal(_totalClaim);
		}
	}

	@Override
	public ObjectId id() {
		return this.id;
	}
}