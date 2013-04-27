package constants;

import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;

/**
 * @author f.patin
 */
public enum MissionType {
	customer("Client", Util.THREE_POINT_SEVEN, ExportMissionType.customer),
	other_customer("AC", Util.THREE_POINT_SEVEN, ExportMissionType.none),
	pre_sale("Avant vente", Util.THREE_POINT_SEVEN, ExportMissionType.none_product),
	holiday("Absence", BigDecimal.ZERO, ExportMissionType.absence),
	not_paid("TP - CSS - MM", BigDecimal.ZERO, ExportMissionType.absence),
	internal_work("TI - F - IC", Util.THREE_POINT_SEVEN, ExportMissionType.none_product),
	none("", BigDecimal.ZERO, ExportMissionType.none);
	public static ImmutableList<String> craMissionType = new ImmutableList.Builder<String>()
		                                                     .add(customer.name())
		                                                     .add(pre_sale.name())
		                                                     .add(not_paid.name())
		                                                     .add(internal_work.name())
		                                                     .build();
	public final String label;
	public final BigDecimal genesisHour;
	public final ExportMissionType exportMissionType;

	MissionType(final String label, final BigDecimal genesisHour, final ExportMissionType exportMissionType) {
		this.label = label;
		this.genesisHour = genesisHour;
		this.exportMissionType = exportMissionType;
	}
}
