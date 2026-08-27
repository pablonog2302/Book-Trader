package entities;

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


    public Long getId() {return this.id;}

    public String getTitulo() {return this.titulo;}

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {return this.descricao;}

    public String getDataPublicada() {return this.dataPublicada;}

    public String getAutor() {return this.autor;}

    public String getIsbn() {return this.isbn;}

    public String getFotoCapa() {return this.fotoCapa;}
}
