package controllers

import models.Day
import play.api.Logger
import play.api.libs.json.{JsObject, Json, JsString}
import play.api.mvc.Action
import security.MyDeadboltHandler


/**
 * @author f.patin
 */
object Days extends BaseController {

	def create = Restrictions(everybody, new MyDeadboltHandler()) {
		Action(parse.json) {
			request =>
				Ok("it works !")
		}
	}

	def fetch(username: String, year: Int, month: Int, day: Int) = Restrictions(everybody, new MyDeadboltHandler()) {
		Action {
			request =>
				Async {
					Day.fetch("51199fa58ba867c9874680f2", 2013, 3, 1)
						.map {
						(day: Option[JsObject]) =>
							Ok(Json.toJson(day))
					}
						.recover {
						case e =>
							Logger.error("authenticate", e)
							InternalServerError(JsString(s"exception ${e.getMessage}"))
					}
				}
		}
	}

}
