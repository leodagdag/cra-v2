package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import models.JAffectedMission;
import models.JMission;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author f.patin
 */
public class AffectedMissionFullDTO {

    public Long startDate;
    public Long endDate;
    public MissionFullDTO mission;
    public String allowanceType;
    public BigDecimal feeAmount;

    @SuppressWarnings({"unused"})
    public AffectedMissionFullDTO() {
    }


    public AffectedMissionFullDTO(final JAffectedMission affectedMission) {
        this.startDate = affectedMission.startDate.getMillis();
        if (affectedMission.endDate != null) {
            this.endDate = affectedMission.endDate.getMillis();
        }
        this.mission = MissionFullDTO.of(JMission.fetch(affectedMission.missionId));
        this.allowanceType = affectedMission.allowanceType;
        this.feeAmount = affectedMission.feeAmount;
    }

    public static List<AffectedMissionFullDTO> of(final List<JAffectedMission> affectedMissions) {
        return Lists.newArrayList(Collections2.transform(affectedMissions, new Function<JAffectedMission, AffectedMissionFullDTO>() {
            @Nullable
            @Override
            public AffectedMissionFullDTO apply(@Nullable JAffectedMission affectedMission) {
                return new AffectedMissionFullDTO(affectedMission);
            }
        }));
    }
}
