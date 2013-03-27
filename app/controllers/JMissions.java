package controllers;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import dto.MissionDTO;
import models.JMission;
import models.JUser;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JMissions extends Controller {

	public static Result absences() {
		final ImmutableList<JMission> missions = JMission.getAbsencesMissions();
		return ok(toJson(MissionDTO.of(missions)));
	}

	public static Result claimable(final String username) {
		final ImmutableList<JMission> missions = JMission.getClaimableMissions(JUser.affectedMissions(username, null, null));
		return ok(toJson(MissionDTO.of(missions)));
	}

	public static Result affectedMissions(final String username, final Long startDate, final Long endDate) {
		final ImmutableCollection<JMission> missions = JMission.codeAndMissionType(JUser.affectedMissions(username, startDate, endDate)).values();
		return ok(toJson(MissionDTO.of(missions)));
	}
}
