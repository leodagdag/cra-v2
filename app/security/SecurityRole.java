package security;

import be.objectify.deadbolt.core.models.Role;

/**
 * @author f.patin
 */
public class SecurityRole implements Role {
    public String name;

    @Override
    public String getName() {
        return name;
    }

    public SecurityRole(String name) {
        this.name = name;
    }
}
