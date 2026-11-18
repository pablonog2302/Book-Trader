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
import org.springframework.web.server.ResponseStatusException;

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado.");
        }

        return ResponseEntity.ok(livro);
    }

    @PostMapping("")
    public ResponseEntity<LivroResponse> post(
            @RequestBody LivroRequest livroRequest,
            HttpServletRequest request) {

        // o proprietario e obtido da autenticacao, nao de um id enviado pelo navegador.
        Usuario usuarioBanco = buscarUsuarioAutenticado(request);
        if (usuarioBanco == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Sua sessão expirou. Faça login novamente.");
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado.");
        }

        verificarProprietario(livroBanco, request);

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado.");
        }

        verificarProprietario(livroBanco, request);

        // livros referenciados por ofertas nao podem ser apagados para preservar o historico das negociacoes.
        if (ofertaRepository.existsByLivroDesejadoId(id)
                || livroOfertadoRepository.existsByLivroId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este livro faz parte do histórico de ofertas e não pode ser excluído.");
        }

        livroRepository.delete(livroBanco);
        return ResponseEntity.ok(
                new LivroResponse(livroBanco.getId(), "Excluido com sucesso"));
    }

    // o cpf validado pelo jwt identifica quem pode alterar ou excluir o livro.
    private void verificarProprietario(
            Livro livro,
            HttpServletRequest request) {

        Usuario usuarioAutenticado = buscarUsuarioAutenticado(request);
        if (usuarioAutenticado == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Sua sessão expirou. Faça login novamente.");
        }

        if (livro.getUsuario() == null
                || !usuarioAutenticado.getId().equals(livro.getUsuario().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você só pode alterar livros cadastrados por você.");
        }
    }

    private Usuario buscarUsuarioAutenticado(HttpServletRequest request) {
        String usuarioCpf = (String) request.getAttribute("usuarioCpf");
        if (usuarioCpf == null) {
            return null;
        }

        return usuarioRepository.getUsuarioByCpf(usuarioCpf).orElse(null);
    }
}