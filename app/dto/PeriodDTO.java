package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import models.JPeriod;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author f.patin
 */
public class PeriodDTO {
	public String missionId;
	public Long startTime;
	public Long endTime;
	public String periodType = "special";

	public PeriodDTO() {
	}

	public PeriodDTO(final JPeriod period) {
		this.missionId = period.missionId.toString();
		this.startTime = period.startTime.toDateTimeToday().getMillis();
		this.endTime = period.endTime.toDateTimeToday().getMillis();
	}

	public static List<PeriodDTO> of(final List<JPeriod> periods) {
		return Lists.newArrayList(Collections2.transform(periods, new Function<JPeriod, PeriodDTO>() {
			@Nullable
			@Override
			public PeriodDTO apply(@Nullable final JPeriod p) {
				return new PeriodDTO(p);
			}
		}));
	}
}
