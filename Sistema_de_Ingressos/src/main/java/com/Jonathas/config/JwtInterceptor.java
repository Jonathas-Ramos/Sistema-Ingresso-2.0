package com.Jonathas.config;

import com.Jonathas.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = null;

        // Procura o cookie chamado "jwt_token"
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("jwt_token")) {
                    token = cookie.getValue();
                }
            }
        }

        // Se achou o token e ele for válido, deixa a pessoa entrar (retorna true)
        if (token != null && authService.isTokenValido(token)) {
            return true;
        }

        // Se não tem token ou é inválido, chuta para a tela de login
        response.sendRedirect("/login");
        return false;
    }
}