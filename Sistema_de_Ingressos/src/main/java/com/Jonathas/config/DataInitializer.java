package com.Jonathas.config;

import com.Jonathas.model.Evento;
import com.Jonathas.model.PerfilUsuario;
import com.Jonathas.model.Usuario;
import com.Jonathas.repository.EventoRepository;
import com.Jonathas.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    public DataInitializer(EventoRepository eventoRepository, UsuarioRepository usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByEmail("admin@umc.br") == null) {
            usuarioRepository.save(new Usuario("Administrador", "admin@umc.br", "123456", PerfilUsuario.ADMIN));
        }

        if (eventoRepository.count() == 0) {
            eventoRepository.save(new Evento(
                    "Festival de Tecnologia",
                    "Palestras e experiencias sobre desenvolvimento de software.",
                    LocalDateTime.now().plusDays(20).withHour(19).withMinute(30),
                    "Auditorio Principal",
                    80,
                    50.00));
            eventoRepository.save(new Evento(
                    "Show Acustico",
                    "Apresentacao musical com lugares limitados.",
                    LocalDateTime.now().plusDays(35).withHour(21).withMinute(0),
                    "Teatro Municipal",
                    120,
                    75.00));
            eventoRepository.save(new Evento(
                    "Workshop de Java",
                    "Oficina pratica de orientacao a objetos com Spring Boot.",
                    LocalDateTime.now().plusDays(12).withHour(14).withMinute(0),
                    "Laboratorio 3",
                    30,
                    35.00));
        }
    }
}
