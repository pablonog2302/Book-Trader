package com.example.booktrader.DTO;

public class EmpresaResponse {

    private Long id;
    private String mensagem;


    public EmpresaResponse(Long id, String mensagem) {

        this.id = id;
        this.mensagem = mensagem;
    }

    public EmpresaResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
