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
					Days.delete,
					Days.deleteHalfDay,
					Cras.invalidate,
					Cras.validate,

					JCras.fetch,

					JDays.fetch,
					JDays.create,

					JAbsences.create,
					JAbsences.delete,
					JAbsences.history,

					JClaims.history,
					JClaims.create,
					JClaims.delete,

					JMissions.absences,
					JMissions.claims,
					JMissions.affectedMissions,

					JUsers.employees,
					JUsers.managers,
					JUsers.all,

					JAccounts.fetch,
					JAccounts.update,
					JAccounts.password,

					JVehicles.saveVehicle

				)
			).as("text/javascript")
	}
}
