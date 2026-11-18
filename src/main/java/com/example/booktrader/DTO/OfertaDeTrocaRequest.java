
package com.example.booktrader.DTO;

import java.util.List;

public class OfertaDeTrocaRequest {

    private Long livroDesejadoId;
    private List<Long> livrosOferecidosIds;

    public Long getLivroDesejadoId() {
        return livroDesejadoId;
    }

    public void setLivroDesejadoId(Long livroDesejadoId) {
        this.livroDesejadoId = livroDesejadoId;
    }

    public List<Long> getLivrosOferecidosIds() {
        return livrosOferecidosIds;
    }

    public void setLivrosOferecidosIds(List<Long> livrosOferecidosIds) {
        this.livrosOferecidosIds = livrosOferecidosIds;
    }

}
