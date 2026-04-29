package miplata.services;

import miplata.domain.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteService {

    Cliente registrarCliente(double saldoInicial);
    Optional<Cliente> login(String usuario, String password);
    Optional<Cliente> findByUsuario(String usuario);
    Cliente actualizarCliente(String usuario);
    void eliminarCliente(String usuario);
    List<Cliente> getAllClientes();
    boolean existeUsuario(String usuario);
}
