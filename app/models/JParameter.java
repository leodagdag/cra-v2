package models;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Index;
import com.github.jmkgreen.morphia.annotations.Indexes;
import com.github.jmkgreen.morphia.annotations.PostLoad;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Transient;
import com.github.jmkgreen.morphia.query.Query;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import constants.VehicleType;
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author f.patin
 */
@Entity("Parameter")
@Indexes({
	         @Index("_startDate, _endDate")
})
public class JParameter extends Model {

	@Id
	public ObjectId id;
	@Transient
	public DateTime startDate;
	@Transient
	public DateTime endDate;
	@Transient
	public Map<Integer, BigDecimal> car = Maps.newHashMap();
	@Transient
	public Map<Integer, BigDecimal> motorcycle = Maps.newHashMap();
	@Transient
	public BigDecimal zoneAmount;
	public String _zoneAmount;
	private Date _startDate;
	private Date _endDate;
	private Boolean active = Boolean.TRUE;
	private Map<Integer, String> _car = Maps.newHashMap();
	private Map<Integer, String> _motorcycle = Maps.newHashMap();

	private static Query<JParameter> q() {
		return MorphiaPlugin.ds().createQuery(JParameter.class);
	}

	public static BigDecimal coefficient(final JVehicle vehicle, final DateTime date) {
		if (VehicleType.car.equals(VehicleType.valueOf(vehicle.vehicleType))) {
			final ImmutableMap<Integer, BigDecimal> refs = JParameter.cars(date);
			if (vehicle.power >= 11) {
				return refs.get(11);
			} else if (vehicle.power >= 8) {
				return refs.get(8);
			} else if (vehicle.power >= 5) {
				return refs.get(5);
			} else {
				return refs.get(0);
			}
		} else {
			final ImmutableMap<Integer, BigDecimal> refs = JParameter.motorcyles(date);
			if (vehicle.power < 501) {
				return refs.get(0);
			} else {
				return refs.get(501);
			}
		}
	}

	public static ImmutableMap<Integer, BigDecimal> cars(final DateTime date) {
		return ImmutableMap.copyOf(vehicles(VehicleType.car, date)
			                           .get()
			                           .car);

	}

	public static ImmutableMap<Integer, BigDecimal> motorcyles(final DateTime date) {
		return ImmutableMap.copyOf(vehicles(VehicleType.motorcycle, date)
			                           .get()
			                           .motorcycle);
	}

	private static Query<JParameter> vehicles(final VehicleType vehicleType, final DateTime date) {
		return queryToFindMe(date)
			       .retrievedFields(true, "_" + vehicleType.name())
			       .disableValidation();
	}

	private static Query<JParameter> queryToFindMe(final DateTime date) {
		final Query<JParameter> q = q().field("_startDate").lessThanOrEq(date.toDate());
		q.or(
			    q.criteria("_endDate").equal(null),
			    q.criteria("_endDate").greaterThanOrEq(date.toDate())
		);
		return q;
	}

	public static BigDecimal zoneAmount(final DateTime date) {
		return queryToFindMe(date)
			       .retrievedFields(true, "_zoneAmount")
			       .disableValidation()
			       .get().zoneAmount;
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
		if (car != null) {
			_car = Maps.transformValues(car, new Function<BigDecimal, String>() {
				@Nullable
				@Override
				public String apply(@Nullable final BigDecimal bigDecimal) {
					return bigDecimal.toPlainString();
				}
			});
		}
		if (motorcycle != null) {
			_motorcycle = Maps.transformValues(motorcycle, new Function<BigDecimal, String>() {
				@Nullable
				@Override
				public String apply(@Nullable final BigDecimal bigDecimal) {
					return bigDecimal.toPlainString();
				}
			});
		}
		if (zoneAmount != null) {
			_zoneAmount = zoneAmount.toPlainString();
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
		if (_car != null) {
			car = Maps.transformValues(_car, new Function<String, BigDecimal>() {
				@Nullable
				@Override
				public BigDecimal apply(@Nullable final String s) {
					return new BigDecimal(s);
				}
			});
		}
		if (_motorcycle != null) {
			motorcycle = Maps.transformValues(_motorcycle, new Function<String, BigDecimal>() {
				@Nullable
				@Override
				public BigDecimal apply(@Nullable final String s) {
					return new BigDecimal(s);
				}
			});
		}
		if (_zoneAmount != null) {
			zoneAmount = new BigDecimal(_zoneAmount);
		}
	}

}
