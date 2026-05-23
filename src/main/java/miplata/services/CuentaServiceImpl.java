package miplata.services;

import miplata.domain.*;
import miplata.domain.enums.CreditCardTier;
import miplata.persistence.repository.CuentaAhorrosRepositoryMySql;
import miplata.persistence.repository.CuentaCorrienteRepositoryMySql;
import miplata.persistence.repository.MovimientoRepositoryMySql;
import miplata.persistence.repository.TarjetaCreditoRepositoryMySql;
import miplata.repository.ClienteRepository;

import java.util.List;

public class CuentaServiceImpl implements CuentaService {

    private final ClienteRepository clienteRepository;
    private final CuentaAhorrosRepositoryMySql cuentaAhorrosRepository;
    private final CuentaCorrienteRepositoryMySql cuentaCorrienteRepository;
    private final TarjetaCreditoRepositoryMySql tarjetaCreditoRepository;
    private final MovimientoRepositoryMySql movimientoRepository;

    public CuentaServiceImpl(ClienteRepository clienteRepository,
                             CuentaAhorrosRepositoryMySql cuentaAhorrosRepository,
                             CuentaCorrienteRepositoryMySql cuentaCorrienteRepository,
                             TarjetaCreditoRepositoryMySql tarjetaCreditoRepository,
                             MovimientoRepositoryMySql movimientoRepository) {
        this.clienteRepository = clienteRepository;
        this.cuentaAhorrosRepository = cuentaAhorrosRepository;
        this.cuentaCorrienteRepository = cuentaCorrienteRepository;
        this.tarjetaCreditoRepository = tarjetaCreditoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    // ── Depositar ────────────────────────────────────────────────────────────

    @Override
    public OperacionResultado depositar(String usuario, String tipoCuenta, double monto) {
        if (tipoCuenta.equalsIgnoreCase("corriente")) {
            CuentaCorriente cc = getCuentaCorriente(usuario);
            if (cc == null) return new OperacionResultado(false, "Cuenta corriente no disponible.");
            OperacionResultado resultado = cc.depositar(monto);
            if (resultado.isOk()) {
                movimientoRepository.guardarMovimiento(new Movimiento(usuario, "CORRIENTE", "DEPOSITO", monto));
                cuentaAhorrosRepository.actualizarSaldo(getCuentaAhorros(usuario));
            }
            return resultado;
        }
        CuentaAhorros ca = getCuentaAhorros(usuario);
        if (ca == null) return new OperacionResultado(false, "Cuenta de ahorros no disponible.");
        OperacionResultado resultado = ca.depositar(monto);
        if (resultado.isOk()) {
            movimientoRepository.guardarMovimiento(new Movimiento(usuario, "AHORROS", "DEPOSITO", monto));
            cuentaAhorrosRepository.actualizarSaldo(ca);
        }
        return resultado;
    }

    // ── Retirar ──────────────────────────────────────────────────────────────

    @Override
    public OperacionResultado retirar(String usuario, String tipoCuenta, double monto) {
        if (tipoCuenta.equalsIgnoreCase("corriente")) {
            CuentaCorriente cc = getCuentaCorriente(usuario);
            if (cc == null) return new OperacionResultado(false, "Cuenta corriente no disponible.");
            OperacionResultado resultado = cc.calcularRetiro(monto);
            if (resultado.isOk()) {
                movimientoRepository.guardarMovimiento(new Movimiento(usuario, "CORRIENTE", "RETIRO", monto));
            }
            return resultado;
        }
        CuentaAhorros ca = getCuentaAhorros(usuario);
        if (ca == null) return new OperacionResultado(false, "Cuenta de ahorros no disponible.");
        OperacionResultado resultado = ca.calcularRetiro(monto);
        if (resultado.isOk()) {
            movimientoRepository.guardarMovimiento(new Movimiento(usuario, "AHORROS", "RETIRO", monto));
            cuentaAhorrosRepository.actualizarSaldo(ca);
        }
        return resultado;
    }

    // ── Transferir ───────────────────────────────────────────────────────────

    @Override
    public OperacionResultado transferir(String usuarioOrigen, String tipoCuentaOrigen,
                                         String usuarioDestino, double monto) {
        if (!clienteRepository.existeUsuario(usuarioDestino)) {
            return new OperacionResultado(false, "El usuario destinatario no existe.");
        }
        if (usuarioOrigen.equals(usuarioDestino)) {
            return new OperacionResultado(false, "No puedes transferirte a ti mismo.");
        }
        if (Double.isNaN(monto) || monto <= 0) {
            return new OperacionResultado(false, "Ingresa un monto válido.");
        }

        Cuenta cuentaEmisor = tipoCuentaOrigen.equalsIgnoreCase("corriente")
                ? getCuentaCorriente(usuarioOrigen)
                : getCuentaAhorros(usuarioOrigen);

        if (cuentaEmisor == null) return new OperacionResultado(false, "Cuenta origen no disponible.");

        if (monto > cuentaEmisor.getSaldo()) {
            return new OperacionResultado(false,
                    "Saldo insuficiente. Tu saldo es $" + cuentaEmisor.formatPesos(cuentaEmisor.getSaldo()));
        }

        CuentaAhorros cuentaDestino = getCuentaAhorros(usuarioDestino);
        if (cuentaDestino == null) return new OperacionResultado(false, "Cuenta destino no disponible.");

        cuentaEmisor.setSaldo(cuentaEmisor.getSaldo() - monto);
        cuentaEmisor.guardarMovimiento("Transferencia enviada a " + usuarioDestino +
                ": $" + cuentaEmisor.formatPesos(monto));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo() + monto);
        cuentaDestino.guardarMovimiento("Transferencia recibida de " + usuarioOrigen +
                ": $" + cuentaDestino.formatPesos(monto));

        movimientoRepository.guardarMovimiento(new Movimiento(usuarioOrigen, tipoCuentaOrigen.toUpperCase(), "TRANSFERENCIA", monto));
        cuentaAhorrosRepository.actualizarSaldo(getCuentaAhorros(usuarioOrigen));

        return new OperacionResultado(true,
                "Transferencia exitosa. Nuevo saldo: $" + cuentaEmisor.formatPesos(cuentaEmisor.getSaldo()));
    }

