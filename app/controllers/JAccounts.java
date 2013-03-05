package controllers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dto.AccountDTO;
import models.JUser;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static play.data.Form.form;
import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JAccounts extends Controller {

	public static Result fetch(final String id) {
		return ok(toJson(AccountDTO.of(JUser.account(id))));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result update() {
		final Form<AccountDTO> form = form(AccountDTO.class).bind(request().body().asJson());
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		}
		final AccountDTO dto = form.get();
		final JUser user = dto.to();
		return created(toJson(AccountDTO.of(JUser.update(user))));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result password() {
		Form<PasswordForm> form = form(PasswordForm.class).bind(request().body().asJson());
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		}
		return ok();
	}

	public static class PasswordForm {

		@Constraints.Required(message = "Le mot de passe actuel est requis.")
		public String oldPassword;
		@Constraints.Required(message = "La nouveau mot de passe est requis.")
		public String newPassword;
		@Constraints.Required(message = "La confirmation du mot de passe est requise.")
		public String confirmPassword;

		public List<ValidationError> validate() {
			List<ValidationError> errors = Lists.newArrayList();
			if (!newPassword.equals(confirmPassword)) {
				errors.add(new ValidationError("global", "Le nouveau mot de passe n'est pas confirmé."));
			}
			return errors;
		}

	}
}
