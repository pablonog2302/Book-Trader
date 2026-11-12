package com.example.booktrader.controller;

import com.example.booktrader.DTO.OfertaDeTrocaConsultaResponse;
import com.example.booktrader.DTO.OfertaDeTrocaRequest;
import com.example.booktrader.service.OfertaDeTrocaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/oferta")
public class OfertaDeTrocaController {

    private final OfertaDeTrocaService ofertaService;

    public OfertaDeTrocaController(OfertaDeTrocaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    @GetMapping
    public List<OfertaDeTrocaConsultaResponse> consultarEnviadas(HttpServletRequest request) {
        return ofertaService.consultarEnviadas((String) request.getAttribute("usuarioCpf"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfertaDeTrocaConsultaResponse> consultarPorId(
            @PathVariable Long id,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                ofertaService.consultarPorId(id, (String) request.getAttribute("usuarioCpf")));
    }

    @PostMapping
    public ResponseEntity<OfertaDeTrocaConsultaResponse> criarOferta(
            @RequestBody OfertaDeTrocaRequest dados,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ofertaService.criarOferta(dados, (String) request.getAttribute("usuarioCpf")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarOferta(
            @PathVariable Long id,
            HttpServletRequest request) {
        ofertaService.cancelarOferta(id, (String) request.getAttribute("usuarioCpf"));
        return ResponseEntity.noContent().build();
    }
}
