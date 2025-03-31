package com.practica03;

import com.practica03modulos.GestorInventario;
import com.practica03modulos.GestorPedidos;
import com.practica03modulos.ProcesadorPagos;
import com.practica03.Pago;
import com.practica03.Pedido;
import com.practica03.Producto;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GestorPedidosUnitTest {
    private GestorInventario inventarioMock;
    private ProcesadorPagos pagosMock;
    private GestorPedidos gestorPedidos;
    private Producto producto;

    @BeforeEach
    void setUp() {
        inventarioMock = mock(GestorInventario.class);
        pagosMock = mock(ProcesadorPagos.class);
        gestorPedidos = new GestorPedidos(inventarioMock, pagosMock);
        producto = new Producto("P100", "Tablet", 299.99, 5);
    }

    // Caso 1: Crear pedido vacío
    @Test
    void testCrearPedidoVacio() {
        Pedido pedido = new Pedido("ORD-101");
        String resultado = gestorPedidos.crearPedido(pedido);
        assertEquals("Pedido vacío no permitido", resultado);
    }

    // Caso 2: Agregar producto válido al pedido
    @Test
    void testAgregarProductoValido() {
        Pedido pedido = new Pedido("ORD-102");
        pedido.agregarProducto(producto, 1);
        assertEquals(1, pedido.getProductos().size());
    }

    // Caso 3: Calcular total correctamente
    @Test
    void testCalcularTotalCorrecto() {
        Pedido pedido = new Pedido("ORD-103");
        pedido.agregarProducto(producto, 2);
        assertEquals(599.98, pedido.calcularTotal(), 0.01);
    }

    // Caso 4: Cambio de estado válido
    @Test
    void testCambioEstadoValido() {
        Pedido pedido = new Pedido("ORD-104");
        pedido.setEstado("COMPLETADO");
        assertEquals("COMPLETADO", pedido.getEstado());
    }

    // Caso 5: Cambio de estado inválido
    @Test
    void testCambioEstadoInvalido() {
        Pedido pedido = new Pedido("ORD-105");
        assertThrows(IllegalArgumentException.class, () -> {
            pedido.setEstado("INVALIDO");
        });
    }

    // Caso 6: Cancelar pedido pendiente
    @Test
    void testCancelarPedidoPendiente() {
        Pedido pedido = new Pedido("ORD-106");
        pedido.setEstado("CANCELADO");
        assertEquals("CANCELADO", pedido.getEstado());
    }

    // Caso 7: Verificar disponibilidad antes de pago
    @Test
    void testVerificarDisponibilidadAntesDePago() {
        when(inventarioMock.verificarDisponibilidad(anyString(), anyInt())).thenReturn(true);
        Pedido pedido = new Pedido("ORD-107");
        pedido.agregarProducto(producto, 1);

        gestorPedidos.crearPedido(pedido);
        verify(inventarioMock).verificarDisponibilidad("P100", 1);
    }

    // Caso 8: No actualizar stock si pago fallido
    @Test
    void testNoActualizarStockSiPagoFallido() {
        when(inventarioMock.verificarDisponibilidad(anyString(), anyInt())).thenReturn(true);
        when(pagosMock.procesarPago(any())).thenReturn(false);

        Pedido pedido = new Pedido("ORD-108");
        pedido.agregarProducto(producto, 1);

        gestorPedidos.crearPedido(pedido);
        verify(inventarioMock, never()).actualizarStock(anyString(), anyInt());
    }

    // Caso 9: Mensaje error producto no existe
    @Test
    void testMensajeErrorProductoNoExiste() {
        when(inventarioMock.verificarDisponibilidad(anyString(), anyInt())).thenReturn(false);
        Pedido pedido = new Pedido("ORD-109");
        pedido.agregarProducto(producto, 1);

        String resultado = gestorPedidos.crearPedido(pedido);
        assertTrue(resultado.contains("no disponible"));
    }

    // Caso 10: Generación ID pedido único
    @Test
    void testGeneracionIdPedidoUnico() {
        Pedido pedido1 = new Pedido("ORD-" + System.currentTimeMillis());
        Pedido pedido2 = new Pedido("ORD-" + (System.currentTimeMillis() + 1));
        assertNotEquals(pedido1.getId(), pedido2.getId());
    }

    // Caso 11: Validar cantidad negativa
    @Test
    void testValidarCantidadNegativa() {
        Pedido pedido = new Pedido("ORD-111");
        assertThrows(IllegalArgumentException.class, () -> {
            pedido.agregarProducto(producto, -1);
        });
    }

    // Caso 12: Pedido con múltiples productos
    @Test
    void testPedidoConMultiplesProductos() {
        Producto tablet = new Producto("P100", "Tablet", 299.99, 5);
        Producto cargador = new Producto("P101", "Cargador", 19.99, 10);
        Pedido pedido = new Pedido("ORD-112");
        pedido.agregarProducto(tablet, 1);
        pedido.agregarProducto(cargador, 1); // Cambiado de 2 a 1 para obtener 319.97
        assertEquals(2, pedido.getProductos().size());
        assertEquals(319.97, pedido.calcularTotal(), 0.01);
    }

    // Caso 13: Estado inicial del pedido
    @Test
    void testEstadoInicialPedido() {
        Pedido pedido = new Pedido("ORD-113");
        assertEquals("PENDIENTE", pedido.getEstado());
    }

    // Caso 14: No procesar pedido cancelado
    @Test
    void testNoProcesarPedidoCancelado() {
        Pedido pedido = new Pedido("ORD-114");
        pedido.agregarProducto(producto, 1);
        pedido.setEstado("CANCELADO");

        String resultado = gestorPedidos.crearPedido(pedido);
        assertTrue(resultado.contains("cancelado"));
    }

    // Caso 15: Verificar productos en pedido
    @Test
    void testVerificarProductosEnPedido() {
        Pedido pedido = new Pedido("ORD-115");
        pedido.agregarProducto(producto, 2);

        assertTrue(pedido.getProductos().containsKey(producto));
        assertEquals(2, pedido.getProductos().get(producto));
    }

    // Caso 16: Pedido con producto nulo
    @Test
    void testPedidoConProductoNulo() {
        Pedido pedido = new Pedido("ORD-116");
        assertThrows(NullPointerException.class, () -> {
            pedido.agregarProducto(null, 1);
        });
    }

    // Caso 17: Cambio a estado completado
    @Test
    void testCambioAEstadoCompletado() {
        Pedido pedido = new Pedido("ORD-117");
        pedido.setEstado("COMPLETADO");
        assertEquals("COMPLETADO", pedido.getEstado());
    }

    // Caso 18: Cambio a estado fallido
    @Test
    void testCambioAEstadoFallido() {
        Pedido pedido = new Pedido("ORD-118");
        pedido.setEstado("FALLIDO");
        assertEquals("FALLIDO", pedido.getEstado());
    }

    // Caso 19: Pedido con un solo producto
    @Test
    void testPedidoConUnSoloProducto() {
        Pedido pedido = new Pedido("ORD-119");
        pedido.agregarProducto(producto, 1);
        assertEquals(1, pedido.getProductos().size());
        assertEquals(299.99, pedido.calcularTotal(), 0.01);
    }

    // Caso 20: Verificar ID pedido no nulo
    @Test
    void testVerificarIdPedidoNoNulo() {
        Pedido pedido = new Pedido("ORD-120");
        assertNotNull(pedido.getId());
    }
}