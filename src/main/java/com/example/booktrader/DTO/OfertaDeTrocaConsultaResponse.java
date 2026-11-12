package com.example.booktrader.DTO;

import com.example.booktrader.entities.OfertaDeTroca;

import java.time.LocalDateTime;
import java.util.List;

public class OfertaDeTrocaConsultaResponse {

    private Long id;
    private String status;
    private LocalDateTime dataCadastro;
    private Long solicitanteId;
    private String solicitanteNome;
    private Long proprietarioId;
    private String proprietarioNome;
    private Long livroDesejadoId;
    private String livroDesejadoTitulo;
    private List<LivroOfertadoResponse> livrosOferecidos;

    public OfertaDeTrocaConsultaResponse(OfertaDeTroca oferta) {
        id = oferta.getId();
        status = oferta.getStatus();
        dataCadastro = oferta.getDataCadastro();
        solicitanteId = oferta.getUsuarioSolicitante().getId();
        solicitanteNome = oferta.getUsuarioSolicitante().getNome();
        proprietarioId = oferta.getUsuarioDestinatario().getId();
        proprietarioNome = oferta.getUsuarioDestinatario().getNome();
        livroDesejadoId = oferta.getLivroDesejado().getId();
        livroDesejadoTitulo = oferta.getLivroDesejado().getTitulo();
        livrosOferecidos = oferta.getLivrosOfertados().stream()
                .map(LivroOfertadoResponse::new)
                .toList();
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public Long getSolicitanteId() {
        return solicitanteId;
    }

    public String getSolicitanteNome() {
        return solicitanteNome;
    }

    public Long getProprietarioId() {
        return proprietarioId;
    }

    public String getProprietarioNome() {
        return proprietarioNome;
    }

    public Long getLivroDesejadoId() {
        return livroDesejadoId;
    }

    public String getLivroDesejadoTitulo() {
        return livroDesejadoTitulo;
    }

    public List<LivroOfertadoResponse> getLivrosOferecidos() {
        return livrosOferecidos;
    }
}
