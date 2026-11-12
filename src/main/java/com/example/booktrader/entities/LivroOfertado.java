package com.example.booktrader.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

// guarda separadamente cada livro e o resultado individual dentro de uma oferta.
@Entity
public class LivroOfertado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // cada item liga um livro a uma oferta e guarda o resultado da decisao
    @ManyToOne
    @JoinColumn(name = "oferta_de_troca_id")
    private OfertaDeTroca ofertaDeTroca;

    @ManyToOne
    @JoinColumn(name = "livro_id")
    private Livro livro;

    private String status;

    public Long getId() {
        return id;
    }

    public OfertaDeTroca getOfertaDeTroca() {
        return ofertaDeTroca;
    }

    public void setOfertaDeTroca(OfertaDeTroca ofertaDeTroca) {
        this.ofertaDeTroca = ofertaDeTroca;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
