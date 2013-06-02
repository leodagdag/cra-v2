package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import models.JUser;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import utils.serializer.ObjectIdSerializer;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author f.patin
 */
public class EmployeeDTO {

	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;
	public String username;
	public String trigramme;
	public String firstName;
	public String lastName;
	public String email;
    @JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId managerId;
	public Boolean isManager;
	public String role;

	@SuppressWarnings({"unused"})
	public EmployeeDTO() {
	}

	public EmployeeDTO(final JUser user) {
		this.id = user.id;
		this.username = user.username;
		this.trigramme = user.trigramme;
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.email = user.email;
		this.managerId = user.managerId;
		this.isManager = user.isManager;
		this.role = user.role;
	}

	public static EmployeeDTO of(final JUser user) {
		return new EmployeeDTO(user);
	}

	public static List<EmployeeDTO> of(final List<JUser> users) {
		return Lists.newArrayList(Collections2.transform(users, new Function<JUser, EmployeeDTO>() {
			@Nullable
			@Override
			public EmployeeDTO apply(@Nullable JUser user) {
				return of(user);
			}
		}));
	}
}
