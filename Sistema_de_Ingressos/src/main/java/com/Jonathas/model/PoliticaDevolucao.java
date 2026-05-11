package com.Jonathas.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PoliticaDevolucao {
    private int diasLimite = 7; // Limite de 7 dias antes do evento
    private double percentualReembolso = 0.8; // 80% do valor

    public boolean podeDevolver(Ingresso ingresso) {
        if (ingresso.getDataEvento() == null) return false;

        // Calcula quantos dias faltam para o evento
        long diasAteEvento = ChronoUnit.DAYS.between(LocalDateTime.now(), ingresso.getDataEvento());
        return diasAteEvento >= diasLimite;
    }

    public double calcularReembolso(Ingresso ingresso) {
        return ingresso.calcularValor() * percentualReembolso;
    }
}