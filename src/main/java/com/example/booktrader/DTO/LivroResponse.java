package com.example.booktrader.DTO;

public class LivroResponse {

    private String mensagem;
    private Long id;

    public LivroResponse(Long id, String mensagem){
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
