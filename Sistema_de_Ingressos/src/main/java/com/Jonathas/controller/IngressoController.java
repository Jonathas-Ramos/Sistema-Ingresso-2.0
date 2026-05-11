package com.Jonathas.controller;

import com.Jonathas.service.IngressoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class IngressoController {

    @Autowired
    private IngressoService service;

    @GetMapping("/ingressos")
    public String listarIngressos(Model model) {
        model.addAttribute("ingressos", service.listarTodos());
        return "lista";
    }

    // NOVAS ROTAS DE CANCELAMENTO E DEVOLUÇÃO
    @GetMapping("/cancelar/{id}")
    public String cancelarIngresso(@PathVariable String id) {
        service.cancelar(id);
        return "redirect:/ingressos"; // Volta pra lista atualizada
    }

    @GetMapping("/devolver/{id}")
    public String devolverIngresso(@PathVariable String id) {
        service.devolver(id);
        return "redirect:/ingressos"; // Volta pra lista atualizada
    }

    @GetMapping("/cliente/cancelar/{id}")
    public String cancelarMeuIngresso(@PathVariable String id) {
        service.cancelar(id);
        return "redirect:/cliente/ingressos";
    }

    @GetMapping("/cliente/devolver/{id}")
    public String devolverMeuIngresso(@PathVariable String id) {
        service.devolver(id);
        return "redirect:/cliente/ingressos";
    }
}
