package com.example.booktrader.service;

import com.example.booktrader.DTO.AtualizaStatusSolicitacao;
import com.example.booktrader.DTO.OfertaDeTrocaConsultaResponse;
import com.example.booktrader.DTO.OfertaDeTrocaRequest;
import com.example.booktrader.entities.Livro;
import com.example.booktrader.entities.LivroOfertado;
import com.example.booktrader.entities.OfertaDeTroca;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.LivroOfertadoRepository;
import com.example.booktrader.repository.LivroRepository;
import com.example.booktrader.repository.OfertaDeTrocaRepository;
import com.example.booktrader.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// essa excecao informa ao spring o codigo http correspondente ao erro de regra.
// @service concentra as regras de troca para que ofertas e solicitacoes usem o mesmo fluxo.
@Service
public class OfertaDeTrocaService {

    private static final String PENDENTE = "PENDENTE";
    private static final String ACEITA = "ACEITA";
    private static final String RECUSADA = "RECUSADA";
    private static final String CANCELADA = "CANCELADA";
    private static final String ITEM_ACEITO = "ACEITO";
    private static final String ITEM_RECUSADO = "RECUSADO";

    private final OfertaDeTrocaRepository ofertaRepository;
    private final LivroOfertadoRepository livroOfertadoRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;

