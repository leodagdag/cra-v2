package controllers

import be.objectify.deadbolt.scala.DeadboltActions
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import scala.Array
import security.SecurityRole

/**
 * @author f.patin
 */
trait BaseController extends Controller with DeadboltActions with MongoController {

  val everybody = List(Array(SecurityRole.employee), Array(SecurityRole.production), Array(SecurityRole.administrator))
  val administrator = List(Array(SecurityRole.administrator))
  val batch = List(Array(SecurityRole.batch))
}
