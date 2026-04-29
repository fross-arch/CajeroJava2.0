package miplata.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa una compra realizada con la tarjeta de crédito a cuotas.
 */
public class DeudaTC {


    private String fecha;
    private double capital;
    private int cuotas;
    private int cuotasPagadas;
    private double cuotaMensual;
    private double totalAPagar;
    private double pagado;
    private double tasa;
    private double saldoPendiente;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DeudaTC(double capital, int cuotas, double cuotaMensual, double tasa) {

        this.fecha = LocalDateTime.now().format(FORMATTER);
        this.capital = capital;
        this.cuotas = cuotas;
        this.cuotasPagadas = 0;
        this.cuotaMensual = cuotaMensual;
        this.totalAPagar = Math.round(cuotaMensual * cuotas);
        this.pagado = 0;
        this.tasa = tasa;
        this.saldoPendiente = this.totalAPagar;
    }

    // ── Getters y Setters ────────────────────────────────────────────────────


    public String getFecha() { return fecha; }
    public double getCapital() { return capital; }
    public int getCuotas() { return cuotas; }
    public int getCuotasPagadas() { return cuotasPagadas; }
    public void setCuotasPagadas(int cuotasPagadas) { this.cuotasPagadas = cuotasPagadas; }
    public double getCuotaMensual() { return cuotaMensual; }
    public double getTotalAPagar() { return totalAPagar; }
    public double getPagado() { return pagado; }
    public void setPagado(double pagado) { this.pagado = pagado; }
    public double getTasa() { return tasa; }
    public double getSaldoPendiente() { return saldoPendiente; }
    public void setSaldoPendiente(double saldoPendiente) { this.saldoPendiente = saldoPendiente; }

    public boolean isPagada() { return cuotasPagadas >= cuotas; }

    public int getCuotasRestantes() { return cuotas - cuotasPagadas; }

    @Override
    public String toString() {
        return String.format(
                "Fecha: %s | Capital: $%,.0f | Cuota: $%,.0f/mes | Cuotas: %d/%d | Pendiente: $%,.0f",
             fecha, capital,cuotaMensual, cuotasPagadas, cuotas, saldoPendiente);
    }
}
