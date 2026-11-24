
const sections = {
    home: {
        title: 'Início',
        content: `
            <div class="panel home-panel">
                <p class="eyebrow">Bem-vindo ao BookTrader</p>
                <h2>Uma nova história pode começar com uma troca.</h2>
                <p>Encontre livros que despertam sua curiosidade e ofereça novas leituras para a comunidade.</p>
                <div class="home-actions">
                    <button type="button" onclick="renderSection('livros')">Explorar livros</button>
                    <button type="button" class="button-secondary" onclick="renderSection('ofertas')">Acompanhar minhas ofertas</button>
                </div>
            </div>
            <div class="panel home-step">
                <span class="step-number">01</span>
                <h3>Descubra</h3>
                <p>Explore o catálogo e encontre seu próximo livro.</p>
            </div>
            <div class="panel home-step">
                <span class="step-number">02</span>
                <h3>Proponha</h3>
                <p>Escolha um livro seu para oferecer em troca.</p>
            </div>
            <div class="panel home-step">
                <span class="step-number">03</span>
                <h3>Acompanhe</h3>
                <p>Veja suas ofertas e responda às solicitações recebidas.</p>
            </div>
        `,
    },
    livros: {
        title: 'Livros',
        content: `
            <div class="panel">
                <h2>Livros cadastrados</h2>
                <p>Carregando livros...</p>
            </div>
        `,
    },
    ofertas: {
        title: 'Ofertas de troca',
        content: `
            <div class="panel">
                <h2>Ofertas de troca</h2>
                <p>Visualize e gerencie as ofertas de troca de livros.</p>
            </div>
        `,
    },
    solicitacoes: {
        title: 'Solicitações',
        content: `
            <div class="panel">
                <h2>Solicitações de troca</h2>
                <p>Acompanhe suas solicitações de troca de livros.</p>
            </div>
        `,
    },
};


const pageTitle = document.getElementById('pageTitle');
const pageContent = document.getElementById('pageContent');
const navItems = document.querySelectorAll('.nav-item');

let livrosCadastrados = [];
let ofertasEnviadas = [];
let solicitacoesRecebidas = [];
let filtroLivroSolicitacao = null;
let modoListaLivros = 'todos';
let filtrosLivros = {
    titulo: '',
    autor: '',
    genero: '',
};
let versaoConteudoAtual = 0;

// cada nova tela recebe uma versao para impedir que respostas antigas sobrescrevam a navegacao atual.
function iniciarAtualizacaoDeTela() {
    versaoConteudoAtual += 1;
    return versaoConteudoAtual;
}

function atualizacaoDeTelaEstaAtual(versao) {
    return versao === versaoConteudoAtual;
}

if (!localStorage.getItem('token')) {
    sessionStorage.removeItem('isLoggedIn');
    window.location.href = 'index.html';
}


function renderSection(sectionKey, livroId = null) {
    const versao = iniciarAtualizacaoDeTela();
    const section = sections[sectionKey] || sections.home;
    pageTitle.textContent = section.title;
    pageContent.innerHTML = section.content;

    navItems.forEach((button) => {
        const isActive = button.dataset.section === sectionKey;
        button.classList.toggle('active', isActive);
    });

    if (sectionKey === 'livros') {
        carregarLivros(versao);
    } else if (sectionKey === 'ofertas') {
        carregarOfertas(versao);
    } else if (sectionKey === 'solicitacoes') {
        filtroLivroSolicitacao = livroId === null ? null : Number(livroId);
        carregarSolicitacoes(filtroLivroSolicitacao, versao);
    }
}

async function carregarUsuarioLogado() {
    const nomeElemento = document.getElementById('usuarioLogadoNome');
    const nomeSalvo = getUsuarioNome();
    if (nomeSalvo) {
        nomeElemento.textContent = nomeSalvo;
        return;
    }

    try {
        const usuario = await getUsuarioAtual();
        nomeElemento.textContent = usuario.nome;
        setUsuarioNome(usuario.nome);
        setUsuarioId(usuario.id);
    } catch (erro) {
        nomeElemento.textContent = 'Usuário indisponível';
        await mostrarErro(mensagemUsuarioDeErro(erro));
    }
}


