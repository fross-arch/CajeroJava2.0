package miplata.services;

import miplata.domain.Cliente;
import miplata.domain.CuentaAhorros;
import miplata.repository.ClienteRepository;
import miplata.utils.FormValidation;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final Scanner sc = new Scanner(System.in);

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Cliente registrarCliente(double saldoInicial) {
        Cliente cliente = new Cliente();

        System.out.println("=== Registro de Nuevo Cliente ===");
        cliente.setUsuario(FormValidation.validateString("Ingrese un nombre de usuario"));
        cliente.setPassword(FormValidation.validateString("Ingrese una contraseña (mínimo 4 caracteres)"));
        cliente.setNombre(FormValidation.validateString("Ingrese su nombre completo"));
        cliente.setIdentificacion(FormValidation.validateString("Ingrese su número de identificación"));
        cliente.setCelular(FormValidation.validateString("Ingrese su número de celular"));

        if (clienteRepository.existeUsuario(cliente.getUsuario())) {
            System.out.println("El usuario ya existe. Intente con otro nombre de usuario.");
            return null;
        }

        Cliente guardado = clienteRepository.guardarCliente(cliente);

        // Asignar saldo inicial a cuenta de ahorros
        clienteRepository.findCuentaAhorros(guardado.getUsuario())
                .ifPresent(c -> {
                    c.setSaldo(saldoInicial);
                    c.guardarMovimiento("Saldo inicial: $" + c.formatPesos(saldoInicial));
                });

        System.out.println("Cliente registrado exitosamente. Bienvenido, " + cliente.getNombre() + "!");
        return guardado;
    }

    @Override
    public Optional<Cliente> login(String usuario, String password) {
        return clienteRepository.findByUsuario(usuario)
                .filter(c -> c.verificarPassword(password));
    }

    @Override
    public Optional<Cliente> findByUsuario(String usuario) {
        return clienteRepository.findByUsuario(usuario);
    }

    @Override
    public Cliente actualizarCliente(String usuario) {
        Cliente cliente = clienteRepository.findByUsuario(usuario).orElse(null);
        if (cliente == null) {
            System.out.println("Cliente no encontrado.");
            return null;
        }

        System.out.println("¿Qué desea actualizar?");
        System.out.println("1. Nombre  2. Identificación  3. Celular  4. Contraseña");
        int opcion = FormValidation.validateInt("Opción");

        switch (opcion) {
            case 1 -> cliente.setNombre(FormValidation.validateString("Nuevo nombre completo"));
            case 2 -> cliente.setIdentificacion(FormValidation.validateString("Nueva identificación"));
            case 3 -> cliente.setCelular(FormValidation.validateString("Nuevo celular"));
            case 4 -> {
                String actual = FormValidation.validateString("Contraseña actual");
                if (!cliente.verificarPassword(actual)) {
                    System.out.println("Contraseña actual incorrecta.");
                    return cliente;
                }
                String nueva = FormValidation.validateString("Nueva contraseña");
                cliente.setPassword(nueva);
                System.out.println("Contraseña actualizada correctamente.");
            }
            default -> System.out.println("Opción no válida.");
        }
        return cliente;
    }

    @Override
    public void eliminarCliente(String usuario) {
        boolean eliminado = clienteRepository.eliminarCliente(usuario);
        System.out.println(eliminado
                ? "Cliente eliminado correctamente."
                : "Cliente no encontrado.");
    }

    @Override
    public List<Cliente> getAllClientes() {
        return clienteRepository.findAllClientes();
    }

    @Override
    public boolean existeUsuario(String usuario) {
        return clienteRepository.existeUsuario(usuario);
    }
}
