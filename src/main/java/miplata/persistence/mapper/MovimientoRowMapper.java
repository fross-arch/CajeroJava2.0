package miplata.persistence.mapper;
import miplata.domain.Movimiento;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MovimientoRowMapper implements RowMapper<Movimiento> {

    @Override
    public Movimiento mapRow(ResultSet rs) throws SQLException {

        Movimiento movimiento = new Movimiento();

        movimiento.setIdMovimiento(rs.getInt("id_movimiento"));
        movimiento.setUsuario(rs.getString("usuario"));
        movimiento.setTipoCuenta(rs.getString("tipo_cuenta"));
        movimiento.setTipoOperacion(rs.getString("tipo_operacion"));
        movimiento.setMonto(rs.getDouble("monto"));
        movimiento.setFecha(rs.getTimestamp("fecha").toLocalDateTime());

        return movimiento;
    }
}