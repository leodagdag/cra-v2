package controllers

import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{text, optional, tuple, nonEmptyText}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Action, Security, Results}
import security.{Profile, MyDeadboltHandler, Auth}
import utils.MD5
import http.CacheControl
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.json.BSONFormats._
import play.api.libs.json.JsString
import scala.Some

/**
 * @author f.patin
 */
object Authentication extends BaseController {

  def login = Action {
    implicit request =>
      Ok(views.html.login())
  }

  def logout = Action {
    request =>
      Results.Redirect(routes.Authentication.login()).withSession(request.session - Security.username)
  }

  val authenticateForm = Form(tuple(
    "username" -> nonEmptyText,
    "password" -> nonEmptyText,
    "redirect" -> optional(text)
  ))

  def authenticate = Action {
    implicit request =>
      authenticateForm.bindFromRequest.fold(
        withErrors => {
          Results.Redirect(routes.Authentication.login())
            .withSession(request.session - Security.username)
            .flashing(("errormsg" -> "Utilisateur ou mot de passe incorrect"))
        },
        form => {
          Async {
            Auth.checkAuthentication((form._1, MD5(form._2))).map {
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

  val fromProfile: OWrites[Profile] = (
    (__ \ "id").writeNullable[String] and
      (__ \ "username").write[String] and
      (__ \ "role").write[String]
    )(unlift(Profile.unapply))

  def profile = Restrict(everybody, new MyDeadboltHandler()) {
    Action {
      request =>
        Async {
          Profile(request.session.get("username").get)
            .map {
            profile => Ok(Json.toJson(profile.get)(fromProfile)).withHeaders(profile.get.eTag, CacheControl.maxAgeO)
          }
            .recover {
            case e: Exception => InternalServerError(JsString(s"exception ${e.getMessage}"))
          }
        }
    }

  }
}
