package miplata.domain;

import miplata.domain.enums.AccountState;
import miplata.domain.enums.AccountType;

/**
 * Cuenta de ahorros con tasa de rendimiento mensual del 1.5%.
 * Hereda de Cuenta e implementa polimorfismo en calcularRetiro().
 */
public class CuentaAhorros extends Cuenta {

    private static final double TASA_MENSUAL = 0.015; // 1.5% mensual

    public CuentaAhorros(String usuario) {
        super(usuario, AccountType.AHORROS);
        this.estado = AccountState.ACTIVA; // Ahorros siempre activa al registrarse
    }

    public double getTasaMensual() { return TASA_MENSUAL; }

    /**
     * Deposita dinero en la cuenta de ahorros.
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
     * Retira dinero de la cuenta de ahorros.
     * Polimorfismo: solo permite retirar hasta el saldo disponible.
     */
    @Override
    public OperacionResultado calcularRetiro(double monto) {
        double saldoActual = getSaldo();

        if (Double.isNaN(monto) || monto <= 0) {
            return new OperacionResultado(false, "Ingresa un monto válido mayor a 0.");
        }
        if (monto > saldoActual) {
            return new OperacionResultado(false,
                    "Saldo insuficiente. Tu saldo es $" + formatPesos(saldoActual));
        }

        setSaldo(saldoActual - monto);
        guardarMovimiento("Retiro: $" + formatPesos(monto));
        return new OperacionResultado(true,
                "Retiro exitoso. Nuevo saldo: $" + formatPesos(getSaldo()));
    }

    /**
     * Aplica el rendimiento mensual del 1.5% al saldo actual.
     */
    public OperacionResultado aplicarRendimiento() {
        double rendimiento = Math.round(getSaldo() * TASA_MENSUAL);
        setSaldo(getSaldo() + rendimiento);
        guardarMovimiento("Rendimiento mensual (1.5%): +$" + formatPesos(rendimiento));
        return new OperacionResultado(true,
                "Rendimiento aplicado: +$" + formatPesos(rendimiento) +
                ". Nuevo saldo: $" + formatPesos(getSaldo()));
    }
}
