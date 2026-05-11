package com.Jonathas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "eventos")
public class Evento {
    @Id
    private String id;
    private String nome;
    private String descricao;
    private LocalDateTime dataHora;
    private String local;
    private int quantidadeDisponivel;
    private double valorIngresso;

    public Evento() {
    }

    public Evento(String nome, String descricao, LocalDateTime dataHora, String local, int quantidadeDisponivel, double valorIngresso) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataHora = dataHora;
        this.local = local;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.valorIngresso = valorIngresso;
    }

    public boolean possuiIngressosDisponiveis() {
        return quantidadeDisponivel > 0;
    }

    public void reservarUmaUnidade() {
        if (!possuiIngressosDisponiveis()) {
            throw new RuntimeException("Nao ha ingressos disponiveis para este evento.");
        }
        quantidadeDisponivel--;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(int quantidadeDisponivel) { this.quantidadeDisponivel = quantidadeDisponivel; }
    public double getValorIngresso() { return valorIngresso; }
    public void setValorIngresso(double valorIngresso) { this.valorIngresso = valorIngresso; }
}
