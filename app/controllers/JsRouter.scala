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

          JCras.fetch,
          JCras.claimSynthesis,
          JCras.exportByEmployee,
          JCras.exportByMission,
          JCras.send,
          JCras.sent,
          JCras.setComment,

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
          JMissions.craMissions,
          JMissions.customerMissions,
          JMissions.customerByMissionId,
          JMissions.fetch,
          JMissions.save,

          JCustomers.all,
          JCustomers.withoutGenesis,
          JCustomers.fetch,
          JCustomers.save,

          JUsers.employees,
          JUsers.managers,
          JUsers.users,
          JUsers.all,
          JUsers.affectedMissions,
          JUsers.allAffectedMissions,
          JUsers.customerAffectedMissions,
          JUsers.saveAffectedMission,
          JUsers.fetch,
          JUsers.save,
          JUsers.resetPwd,

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