async function carregarLivros(versaoRecebida = null) {
    const versao = versaoRecebida === null
        ? iniciarAtualizacaoDeTela()
        : versaoRecebida;
    pageContent.innerHTML = `
        <div class="panel">
            <h2>Livros cadastrados</h2>
            <p>Carregando livros...</p>
        </div>
    `;

    try {
        const livros = await getLivros();
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        livrosCadastrados = livros;
        mostrarListaAtual(versao);
    } catch (erro) {
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        await mostrarErro(mensagemUsuarioDeErro(erro));
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        pageContent.innerHTML = `
            <div class="panel">
                <h2>Erro</h2>
                <p>Não foi possível carregar os livros.</p>
                <button onclick="carregarLivros()">Tentar novamente</button>
            </div>
        `;
    }
}


function mostrarTodosLivros() {
    modoListaLivros = 'todos';
    mostrarListaAtual();
}


function mostrarMeusLivros() {
    modoListaLivros = 'meus';
    mostrarListaAtual();
}


function mostrarListaAtual(versaoRecebida = null) {
    const versao = versaoRecebida === null
        ? iniciarAtualizacaoDeTela()
        : versaoRecebida;
    if (!atualizacaoDeTelaEstaAtual(versao)) {
        return;
    }

    let listaLivros = livrosCadastrados;

    if (modoListaLivros === 'meus') {
        const usuarioId = getUsuarioId();
        listaLivros = livrosCadastrados.filter((livro) => {
            return livro.usuario && String(livro.usuario.id) === String(usuarioId);
        });
    }

    exibirLivros(listaLivros, versao);
}


function aplicarFiltros() {
    filtrosLivros.titulo = document.getElementById('filtroTitulo').value.trim();
    filtrosLivros.autor = document.getElementById('filtroAutor').value.trim();
    filtrosLivros.genero = document.getElementById('filtroGenero').value.trim();
    mostrarListaAtual();
}


function filtrarLista(listaLivros) {
    return listaLivros.filter((livro) => {
        const titulo = String(livro.titulo || '').toLowerCase();
        const autor = String(livro.autor || '').toLowerCase();
        const genero = String(livro.genero || '').toLowerCase();

        return titulo.includes(filtrosLivros.titulo.toLowerCase())
            && autor.includes(filtrosLivros.autor.toLowerCase())
            && genero.includes(filtrosLivros.genero.toLowerCase());
    });
}


function mostrarFormularioCadastro() {
    iniciarAtualizacaoDeTela();
    pageContent.innerHTML = `
        <div class="panel">
            <h2>Cadastrar livro</h2>
            <p id="mensagemFormulario" class="message" role="status" aria-live="polite"></p>

            <form id="formCadastroLivro" class="book-form">
                <label for="titulo">Título:</label>
                <input type="text" id="titulo" required>

                <label for="autor">Autor:</label>
                <input type="text" id="autor" required>

                <label for="genero">Gênero:</label>
                <input type="text" id="genero">

                <label for="descricao">Descrição:</label>
                <textarea id="descricao" maxlength="255"></textarea>
                <p id="contadorDescricao">0/255 caracteres</p>

                <label for="isbn">ISBN:</label>
                <input type="text" id="isbn">

                <label for="fotoCapa">Foto da capa (URL):</label>
                <input type="text" id="fotoCapa">

                <button type="submit">Cadastrar livro</button>
                <button type="button" onclick="mostrarListaAtual()">Cancelar</button>
            </form>
        </div>
    `;

    ligarContadorDescricao();

    const formCadastroLivro = document.getElementById('formCadastroLivro');

    formCadastroLivro.addEventListener('submit', async function(event) {
        event.preventDefault();
        const botaoCadastrar = formCadastroLivro.querySelector('button[type="submit"]');
        if (botaoCadastrar.disabled) {
            return;
        }
        botaoCadastrar.disabled = true;
        const versaoDoFormulario = versaoConteudoAtual;

        const dadosLivro = {
            usuarioId: getUsuarioId(),
            titulo: document.getElementById('titulo').value,
            autor: document.getElementById('autor').value,
            genero: document.getElementById('genero').value,
            descricao: document.getElementById('descricao').value,
            isbn: document.getElementById('isbn').value,
            fotoCapa: document.getElementById('fotoCapa').value,
        };

        try {
            await cadastrarLivro(dadosLivro);
            await mostrarSucesso('Livro cadastrado com sucesso.');
            if (atualizacaoDeTelaEstaAtual(versaoDoFormulario)) {
                await carregarLivros();
            }
        } catch (erro) {
            await mostrarErroFormulario(erro);
        } finally {
            botaoCadastrar.disabled = false;
        }
    });
}


