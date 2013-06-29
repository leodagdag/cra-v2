package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import models.JCustomer;
import models.JMission;
import org.bson.types.ObjectId;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * @author f.patin
 */
public class MissionFullDTO extends AbstractMissionDTO {

	public String description;
	public CustomerDTO customer;

	@SuppressWarnings({"unused"})
	public MissionFullDTO() {
		super();
	}

	public MissionFullDTO(final JMission mission) {
		super(mission);
		this.description = mission.description;
		this.customer = CustomerDTO.of(JCustomer.byId(mission.customerId));
	}

	public static MissionFullDTO of(final JMission mission) {
		return new MissionFullDTO(mission);
	}

	public static List<MissionFullDTO> of(Collection<JMission> missions) {
		return Lists.newArrayList(Collections2.transform(missions, new Function<JMission, MissionFullDTO>() {
			@Nullable
			@Override
			public MissionFullDTO apply(@Nullable final JMission mission) {
				return new MissionFullDTO(mission);
			}
		}));
	}

	public static MissionFullDTO of(final ObjectId id) {
		return new MissionFullDTO(JMission.codeAndMissionType(id));
	}
}
