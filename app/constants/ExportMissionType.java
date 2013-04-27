package constants;

/**
 * @author f.patin
 */
public enum ExportMissionType {
	customer("jours produits"),
	absence("jours absence"),
	none_product("jours non produits"),
	none("");
	public final String label;


	private ExportMissionType(final String label) {
		this.label = label;
	}
}
