package miplata.domain.enums;

public enum AccountType {

    AHORROS("Cuenta de Ahorros"),
    CORRIENTE("Cuenta Corriente"),
    TARJETA_CREDITO("Tarjeta de Crédito");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
