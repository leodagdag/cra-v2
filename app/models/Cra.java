package models;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;

/**
 * @author f.patin
 */
@Entity
public class Cra {

	@Id
	public ObjectId id;

	public Integer year;

	public Integer month;

	public ObjectId userId;

	public String comment;

	public Boolean validated;

	public static Cra find(final ObjectId userId, final Integer year, final Integer month) {
		return MorphiaPlugin.ds().createQuery(Cra.class)
			.field("userId").equal(userId)
			.field("year").equal(year)
			.field("month").equal(month)
			.get();
	}
}
