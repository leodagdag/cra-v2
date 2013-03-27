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
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import constants.ClaimType;
import constants.MissionAllowanceType;
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.MorphiaPlugin;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
	public String comment;
	public String _amount;
	public String _kilometer;
	private String _kilometerAmount;
	private Date _date;

	public JClaim() {
	}

	public JClaim(final ObjectId userId, final DateTime date, final ClaimType claimType, final ObjectId missionId) {
		this.userId = userId;
		this.date = date;
		this.claimType = claimType.name();
		this.missionId = missionId;
	}

	private static Query<JClaim> q() {
		return MorphiaPlugin.ds().createQuery(JClaim.class);
	}

	public static List<JClaim> create(final List<JClaim> claims) {
		MorphiaPlugin.ds().save(claims);
		return claims;
	}

	public static ObjectId delete(final String id) {
		delete(queryToFindMe(ObjectId.massageToObjectId(id)));
		return ObjectId.massageToObjectId(id);
	}

	private static Query<JClaim> queryToFindMe(final ObjectId id) {
		return q().field(Mapper.ID_KEY).equal(id);
	}

	private static WriteResult delete(final Query<JClaim> q) {
		return MorphiaPlugin.ds().delete(q, WriteConcern.ACKNOWLEDGED);
	}

	public static ImmutableList<JClaim> history(final ObjectId userId, final Integer year, final Integer month) {
		return ImmutableList.copyOf(q()
			                            .field("userId").equal(userId)
			                            .field("year").equal(year)
			                            .field("month").equal(month)
			                            .asList());
	}

	public static ImmutableList<JClaim> synthesis(final String userId, final Integer year, final Integer month) {
		return ImmutableList.copyOf(q()
			                            .field("userId").equal(ObjectId.massageToObjectId(userId))
			                            .field("year").equal(year)
			                            .field("month").equal(month)
			                            .asList());
	}

	public static void computeMissionAllowance(final ObjectId userId, final JDay day) {
		computeMissionAllowance(userId, Lists.newArrayList(day));
	}

	public static void computeMissionAllowance(final ObjectId userId, final List<JDay> days) {
		final List<DateTime> dts = Lists.newArrayList(Collections2.transform(days, new Function<JDay, DateTime>() {
			@Nullable
			@Override
			public DateTime apply(@Nullable final JDay day) {
				return day.date;
			}
		}));
		deleteMissionAllowance(userId, dts);
		addMissionAllowance(days);
	}

	private static void addMissionAllowance(final List<JDay> days) {
		final Set<JClaim> claims = Sets.newHashSet();
		for (final JDay day : days) {
			claims.addAll(Collections2.transform(day.missionIds(), new Function<ObjectId, JClaim>() {
				@Nullable
				@Override
				public JClaim apply(@Nullable final ObjectId missionId) {
					return JMission.isClaimable(missionId) ? new JClaim(day.userId, day.date, ClaimType.MISSION_ALLOWANCE, missionId).computeMissionAllowance() : null;
				}
			}));
		}
		claims.removeAll(Collections.singletonList(null));
		MorphiaPlugin.ds().save(claims, WriteConcern.ACKNOWLEDGED);
	}

	private JClaim computeMissionAllowance() {
		final JVehicle vehicle = JVehicle.active(this.userId);
		final JMission mission = JMission.fetch(this.missionId);
		switch (MissionAllowanceType.valueOf(mission.allowanceType)) {
			case ZONE:
				this.amount = JParameter.zoneAmount(date);
				break;
			case REAL:
				final BigDecimal coefficient = JParameter.coefficient(vehicle, this.date);
				this.amount = mission.distance.multiply(coefficient);
		}
		return this;
	}

	private static WriteResult deleteMissionAllowance(final ObjectId userId, final List<DateTime> dts) {
		final Collection<Date> dates = Collections2.transform(dts, new Function<DateTime, Date>() {
			@Nullable
			@Override
			public Date apply(@Nullable final DateTime dt) {
				return dt.toDate();
			}
		});
		final Query<JClaim> q = q()
			                        .field("userId").equal(userId)
			                        .field("_date").in(dates)
			                        .field("claimType").equal(ClaimType.MISSION_ALLOWANCE.name());
		return delete(q);
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
			final JVehicle vehicle = JVehicle.active(this.userId);
			final BigDecimal coefficient = JParameter.coefficient(vehicle, this.date);
			kilometerAmount = kilometer.multiply(coefficient);
			_kilometerAmount = kilometerAmount.toPlainString();
		}
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
	}

	@Override
	public ObjectId id() {
		return this.id;
	}

}
