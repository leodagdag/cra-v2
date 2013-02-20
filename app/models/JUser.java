package models;


import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import com.github.jmkgreen.morphia.annotations.Embedded;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Index;
import com.github.jmkgreen.morphia.annotations.Indexes;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import security.JSecurityRole;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author f.patin
 */
@Entity("User")
@Indexes({
	         @Index("username"),
	         @Index("username, password"),
	         @Index("lastName, firstName")
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
	@Embedded
	public List<JAffectedMission> affectedMissions = Lists.newArrayList();

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

	private static Query<JUser> queryToFindMe(final String username) {
		return MorphiaPlugin.ds().createQuery(JUser.class).field("username").equal(username);
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

	public static ObjectId idByUsername(final String username) {
		return queryToFindMe(username)
			       .retrievedFields(true, "id")
			       .disableValidation()
			       .get().id;
	}

	public static List<JUser> byRole(final String role) {
		return MorphiaPlugin.ds().createQuery(JUser.class)
			       .field("role").equal(role)
			       .retrievedFields(true, "username", "firstName", "lastName", "role")
			       .disableValidation()
			       .order("lastName, firstName")
			       .asList();
	}

	public static ImmutableMap<ObjectId, JMission> affectedMissions(final String username, final Long start, final Long end) {
		final DateTime startDate = new DateTime(start);
		final DateTime endDate = new DateTime(end);
		final JUser user = queryToFindMe(username)
			                   .retrievedFields(true, "affectedMissions")
			                   .disableValidation()
			                   .get();
		final List<JAffectedMission> affectedMissions = Lists.newArrayList(Iterables.filter(user.affectedMissions, new Predicate<JAffectedMission>() {
			@Override
			public boolean apply(@Nullable final JAffectedMission affectedMission) {
				if ((affectedMission.startDate == null || affectedMission.startDate.isBefore(startDate))
					    && (affectedMission.endDate == null || affectedMission.endDate.isAfter(endDate))) {
					return true;
				}
				return false;
			}
		}));
		final List<ObjectId> affectedMissionIds = Lists.newArrayList(Collections2.transform(affectedMissions, new Function<JAffectedMission, ObjectId>() {
			@Nullable
			@Override
			public ObjectId apply(@Nullable final JAffectedMission am) {
				return am.missionId;
			}
		}));
		return JMission.codeAndMissionType(affectedMissionIds, false);
	}
}
