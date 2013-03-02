package dto;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import models.JClaim;
import models.JMission;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author f.patin
 */
public class ClaimDTO {

	public String id;
	public String userId;
	public Integer year;
	public Integer month;
	public Long date;
	public MissionDTO mission;
	public String claimType;
	public BigDecimal amount;
	public BigDecimal kilometer;
	public String journey;
	public String comment;

	public ClaimDTO() {
	}

	public ClaimDTO(final JClaim claim, final MissionDTO mission) {
		this.id = claim.id.toString();
		this.userId = claim.userId.toString();
		this.year = claim.year;
		this.month = claim.month;
		this.date = claim.date.getMillis();
		this.mission = mission;
		this.claimType = claim.claimType;
		this.amount = claim.amount;
		this.kilometer = claim.kilometer;
		this.journey = claim.journey;
		this.comment = claim.comment;
	}

	public static List<ClaimDTO> of(final ImmutableList<JClaim> claims, final ImmutableList<JMission> missions) {
		return ImmutableList.copyOf(Collections2.transform(claims, new Function<JClaim, ClaimDTO>() {
			@Nullable
			@Override
			public ClaimDTO apply(@Nullable final JClaim claim) {
				final MissionDTO mission = MissionDTO.of(Iterables.find(missions, new Predicate<JMission>() {
					@Override
					public boolean apply(@Nullable final JMission mission) {
						return mission.id.equals(claim.missionId);
					}
				}));
				return new ClaimDTO(claim, mission);
			}
		}));
	}

	public static ClaimDTO of(final JClaim claim) {
		return new ClaimDTO(claim, MissionDTO.of(claim.missionId));
	}
}