async function mostrarDetalhesLivro(id) {
    const versao = iniciarAtualizacaoDeTela();
    pageContent.innerHTML = `
        <div class="panel">
            <h2>Detalhes do livro</h2>
            <p>Carregando...</p>
        </div>
    `;

    try {
        const livro = await getLivroPorId(id);
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        // as acoes variam pela propriedade: o dono gerencia o livro e ve solicitacoes; outros usuarios podem ofertar.
        const ehMeuLivro = livro.usuario
            && String(livro.usuario.id) === String(getUsuarioId());

        pageContent.innerHTML = `
            <div class="panel book-detail">
                <h2>${escaparHtml(livro.titulo || 'Título não informado')}</h2>
                <div class="book-detail-layout">
                    <div class="detail-cover-frame">
                        ${livro.fotoCapa
            ? `<img class="detail-cover" src="${escaparHtml(livro.fotoCapa)}" alt="Capa de ${escaparHtml(livro.titulo || 'livro')}">`
            : '<div class="cover-placeholder">Sem capa</div>'}
                    </div>
                    <div class="book-detail-info">
                        <p><strong>Autor:</strong> ${escaparHtml(livro.autor || 'Não informado')}</p>
                        <p><strong>Gênero:</strong> ${escaparHtml(livro.genero || 'Não informado')}</p>
                        <p><strong>ISBN:</strong> ${escaparHtml(livro.isbn || 'Não informado')}</p>
                        <p><strong>Data de cadastro:</strong> ${livro.dataCadastro ? new Date(livro.dataCadastro).toLocaleDateString('pt-BR') : 'Não informada'}</p>
                        <p><strong>Descrição:</strong> ${escaparHtml(livro.descricao || 'Sem descrição')}</p>
                    </div>
                </div>
                <div class="book-detail-actions">
                    ${ehMeuLivro
            ? `<button onclick="renderSection('solicitacoes', ${Number(livro.id)})">Ver solicitações deste livro</button>`
            : `<button onclick="mostrarFormularioOferta(${Number(livro.id)})">Fazer oferta por este livro</button>`}
                    ${ehMeuLivro ? `
                        <button class="button-secondary" onclick="mostrarFormularioEdicao(${Number(livro.id)})">Editar</button>
                        <button class="button-danger" onclick="confirmarExclusaoLivro(${Number(livro.id)})">Excluir</button>
                    ` : ''}
                    <button class="button-secondary" onclick="mostrarListaAtual()">Voltar para os livros</button>
                </div>
            </div>
        `;
    } catch (erro) {
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        await mostrarErro(mensagemUsuarioDeErro(erro));
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        pageContent.innerHTML = `
            <div class="panel">
                <h2>Erro</h2>
                <p>Não foi possível carregar os detalhes.</p>
                <button onclick="mostrarListaAtual()">Voltar para os livros</button>
            </div>
        `;
    }
}


