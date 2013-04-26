package constants;

/**
 * @author f.patin
 */
public enum MissionAllowanceType {
	FIXED(ClaimType.FIXED_FEE),
	ZONE(ClaimType.ZONE_FEE),
	NONE(null);

	public final ClaimType claimType;

	private MissionAllowanceType(final ClaimType claimType) {
		this.claimType = claimType;
	}
}
