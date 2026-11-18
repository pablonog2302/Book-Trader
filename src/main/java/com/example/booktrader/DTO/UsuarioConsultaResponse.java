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
    }

    private String nome;

    private String cpf;

    private String dataNascimento;

    private Long id;


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

}
