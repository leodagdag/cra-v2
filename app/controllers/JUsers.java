package controllers;

import dto.AffectedMissionDTO;
import dto.MissionDTO;
import http.ResponseCache;
import dto.EmployeeDTO;
import dto.ManagerDTO;
import models.JAffectedMission;
import models.JMission;
import models.JUser;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import play.mvc.Controller;
import play.mvc.Result;
import security.SecurityRole;

import java.util.Collection;
import java.util.List;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JUsers extends Controller {

	@ResponseCache.NoCacheResponse
	public static Result employees() {
		return ok(toJson(EmployeeDTO.of(JUser.byRole(SecurityRole.employee()))));
	}

	@ResponseCache.NoCacheResponse
	public static Result managers() {
		return ok(toJson(ManagerDTO.of(JUser.managers())));
	}

	@ResponseCache.NoCacheResponse
	public static Result all() {
		return ok(toJson(EmployeeDTO.of(JUser.all())));
	}

    @ResponseCache.NoCacheResponse
    public static Result one(final String username) {
        return ok(toJson(EmployeeDTO.of(JUser.fetch(username))));
    }

    @ResponseCache.NoCacheResponse
    public static Result affectedMissions(final String username, final Long startDate, final Long endDate) {
        final List<JAffectedMission> affectedMissions =  JUser.affectedMissions(JUser.id(username), new DateTime(startDate), new DateTime(endDate));
        return ok(toJson(AffectedMissionDTO.of(affectedMissions)));
    }

    @ResponseCache.NoCacheResponse
    public static Result allAffectedMissions(final String username) {
        return affectedMissions(username, null, null);
    }
}
