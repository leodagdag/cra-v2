package constants;

import com.google.common.collect.ImmutableList;

/**
 * @author f.patin
 */
public enum MissionType {
	customer,
	pre_sale,
	holiday,
	not_paid,
	internal_work;

	public static ImmutableList<String> craMissionType = new ImmutableList.Builder<String>()
		                                                     .add(customer.name())
		                                                     .add(pre_sale.name())
		                                                     .add(not_paid.name())
		                                                     .add(internal_work.name())
		                                                     .build();
}
