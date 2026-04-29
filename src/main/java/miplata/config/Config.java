package miplata.config;

import miplata.repository.ClienteRepository;
import miplata.services.ClienteService;
import miplata.services.ClienteServiceImpl;
import miplata.services.CuentaService;
import miplata.services.CuentaServiceImpl;
import miplata.userinterface.MenuApp;
import miplata.view.ClienteView;
import miplata.view.CuentaView;

/**
 * Simple Factory — crea y conecta todos los objetos de la aplicación.
 * Equivalente al Config.java del proyecto Lucia de la profe.
 * Sigue el mismo patrón: centraliza la creación de dependencias para que
 * el Main quede limpio y fácil de mantener.
 */
public class Config {

    public static MenuApp createMenuApp() {

        // Capa de repositorio (almacenamiento en memoria — equivalente al localStorage)
        ClienteRepository clienteRepository = new ClienteRepository();

        // Capa de servicios
        ClienteService clienteService = new ClienteServiceImpl(clienteRepository);
        CuentaService cuentaService = new CuentaServiceImpl(clienteRepository);

        // Capa de vistas
        ClienteView clienteView = new ClienteView(clienteService);
        CuentaView cuentaView = new CuentaView(cuentaService, clienteRepository);

        // Interfaz de usuario
        return new MenuApp(clienteView, cuentaView, clienteService, clienteRepository);
    }
}
