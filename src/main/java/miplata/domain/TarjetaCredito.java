package miplata.domain;

import miplata.domain.enums.AccountState;
import miplata.domain.enums.AccountType;
import miplata.domain.enums.CreditCardTier;

import java.util.ArrayList;
import java.util.List;

/**
 * Tarjeta de crédito con cupo, compras a cuotas y pago de deudas.
 * Hereda de Cuenta. Las tasas son: 0% (1-2 cuotas), 1.9% (3-6), 2.3% (7+).
 */
public class TarjetaCredito extends Cuenta {

    private double cupo;
    private double usado;
    private List<DeudaTC> deudas;

    public TarjetaCredito(String usuario) {
        super(usuario, AccountType.TARJETA_CREDITO);
        this.estado = AccountState.INACTIVA;
        this.cupo = 0;
        this.usado = 0;
        this.deudas = new ArrayList<>();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public double getCupo() { return cupo; }
    public double getUsado() { return Math.round(usado); }
    public double getCupoDisponible() { return Math.round(cupo - usado); }
    public List<DeudaTC> getDeudas() { return deudas; }

    public List<DeudaTC> getDeudasActivas() {
        return deudas.stream().filter(d -> !d.isPagada()).toList();
    }

    // ── Activación ───────────────────────────────────────────────────────────

    public void activar(CreditCardTier tier) {
        this.cupo = tier.getCupo();
        this.usado = 0;
        setEstado(AccountState.ACTIVA);
        guardarMovimiento("Tarjeta de Crédito activada. Cupo: $" +
                formatPesos(cupo) + " (" + tier.getDescription() + ")");
    }

    // ── Cálculo de cuota ─────────────────────────────────────────────────────

    /**
     * Calcula la cuota mensual usando la fórmula de amortización francesa.
     * Tasa: 0% para ≤2 cuotas, 1.9% para 3-6, 2.3% para 7+.
     */
    public double calcularCuota(double capital, int cuotas) {
        double tasa = obtenerTasa(cuotas);
        if (tasa == 0) {
            return Math.round(capital / cuotas);
        }
        return Math.round((capital * tasa) / (1 - Math.pow(1 + tasa, -cuotas)));
    }

    private double obtenerTasa(int cuotas) {
        if (cuotas <= 2) return 0;
        if (cuotas <= 6) return 0.019;
        return 0.023;
    }

    // ── Comprar ──────────────────────────────────────────────────────────────

    public OperacionResultado comprar(double monto, int cuotas) {
        if (Double.isNaN(monto) || monto <= 0) {
            return new OperacionResultado(false, "Ingresa un monto válido.");
        }
        if (monto > getCupoDisponible()) {
            return new OperacionResultado(false,
                    "Cupo insuficiente. Disponible: $" + formatPesos(getCupoDisponible()));
        }

        double tasa = obtenerTasa(cuotas);
        double cuotaMensual = calcularCuota(monto, cuotas);
        double totalAPagar = Math.round(cuotaMensual * cuotas);

        usado = Math.round(usado + monto);

        DeudaTC deuda = new DeudaTC(monto, cuotas, cuotaMensual, tasa);
        deudas.add(deuda);

        guardarMovimiento(
            "Compra: $" + formatPesos(monto) +
            " en " + cuotas + " cuota(s) de $" + formatPesos(cuotaMensual) + "/mes" +
            (tasa > 0 ? " (tasa " + String.format("%.1f", tasa * 100) + "%)" : " (sin interés)")
        );

        return new OperacionResultado(true,
                "Compra registrada. Cuota mensual: $" + formatPesos(cuotaMensual) +
                " | Total a pagar: $" + formatPesos(totalAPagar));
    }

    // ── Pagar cuota ──────────────────────────────────────────────────────────

    public OperacionResultado pagarCuota(int index, Cuenta cuentaOrigen) {
        DeudaTC deuda = findDeuda(index);
        if (deuda == null) return new OperacionResultado(false, "Deuda no encontrada.");
        if (deuda.isPagada()) return new OperacionResultado(false, "Esta deuda ya está completamente pagada.");

        double saldoCuenta = cuentaOrigen.getSaldo();
        if (saldoCuenta < deuda.getCuotaMensual()) {
            return new OperacionResultado(false,
                    "Saldo insuficiente. Necesitas $" + formatPesos(deuda.getCuotaMensual()));
        }

        cuentaOrigen.setSaldo(saldoCuenta - deuda.getCuotaMensual());
        deuda.setCuotasPagadas(deuda.getCuotasPagadas() + 1);
        deuda.setPagado(Math.round(deuda.getPagado() + deuda.getCuotaMensual()));

        if (deuda.isPagada()) {
            deuda.setSaldoPendiente(0);
            usado = Math.max(0, Math.round(usado - deuda.getCapital()));
        } else {
            deuda.setSaldoPendiente(Math.round(deuda.getCuotasRestantes() * deuda.getCuotaMensual()));
        }

        guardarMovimiento(
            "Pago cuota " + deuda.getCuotasPagadas() + "/" + deuda.getCuotas() +
            " compra $" + formatPesos(deuda.getCapital()) +
            ": $" + formatPesos(deuda.getCuotaMensual()) +
            " desde " + cuentaOrigen.getTipo().getDescription()
        );

        return new OperacionResultado(true,
                "Cuota pagada correctamente. " +
                (deuda.isPagada() ? "¡Deuda saldada!" :
                 "Cuotas restantes: " + deuda.getCuotasRestantes()));
    }

    // ── Pagar total ──────────────────────────────────────────────────────────

    public OperacionResultado pagarTotal(int index, Cuenta cuentaOrigen) {
        DeudaTC deuda = findDeuda(index);
        if (deuda == null) return new OperacionResultado(false, "Deuda no encontrada.");
        if (deuda.isPagada()) return new OperacionResultado(false, "Esta deuda ya está completamente pagada.");

        double saldoCuenta = cuentaOrigen.getSaldo();
        if (saldoCuenta < deuda.getSaldoPendiente()) {
            return new OperacionResultado(false,
                    "Saldo insuficiente. Necesitas $" + formatPesos(deuda.getSaldoPendiente()) +
                    " para pagar el total.");
        }

        cuentaOrigen.setSaldo(saldoCuenta - deuda.getSaldoPendiente());
        deuda.setPagado(deuda.getTotalAPagar());
        deuda.setCuotasPagadas(deuda.getCuotas());
        deuda.setSaldoPendiente(0);
        usado = Math.max(0, Math.round(usado - deuda.getCapital()));

        guardarMovimiento(
            "Pago total deuda compra $" + formatPesos(deuda.getCapital()) +
            " desde " + cuentaOrigen.getTipo().getDescription()
        );

        return new OperacionResultado(true, "Deuda pagada completamente.");
    }

    // ── Depositar / Retirar (no aplican directamente a TC) ───────────────────

    @Override
    public OperacionResultado depositar(double monto) {
        return new OperacionResultado(false, "Operación no disponible para Tarjeta de Crédito.");
    }

    @Override
    public OperacionResultado calcularRetiro(double monto) {
        return new OperacionResultado(false, "Operación no disponible para Tarjeta de Crédito.");
    }

    // ── Utilidades ───────────────────────────────────────────────────────────

    private DeudaTC findDeuda(int index) {
        if (index < 0 || index >= deudas.size()) return null;
        return deudas.get(index);
    }}