async function mostrarFormularioEdicao(id) {
    const versao = iniciarAtualizacaoDeTela();
    try {
        const livro = await getLivroPorId(id);
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        const ehMeuLivro = livro.usuario
            && String(livro.usuario.id) === String(getUsuarioId());

        if (!ehMeuLivro) {
            await mostrarErro('Você só pode editar os seus próprios livros.');
            if (atualizacaoDeTelaEstaAtual(versao)) {
                mostrarListaAtual();
            }
            return;
        }

        pageContent.innerHTML = `
            <div class="panel">
                <h2>Editar livro</h2>
                <p id="mensagemFormulario" class="message" role="status" aria-live="polite"></p>
                <form id="formEdicaoLivro" class="book-form">
                    <label for="titulo">Título:</label>
                    <input type="text" id="titulo" value="${escaparHtml(livro.titulo)}" required>

                    <label for="autor">Autor:</label>
                    <input type="text" id="autor" value="${escaparHtml(livro.autor)}" required>

                    <label for="genero">Gênero:</label>
                    <input type="text" id="genero" value="${escaparHtml(livro.genero)}">

                    <label for="descricao">Descrição:</label>
                    <textarea id="descricao" maxlength="255">${escaparHtml(livro.descricao)}</textarea>
                    <p id="contadorDescricao">${(livro.descricao || '').length}/255 caracteres</p>

                    <label for="isbn">ISBN:</label>
                    <input type="text" id="isbn" value="${escaparHtml(livro.isbn)}">

                    <label for="fotoCapa">Foto da capa (URL):</label>
                    <input type="text" id="fotoCapa" value="${escaparHtml(livro.fotoCapa)}">

                    <button type="submit">Salvar alterações</button>
                    <button type="button" onclick="mostrarDetalhesLivro(${Number(livro.id)})">Cancelar</button>
                </form>
            </div>
        `;

        ligarContadorDescricao();

        document.getElementById('formEdicaoLivro').addEventListener('submit', async function(event) {
            event.preventDefault();
            const versaoDoFormulario = versaoConteudoAtual;

            const dadosLivro = {
                titulo: document.getElementById('titulo').value,
                autor: document.getElementById('autor').value,
                genero: document.getElementById('genero').value,
                descricao: document.getElementById('descricao').value,
                isbn: document.getElementById('isbn').value,
                fotoCapa: document.getElementById('fotoCapa').value,
            };

            try {
                await atualizarLivro(id, dadosLivro);
                await mostrarSucesso('Livro atualizado com sucesso.');
                if (atualizacaoDeTelaEstaAtual(versaoDoFormulario)) {
                    await carregarLivros();
                }
            } catch (erro) {
                await mostrarErroFormulario(erro);
            }
        });
    } catch (erro) {
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        await mostrarErro(mensagemUsuarioDeErro(erro));
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        pageContent.innerHTML = `
            <div class="panel">
                <h2>Erro</h2>
                <p>Não foi possível abrir o formulário.</p>
                <button onclick="mostrarListaAtual()">Voltar para os livros</button>
            </div>
        `;
    }
}


async function confirmarExclusaoLivro(id) {
    const versao = versaoConteudoAtual;
    if (!await confirmarAcao('Tem certeza de que deseja excluir este livro?', 'Excluir livro', 'Excluir')) {
        return;
    }

    try {
        await excluirLivro(id);
        await mostrarSucesso('Livro excluído com sucesso.');
        if (atualizacaoDeTelaEstaAtual(versao)) {
            await carregarLivros();
        }
    } catch (erro) {
        await mostrarErro(mensagemUsuarioDeErro(erro));
    }
}


function mostrarErroFormulario(erro) {
    return mostrarErro(mensagemUsuarioDeErro(erro));
}


function ligarContadorDescricao() {
    const descricao = document.getElementById('descricao');
    const contador = document.getElementById('contadorDescricao');

    contador.textContent = descricao.value.length + '/255 caracteres';

    descricao.addEventListener('input', function() {
        contador.textContent = descricao.value.length + '/255 caracteres';
    });
}


