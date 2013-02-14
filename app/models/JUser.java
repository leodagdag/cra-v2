package models;


import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Index;
import com.github.jmkgreen.morphia.annotations.Indexes;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.google.common.collect.Lists;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;
import security.JSecurityRole;

import java.util.List;

/**
 * @author f.patin
 */
@Entity("User")
@Indexes({
	@Index("username"),
	@Index("username, password"),
	@Index("trigramme")
})
public class JUser implements Subject {


	@Id
	public ObjectId id;
	public String username;
	public String password;
	public String trigramme;
	public String firstName;
	public String lastName;
	public String email;
	public String role;

	@Override
	public List<? extends Role> getRoles() {
		return Lists.newArrayList(new JSecurityRole(role));
	}

	@Override
	public List<? extends Permission> getPermissions() {
		return null;
	}

	@Override
	public String getIdentifier() {
		return username;
	}

	private static Query<JUser> queryToFindMe(final ObjectId id) {
		return MorphiaPlugin.ds().createQuery(JUser.class).field(Mapper.ID_KEY).equal(id);
	}

	private static Query<JUser> queryToFindMe(final String trigramme) {
		return MorphiaPlugin.ds().createQuery(JUser.class).field("trigramme").equal(trigramme);
	}

	public static JUser findAuthorisedUser(final String username, final String password) {
		return MorphiaPlugin.ds().createQuery(JUser.class)
			.field("username").equal(username)
			.field("password").equal(password)
			.retrievedFields(true, "username", "password", "role")
			.disableValidation()
			.get();
	}

	public static Subject getSubject(final String username) {
		return MorphiaPlugin.ds().createQuery(JUser.class)
			.field("username").equal(username)
			.retrievedFields(true, "username", "role")
			.disableValidation()
			.get();
	}

	public static JUser idByTrigramme(final String trigramme) {
		return queryToFindMe(trigramme)
			.retrievedFields(true, "id")
			.disableValidation()
			.get();
	}
}
