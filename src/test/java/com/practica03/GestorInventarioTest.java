package com.practica03.unit;

import com.practica03modulos.GestorInventario;
import com.practica03modulos.GestorPedidos;
import com.practica03modulos.ProcesadorPagos;
import com.practica03.Pago;
import com.practica03.Pedido;
import com.practica03.Producto;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class GestorInventarioUnitTest {
    private GestorInventario gestor;
    private Producto producto;

    @BeforeEach
    void setUp() {
        gestor = new GestorInventario();
        producto = new Producto("P200", "Smartphone", 799.99, 10);
        gestor.agregarProducto(producto);
    }

    // Caso 1: Agregar producto válido
    @Test
    void testAgregarProductoValido() {
        Producto nuevo = new Producto("P201", "Cargador", 19.99, 20);
        gestor.agregarProducto(nuevo);
        assertNotNull(gestor.obtenerProducto("P201"));
    }

    // Caso 2: Agregar producto duplicado
    @Test
    void testAgregarProductoDuplicado() {
        assertThrows(IllegalArgumentException.class, () -> {
            gestor.agregarProducto(new Producto("P200", "Duplicado", 1.00, 1));
        });
    }

    // Caso 3: Verificar stock suficiente
    @Test
    void testVerificarStockSuficiente() {
        assertTrue(gestor.verificarDisponibilidad("P200", 5));
    }

    // Caso 4: Verificar stock insuficiente
    @Test
    void testVerificarStockInsuficiente() {
        assertFalse(gestor.verificarDisponibilidad("P200", 15));
    }

    // Caso 5: Actualizar stock correctamente
    @Test
    void testActualizarStockCorrectamente() {
        gestor.actualizarStock("P200", 3);
        assertEquals(7, producto.getStock());
    }

    // Caso 6: Actualizar stock negativo
    @Test
    void testActualizarStockNegativo() {
        assertThrows(IllegalStateException.class, () -> {
            gestor.actualizarStock("P200", 11);
        });
    }

    // Caso 7: Obtener producto existente
    @Test
    void testObtenerProductoExistente() {
        assertEquals(producto, gestor.obtenerProducto("P200"));
    }

    // Caso 8: Obtener producto no existente
    @Test
    void testObtenerProductoNoExistente() {
        assertNull(gestor.obtenerProducto("P999"));
    }

    // Caso 9: Actualizar precio producto
    @Test
    void testActualizarPrecioProducto() {
        producto.setPrecio(699.99);
        assertEquals(699.99, producto.getPrecio(), 0.01);
    }

    // Caso 10: Verificar producto recién agregado
    @Test
    void testVerificarProductoRecienAgregado() {
        Producto nuevo = new Producto("P202", "Tablet", 299.99, 5);
        gestor.agregarProducto(nuevo);
        assertTrue(gestor.verificarDisponibilidad("P202", 1));
    }

    // Caso 11: Actualizar stock a cero
    @Test
    void testActualizarStockACero() {
        gestor.actualizarStock("P200", 10);
        assertEquals(0, producto.getStock());
    }

    // Caso 12: Verificar disponibilidad con cantidad cero
    @Test
    void testVerificarDisponibilidadCantidadCero() {
        assertFalse(gestor.verificarDisponibilidad("P200", 0));
    }

    // Caso 13: Verificar disponibilidad producto no existente
    @Test
    void testVerificarDisponibilidadProductoNoExistente() {
        assertFalse(gestor.verificarDisponibilidad("P999", 1));
    }

    // Caso 14: Actualizar stock producto no existente
    @Test
    void testActualizarStockProductoNoExistente() {
        assertDoesNotThrow(() -> {
            gestor.actualizarStock("P999", 1);
        });
    }

    // Caso 15: Producto con stock inicial cero
    @Test
    void testProductoConStockInicialCero() {
        Producto sinStock = new Producto("P203", "Sin Stock", 9.99, 0);
        gestor.agregarProducto(sinStock);
        assertFalse(gestor.verificarDisponibilidad("P203", 1));
    }

    // Caso 16: Actualizar múltiples veces stock
    @Test
    void testActualizarMultiplesVecesStock() {
        gestor.actualizarStock("P200", 2);
        gestor.actualizarStock("P200", 3);
        assertEquals(5, producto.getStock());
    }

    // Caso 17: Verificar todos los campos producto
    @Test
    void testVerificarTodosLosCamposProducto() {
        Producto p = gestor.obtenerProducto("P200");
        assertAll("Verificar campos producto",
                () -> assertEquals("P200", p.getId()),
                () -> assertEquals("Smartphone", p.getNombre()),
                () -> assertEquals(799.99, p.getPrecio()),
                () -> assertEquals(10, p.getStock())
        );
    }

    // Caso 18: Agregar múltiples productos
    @Test
    void testAgregarMultiplesProductos() {
        int cantidadInicial = gestor.obtenerProducto("P200").getStock();
        Producto p2 = new Producto("P204", "Producto 2", 49.99, 7);
        gestor.agregarProducto(p2);

        assertEquals(cantidadInicial, producto.getStock());
        assertEquals(7, p2.getStock());
    }

    // Caso 19: No permitir stock negativo al agregar
    @Test
    void testNoPermitirStockNegativoAlAgregar() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Producto("P205", "Inválido", 9.99, -1);
        });
    }

    // Caso 20: No permitir precio negativo
    @Test
    void testNoPermitirPrecioNegativo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Producto("P206", "Inválido", -0.01, 1);
        });
    }
}