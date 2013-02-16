package models;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.UpdateOperations;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;

/**
 * @author f.patin
 */
@Entity("Cra")
public class JCra {

	@Id
	public ObjectId id;

	public Integer year;

	public Integer month;

	public ObjectId userId;

	public String comment;

	public Boolean isValidated = Boolean.FALSE;

	public JCra() {
	}

	public JCra(final Integer year, final Integer month) {
		this.year = year;
		this.month = month;
	}

	private static Query<JCra> queryByUserId(final ObjectId userId){
		return MorphiaPlugin.ds().createQuery(JCra.class).field("userId").equal(userId);
	}
	public static JCra find(final ObjectId userId, final Integer year, final Integer month) {
		return queryByUserId(userId)
			.field("year").equal(year)
			.field("month").equal(month)
			.get();
	}

	public static JCra validate(final ObjectId userId, final Integer year, final Integer month){
		UpdateOperations<JCra> op = MorphiaPlugin.ds().createUpdateOperations(JCra.class)
			.set("isValidated", true);
		return MorphiaPlugin.ds().findAndModify(queryByUserId(userId),op);
	}

	public static JCra invalidate(final ObjectId userId, final Integer year, final Integer month){
		UpdateOperations<JCra> op = MorphiaPlugin.ds().createUpdateOperations(JCra.class)
			.set("isValidated", false);
		return MorphiaPlugin.ds().findAndModify(queryByUserId(userId),op);
	}
}
