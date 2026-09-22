package com.example.booktrader.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String leitorSolicitante;
    private String ofertaDeTroca;
    private String status;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;


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

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
