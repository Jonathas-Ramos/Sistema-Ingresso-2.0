package com.Jonathas.service;

import com.Jonathas.model.Evento;
import com.Jonathas.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    @Autowired
    private EventoRepository repository;

    public List<Evento> listarTodos() {
        return repository.findAll();
    }

    public Evento buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento nao encontrado."));
    }

    public Evento salvar(Evento evento) {
        if (evento.getValorIngresso() <= 0) {
            throw new RuntimeException("O valor do ingresso deve ser maior que zero.");
        }
        if (evento.getQuantidadeDisponivel() < 0) {
            throw new RuntimeException("A quantidade disponivel nao pode ser negativa.");
        }
        return repository.save(evento);
    }
}
