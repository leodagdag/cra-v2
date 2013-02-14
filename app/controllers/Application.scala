package controllers

import play.api.Routes
import play.api.mvc.Action
import security.MyDeadboltHandler


/**
 * @author f.patin
 */
object Application extends BaseController {

	def index = SubjectPresent(new MyDeadboltHandler) {
		Action {
			implicit request =>
				Ok(views.html.index(new MyDeadboltHandler()))
		}
	}

	def javascriptRoutes = Action {
		implicit request =>
			import routes.javascript._
			Ok(
				Routes.javascriptRouter("jsRoutes")(
					JCras.fetch
				)
			).as("text/javascript")
	}
}
