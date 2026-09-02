package com.example.booktrader.DTO;

public class UsuarioResponse {
    private String mensagem;
    private Long id;

    public UsuarioResponse(){};

    public UsuarioResponse(Long id, String mensagem) {
        this.mensagem = mensagem;
        this.id = id;
    }

    public String getmensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        mensagem = mensagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
