package com.example.booktrader.service;

import com.example.booktrader.DTO.AtualizaStatusSolicitacao;
import com.example.booktrader.DTO.OfertaDeTrocaRequest;
import com.example.booktrader.entities.Livro;
import com.example.booktrader.entities.LivroOfertado;
import com.example.booktrader.entities.OfertaDeTroca;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.LivroOfertadoRepository;
import com.example.booktrader.repository.LivroRepository;
import com.example.booktrader.repository.OfertaDeTrocaRepository;
import com.example.booktrader.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfertaDeTrocaServiceTest {

    @Mock
    private OfertaDeTrocaRepository ofertaRepository;

    @Mock
    private LivroOfertadoRepository livroOfertadoRepository;

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private OfertaDeTrocaService ofertaService;

    private Usuario solicitante;
    private Usuario proprietario;
    private Livro desejado;
    private Livro oferecido;
    private Livro outroOferecido;

    @BeforeEach
    void prepararDados() {
        solicitante = usuario(1L, "Ana");
        proprietario = usuario(2L, "Bia");
        desejado = livro(20L, "Livro desejado", proprietario);
        oferecido = livro(10L, "Livro oferecido", solicitante);
        outroOferecido = livro(11L, "Outro livro", solicitante);
    }

    @Test
    void criaOfertaSomenteComLivroDeOutroUsuarioEItensProprios() {
        when(usuarioRepository.getUsuarioByCpf("cpf-ana")).thenReturn(Optional.of(solicitante));
        when(livroRepository.findAllByIdsParaAtualizacao(List.of(10L, 11L, 20L)))
                .thenReturn(List.of(oferecido, outroOferecido, desejado));
        when(ofertaRepository.save(any(OfertaDeTroca.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
        when(livroOfertadoRepository.saveAll(org.mockito.ArgumentMatchers.<LivroOfertado>anyList()))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        OfertaDeTrocaRequest dados = new OfertaDeTrocaRequest();
        dados.setLivroDesejadoId(20L);
        dados.setLivrosOferecidosIds(List.of(10L, 11L));

        var resposta = ofertaService.criarOferta(dados, "cpf-ana");

        assertEquals("PENDENTE", resposta.getStatus());
        assertEquals("Livro desejado", resposta.getLivroDesejadoTitulo());
        assertEquals(2, resposta.getLivrosOferecidos().size());
    }

    @Test
    void impedeOfertaDeLivroQuePertenceAOutraPessoa() {
        Livro livroDeOutro = livro(11L, "Livro alheio", proprietario);
        when(usuarioRepository.getUsuarioByCpf("cpf-ana")).thenReturn(Optional.of(solicitante));
        when(livroRepository.findAllByIdsParaAtualizacao(List.of(11L, 20L)))
                .thenReturn(List.of(livroDeOutro, desejado));

        OfertaDeTrocaRequest dados = new OfertaDeTrocaRequest();
        dados.setLivroDesejadoId(20L);
        dados.setLivrosOferecidosIds(List.of(11L));

        assertThrows(ResponseStatusException.class,
                () -> ofertaService.criarOferta(dados, "cpf-ana"));
        verify(ofertaRepository, never()).save(any(OfertaDeTroca.class));
    }

    @Test
    void aceitaParteDosLivrosTransfereEFechaOfertasConflitantes() {
        OfertaDeTroca oferta = oferta(100L, solicitante, desejado, "PENDENTE");
        LivroOfertado itemAceito = item(oferta, oferecido);
        LivroOfertado itemRecusado = item(oferta, outroOferecido);
        oferta.setLivrosOfertados(List.of(itemAceito, itemRecusado));

        Livro livroDesejadoPelaOutraOferta = livro(30L, "Outro desejado", proprietario);
        OfertaDeTroca conflito = oferta(101L, proprietario, livroDesejadoPelaOutraOferta, "PENDENTE");
        conflito.setLivrosOfertados(List.of(item(conflito, oferecido)));

        when(usuarioRepository.getUsuarioByCpf("cpf-bia")).thenReturn(Optional.of(proprietario));
        when(ofertaRepository.findByStatusParaAtualizacao("PENDENTE"))
                .thenReturn(List.of(oferta, conflito), List.of(conflito));
        when(livroOfertadoRepository.findByOfertaDeTrocaIdOrderById(100L))
                .thenReturn(List.of(itemAceito, itemRecusado));
        when(livroRepository.findAllByIdsParaAtualizacao(List.of(10L, 20L)))
                .thenReturn(List.of(oferecido, desejado));
        when(ofertaRepository.save(any(OfertaDeTroca.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
        when(livroRepository.saveAll(org.mockito.ArgumentMatchers.<Livro>anyList()))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
        when(livroOfertadoRepository.saveAll(org.mockito.ArgumentMatchers.<LivroOfertado>anyList()))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        AtualizaStatusSolicitacao decisao = new AtualizaStatusSolicitacao();
        decisao.setStatus("ACEITA");
        decisao.setLivrosAceitosIds(List.of(10L));

        var resposta = ofertaService.decidirOferta(100L, decisao, "cpf-bia");

        assertEquals("ACEITA", resposta.getStatus());
        assertEquals(solicitante, desejado.getUsuario());
        assertEquals(proprietario, oferecido.getUsuario());
        assertEquals(solicitante, outroOferecido.getUsuario());
        assertEquals("ACEITO", itemAceito.getStatus());
        assertEquals("RECUSADO", itemRecusado.getStatus());
        assertEquals("CANCELADA", conflito.getStatus());
    }

    @Test
    void recusaTotalNaoTransfereLivro() {
        OfertaDeTroca oferta = oferta(100L, solicitante, desejado, "PENDENTE");
        LivroOfertado item = item(oferta, oferecido);
        oferta.setLivrosOfertados(List.of(item));
        when(usuarioRepository.getUsuarioByCpf("cpf-bia")).thenReturn(Optional.of(proprietario));
        when(ofertaRepository.findByStatusParaAtualizacao("PENDENTE")).thenReturn(List.of(oferta));
        when(livroOfertadoRepository.findByOfertaDeTrocaIdOrderById(100L)).thenReturn(List.of(item));
        when(livroRepository.findAllByIdsParaAtualizacao(List.of(20L))).thenReturn(List.of(desejado));
        when(ofertaRepository.save(any(OfertaDeTroca.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
        when(livroOfertadoRepository.saveAll(org.mockito.ArgumentMatchers.<LivroOfertado>anyList()))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        AtualizaStatusSolicitacao decisao = new AtualizaStatusSolicitacao();
        decisao.setStatus("RECUSADA");

        var resposta = ofertaService.decidirOferta(100L, decisao, "cpf-bia");

        assertEquals("RECUSADA", resposta.getStatus());
        assertEquals(proprietario, desejado.getUsuario());
        assertEquals(solicitante, oferecido.getUsuario());
        assertEquals("RECUSADO", item.getStatus());
        verify(livroRepository, never()).saveAll(org.mockito.ArgumentMatchers.<Livro>anyList());
    }

    @Test
    void impedeProprietarioDiferenteDeDecidirOfertaRecebida() {
        Usuario terceiro = usuario(3L, "Caio");
        OfertaDeTroca oferta = oferta(100L, solicitante, desejado, "PENDENTE");
        when(usuarioRepository.getUsuarioByCpf("cpf-caio")).thenReturn(Optional.of(terceiro));
        when(ofertaRepository.findByStatusParaAtualizacao("PENDENTE")).thenReturn(List.of(oferta));

        AtualizaStatusSolicitacao decisao = new AtualizaStatusSolicitacao();
        decisao.setStatus("RECUSADA");

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> ofertaService.decidirOferta(100L, decisao, "cpf-caio"));

        assertEquals(HttpStatus.FORBIDDEN, erro.getStatusCode());
        verify(ofertaRepository, never()).save(any(OfertaDeTroca.class));
    }

    @Test
    void naoPermiteDecidirNovamenteOfertaFinalizada() {
        OfertaDeTroca oferta = oferta(100L, solicitante, desejado, "ACEITA");
        when(usuarioRepository.getUsuarioByCpf("cpf-bia")).thenReturn(Optional.of(proprietario));
        when(ofertaRepository.findByStatusParaAtualizacao("PENDENTE")).thenReturn(List.of());
        when(ofertaRepository.findById(100L)).thenReturn(Optional.of(oferta));

        AtualizaStatusSolicitacao decisao = new AtualizaStatusSolicitacao();
        decisao.setStatus("RECUSADA");

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> ofertaService.decidirOferta(100L, decisao, "cpf-bia"));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
    }

    private Usuario usuario(Long id, String nome) {
        Usuario usuario = new Usuario();
        // reflectiontestutils preenche ids gerados pelo banco sem adicionar setters so para testes.
        ReflectionTestUtils.setField(usuario, "id", id);
        usuario.setNome(nome);
        return usuario;
    }

    private Livro livro(Long id, String titulo, Usuario usuario) {
        Livro livro = new Livro();
        ReflectionTestUtils.setField(livro, "id", id);
        livro.setTitulo(titulo);
        livro.setAutor("Autor");
        livro.setUsuario(usuario);
        return livro;
    }

    private OfertaDeTroca oferta(Long id, Usuario solicitante, Livro desejado, String status) {
        OfertaDeTroca oferta = new OfertaDeTroca();
        ReflectionTestUtils.setField(oferta, "id", id);
        oferta.setUsuarioSolicitante(solicitante);
        oferta.setUsuarioDestinatario(desejado.getUsuario());
        oferta.setLivroDesejado(desejado);
        oferta.setStatus(status);
        return oferta;
    }

    private LivroOfertado item(OfertaDeTroca oferta, Livro livro) {
        LivroOfertado item = new LivroOfertado();
        item.setOfertaDeTroca(oferta);
        item.setLivro(livro);
        item.setStatus("PENDENTE");
        return item;
    }
}
