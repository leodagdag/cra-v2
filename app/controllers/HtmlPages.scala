package controllers

import play.api.mvc.{Action, Controller}

/**
 * @author f.patin
 */
object HtmlPages extends Controller {

	def unsupportedBrowser = Action {
		Ok(views.html.layout.unsupportedBrowser())
	}
}
