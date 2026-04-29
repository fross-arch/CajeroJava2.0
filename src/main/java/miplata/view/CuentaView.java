package miplata.view;

import miplata.domain.DeudaTC;
import miplata.domain.TarjetaCredito;
import miplata.domain.enums.CreditCardTier;
import miplata.repository.ClienteRepository;
import miplata.services.CreditCardTierSelector;
import miplata.services.CuentaService;
import miplata.utils.FormValidation;

import java.util.List;

/**
 * Vista de operaciones bancarias — panel del cajero/cliente.
 * Traduce los módulos JS (moduloAhorros, moduloCorriente, moduloTC, etc.)
 * al patrón de vista con consola.
 */
public class CuentaView {

    private final CuentaService cuentaService;
    private final ClienteRepository clienteRepository;

    public CuentaView(CuentaService cuentaService, ClienteRepository clienteRepository) {
        this.cuentaService = cuentaService;
        this.clienteRepository = clienteRepository;
    }

    // ── Saldo ────────────────────────────────────────────────────────────────

    public void verSaldo(String usuario) {
        cuentaService.verSaldo(usuario);
    }

    // ── Depósito ─────────────────────────────────────────────────────────────

    public void depositar(String usuario) {
        System.out.println("¿En qué cuenta desea depositar?  1. Ahorros  2. Corriente");
        int opcion = FormValidation.validateInt("Opción");
        String tipo = (opcion == 2) ? "corriente" : "ahorros";
        double monto = FormValidation.validateDouble("Monto a depositar");
        System.out.println(cuentaService.depositar(usuario, tipo, monto));
    }

    // ── Retiro ───────────────────────────────────────────────────────────────

    public void retirar(String usuario) {
        System.out.println("¿De qué cuenta desea retirar?  1. Ahorros  2. Corriente");
        int opcion = FormValidation.validateInt("Opción");
        String tipo = (opcion == 2) ? "corriente" : "ahorros";
        double monto = FormValidation.validateDouble("Monto a retirar");
        System.out.println(cuentaService.retirar(usuario, tipo, monto));
    }

    // ── Transferir ───────────────────────────────────────────────────────────

    public void transferir(String usuarioOrigen) {
        System.out.println("¿Desde qué cuenta?  1. Ahorros  2. Corriente");
        int opcion = FormValidation.validateInt("Opción");
        String tipo = (opcion == 2) ? "corriente" : "ahorros";
        String destinatario = FormValidation.validateString("Usuario destinatario");
        double monto = FormValidation.validateDouble("Monto a transferir");
        System.out.println(cuentaService.transferir(usuarioOrigen, tipo, destinatario, monto));
    }

    // ── Traslado interno ─────────────────────────────────────────────────────

    public void trasladarInterno(String usuario) {
        System.out.println("Desde:  1. Ahorros  2. Corriente");
        int opcionOrigen = FormValidation.validateInt("Opción");
        System.out.println("Hacia:  1. Ahorros  2. Corriente");
        int opcionDestino = FormValidation.validateInt("Opción");
        String origen = (opcionOrigen == 2) ? "corriente" : "ahorros";
        String destino = (opcionDestino == 2) ? "corriente" : "ahorros";
        double monto = FormValidation.validateDouble("Monto a trasladar");
        System.out.println(cuentaService.trasladarInterno(usuario, origen, destino, monto));
    }

    // ── Movimientos ──────────────────────────────────────────────────────────

    public void verMovimientos(String usuario) {
        cuentaService.verMovimientos(usuario);
    }

    // ── Cuenta Corriente ─────────────────────────────────────────────────────

    public void activarCuentaCorriente(String usuario) {
        double monto = FormValidation.validateDouble("Monto a trasladar desde Ahorros para activar");
        System.out.println(cuentaService.activarCuentaCorriente(usuario, monto));
    }

    // ── Tarjeta de Crédito ───────────────────────────────────────────────────

    public void activarTarjetaCredito(String usuario) {
        CreditCardTier tier = CreditCardTierSelector.selectTier();
        System.out.println(cuentaService.activarTarjetaCredito(usuario, tier));
    }

    public void realizarCompra(String usuario) {
        double monto = FormValidation.validateDouble("Valor de la compra");
        System.out.println("Cuotas disponibles:");
        System.out.println("  1. 1 cuota (sin interés)   2. 2 cuotas (sin interés)");
        System.out.println("  3. 3 cuotas (1.9%/mes)     4. 6 cuotas (1.9%/mes)");
        System.out.println("  5. 7 cuotas (2.3%/mes)     6. 12 cuotas (2.3%/mes)   7. 24 cuotas (2.3%/mes)");
        int opcion = FormValidation.validateInt("Opción");
        int[] mapaCuotas = {1, 2, 3, 6, 7, 12, 24};
        int cuotas = (opcion >= 1 && opcion <= 7) ? mapaCuotas[opcion - 1] : 1;
        System.out.println(cuentaService.realizarCompra(usuario, monto, cuotas));
    }

    public void pagarCuotaTC(String usuario) {
        TarjetaCredito tc = clienteRepository.findTarjetaCredito(usuario).orElse(null);
        if (tc == null || tc.getDeudasActivas().isEmpty()) {
            System.out.println("No tienes deudas activas.");
            return;
        }

        System.out.println("\n=== Deudas Activas ===");
        List<DeudaTC> activas = tc.getDeudasActivas();
        for (int i = 0; i < activas.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + activas.get(i));
        }

        System.out.println("(Las deudas están numeradas desde 1)");
        int index = FormValidation.validateInt("Número de la deuda a pagar") - 1;
        System.out.println("¿Pagar desde?  1. Ahorros  2. Corriente");
        int opcion = FormValidation.validateInt("Opción");
        String cuentaOrigen = (opcion == 2) ? "corriente" : "ahorros";

        System.out.println("¿Tipo de pago?  1. Pagar una cuota  2. Pagar total");
        int tipoPago = FormValidation.validateInt("Opción");

        if (tipoPago == 2) {
            System.out.println(cuentaService.pagarTotal(usuario, index , cuentaOrigen));
        } else {
            System.out.println(cuentaService.pagarCuota(usuario, index, cuentaOrigen));
        }
    }
}
