package com.example.booktrader.DTO;

import com.example.booktrader.entities.Empresa;

public class EmpresaConsultaResponse {

    public EmpresaConsultaResponse(){}

    public EmpresaConsultaResponse(Empresa empresa){
        this.cnpj = empresa.getCnpj();
        this.razaoSocial = empresa.getRazaoSocial();
        this.nomeFantasia = empresa.getNomeFantasia();
        this.inscricaoEstadual = empresa.getInscricaoEstadual();
        this.id = empresa.getId();

        if (empresa.getUsuarios()!= null){
            this.quantidadeUsuario = empresa.getUsuarios().size();
        }else {
            this.quantidadeUsuario = 0;
        }
    }

    private Long id;
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
    private String inscricaoEstadual;
    private int quantidadeUsuario;

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
