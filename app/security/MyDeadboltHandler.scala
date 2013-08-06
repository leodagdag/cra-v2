package security

import be.objectify.deadbolt.core.models.Subject
import be.objectify.deadbolt.scala.{DeadboltHandler, DynamicResourceHandler}
import controllers.routes
import play.api.Logger
import play.api.http.{MediaRange, MimeTypes}
import play.api.mvc.{Security, Results, Result, Request}
import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * @author f.patin
 */
class MyDeadboltHandler(dynamicResourceHandler: Option[DynamicResourceHandler] = None) extends DeadboltHandler {

  def beforeAuthCheck[A](request: Request[A]) = None

  override def getSubject[A](request: Request[A]): Option[Subject] = request.session.get("username")
    .map(username => Await.result(Auth.asSubject(username), 60.seconds))
    .getOrElse(None)


  def getDynamicResourceHandler[A](request: Request[A]): Option[DynamicResourceHandler] = ???

  def onAuthFailure[A](request: Request[A]): Result = {
    Logger.error(s"Authentication Failed ! Call from[${request.remoteAddress}}] - [${ request.method} ${request.path}]")
    request.acceptedTypes
      .find(mr => mr == MediaRange(MimeTypes.JAVASCRIPT) || mr == MediaRange(MimeTypes.JSON))
      .map(some => Results.Unauthorized)
      .getOrElse(
      Results.Redirect(routes.Authentication.login())
        .flashing(
        request.getQueryString("redirect")
          .map(redirect => ("redirect" -> redirect))
          .getOrElse(("" -> ""))
      ))
      .withSession(request.session - Security.username)
  }

}
