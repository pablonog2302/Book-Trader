package com.example.booktrader.entities;

public class Solicitacao {

    private Long id;
    private String leitorSolicitante;
    private String ofertaDeTroca;
    private String status;

    public Long getId() {
        return id;
    }


    public String getLeitorSolicitante() {
        return leitorSolicitante;
    }

    public void setLeitorSolicitante(String leitorSolicitante) {
        this.leitorSolicitante = leitorSolicitante;
    }

    public String getOfertaDeTroca() {
        return ofertaDeTroca;
    }

    public void setOfertaDeTroca(String ofertaDeTroca) {
        this.ofertaDeTroca = ofertaDeTroca;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
