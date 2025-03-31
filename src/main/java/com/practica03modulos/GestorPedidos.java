package com.practica03modulos;

import com.practica03.Pago;
import com.practica03.Pedido;
import com.practica03.Producto;

import java.util.Map;

public class GestorPedidos {
    private final GestorInventario gestorInventario;
    private final ProcesadorPagos procesadorPagos;

    public GestorPedidos(GestorInventario gestorInventario, ProcesadorPagos procesadorPagos) {
        this.gestorInventario = gestorInventario;
        this.procesadorPagos = procesadorPagos;
    }

    public String crearPedido(Pedido pedido) {
        if ("CANCELADO".equals(pedido.getEstado())) {
            return "Pedido cancelado no procesado";
        }
        if (pedido.getProductos() == null || pedido.getProductos().isEmpty()) {
            pedido.setEstado("CANCELADO");
            return "Pedido vacío no permitido";
        }
        for (Map.Entry<Producto, Integer> entry : pedido.getProductos().entrySet()) {
            Producto producto = entry.getKey();
            int cantidad = entry.getValue();

            if (!gestorInventario.verificarDisponibilidad(producto.getId(), cantidad)) {
                pedido.setEstado("CANCELADO");
                return "Producto " + producto.getNombre() + " no disponible";
            }
        }

        Pago pago = new Pago("PAY-" + System.currentTimeMillis(), "TARJETA", pedido.calcularTotal());
        boolean pagoExitoso = procesadorPagos.procesarPago(pago);

        if (pagoExitoso) {
            for (Map.Entry<Producto, Integer> entry : pedido.getProductos().entrySet()) {
                Producto producto = entry.getKey();
                int cantidad = entry.getValue();
                gestorInventario.actualizarStock(producto.getId(), cantidad);
            }
            pedido.setEstado("COMPLETADO");
            return "Pedido completado exitosamente";
        } else {
            pedido.setEstado("CANCELADO");
            return "Pago fallido";
        }
    }
}