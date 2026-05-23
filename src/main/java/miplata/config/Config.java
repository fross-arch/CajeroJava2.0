package miplata.config;

import miplata.persistence.mapper.*;
import miplata.persistence.repository.*;
import miplata.repository.ClienteRepository;
import miplata.services.ClienteService;
import miplata.services.ClienteServiceImpl;
import miplata.services.CuentaService;
import miplata.services.CuentaServiceImpl;
import miplata.services.outputport.ClientePersistencePort;
import miplata.userinterface.MenuApp;
import miplata.view.ClienteView;
import miplata.view.CuentaView;

import java.sql.Connection;

public class Config {

    public static MenuApp createMenuApp() {

        // 1. Conexión a la base de datos (Singleton)
        Connection connection = DataBaseConnectionMySql.getInstance().getConnection();

        // 2. Mappers
        ClienteRowMapper clienteRowMapper = new ClienteRowMapper();
        CuentaAhorrosRowMapper cuentaAhorrosRowMapper = new CuentaAhorrosRowMapper();
        CuentaCorrienteRowMapper cuentaCorrienteRowMapper = new CuentaCorrienteRowMapper();
        TarjetaCreditoRowMapper tarjetaCreditoRowMapper = new TarjetaCreditoRowMapper();
        MovimientoRowMapper movimientoRowMapper = new MovimientoRowMapper();

        // 3. Repositorios MySQL
        ClientePersistencePort clienteRepository = new ClienteRepositoryMySql(connection, clienteRowMapper);
        CuentaAhorrosRepositoryMySql cuentaAhorrosRepository = new CuentaAhorrosRepositoryMySql(connection, cuentaAhorrosRowMapper);
        CuentaCorrienteRepositoryMySql cuentaCorrienteRepository = new CuentaCorrienteRepositoryMySql(connection, cuentaCorrienteRowMapper);
        TarjetaCreditoRepositoryMySql tarjetaCreditoRepository = new TarjetaCreditoRepositoryMySql(connection, tarjetaCreditoRowMapper);
        MovimientoRepositoryMySql movimientoRepository = new MovimientoRepositoryMySql(connection, movimientoRowMapper);

        // 4. Repositorio en memoria para las cuentas
        ClienteRepository clienteRepositoryMemoria = new ClienteRepository();

        // 5. Servicios
        ClienteService clienteService = new ClienteServiceImpl(clienteRepository, clienteRepositoryMemoria, cuentaAhorrosRepository, cuentaCorrienteRepository, tarjetaCreditoRepository);
        CuentaService cuentaService = new CuentaServiceImpl(clienteRepositoryMemoria, cuentaAhorrosRepository, cuentaCorrienteRepository, tarjetaCreditoRepository, movimientoRepository);

        // 6. Vistas
        ClienteView clienteView = new ClienteView(clienteService);
        CuentaView cuentaView = new CuentaView(cuentaService, clienteRepositoryMemoria);

        // 7. Menú principal
        return new MenuApp(clienteView, cuentaView, clienteService, clienteRepositoryMemoria);
    }
}