    public OfertaDeTrocaService(
            OfertaDeTrocaRepository ofertaRepository,
            LivroOfertadoRepository livroOfertadoRepository,
            LivroRepository livroRepository,
            UsuarioRepository usuarioRepository) {
        this.ofertaRepository = ofertaRepository;
        this.livroOfertadoRepository = livroOfertadoRepository;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // @transactional mantem a leitura e o carregamento das relacoes no mesmo contexto do banco.
    @Transactional(readOnly = true)
    public List<OfertaDeTrocaConsultaResponse> consultarEnviadas(String cpf) {
        Usuario usuario = buscarUsuario(cpf);
        return ofertaRepository.findByUsuarioSolicitanteIdOrderByDataCadastroDesc(usuario.getId())
                .stream()
                .map(OfertaDeTrocaConsultaResponse::new)
                .toList();
    }

    // @transactional mantem a leitura e o carregamento das relacoes no mesmo contexto do banco.
    @Transactional(readOnly = true)
    public List<OfertaDeTrocaConsultaResponse> consultarRecebidas(String cpf) {
        Usuario usuario = buscarUsuario(cpf);
        return ofertaRepository.findByUsuarioDestinatarioIdOrderByDataCadastroDesc(usuario.getId())
                .stream()
                .map(OfertaDeTrocaConsultaResponse::new)
                .toList();
    }

    // @transactional mantem a leitura e o carregamento das relacoes no mesmo contexto do banco.
    @Transactional(readOnly = true)
    public OfertaDeTrocaConsultaResponse consultarRecebida(Long id, String cpf) {
        Usuario usuario = buscarUsuario(cpf);
        OfertaDeTroca oferta = ofertaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (oferta.getUsuarioDestinatario() == null
                || !usuario.getId().equals(oferta.getUsuarioDestinatario().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solicitacao nao pertence a voce.");
        }

        return new OfertaDeTrocaConsultaResponse(oferta);
    }

    // @transactional mantem a leitura e o carregamento das relacoes no mesmo contexto do banco.
    @Transactional(readOnly = true)
    public OfertaDeTrocaConsultaResponse consultarPorId(Long id, String cpf) {
        Usuario usuario = buscarUsuario(cpf);
        OfertaDeTroca oferta = ofertaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        boolean ehSolicitante = oferta.getUsuarioSolicitante() != null
                && usuario.getId().equals(oferta.getUsuarioSolicitante().getId());
        boolean ehProprietario = oferta.getUsuarioDestinatario() != null
                && usuario.getId().equals(oferta.getUsuarioDestinatario().getId());

        if (!ehSolicitante && !ehProprietario) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Oferta nao pertence a voce.");
        }

        return new OfertaDeTrocaConsultaResponse(oferta);
    }

    // a criacao valida os donos pelo cpf do jwt, sem confiar nos ids enviados pelo navegador.
    // a troca e seus cancelamentos precisam ser salvos juntos ou desfeitos juntos em caso de erro.
    @Transactional
    public OfertaDeTrocaConsultaResponse criarOferta(OfertaDeTrocaRequest dados, String cpf) {
        if (dados == null || dados.getLivroDesejadoId() == null
                || dados.getLivrosOferecidosIds() == null
                || dados.getLivrosOferecidosIds().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o livro desejado e pelo menos um livro oferecido.");
        }

        validarIdsUnicos(dados.getLivrosOferecidosIds());
        if (dados.getLivrosOferecidosIds().contains(dados.getLivroDesejadoId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O livro desejado nao pode estar entre os livros oferecidos.");
        }

        Usuario solicitante = buscarUsuario(cpf);
        List<Long> ids = new ArrayList<>(dados.getLivrosOferecidosIds());
        ids.add(dados.getLivroDesejadoId());
        List<Livro> livros = livroRepository.findAllByIdsParaAtualizacao(ids);

        if (livros.size() != ids.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Um ou mais livros nao foram encontrados.");
        }

        Livro livroDesejado = buscarLivro(livros, dados.getLivroDesejadoId());
        if (livroDesejado.getUsuario() == null
                || solicitante.getId().equals(livroDesejado.getUsuario().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Escolha um livro que pertença a outro usuario.");
        }

        List<Livro> livrosOferecidos = new ArrayList<>();
        for (Long livroId : dados.getLivrosOferecidosIds()) {
            Livro livro = buscarLivro(livros, livroId);
            if (livro.getUsuario() == null
                    || !solicitante.getId().equals(livro.getUsuario().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Todos os livros oferecidos devem pertencer a voce.");
            }
            livrosOferecidos.add(livro);
        }

        OfertaDeTroca oferta = new OfertaDeTroca();
        oferta.setUsuarioSolicitante(solicitante);
        oferta.setUsuarioDestinatario(livroDesejado.getUsuario());
        oferta.setLivroDesejado(livroDesejado);
        oferta.setStatus(PENDENTE);
        oferta.setDataCadastro(LocalDateTime.now());
        oferta = ofertaRepository.save(oferta);

        List<LivroOfertado> itens = new ArrayList<>();
        for (Livro livro : livrosOferecidos) {
            LivroOfertado item = new LivroOfertado();
            item.setOfertaDeTroca(oferta);
            item.setLivro(livro);
            item.setStatus(PENDENTE);
            itens.add(item);
        }

        oferta.setLivrosOfertados(livroOfertadoRepository.saveAll(itens));
        return new OfertaDeTrocaConsultaResponse(oferta);
    }

    // a decisao aceita somente itens escolhidos e marca os demais como recusados.
    // o bloqueio e a transacao mantem a decisao e a transferencia como uma unica operacao.
    @Transactional
    public OfertaDeTrocaConsultaResponse decidirOferta(
            Long id,
            AtualizaStatusSolicitacao decisao,
            String cpf) {
        Usuario proprietario = buscarUsuario(cpf);
        List<OfertaDeTroca> ofertasPendentes = ofertaRepository.findByStatusParaAtualizacao(PENDENTE);
        OfertaDeTroca oferta = null;
        for (OfertaDeTroca ofertaPendente : ofertasPendentes) {
            if (ofertaPendente.getId().equals(id)) {
                oferta = ofertaPendente;
                break;
            }
        }
        if (oferta == null) {
            OfertaDeTroca ofertaFinalizada = ofertaRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            if (ofertaFinalizada.getUsuarioDestinatario() == null
                    || !proprietario.getId().equals(
                    ofertaFinalizada.getUsuarioDestinatario().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Solicitacao nao pertence a voce.");
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta oferta ja foi finalizada.");
        }

        if (oferta.getUsuarioDestinatario() == null
                || !proprietario.getId().equals(oferta.getUsuarioDestinatario().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solicitacao nao pertence a voce.");
        }
        if (!PENDENTE.equals(oferta.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta oferta ja foi finalizada.");
        }
        if (decisao == null || decisao.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe a decisao.");
        }

        List<LivroOfertado> itens = livroOfertadoRepository.findByOfertaDeTrocaIdOrderById(id);
        oferta.setLivrosOfertados(itens);
        if (RECUSADA.equalsIgnoreCase(decisao.getStatus())) {
            if (decisao.getLivrosAceitosIds() != null
                    && !decisao.getLivrosAceitosIds().isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Uma recusa total nao pode incluir livros aceitos.");
            }
            List<Livro> livroDesejado = livroRepository.findAllByIdsParaAtualizacao(
                    List.of(oferta.getLivroDesejado().getId()));
            if (livroDesejado.isEmpty()
                    || livroDesejado.get(0).getUsuario() == null
                    || !proprietario.getId().equals(livroDesejado.get(0).getUsuario().getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "A propriedade do livro mudou.");
            }
            oferta.setStatus(RECUSADA);
            finalizarItens(itens, ITEM_RECUSADO);
            oferta.setDataAtualizacao(LocalDateTime.now());
            ofertaRepository.save(oferta);
            livroOfertadoRepository.saveAll(itens);
            return new OfertaDeTrocaConsultaResponse(oferta);
        }

        if (!ACEITA.equalsIgnoreCase(decisao.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A decisao deve ser ACEITA ou RECUSADA.");
        }
        if (decisao.getLivrosAceitosIds() == null
                || decisao.getLivrosAceitosIds().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selecione pelo menos um livro ou recuse a oferta.");
        }
        validarIdsUnicos(decisao.getLivrosAceitosIds());

        Set<Long> idsOferecidos = new HashSet<>();
        for (LivroOfertado item : itens) {
            idsOferecidos.add(item.getLivro().getId());
        }
        if (!idsOferecidos.containsAll(decisao.getLivrosAceitosIds())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A selecao contem livros que nao fazem parte da oferta.");
        }

        List<Long> idsTransferidos = new ArrayList<>(decisao.getLivrosAceitosIds());
        idsTransferidos.add(oferta.getLivroDesejado().getId());
        List<Livro> livrosTransferidos = livroRepository.findAllByIdsParaAtualizacao(idsTransferidos);
        if (livrosTransferidos.size() != idsTransferidos.size()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Um dos livros nao esta mais disponivel.");
        }

        Usuario solicitante = oferta.getUsuarioSolicitante();
        Livro livroDesejado = buscarLivro(livrosTransferidos, oferta.getLivroDesejado().getId());
        if (solicitante == null || livroDesejado.getUsuario() == null
                || !proprietario.getId().equals(livroDesejado.getUsuario().getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A propriedade do livro mudou.");
        }

        for (Long livroId : decisao.getLivrosAceitosIds()) {
            Livro livro = buscarLivro(livrosTransferidos, livroId);
            if (livro.getUsuario() == null || !solicitante.getId().equals(livro.getUsuario().getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Um livro oferecido nao pertence mais ao solicitante.");
            }
            livro.setUsuario(proprietario);
        }
        livroDesejado.setUsuario(solicitante);

        for (LivroOfertado item : itens) {
            if (decisao.getLivrosAceitosIds().contains(item.getLivro().getId())) {
                item.setStatus(ITEM_ACEITO);
            } else {
                item.setStatus(ITEM_RECUSADO);
            }
        }
        oferta.setStatus(ACEITA);
        oferta.setDataAtualizacao(LocalDateTime.now());

        livroRepository.saveAll(livrosTransferidos);
        livroOfertadoRepository.saveAll(itens);
        ofertaRepository.save(oferta);
        cancelarOfertasConflitantes(oferta, idsTransferidos);

        return new OfertaDeTrocaConsultaResponse(oferta);
    }

    // o bloqueio e a transacao mantem o cancelamento e o historico consistentes.
    @Transactional
    public void cancelarOferta(Long id, String cpf) {
        Usuario solicitante = buscarUsuario(cpf);
        OfertaDeTroca oferta = ofertaRepository.findByIdParaAtualizacao(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (oferta.getUsuarioSolicitante() == null
                || !solicitante.getId().equals(oferta.getUsuarioSolicitante().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Voce nao pode cancelar esta oferta.");
        }
        if (!PENDENTE.equals(oferta.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta oferta ja foi finalizada.");
        }

        List<LivroOfertado> itens = livroOfertadoRepository.findByOfertaDeTrocaIdOrderById(id);
        finalizarItens(itens, CANCELADA);
        oferta.setLivrosOfertados(itens);
        oferta.setStatus(CANCELADA);
        oferta.setDataAtualizacao(LocalDateTime.now());
        ofertaRepository.save(oferta);
        livroOfertadoRepository.saveAll(itens);
    }

    // a troca aceita encerra propostas que dependem de qualquer livro transferido.
    private void cancelarOfertasConflitantes(OfertaDeTroca ofertaAceita, List<Long> idsTransferidos) {
        List<OfertaDeTroca> pendentes = ofertaRepository.findByStatusParaAtualizacao(PENDENTE);
        for (OfertaDeTroca outraOferta : pendentes) {
            if (outraOferta.getId().equals(ofertaAceita.getId())) {
                continue;
            }

            boolean livroEnvolvido = outraOferta.getLivroDesejado() != null
                    && idsTransferidos.contains(outraOferta.getLivroDesejado().getId());
            List<LivroOfertado> itens = outraOferta.getLivrosOfertados();
            for (LivroOfertado item : itens) {
                if (idsTransferidos.contains(item.getLivro().getId())) {
                    livroEnvolvido = true;
                }
            }

            if (livroEnvolvido) {
                outraOferta.setStatus(CANCELADA);
                outraOferta.setDataAtualizacao(LocalDateTime.now());
                finalizarItens(itens, CANCELADA);
                ofertaRepository.save(outraOferta);
                livroOfertadoRepository.saveAll(itens);
            }
        }
    }

    private void finalizarItens(List<LivroOfertado> itens, String status) {
        for (LivroOfertado item : itens) {
            if (PENDENTE.equals(item.getStatus())) {
                item.setStatus(status);
            }
        }
    }

    private void validarIdsUnicos(List<Long> ids) {
        for (int indice = 0; indice < ids.size(); indice++) {
            if (ids.get(indice) == null || ids.indexOf(ids.get(indice)) != indice) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A lista de livros possui um ID vazio ou repetido.");
            }
        }
    }

    private Usuario buscarUsuario(String cpf) {
        Usuario usuario = usuarioRepository.getUsuarioByCpf(cpf).orElse(null);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario nao autenticado.");
        }
        return usuario;
    }

    private Livro buscarLivro(List<Livro> livros, Long id) {
        for (Livro livro : livros) {
            if (livro.getId().equals(id)) {
                return livro;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro nao encontrado.");
    }
}
