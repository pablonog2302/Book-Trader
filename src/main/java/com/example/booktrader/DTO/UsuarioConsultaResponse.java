package com.example.booktrader.DTO;

import com.example.booktrader.entities.Usuario;

public class UsuarioConsultaResponse {

    public UsuarioConsultaResponse(){}


    //construtor para mapear a listaBanco para resposta de usuario
    public UsuarioConsultaResponse(Usuario usuario){
        this.nome = usuario.getNome();
        this.setCpf(usuario.getCpf());
        this.dataNascimento = usuario.getDataNascimento();
        this.id = usuario.getId();
        if (usuario.getEmpresa()!=null) {
            this.empresa_id = usuario.getEmpresa().getId();
            this.razaoSocialEmpresa = usuario.getEmpresa().getRazaoSocial();
        }

    }

    private String nome;

    private String razaoSocialEmpresa;

    private String cpf;

    private String dataNascimento;

    private Long empresa_id;

    private Long id;


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRazaoSocialEmpresa() {
        return razaoSocialEmpresa;
    }

    public void setRazaoSocialEmpresa(String razaoSocialEmpresa) {
        this.razaoSocialEmpresa = razaoSocialEmpresa;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmpresa_id() {
        return empresa_id;
    }

    public void setEmpresa_id(Long empresa_id) {
        this.empresa_id = empresa_id;
    }
}
