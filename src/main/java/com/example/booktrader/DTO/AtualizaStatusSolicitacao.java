package com.example.booktrader.DTO;

import java.util.List;

public class AtualizaStatusSolicitacao {

    public AtualizaStatusSolicitacao(){}

    private String status;
    private List<Long> livrosAceitosIds;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Long> getLivrosAceitosIds() {
        return livrosAceitosIds;
    }

    public void setLivrosAceitosIds(List<Long> livrosAceitosIds) {
        this.livrosAceitosIds = livrosAceitosIds;
    }
}