    // ── Traslado interno ─────────────────────────────────────────────────────

    @Override
    public OperacionResultado trasladarInterno(String usuario, String cuentaOrigen,
                                               String cuentaDestino, double monto) {
        if (cuentaOrigen.equalsIgnoreCase(cuentaDestino)) {
            return new OperacionResultado(false, "La cuenta origen y destino no pueden ser la misma.");
        }
        if (Double.isNaN(monto) || monto <= 0) {
            return new OperacionResultado(false, "Ingresa un monto válido.");
        }

        Cuenta origen = cuentaOrigen.equalsIgnoreCase("corriente")
                ? getCuentaCorriente(usuario) : getCuentaAhorros(usuario);
        Cuenta destino = cuentaDestino.equalsIgnoreCase("corriente")
                ? getCuentaCorriente(usuario) : getCuentaAhorros(usuario);

        if (origen == null || destino == null) {
            return new OperacionResultado(false, "Cuenta no disponible.");
        }
        if (monto > origen.getSaldo()) {
            return new OperacionResultado(false,
                    "Saldo insuficiente. Tu saldo es $" + origen.formatPesos(origen.getSaldo()));
        }

        origen.setSaldo(origen.getSaldo() - monto);
        origen.guardarMovimiento("Traslado a " + destino.getTipo().getDescription() +
                ": $" + origen.formatPesos(monto));
        destino.setSaldo(destino.getSaldo() + monto);
        destino.guardarMovimiento("Traslado desde " + origen.getTipo().getDescription() +
                ": $" + destino.formatPesos(monto));

        cuentaAhorrosRepository.actualizarSaldo(getCuentaAhorros(usuario));

        return new OperacionResultado(true,
                "Traslado exitoso. Nuevo saldo " + origen.getTipo().getDescription() +
                        ": $" + origen.formatPesos(origen.getSaldo()));
    }

    // ── Activar Cuenta Corriente ─────────────────────────────────────────────

    @Override
    public OperacionResultado activarCuentaCorriente(String usuario, double montoTraslado) {
        CuentaAhorros ahorros = getCuentaAhorros(usuario);
        CuentaCorriente corriente = getCuentaCorriente(usuario);

        if (ahorros == null || corriente == null) return new OperacionResultado(false, "Error interno.");
        if (corriente.isActiva()) return new OperacionResultado(false, "La cuenta corriente ya está activa.");
        if (montoTraslado <= 0) return new OperacionResultado(false, "Ingresa un monto válido mayor a 0.");
        if (montoTraslado > ahorros.getSaldo()) {
            return new OperacionResultado(false,
                    "No puedes trasladar más de tu saldo en ahorros ($" +
                            ahorros.formatPesos(ahorros.getSaldo()) + ").");
        }

        ahorros.setSaldo(ahorros.getSaldo() - montoTraslado);
        ahorros.guardarMovimiento("Traslado a Cuenta Corriente: $" + ahorros.formatPesos(montoTraslado));
        corriente.activar(montoTraslado);

        // Guardar en BD
        cuentaAhorrosRepository.actualizarSaldo(ahorros);
        cuentaCorrienteRepository.actualizarSaldo(corriente);
        movimientoRepository.guardarMovimiento(new Movimiento(usuario, "AHORROS", "TRASLADO", montoTraslado));

        return new OperacionResultado(true,
                "¡Cuenta Corriente activada! Saldo inicial: $" + corriente.formatPesos(montoTraslado));
    }

