package com.Jonathas.repository;

import com.Jonathas.model.Evento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends MongoRepository<Evento, String> {
}
