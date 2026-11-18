package com.example.booktrader.DTO;

import com.example.booktrader.entities.Usuario;

public class UsuarioIdentidadeResponse {

    private final Long id;
    private final String nome;

    public UsuarioIdentidadeResponse(Usuario usuario) {
        id = usuario.getId();
        nome = usuario.getNome();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
