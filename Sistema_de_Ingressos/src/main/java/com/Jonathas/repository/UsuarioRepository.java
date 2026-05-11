package com.Jonathas.repository;

import com.Jonathas.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Usuario findByEmail(String email); // O Spring cria a query no MongoDB automaticamente!
}