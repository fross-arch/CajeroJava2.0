package miplata.persistence.repository;

import miplata.domain.TarjetaCredito;
import miplata.persistence.mapper.TarjetaCreditoRowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class TarjetaCreditoRepositoryMySql {

    private final Connection connection;
    private final TarjetaCreditoRowMapper rowMapper;

    public TarjetaCreditoRepositoryMySql(Connection connection, TarjetaCreditoRowMapper rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    public TarjetaCredito guardarTarjeta(TarjetaCredito tarjeta) {

        String sql = "INSERT INTO tarjeta_credito (usuario, cupo, cupo_disponible, tier, estado) VALUES (?,?,?,?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, tarjeta.getUsuario());
            ps.setDouble(2, tarjeta.getCupo());
            ps.setDouble(3, tarjeta.getCupoDisponible());
            ps.setString(4, "NINGUNO");
            ps.setString(5, tarjeta.getEstado().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la tarjeta de crédito", e);
        }

        return tarjeta;
    }

    public Optional<TarjetaCredito> findByUsuario(String usuario) {

        String sql = "SELECT * FROM tarjeta_credito WHERE usuario = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(rowMapper.mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la tarjeta de crédito", e);
        }

        return Optional.empty();
    }

    public TarjetaCredito actualizarTarjeta(TarjetaCredito tarjeta) {

        String sql = "UPDATE tarjeta_credito SET cupo = ?, cupo_disponible = ?, tier = ?, estado = ? WHERE usuario = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setDouble(1, tarjeta.getCupo());
            ps.setDouble(2, tarjeta.getCupoDisponible());
            ps.setString(3, tarjeta.getTier().name());
            ps.setString(4, tarjeta.getEstado().name());
            ps.setString(5, tarjeta.getUsuario());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la tarjeta de crédito", e);
        }

        return tarjeta;
    }
}