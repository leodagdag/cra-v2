package dto;

import models.JUser;
import org.bson.types.ObjectId;

/**
 * @author f.patin
 */
public class AccountDTO {

	public String id;
	public String username;
	public String trigramme;
	public String firstName;
	public String lastName;
	public String email;
	public String managerId;

	public AccountDTO() {
	}

	public AccountDTO(final JUser user) {
		this.id = user.id.toString();
		this.username = user.username;
		this.trigramme = user.trigramme;
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.email = user.email;
		if (user.managerId != null) {
			this.managerId = user.managerId.toString();
		}
	}

	public static AccountDTO of(final JUser account) {
		return new AccountDTO(account);
	}

	public JUser to() {
		final JUser user = new JUser();
		user.id = ObjectId.massageToObjectId(this.id);
		user.username = this.username;
		user.trigramme = this.trigramme;
		user.firstName = this.firstName;
		user.lastName = this.lastName;
		user.email = this.email;
		user.managerId = ObjectId.massageToObjectId(this.managerId);
		return user;
	}
}
