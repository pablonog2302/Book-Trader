package com.example.booktrader.entities;

public class OfertaDeTroca {


    public OfertaDeTroca(){
        this.id = 1L;
    }

    public OfertaDeTroca(String solicitante, String receptor, String livroDesejado, String livroOfertado){
        this.id = 1000L;
        this.leitorDono = leitorDono;
        this.livroOfertado = livroOfertado;
    }

    private Long id;
    private String leitorDono;
    private String livroOfertado;
    private String status;


    public Long getId(){return this.id;}


    //leitorDono


    public String getLeitorDono() {
        return leitorDono;
    }

    public void setLeitorDono(String leitorDono) {
        this.leitorDono = leitorDono;
    }



    //status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //leitorDono
    public String getSolicitante() {
        return leitorDono;
    }

    public void setSolicitante(String solicitante) {
        this.leitorDono = solicitante;
    }


    //LIVROOFERTADO
    public String getLivroOfertado() {
        return livroOfertado;
    }

    public void setLivroOfertado(String livroOfertado) {
        this.livroOfertado = livroOfertado;
    }
}
