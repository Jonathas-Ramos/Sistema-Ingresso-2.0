package com.Jonathas.model;

import java.time.LocalDateTime;

public class IngressoNormal extends Ingresso {

    public IngressoNormal() {
    }

    public IngressoNormal(String evento, LocalDateTime dataEvento, double valorBase) {
        super(evento, dataEvento, valorBase);
    }

    @Override
    public double calcularValor() {
        return getValorBase(); // Valor integral
    }

    @Override
    public String imprimirIngresso() {
        return "INGRESSO NORMAL | Evento: " + getEvento() + " | Valor Final: R$ " + calcularValor();
    }
}
