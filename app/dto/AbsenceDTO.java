package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import models.JAbsence;
import models.JMission;
import org.bson.types.ObjectId;
import utils.time.TimeUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author f.patin
 */
public class AbsenceDTO {

    public String id;
    public String userId;
    public String missionId;
    public String code;
    public String description;
    public Long startDate;
    public Boolean startMorning;
    public Boolean startAfternoon;
    public Long endDate;
    public Boolean endMorning;
    public Boolean endAfternoon;
    public Integer nbDays = 0;
    public String comment;

    public AbsenceDTO() {
    }

    public AbsenceDTO(final JAbsence absence, final JMission mission) {
        this.id = absence.id.toString();
        this.userId = absence.userId.toStringBabble();
        this.missionId = absence.missionId.toString();
        if (mission != null) {
            this.code = mission.code;
            this.description = mission.description;
        }
        this.startDate = absence.startDate.getMillis();
        this.startMorning = absence.startMorning;
        this.startAfternoon = absence.startAfternoon;
        this.endDate = absence.endDate.getMillis();
        this.endMorning = absence.endMorning;
        this.endAfternoon = absence.endAfternoon;
        this.nbDays = TimeUtils.datesBetween(this.startDate, this.endDate, true).size();
        this.comment = absence.comment;
    }

    public static List<AbsenceDTO> of(final List<JAbsence> absences) {
        final ImmutableMap<ObjectId, JMission> missions = JMission.codeAndMissionType(Lists.newArrayList(Collections2.transform(absences, new Function<JAbsence, ObjectId>() {
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
