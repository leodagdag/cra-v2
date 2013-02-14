package controllers

import play.api.mvc.Controller
import be.objectify.deadbolt.scala.DeadboltActions
import play.modules.reactivemongo.MongoController

/**
 * @author f.patin
 */
trait BaseController extends Controller with DeadboltActions with MongoController{

}
