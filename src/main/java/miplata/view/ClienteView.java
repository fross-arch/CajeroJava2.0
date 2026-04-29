package miplata.view;

import miplata.domain.Cliente;
import miplata.services.ClienteService;
import miplata.utils.FormValidation;

import java.util.List;

/**
 * Vista de clientes — muestra resultados y captura inputs del usuario.
 * Sigue el mismo patrón que CustomerView.java del proyecto Lucia.
 */
public class ClienteView {

    private final ClienteService clienteService;

    public ClienteView(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    public void registrarCliente() {
        double saldoInicial = FormValidation.validateDouble("Saldo inicial para cuenta de ahorros");
        Cliente cliente = clienteService.registrarCliente(saldoInicial);
        if (cliente != null) {
            System.out.println("Registro completado: " + cliente);
        }
    }

    public void mostrarTodosLosClientes() {
        List<Cliente> clientes = clienteService.getAllClientes();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        System.out.println("\n=== Lista de Clientes ===");
        clientes.forEach(c -> System.out.println(
            "  Usuario: @" + c.getUsuario() +
            " | Nombre: " + c.getNombre() +
            " | ID: " + c.getIdentificacion() +
            " | Celular: " + c.getCelular()
        ));
    }

    public void actualizarCliente(String usuario) {
        clienteService.actualizarCliente(usuario);
    }

    public void eliminarCliente() {
        String usuario = FormValidation.validateString("Ingrese el usuario a eliminar");
        clienteService.eliminarCliente(usuario);
    }
}
