package miplata.services;

import miplata.domain.OperacionResultado;
import miplata.domain.enums.CreditCardTier;

public interface CuentaService {

    OperacionResultado depositar(String usuario, String tipoCuenta, double monto);
    OperacionResultado retirar(String usuario, String tipoCuenta, double monto);
    OperacionResultado transferir(String usuarioOrigen, String tipoCuentaOrigen,
                                  String usuarioDestino, double monto);
    OperacionResultado trasladarInterno(String usuario, String cuentaOrigen,
                                        String cuentaDestino, double monto);
    OperacionResultado activarCuentaCorriente(String usuario, double montoTraslado);
    OperacionResultado activarTarjetaCredito(String usuario, CreditCardTier tier);
    OperacionResultado realizarCompra(String usuario, double monto, int cuotas);
    OperacionResultado pagarCuota(String usuario, int index, String cuentaOrigen);
    OperacionResultado pagarTotal(String usuario, int index, String cuentaOrigen);
    void verSaldo(String usuario);
    void verMovimientos(String usuario);
}
