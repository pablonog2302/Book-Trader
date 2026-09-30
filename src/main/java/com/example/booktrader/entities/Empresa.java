package com.example.booktrader.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Empresa {

    public Empresa(){
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //tornar tal informacao unica
    @Column(unique = true)
    private String cnpj;

    private String razaoSocial;
    private String nomeFantasia;
    private String inscricaoEstadual;
//29092026
    @OneToMany(mappedBy = "empresa")//segure o mouse e deixe em cima de empresa
    private List<Usuario> usuarios;

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
//
    public Long getId() {
        return id;
    }



    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }
}
