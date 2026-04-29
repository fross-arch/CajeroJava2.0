package miplata.domain;

import miplata.domain.enums.AccountState;
import miplata.domain.enums.AccountType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase padre abstracta que representa una cuenta bancaria genérica.
 * Implementa herencia: CuentaAhorros, CuentaCorriente y TarjetaCredito la extienden.
 */
public abstract class Cuenta {

    protected String usuario;
    protected double saldo;
    protected AccountState estado;
    protected AccountType tipo;
    protected List<String> movimientos;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Cuenta(String usuario, AccountType tipo) {
        this.usuario = usuario;
        this.saldo = 0;
        this.estado = AccountState.INACTIVA;
        this.tipo = tipo;
        this.movimientos = new ArrayList<>();
    }

    // ── Getters y Setters ────────────────────────────────────────────────────

    public String getUsuario() { return usuario; }

    public double getSaldo() { return saldo; }

    public void setSaldo(double saldo) { this.saldo = Math.round(saldo); }

    public AccountState getEstado() { return estado; }

    public void setEstado(AccountState estado) { this.estado = estado; }

    public AccountType getTipo() { return tipo; }

    public List<String> getMovimientos() { return movimientos; }

    public boolean isActiva() { return estado == AccountState.ACTIVA; }

    // ── Métodos comunes ──────────────────────────────────────────────────────

    public void guardarMovimiento(String texto) {
        String fecha = LocalDateTime.now().format(FORMATTER);
        movimientos.add(fecha + " - " + texto);
    }

    public String formatPesos(double valor) {
        return String.format("%,.0f", valor).replace(",", ".");
    }

    // ── Método polimórfico (cada hijo lo implementa diferente) ───────────────

    public abstract OperacionResultado depositar(double monto);

    public abstract OperacionResultado calcularRetiro(double monto);
}
