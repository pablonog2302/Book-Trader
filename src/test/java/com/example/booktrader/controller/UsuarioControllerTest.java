package com.example.booktrader.controller;

import com.example.booktrader.DTO.UsuarioRequest;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioRequest dados;

    @BeforeEach
    void prepararDados() {
        dados = new UsuarioRequest();
        dados.setNome("Maria Silva");
        dados.setCpf("52998224725");
        dados.setDataNascimento("2000-01-01");
        dados.setSenha("00123456");
    }

    @Test
    void cadastraUsuarioComCpfValido() {
        when(usuarioRepository.existsUsuarioByCpf("52998224725")).thenReturn(false);

        var resposta = usuarioController.cadastrarUsuario(dados);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals(CREATED, resposta.getStatusCode());
        assertEquals("52998224725", captor.getValue().getCpf());
        assertEquals("Maria Silva", captor.getValue().getNome());
        assertEquals("00123456", captor.getValue().getSenha());
        assertEquals("A", captor.getValue().getStatus());
    }

    @Test
    void rejeitaCpfJaCadastrado() {
        when(usuarioRepository.existsUsuarioByCpf("52998224725")).thenReturn(true);

        var erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioController.cadastrarUsuario(dados));

        assertEquals(CONFLICT, erro.getStatusCode());
        assertEquals("Já existe uma conta cadastrada com este CPF.", erro.getReason());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void rejeitaCamposObrigatoriosInvalidos() {
        dados.setNome(" ");

        var erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioController.cadastrarUsuario(dados));

        assertEquals(400, erro.getStatusCode().value());
        assertEquals("Informe nome e data de nascimento.", erro.getReason());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void rejeitaCpfComPontuacao() {
        dados.setCpf("529.982.247-25");

        var erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioController.cadastrarUsuario(dados));

        assertEquals(400, erro.getStatusCode().value());
        assertEquals(
                "O CPF deve conter exatamente 11 números, sem letras ou símbolos.",
                erro.getReason());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void rejeitaSenhaMaiorQueOitoDigitos() {
        dados.setSenha("123456789");

        var erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioController.cadastrarUsuario(dados));

        assertEquals(400, erro.getStatusCode().value());
        assertEquals("A senha deve ter no máximo 8 dígitos.", erro.getReason());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void rejeitaSenhaComCaracteresNaoNumericos() {
        dados.setSenha("senha123");

        var erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioController.cadastrarUsuario(dados));

        assertEquals(400, erro.getStatusCode().value());
        assertEquals("A senha deve conter somente números.", erro.getReason());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void retornaIdentidadeDoUsuarioAutenticadoSemExporDadosExtras() {
        Usuario usuario = new Usuario();
        usuario.setId(9L);
        usuario.setNome("Maria Silva");
        when(usuarioRepository.getUsuarioByCpf("52998224725")).thenReturn(Optional.of(usuario));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("usuarioCpf", "52998224725");

        var resposta = usuarioController.consultarUsuarioAutenticado(request);

        assertEquals(200, resposta.getStatusCode().value());
        assertEquals(9L, resposta.getBody().getId().longValue());
        assertEquals("Maria Silva", resposta.getBody().getNome());
    }
}
