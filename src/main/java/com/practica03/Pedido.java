package com.practica03;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Pedido {
    private final String id;
    private final Map<Producto, Integer> productos;
    private String estado;

    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "PENDIENTE",
            "COMPLETADO",
            "CANCELADO",
            "FALLIDO"
    );

    public Pedido(String id) {
        this.id = id;
        this.productos = new HashMap<>();
        this.estado = "PENDIENTE";
    }

    public void setEstado(String estado) {
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new IllegalArgumentException("Estado de pedido inválido: " + estado);
        }
        this.estado = estado;
    }

    public void agregarProducto(Producto producto, int cantidad) {
        if (producto == null) {
            throw new NullPointerException("El producto no puede ser nulo");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        productos.put(producto, cantidad);
    }

    public double calcularTotal() {
        return productos.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPrecio() * e.getValue())
                .sum();
    }

    public String getId() { return id; }
    public Map<Producto, Integer> getProductos() { return productos; }
    public String getEstado() { return estado; }
}