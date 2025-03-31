package com.practica03modulos;

import com.practica03modulos.GestorInventario;
import com.practica03modulos.GestorPedidos;
import com.practica03modulos.ProcesadorPagos;
import com.practica03.Pedido;
import com.practica03.Producto;

public class Main {
    public static void main(String[] args) {
        GestorInventario inventario = new GestorInventario();
        ProcesadorPagos procesadorPagos = new ProcesadorPagos();
        GestorPedidos gestorPedidos = new GestorPedidos(inventario, procesadorPagos);

        Producto laptop = new Producto("P001", "Laptop", 999.99, 10);
        Producto mouse = new Producto("P002", "Mouse", 25.50, 20);
        inventario.agregarProducto(laptop);
        inventario.agregarProducto(mouse);

        Pedido pedido1 = new Pedido("ORD-001");
        pedido1.agregarProducto(mouse, 2);
        System.out.println("Resultado pedido 1: " + gestorPedidos.crearPedido(pedido1));

        Pedido pedido2 = new Pedido("ORD-002");
        pedido2.agregarProducto(laptop, 15);
        System.out.println("Resultado pedido 2: " + gestorPedidos.crearPedido(pedido2));

        Pedido pedido3 = new Pedido("ORD-003");
        pedido3.agregarProducto(laptop, 2); // Total = 1999.98 > 1000
        System.out.println("Resultado pedido 3: " + gestorPedidos.crearPedido(pedido3));
    }
}