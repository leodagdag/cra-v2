package controllers

import models.Day
import play.api.Logger
import play.api.libs.json.{Json, JsString}
import play.api.mvc.Action
import security.MyDeadboltHandler
import reactivemongo.core.commands.LastError


/**
 * @author f.patin
 */
object Days extends BaseController {

	def remove(craId: String, date: Long) = Restrict(everybody, new MyDeadboltHandler()) {
		Action {
			implicit request =>
				Async {
					Day.delete(craId, date).map {
						(lastError: LastError) =>
							if (lastError.ok) {
								Ok("Journée supprimée")
							} else {
								InternalServerError(Json.toJson(lastError.errMsg))
							}
					}.recover {
						case e =>
							Logger.error("delete", e)
							InternalServerError(JsString(s"exception ${e.getMessage}"))
					}
				}
		}
	}

	def removeHalfDay(craId: String, date: Long, momentOfDay: String) = Restrict(everybody, new MyDeadboltHandler()) {
		Action {
			implicit request =>
				Async {
					Day.delete(craId, date, momentOfDay).map {
						(lastError: LastError) =>
							if (lastError.ok) {
								Ok("Demi-journée supprimée")
							} else {
								InternalServerError(Json.toJson(lastError.errMsg))
							}
					}.recover {
						case e =>
							Logger.error("deleteHalfDay", e)
							InternalServerError(JsString(s"exception ${e.getMessage}"))
					}
				}
		}
	}
}
