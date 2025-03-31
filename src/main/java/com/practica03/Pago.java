package com.practica03;

public class Pago {
    private String id;
    private String metodoPago;
    private double monto;
    private boolean aprobado;

    public Pago(String id, String metodoPago, double monto) {
        this.id = id;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.aprobado = false;
    }

    public String getId() { return id; }
    public String getMetodoPago() { return metodoPago; }
    public double getMonto() { return monto; }
    public boolean isAprobado() { return aprobado; }
    public void setAprobado(boolean aprobado) { this.aprobado = aprobado; }
}