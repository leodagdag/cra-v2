package controllers

import play.api.Routes
import play.api.mvc.{Action, Controller}

/**
 * @author f.patin
 */
object JsRouter extends Controller {
  def javascriptRoutes = Action {
    implicit request =>
      import routes.javascript._
      Ok(
        Routes.javascriptRouter("jsRoutes")(
          Authentication.profile,

          Cras.invalidate,
          Cras.validate,

          JCras.fetch,
          JCras.claimSynthesis,
          JCras.exportByEmployee,
          JCras.exportByMission,
          JCras.send,
          JCras.sent,

          JDays.fetch,
          JDays.create,
          JDays.remove,
          JDays.removeHalfDay,

          JAbsences.create,
          JAbsences.remove,
          JAbsences.history,
          JAbsences.send,
          JAbsences.exportFile,

          JClaims.history,
          JClaims.create,
          JClaims.remove,

          JMissions.absences,
          JMissions.claimable,
          JMissions.affectedMissions,
          JMissions.craMissions,

          JUsers.employees,
          JUsers.managers,
          JUsers.all,

          JAccounts.fetch,
          JAccounts.update,
          JAccounts.password,

          JPartTimes.active,
          JPartTimes.history,
          JPartTimes.addPartTimes,
          JPartTimes.deactivate,

          JVehicles.active,
          JVehicles.history,
          JVehicles.deactivate,
          JVehicles.save

        )
      ).as("text/javascript")
  }
}
