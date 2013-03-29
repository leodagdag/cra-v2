package models;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import com.github.jmkgreen.morphia.annotations.*;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.UpdateOperations;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mongodb.WriteConcern;
import leodagdag.play2morphia.MorphiaPlugin;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import security.JSecurityRole;
import utils.MD5;

import javax.annotation.Nullable;
import java.util.Collection;
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
	public String trigramme;
	public String firstName;
	public String lastName;
	public String email;
	public String role;
	public ObjectId managerId;
	public Boolean isManager = Boolean.FALSE;
	@Embedded
	public List<JAffectedMission> affectedMissions = Lists.newArrayList();
	private String password;

	private static Query<JUser> queryToFindMe(final ObjectId id) {
		return MorphiaPlugin.ds().createQuery(JUser.class).field(Mapper.ID_KEY).equal(id);
	}

	private static Query<JUser> queryToFindMe(final String username) {
		return MorphiaPlugin.ds().createQuery(JUser.class).field("username").equal(username);
	}

	public static Boolean checkAuthentication(final String username, final String password) {
		return MorphiaPlugin.ds().createQuery(JUser.class)
			       .field("username").equal(username)
			       .field("password").equal(MD5.apply(password))
			       .countAll() > 0;
	}

	public static Subject getSubject(final String username) {
		return MorphiaPlugin.ds().createQuery(JUser.class)
			       .field("username").equal(username)
			       .retrievedFields(true, "username", "role")
			       .disableValidation()
			       .get();
	}

	public static ObjectId id(final String username) {
		return queryToFindMe(username)
			       .retrievedFields(true, "id")
			       .disableValidation()
			       .get().id;
	}

	public static JUser account(final String id) {
		return queryToFindMe(ObjectId.massageToObjectId(id))
			       .retrievedFields(false, "role", "username", "password", "isManager")
			       .disableValidation()
			       .get();

	}

	public static JUser identity(final ObjectId id) {
		return queryToFindMe(id)
			       .retrievedFields(true, "firstName", "lastName")
			       .disableValidation()
			       .get();
	}

	public static ImmutableList<JUser> byRole(final String role) {
		return ImmutableList.copyOf(MorphiaPlugin.ds().createQuery(JUser.class)
			                            .field("role").equal(role)
			                            .retrievedFields(true, "username", "firstName", "lastName", "role")
			                            .disableValidation()
			                            .order("lastName, firstName")
			                            .asList());
	}

	public static ImmutableList<ObjectId> affectedMissions(final String username, final Long start, final Long end) {

		final JUser user = queryToFindMe(username)
			                   .retrievedFields(true, "affectedMissions")
			                   .disableValidation()
			                   .get();
		final Collection<JAffectedMission> affectedMissions = Lists.newArrayList();
		if(start != null && end != null) {
			final DateTime startDate = new DateTime(start);
			final DateTime endDate = new DateTime(end);
			affectedMissions.addAll(Collections2.filter(user.affectedMissions, new Predicate<JAffectedMission>() {
				@Override
				public boolean apply(@Nullable final JAffectedMission affectedMission) {
					return ((affectedMission.startDate == null || affectedMission.startDate.isBefore(startDate))
						        && (affectedMission.endDate == null || affectedMission.endDate.isAfter(endDate)));
				}
			}));

		} else {
			affectedMissions.addAll(user.affectedMissions);
		}

		return ImmutableList.copyOf(Collections2.transform(affectedMissions, new Function<JAffectedMission, ObjectId>() {
			@Nullable
			@Override
			public ObjectId apply(@Nullable final JAffectedMission am) {
				return am.missionId;
			}
		}));
	}

	public static ImmutableList<JUser> all() {
		return ImmutableList.copyOf(MorphiaPlugin.ds().createQuery(JUser.class)
			                            .retrievedFields(false, "affectedMissions")
			                            .disableValidation()
			                            .asList());
	}

	public static ImmutableList<JUser> managers() {
		return ImmutableList.copyOf(MorphiaPlugin.ds().createQuery(JUser.class)
			                            .field("isManager").equal(Boolean.TRUE)
			                            .retrievedFields(true, Mapper.ID_KEY, "lastName", "firstName")
			                            .disableValidation()
			                            .asList());
	}

	public static JUser update(final JUser user) {
		MorphiaPlugin.ds().merge(user, WriteConcern.ACKNOWLEDGED);
		return user;
	}

	public static void password(final String username, final String newPassword) {
		final UpdateOperations<JUser> uop = MorphiaPlugin.ds().createUpdateOperations(JUser.class).set("password", MD5.apply(newPassword));
		MorphiaPlugin.ds().update(queryToFindMe(username), uop, false, WriteConcern.ACKNOWLEDGED);
	}

	@Override
	@JsonIgnore
	public List<? extends Role> getRoles() {
		return Lists.newArrayList(new JSecurityRole(role));
	}

	@Override
	@JsonIgnore
	public List<? extends Permission> getPermissions() {
		return null;
	}

	@Override
	@JsonIgnore
	public String getIdentifier() {
		return username;
	}
}
