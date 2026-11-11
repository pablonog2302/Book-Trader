package com.example.booktrader.controller;

import com.example.booktrader.DTO.UsuarioRequest;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.EmpresaRepository;
import com.example.booktrader.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioRequest dados;

    @BeforeEach
    void prepararDados() {
        dados = new UsuarioRequest();
        dados.setNome("Maria Silva");
        dados.setCpf("529.982.247-25");
        dados.setDataNascimento("2000-01-01");
        dados.setSenha("senha123");
    }

    @Test
    void cadastraUsuarioComCpfNormalizado() {
        when(usuarioRepository.existsUsuarioByCpf("52998224725")).thenReturn(false);

        var resposta = usuarioController.cadastrarUsuario(dados);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals(CREATED, resposta.getStatusCode());
        assertEquals("52998224725", captor.getValue().getCpf());
        assertEquals("Maria Silva", captor.getValue().getNome());
        assertEquals("A", captor.getValue().getStatus());
    }

    @Test
    void rejeitaCpfJaCadastrado() {
        when(usuarioRepository.existsUsuarioByCpf("52998224725")).thenReturn(true);

        var resposta = usuarioController.cadastrarUsuario(dados);

        assertEquals(CONFLICT, resposta.getStatusCode());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void rejeitaCamposObrigatoriosInvalidos() {
        dados.setNome(" ");

        var resposta = usuarioController.cadastrarUsuario(dados);

        assertEquals(400, resposta.getStatusCode().value());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
