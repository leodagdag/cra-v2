package security;

import be.objectify.deadbolt.core.models.Role;

/**
 * @author f.patin
 */
public enum SecurityRoles implements Role {
    admin,
    production,
    user;

    @Override
    public String getName() {
        return name();
    }


}
