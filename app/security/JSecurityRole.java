package security;

import be.objectify.deadbolt.core.models.Role;

/**
 * @author f.patin
 */
public class JSecurityRole implements Role {
    public String name;

    @Override
    public String getName() {
        return name;
    }

    public JSecurityRole(String name) {
        this.name = name;
    }
}
