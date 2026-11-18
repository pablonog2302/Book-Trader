package com.example.booktrader.controller;

import com.example.booktrader.DTO.LoginRequest;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void prepararConfiguracao() {
        ReflectionTestUtils.setField(loginController, "secret", "booktrader-test-secret-0123456789");
        ReflectionTestUtils.setField(loginController, "emissor", "booktrader-test");
        ReflectionTestUtils.setField(loginController, "tempo", 60L);
    }

    @Test
    void rejeitaCpfComPontuacaoAntesDeConsultarRepositorio() {
        var erro = assertThrows(
                ResponseStatusException.class,
                () -> loginController.logar(requisicao("529.982.247-25", "12345678")));

        assertEquals(BAD_REQUEST, erro.getStatusCode());
        assertEquals(
                "O CPF deve conter exatamente 11 números, sem letras ou símbolos.",
                erro.getReason());
        verify(usuarioRepository, never()).existsUsuarioByCpfAndSenha(
                "52998224725",
                "12345678");
    }

    @Test
    void rejeitaSenhaAcimaDoLimiteAntesDeConsultarRepositorio() {
        var erro = assertThrows(
                ResponseStatusException.class,
                () -> loginController.logar(requisicao("52998224725", "123456789")));

        assertEquals(BAD_REQUEST, erro.getStatusCode());
        assertEquals("A senha deve ter no máximo 8 dígitos.", erro.getReason());
        verify(usuarioRepository, never()).existsUsuarioByCpfAndSenha(
                "52998224725",
                "123456789");
    }

    @Test
    void rejeitaSenhaComCaracteresNaoNumericosAntesDeConsultarRepositorio() {
        var erro = assertThrows(
                ResponseStatusException.class,
                () -> loginController.logar(requisicao("52998224725", "senha123")));

        assertEquals(BAD_REQUEST, erro.getStatusCode());
        assertEquals("A senha deve conter somente números.", erro.getReason());
        verify(usuarioRepository, never()).existsUsuarioByCpfAndSenha(
                "52998224725",
                "senha123");
    }

    @Test
    void autenticaContaComCpfEsenhaNumericosValidos() {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setNome("Maria Silva");
        when(usuarioRepository.existsUsuarioByCpfAndSenha("52998224725", "12345678"))
                .thenReturn(true);
        when(usuarioRepository.getUsuarioByCpf("52998224725")).thenReturn(Optional.of(usuario));

        var resposta = loginController.logar(requisicao("52998224725", "12345678"));

        assertEquals(OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(7L, resposta.getBody().getUsuarioId());
        assertEquals("Maria Silva", resposta.getBody().getUsuarioNome());
        assertNotNull(resposta.getBody().getToken());
    }

    private LoginRequest requisicao(String cpf, String senha) {
        LoginRequest request = new LoginRequest();
        request.setLogin(cpf);
        request.setSenha(senha);
        return request;
    }
}
