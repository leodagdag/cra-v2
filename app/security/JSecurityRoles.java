package security;

import be.objectify.deadbolt.core.models.Role;

/**
 * @author f.patin
 */
public enum JSecurityRoles implements Role {
    admin,
    production,
    user;

    @Override
    public String getName() {
        return name();
    }

	public static final String role_admin =  "admin";
	public static final String role_production=  "production";
	public static final String role_employee =  "employee";
}
