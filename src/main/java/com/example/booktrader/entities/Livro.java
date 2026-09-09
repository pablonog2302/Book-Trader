package com.example.booktrader.entities;

public class Livro {

    public Livro(){
        this.id = 1L;
    }


    //CONSTRUTOR

    public Livro(String titulo, String descricao, String dataPublicada, String autor, String isbn, String fotoCapa){

        this.id = 1000L;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataPublicada = dataPublicada;
        this.autor = autor;
        this.isbn = isbn;
        this.fotoCapa = fotoCapa;
    }


    //DEFINICAO DE VALORES

    private Long id;
    private String titulo;
    private String descricao;
    private String dataPublicada;
    private String autor;
    private String isbn;
    private String fotoCapa;

    //GET SET

    //ID
    public Long getId() {return this.id;}

    //TITULO
    public String getTitulo() {return this.titulo;}

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    //DESCRICAO
    public String getDescricao() {return this.descricao;}

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    //DATA
    public String getDataPublicada() {return this.dataPublicada;}

    public void setDataPublicada(String dataPublicada) {
        this.dataPublicada = dataPublicada;
    }

    //AUTOR
    public String getAutor() {return this.autor;}

    public void setAutor(String autor) {
        this.autor = autor;
    }

    //ISBN
    public String getIsbn() {return this.isbn;}

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    //FOTO
    public String getFotoCapa() {return this.fotoCapa;}

    public void setFotoCapa(String fotoCapa) {
        this.fotoCapa = fotoCapa;
    }
}
