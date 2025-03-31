package com.practica03modulos;

import com.practica03.Pago;


public class ProcesadorPagos {
    public boolean procesarPago(Pago pago) {

        if (pago == null || pago.getMonto() <= 0) {
            return false;
        }
        return pago.getMonto() <= 1000;
    }
}