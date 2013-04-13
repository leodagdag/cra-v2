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
public class ManagerDTO {

	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;
	public String firstName;
	public String lastName;

	@SuppressWarnings({"unused"})
	public ManagerDTO() {
	}

	public ManagerDTO(final JUser user) {
		this.id = user.id;
		this.firstName = user.firstName;
		this.lastName = user.lastName;
	}

	public static List<ManagerDTO> of(final List<JUser> users) {
		return Lists.newArrayList(Collections2.transform(users, new Function<JUser, ManagerDTO>() {
			@Nullable
			@Override
			public ManagerDTO apply(@Nullable final JUser user) {
				return of(user);
			}
		}));
	}

	public static ManagerDTO of(final JUser user) {
		return new ManagerDTO(user);
	}
}
