package com.example.booktrader.DTO;

import com.example.booktrader.entities.LivroOfertado;

public class LivroOfertadoResponse {

    private Long id;
    private String titulo;
    private String autor;
    private String status;

    public LivroOfertadoResponse(LivroOfertado livroOfertado) {
        id = livroOfertado.getLivro().getId();
        titulo = livroOfertado.getLivro().getTitulo();
        autor = livroOfertado.getLivro().getAutor();
        status = livroOfertado.getStatus();
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getStatus() {
        return status;
    }
}
