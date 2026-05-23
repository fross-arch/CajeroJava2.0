package miplata.persistence.mapper;

import miplata.domain.TarjetaCredito;
import miplata.domain.enums.AccountState;
import miplata.domain.enums.CreditCardTier;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TarjetaCreditoRowMapper implements RowMapper<TarjetaCredito> {

    @Override
    public TarjetaCredito mapRow(ResultSet rs) throws SQLException {

        TarjetaCredito tarjeta = new TarjetaCredito(rs.getString("usuario"));
        tarjeta.setCupo(rs.getDouble("cupo"));
        tarjeta.setCupoDisponible(rs.getDouble("cupo_disponible"));
        tarjeta.setEstado(AccountState.valueOf(rs.getString("estado")));

        return tarjeta;
    }
}