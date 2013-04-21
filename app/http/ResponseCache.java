package http;

import org.joda.time.DateTime;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author f.patin
 */

public class ResponseCache {

	@With(NoCacheResponseAction.class)
	@Target({ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface NoCacheResponse {

	}

	public static class NoCacheResponseAction extends Action<NoCacheResponse> {

		@Override
		public Result call(final Http.Context ctx) throws Throwable {
			ctx.response().setHeader(Http.HeaderNames.CACHE_CONTROL, "max-age=0");
			ctx.response().setHeader(Http.HeaderNames.ETAG, Long.toString(DateTime.now().getMillis()));
			return delegate.call(ctx);
		}
	}
}
