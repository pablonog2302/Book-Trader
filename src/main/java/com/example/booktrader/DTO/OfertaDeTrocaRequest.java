package com.example.booktrader.DTO;

import com.example.booktrader.entities.OfertaDeTroca;

public class OfertaDeTrocaResponse {

    String mensagem;
    Long id;

    public OfertaDeTrocaResponse(){}

    public OfertaDeTrocaResponse(Long id, String mensagem) {
        this.mensagem = mensagem;
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
