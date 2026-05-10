package com.Jonathas.controller;

import com.Jonathas.model.*;
import com.Jonathas.service.AuthService;
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
public class IngressoController {

    @Autowired
    private IngressoService service;

    @Autowired
    private AuthService authService;

    @GetMapping("/comprar")
    public String exibirFormulario() {
        return "index";
    }

    @PostMapping("/comprarIngresso")
    public String comprarIngresso(
            @RequestParam String evento,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataEvento,
            @RequestParam double valorBase,
            @RequestParam String tipo,
            @RequestParam(defaultValue = "0.0") double taxaVip,
            HttpServletRequest request,
            Model model) {

        Ingresso ingresso = null;

        switch (tipo) {
            case "NORMAL":
                ingresso = new IngressoNormal(evento, dataEvento, valorBase);
                break;
            case "VIP":
                ingresso = new IngressoVIP(evento, dataEvento, valorBase, taxaVip);
                break;
            case "MEIA":
                ingresso = new IngressoMeia(evento, dataEvento, valorBase);
                break;
        }

        if (ingresso != null) {
            ingresso.setEstado(EstadoIngresso.PAGO);
            Usuario usuario = authService.usuarioLogado(request);
            service.salvarIngressoManual(ingresso, usuario);

            model.addAttribute("detalhes", ingresso.imprimirIngresso());
            model.addAttribute("valorFinal", ingresso.calcularValor());
            model.addAttribute("estado", ingresso.getEstado());
            model.addAttribute("codigoHash", ingresso.getCodigoHash());
            model.addAttribute("qrCodeBase64", ingresso.getQrCodeBase64());
        }
        return "resultado";
    }

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
