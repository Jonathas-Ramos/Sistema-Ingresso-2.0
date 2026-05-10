package com.Jonathas.model;

import java.time.LocalDateTime;

public class IngressoMeia extends Ingresso {

    public IngressoMeia() {
    }

    public IngressoMeia(String evento, LocalDateTime dataEvento, double valorBase) {
        super(evento, dataEvento, valorBase); // E repassamos ele para a classe mãe aqui
    }

    @Override
    public double calcularValor() {
        return getValorBase() / 2.0; // 50% de desconto
    }

    @Override
    public String imprimirIngresso() {
        return "INGRESSO MEIA-ENTRADA | Evento: " + getEvento() + " | Valor Final: R$ " + calcularValor();
    }
}
