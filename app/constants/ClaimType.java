package constants;

/**
 * @author f.patin
 */
public enum ClaimType {
	TAXI("taxi"),
	TOLL("péage"),
	PARKING("parking"),
	RENT_CAR("location de voiture"),
	JOURNEY("frais kilométriques"),
	FIXED_FEE("forfait de séjour"),
	ZONE_FEE("forfait de zone"),
	TOTAL("total");
	public String label;

	ClaimType(final String label) {
		this.label = label;
	}

	public static String label(final String claimType) {
		return ClaimType.valueOf(claimType).label;
	}
}
