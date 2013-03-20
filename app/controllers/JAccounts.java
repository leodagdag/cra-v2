package controllers;

import com.google.common.collect.Lists;
import dto.AccountDTO;
import models.JUser;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

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
		final Form<AccountDTO> form = Form.form(AccountDTO.class).bind(request().body().asJson());
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		}
		final AccountDTO dto = form.get();
		final JUser user = dto.to();
		return created(toJson(AccountDTO.of(JUser.update(user))));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result password() {
		Form<PasswordForm> form = Form.form(PasswordForm.class).bind(request().body().asJson());
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		}
		final PasswordForm passwordForm = form.get();
		final String newPassword = passwordForm.newPassword;
		JUser.password(session().get("username"), newPassword);
		session().remove("username");
		return unauthorized();
	}

	public static class PasswordForm {

		@Constraints.Required(message = "Le mot de passe actuel est requis.")
		public String oldPassword;
		@Constraints.Required(message = "La nouveau mot de passe est requis.")
		public String newPassword;
		@Constraints.Required(message = "La confirmation du mot de passe est requise.")
		public String confirmPassword;

		public List<ValidationError> validate() {
			final List<ValidationError> errors = Lists.newArrayList();
			if (Boolean.FALSE.equals(JUser.checkAuthentication(session("username"), oldPassword))) {
				errors.add(new ValidationError("oldPassword", "Le mot de passe actuel est faux."));
			}
			if(oldPassword.equals(newPassword)){
				errors.add(new ValidationError("newPassword", "Le nouveau mot de passe ne peut pas être identique à l'actuel."));

			}
			if (!newPassword.equals(confirmPassword)) {
				errors.add(new ValidationError("confirmPassword", "Le nouveau mot de passe n'est pas confirmé."));
			}
			return errors.isEmpty() ? null : errors;
		}

	}
}
