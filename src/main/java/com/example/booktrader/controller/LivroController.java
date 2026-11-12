package com.example.booktrader.controller;

import com.example.booktrader.DTO.*;
import com.example.booktrader.entities.Livro;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.LivroRepository;
import com.example.booktrader.repository.LivroOfertadoRepository;
import com.example.booktrader.repository.OfertaDeTrocaRepository;
import com.example.booktrader.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private OfertaDeTrocaRepository ofertaRepository;

    @Autowired
    private LivroOfertadoRepository livroOfertadoRepository;

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
    public ResponseEntity<LivroResponse> post(
            @RequestBody LivroRequest livroRequest,
            HttpServletRequest request) {

        Usuario usuarioBanco = buscarUsuarioAutenticado(request);
        if (usuarioBanco == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LivroResponse(null, "Usuario nao autenticado."));
        }

        Livro livroBanco = new Livro();

        livroBanco.setTitulo(livroRequest.getTitulo());
        livroBanco.setDescricao(livroRequest.getDescricao());
        livroBanco.setAutor(livroRequest.getAutor());
        livroBanco.setGenero(livroRequest.getGenero());
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
            @RequestBody LivroRequest livroRequest,
            HttpServletRequest request) {

        Livro livroBanco = livroRepository.findById(id).orElse(null);

        if (livroBanco == null) {
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<LivroResponse> respostaAcesso = verificarProprietario(livroBanco, request);
        if (respostaAcesso != null) {
            return respostaAcesso;
        }

        livroBanco.setTitulo(livroRequest.getTitulo());
        livroBanco.setDescricao(livroRequest.getDescricao());
        livroBanco.setAutor(livroRequest.getAutor());
        livroBanco.setGenero(livroRequest.getGenero());
        livroBanco.setIsbn(livroRequest.getIsbn());
        livroBanco.setFotoCapa(livroRequest.getFotoCapa());

        livroRepository.save(livroBanco);

        return ResponseEntity.ok(
                new LivroResponse(livroBanco.getId(), "Atualizado com Sucesso"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LivroResponse> excluirLivro(
            @PathVariable Long id,
            HttpServletRequest request) {

        Livro livroBanco = livroRepository.findById(id).orElse(null);

        if (livroBanco == null) {
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<LivroResponse> respostaAcesso = verificarProprietario(livroBanco, request);
        if (respostaAcesso != null) {
            return respostaAcesso;
        }

        if (ofertaRepository.existsByLivroDesejadoId(id)
                || livroOfertadoRepository.existsByLivroId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new LivroResponse(null,
                            "Este livro faz parte do historico de ofertas e nao pode ser excluido."));
        }

        livroRepository.delete(livroBanco);
        return ResponseEntity.ok(
                new LivroResponse(livroBanco.getId(), "Excluido com sucesso"));
    }

    private ResponseEntity<LivroResponse> verificarProprietario(
            Livro livro,
            HttpServletRequest request) {

        Usuario usuarioAutenticado = buscarUsuarioAutenticado(request);
        if (usuarioAutenticado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LivroResponse(null, "Usuario nao autenticado."));
        }

        if (livro.getUsuario() == null
                || !usuarioAutenticado.getId().equals(livro.getUsuario().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new LivroResponse(null, "Voce nao pode alterar este livro."));
        }

        return null;
    }

    private Usuario buscarUsuarioAutenticado(HttpServletRequest request) {
        String usuarioCpf = (String) request.getAttribute("usuarioCpf");
        if (usuarioCpf == null) {
            return null;
        }

        return usuarioRepository.getUsuarioByCpf(usuarioCpf).orElse(null);
    }
}