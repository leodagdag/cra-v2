package models;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.UpdateOperations;
import com.google.common.collect.ImmutableList;
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
		final ImmutableList<JPartTime> partTimes = JPartTime.activeByUser(cra.userId);
		for (JPartTime partTime : partTimes) {
			final List<F.Tuple3<DateTime, Boolean, Boolean>> dates = JTimeUtils.extractDatesInYearMonth(cra.year, cra.month, partTime.startDate, partTime.dayOfWeek, partTime.momentOfDay, partTime.frequency);
			for (F.Tuple3<DateTime, Boolean, Boolean> d : dates) {
				JDay.addPartTime(cra.id, cra.userId, d._1, d._2, d._3);
			}
		}
		return cra;
	}

	public static JCra validate(final ObjectId userId, final Integer year, final Integer month) {
		UpdateOperations<JCra> op = MorphiaPlugin.ds().createUpdateOperations(JCra.class)
			                            .set("isValidated", true);
		return MorphiaPlugin.ds().findAndModify(queryByUserId(userId), op);
	}

	public static JCra invalidate(final ObjectId userId, final Integer year, final Integer month) {
		UpdateOperations<JCra> op = MorphiaPlugin.ds().createUpdateOperations(JCra.class)
			                            .set("isValidated", false);
		return MorphiaPlugin.ds().findAndModify(queryByUserId(userId), op);
	}

	public static JCra getOrCreate(final ObjectId userId, final Integer year, final Integer month) {
		final JCra cra = find(userId, year, month);
		if (cra != null) {
			return applyPartTime(cra);
		} else {
			return create(userId, year, month);
		}
	}

	public static JCra getOrCreate(final ObjectId craId, final ObjectId userId, final Integer year, final Integer month) {
		if (craId == null) {
			return create(userId, year, month);
		} else {
			return applyPartTime(queryToFindMe(craId).get());
		}
	}

	public static JCra find(final ObjectId userId, final Integer year, final Integer month) {
		return queryByUserYearMonth(userId, year, month)
			       .get();
	}

	public static JCra create(final ObjectId userId, final Integer year, final Integer month) {
		JCra cra = new JCra(userId, year, month);
		return applyPartTime(cra.<JCra>insert());
	}

	public static void delete(final ObjectId id) {
		MorphiaPlugin.ds().delete(queryToFindMe(id), WriteConcern.ACKNOWLEDGED);
	}

	public static void delete(final ObjectId userId, final Integer year, final Integer month) {
		MorphiaPlugin.ds().delete(queryByUserYearMonth(userId, year, month), WriteConcern.ACKNOWLEDGED);
	}
}
