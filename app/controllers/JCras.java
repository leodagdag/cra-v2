package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import dto.CraDTO;
import models.JCra;
import models.JDay;
import models.JMission;
import models.JUser;
import org.bson.types.ObjectId;
import play.mvc.Controller;
import play.mvc.Result;
import security.JDeadboltHandler;
import security.JSecurityRoles;

import java.util.List;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JCras extends Controller {

	@Restrict(value = {@Group(JSecurityRoles.role_user), @Group(JSecurityRoles.role_production), @Group(JSecurityRoles.role_admin)}, handler = JDeadboltHandler.class)
	public static Result fetch(final String trigramme, final Integer year, final Integer month) {
		final JUser user = JUser.idByTrigramme(trigramme);
		final JCra cra = JCra.find(user.id, year, month);
		final List<JDay> jDays = JDay.find(cra.id, year, month, true);
		final List<ObjectId> missionsIds = Lists.newArrayList();
		for (JDay jDay : jDays) {
			missionsIds.addAll(jDay.missionIds());
		}
		final ImmutableMap<ObjectId, JMission> jMissions = JMission.codeAndMissionType(missionsIds);

		return ok(toJson(CraDTO.of(cra, jDays, jMissions)));
	}
}
