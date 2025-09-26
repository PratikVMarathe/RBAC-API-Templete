package com.academy.models;

public enum Role {
    OPS_EXECUTIVE("Ops Executive"),
    BD_EXECUTIVE("BD Executive"),
    ACCOUNTS("Accounts"),
    COACHES("Coaches"),
    USERS("Users"),
    ADMIN("Admin");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Utility method to check if a role is admin
    public boolean isAdmin() {
        return this == ADMIN;
    }
}

