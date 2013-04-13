package dto;

import models.JUser;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import play.data.validation.Constraints;
import utils.serializer.ObjectIdSerializer;

/**
 * @author f.patin
 */
public class AccountDTO {

	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;
	@Constraints.Required(message = "Le trigramme est requis.")
	public String trigramme;
	@Constraints.Required(message = "Le prénom est requis.")
	public String firstName;
	@Constraints.Required(message = "Le nom est requis.")
	public String lastName;
	@Constraints.Required(message = "L'email est requis.")
	@Constraints.Email(message = "Un email valide est requis")
	public String email;
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId managerId;

	@SuppressWarnings({"unused"})
	public AccountDTO() {
	}

	public AccountDTO(final JUser user) {
		this.id = user.id;
		this.trigramme = user.trigramme;
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.email = user.email;
		if(user.managerId != null) {
			this.managerId = user.managerId;
		}
	}

	public static AccountDTO of(final JUser account) {
		return new AccountDTO(account);
	}

	public JUser to() {
		final JUser user = new JUser();
		user.id = this.id;
		user.trigramme = this.trigramme;
		user.firstName = this.firstName;
		user.lastName = this.lastName;
		user.email = this.email;
		user.managerId = this.managerId;
		return user;
	}
}
