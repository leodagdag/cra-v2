package models;


import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.google.common.collect.Lists;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;
import security.SecurityRole;

import java.util.List;

/**
 * @author f.patin
 */
@Entity
public class User implements Subject {


	@Id
	public ObjectId id;
	public String username;
	public String password;
	public String role;

	@Override
	public List<? extends Role> getRoles() {
		return Lists.newArrayList(new SecurityRole(role));
	}

	@Override
	public List<? extends Permission> getPermissions() {
		return null;
	}

	@Override
	public String getIdentifier() {
		return null;
	}

	private static Query<User> queryToFindMe(final ObjectId id) {
		return MorphiaPlugin.ds().createQuery(User.class).field(Mapper.ID_KEY).equal(id);
	}

	private static Query<User> queryToFindMe(final String username) {
		return MorphiaPlugin.ds().createQuery(User.class).field("username").equal(username);
	}

	public static User findAuthorisedUser(final String username, final String password) {
		return queryToFindMe(username)
			.field("password").equal(password)
			.retrievedFields(true, "username", "password", "role")
			.disableValidation()
			.get();
	}

	public static User getSubject(final String username) {
		return queryToFindMe(username)
			.retrievedFields(true, "username", "password", "role")
			.disableValidation()
			.get();
	}
}
