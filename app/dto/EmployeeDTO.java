package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
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
    public String trigramme;
    public String firstName;
    public String lastName;
    public String email;
    public ObjectId managerId;
    public Boolean isManager = Boolean.FALSE;

    public EmployeeDTO() {
    }

    public EmployeeDTO(final JUser user) {
        this.id = user.id;
        this.trigramme = user.trigramme;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.email = user.email;
        this.managerId = user.managerId;
        this.isManager = user.isManager;
    }

    public static EmployeeDTO of(final JUser user){
        return new EmployeeDTO(user);
    }

    public static ImmutableList<EmployeeDTO> of(final ImmutableList<JUser> users){
        return ImmutableList.copyOf(Collections2.transform(users,  new Function<JUser, EmployeeDTO>() {
            @Nullable
            @Override
            public EmployeeDTO apply(@Nullable JUser user) {
                return of(user);
            }
        }));
    }
}
