package miplata.persistence.mapper;

import miplata.domain.Cliente;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteRowMapper implements RowMapper<Cliente> {

    @Override
    public Cliente mapRow(ResultSet rs) throws SQLException {

        Cliente cliente = new Cliente();

        cliente.setUsuario(rs.getString("usuario"));
        cliente.setPassword(rs.getString("password"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setIdentificacion(rs.getString("identificacion"));
        cliente.setCelular(rs.getString("celular"));

        return cliente;
    }
}