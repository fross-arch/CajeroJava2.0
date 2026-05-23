package miplata.services.outputport;

import miplata.domain.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClientePersistencePort {

    Cliente guardarCliente(Cliente cliente);
    Optional<Cliente> findByUsuario(String usuario);
    boolean existeUsuario(String usuario);
    List<Cliente> findAllClientes();
    boolean eliminarCliente(String usuario);
}