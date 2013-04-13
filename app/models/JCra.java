package models;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.UpdateOperations;
import com.mongodb.WriteConcern;
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import play.libs.F;
import utils.time.JTimeUtils;

import java.util.List;

/**
 * @author f.patin
 */
@Entity("Cra")
public class JCra extends Model {

	@Id
	public ObjectId id;
	public Integer year;
	public Integer month;
	public ObjectId userId;
	public String comment;
	public Boolean isValidated = Boolean.FALSE;
	public Boolean partTimeApplied = Boolean.FALSE;

	public JCra() {
	}

	public JCra(final ObjectId userId, final Integer year, final Integer month) {
		this.year = year;
		this.month = month;
		this.userId = userId;
	}

	private static Query<JCra> q() {
		return MorphiaPlugin.ds().createQuery(JCra.class);
	}

	private static UpdateOperations<JCra> uop() {
		return MorphiaPlugin.ds().createUpdateOperations(JCra.class);
	}

	private static Query<JCra> queryToFindMe(final ObjectId id) {
		return MorphiaPlugin.ds().createQuery(JCra.class).field(Mapper.ID_KEY).equal(id);
	}

	private static Query<JCra> queryByUserId(final ObjectId userId) {
		return q().field("userId").equal(userId);
	}

	private static Query<JCra> queryByUserYearMonth(final ObjectId userId, final Integer year, final Integer month) {
		return queryByUserId(userId)
			       .field("year").equal(year)
			       .field("month").equal(month);
	}

	private static JCra applyPartTime(final JCra cra) {
		if(Boolean.FALSE.equals(cra.partTimeApplied)) {
			final List<JPartTime> partTimes = JPartTime.activeByUser(cra.userId, cra.year, cra.month);

			if(!partTimes.isEmpty()) {
				for(JPartTime partTime : partTimes) {
					final List<F.Tuple3<DateTime, Boolean, Boolean>> dates = JTimeUtils.extractDatesInYearMonth(cra.year, cra.month, partTime.startDate, partTime.endDate, partTime.dayOfWeek, partTime.momentOfDay, partTime.frequency);
					for(F.Tuple3<DateTime, Boolean, Boolean> d : dates) {
						JDay.addPartTime(cra.id, cra.userId, d._1, d._2, d._3);
					}
				}

				cra.partTimeApplied = Boolean.TRUE;
				cra.update();
			}
		}
		return cra;
	}

	public static JCra getOrCreate(final ObjectId userId, final Integer year, final Integer month) {
		final JCra cra = find(userId, year, month);
		if(cra == null) {
			return create(userId, year, month);
		} else {
			return applyPartTime(cra);
		}
	}

	public static JCra getOrCreate(final ObjectId craId, final ObjectId userId, final Integer year, final Integer month) {
		if(craId == null) {
			return create(userId, year, month);
		} else {
			return applyPartTime(queryToFindMe(craId).get());
		}
	}

	private static JCra create(final ObjectId userId, final Integer year, final Integer month) {
		JCra cra = new JCra(userId, year, month);
		return applyPartTime(cra.<JCra>insert());
	}

	public static JCra find(final ObjectId userId, final Integer year, final Integer month) {
		return queryByUserYearMonth(userId, year, month)
			       .get();
	}

	public static void delete(final ObjectId id) {
		MorphiaPlugin.ds().delete(queryToFindMe(id), WriteConcern.ACKNOWLEDGED);
	}

	public static void delete(final ObjectId userId, final Integer year, final Integer month) {
		MorphiaPlugin.ds().delete(queryByUserYearMonth(userId, year, month), WriteConcern.ACKNOWLEDGED);
	}

	public static void unapplyPartTime(final JPartTime partTime) {
		final Query<JCra> q = queryByUserId(partTime.userId)
			                      .field("year").greaterThanOrEq(partTime.startDate.getYear())
			                      .field("month").greaterThanOrEq(partTime.startDate.getMonthOfYear());
		if(partTime.endDate != null) {
			q
				.field("year").lessThanOrEq(partTime.endDate.getYear())
				.field("month").lessThanOrEq(partTime.endDate.getMonthOfYear());
		}
		MorphiaPlugin.ds().update(q, uop().set("partTimeApplied", false), false, WriteConcern.ACKNOWLEDGED);
	}

	public static JCra fetch(final String id) {
		return queryToFindMe(ObjectId.massageToObjectId(id)).get();
	}
}
