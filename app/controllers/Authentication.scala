package controllers

import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{text, optional, tuple, nonEmptyText}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, JsString, __}
import play.api.mvc.{Action, Security, Results}
import scala.Some
import security.{Profile, MyDeadboltHandler, Auth}

/**
 * @author f.patin
 */
object Authentication extends BaseController {

	def login = Action {
		implicit request =>
			Ok(views.html.login())
	}

	val authenticateForm = Form(tuple(
		"username" -> nonEmptyText,
		"password" -> nonEmptyText,
		"redirect" -> optional(text)
	))

	def authenticate = Action {
		implicit request =>
			authenticateForm.bindFromRequest.fold(
				withErrors => BadRequest("KO"),
				form => {
					Async {
						Auth.checkAuthentication((form._1, form._2)).map {
							check =>
								check match {
									case Some(auth) =>
										form._3.map(next => Results.Redirect(routes.Application.index() + "#" + next))
											.getOrElse(Results.Redirect(routes.Application.index()))
											.withSession(request.session + (Security.username -> auth.username))
									case None => Results.Redirect(routes.Authentication.login())
										.withSession(request.session - Security.username)
										.flashing(("errormsg" -> "Utilisateur ou mot de passe incorrect"))
								}
						}.recover {
							case e =>
								Logger.error("authenticate", e)
								InternalServerError(JsString(s"exception ${e.getMessage}"))
						}
					}
				}
			)

	}

	def logout = Action {
		request =>
			Results.Redirect(routes.Authentication.login()).withSession(request.session - Security.username)
	}

	val fromProfile = (
		(__ \ "id").write[String] and
		(__ \ "username").write[String] and
			(__ \ "role").write[String]
		)(unlift(Profile.unapply))

	def profile = Restrictions(everybody, new MyDeadboltHandler()) {
		Action {
			request =>
				Async {
					Profile(request.session.get("username").get)
						.map {
						(profile: Option[Profile]) => Ok(Json.toJson(profile.get)(fromProfile))
					}
						.recover {
						case e: Throwable => InternalServerError(JsString(s"exception ${e.getMessage}"))
					}
				}
		}

	}
}
