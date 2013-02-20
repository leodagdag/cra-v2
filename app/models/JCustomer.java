package models;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Index;
import com.github.jmkgreen.morphia.annotations.Indexes;
import org.bson.types.ObjectId;

/**
 * @author f.patin
 */
@Entity("Customer")
@Indexes({
	         @Index(value = "code", unique = true),
	         @Index("isGenesis")
})
public class JCustomer {

	@Id
	public ObjectId id;

	public String code;

	public String name;

	public ObjectId finalCustomerId;

	public Boolean isGenesis = Boolean.FALSE;
}
