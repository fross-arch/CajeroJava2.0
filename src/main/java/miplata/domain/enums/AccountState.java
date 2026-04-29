package miplata.domain.enums;

public enum AccountState {

    ACTIVA("activa"),
    INACTIVA("inactiva");

    private final String description;

    AccountState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
