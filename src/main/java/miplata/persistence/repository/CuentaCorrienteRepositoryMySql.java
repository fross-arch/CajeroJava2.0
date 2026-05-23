package miplata.persistence.repository;

import miplata.domain.CuentaCorriente;
import miplata.persistence.mapper.CuentaCorrienteRowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CuentaCorrienteRepositoryMySql {

    private final Connection connection;
    private final CuentaCorrienteRowMapper rowMapper;

    public CuentaCorrienteRepositoryMySql(Connection connection, CuentaCorrienteRowMapper rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    public CuentaCorriente guardarCuenta(CuentaCorriente cuenta) {

        String sql = "INSERT INTO cuenta_corriente (usuario, saldo, estado) VALUES (?,?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, cuenta.getUsuario());
            ps.setDouble(2, cuenta.getSaldo());
            ps.setString(3, cuenta.getEstado().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la cuenta corriente", e);
        }

        return cuenta;
    }

    public Optional<CuentaCorriente> findByUsuario(String usuario) {

        String sql = "SELECT * FROM cuenta_corriente WHERE usuario = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(rowMapper.mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la cuenta corriente", e);
        }

        return Optional.empty();
    }

    public CuentaCorriente actualizarSaldo(CuentaCorriente cuenta) {

        String sql = "UPDATE cuenta_corriente SET saldo = ?, estado = ? WHERE usuario = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setDouble(1, cuenta.getSaldo());
            ps.setString(2, cuenta.getEstado().name());
            ps.setString(3, cuenta.getUsuario());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la cuenta corriente", e);
        }

        return cuenta;
    }
}