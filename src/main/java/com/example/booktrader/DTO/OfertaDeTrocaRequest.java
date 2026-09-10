package com.example.booktrader.DTO;

public class OfertaDeTrocaRequest {

    private String livroDesejado;
    private String livroOfertado;

    public String getLivroOfertado() {
        return livroOfertado;
    }

    public void setLivroOfertado(String livroOfertado) {
        this.livroOfertado = livroOfertado;
    }

    public String getLivroDesejado() {
        return livroDesejado;
    }

    public void setLivroDesejado(String livroDesejado) {
        this.livroDesejado = livroDesejado;
    }
}
