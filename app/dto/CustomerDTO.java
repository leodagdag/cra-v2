package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import models.JCustomer;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import utils.serializer.ObjectIdSerializer;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author f.patin
 */
public class CustomerDTO {

	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;
	public String code;
	public String name;

	public CustomerDTO(final JCustomer customer) {
		this.id = customer.id;
		this.code = customer.code;
		this.name = customer.name;
	}

	public static CustomerDTO of(final JCustomer customer) {
		return new CustomerDTO(customer);
	}

	public static List<CustomerDTO> of(final List<JCustomer> customers) {
		return Lists.newArrayList(Collections2.transform(customers, new Function<JCustomer, CustomerDTO>() {
			@Nullable
			@Override
			public CustomerDTO apply(@Nullable final JCustomer customer) {
				return new CustomerDTO(customer);
			}
		}));
	}
}
