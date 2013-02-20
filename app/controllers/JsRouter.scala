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
					JUsers.employees,
					JUsers.affectedMissions

				)
			).as("text/javascript")
	}
}
