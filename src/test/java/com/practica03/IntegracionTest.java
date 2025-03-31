package com.practica03;

import com.practica03modulos.GestorInventario;
import com.practica03modulos.GestorPedidos;
import com.practica03modulos.ProcesadorPagos;
import com.practica03.Pago;
import com.practica03.Pedido;
import com.practica03.Producto;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

class IntegracionTest {
    private GestorInventario inventario;
    private ProcesadorPagos procesadorPagos;
    private GestorPedidos gestorPedidos;
    private Producto producto1, producto2;

    @BeforeEach
    void setUp() {
        inventario = new GestorInventario();
        procesadorPagos = new ProcesadorPagos();
        gestorPedidos = new GestorPedidos(inventario, procesadorPagos);

        producto1 = new Producto("P001", "Laptop", 999.99, 10);
        producto2 = new Producto("P002", "Mouse", 25.50, 20);

        inventario.agregarProducto(producto1);
        inventario.agregarProducto(producto2);
    }

    // Caso 1: Validación de stock antes del pago (éxito)
    @Test
    void testValidacionStockAntesPago_Exito() {
        Pedido pedido = new Pedido("ORD-001");
        pedido.agregarProducto(producto2, 1);
        String resultado = gestorPedidos.crearPedido(pedido);
        assertEquals("Pedido completado exitosamente", resultado);
        assertEquals(19, producto2.getStock());
    }

    // Caso 2: Validación de stock antes del pago (fallo)
    @Test
    void testValidacionStockAntesPago_Fallo() {
        Pedido pedido = new Pedido("ORD-002");
        pedido.agregarProducto(producto1, 15); // Stock insuficiente

        String resultado = gestorPedidos.crearPedido(pedido);

        assertTrue(resultado.contains("no disponible"));
        assertEquals(10, producto1.getStock()); // Stock no cambia
    }

    // Caso 3: Actualización de inventario después de compra exitosa
    @Test
    void testActualizacionInventarioCompraExitosa() {
        Pedido pedido = new Pedido("ORD-003");
        pedido.agregarProducto(producto2, 5);

        gestorPedidos.crearPedido(pedido);

        assertEquals(15, producto2.getStock());
    }

    // Caso 4: Cancelación de pedido por fallo en pago
    @Test
    void testCancelacionPedidoFalloPago() {
        Pedido pedido = new Pedido("ORD-004");
        pedido.agregarProducto(producto1, 2); // Total $1999.98 > $1000 límite

        String resultado = gestorPedidos.crearPedido(pedido);

        assertEquals("Pago fallido", resultado);
        assertEquals(10, producto1.getStock()); // Stock no cambia
    }

    // Caso 5: Notificación de confirmación al cliente
    @Test
    void testNotificacionConfirmacionCliente() {
        Pedido pedido = new Pedido("ORD-005");
        pedido.agregarProducto(producto2, 2);

        String resultado = gestorPedidos.crearPedido(pedido);

        assertEquals("COMPLETADO", pedido.getEstado());
        assertTrue(resultado.contains("exitosamente"));
    }

    // Caso 6: Manejo de concurrencia en inventario
    @Test
    void testManejoConcurrenciaInventario() throws InterruptedException {
        Producto productoLimited = new Producto("P003", "Monitor", 199.99, 1);
        inventario.agregarProducto(productoLimited);

        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger exitos = new AtomicInteger(0);

        Runnable compra = () -> {
            try {
                Pedido pedido = new Pedido("ORD-" + Thread.currentThread().getId());
                pedido.agregarProducto(productoLimited, 1);
                if (gestorPedidos.crearPedido(pedido).contains("exitosamente")) {
                    exitos.incrementAndGet();
                }
            } finally {
                latch.countDown();
            }
        };

        new Thread(compra).start();
        new Thread(compra).start();
        latch.await(2, TimeUnit.SECONDS);

        assertEquals(1, exitos.get());
        assertEquals(0, productoLimited.getStock());
    }

