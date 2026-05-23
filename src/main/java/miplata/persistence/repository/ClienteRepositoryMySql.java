package miplata.persistence.repository;

import miplata.domain.Cliente;
import miplata.persistence.mapper.ClienteRowMapper;
import miplata.services.outputport.ClientePersistencePort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepositoryMySql implements ClientePersistencePort {

    private final Connection connection;
    private final ClienteRowMapper rowMapper;

    public ClienteRepositoryMySql(Connection connection, ClienteRowMapper rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    @Override
    public Cliente guardarCliente(Cliente cliente) {

        String sql = "INSERT INTO cliente (usuario, password, nombre, identificacion, celular) VALUES (?,?,?,?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, cliente.getUsuario());
            ps.setString(2, cliente.getPassword());
            ps.setString(3, cliente.getNombre());
            ps.setString(4, cliente.getIdentificacion());
            ps.setString(5, cliente.getCelular());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el cliente", e);
        }

        return cliente;
    }

    @Override
    public Optional<Cliente> findByUsuario(String usuario) {

        String sql = "SELECT * FROM cliente WHERE usuario = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(rowMapper.mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el cliente", e);
        }

        return Optional.empty();
    }

    @Override
    public boolean existeUsuario(String usuario) {
        return findByUsuario(usuario).isPresent();
    }

    @Override
    public List<Cliente> findAllClientes() {

        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                clientes.add(rowMapper.mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los clientes", e);
        }

        return clientes;
    }

    @Override
    public boolean eliminarCliente(String usuario) {

        String sql = "DELETE FROM cliente WHERE usuario = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, usuario);
            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el cliente", e);
        }
    }
}