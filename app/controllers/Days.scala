package controllers

import models.Day
import play.api.mvc.Action
import security.{MyDeadboltHandler, SecurityRole}
import play.api.Logger
import play.api.libs.json.{Json, JsString}


/**
 * @author f.patin
 */
object Days extends BaseController {

	def create = Restrictions(List(Array(SecurityRole.user), Array(SecurityRole.production), Array(SecurityRole.administrator)), new MyDeadboltHandler()) {
		Action(parse.json) {
			request =>
				Ok("it works !")
		}
	}

	def fetch(trigramme: String, year: Int, month: Int, day: Int) = Restrictions(List(Array(SecurityRole.user), Array(SecurityRole.production), Array(SecurityRole.administrator)),
		new MyDeadboltHandler()) {
		Action {
			request =>
				Async {
					Day.fetch("51199fa58ba867c9874680f2", 2013, 3, 1).map{
						day =>
							Ok(Json.toJson(day))
					}
					.recover{
						case e =>
							Logger.error("authenticate", e)
							InternalServerError(JsString(s"exception ${e.getMessage}"))
					}
				}
		}
	}

}
