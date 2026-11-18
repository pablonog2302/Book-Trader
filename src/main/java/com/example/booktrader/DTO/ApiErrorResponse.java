package com.example.booktrader.DTO;

public class ApiErrorResponse {

    private final String mensagem;

    public ApiErrorResponse(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }
}
