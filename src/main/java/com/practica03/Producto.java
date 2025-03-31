package com.practica03;


public class Producto {
    private final String id;
    private final String nombre;
    private double precio;
    private int stock;

    public Producto(String id, String nombre, double precio, int stock) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID no puede ser nulo o vacío");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre no puede ser nulo o vacío");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("Precio no puede ser negativo");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock no puede ser negativo");
        }

        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }

    public void setPrecio(double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("Precio no puede ser negativo");
        }
        this.precio = precio;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock no puede ser negativo");
        }
        this.stock = stock;
    }
}