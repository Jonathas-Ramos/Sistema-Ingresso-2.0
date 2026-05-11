package com.Jonathas.model;

import java.time.LocalDateTime; // Import necessário para a data

public class IngressoVIP extends Ingresso {
    private double taxaVIP;

    public IngressoVIP() {
    }

    // Construtor atualizado recebendo o LocalDateTime dataEvento
    public IngressoVIP(String evento, LocalDateTime dataEvento, double valorBase, double taxaVIP) {
        super(evento, dataEvento, valorBase); // Repassando a data para a classe mãe
        this.taxaVIP = taxaVIP;
    }

    @Override
    public double calcularValor() {
        return getValorBase() + taxaVIP;
    }

    @Override
    public String imprimirIngresso() {
        return "INGRESSO VIP | Evento: " + getEvento() + " | Taxa VIP: R$ " + taxaVIP + " | Valor Final: R$ " + calcularValor();
    }

    public double getTaxaVIP() { return taxaVIP; }
    public void setTaxaVIP(double taxaVIP) { this.taxaVIP = taxaVIP; }
}
