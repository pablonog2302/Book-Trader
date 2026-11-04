package com.example.booktrader.DTO;

public class LoginResponse {

    public LoginResponse(){}

    private String mensagem;

    //aula q n vim
    private String token;

    private Long usuarioId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    //

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}