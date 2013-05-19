package models;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Index;
import com.github.jmkgreen.morphia.annotations.Indexes;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import leodagdag.play2morphia.Model;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;

/**
 * @author f.patin
 */
@Entity("Customer")
@Indexes({
	         @Index(value = "code", unique = true),
	         @Index("isGenesis")
})
public class JCustomer extends Model {

	@Id
	public ObjectId id;
	public String code;
	public String name;
	public ObjectId finalCustomerId;
	public Boolean isGenesis = Boolean.FALSE;

	public JCustomer() {
	}

	private static Query<JCustomer> q() {
		return MorphiaPlugin.ds().createQuery(JCustomer.class);
	}

	private static Query<JCustomer> queryToFindMe(final ObjectId id) {
		return q().field(Mapper.ID_KEY).equal(id);
	}

	public static JCustomer genesis() {
		return q()
			       .field("isGenesis").equal(true)
			       .get();
	}

	public static JCustomer fetch(final ObjectId id) {
		return queryToFindMe(id).get();
	}

	public static JCustomer byCode(final String code) {
		return q().field("code").equal(code).get();
	}
	public static boolean exist(final String code) {
		return q().field("code").equal(code).countAll() > 1;
	}
}
