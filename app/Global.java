import play.GlobalSettings;
import play.Logger;
import play.api.mvc.Handler;
import play.mvc.Action;
import play.mvc.Http;

import java.lang.reflect.Method;

/**
 * @author f.patin
 */
public class Global extends GlobalSettings {


	@Override
	public Action onRequest(final Http.Request request, final Method actionMethod) {
		Logger.info(String.format("Call [%s] [%s %s]", request.remoteAddress(), request.method(), request.path()));
		return super.onRequest(request, actionMethod);
	}

	@Override
	public Handler onRouteRequest(Http.RequestHeader request) {
		Logger.debug(String.format("Call from[%s] - [%s %s]", request.remoteAddress(), request.method(), request.path()));
		return super.onRouteRequest(request);
	}
}