function exibirLivros(listaLivros, versao) {
    if (!atualizacaoDeTelaEstaAtual(versao)) {
        return;
    }

    const livrosFiltrados = filtrarLista(listaLivros);
    let conteudoLivros = `
        <div class="panel library-toolbar">
            <h2>Livros</h2>

            <div class="library-actions">
                <button class="${modoListaLivros === 'todos' ? 'is-selected' : 'button-secondary'}" onclick="mostrarTodosLivros()">Todos os livros</button>
                <button class="${modoListaLivros === 'meus' ? 'is-selected' : 'button-secondary'}" onclick="mostrarMeusLivros()">Meus livros</button>
                <button class="button-create" onclick="mostrarFormularioCadastro()">Cadastrar livro</button>
            </div>

            <form id="formFiltrosLivros">
                <div class="filter-field">
                    <label for="filtroTitulo">Filtrar por título:</label>
                    <input type="text" id="filtroTitulo" value="${escaparHtml(filtrosLivros.titulo)}">
                </div>
                <div class="filter-field">
                    <label for="filtroAutor">Filtrar por autor:</label>
                    <input type="text" id="filtroAutor" value="${escaparHtml(filtrosLivros.autor)}">
                </div>
                <div class="filter-field">
                    <label for="filtroGenero">Filtrar por gênero:</label>
                    <input type="text" id="filtroGenero" value="${escaparHtml(filtrosLivros.genero)}">
                </div>
                <div class="filter-actions">
                    <button type="submit">Filtrar</button>
                    <button type="button" class="button-secondary" onclick="limparFiltrosLivros()">Limpar filtros</button>
                </div>
            </form>
        </div>
    `;

    if (livrosFiltrados.length === 0) {
        conteudoLivros += `
            <div class="panel empty-state">
                <p>Nenhum livro encontrado.</p>
            </div>
        `;
        pageContent.innerHTML = conteudoLivros;
        ligarFormularioFiltros();
        return;
    }

    livrosFiltrados.forEach((livro) => {
        conteudoLivros += `
            <div class="panel book-card">
                <div class="book-cover">
                    ${livro.fotoCapa
            ? `<img src="${escaparHtml(livro.fotoCapa)}" alt="Capa de ${escaparHtml(livro.titulo || 'livro')}">`
            : '<div class="cover-placeholder" aria-label="Capa não informada">Sem capa</div>'}
                </div>
                <div class="book-info">
                    <p class="eyebrow">${escaparHtml(livro.genero || 'Livro disponível')}</p>
                    <h3>${escaparHtml(livro.titulo || 'Título não informado')}</h3>
                    <p><strong>Autor:</strong> ${escaparHtml(livro.autor || 'Não informado')}</p>
                    <p><strong>Descrição:</strong> ${escaparHtml(livro.descricao || 'Sem descrição')}</p>
                    <button onclick="mostrarDetalhesLivro(${Number(livro.id)})">Ver detalhes</button>
                </div>
            </div>
        `;
    });

    pageContent.innerHTML = conteudoLivros;
    ligarFormularioFiltros();
}


function ligarFormularioFiltros() {
    const formFiltros = document.getElementById('formFiltrosLivros');
    formFiltros.addEventListener('submit', function(event) {
        event.preventDefault();
        aplicarFiltros();
    });
}


function limparFiltrosLivros() {
    filtrosLivros = {
        titulo: '',
        autor: '',
        genero: '',
    };
    mostrarListaAtual();
}


async function carregarOfertas(versaoRecebida = null) {
    const versao = versaoRecebida === null
        ? iniciarAtualizacaoDeTela()
        : versaoRecebida;
    pageContent.innerHTML = '<div class="panel"><p>Carregando ofertas...</p></div>';

    try {
        const ofertas = await getOfertasEnviadas();
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        ofertasEnviadas = ofertas;
        exibirOfertas(versao);
    } catch (erro) {
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        await mostrarErro(mensagemUsuarioDeErro(erro));
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        pageContent.innerHTML = `
            <div class="panel">
                <p>Não foi possível carregar as ofertas.</p>
                <button onclick="carregarOfertas()">Tentar novamente</button>
            </div>
        `;
    }
}


function exibirOfertas(versaoRecebida = null) {
    const versao = versaoRecebida === null
        ? iniciarAtualizacaoDeTela()
        : versaoRecebida;
    if (!atualizacaoDeTelaEstaAtual(versao)) {
        return;
    }

    let conteudo = `
        <div class="panel">
            <h2>Ofertas enviadas</h2>
            <p>As ofertas são propostas de troca de livros, sem compra ou pagamento.</p>
            <button onclick="mostrarFormularioOferta()">Criar oferta</button>
        </div>
    `;

    if (ofertasEnviadas.length === 0) {
        conteudo += '<div class="panel empty-state"><p>Você ainda não enviou ofertas.</p></div>';
    }

    ofertasEnviadas.forEach((oferta) => {
        conteudo += `
            <div class="panel trade-card offer-card">
                <h3>Oferta ${Number(oferta.id)} <span class="status status-${escaparHtml(String(oferta.status || '').toLowerCase())}">${escaparHtml(oferta.status)}</span></h3>
                <p><strong>Livro desejado:</strong> ${escaparHtml(oferta.livroDesejadoTitulo)}</p>
                <p><strong>Proprietário:</strong> ${escaparHtml(oferta.proprietarioNome)}</p>
                ${oferta.motivoEncerramento ? `<p><strong>Motivo:</strong> ${escaparHtml(oferta.motivoEncerramento)}</p>` : ''}
                <p><strong>Livros oferecidos:</strong></p>
                <ul>
                    ${oferta.livrosOferecidos.map((livro) => `
                        <li>
                            ${escaparHtml(livro.titulo)} — ${escaparHtml(livro.status)}
                            ${oferta.status === 'PENDENTE' && livro.status === 'PENDENTE'
            ? `<button onclick="removerLivroDaOferta(${Number(oferta.id)}, ${Number(livro.id)})">Remover</button>`
            : ''}
                        </li>
                    `).join('')}
                </ul>
                ${oferta.status === 'PENDENTE' ? `
                    <button onclick="cancelarOfertaEnviada(${Number(oferta.id)})">Cancelar oferta</button>
                ` : ''}
            </div>
        `;
    });

    pageContent.innerHTML = conteudo;
}


