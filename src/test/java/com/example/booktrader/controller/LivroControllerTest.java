package com.example.booktrader.controller;

import com.example.booktrader.DTO.LivroRequest;
import com.example.booktrader.entities.Livro;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.LivroRepository;
import com.example.booktrader.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LivroControllerTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private LivroController livroController;

    private Usuario dono;
    private Usuario outroUsuario;
    private Livro livro;
    private MockHttpServletRequest request;

    @BeforeEach
    void prepararDados() {
        dono = new Usuario();
        dono.setId(1L);
        outroUsuario = new Usuario();
        outroUsuario.setId(2L);

        livro = new Livro();
        livro.setUsuario(dono);

        request = new MockHttpServletRequest();
        request.setAttribute("usuarioCpf", "12345678900");
    }

    @Test
    void atualizaLivroDoProprioUsuarioIncluindoGenero() {
        when(livroRepository.findById(10L)).thenReturn(Optional.of(livro));
        when(usuarioRepository.getUsuarioByCpf("12345678900")).thenReturn(Optional.of(dono));

        LivroRequest livroRequest = new LivroRequest();
        livroRequest.setTitulo("Novo título");
        livroRequest.setGenero("Aventura");

        var resposta = livroController.AtualizarLivro(10L, livroRequest, request);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Novo título", livro.getTitulo());
        assertEquals("Aventura", livro.getGenero());
        verify(livroRepository).save(livro);
    }

    @Test
    void cadastroUsaUsuarioDoTokenEmVezDoIdRecebidoNoFormulario() {
        when(usuarioRepository.getUsuarioByCpf("12345678900")).thenReturn(Optional.of(dono));
        when(livroRepository.save(any(Livro.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        LivroRequest livroRequest = new LivroRequest();
        livroRequest.setUsuarioId(outroUsuario.getId());
        livroRequest.setTitulo("Livro novo");

        var resposta = livroController.post(livroRequest, request);

        var captor = forClass(Livro.class);
        verify(livroRepository).save(captor.capture());
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertSame(dono, captor.getValue().getUsuario());
    }

    @Test
    void impedeEdicaoDeLivroDeOutroUsuario() {
        livro.setUsuario(outroUsuario);
        when(livroRepository.findById(10L)).thenReturn(Optional.of(livro));
        when(usuarioRepository.getUsuarioByCpf("12345678900")).thenReturn(Optional.of(dono));

        var resposta = livroController.AtualizarLivro(10L, new LivroRequest(), request);

        assertEquals(HttpStatus.FORBIDDEN, resposta.getStatusCode());
        verify(livroRepository, never()).save(livro);
    }

    @Test
    void impedeExclusaoDeLivroDeOutroUsuario() {
        livro.setUsuario(outroUsuario);
        when(livroRepository.findById(10L)).thenReturn(Optional.of(livro));
        when(usuarioRepository.getUsuarioByCpf("12345678900")).thenReturn(Optional.of(dono));

        var resposta = livroController.excluirLivro(10L, request);

        assertEquals(HttpStatus.FORBIDDEN, resposta.getStatusCode());
        verify(livroRepository, never()).delete(livro);
    }

    @Test
    void excluiLivroDoProprioUsuario() {
        when(livroRepository.findById(10L)).thenReturn(Optional.of(livro));
        when(usuarioRepository.getUsuarioByCpf("12345678900")).thenReturn(Optional.of(dono));

        var resposta = livroController.excluirLivro(10L, request);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(livroRepository).delete(livro);
    }
}
