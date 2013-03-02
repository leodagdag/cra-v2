package controllers;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import dto.ClaimDTO;
import models.JAbsence;
import models.JClaim;
import models.JMission;
import models.JUser;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import play.data.Form;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JClaims extends Controller {

	public static Result fetch(final String username, final Integer year, final Integer month) {
		final ObjectId userId = JUser.id(username);
		final ImmutableList<JClaim> claims = JClaim.forUserId(userId, year, month);
		final List<ObjectId> missionIds = Lists.newArrayList(Collections2.transform(claims, new Function<JClaim, ObjectId>() {
			@Nullable
			@Override
			public ObjectId apply(@Nullable final JClaim claim) {
				return claim.missionId;
			}
		}));
		final ImmutableMap<ObjectId, JMission> missions = JMission.codeAndMissionType(missionIds);
		return ok(toJson(ClaimDTO.of(claims, missions.values().asList())));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result create(){
		final Form<CreateClaimForm> form = Form.form(CreateClaimForm.class).bind(request().body().asJson());
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		}

		final CreateClaimForm createClaimForm = form.get();
		final JClaim claim = createClaimForm.to();
		return ok(toJson(ClaimDTO.of(JClaim.create(claim))));


	}
	public static class CreateClaimForm {

		public String username;
		public String missionId;
		public Long date;
		public String claimType;
		public String amount;
		public String kilometer;
		public String journey;
		public String comment;

		public JClaim to() {
			final JClaim claim = new JClaim();
			claim.userId = JUser.id(this.username);
			claim.missionId = ObjectId.massageToObjectId(this.missionId);
			claim.date = new DateTime(this.date);
			claim.claimType = this.claimType;
			claim.amount = new BigDecimal(this.amount);
			claim.kilometer = new BigDecimal(this.kilometer);
			claim.journey = this.journey;
			claim.comment = this.comment;
			return claim;
		}
	}

}
