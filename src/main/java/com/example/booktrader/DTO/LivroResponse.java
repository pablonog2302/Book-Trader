package com.example.booktrader.DTO;

public class LivroResponse {

    private String mensagem;
    private Long id;

    //aula q n vim
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    //

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
