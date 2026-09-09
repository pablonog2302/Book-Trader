package com.example.booktrader.entities;

public class OfertaDeTroca {

    public OfertaDeTroca(){
        this.id = 1L;
    }

    public OfertaDeTroca(String solicitante, String receptor, String livroDesejado, String livroOfertado){
        this.id = 1000L;
        this.solicitante = solicitante;
        this.receptor = receptor;
        this.livroDesejado = livroDesejado;
        this.livroOfertado = livroOfertado;
    }

    private Long id;
    private String solicitante;
    private String receptor;
    private String livroDesejado;
    private String livroOfertado;

    public Long getId(){return this.id;}


    //SOLICITANTE
    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    //RECEPTOR
    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    //LIVRODESEJADO
    public String getLivroDesejado() {
        return livroDesejado;
    }

    public void setLivroDesejado(String livroDesejado) {
        this.livroDesejado = livroDesejado;
    }

    //LIVROOFERTADO
    public String getLivroOfertado() {
        return livroOfertado;
    }

    public void setLivroOfertado(String livroOfertado) {
        this.livroOfertado = livroOfertado;
    }
}
