package models;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import constants.MissionType;
import constants.MomentOfDay;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * @author f.patin
 */
@Embedded
public class JHalfDay {

	public ObjectId missionId;
	public List<JPeriod> periods = Lists.newArrayList();
	public Integer momentOfDay = MomentOfDay.morning.index;

	public JHalfDay() {
	}

	public JHalfDay(final MomentOfDay momentOfDay) {
		this.momentOfDay = momentOfDay.index;
	}

	public JHalfDay(final MomentOfDay momentOfDay, final ObjectId missionId) {
		this.momentOfDay = momentOfDay.index;
		this.missionId = missionId;
	}

	@JsonProperty("isSpecial")
	public Boolean isSpecial() {
		return !Iterables.isEmpty(periods);
	}

	public Set<ObjectId> missionIds() {
		if(isSpecial()) {
			return Sets.newHashSet(Collections2.transform(periods, new Function<JPeriod, ObjectId>() {
				@Nullable
				@Override
				public ObjectId apply(@Nullable final JPeriod p) {
					return p.missionId;
				}
			}));
		} else {
			return Sets.newHashSet(missionId);
		}
	}

	public BigDecimal inGenesisHour() {
		if(this.isSpecial()) {
			return BigDecimal.ZERO;
		} else {
			final JMission mission = JMission.codeAndMissionType(this.missionId);
			return MissionType.valueOf(mission.missionType).genesisHour;
		}
	}

	public BigDecimal inGenesisHour(final JMission customerMission) {
		if(this.isSpecial()) {
			return BigDecimal.ZERO;
		} else {
			final JMission mission = JMission.codeAndMissionType(this.missionId);
			if(this.missionId.equals(customerMission.id) || !MissionType.valueOf(mission.missionType).equals(MissionType.customer)) {
				return MissionType.valueOf(mission.missionType).genesisHour;
			} else {
				return BigDecimal.ZERO;
			}
		}
	}
}
