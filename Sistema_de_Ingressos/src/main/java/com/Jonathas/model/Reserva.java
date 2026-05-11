package com.Jonathas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reservas")
public class Reserva {
    @Id
    private String id;
    private String clienteId;
    private String eventoId;
    private String ingressoId;
    private LocalDateTime dataReserva;
    private StatusReserva status;

    public Reserva() {
    }

    public Reserva(String clienteId, String eventoId, String ingressoId) {
        this.clienteId = clienteId;
        this.eventoId = eventoId;
        this.ingressoId = ingressoId;
        this.dataReserva = LocalDateTime.now();
        this.status = StatusReserva.RESERVADA;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }
    public String getIngressoId() { return ingressoId; }
    public void setIngressoId(String ingressoId) { this.ingressoId = ingressoId; }
    public LocalDateTime getDataReserva() { return dataReserva; }
    public void setDataReserva(LocalDateTime dataReserva) { this.dataReserva = dataReserva; }
    public StatusReserva getStatus() { return status; }
    public void setStatus(StatusReserva status) { this.status = status; }
}
