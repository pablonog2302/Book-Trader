package com.example.booktrader.DTO;

import com.example.booktrader.entities.Curso;

public class CursoRequest {
    public CursoRequest() {
    }

    private String titulo;

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
