package com.example.booktrader.controller;

import com.example.booktrader.DTO.*;
import com.example.booktrader.entities.Livro;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.LivroRepository;
import com.example.booktrader.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/livro")
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Livro> ConsultaLivro() {
        return livroRepository.findAll();
    }

    @GetMapping("/{titulo}/titulo")
    public List<Livro> ConsultaLivrosPorTitulo(@PathVariable String titulo) {
        return livroRepository.getLivrosByTituloContainingIgnoreCase(titulo).orElse(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> ConsultaLivroPorId(@PathVariable Long id) {

        var livro = livroRepository.findById(id).orElse(null);

        if (livro == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(livro);
    }

    @PostMapping("")
    public ResponseEntity<LivroResponse> post(@RequestBody LivroRequest livroRequest) {

        // verifica se o id do usuario foi informado
        if (livroRequest.getUsuarioId() == null) {
            return ResponseEntity.badRequest().body(
                    new LivroResponse(null, "Informe o ID do usuario."));
        }

        // procura o usuario pelo id
        Usuario usuarioBanco = usuarioRepository
                .findById(livroRequest.getUsuarioId()).orElse(null);

        // verifica se o usuario existe
        if (usuarioBanco == null) {
            return ResponseEntity.badRequest().body(
                    new LivroResponse(null, "Usuario nao encontrado."));
        }

        Livro livroBanco = new Livro();

        livroBanco.setTitulo(livroRequest.getTitulo());
        livroBanco.setDescricao(livroRequest.getDescricao());
        livroBanco.setAutor(livroRequest.getAutor());
        livroBanco.setIsbn(livroRequest.getIsbn());
        livroBanco.setFotoCapa(livroRequest.getFotoCapa());
        livroBanco.setDataCadastro(LocalDateTime.now());

        // associa o usuario ao livro pelo relacionamento many to one
        livroBanco.setUsuario(usuarioBanco);

        livroRepository.save(livroBanco);

        return ResponseEntity.ok(new LivroResponse(
                livroBanco.getId(),
                "Livro Cadastrado Com Sucesso!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivroResponse> AtualizarLivro(
            @PathVariable Long id,
            @RequestBody LivroRequest livroRequest) {

        Livro livroBanco = livroRepository.findById(id).orElse(null);

        if (livroBanco != null) {
            livroBanco.setTitulo(livroRequest.getTitulo());
            livroBanco.setDescricao(livroRequest.getDescricao());
            livroBanco.setAutor(livroRequest.getAutor());
            livroBanco.setIsbn(livroRequest.getIsbn());
            livroBanco.setFotoCapa(livroRequest.getFotoCapa());

            livroRepository.save(livroBanco);

            return ResponseEntity.ok(
                    new LivroResponse(livroBanco.getId(), "Atualizado com Sucesso"));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LivroResponse> AtualizaStatus(@PathVariable Long id) {

        Livro livroBanco = livroRepository.findById(id).orElse(null);

        if (livroBanco != null) {

            livroRepository.save(livroBanco);
            return ResponseEntity.ok(
                    new LivroResponse(livroBanco.getId(), "Excluido com sucesso"));
        }

        return ResponseEntity.notFound().build();
    }
}