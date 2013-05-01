package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.google.common.collect.Lists;
import constants.ClaimType;
import dto.CraDTO;
import export.PDF;
import http.ResponseCache;
import mail.CraBody;
import mail.MailerCra;
import models.DbFile;
import models.JClaim;
import models.JCra;
import models.JDay;
import models.JMission;
import models.JUser;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import play.data.Form;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import security.JDeadboltHandler;
import security.JSecurityRoles;
import utils.business.JClaimUtils;
import utils.time.TimeUtils;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JCras extends Controller {

	private static String title(final String craId, final F.Option<String> missionId) {
		final JCra cra = JCra.fetch(craId);
		final JUser user = JUser.account(cra.userId);
		final DateTime dt = TimeUtils.firstDateOfMonth(cra.year, cra.month);
		final StringBuilder title = new StringBuilder()
			                            .append("CRA")
			                            .append("_")
			                            .append(user.fullName())
			                            .append("_")
			                            .append(dt.toString("yyyy_MMMM", Locale.FRANCE).toLowerCase());
		if(missionId.isDefined()) {
			final JMission mission = JMission.codeAndMissionType(ObjectId.massageToObjectId(missionId.get()));
			title.append("_")
				.append(mission.label);
		}
		title.append(".pdf");
		return title.toString().replaceAll("[/ ()]", "");

	}

	@Restrict(value = {@Group(JSecurityRoles.role_employee), @Group(JSecurityRoles.role_production), @Group(JSecurityRoles.role_admin)}, handler = JDeadboltHandler.class)
	@ResponseCache.NoCacheResponse
	public static Result fetch(final String username, final Integer year, final Integer month) {
		final ObjectId userId = JUser.id(username);
		final JCra cra = JCra.getOrCreate(userId, year, month);
		final List<JDay> jDays = Lists.newArrayList();
		final List<ObjectId> missionsIds = Lists.newArrayList();

		jDays.addAll(JDay.find(cra.id, userId, year, month, true));
		for(JDay jDay : jDays) {
			missionsIds.addAll(jDay.missionIds());
		}
		final Map<ObjectId, JMission> jMissions = JMission.codeAndMissionType(missionsIds);
		return ok(toJson(CraDTO.of(cra, jDays, jMissions)));
	}

	@Restrict(value = {@Group(JSecurityRoles.role_employee), @Group(JSecurityRoles.role_production), @Group(JSecurityRoles.role_admin)}, handler = JDeadboltHandler.class)
	@ResponseCache.NoCacheResponse
	public static Result claimSynthesis(final String userId, final Integer year, final Integer month) {
		final List<JClaim> claims = JClaim.synthesis(userId, year, month);
		final Map<String, Map<ClaimType, String>> synthesis = JClaimUtils.synthesis(year, month, claims);
		return ok(toJson(synthesis));
	}

	@ResponseCache.NoCacheResponse
	public static Result exportByEmployee(final String craId) {
		return redirect(routes.JExports.exportByEmployee(craId, title(craId, new F.None<String>())));
	}

	@ResponseCache.NoCacheResponse
	public static Result exportByMission(final String craId, final String missionId) {
		return redirect(routes.JExports.exportByMission(craId, missionId, title(craId, new F.Some<>(missionId))));
	}

	@ResponseCache.NoCacheResponse
	public static Result send(final String craId) {
		final JCra cra = JCra.fetch(craId);
		if(cra.fileId != null) {
			DbFile.remove(cra.fileId);
		}
		final JUser user = JUser.identity(cra.userId);
		final File file = PDF.createProductionCraFile(cra, user);
		final DateTime date = MailerCra.send(cra, user, file, CraBody.cra(cra, user));
		JCra.updateSentDate(cra.id, date);
		return ok(toJson(date.getMillis()));
	}

	@ResponseCache.NoCacheResponse
	public static Result sent(final String craId) {
		final JCra cra = JCra.fetch(craId);
		return redirect(routes.JExports.exportForProduction(cra.fileId.toString(), title(craId, new F.None<String>())));
	}

	public static Result setComment(final String id) {
		final Form<String> form = Form.form(String.class).bind(request().body().asJson());
		final String result = JCra.comment(id, form.data().get("comment"));
		return result == null ? created() : created(result);
	}

}
