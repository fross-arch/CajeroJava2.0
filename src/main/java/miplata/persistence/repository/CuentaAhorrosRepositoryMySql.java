package miplata.persistence.repository;

import miplata.domain.CuentaAhorros;
import miplata.persistence.mapper.CuentaAhorrosRowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CuentaAhorrosRepositoryMySql {

    private final Connection connection;
    private final CuentaAhorrosRowMapper rowMapper;

    public CuentaAhorrosRepositoryMySql(Connection connection, CuentaAhorrosRowMapper rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    public CuentaAhorros guardarCuenta(CuentaAhorros cuenta) {

        String sql = "INSERT INTO cuenta_ahorros (usuario, saldo, estado) VALUES (?,?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, cuenta.getUsuario());
            ps.setDouble(2, cuenta.getSaldo());
            ps.setString(3, cuenta.getEstado().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la cuenta de ahorros", e);
        }

        return cuenta;
    }

    public Optional<CuentaAhorros> findByUsuario(String usuario) {

        String sql = "SELECT * FROM cuenta_ahorros WHERE usuario = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(rowMapper.mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la cuenta de ahorros", e);
        }

        return Optional.empty();
    }

    public CuentaAhorros actualizarSaldo(CuentaAhorros cuenta) {

        String sql = "UPDATE cuenta_ahorros SET saldo = ? WHERE usuario = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setDouble(1, cuenta.getSaldo());
            ps.setString(2, cuenta.getUsuario());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el saldo", e);
        }

        return cuenta;
    }
}