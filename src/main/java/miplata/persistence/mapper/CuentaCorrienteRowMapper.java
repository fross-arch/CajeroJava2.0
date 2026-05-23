package miplata.persistence.mapper;

import miplata.domain.CuentaCorriente;
import miplata.domain.enums.AccountState;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CuentaCorrienteRowMapper implements RowMapper<CuentaCorriente> {

    @Override
    public CuentaCorriente mapRow(ResultSet rs) throws SQLException {

        CuentaCorriente cuenta = new CuentaCorriente(rs.getString("usuario"));
        cuenta.setSaldo(rs.getDouble("saldo"));
        cuenta.setEstado(AccountState.valueOf(rs.getString("estado")));

        return cuenta;
    }
}