package com.example.booktrader.controller;

import com.example.booktrader.DTO.LivroRequest;
import com.example.booktrader.DTO.LivroResponse;
import com.example.booktrader.entities.Livro;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("")
    public ResponseEntity<LivroResponse> post(@RequestBody LivroRequest livroRequest) {

        Livro livroBanco = new Livro();

        livroBanco.setTitulo(livroRequest.getTitulo());
        livroBanco.setDescricao(livroRequest.getDescricao());
        livroBanco.setAutor(livroRequest.getAutor());
        livroBanco.setIsbn(livroRequest.getIsbn());
        livroBanco.setFotoCapa(livroRequest.getFotoCapa());

        return ResponseEntity.ok(new LivroResponse(livroBanco.getId(),
                "Livro Cadastrado Com Sucesso!"));
    }


}
