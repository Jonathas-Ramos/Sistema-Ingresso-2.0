package com.Jonathas.service;

import com.Jonathas.model.Cliente;
import com.Jonathas.model.EstadoUsuario;
import com.Jonathas.model.PerfilUsuario;
import com.Jonathas.model.Usuario;
import com.Jonathas.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository repository;

    // Chave fixa para manter os tokens validos enquanto a aplicacao estiver em uso academico.
    private final Key key = Keys.hmacShaKeyFor("SistemaDeIngressosChaveJwtAcademica2026!".getBytes(StandardCharsets.UTF_8));

    public String autenticar(String email, String senha) {
        Usuario usuario = repository.findByEmail(email);

        // Usuário não existe
        if (usuario == null) {
            throw new RuntimeException("Erro: Usuário não encontrado.");
        }

        // Verifica se está bloqueado (Diagrama 2.3)
        if (usuario.getEstado() == EstadoUsuario.BLOQUEADO) {
            throw new RuntimeException("Erro: Conta bloqueada devido a múltiplas tentativas falhas.");
        }

        // Validação de Senha (Diagrama 2.1 e 2.2)
        if (!usuario.getSenha().equals(senha)) {
            usuario.setTentativasFalhas(usuario.getTentativasFalhas() + 1);

            if (usuario.getTentativasFalhas() >= 3) {
                usuario.setEstado(EstadoUsuario.BLOQUEADO);
            }
            repository.save(usuario);
            throw new RuntimeException("Erro: Senha inválida.");
        }

        // Senha válida! Reseta falhas, muda estado e gera o Token (Diagrama 2.2, passo 8)
        usuario.setTentativasFalhas(0);
        usuario.setEstado(EstadoUsuario.LOGADO);
        if (usuario.getPerfil() == null) {
            usuario.setPerfil(PerfilUsuario.CLIENTE);
        }
        repository.save(usuario);

        return gerarTokenJWT(usuario.getEmail());
    }

    private String gerarTokenJWT(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("perfil", perfilDoUsuario(email).name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // Expira em 1 hora
                .signWith(key)
                .compact();
    }

    private PerfilUsuario perfilDoUsuario(String email) {
        Usuario usuario = repository.findByEmail(email);
        if (usuario == null || usuario.getPerfil() == null) {
            return PerfilUsuario.CLIENTE;
        }
        return usuario.getPerfil();
    }

    // Novo método para validar se o Token é autêntico e não está expirado
    public boolean isTokenValido(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Token falso, expirado ou mal formatado
        }
    }

    public String extrairEmail(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public Usuario buscarUsuarioPorToken(String token) {
        return repository.findByEmail(extrairEmail(token));
    }

    public Usuario usuarioLogado(HttpServletRequest request) {
        String token = extrairTokenDoCookie(request);
        if (token == null || !isTokenValido(token)) {
            throw new RuntimeException("Usuario nao autenticado.");
        }
        return buscarUsuarioPorToken(token);
    }

    public String extrairTokenDoCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if ("jwt_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public boolean isAdmin(String token) {
        Usuario usuario = buscarUsuarioPorToken(token);
        return usuario != null && usuario.getPerfil() == PerfilUsuario.ADMIN;
    }

    // Este método FICA AQUI NO SERVICE!
    public void cadastrarUsuario(String email, String senha) {
        // Verifica se já existe alguém com esse e-mail
        if (repository.findByEmail(email) != null) {
            throw new RuntimeException("Erro: Este e-mail já está cadastrado no sistema.");
        }

        // Cria e salva o novo usuário
        Usuario novoUsuario = new Cliente("Cliente", email, senha);
        repository.save(novoUsuario);
    }

    public void cadastrarAdministrador(String email, String senha) {
        if (repository.findByEmail(email) != null) {
            throw new RuntimeException("Erro: Este e-mail já está cadastrado no sistema.");
        }

        Usuario novoAdmin = new Usuario("Administrador", email, senha, PerfilUsuario.ADMIN);
        repository.save(novoAdmin);
    }
}
