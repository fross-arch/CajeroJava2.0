package miplata.persistence.repository;

import miplata.domain.Movimiento;
import miplata.persistence.mapper.MovimientoRowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovimientoRepositoryMySql {

    private final Connection connection;
    private final MovimientoRowMapper rowMapper;

    public MovimientoRepositoryMySql(Connection connection, MovimientoRowMapper rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    public Movimiento guardarMovimiento(Movimiento movimiento) {

        String sql = "INSERT INTO movimiento (usuario, tipo_cuenta, tipo_operacion, monto) VALUES (?,?,?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, movimiento.getUsuario());
            ps.setString(2, movimiento.getTipoCuenta());
            ps.setString(3, movimiento.getTipoOperacion());
            ps.setDouble(4, movimiento.getMonto());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el movimiento", e);
        }

        return movimiento;
    }

    public List<Movimiento> findByUsuario(String usuario) {

        List<Movimiento> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM movimiento WHERE usuario = ? ORDER BY fecha DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                movimientos.add(rowMapper.mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar los movimientos", e);
        }

        return movimientos;
    }

    public List<Movimiento> findByUsuarioYTipoCuenta(String usuario, String tipoCuenta) {

        List<Movimiento> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM movimiento WHERE usuario = ? AND tipo_cuenta = ? ORDER BY fecha DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, tipoCuenta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                movimientos.add(rowMapper.mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar los movimientos", e);
        }

        return movimientos;
    }
}