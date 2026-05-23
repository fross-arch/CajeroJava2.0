package miplata.persistence.mapper;

import miplata.domain.CuentaAhorros;
import miplata.domain.enums.AccountState;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CuentaAhorrosRowMapper implements RowMapper<CuentaAhorros> {

    @Override
    public CuentaAhorros mapRow(ResultSet rs) throws SQLException {

        CuentaAhorros cuenta = new CuentaAhorros(rs.getString("usuario"));
        cuenta.setSaldo(rs.getDouble("saldo"));
        cuenta.setEstado(AccountState.valueOf(rs.getString("estado")));

        return cuenta;
    }
}