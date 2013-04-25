package constants;

import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;

/**
 * @author f.patin
 */
public enum MissionType {
	customer("Client", Util.THREE_POINT_SEVEN),
	other_customer("AC", Util.THREE_POINT_SEVEN),
	pre_sale("Avant vente", Util.THREE_POINT_SEVEN),
	holiday("Absence", BigDecimal.ZERO),
	not_paid("TP - CSS - MM", BigDecimal.ZERO),
	internal_work("TI - F - IC", Util.THREE_POINT_SEVEN),
	none("", BigDecimal.ZERO);
	public static ImmutableList<String> craMissionType = new ImmutableList.Builder<String>()
		                                                     .add(customer.name())
		                                                     .add(pre_sale.name())
		                                                     .add(not_paid.name())
		                                                     .add(internal_work.name())
		                                                     .build();
	public final String label;
	public final BigDecimal genesisHour;

	MissionType(final String label, final BigDecimal genesisHour) {
		this.label = label;
		this.genesisHour = genesisHour;
	}
}
