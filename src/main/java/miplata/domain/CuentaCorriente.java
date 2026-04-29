package miplata.domain;

import miplata.domain.enums.AccountState;
import miplata.domain.enums.AccountType;

/**
 * Cuenta corriente con sobregiro permitido del 20% sobre el saldo.
 * Hereda de Cuenta e implementa polimorfismo en calcularRetiro().
 */
public class CuentaCorriente extends Cuenta {

    private static final double PORCENTAJE_SOBREGIRO = 0.20; // 20% extra

    public CuentaCorriente(String usuario) {
        super(usuario, AccountType.CORRIENTE);
        this.estado = AccountState.INACTIVA;
    }

    public double getPorcentajeSobregiro() { return PORCENTAJE_SOBREGIRO; }

    /**
     * Deposita dinero en la cuenta corriente.
     */
    @Override
    public OperacionResultado depositar(double monto) {
        if (Double.isNaN(monto) || monto <= 0) {
            return new OperacionResultado(false, "Ingresa un monto válido mayor a 0.");
        }
        setSaldo(getSaldo() + monto);
        guardarMovimiento("Depósito: $" + formatPesos(monto));
        return new OperacionResultado(true,
                "Depósito exitoso. Nuevo saldo: $" + formatPesos(getSaldo()));
    }

    /**
     * Retira dinero de la cuenta corriente.
     * Polimorfismo: permite retirar hasta un 20% extra del saldo (sobregiro).
     */
    @Override
    public OperacionResultado calcularRetiro(double monto) {
        double saldoActual = getSaldo();
        double limiteRetiro = saldoActual + (saldoActual * PORCENTAJE_SOBREGIRO);

        if (Double.isNaN(monto) || monto <= 0) {
            return new OperacionResultado(false, "Ingresa un monto válido mayor a 0.");
        }
        if (monto > limiteRetiro) {
            return new OperacionResultado(false,
                    "Supera el límite permitido. Puedes retirar hasta $" +
                    formatPesos(limiteRetiro) + " (sobregiro 20%)");
        }

        setSaldo(saldoActual - monto);
        guardarMovimiento("Retiro: $" + formatPesos(monto));
        return new OperacionResultado(true,
                "Retiro exitoso. Nuevo saldo: $" + formatPesos(getSaldo()));
    }

    /**
     * Activa la cuenta corriente con un saldo inicial trasladado desde ahorros.
     */
    public void activar(double saldoInicial) {
        setSaldo(saldoInicial);
        setEstado(AccountState.ACTIVA);
        guardarMovimiento("Activación con saldo inicial: $" + formatPesos(saldoInicial));
    }
}
