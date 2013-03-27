package controllers;

import caches.ResponseCache;
import dto.EmployeeDTO;
import dto.ManagerDTO;
import models.JUser;
import play.mvc.Controller;
import play.mvc.Result;
import security.SecurityRole;

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
		return ok(toJson(JUser.all()));
	}
}
