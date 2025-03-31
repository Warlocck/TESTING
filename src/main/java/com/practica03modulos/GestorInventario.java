package com.practica03modulos;


import com.practica03.Producto;
import java.util.HashMap;
import java.util.Map;

public class GestorInventario {
    private final Map<String, Producto> productos;

    public GestorInventario() {
        this.productos = new HashMap<>();
    }

    public void agregarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (productos.containsKey(producto.getId())) {
            throw new IllegalArgumentException("Producto ya existe: " + producto.getId());
        }
        productos.put(producto.getId(), producto);
    }

    public boolean verificarDisponibilidad(String productoId, int cantidad) {
        if (cantidad <= 0) {
            return false;
        }
        Producto producto = productos.get(productoId);
        return producto != null && producto.getStock() >= cantidad;
    }

    public void actualizarStock(String productoId, int cantidad) {
        Producto producto = productos.get(productoId);
        if (producto != null) {
            int nuevoStock = producto.getStock() - cantidad;
            if (nuevoStock < 0) {
                throw new IllegalStateException("Stock no puede ser negativo");
            }
            producto.setStock(nuevoStock);
        }
    }

    public Producto obtenerProducto(String productoId) {
        return productos.get(productoId);
    }
}