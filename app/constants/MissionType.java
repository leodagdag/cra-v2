package constants;

import com.google.common.collect.ImmutableList;

/**
 * @author f.patin
 */
public enum MissionType {
	customer("Client"),
	pre_sale("Avant vente"),
	holiday("Absence"),
	not_paid("TP - CSS - MM"),
	internal_work("TI - F - IC");

	public static ImmutableList<String> craMissionType = new ImmutableList.Builder<String>()
		                                                     .add(customer.name())
		                                                     .add(pre_sale.name())
		                                                     .add(not_paid.name())
		                                                     .add(internal_work.name())
		                                                     .build();
	public final String label;


	MissionType(final String label) {
		this.label = label;
	}
}
