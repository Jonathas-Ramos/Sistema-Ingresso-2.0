package com.Jonathas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**") // Protege TODAS as páginas do sistema
                // AQUI ESTÁ A MÁGICA: Adicionamos o /cadastro e o /realizarCadastro na lista de exceções!
                .excludePathPatterns("/login", "/realizarLogin", "/cadastro", "/realizarCadastro", "/cadastroAdmin", "/realizarCadastroAdmin");
    }
}