    // ── Activar Tarjeta de Crédito ───────────────────────────────────────────

    @Override
    public OperacionResultado activarTarjetaCredito(String usuario, CreditCardTier tier) {
        TarjetaCredito tc = getTarjetaCredito(usuario);
        if (tc == null) return new OperacionResultado(false, "Error interno.");
        if (tc.isActiva()) return new OperacionResultado(false, "La tarjeta de crédito ya está activa.");

        tc.activar(tier);

        // Guardar en BD
        tarjetaCreditoRepository.actualizarTarjeta(tc);

        return new OperacionResultado(true,
                "¡Tarjeta activada! Cupo aprobado: $" + tc.formatPesos(tc.getCupo()) +
                        " (" + tier.getDescription() + ")");
    }

    // ── Comprar con TC ───────────────────────────────────────────────────────

    @Override
    public OperacionResultado realizarCompra(String usuario, double monto, int cuotas) {
        TarjetaCredito tc = getTarjetaCredito(usuario);
        if (tc == null || !tc.isActiva()) {
            return new OperacionResultado(false, "Tarjeta de crédito no activa.");
        }
        return tc.comprar(monto, cuotas);
    }

    // ── Pagar cuota TC ───────────────────────────────────────────────────────

    @Override
    public OperacionResultado pagarCuota(String usuario, int index, String cuentaOrigen) {
        TarjetaCredito tc = getTarjetaCredito(usuario);
        Cuenta cuenta = cuentaOrigen.equalsIgnoreCase("corriente")
                ? getCuentaCorriente(usuario) : getCuentaAhorros(usuario);
        if (tc == null || cuenta == null) return new OperacionResultado(false, "Cuenta no disponible.");
        return tc.pagarCuota(index, cuenta);
    }

    // ── Pagar total TC ───────────────────────────────────────────────────────

    @Override
    public OperacionResultado pagarTotal(String usuario, int index, String cuentaOrigen) {
        TarjetaCredito tc = getTarjetaCredito(usuario);
        Cuenta cuenta = cuentaOrigen.equalsIgnoreCase("corriente")
                ? getCuentaCorriente(usuario) : getCuentaAhorros(usuario);
        if (tc == null || cuenta == null) return new OperacionResultado(false, "Cuenta no disponible.");
        return tc.pagarTotal(index, cuenta);
    }

    // ── Ver Saldo ────────────────────────────────────────────────────────────

    @Override
    public void verSaldo(String usuario) {
        CuentaAhorros ca = getCuentaAhorros(usuario);
        CuentaCorriente cc = getCuentaCorriente(usuario);
        TarjetaCredito tc = getTarjetaCredito(usuario);

        System.out.println("\n=== Mi Saldo ===");
        if (ca != null)
            System.out.println("Cuenta Ahorros:    $" + ca.formatPesos(ca.getSaldo()));
        if (cc != null && cc.isActiva())
            System.out.println("Cuenta Corriente:  $" + cc.formatPesos(cc.getSaldo()));
        if (tc != null && tc.isActiva())
            System.out.println("TC Disponible:     $" + tc.formatPesos(tc.getCupoDisponible()));
    }

    // ── Ver Movimientos ──────────────────────────────────────────────────────

    @Override
    public void verMovimientos(String usuario) {
        System.out.println("\n=== Movimientos desde base de datos ===");
        List<miplata.domain.Movimiento> movimientos = movimientoRepository.findByUsuario(usuario);
        if (movimientos.isEmpty()) {
            System.out.println("  Sin movimientos registrados.");
        } else {
            movimientos.forEach(m -> System.out.println("  " + m));
        }
    }

    // ── Helpers privados ─────────────────────────────────────────────────────

    private CuentaAhorros getCuentaAhorros(String usuario) {
        return clienteRepository.findCuentaAhorros(usuario).orElse(null);
    }

    private CuentaCorriente getCuentaCorriente(String usuario) {
        return clienteRepository.findCuentaCorriente(usuario).orElse(null);
    }

    private TarjetaCredito getTarjetaCredito(String usuario) {
        return clienteRepository.findTarjetaCredito(usuario).orElse(null);
    }
}