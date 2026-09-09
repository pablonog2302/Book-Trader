package com.example.booktrader.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oferta")
public class OfertaDeTrocaController {

    @GetMapping()
    public String ConsultaOferta(){
        return "Consulta por Oferta";
    }

    @GetMapping("/{id}")
    public String ConsultaOfertaPorId(@PathVariable Long id){
        return "Consulta por Id";
    }

    @GetMapping("/empresa/{empresaId}")
    public String ConsultaEmpresa(@PathVariable Long empresaId){
        return "Consultou por Empresa";
    }



}
