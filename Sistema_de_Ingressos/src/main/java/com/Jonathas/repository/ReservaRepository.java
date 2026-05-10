package com.Jonathas.repository;

import com.Jonathas.model.Reserva;
import com.Jonathas.model.StatusReserva;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends MongoRepository<Reserva, String> {
    boolean existsByClienteIdAndEventoIdAndStatus(String clienteId, String eventoId, StatusReserva status);
    List<Reserva> findByClienteId(String clienteId);
    List<Reserva> findByEventoId(String eventoId);
    Reserva findByIngressoId(String ingressoId);
}