async function mostrarFormularioOferta(livroDesejadoId = null) {
    const versao = iniciarAtualizacaoDeTela();
    pageContent.innerHTML = '<div class="panel"><p>Carregando livros...</p></div>';

    try {
        const livros = await getLivros();
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        livrosCadastrados = livros;
        // separa os livros proprios dos livros de terceiros e usa o id recebido para pre-selecionar o desejado.
        const usuarioId = getUsuarioId();
        const meusLivros = livrosCadastrados.filter((livro) => {
            return livro.usuario && String(livro.usuario.id) === String(usuarioId);
        });
        const livrosDeOutros = livrosCadastrados.filter((livro) => {
            return livro.usuario && String(livro.usuario.id) !== String(usuarioId);
        });

        pageContent.innerHTML = `
            <div class="panel">
                <h2>Criar oferta de troca</h2>
                <form id="formOferta">
                    <label for="livroDesejado">Livro que deseja receber:</label>
                    <select id="livroDesejado" required>
                        <option value="">Selecione um livro</option>
                        ${livrosDeOutros.map((livro) => `
                            <option value="${Number(livro.id)}" ${Number(livro.id) === Number(livroDesejadoId) ? 'selected' : ''}>
                                ${escaparHtml(livro.titulo)} — ${escaparHtml(livro.usuario.nome || 'Usuário')}
                            </option>
                        `).join('')}
                    </select>

                    <p>Selecione um ou mais livros seus para oferecer:</p>
                    ${meusLivros.length ? meusLivros.map((livro) => `
                        <label>
                            <input type="checkbox" name="livrosOferecidos" value="${Number(livro.id)}">
                            ${escaparHtml(livro.titulo)} — ${escaparHtml(livro.autor)}
                        </label>
                    `).join('') : '<p>Você precisa cadastrar um livro antes de criar uma oferta.</p>'}

                    <div id="resumoOferta"></div>
                    <button type="submit" ${meusLivros.length === 0 || livrosDeOutros.length === 0 ? 'disabled' : ''}>
                        Conferir e enviar oferta
                    </button>
                    <button type="button" onclick="exibirOfertas()">Cancelar</button>
                </form>
            </div>
        `;

        document.getElementById('formOferta').addEventListener('change', atualizarResumoOferta);
        document.getElementById('formOferta').addEventListener('submit', enviarOferta);
        atualizarResumoOferta();
    } catch (erro) {
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        await mostrarErro(mensagemUsuarioDeErro(erro));
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        pageContent.innerHTML = `
            <div class="panel">
                <p>Não foi possível carregar os livros.</p>
                <button onclick="carregarOfertas()">Voltar</button>
            </div>
        `;
    }
}


function atualizarResumoOferta() {
    const livroDesejado = document.getElementById('livroDesejado');
    const livrosSelecionados = document.querySelectorAll('input[name="livrosOferecidos"]:checked');
    const titulosOferecidos = [];
    livrosSelecionados.forEach((item) => {
        titulosOferecidos.push(item.parentElement.textContent.trim());
    });

    document.getElementById('resumoOferta').innerHTML = `
        <h3>Resumo da proposta</h3>
        <p><strong>Livro desejado:</strong> ${escaparHtml(livroDesejado.selectedOptions[0]?.textContent || 'Nenhum')}</p>
        <p><strong>Livros oferecidos:</strong> ${escaparHtml(titulosOferecidos.join(', ') || 'Nenhum')}</p>
    `;
}