    // Caso 7: Pedido con múltiples productos y validación parcial
    @Test
    void testPedidoMultiplesProductosValidacionParcial() {
        Producto productoSinStock = new Producto("P004", "Teclado", 45.00, 0);
        inventario.agregarProducto(productoSinStock);

        Pedido pedido = new Pedido("ORD-007");
        pedido.agregarProducto(producto1, 2); // Disponible
        pedido.agregarProducto(productoSinStock, 1); // Sin stock

        String resultado = gestorPedidos.crearPedido(pedido);

        assertTrue(resultado.contains("no disponible"));
        assertEquals(10, producto1.getStock()); // Stock no cambia
    }

    // Caso 8: Reintento de pago exitoso después de fallo
    @Test
    void testReintentoPagoExitoso() {
        ProcesadorPagos procesadorMock = new ProcesadorPagos() {
            private int intentos = 0;
            @Override
            public boolean procesarPago(Pago pago) {
                intentos++;
                return intentos > 1;
            }
        };

        GestorPedidos gestorConMock = new GestorPedidos(inventario, procesadorMock);
        Pedido pedido = new Pedido("ORD-008");
        pedido.agregarProducto(producto2, 1);

        String resultado = gestorConMock.crearPedido(pedido);

        assertEquals("Pedido completado exitosamente", resultado);
    }

    // Caso 9: Cancelación manual de pedido
    @Test
    void testCancelacionManualPedido() {
        Pedido pedido = new Pedido("ORD-009");
        pedido.agregarProducto(producto2, 2);
        pedido.setEstado("CANCELADO");

        String resultado = gestorPedidos.crearPedido(pedido);

        assertTrue(resultado.contains("cancelado"));
        assertEquals(20, producto2.getStock());
    }

    // Caso 10: Pedido con cantidad cero
    @Test
    void testPedidoCantidadCero() {
        Pedido pedido = new Pedido("ORD-010");

        assertThrows(IllegalArgumentException.class, () -> {
            pedido.agregarProducto(producto1, 0);
        });
    }

    // Caso 11: Pedido con producto recién agregado
    @Test
    void testPedidoProductoRecienAgregado() {
        Producto nuevoProducto = new Producto("P005", "Webcam", 59.99, 3);
        inventario.agregarProducto(nuevoProducto);

        Pedido pedido = new Pedido("ORD-011");
        pedido.agregarProducto(nuevoProducto, 2);

        String resultado = gestorPedidos.crearPedido(pedido);

        assertEquals("Pedido completado exitosamente", resultado);
        assertEquals(1, nuevoProducto.getStock());
    }

    // Caso 12: Pago con diferentes métodos
    @Test
    void testPagoConDiferentesMetodos() {
        Pago pagoTarjeta = new Pago("PAY-012A", "TARJETA", 25.50);
        Pago pagoTransferencia = new Pago("PAY-012B", "TRANSFERENCIA", 25.50);

        assertTrue(procesadorPagos.procesarPago(pagoTarjeta));
        assertTrue(procesadorPagos.procesarPago(pagoTransferencia));
    }

    // Caso 13: Verificación de límites de stock
    @Test
    void testLimiteCompraPorProducto() {
        Pedido pedido = new Pedido("ORD-013");
        pedido.agregarProducto(producto2, 21);

        String resultado = gestorPedidos.crearPedido(pedido);

        assertTrue(resultado.contains("no disponible"));
        assertEquals(20, producto2.getStock());
    }

    // Caso 14: Pedido con descuento aplicado
    @Test
    void testPedidoConDescuento() {
        Pedido pedido = new Pedido("ORD-014");
        pedido.agregarProducto(producto2, 4);

        // Aplicar 10% de descuento
        double totalConDescuento = pedido.calcularTotal() * 0.9;
        Pago pago = new Pago("PAY-014", "TARJETA", totalConDescuento);

        assertTrue(procesadorPagos.procesarPago(pago));
        assertEquals(91.80, totalConDescuento, 0.01);
    }

    // Caso 15: Notificaciones de estado
    @Test
    void testNotificacionesEstado() {
        Pedido pedido = new Pedido("ORD-015");
        pedido.agregarProducto(producto2, 1);

        String resultado = gestorPedidos.crearPedido(pedido);

        assertEquals("COMPLETADO", pedido.getEstado());
        assertTrue(resultado.contains("exitosamente"));
    }
}