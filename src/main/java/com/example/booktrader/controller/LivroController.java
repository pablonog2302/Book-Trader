package com.example.booktrader.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/livro")
public class LivroController {

    @GetMapping
    public String ConsultaLivro(){
        return "Consultou Livro";
    }

    @GetMapping("/{id}")
    public String ConsultaLivroPorId(@PathVariable Long id){
        return "Consultou Livro por ID";
    }

    @GetMapping("/empresa/{empresaId}")
    public String ConsultaPorEmpresa(@PathVariable String empresaId){
        return "Consultou por Empresa" + empresaId;
    }

}
