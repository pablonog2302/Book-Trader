package com.example.booktrader.controller;


import com.example.booktrader.DTO.*;
import com.example.booktrader.entities.Livro;
import com.example.booktrader.entities.OfertaDeTroca;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.OfertaDeTrocaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/oferta")
public class OfertaDeTrocaController {

    @Autowired
    private OfertaDeTrocaRepository ofertaDeTrocaRepository;


    @GetMapping
    public List<OfertaDeTroca> ConsultaOfertaDeTroca(){
        return ofertaDeTrocaRepository.findAll();
    }

    @GetMapping("/{livroOfertado}/livroOfertado")
    public List<OfertaDeTroca> ConsultaOfertaDeTrocaPorLivroOfertado(@PathVariable String livroOfertado){
        return ofertaDeTrocaRepository.getOfertaDeTrocaByLivroOfertadoContainingIgnoreCase(livroOfertado).orElse(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfertaDeTroca> ConsultaOfertaDeTrocaPorId(@PathVariable Long id){

        var ofertaDeTroca = ofertaDeTrocaRepository.findById(id).orElse(null);

        if (ofertaDeTroca == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ofertaDeTroca);
    }

    @GetMapping("/{status}/status")
    public List<OfertaDeTroca> ConsultaOfertaDeTrocaPorStatus(@PathVariable String status){
        return ofertaDeTrocaRepository.getOfertaDeTrocaByStatusContainingIgnoreCase(status).orElse(null);
    }

    @PostMapping("")
    public ResponseEntity<OfertaDeTrocaResponse> post(@RequestBody OfertaDeTrocaRequest ofertaDeTrocaRequest) {

        OfertaDeTroca ofertaDeTrocaBanco = new OfertaDeTroca();

        ofertaDeTrocaBanco.setLivroOfertado(ofertaDeTrocaRequest.getLivroOfertado());

        ofertaDeTrocaRepository.save(ofertaDeTrocaBanco);

        return ResponseEntity.ok(new OfertaDeTrocaResponse(ofertaDeTrocaBanco.getId(),
                "Oferta Cadastrada Com Sucesso!"));
    }


    @PatchMapping("/{id}/livroOfertado")
    public ResponseEntity<OfertaDeTrocaResponse>
    AtualizarLivroOfertado(@PathVariable Long id, @RequestBody AtualizaLivro usuarioRequest){

        //consulta no banco
        OfertaDeTroca ofertaDeTrocaBanco = ofertaDeTrocaRepository.findById(id).orElse(null);


        if (ofertaDeTrocaBanco != null){

            ofertaDeTrocaBanco.setLivroOfertado(ofertaDeTrocaBanco.getLivroOfertado());

            ofertaDeTrocaRepository.save(ofertaDeTrocaBanco);
            return ResponseEntity.ok(new OfertaDeTrocaResponse(ofertaDeTrocaBanco.getId(), "Cadastro Com Sucesso"));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OfertaDeTrocaResponse>
    AtualizaStatus(@PathVariable Long id){

        OfertaDeTroca ofertaDeTrocaBanco = ofertaDeTrocaRepository.findById(id).orElse(null);

        if (ofertaDeTrocaBanco != null){

            ofertaDeTrocaRepository.save(ofertaDeTrocaBanco);
            return ResponseEntity.ok(new OfertaDeTrocaResponse(ofertaDeTrocaBanco.getId(), "Excluido com sucesso"));
        }
        return ResponseEntity.notFound().build();
    }


}
