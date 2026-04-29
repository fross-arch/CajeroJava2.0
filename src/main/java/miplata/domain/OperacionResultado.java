package miplata.domain;

/**
 * DTO que encapsula el resultado de una operación bancaria.
 * Equivalente al objeto { ok, mensaje } retornado en el JS.
 */
public class OperacionResultado {

    private final boolean ok;
    private final String mensaje;

    public OperacionResultado(boolean ok, String mensaje) {
        this.ok = ok;
        this.mensaje = mensaje;
    }

    public boolean isOk() { return ok; }

    public String getMensaje() { return mensaje; }

    @Override
    public String toString() {
        return (ok ? "[OK] " : "[ERROR] ") + mensaje;
    }
}
