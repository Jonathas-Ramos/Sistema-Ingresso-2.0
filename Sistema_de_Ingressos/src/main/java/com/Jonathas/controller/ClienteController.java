package com.Jonathas.controller;

import com.Jonathas.model.Usuario;
import com.Jonathas.service.AuthService;
import com.Jonathas.service.EventoService;
import com.Jonathas.service.IngressoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ClienteController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private IngressoService ingressoService;

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String inicio() {
        return "redirect:/cliente/catalogo";
    }

    @GetMapping("/cliente/catalogo")
    public String catalogo(Model model, HttpServletRequest request) {
        Usuario cliente = authService.usuarioLogado(request);
        model.addAttribute("cliente", cliente);
        model.addAttribute("eventos", eventoService.listarTodos());
        return "catalogo";
    }

    @PostMapping("/cliente/reservar/{eventoId}")
    public String reservar(@PathVariable String eventoId, Model model, HttpServletRequest request) {
        Usuario cliente = authService.usuarioLogado(request);
        try {
            ingressoService.reservarIngresso(cliente, eventoId);
            return "redirect:/cliente/ingressos";
        } catch (RuntimeException e) {
            model.addAttribute("cliente", cliente);
            model.addAttribute("eventos", eventoService.listarTodos());
            model.addAttribute("erro", e.getMessage());
            return "catalogo";
        }
    }

    @GetMapping("/cliente/ingressos")
    public String meusIngressos(Model model, HttpServletRequest request) {
        Usuario cliente = authService.usuarioLogado(request);
        model.addAttribute("cliente", cliente);
        model.addAttribute("ingressos", ingressoService.listarPorCliente(cliente.getId()));
        return "meusIngressos";
    }
}
