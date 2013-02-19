
import java.io.File
import play.api.mvc.{Result, Handler, RequestHeader}
import play.api.{Mode, Configuration, Logger, GlobalSettings, Application}


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
		super.onBadRequest(request, error)
	}

	override def onHandlerNotFound(request: RequestHeader): Result = {
		Logger warn s"HandlerNotFound from[${request.remoteAddress}}] - [${request.method} ${request.path}]"
		super.onHandlerNotFound(request)
	}

	override def onError(request: RequestHeader, ex: Throwable): Result = {
		Logger error(s"onError from[${request.remoteAddress}}] - [${request.method} ${request.path}]", ex)
		super.onError(request, ex)
	}
}
