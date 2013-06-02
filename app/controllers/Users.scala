package controllers

import security.MyDeadboltHandler
import play.api.mvc.Action
import play.api.libs.json.JsString

/**
 * @author f.patin
 */
object Users extends BaseController {

  def profile = Restrict(administrator, new MyDeadboltHandler()) {
    Action{
      request =>
        Async {
          Ok(JsString(""))
        }
    }
  }

}
