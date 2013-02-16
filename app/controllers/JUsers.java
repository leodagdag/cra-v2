package controllers;

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
		return ok(toJson(JUser.byRole(SecurityRole.employee())));
	}
}
