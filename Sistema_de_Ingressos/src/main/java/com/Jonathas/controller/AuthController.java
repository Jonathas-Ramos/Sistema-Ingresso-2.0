package com.Jonathas.controller;

import com.Jonathas.model.Usuario;
import com.Jonathas.model.PerfilUsuario;
import com.Jonathas.repository.UsuarioRepository;
import com.Jonathas.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String exibirTelaLogin() {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(new Usuario("Administrador", "admin@umc.br", "123456", PerfilUsuario.ADMIN));
        }
        return "login";
    }

    @PostMapping("/realizarLogin")
    public String realizarLogin(@RequestParam String email, @RequestParam String senha, HttpServletResponse response, Model model) {
        try {
            String tokenJwt = authService.autenticar(email, senha);

            // Cria um Cookie seguro para guardar o token no navegador do usuário
            Cookie cookie = new Cookie("jwt_token", tokenJwt);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(3600); // O cookie dura 1 hora (tempo do token)
            cookie.setPath("/"); // Funciona no site inteiro
            response.addCookie(cookie);

            Usuario usuario = usuarioRepository.findByEmail(email);
            if (usuario.getPerfil() == PerfilUsuario.ADMIN) {
                return "redirect:/admin";
            }
            return "redirect:/cliente/catalogo";

        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String fazerLogout(HttpServletResponse response) {
        // Para deslogar, basta "matar" o cookie
        Cookie cookie = new Cookie("jwt_token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/login";
    }

    // Exibe a tela de cadastro
    @GetMapping("/cadastro")
    public String exibirTelaCadastro() {
        return "cadastro";
    }

    // Processa o formulário de cadastro
    @PostMapping("/realizarCadastro")
    public String realizarCadastro(@RequestParam String email, @RequestParam String senha, Model model) {
        try {
            // O Controller apenas CHAMA o AuthService aqui:
            authService.cadastrarUsuario(email, senha);

            // Se deu certo, manda uma mensagem de sucesso para a tela de login
            model.addAttribute("mensagemSucesso", "Conta criada com sucesso! Faça seu login.");
            return "login";
        } catch (RuntimeException e) {
            // Se deu erro (ex: e-mail já existe), volta pra tela de cadastro mostrando o erro
            model.addAttribute("erro", e.getMessage());
            return "cadastro";
        }
    }
}
