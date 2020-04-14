package io.wastelesscorp.platform.atoms.user.api;

public enum Role {
    STANDARD_USER("standard_user"),
    ADMIN_USER("admin_user");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
