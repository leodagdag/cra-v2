package models;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Index;
import com.github.jmkgreen.morphia.annotations.Indexes;
import com.github.jmkgreen.morphia.annotations.PostLoad;
import com.github.jmkgreen.morphia.annotations.PrePersist;
import com.github.jmkgreen.morphia.annotations.Transient;
import com.google.common.collect.ImmutableList;
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.MorphiaPlugin;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.map.ser.StdSerializers;
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
public class JClaim extends Model implements MongoModel{

	@Id
	public ObjectId id;
	public ObjectId userId;
	public Integer year;
	public Integer month;
	@Transient
	@JsonDeserialize(using = DateTimeDeserializer.class)
	public DateTime date;
	public ObjectId missionId;
	public String claimType;
	@Transient
	public BigDecimal amount;
	@Transient
	public BigDecimal kilometer;
	public String journey;
	public String comment;
	private Date _date;
	public String _amount;
	public String _kilometer;
	public static ImmutableList<JClaim> forUserId(final ObjectId userId, final Integer year, final Integer month) {
		return ImmutableList.copyOf(MorphiaPlugin.ds().createQuery(JClaim.class)
			                            .field("userId").equal(userId)
			                            .field("year").equal(year)
			                            .field("month").equal(month)
			                            .asList());
	}

	@SuppressWarnings({"unused"})
	@PrePersist
	private void prePersist() {
		if (date != null) {
			_date = date.toDate();
			year = new DateTime(_date).getYear();
			month = new DateTime(_date).getMonthOfYear();
		}
		if(amount != null){
			_amount = amount.toPlainString();
		}
		if(kilometer != null){
			_kilometer = kilometer.toPlainString();
		}
	}

	@SuppressWarnings({"unused"})
	@PostLoad
	private void postLoad() {
		if (_date != null) {
			date = new DateTime(_date.getTime());
		}
		if(StringUtils.isNotBlank(_amount)){
			amount = new BigDecimal(_amount);
		}
		if(StringUtils.isNotBlank(_kilometer)){
			kilometer = new BigDecimal(_kilometer);
		}
	}

	@Override
	public ObjectId id() {
		return this.id;
	}

	public static JClaim create(final JClaim claim) {
		return claim.insert();
	}
}
