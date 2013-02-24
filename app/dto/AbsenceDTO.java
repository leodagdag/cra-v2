package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import models.JAbsence;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author f.patin
 */
public class AbsenceDTO {
	public String id;
	public String userId;
	public String missionId;
	public Long startDate;
	public Boolean startMorning;
	public Boolean startAfternoon;
	public Long endDate;
	public Boolean endMorning;
	public Boolean endAfternoon;
	public String comment;

	public AbsenceDTO() {
	}

	public AbsenceDTO(final JAbsence absence) {
		this.id = absence.id.toString();
		this.userId = absence.userId.toStringBabble();
		this.missionId = absence.missionId.toString();
		this.startDate = absence.startDate.getMillis();
		this.startMorning = absence.startMorning;
		this.startAfternoon = absence.startAfternoon;
		this.endDate = absence.endDate.getMillis();
		this.endMorning = absence.endMorning;
		this.endAfternoon = absence.endAfternoon;
		this.comment = absence.comment;
	}

	public static AbsenceDTO of(JAbsence absence){
		return new AbsenceDTO(absence);
	}

	public static List<AbsenceDTO> of(List<JAbsence> absences){
		return Lists.newArrayList(Collections2.transform(absences, new Function<JAbsence, AbsenceDTO>() {
			@Nullable
			@Override
			public AbsenceDTO apply(@Nullable final JAbsence absence) {
				return AbsenceDTO.of(absence);
			}
		}));
	}
}
