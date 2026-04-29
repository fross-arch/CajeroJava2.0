package miplata.domain.enums;

public enum CreditCardTier {

    BASICO(1_000_000, "Cupo básico"),
    INTERMEDIO(2_000_000, "Cupo intermedio"),
    PREMIUM(3_000_000, "Cupo premium");

    private final double cupo;
    private final String description;

    CreditCardTier(double cupo, String description) {
        this.cupo = cupo;
        this.description = description;
    }

    public double getCupo() {
        return cupo;
    }

    public String getDescription() {
        return description;
    }
}
