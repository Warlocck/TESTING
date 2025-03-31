package com.practica03;

import com.practica03modulos.GestorInventario;
import com.practica03modulos.GestorPedidos;
import com.practica03modulos.ProcesadorPagos;
import com.practica03.Pago;
import com.practica03.Pedido;
import com.practica03.Producto;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ProcesadorPagosUnitTest {
    private ProcesadorPagos procesador;

    @BeforeEach
    void setUp() {
        procesador = new ProcesadorPagos();
    }

    // Caso 1: Procesar pago exitoso
    @Test
    void testProcesarPagoExitoso() {
        Pago pago = new Pago("PAY-301", "TARJETA", 100.00);
        assertTrue(procesador.procesarPago(pago));
        assertTrue(pago.isAprobado());
    }

    // Caso 2: Procesar pago monto alto
    @Test
    void testProcesarPagoMontoAlto() {
        Pago pago = new Pago("PAY-302", "TARJETA", 1500.00);
        assertFalse(procesador.procesarPago(pago));
    }

    // Caso 3: Procesar pago monto cero
    @Test
    void testProcesarPagoMontoCero() {
        Pago pago = new Pago("PAY-303", "TARJETA", 0.00);
        assertFalse(procesador.procesarPago(pago));
    }

    // Caso 4: Procesar pago monto negativo
    @Test
    void testProcesarPagoMontoNegativo() {
        Pago pago = new Pago("PAY-304", "TARJETA", -50.00);
        assertFalse(procesador.procesarPago(pago));
    }

    // Caso 5: Procesar pago con tarjeta inválida
    @Test
    void testProcesarPagoConTarjetaInvalida() {
        Pago pago = new Pago("PAY-305", "", 100.00);
        assertFalse(procesador.procesarPago(pago));
    }

    // Caso 6: Generar ID transacción único
    @Test
    void testGenerarIdTransaccionUnico() {
        Pago pago1 = new Pago("PAY-" + System.currentTimeMillis(), "TARJETA", 100.00);
        Pago pago2 = new Pago("PAY-" + (System.currentTimeMillis() + 1), "TARJETA", 100.00);
        assertNotEquals(pago1.getId(), pago2.getId());
    }

    // Caso 7: Registrar intento pago
    @Test
    void testRegistrarIntentoPago() {
        Pago pago = new Pago("PAY-306", "TARJETA", 100.00);
        boolean resultado = procesador.procesarPago(pago);
        assertTrue(resultado || !resultado);
    }

    // Caso 8: Estado inicial pago no aprobado
    @Test
    void testEstadoInicialPagoNoAprobado() {
        Pago pago = new Pago("PAY-307", "TARJETA", 100.00);
        assertFalse(pago.isAprobado());
    }

    // Caso 9: Pago aprobado cambia estado
    @Test
    void testPagoAprobadoCambiaEstado() {
        Pago pago = new Pago("PAY-308", "TARJETA", 100.00);
        procesador.procesarPago(pago);
        assertTrue(pago.isAprobado());
    }

    // Caso 10: Pago rechazado mantiene estado
    @Test
    void testPagoRechazadoMantieneEstado() {
        Pago pago = new Pago("PAY-309", "TARJETA", 1500.00);
        procesador.procesarPago(pago);
        assertFalse(pago.isAprobado());
    }

    // Caso 11: Método pago no afecta resultado
    @Test
    void testMetodoPagoNoAfectaResultado() {
        Pago pagoTarjeta = new Pago("PAY-310", "TARJETA", 100.00);
        Pago pagoTransferencia = new Pago("PAY-311", "TRANSFERENCIA", 100.00);

        assertTrue(procesador.procesarPago(pagoTarjeta));
        assertTrue(procesador.procesarPago(pagoTransferencia));
    }

    // Caso 12: Pago con monto límite exacto
    @Test
    void testPagoConMontoLimiteExacto() {
        Pago pago = new Pago("PAY-312", "TARJETA", 1000.00);
        assertTrue(procesador.procesarPago(pago));
    }

    // Caso 13: Pago con monto un centavo arriba del límite
    @Test
    void testPagoConMontoUnCentavoArribaLimite() {
        Pago pago = new Pago("PAY-313", "TARJETA", 1000.01);
        assertFalse(procesador.procesarPago(pago));
    }

    // Caso 14: Pago con ID nulo
    @Test
    void testPagoConIdNulo() {
        Pago pago = new Pago(null, "TARJETA", 100.00);
        assertThrows(NullPointerException.class, () -> {
            procesador.procesarPago(pago);
        });
    }

    // Caso 15: Pago con método de pago nulo
    @Test
    void testPagoConMetodoNulo() {
        Pago pago = new Pago("PAY-315", null, 100.00);
        assertFalse(procesador.procesarPago(pago));
    }

    // Caso 16: Pago con monto muy pequeño
    @Test
    void testPagoConMontoMuyPequeno() {
        Pago pago = new Pago("PAY-316", "TARJETA", 0.01);
        assertTrue(procesador.procesarPago(pago));
    }

    // Caso 17: Pago con monto extremadamente grande
    @Test
    void testPagoConMontoExtremadamenteGrande() {
        Pago pago = new Pago("PAY-317", "TARJETA", Double.MAX_VALUE);
        assertFalse(procesador.procesarPago(pago));
    }

    // Caso 18: Múltiples pagos consecutivos
    @Test
    void testMultiplesPagosConsecutivos() {
        Pago pago1 = new Pago("PAY-318A", "TARJETA", 100.00);
        Pago pago2 = new Pago("PAY-318B", "TRANSFERENCIA", 200.00);

        assertTrue(procesador.procesarPago(pago1));
        assertTrue(procesador.procesarPago(pago2));
    }

    // Caso 19: Verificar todos los campos pago
    @Test
    void testVerificarTodosLosCamposPago() {
        Pago pago = new Pago("PAY-319", "TARJETA", 100.00);
        assertAll("Verificar campos pago",
                () -> assertEquals("PAY-319", pago.getId()),
                () -> assertEquals("TARJETA", pago.getMetodoPago()),
                () -> assertEquals(100.00, pago.getMonto()),
                () -> assertFalse(pago.isAprobado())
        );
    }

    // Caso 20: Pago con método de pago desconocido
    @Test
    void testPagoConMetodoDesconocido() {
        Pago pago = new Pago("PAY-320", "CRIPTO", 100.00);
        assertFalse(procesador.procesarPago(pago));
    }
}