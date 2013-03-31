
import controllers.routes
import java.io.File
import models.JUser
import play.api.http.{MimeTypes, MediaRange}
import play.api.libs.json.JsString
import play.api.mvc.Results._
import play.api.mvc.{Results, Result, Handler, RequestHeader}
import play.api.{Mode, Configuration, Logger, GlobalSettings, Application}
import security.BatchRole


/**
 * @author f.patin
 */
object Global extends GlobalSettings {

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    Logger debug s"Call from[${request.remoteAddress}}] - [${ request.method} ${request.path}]"
    super.onRouteRequest(request)
  }

  override def onStart(app: Application) {
    Logger info "Application start ..."
    import models._
    models.modelsToIndex.foreach(m => m.ensureIndexes)
    createBatchUser(app)
    super.onStart(app)
  }

  override def onStop(app: Application) {
    Logger info "Application stop ..."
    super.onStop(app)
  }

  override def onLoadConfig(config: Configuration, path: File, classloader: ClassLoader, mode: Mode.Mode): Configuration = {
    Logger debug s"LoadConfig [$mode]"
    lazy val altConfig = Configuration.from(
      Map(
        "mongodb.db" -> s"${mode.toString.toLowerCase}_${config.getString("mongodb.db").get}",
        "morphia.db.name" -> s"${mode.toString.toLowerCase}_${config.getString("morphia.db.name").get}"
      )
    )
    mode match {
      case Mode.Prod => super.onLoadConfig(config, path, classloader, mode)
      case _ => super.onLoadConfig(config ++ altConfig, path, classloader, mode)
    }
  }

  override def onBadRequest(request: RequestHeader, error: String): Result = {
    Logger warn s"BadRequest from[${request.remoteAddress}}] - [${request.method} ${request.path}] - error[$error]"
    request.acceptedTypes
      .find(mr => mr == MediaRange(MimeTypes.JAVASCRIPT) || mr == MediaRange(MimeTypes.JSON))
      .map(m => BadRequest(JsString(s"exception $error")))
      .getOrElse(super.onBadRequest(request, error))

  }

  override def onHandlerNotFound(request: RequestHeader): Result = {
    Logger warn s"HandlerNotFound from[${request.remoteAddress}}] - [${request.method} ${request.path}]"
    Results.Redirect(routes.Application.index())
  }

  override def onError(request: RequestHeader, ex: Throwable): Result = {
    Logger error(s"onError from[${request.remoteAddress}}] - [${request.method} ${request.path}]", ex)
    request.acceptedTypes
      .find(mr => mr == MediaRange(MimeTypes.JAVASCRIPT) || mr == MediaRange(MimeTypes.JSON))
      .map(m => InternalServerError(JsString(s"exception ${ex.getMessage}")))
      .getOrElse(super.onError(request, ex))
  }

  private def createBatchUser(app: Application) {
    Logger.info("Check Batch user")
    val username = app.configuration.getString("batch.username").getOrElse(throw app.configuration.reportError("batch.username", "Missing configuration Key"))
    val password = app.configuration.getString("batch.password").getOrElse(throw app.configuration.reportError("batch.username", "Missing configuration Key"))
    if (!JUser.exist(username)) {
      Logger.warn("Create batch user")
      JUser.add {
        val user = new JUser()
        user.username = username
        user.firstName = "Batch"
        user.lastName = "System"
        user.trigramme = "°_°"
        user.role = BatchRole.getName
        user
      }
    }
    JUser.password(username, password)

  }
}
