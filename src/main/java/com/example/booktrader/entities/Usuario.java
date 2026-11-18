package com.example.booktrader.entities;


import com.example.booktrader.utils.CpfUtils;
import com.example.booktrader.utils.OnlyNumber;
import com.example.booktrader.utils.SenhaUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;


// o indice unico do banco tambem bloqueia cpfs repetidos em cadastros simultaneos.
@Entity
@Table(indexes = @Index(name = "uk_usuario_cpf", columnList = "cpf", unique = true))
public class Usuario {

    public Usuario(){}

    public Usuario(String nome, String cpf, String dataNascimento){
        this.nome = nome;
        this.setCpf(cpf);
        this.dataNascimento = dataNascimento;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String cpf;

    private String dataNascimento;

    private LocalDateTime dataCadastro;

    private LocalDateTime dataAtualizacao;

    private String status;

    private String senha;

    // impede a repeticao dos livros na resposta json
    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<Livro> livros;

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = livros;
    }






    @JsonIgnore
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        String mensagemDeErro = SenhaUtils.mensagemDeErro(senha);
        if (mensagemDeErro != null) {
            throw new IllegalArgumentException(mensagemDeErro);
        }
        this.senha = senha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

        if(!CpfUtils.isValidCpf(cpf)){
            throw new IllegalArgumentException("CPF inválido.");
        }
        this.cpf = OnlyNumber.apenasNumeros(cpf);
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