async function enviarOferta(event) {
    event.preventDefault();
    const formOferta = document.getElementById('formOferta');
    const botaoEnviar = formOferta.querySelector('button[type="submit"]');
    if (botaoEnviar.disabled) {
        return;
    }
    botaoEnviar.disabled = true;
    const versaoDoFormulario = versaoConteudoAtual;

    try {
        const livroDesejadoId = Number(document.getElementById('livroDesejado').value);
        const livrosOferecidosIds = [];
        document.querySelectorAll('input[name="livrosOferecidos"]:checked').forEach((item) => {
            livrosOferecidosIds.push(Number(item.value));
        });

        if (!livroDesejadoId || livrosOferecidosIds.length === 0) {
            await mostrarErro('Selecione o livro desejado e pelo menos um livro para oferecer.');
            return;
        }

        const tituloDesejado = document.getElementById('livroDesejado').selectedOptions[0].textContent.trim();
        if (!await confirmarAcao(`Enviar oferta para ${tituloDesejado} com ${livrosOferecidosIds.length} livro(s)?`, 'Confirmar oferta', 'Enviar oferta')) {
            return;
        }

        await criarOferta({ livroDesejadoId, livrosOferecidosIds });
        await mostrarSucesso('Oferta enviada com sucesso.');
        if (atualizacaoDeTelaEstaAtual(versaoDoFormulario)) {
            await carregarOfertas();
        }
    } catch (erro) {
        await mostrarErro(mensagemUsuarioDeErro(erro));
    } finally {
        botaoEnviar.disabled = false;
    }
}


async function cancelarOfertaEnviada(id) {
    const versao = versaoConteudoAtual;
    const motivo = await solicitarMotivo('Informe por que deseja cancelar esta oferta.', 'Cancelar oferta');
    if (motivo === null) {
        return;
    }

    try {
        await cancelarOferta(id, motivo);
        await mostrarSucesso('Oferta cancelada.');
        if (atualizacaoDeTelaEstaAtual(versao)) {
            await carregarOfertas();
        }
    } catch (erro) {
        await mostrarErro(mensagemUsuarioDeErro(erro));
    }
}

async function removerLivroDaOferta(ofertaId, livroId) {
    const versao = versaoConteudoAtual;
    if (!await confirmarAcao('Deseja remover este livro da oferta pendente?', 'Remover livro', 'Remover')) {
        return;
    }
    try {
        await removerLivroOferta(ofertaId, livroId);
        await mostrarSucesso('Livro removido da oferta.');
        if (atualizacaoDeTelaEstaAtual(versao)) {
            await carregarOfertas();
        }
    } catch (erro) {
        await mostrarErro(mensagemUsuarioDeErro(erro));
    }
}


async function carregarSolicitacoes(livroId = filtroLivroSolicitacao, versaoRecebida = null) {
    const versao = versaoRecebida === null
        ? iniciarAtualizacaoDeTela()
        : versaoRecebida;
    pageContent.innerHTML = '<div class="panel"><p>Carregando solicitações...</p></div>';

    try {
        const solicitacoes = await getSolicitacoesRecebidas();
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        solicitacoesRecebidas = solicitacoes;
        filtroLivroSolicitacao = livroId;
        exibirSolicitacoes(versao);
    } catch (erro) {
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        await mostrarErro(mensagemUsuarioDeErro(erro));
        if (!atualizacaoDeTelaEstaAtual(versao)) {
            return;
        }
        pageContent.innerHTML = `
            <div class="panel">
                <p>Não foi possível carregar as solicitações.</p>
                <button onclick="carregarSolicitacoes()">Tentar novamente</button>
            </div>
        `;
    }
}


