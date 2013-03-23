package constants;

/**
 * @author f.patin
 */
public enum ClaimType {
	TAXI("taxi"),
	TOLL("péage"),
	PARKING("parking"),
	RENT_CAR("location de voiture"),
	JOURNEY("déplacement"),
	MISSION_ALLOWANCE("indemnité de mission"),
	TOTAL("total");
	public String label;

	ClaimType(final String label) {
		this.label = label;
	}

	public static String label(final String claimType) {
		return ClaimType.valueOf(claimType).label;
	}
}
