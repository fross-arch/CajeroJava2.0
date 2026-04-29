package miplata.services;

import miplata.domain.enums.CreditCardTier;
import miplata.utils.FormValidation;

/**
 * Selector de tier de tarjeta de crédito.
 * Equivalente al menú de selección de cupos del JS.
 */
public class CreditCardTierSelector {

    public static CreditCardTier selectTier() {
        System.out.println("Seleccione el cupo de su tarjeta:");
        System.out.println("  1. $1.000.000 - Cupo básico");
        System.out.println("  2. $2.000.000 - Cupo intermedio");
        System.out.println("  3. $3.000.000 - Cupo premium");

        int opcion = FormValidation.validateInt("Opción");
        return switch (opcion) {
            case 1 -> CreditCardTier.BASICO;
            case 2 -> CreditCardTier.INTERMEDIO;
            case 3 -> CreditCardTier.PREMIUM;
            default -> {
                System.out.println("Opción no válida, se asignará cupo básico.");
                yield CreditCardTier.BASICO;
            }
        };
    }
}