function exibirSolicitacoes(versao) {
    if (!atualizacaoDeTelaEstaAtual(versao)) {
        return;
    }

    const listaSolicitacoes = filtroLivroSolicitacao === null
        ? solicitacoesRecebidas
        : solicitacoesRecebidas.filter((oferta) => {
            return Number(oferta.livroDesejadoId) === Number(filtroLivroSolicitacao);
        });
    let conteudo = `
        <div class="panel">
            <h2>Solicitações recebidas</h2>
            ${filtroLivroSolicitacao !== null
        ? `<p>Filtradas pelo livro selecionado.</p><button class="button-secondary" onclick="renderSection('solicitacoes')">Ver todas as solicitações</button>`
        : ''}
        </div>
    `;

    if (listaSolicitacoes.length === 0) {
        conteudo += `<div class="panel empty-state"><p>${filtroLivroSolicitacao === null
            ? 'Você não possui solicitações recebidas.'
            : 'Não há solicitações relacionadas a este livro.'}</p></div>`;
    }

    listaSolicitacoes.forEach((oferta) => {
        const pendente = oferta.status === 'PENDENTE';
        conteudo += `
            <div class="panel trade-card request-card">
                <h3>Solicitação ${Number(oferta.id)} <span class="status status-${escaparHtml(String(oferta.status || '').toLowerCase())}">${escaparHtml(oferta.status)}</span></h3>
                <p><strong>Enviada por:</strong> ${escaparHtml(oferta.solicitanteNome)}</p>
                <p><strong>Livro desejado:</strong> ${escaparHtml(oferta.livroDesejadoTitulo)}</p>
                ${oferta.motivoEncerramento ? `<p><strong>Motivo:</strong> ${escaparHtml(oferta.motivoEncerramento)}</p>` : ''}
                <p><strong>Livros oferecidos:</strong></p>
                <ul>
                    ${oferta.livrosOferecidos.map((livro) => `
                        <li>
                            ${pendente && livro.status === 'PENDENTE' ? `
                                <label>
                                    <input type="checkbox" name="livroAceito-${Number(oferta.id)}" value="${Number(livro.id)}">
                                    ${escaparHtml(livro.titulo)} — ${escaparHtml(livro.autor)}
                                </label>
                            ` : `${escaparHtml(livro.titulo)} — ${escaparHtml(livro.status)}`}
                        </li>
                    `).join('')}
                </ul>
                ${pendente ? `
                    <button onclick="decidirSolicitacaoRecebida(${Number(oferta.id)}, 'ACEITA')">
                        Aceitar livros selecionados
                    </button>
                    <button onclick="decidirSolicitacaoRecebida(${Number(oferta.id)}, 'RECUSADA')">
                        Recusar todos
                    </button>
                ` : ''}
            </div>
        `;
    });

    pageContent.innerHTML = conteudo;
}


async function decidirSolicitacaoRecebida(id, status) {
    const versao = versaoConteudoAtual;
    // o aceite envia os itens escolhidos; a recusa coleta o motivo que sera mantido no historico.
    const livrosAceitosIds = [];
    if (status === 'ACEITA') {
        document.querySelectorAll(`input[name="livroAceito-${id}"]:checked`).forEach((item) => {
            livrosAceitosIds.push(Number(item.value));
        });
        if (livrosAceitosIds.length === 0) {
            await mostrarErro('Selecione pelo menos um livro ou escolha recusar todos.');
            return;
        }
    }

    let motivo = null;
    if (status === 'RECUSADA') {
        motivo = await solicitarMotivo('Informe o motivo da recusa desta solicitação.', 'Recusar solicitação');
        if (motivo === null) {
            return;
        }
    }

    const acao = status === 'ACEITA' ? 'aceitar os livros selecionados' : 'recusar toda a proposta';
    if (!await confirmarAcao(`Deseja ${acao}?`, 'Confirmar decisão')) {
        return;
    }

    try {
        await decidirSolicitacao(id, { status, livrosAceitosIds, motivo: motivo ? motivo.trim() : null });
        await mostrarSucesso('A solicitação foi atualizada com sucesso.');
        if (atualizacaoDeTelaEstaAtual(versao)) {
            await carregarSolicitacoes();
        }
    } catch (erro) {
        await mostrarErro(mensagemUsuarioDeErro(erro));
    }
}


// escapa texto antes de inseri-lo no html da tela, para que dados nao sejam interpretados como marcacao.
function escaparHtml(valor) {
    if (valor === null || valor === undefined) {
        return '';
    }

    return String(valor).replace(/[&<>"']/g, function(caractere) {
        const caracteresHtml = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;',
        };
        return caracteresHtml[caractere];
    });
}


function logoutUsuario() {
    sessionStorage.removeItem('isLoggedIn');
    localStorage.removeItem('token');
    localStorage.removeItem('usuarioId');
    localStorage.removeItem('usuarioNome');
    window.location.href = 'index.html';
}


renderSection('home');
carregarUsuarioLogado();
