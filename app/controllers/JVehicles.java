package controllers;

import caches.ResponseCache;
import dto.VehicleDTO;
import models.JVehicle;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

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

	@ResponseCache.NoCacheResponse
    public static Result active(final String userId) {
        final JVehicle vehicle = JVehicle.active(userId);
        if (vehicle == null) {
            return ok();
        }
        return ok(toJson(VehicleDTO.of(JVehicle.active(userId))));
    }

    public static Result history(final String userId) {
        return ok(toJson(VehicleDTO.of(JVehicle.history(userId))));
    }

    public static Result deactivate() {
        final Form<String> form = Form.form(String.class).bind(request().body().asJson());
        if (form.hasErrors()) {
            return badRequest(form.errorsAsJson());
        }
        final String id = form.data().get("id");
        JVehicle.deactivate(id);
        return ok();
    }

    public static class CreateVehicleForm {

        public ObjectId userId;
        public String vehicleType;
        public String brand;
        public Integer power;
        public String matriculation;
        public Long startDate;

	    public List<ValidationError> validate() {
		    return null;
	    }
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
