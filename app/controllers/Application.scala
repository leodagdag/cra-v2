package controllers

import play.api.mvc.Action
import security.MyDeadboltHandler
import utils.Version


/**
 * @author f.patin
 */
object Application extends BaseController {

	def index() = SubjectPresent(new MyDeadboltHandler) {
		Action {
			implicit request =>
				Ok(views.html.index(new MyDeadboltHandler(), Version.version))
		}
	}


}
