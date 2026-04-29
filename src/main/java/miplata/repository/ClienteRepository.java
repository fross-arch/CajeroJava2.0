package miplata.repository;

import miplata.domain.Cliente;
import miplata.domain.CuentaAhorros;
import miplata.domain.CuentaCorriente;
import miplata.domain.TarjetaCredito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio en memoria de clientes y sus cuentas.
 * Reemplaza el localStorage del JavaScript original.
 */
public class ClienteRepository {

    private final List<Cliente> clientes = new ArrayList<>();
    private final List<CuentaAhorros> cuentasAhorros = new ArrayList<>();
    private final List<CuentaCorriente> cuentasCorriente = new ArrayList<>();
    private final List<TarjetaCredito> tarjetasCredito = new ArrayList<>();

    // ── Clientes ─────────────────────────────────────────────────────────────

    public Cliente guardarCliente(Cliente cliente) {
        clientes.add(cliente);
        // Crear cuentas asociadas
        cuentasAhorros.add(new CuentaAhorros(cliente.getUsuario()));
        cuentasCorriente.add(new CuentaCorriente(cliente.getUsuario()));
        tarjetasCredito.add(new TarjetaCredito(cliente.getUsuario()));
        return cliente;
    }

    public Optional<Cliente> findByUsuario(String usuario) {
        return clientes.stream()
                .filter(c -> c.getUsuario().equals(usuario))
                .findFirst();
    }

    public boolean existeUsuario(String usuario) {
        return findByUsuario(usuario).isPresent();
    }

    public List<Cliente> findAllClientes() {
        return new ArrayList<>(clientes);
    }

    public boolean eliminarCliente(String usuario) {
        boolean removed = clientes.removeIf(c -> c.getUsuario().equals(usuario));
        cuentasAhorros.removeIf(c -> c.getUsuario().equals(usuario));
        cuentasCorriente.removeIf(c -> c.getUsuario().equals(usuario));
        tarjetasCredito.removeIf(c -> c.getUsuario().equals(usuario));
        return removed;
    }

    // ── Cuentas ──────────────────────────────────────────────────────────────

    public Optional<CuentaAhorros> findCuentaAhorros(String usuario) {
        return cuentasAhorros.stream()
                .filter(c -> c.getUsuario().equals(usuario))
                .findFirst();
    }

    public Optional<CuentaCorriente> findCuentaCorriente(String usuario) {
        return cuentasCorriente.stream()
                .filter(c -> c.getUsuario().equals(usuario))
                .findFirst();
    }

    public Optional<TarjetaCredito> findTarjetaCredito(String usuario) {
        return tarjetasCredito.stream()
                .filter(c -> c.getUsuario().equals(usuario))
                .findFirst();
    }
}
