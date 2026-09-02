package com.example.booktrader.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/solicitacao")
public class SolicitacaoController {

    @GetMapping
    public String ConsultaSolicitacao(){
        return "Consultou por Solicitação";
    }

    @GetMapping("/{id}")
    public String ConsultaSolicitacaoPorId(@PathVariable Long id){
        return "Consultou Solicitação por ID";
    }

    @GetMapping("/empresa/{empresaId}")
    public String ConsultaPorEmpresa(@PathVariable String empresaId){
        return "Consultou por Empresa" + empresaId;
    }
}
