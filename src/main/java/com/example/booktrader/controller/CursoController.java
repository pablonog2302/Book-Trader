package com.example.booktrader.controller;

import com.example.booktrader.DTO.CursoConsultaResponse;
import com.example.booktrader.DTO.CursoRequest;
import com.example.booktrader.DTO.CursoResponse;
import com.example.booktrader.DTO.MatriculaRequest;
import com.example.booktrader.entities.Curso;
import com.example.booktrader.repository.CursoRepository;
import com.example.booktrader.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/curso")
public class CursoController {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<CursoResponse> cadastrarCurso(@RequestBody CursoRequest cursoRequest){
        Curso cursoBanco = new Curso();

        cursoBanco.setTitulo(cursoRequest.getTitulo());
        cursoBanco.setDescricao(cursoRequest.getDescricao());

        cursoRepository.save(cursoBanco);

        return ResponseEntity.ok(new CursoResponse(cursoBanco.getId(),"lasanha1"));

    }


    @GetMapping
    public ResponseEntity<List<CursoConsultaResponse>> listarTodos(){


        var listaCurso = cursoRepository.findAll()
                .stream()
                .map(CursoConsultaResponse::new).toList();

        return ResponseEntity.ok(listaCurso);


    }

    @PostMapping("/matricula")
    public ResponseEntity<CursoResponse> matricular(@RequestBody MatriculaRequest matriculaRequest){

        var usuarioBanco = usuarioRepository.findById(matriculaRequest.getUsuario_id()).orElse(null);
        var cursoBanco = cursoRepository.findById(matriculaRequest.getCurso_id()).orElse(null);

        if (usuarioBanco == null || cursoBanco == null){
            return ResponseEntity.notFound().build();
        }

        if (cursoBanco.getAlunos().contains(usuarioBanco)){
            throw new RuntimeException("usuario ja consta");
        }

        cursoBanco.adicionarAluno(usuarioBanco);

        cursoRepository.save(cursoBanco);

        return ResponseEntity.ok(new CursoResponse(cursoBanco.getId(),"lasanha2"));
    }

}