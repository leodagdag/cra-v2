package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import models.JVehicle;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import utils.serializer.ObjectIdSerializer;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author f.patin
 */
public class VehicleDTO {

    @JsonSerialize(using = ObjectIdSerializer.class)
    public ObjectId id;
    @JsonSerialize(using = ObjectIdSerializer.class)
    public ObjectId userId;
    public String vehicleType;
    public String brand;
    public Integer power;
    public String matriculation;
    public Integer month;
    public Integer year;
    public Long endDate;

	@SuppressWarnings({"unused"})
    public VehicleDTO() {
    }

    public VehicleDTO(final JVehicle vehicle) {
        this.id = vehicle.id;
        this.userId = vehicle.userId;
        this.vehicleType = vehicle.vehicleType;
        this.brand = vehicle.brand;
        this.power = vehicle.power;
        this.matriculation = vehicle.matriculation;
        this.month = vehicle.startDate.getMonthOfYear();
        this.year = vehicle.startDate.getYear();
        if (vehicle.endDate != null) {
            this.endDate = vehicle.endDate.getMillis();
        }
    }

    public static List<VehicleDTO> of(final List<JVehicle> history) {
        return Lists.newArrayList(Collections2.transform(history, new Function<JVehicle, VehicleDTO>() {
            @Nullable
            @Override
            public VehicleDTO apply(@Nullable final JVehicle vehicle) {
                return VehicleDTO.of(vehicle);
            }
        }));
    }

    public static VehicleDTO of(final JVehicle vehicle) {
        return new VehicleDTO(vehicle);
    }
}
