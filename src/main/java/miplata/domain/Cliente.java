package miplata.domain;

/**
 * Representa un cliente/usuario del banco digital Mi Plata.
 * Contiene los datos personales y credenciales del usuario.
 */
public class Cliente {

    private String usuario;
    private String password;
    private String nombre;
    private String identificacion;
    private String celular;

    // ── Constructores ────────────────────────────────────────────────────────

    public Cliente() {}

    public Cliente(String usuario, String password, String nombre,
                   String identificacion, String celular) {
        this.usuario = usuario;
        this.password = password;
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.celular = celular;
    }

    // ── Getters y Setters ────────────────────────────────────────────────────

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    // ── Métodos propios ──────────────────────────────────────────────────────

    public boolean verificarPassword(String passwordIngresado) {
        return this.password != null && this.password.equals(passwordIngresado);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "usuario='" + usuario + '\'' +
                ", nombre='" + nombre + '\'' +
                ", identificacion='" + identificacion + '\'' +
                ", celular='" + celular + '\'' +
                '}';
    }
}
