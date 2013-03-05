package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import models.JUser;

import javax.annotation.Nullable;

/**
 * @author f.patin
 */
public class ManagerDTO {

	public String id;
	public String firstName;
	public String lastName;

	public ManagerDTO() {
	}

	public ManagerDTO(final JUser user) {
		this.id = user.id.toString();
		this.firstName = user.firstName;
		this.lastName = user.lastName;
	}

	public static ImmutableList<ManagerDTO> of(final ImmutableList<JUser> users) {
		return ImmutableList.copyOf(Collections2.transform(users, new Function<JUser, ManagerDTO>() {
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
