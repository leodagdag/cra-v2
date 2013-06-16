package controllers;

import com.google.common.collect.Lists;
import constants.MissionType;
import dto.CustomerDTO;
import http.ResponseCache;
import models.JCustomer;
import models.JMission;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;
import java.util.Locale;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JCustomers extends Controller {

	@ResponseCache.NoCacheResponse
	public static Result all() {
		return ok(toJson(CustomerDTO.of(JCustomer.all())));
	}

	@ResponseCache.NoCacheResponse
	public static Result fetch(final String code) {
		return ok(toJson(CustomerDTO.of(JCustomer.byCode(code))));
	}

	@ResponseCache.NoCacheResponse
	public static Result withoutGenesis(){
		return ok(toJson(CustomerDTO.of(JCustomer.withoutGenesis())));
	}

	@BodyParser.Of(BodyParser.Json.class)
	@ResponseCache.NoCacheResponse
	public static Result save(){
		final Form<CustomerForm> form = Form.form(CustomerForm.class).bind(request().body().asJson());
		if(form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		}
		final CustomerForm customerForm = form.get();
		return created(toJson(CustomerDTO.of(JCustomer.save(customerForm.to()))));
	}

	public static class CustomerForm {
		public ObjectId id;
		public String code;
		public String name;

		public List<ValidationError> validate() {
			final List<ValidationError> errors = Lists.newArrayList();

			if(StringUtils.isBlank(this.code)) {
				errors.add(new ValidationError("code", "Le code est obligatoire."));
			}
			if(StringUtils.isBlank(this.name)) {
				errors.add(new ValidationError("label", "Le nom est obligatoire."));
			}
			return errors.isEmpty() ? null : errors;
		}

		public JCustomer to() {
			final JCustomer customer = new JCustomer();
			customer.id = this.id;
			customer.code = this.code.toUpperCase(Locale.FRANCE);
			customer.name = this.name;
			return customer;
		}
	}
}