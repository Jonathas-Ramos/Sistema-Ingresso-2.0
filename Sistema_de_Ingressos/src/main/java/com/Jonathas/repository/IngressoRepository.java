package com.Jonathas.repository;

import com.Jonathas.model.Ingresso;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngressoRepository extends MongoRepository<Ingresso, String> {
    Ingresso findByCodigoHash(String codigoHash);
    List<Ingresso> findByClienteId(String clienteId);
    List<Ingresso> findByEventoId(String eventoId);
}
