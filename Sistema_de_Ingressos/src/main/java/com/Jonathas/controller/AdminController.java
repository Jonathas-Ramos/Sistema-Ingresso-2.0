package com.Jonathas.controller;

import com.Jonathas.model.EstadoIngresso;
import com.Jonathas.model.Evento;
import com.Jonathas.model.Ingresso;
import com.Jonathas.model.PerfilUsuario;
import com.Jonathas.model.Usuario;
import com.Jonathas.service.AuthService;
import com.Jonathas.service.EventoService;
import com.Jonathas.service.IngressoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private AuthService authService;

    @Autowired
    private EventoService eventoService;

    @Autowired
    private IngressoService ingressoService;

    @GetMapping("/admin")
    public String painel(Model model, HttpServletRequest request) {
        garantirAdmin(request);
        List<Ingresso> ingressos = ingressoService.listarTodos();
        long reservados = ingressos.stream().filter(i -> i.getEstado() == EstadoIngresso.RESERVADO).count();
        long utilizados = ingressos.stream().filter(i -> i.getEstado() == EstadoIngresso.UTILIZADO).count();
        long cancelados = ingressos.stream().filter(i -> i.getEstado() == EstadoIngresso.CANCELADO).count();
        int disponiveis = eventoService.listarTodos().stream()
                .mapToInt(Evento::getQuantidadeDisponivel)
                .sum();

        model.addAttribute("eventos", eventoService.listarTodos());
        model.addAttribute("reservados", reservados);
        model.addAttribute("utilizados", utilizados);
        model.addAttribute("cancelados", cancelados);
        model.addAttribute("disponiveis", disponiveis);
        return "admin";
    }

    @PostMapping("/admin/eventos")
    public String criarEvento(
            @RequestParam String nome,
            @RequestParam String descricao,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataHora,
            @RequestParam String local,
            @RequestParam int quantidadeDisponivel,
            @RequestParam double valorIngresso,
            HttpServletRequest request) {
        garantirAdmin(request);
        eventoService.salvar(new Evento(nome, descricao, dataHora, local, quantidadeDisponivel, valorIngresso));
        return "redirect:/admin";
    }

    @GetMapping("/admin/eventos/{eventoId}/ingressos")
    public String ingressosPorEvento(@PathVariable String eventoId, Model model, HttpServletRequest request) {
        garantirAdmin(request);
        model.addAttribute("evento", eventoService.buscarPorId(eventoId));
        model.addAttribute("ingressos", ingressoService.listarPorEvento(eventoId));
        return "adminIngressosEvento";
    }

    @PostMapping("/admin/confirmar/{ingressoId}")
    public String confirmarIngresso(@PathVariable String ingressoId, @RequestParam String eventoId, HttpServletRequest request) {
        garantirAdmin(request);
        ingressoService.confirmar(ingressoId);
        return "redirect:/admin/eventos/" + eventoId + "/ingressos";
    }

    @GetMapping("/admin/validar")
    public String telaValidacao(HttpServletRequest request) {
        garantirAdmin(request);
        return "validarQr";
    }

    @PostMapping("/admin/validar")
    public String validar(@RequestParam String codigoHash, Model model, HttpServletRequest request) {
        garantirAdmin(request);
        try {
            model.addAttribute("ingresso", ingressoService.validarQrCode(codigoHash.trim()));
            model.addAttribute("mensagem", "Ingresso validado e marcado como utilizado.");
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
        }
        return "validarQr";
    }

    private void garantirAdmin(HttpServletRequest request) {
        Usuario usuario = authService.usuarioLogado(request);
        if (usuario.getPerfil() != PerfilUsuario.ADMIN) {
            throw new RuntimeException("Acesso permitido apenas para administrador.");
        }
    }
}
