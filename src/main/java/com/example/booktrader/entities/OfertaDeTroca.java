
package com.example.booktrader.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class OfertaDeTroca {

    public OfertaDeTroca(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String leitorDono;
    // a coluna antiga fica como texto para preservar os valores ja gravados no postgres
    @Column(name = "livro_ofertado")
    private String livroOfertado;
    private String status;

    // cada oferta guarda quem enviou; um usuario pode enviar varias ofertas
    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private Usuario usuarioSolicitante;

    // guarda o destinatario original para manter o historico apos a troca do livro desejado
    @ManyToOne
    @JoinColumn(name = "destinatario_id")
    private Usuario usuarioDestinatario;

    // registra o livro que o solicitante quer receber
    @ManyToOne
    @JoinColumn(name = "livro_desejado_id")
    private Livro livroDesejado;

    private LocalDateTime dataCadastro;

    private LocalDateTime dataAtualizacao;

    // permite guardar varios livros dentro da mesma oferta
    @OneToMany(mappedBy = "ofertaDeTroca")
    private List<LivroOfertado> livrosOfertados = new ArrayList<>();

    public Long getId(){return this.id;}

    public String getLeitorDono() {
        return leitorDono;
    }

    public void setLeitorDono(String leitorDono) {
        this.leitorDono = leitorDono;
    }

    // status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // leitor dono
    public String getSolicitante() {
        return leitorDono;
    }

    public void setSolicitante(String solicitante) {
        this.leitorDono = solicitante;
    }

    // livro ofertado
    public String getLivroOfertado() {
        return livroOfertado;
    }

    public void setLivroOfertado(String livroOfertado) {
        this.livroOfertado = livroOfertado;
    }

    public Usuario getUsuarioSolicitante() {
        return usuarioSolicitante;
    }

    public void setUsuarioSolicitante(Usuario solicitante) {
        this.usuarioSolicitante = solicitante;
    }

    public Usuario getUsuarioDestinatario() {
        return usuarioDestinatario;
    }

    public void setUsuarioDestinatario(Usuario usuarioDestinatario) {
        this.usuarioDestinatario = usuarioDestinatario;
    }

    public Livro getLivroDesejado() {
        return livroDesejado;
    }

    public void setLivroDesejado(Livro livroDesejado) {
        this.livroDesejado = livroDesejado;
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

    public List<LivroOfertado> getLivrosOfertados() {
        return livrosOfertados;
    }

    public void setLivrosOfertados(List<LivroOfertado> livrosOfertados) {
        this.livrosOfertados = livrosOfertados;
    }
}
