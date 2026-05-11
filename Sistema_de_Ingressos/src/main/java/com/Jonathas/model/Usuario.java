package com.Jonathas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String id;
    private String nome;
    private String email;
    private String senha;
    private EstadoUsuario estado;
    private PerfilUsuario perfil;
    private int tentativasFalhas;

    public Usuario() {
    }

    public Usuario(String email, String senha) {
        this("Cliente", email, senha, PerfilUsuario.CLIENTE);
    }

    public Usuario(String nome, String email, String senha, PerfilUsuario perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.estado = EstadoUsuario.DESLOGADO;
        this.perfil = perfil;
        this.tentativasFalhas = 0;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public EstadoUsuario getEstado() { return estado; }
    public void setEstado(EstadoUsuario estado) { this.estado = estado; }
    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }
    public int getTentativasFalhas() { return tentativasFalhas; }
    public void setTentativasFalhas(int tentativasFalhas) { this.tentativasFalhas = tentativasFalhas; }
}
