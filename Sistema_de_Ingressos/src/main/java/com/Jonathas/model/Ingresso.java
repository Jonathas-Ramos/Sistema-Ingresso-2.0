package com.Jonathas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "ingressos")
public abstract class Ingresso {
    @Id
    private String id;
    private String evento;
    private String eventoId;
    private String clienteId;
    private String codigoHash;
    private String qrCodeBase64;
    private LocalDateTime dataEvento; // NOVO ATRIBUTO!
    private LocalDateTime dataEmissao;
    private LocalDateTime dataExpiracao;
    private double valorBase;
    private EstadoIngresso estado;
    @Transient
    private StatusReserva statusReserva;

    protected Ingresso() {
    }

    public Ingresso(String evento, LocalDateTime dataEvento, double valorBase) {
        this.evento = evento;
        this.dataEvento = dataEvento;
        this.valorBase = valorBase;
        this.estado = EstadoIngresso.DISPONIVEL;
    }

    public abstract double calcularValor();
    public abstract String imprimirIngresso();

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEvento() { return evento; }
    public void setEvento(String evento) { this.evento = evento; }
    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getCodigoHash() { return codigoHash; }
    public void setCodigoHash(String codigoHash) { this.codigoHash = codigoHash; }
    public String getQrCodeBase64() { return qrCodeBase64; }
    public void setQrCodeBase64(String qrCodeBase64) { this.qrCodeBase64 = qrCodeBase64; }
    public LocalDateTime getDataEvento() { return dataEvento; }
    public void setDataEvento(LocalDateTime dataEvento) { this.dataEvento = dataEvento; }
    public LocalDateTime getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDateTime dataEmissao) { this.dataEmissao = dataEmissao; }
    public LocalDateTime getDataExpiracao() { return dataExpiracao; }
    public void setDataExpiracao(LocalDateTime dataExpiracao) { this.dataExpiracao = dataExpiracao; }
    public double getValorBase() { return valorBase; }
    public void setValorBase(double valorBase) { this.valorBase = valorBase; }
    public EstadoIngresso getEstado() { return estado; }
    public void setEstado(EstadoIngresso estado) { this.estado = estado; }
    public StatusReserva getStatusReserva() { return statusReserva; }
    public void setStatusReserva(StatusReserva statusReserva) { this.statusReserva = statusReserva; }
}
