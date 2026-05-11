package com.Jonathas.service;

import com.Jonathas.model.*;
import com.Jonathas.repository.EventoRepository;
import com.Jonathas.repository.IngressoRepository;
import com.Jonathas.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class IngressoService {

    @Autowired
    private IngressoRepository repository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private QrCodeService qrCodeService;

    public void salvarIngresso(Ingresso ingresso) {
        prepararIngressoManual(ingresso, null);
        repository.save(ingresso);
    }

    public void salvarIngressoManual(Ingresso ingresso, Usuario cliente) {
        prepararIngressoManual(ingresso, cliente);
        repository.save(ingresso);
    }

    private void prepararIngressoManual(Ingresso ingresso, Usuario cliente) {
        if (cliente != null) {
            ingresso.setClienteId(cliente.getId());
        }
        if (ingresso.getDataEmissao() == null) {
            ingresso.setDataEmissao(LocalDateTime.now());
        }
        if (ingresso.getDataExpiracao() == null) {
            ingresso.setDataExpiracao(ingresso.getDataEvento());
        }
        if (ingresso.getCodigoHash() == null || ingresso.getCodigoHash().isBlank()) {
            ingresso.setCodigoHash(gerarHashManual(ingresso, cliente));
        }
        if (ingresso.getQrCodeBase64() == null || ingresso.getQrCodeBase64().isBlank()) {
            ingresso.setQrCodeBase64(qrCodeService.gerarQrCodeBase64(ingresso.getCodigoHash()));
        }
    }

    public List<Ingresso> listarTodos() {
        return repository.findAll();
    }

    public List<Ingresso> listarPorCliente(String clienteId) {
        return repository.findByClienteId(clienteId);
    }

    public List<Ingresso> listarPorClienteComStatusReserva(String clienteId) {
        List<Ingresso> ingressos = repository.findByClienteId(clienteId);
        for (Ingresso ingresso : ingressos) {
            Reserva reserva = reservaRepository.findByIngressoId(ingresso.getId());
            if (reserva != null) {
                ingresso.setStatusReserva(reserva.getStatus());
            }
        }
        return ingressos;
    }

    public List<Ingresso> listarPorEvento(String eventoId) {
        return repository.findByEventoId(eventoId);
    }

    public Ingresso buscarPorId(String id) {
        return repository.findById(id).orElse(null);
    }

    public Ingresso reservarIngresso(Usuario cliente, String eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento nao encontrado."));

        if (!evento.possuiIngressosDisponiveis()) {
            throw new RuntimeException("Nao ha ingressos disponiveis para este evento.");
        }

        boolean jaReservou = reservaRepository.existsByClienteIdAndEventoIdAndStatusIn(
                cliente.getId(), eventoId, Arrays.asList(StatusReserva.RESERVADA, StatusReserva.ATIVA));
        if (jaReservou) {
            throw new RuntimeException("Voce ja possui uma reserva ativa para este evento.");
        }

        evento.reservarUmaUnidade();
        eventoRepository.save(evento);

        IngressoNormal ingresso = new IngressoNormal(evento.getNome(), evento.getDataHora(), evento.getValorIngresso());
        ingresso.setClienteId(cliente.getId());
        ingresso.setEventoId(evento.getId());
        ingresso.setEstado(EstadoIngresso.RESERVADO);
        ingresso.setDataEmissao(LocalDateTime.now());
        ingresso.setDataExpiracao(evento.getDataHora());
        ingresso.setCodigoHash(gerarHashIngresso(cliente, evento));
        ingresso.setQrCodeBase64(qrCodeService.gerarQrCodeBase64(ingresso.getCodigoHash()));

        Ingresso ingressoSalvo = repository.save(ingresso);
        reservaRepository.save(new Reserva(cliente.getId(), evento.getId(), ingressoSalvo.getId()));
        return ingressoSalvo;
    }

    public String gerarHashIngresso(Usuario cliente, Evento evento) {
        String dados = cliente.getId() + "|" + cliente.getEmail() + "|" + evento.getId() + "|" +
                evento.getDataHora() + "|" + UUID.randomUUID();
        return gerarSha256(dados);
    }

    private String gerarHashManual(Ingresso ingresso, Usuario cliente) {
        String dados = ingresso.getEvento() + "|" + ingresso.getDataEvento() + "|" + ingresso.calcularValor() + "|" +
                (cliente != null ? cliente.getEmail() : "cadastro-manual") + "|" + UUID.randomUUID();
        return gerarSha256(dados);
    }

    private String gerarSha256(String dados) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(dados.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash do ingresso.", e);
        }
    }

    public Ingresso validarQrCode(String codigoHash) {
        Ingresso ingresso = repository.findByCodigoHash(codigoHash);
        if (ingresso == null) {
            throw new RuntimeException("Ingresso invalido.");
        }
        if (ingresso.getEstado() == EstadoIngresso.UTILIZADO || ingresso.getEstado() == EstadoIngresso.USADO) {
            throw new RuntimeException("Este ingresso ja foi utilizado.");
        }
        if (ingresso.getEstado() == EstadoIngresso.CANCELADO) {
            throw new RuntimeException("Este ingresso foi cancelado.");
        }
        if (ingresso.getDataExpiracao() != null && LocalDateTime.now().isAfter(ingresso.getDataExpiracao())) {
            ingresso.setEstado(EstadoIngresso.EXPIRADO);
            repository.save(ingresso);
            throw new RuntimeException("Este ingresso esta expirado.");
        }

        ingresso.setEstado(EstadoIngresso.UTILIZADO);
        Reserva reserva = reservaRepository.findByIngressoId(ingresso.getId());
        if (reserva != null) {
            reserva.setStatus(StatusReserva.UTILIZADA);
            reservaRepository.save(reserva);
        }
        return repository.save(ingresso);
    }

    public Ingresso confirmar(String id) {
        Ingresso ingresso = buscarPorId(id);
        if (ingresso == null) {
            throw new RuntimeException("Ingresso nao encontrado.");
        }
        if (ingresso.getEstado() == EstadoIngresso.CANCELADO || ingresso.getEstado() == EstadoIngresso.DEVOLVIDO) {
            throw new RuntimeException("Nao e possivel confirmar um ingresso cancelado ou devolvido.");
        }
        if (ingresso.getEstado() == EstadoIngresso.UTILIZADO || ingresso.getEstado() == EstadoIngresso.USADO) {
            throw new RuntimeException("Nao e possivel confirmar um ingresso ja utilizado.");
        }
        ingresso.setEstado(EstadoIngresso.CONFIRMADO);
        return repository.save(ingresso);
    }

    // FLUXO B DO DIAGRAMA DE SEQUÊNCIA
    public Ingresso cancelar(String id) {
        Ingresso ingresso = buscarPorId(id);
        if (ingresso != null && ingresso.getEstado() != EstadoIngresso.CANCELADO) {
            ingresso.setEstado(EstadoIngresso.CANCELADO);
            cancelarReservaVinculada(ingresso);
            devolverQuantidadeAoEvento(ingresso);
            return repository.save(ingresso);
        }
        return null;
    }

    // FLUXO C DO DIAGRAMA DE SEQUÊNCIA
    public Ingresso devolver(String id) {
        Ingresso ingresso = buscarPorId(id);
        if (ingresso != null && ingresso.getEstado() != EstadoIngresso.DEVOLVIDO && ingresso.getEstado() != EstadoIngresso.CANCELADO) {
            PoliticaDevolucao politica = new PoliticaDevolucao();

            if (politica.podeDevolver(ingresso)) {
                ingresso.setEstado(EstadoIngresso.DEVOLVIDO);
                double valorReembolso = politica.calcularReembolso(ingresso);
                cancelarReservaVinculada(ingresso);
                devolverQuantidadeAoEvento(ingresso);

                // Simula o envio para o Gateway de Pagamento Externo
                System.out.println(">>> INTEGRAÇÃO GATEWAY: Reembolsando R$ " + valorReembolso + " para o ingresso " + id);

                return repository.save(ingresso);
            }
        }
        return null; // Não cumpriu a política (ex: muito perto do evento)
    }

    private void cancelarReservaVinculada(Ingresso ingresso) {
        Reserva reserva = reservaRepository.findByIngressoId(ingresso.getId());
        if (reserva != null && (reserva.getStatus() == StatusReserva.RESERVADA || reserva.getStatus() == StatusReserva.ATIVA)) {
            reserva.setStatus(StatusReserva.CANCELADA);
            reservaRepository.save(reserva);
        }
    }

    private void devolverQuantidadeAoEvento(Ingresso ingresso) {
        if (ingresso.getEventoId() == null) {
            return;
        }
        eventoRepository.findById(ingresso.getEventoId()).ifPresent(evento -> {
            evento.setQuantidadeDisponivel(evento.getQuantidadeDisponivel() + 1);
            eventoRepository.save(evento);
        });
    }
}
