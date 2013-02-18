package controllers

import play.api.mvc.{Action, Controller}

/**
 * @author f.patin
 */
object HtmlPages extends Controller {

	def unsupportedBrowser = Action {
		Ok(views.html.layout.unsupportedBrowser())
	}
	def paneTemplate = Action {
		Ok(views.html.template.pane())
	}
	def tabsTemplate = Action {
		Ok(views.html.template.tabs())
	}
}
