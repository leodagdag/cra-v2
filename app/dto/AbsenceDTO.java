package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import models.JAbsence;
import models.JMission;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import utils.serializer.ObjectIdSerializer;
import utils.time.TimeUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author f.patin
 */
public class AbsenceDTO {

    @JsonSerialize(using = ObjectIdSerializer.class)
    public ObjectId id;
    @JsonSerialize(using = ObjectIdSerializer.class)
    public ObjectId userId;
    @JsonSerialize(using = ObjectIdSerializer.class)
    public ObjectId missionId;
    public String code;
    public String description;
    public Long startDate;
    public Long endDate;
    public BigDecimal nbDays;
    public String comment;

	@SuppressWarnings({"unused"})
    public AbsenceDTO() {
    }

    public AbsenceDTO(final JAbsence absence, final JMission mission) {
        this.id = absence.id;
        this.userId = absence.userId;
        this.missionId = absence.missionId;
        if (mission != null) {
            this.code = mission.code;
            this.description = mission.description;
        }
        this.startDate = absence.startDate.getMillis();
        this.endDate = absence.endDate.getMillis();
        this.nbDays = absence.nbDays;
        this.comment = absence.comment;
    }

    public static List<AbsenceDTO> of(final List<JAbsence> absences) {
        final ImmutableMap<ObjectId, JMission> missions = JMission.codeAndMissionType(ImmutableList.copyOf(Collections2.transform(absences, new Function<JAbsence, ObjectId>() {
	        @Nullable
	        @Override
	        public ObjectId apply(@Nullable final JAbsence absence) {
		        return absence.missionId;
	        }
        })));
        return Lists.newArrayList(Collections2.transform(absences, new Function<JAbsence, AbsenceDTO>() {
            @Nullable
            @Override
            public AbsenceDTO apply(@Nullable final JAbsence absence) {
                return AbsenceDTO.of(absence, missions.get(absence.missionId));
            }
        }));
    }

    private static AbsenceDTO of(JAbsence absence, final JMission missions) {
        return new AbsenceDTO(absence, missions);
    }

    public static AbsenceDTO of(JAbsence absence) {
        return of(Lists.newArrayList(absence)).get(0);
    }
}
