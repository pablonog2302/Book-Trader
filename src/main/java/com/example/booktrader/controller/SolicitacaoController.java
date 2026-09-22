package com.example.booktrader.controller;

import com.example.booktrader.DTO.AtualizaStatusSolicitacao;
import com.example.booktrader.DTO.SolicitacaoRequest;
import com.example.booktrader.DTO.SolicitacaoResponse;
import com.example.booktrader.entities.Solicitacao;
import com.example.booktrader.repository.SolicitacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/solicitacao")
public class SolicitacaoController { 

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @GetMapping
    public List<Solicitacao> consultaSolicitacao() {
        return solicitacaoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Solicitacao> consultaSolicitacaoPorId(@PathVariable Long id) {
        var solicitacao = solicitacaoRepository.findById(id).orElse(null);

        if (solicitacao == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(solicitacao);
    }

    @PostMapping("")
    public ResponseEntity<SolicitacaoResponse> post(@RequestBody SolicitacaoRequest solicitacaoRequest) {

        Solicitacao solicitacaoBanco = new Solicitacao();

        solicitacaoBanco.setOfertaDeTroca(solicitacaoRequest.getOfertaDeTroca());
        solicitacaoBanco.setDataCadastro(LocalDateTime.now());

        solicitacaoRepository.save(solicitacaoBanco);

        return ResponseEntity.ok(new SolicitacaoResponse(solicitacaoBanco.getId(),
                "Solicitação Cadastrada Com Sucesso!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolicitacaoResponse> atualizarSolicitacao(@PathVariable Long id, @RequestBody SolicitacaoRequest solicitacaoRequest) {

        // consulta no banco
        Solicitacao solicitacaoBanco = solicitacaoRepository.findById(id).orElse(null);

        if (solicitacaoBanco != null) {
            solicitacaoBanco.setOfertaDeTroca(solicitacaoRequest.getOfertaDeTroca());
            solicitacaoBanco.setDataAtualizacao(LocalDateTime.now());

            solicitacaoRepository.save(solicitacaoBanco);
            return ResponseEntity.ok(new SolicitacaoResponse(solicitacaoBanco.getId(), "Cadastro Com Sucesso"));
        } 

        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SolicitacaoResponse> atualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusSolicitacao solicitacaoRequest) {

        // consulta no banco
        Solicitacao solicitacaoBanco = solicitacaoRepository.findById(id).orElse(null);

        if (solicitacaoBanco != null) {
            solicitacaoBanco.setStatus(solicitacaoRequest.getStatus());

            solicitacaoRepository.save(solicitacaoBanco);
            return ResponseEntity.ok(new SolicitacaoResponse(solicitacaoBanco.getId(), "Cadastro Com Sucesso"));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SolicitacaoResponse> deletarSolicitacao(@PathVariable Long id) {

        // consulta no banco
        Solicitacao solicitacaoBanco = solicitacaoRepository.findById(id).orElse(null);

        if (solicitacaoBanco != null) {
            solicitacaoBanco.setStatus("D");

            solicitacaoRepository.save(solicitacaoBanco);
            return ResponseEntity.ok(new SolicitacaoResponse(solicitacaoBanco.getId(), "Excluído Com Sucesso"));
        }

        return ResponseEntity.notFound().build();
    }
}