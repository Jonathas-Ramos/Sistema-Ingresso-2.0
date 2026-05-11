package com.Jonathas.model;

public class Cliente extends Usuario {

    public Cliente() {
        setPerfil(PerfilUsuario.CLIENTE);
    }

    public Cliente(String nome, String email, String senha) {
        super(nome, email, senha, PerfilUsuario.CLIENTE);
    }
}
