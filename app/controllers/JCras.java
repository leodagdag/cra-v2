package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import caches.ResponseCache;
import com.google.common.collect.Lists;
import constants.ClaimType;
import dto.CraDTO;
import models.*;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import security.JDeadboltHandler;
import security.JSecurityRoles;
import utils.business.JClaimUtils;
import utils.time.TimeUtils;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JCras extends Controller {

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

	private static String title(final String craId, final F.Option<String> missionId) {
		final JCra cra = JCra.fetch(craId);
		final JUser user = JUser.account(cra.userId);
		final DateTime dt = TimeUtils.firstDateOfMonth(cra.year, cra.month);
		final StringBuilder title = new StringBuilder()
			                            .append(user.trigramme)
			                            .append("_")
			                            .append(dt.toString("yyyy_MMMM").toLowerCase());
		if(missionId.isDefined()) {
			final JMission mission = JMission.codeAndMissionType(ObjectId.massageToObjectId(missionId.get()));
			title.append("_")
				.append(mission.label);
		}
		return title.toString().replaceAll("[/ ()]", "");

	}

	@ResponseCache.NoCacheResponse
	public static Result exportByMission(final String craId, final String missionId) {
		return redirect(routes.JExports.exportByMission(craId, missionId, title(craId, new F.Some<>(missionId))));
	}

}
