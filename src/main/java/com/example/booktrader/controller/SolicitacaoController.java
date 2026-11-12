package com.example.booktrader.controller;

import com.example.booktrader.DTO.AtualizaStatusSolicitacao;
import com.example.booktrader.DTO.OfertaDeTrocaConsultaResponse;
import com.example.booktrader.service.OfertaDeTrocaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/solicitacao")
public class SolicitacaoController {

    private final OfertaDeTrocaService ofertaService;

    public SolicitacaoController(OfertaDeTrocaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    @GetMapping
    public List<OfertaDeTrocaConsultaResponse> consultarRecebidas(HttpServletRequest request) {
        return ofertaService.consultarRecebidas((String) request.getAttribute("usuarioCpf"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfertaDeTrocaConsultaResponse> consultarRecebida(
            @PathVariable Long id,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                ofertaService.consultarRecebida(id, (String) request.getAttribute("usuarioCpf")));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OfertaDeTrocaConsultaResponse> decidirOferta(
            @PathVariable Long id,
            @RequestBody AtualizaStatusSolicitacao decisao,
            HttpServletRequest request) {
        return ResponseEntity.ok(ofertaService.decidirOferta(
                id,
                decisao,
                (String) request.getAttribute("usuarioCpf")));
    }
}
