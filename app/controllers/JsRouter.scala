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

					Days.remove,
					Days.removeHalfDay,

					Cras.invalidate,
					Cras.validate,

					JCras.fetch,

					JDays.fetch,
					JDays.create,

					JAbsences.create,
					JAbsences.remove,
					JAbsences.history,

					JClaims.history,
					JClaims.create,
					JClaims.remove,

					JMissions.absences,
					JMissions.claims,
					JMissions.affectedMissions,

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
