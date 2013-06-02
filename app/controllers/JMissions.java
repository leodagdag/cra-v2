package controllers;

import http.ResponseCache;
import dto.MissionDTO;
import models.JMission;
import models.JUser;
import org.joda.time.DateTime;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Collection;
import java.util.List;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JMissions extends Controller {

	@ResponseCache.NoCacheResponse
	public static Result absences() {
		final List<JMission> missions = JMission.getAbsencesMissions();
		return ok(toJson(MissionDTO.of(missions)));
	}

	@ResponseCache.NoCacheResponse
	public static Result claimable(final String username) {
		final List<JMission> missions = JMission.getClaimableMissions(JUser.affectedMissions(username));
		return ok(toJson(MissionDTO.of(missions)));
	}



	@ResponseCache.NoCacheResponse
	public static Result craMissions(final String username, final Long startDate, final Long endDate) {
		final List<JMission> missions = JMission.craMissions(JUser.affectedMissions(username, new DateTime(startDate), new DateTime(endDate)));
		return ok(toJson(MissionDTO.of(missions)));
	}

}
