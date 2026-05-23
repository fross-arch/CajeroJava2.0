package miplata.domain;

import java.time.LocalDateTime;

public class Movimiento {

    private int idMovimiento;
    private String usuario;
    private String tipoCuenta;
    private String tipoOperacion;
    private double monto;
    private LocalDateTime fecha;

    public Movimiento() {}

    public Movimiento(String usuario, String tipoCuenta, String tipoOperacion, double monto) {
        this.usuario = usuario;
        this.tipoCuenta = tipoCuenta;
        this.tipoOperacion = tipoOperacion;
        this.monto = monto;
    }

    // Getters y Setters

    public int getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(int idMovimiento) { this.idMovimiento = idMovimiento; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }

    public String getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(String tipoOperacion) { this.tipoOperacion = tipoOperacion; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    @Override
    public String toString() {
        return fecha + " | " + tipoCuenta + " | " + tipoOperacion + " | $" + String.format("%,.0f", monto);
    }
}