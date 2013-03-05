package models;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.UpdateOperations;
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;

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

	public static JCra validate(final ObjectId userId, final Integer year, final Integer month) {
		UpdateOperations<JCra> op = MorphiaPlugin.ds().createUpdateOperations(JCra.class)
			                            .set("isValidated", true);
		return MorphiaPlugin.ds().findAndModify(queryByUserId(userId), op);
	}

	private static Query<JCra> queryByUserId(final ObjectId userId) {
		return MorphiaPlugin.ds().createQuery(JCra.class).field("userId").equal(userId);
	}

	private static Query<JCra> queryToFindMe(final ObjectId id) {
		return MorphiaPlugin.ds().createQuery(JCra.class).field(Mapper.ID_KEY).equal(id);
	}

	public static JCra invalidate(final ObjectId userId, final Integer year, final Integer month) {
		UpdateOperations<JCra> op = MorphiaPlugin.ds().createUpdateOperations(JCra.class)
			                            .set("isValidated", false);
		return MorphiaPlugin.ds().findAndModify(queryByUserId(userId), op);
	}

	public static JCra getOrCreate(final ObjectId userId, final Integer year, final Integer month) {
		final JCra cra = find(userId, year, month);
		if (cra != null) {
			return cra;
		} else {
			return create(userId, year, month);
		}
	}

	public static JCra create(final ObjectId userId, final Integer year, final Integer month) {
		JCra cra = new JCra(userId, year, month);
		return cra.insert();
	}

	public static JCra find(final ObjectId userId, final Integer year, final Integer month) {
		return queryByUserId(userId)
			       .field("year").equal(year)
			       .field("month").equal(month)
			       .get();
	}

	public static JCra getOrCreate(final ObjectId id, final ObjectId userId, final Integer year, final Integer month) {
		if (id == null) {
			return create(userId, year, month);
		} else {
			return queryToFindMe(id).get();
		}
	}

}
