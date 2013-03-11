package controllers;

import dto.VehicleDTO;
import models.JUser;
import models.JVehicle;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.joda.time.DateTime;
import play.data.Form;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import utils.deserializer.ObjectIdDeserializer;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JVehicles extends Controller {

	@BodyParser.Of(BodyParser.Json.class)
	public static Result save() {
		final Form<CreateVehicleForm> form = Form.form(CreateVehicleForm.class).bind(request().body().asJson());
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		}
		final CreateVehicleForm createVehicleForm = form.get();
		return created(toJson(VehicleDTO.of(JVehicle.save(createVehicleForm.to()))));

	}

	public static Result active (final String userId){
		return ok(toJson(VehicleDTO.of(JVehicle.active(userId))));
	}

	public static Result history(final String userId) {
		return ok(toJson(VehicleDTO.of(JVehicle.history(userId))));
	}

	public static class CreateVehicleForm {

		//@JsonDeserialize(using = ObjectIdDeserializer.class)
		public ObjectId userId;
		public String vehicleType;
		public String brand;
		public Integer power;
		public String matriculation;
		public Long startDate;

		public JVehicle to() {
			JVehicle vehicle = new JVehicle();
			vehicle.userId = this.userId;
			vehicle.vehicleType = this.vehicleType;
			vehicle.brand = this.brand;
			vehicle.power = this.power;
			vehicle.matriculation = this.matriculation;
			vehicle.startDate = new DateTime(this.startDate);
			return vehicle;
		}

	}

}
