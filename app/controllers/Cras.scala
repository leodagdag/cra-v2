package controllers

import models.Cra
import play.api.Logger
import play.api.libs.json.{JsString, Json}
import play.api.mvc.Action
import reactivemongo.core.commands.LastError
import security.MyDeadboltHandler


/**
 * @author f.patin
 */
object Cras extends BaseController {

  def validate(id: String) = Restrict(everybody, new MyDeadboltHandler()) {
    Action {
      implicit request =>
        Async {
          Cra.validate(id).map {
            (lastError: LastError) =>
              if (lastError.ok) {
                Ok("Cra validé")
              } else {
                InternalServerError(Json.toJson(lastError.errMsg))
              }
          }.recover {
            case e =>
              Logger.error("validate", e)
              InternalServerError(JsString(s"exception ${e.getMessage}"))
          }
        }
    }
  }

  def invalidate(id: String) = Restrict(everybody, new MyDeadboltHandler()) {
    Action {
      implicit request =>
        Async {
          Cra.invalidate(id).map {
            (lastError: LastError) =>
              if (lastError.ok) {
                Ok("Cra validé")
              } else {
                InternalServerError(Json.toJson(lastError.errMsg))
              }
          }.recover {
            case e =>
              Logger.error("validate", e)
              InternalServerError(JsString(s"exception ${e.getMessage}"))
          }
        }
    }
  }
}

