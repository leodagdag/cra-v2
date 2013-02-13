package controllers;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import models.User;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import views.html.login;

import java.util.List;

import static play.data.Form.form;

/**
 * @author f.patin
 */
public class Authentication extends Controller {

	public static class LoginForm {
		public String username;
		public String password;
		public String redirect;
	}

	public static Result login() {
		return ok(login.render());
	}

	public static Result authenticate() {
		final Form<LoginForm> loginForm = form(LoginForm.class).bindFromRequest();
		if (loginForm.hasErrors()) {
			return badRequest();
		}
		final String username = loginForm.get().username;
		final String password = loginForm.get().password;
		final String redirect = loginForm.get().redirect;


		if (User.findAuthorisedUser(username, password) != null) {
			session("username", username);
			final List<String> url = Lists.newArrayList(routes.Application.index().url());
			if (StringUtils.isNotBlank(redirect)) {
				url.add("#" + redirect);
			}
			return Results.redirect(Joiner.on("").join(url));
		} else {
			Logger.error(String.format("%s.authenticate failure user:[%s]", Authentication.class.getSimpleName(), username));
			session().clear();
			flash("errormsg", "Utilisateur ou mot de passe incorrect");
			return Results.redirect(routes.Authentication.login());
		}
	}

	public static Result logout() {
		session().clear();
		return Results.redirect(routes.Authentication.login());
	}
}
