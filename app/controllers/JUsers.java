package controllers;

import com.google.common.collect.ImmutableList;
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

	public static Result employees() {
		return ok(toJson(EmployeeDTO.of(JUser.byRole(SecurityRole.employee()))));
	}

	public static Result managers(){
		return ok(toJson(ManagerDTO.of(JUser.managers())));
	}

	public static Result all() {
		return ok(toJson(JUser.all()));
	}
}
