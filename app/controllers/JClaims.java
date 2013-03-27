package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import caches.ResponseCache;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import constants.ClaimType;
import dto.ClaimDTO;
import models.JClaim;
import models.JMission;
import models.JUser;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import security.JDeadboltHandler;
import security.JSecurityRoles;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JClaims extends Controller {

	@Restrict(value = {@Group(JSecurityRoles.role_employee), @Group(JSecurityRoles.role_production), @Group(JSecurityRoles.role_admin)}, handler = JDeadboltHandler.class)
	@ResponseCache.NoCacheResponse
	public static Result history(final String userId, final Integer year, final Integer month) {
		final ImmutableList<JClaim> claims = JClaim.history(JUser.id(userId), year, month);
		final ImmutableList<ObjectId> missionIds = ImmutableList.copyOf(Collections2.transform(claims, new Function<JClaim, ObjectId>() {
			@Nullable
			@Override
			public ObjectId apply(@Nullable final JClaim claim) {
				return claim.missionId;
			}
		}));
		final ImmutableMap<ObjectId, JMission> missions = JMission.codeAndMissionType(missionIds);
		return ok(toJson(ClaimDTO.of(claims, missions.values().asList())));
	}

	@Restrict(value = {@Group(JSecurityRoles.role_employee), @Group(JSecurityRoles.role_production), @Group(JSecurityRoles.role_admin)}, handler = JDeadboltHandler.class)
	@BodyParser.Of(BodyParser.Json.class)
	public static Result create() {
		final Form<CreateClaimForm> form = Form.form(CreateClaimForm.class).bind(request().body().asJson());
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		}

		final CreateClaimForm createClaimForm = form.get();
		final List<JClaim> claim = createClaimForm.to();
		JClaim.create(claim);
		return created();

	}

	@Restrict(value = {@Group(JSecurityRoles.role_employee), @Group(JSecurityRoles.role_production), @Group(JSecurityRoles.role_admin)}, handler = JDeadboltHandler.class)
	@ResponseCache.NoCacheResponse
	public static Result remove(final String id) {
		return ok(toJson(JClaim.delete(id)));
	}

	public static class CreateClaimForm {

		public ObjectId userId;
		public ObjectId missionId;
		public Long date;
		public String claimType;
		public String amount;
		public String kilometer;
		public String journey;
		public String comment;

		public List<ValidationError> validate() {
			return null;
		}

		public List<JClaim> to() {
			final List<JClaim> claims = Lists.newArrayListWithExpectedSize(2);
			if (this.amount != null) {
				final JClaim claim = new JClaim();
				claim.userId = this.userId;
				claim.missionId = this.missionId;
				claim.date = new DateTime(this.date);
				claim.claimType = this.claimType;
				claim.amount = new BigDecimal(this.amount);
				claim.comment = this.comment;
				claims.add(claim);
			}
			if (this.kilometer != null) {
				final JClaim claim = new JClaim();
				claim.userId = userId;
				claim.missionId = this.missionId;
				claim.date = new DateTime(this.date);
				claim.kilometer = new BigDecimal(this.kilometer);
				claim.journey = this.journey;
				claim.comment = this.comment;
				claim.claimType = ClaimType.JOURNEY.name();
				claims.add(claim);
			}
			return claims;
		}
	}

}
