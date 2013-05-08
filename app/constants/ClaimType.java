package constants;

/**
 * @author f.patin
 */
public enum ClaimType {
	FUEL("carburant"),
	TOLL("péage"),
	PARKING("parking"),
	PUBLIC_TRANSPORT("métro, bus"),
	TRAIN_PLANE("train, avion"),
	TAXI("taxi"),
	RENT_CAR("location de voiture"),
	BREAKFAST("petit déjeuner"),
	LUNCH("déjeuner"),
	DINER("diner"),
	HOSTEL("hotels"),
	INVITATION("invitations"),
	CONSUMPTION("consommations"),
	PHONE("tél, poste"),
	MISCELLANEOUS("divers"),
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
