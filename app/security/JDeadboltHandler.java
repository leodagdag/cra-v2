package security;

import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import controllers.routes;
import models.JUser;
import play.api.http.MediaRange;
import play.mvc.Http;
import play.mvc.Result;

import javax.annotation.Nullable;

/**
 * @author f.patin
 */
public class JDeadboltHandler extends AbstractDeadboltHandler {
	@Override
	public Result beforeAuthCheck(Http.Context context) {
		return null;
	}

	@Override
	public Subject getSubject(Http.Context context) {
		return JUser.getSubject(context.session().get("username"));
	}

	@Override
	public Result onAuthFailure(Http.Context context, String content) {
		context.session().remove("username");
		MediaRange mr = Iterables.find(context.request().acceptedTypes(), new Predicate<MediaRange>() {
			@Override
			public boolean apply(@Nullable MediaRange mediaRange) {
				return MediaRange.apply("application/json").equals(mediaRange) || MediaRange.apply("text/javascript").equals(mediaRange);
			}
		},null);
		if (mr != null) {
			return unauthorized();
		} else {
			return redirect(routes.Authentication.login());
		}
	}

	@Override
	public DynamicResourceHandler getDynamicResourceHandler(Http.Context context) {
		return null;
	}
}
