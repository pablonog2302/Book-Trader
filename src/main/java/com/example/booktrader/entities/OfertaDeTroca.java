
package com.example.booktrader.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class OfertaDeTroca {


    public OfertaDeTroca(){
        this.id = 1L;
    }

    public OfertaDeTroca(String solicitante, String receptor, String livroDesejado, String livroOfertado){
        this.id = 1000L;
        this.leitorDono = leitorDono;
        this.livroOfertado = Long.parseLong(livroOfertado);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String leitorDono;
    private Long livroOfertado;
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
    public Long getLivroOfertado() {
        return livroOfertado;
    }

    public void setLivroOfertado(Long livroOfertado) {
        this.livroOfertado = livroOfertado;
    }
}
