const sections = {
    home: {
        title: 'Início',
        content: `
            <div class="panel">
                <h2>Bem-vindo ao BookTrader!</h2>
                <p>
                    Encontre livros, publique ofertas e acompanhe suas solicitações de troca.
                </p>
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
let modoListaLivros = 'todos';
let filtrosLivros = {
    titulo: '',
    autor: '',
    genero: '',
};
let mensagemLista = '';
let tipoMensagemLista = 'success';


if (!localStorage.getItem('token')) {
    sessionStorage.removeItem('isLoggedIn');
    window.location.href = 'index.html';
}


function renderSection(sectionKey) {
    const section = sections[sectionKey] || sections.home;
    pageTitle.textContent = section.title;
    pageContent.innerHTML = section.content;

    navItems.forEach((button) => {
        const isActive = button.dataset.section === sectionKey;
        button.classList.toggle('active', isActive);
    });

    if (sectionKey === 'livros') {
        carregarLivros();
    } else if (sectionKey === 'ofertas') {
        carregarOfertas();
    } else if (sectionKey === 'solicitacoes') {
        carregarSolicitacoes();
    }
}


async function carregarLivros(mensagem = '', tipoMensagem = 'success') {
    pageContent.innerHTML = `
        <div class="panel">
            <h2>Livros cadastrados</h2>
            <p>Carregando livros...</p>
        </div>
    `;

    try {
        livrosCadastrados = await getLivros();
        mensagemLista = mensagem;
        tipoMensagemLista = tipoMensagem;
        mostrarListaAtual();
    } catch (erro) {
        console.error('Erro ao carregar livros:', erro);
        pageContent.innerHTML = `
            <div class="panel">
                <h2>Erro</h2>
                <p>Não foi possível carregar os livros. ${escaparHtml(erro.message)}</p>
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


function mostrarListaAtual() {
    let listaLivros = livrosCadastrados;

    if (modoListaLivros === 'meus') {
        const usuarioId = getUsuarioId();
        listaLivros = livrosCadastrados.filter((livro) => {
            return livro.usuario && String(livro.usuario.id) === String(usuarioId);
        });
    }

    exibirLivros(listaLivros);
}


function aplicarFiltros() {
    filtrosLivros.titulo = document.getElementById('filtroTitulo').value.trim();
    filtrosLivros.autor = document.getElementById('filtroAutor').value.trim();
    filtrosLivros.genero = document.getElementById('filtroGenero').value.trim();
    mensagemLista = '';
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
    pageContent.innerHTML = `
        <div class="panel">
            <h2>Cadastrar livro</h2>
            <p id="mensagemFormulario" class="message" role="status" aria-live="polite"></p>

            <form id="formCadastroLivro">
                <label for="titulo">Título:</label>
                <input type="text" id="titulo" required>

                <label for="autor">Autor:</label>
                <input type="text" id="autor" required>

                <label for="genero">Gênero:</label>
                <input type="text" id="genero">

                <label for="descricao">Descrição:</label>
                <textarea id="descricao"></textarea>

                <label for="isbn">ISBN:</label>
                <input type="text" id="isbn">

                <label for="fotoCapa">Foto da capa (URL):</label>
                <input type="text" id="fotoCapa">

                <button type="submit">Cadastrar livro</button>
                <button type="button" onclick="mostrarListaAtual()">Cancelar</button>
            </form>
        </div>
    `;

    const formCadastroLivro = document.getElementById('formCadastroLivro');

    formCadastroLivro.addEventListener('submit', async function(event) {
        event.preventDefault();

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
            await carregarLivros('Livro cadastrado com sucesso!');
        } catch (erro) {
            mostrarErroFormulario(erro);
        }
    });
}


async function mostrarDetalhesLivro(id) {
    pageContent.innerHTML = `
        <div class="panel">
            <h2>Detalhes do livro</h2>
            <p>Carregando...</p>
        </div>
    `;

    try {
        const livro = await getLivroPorId(id);
        const ehMeuLivro = livro.usuario
            && String(livro.usuario.id) === String(getUsuarioId());

        pageContent.innerHTML = `
            <div class="panel">
                <h2>${escaparHtml(livro.titulo || 'Título não informado')}</h2>
                ${livro.fotoCapa ? `<img src="${escaparHtml(livro.fotoCapa)}" alt="Capa do livro" width="120">` : ''}
                <p><strong>Autor:</strong> ${escaparHtml(livro.autor || 'Não informado')}</p>
                <p><strong>Gênero:</strong> ${escaparHtml(livro.genero || 'Não informado')}</p>
                <p><strong>ISBN:</strong> ${escaparHtml(livro.isbn || 'Não informado')}</p>
                <p><strong>Data de publicação:</strong> ${escaparHtml(livro.dataPublicada || 'Não informada')}</p>
                <p><strong>Descrição:</strong> ${escaparHtml(livro.descricao || 'Sem descrição')}</p>
                ${ehMeuLivro ? `
                    <button onclick="mostrarFormularioEdicao(${Number(livro.id)})">Editar</button>
                    <button onclick="confirmarExclusaoLivro(${Number(livro.id)})">Excluir</button>
                ` : ''}
                <button onclick="mostrarListaAtual()">Voltar para os livros</button>
            </div>
        `;
    } catch (erro) {
        console.error('Erro ao carregar detalhes do livro:', erro);
        pageContent.innerHTML = `
            <div class="panel">
                <h2>Erro</h2>
                <p>Não foi possível carregar os detalhes. ${escaparHtml(erro.message)}</p>
                <button onclick="mostrarListaAtual()">Voltar para os livros</button>
            </div>
        `;
    }
}


async function mostrarFormularioEdicao(id) {
    try {
        const livro = await getLivroPorId(id);
        const ehMeuLivro = livro.usuario
            && String(livro.usuario.id) === String(getUsuarioId());

        if (!ehMeuLivro) {
            alert('Você só pode editar os seus próprios livros.');
            mostrarListaAtual();
            return;
        }

        pageContent.innerHTML = `
            <div class="panel">
                <h2>Editar livro</h2>
                <p id="mensagemFormulario" class="message" role="status" aria-live="polite"></p>
                <form id="formEdicaoLivro">
                    <label for="titulo">Título:</label>
                    <input type="text" id="titulo" value="${escaparHtml(livro.titulo)}" required>

                    <label for="autor">Autor:</label>
                    <input type="text" id="autor" value="${escaparHtml(livro.autor)}" required>

                    <label for="genero">Gênero:</label>
                    <input type="text" id="genero" value="${escaparHtml(livro.genero)}">

                    <label for="descricao">Descrição:</label>
                    <textarea id="descricao">${escaparHtml(livro.descricao)}</textarea>

                    <label for="isbn">ISBN:</label>
                    <input type="text" id="isbn" value="${escaparHtml(livro.isbn)}">

                    <label for="fotoCapa">Foto da capa (URL):</label>
                    <input type="text" id="fotoCapa" value="${escaparHtml(livro.fotoCapa)}">

                    <button type="submit">Salvar alterações</button>
                    <button type="button" onclick="mostrarDetalhesLivro(${Number(livro.id)})">Cancelar</button>
                </form>
            </div>
        `;

        document.getElementById('formEdicaoLivro').addEventListener('submit', async function(event) {
            event.preventDefault();

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
                await carregarLivros('Livro atualizado com sucesso!');
            } catch (erro) {
                mostrarErroFormulario(erro);
            }
        });
    } catch (erro) {
        console.error('Erro ao preparar a edição do livro:', erro);
        pageContent.innerHTML = `
            <div class="panel">
                <h2>Erro</h2>
                <p>Não foi possível abrir o formulário. ${escaparHtml(erro.message)}</p>
                <button onclick="mostrarListaAtual()">Voltar para os livros</button>
            </div>
        `;
    }
}


async function confirmarExclusaoLivro(id) {
    if (!window.confirm('Tem certeza de que deseja excluir este livro?')) {
        return;
    }

    try {
        await excluirLivro(id);
        await carregarLivros('Livro excluído com sucesso!');
    } catch (erro) {
        console.error('Erro ao excluir livro:', erro);
        alert(`Não foi possível excluir o livro: ${erro.message}`);
    }
}


function mostrarErroFormulario(erro) {
    const mensagem = document.getElementById('mensagemFormulario');
    mensagem.textContent = `Não foi possível salvar o livro: ${erro.message}`;
    mensagem.className = 'message error';
}


function exibirLivros(listaLivros) {
    const livrosFiltrados = filtrarLista(listaLivros);
    let conteudoLivros = `
        <div class="panel">
            <h2>Livros</h2>

            <button onclick="mostrarTodosLivros()">Todos os livros</button>
            <button onclick="mostrarMeusLivros()">Meus livros</button>
            <button onclick="mostrarFormularioCadastro()">Cadastrar livro</button>

            <form id="formFiltrosLivros">
                <label for="filtroTitulo">Filtrar por título:</label>
                <input type="text" id="filtroTitulo" value="${escaparHtml(filtrosLivros.titulo)}">

                <label for="filtroAutor">Filtrar por autor:</label>
                <input type="text" id="filtroAutor" value="${escaparHtml(filtrosLivros.autor)}">

                <label for="filtroGenero">Filtrar por gênero:</label>
                <input type="text" id="filtroGenero" value="${escaparHtml(filtrosLivros.genero)}">

                <button type="submit">Filtrar</button>
                <button type="button" onclick="limparFiltrosLivros()">Limpar filtros</button>
            </form>
            ${mensagemLista ? `<p class="message ${tipoMensagemLista}">${escaparHtml(mensagemLista)}</p>` : ''}
        </div>
    `;

    if (livrosFiltrados.length === 0) {
        conteudoLivros += `
            <div class="panel">
                <p>Nenhum livro encontrado.</p>
            </div>
        `;
        pageContent.innerHTML = conteudoLivros;
        ligarFormularioFiltros();
        return;
    }

    livrosFiltrados.forEach((livro) => {
        conteudoLivros += `
            <div class="panel">
                <h3>${escaparHtml(livro.titulo || 'Título não informado')}</h3>
                ${livro.fotoCapa ? `
                    <img src="${escaparHtml(livro.fotoCapa)}" alt="Capa do livro" width="120">
                ` : ''}
                <p><strong>Autor:</strong> ${escaparHtml(livro.autor || 'Não informado')}</p>
                <p><strong>Gênero:</strong> ${escaparHtml(livro.genero || 'Não informado')}</p>
                <p><strong>ISBN:</strong> ${escaparHtml(livro.isbn || 'Não informado')}</p>
                <p><strong>Descrição:</strong> ${escaparHtml(livro.descricao || 'Sem descrição')}</p>
                <button onclick="mostrarDetalhesLivro(${Number(livro.id)})">Ver detalhes</button>
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
    mensagemLista = '';
    mostrarListaAtual();
}


async function carregarOfertas(mensagem = '') {
    pageContent.innerHTML = '<div class="panel"><p>Carregando ofertas...</p></div>';

    try {
        ofertasEnviadas = await getOfertasEnviadas();
        exibirOfertas(mensagem);
    } catch (erro) {
        console.error('Erro ao carregar ofertas:', erro);
        pageContent.innerHTML = `
            <div class="panel">
                <p>Não foi possível carregar as ofertas. ${escaparHtml(erro.message)}</p>
                <button onclick="carregarOfertas()">Tentar novamente</button>
            </div>
        `;
    }
}


function exibirOfertas(mensagem = '') {
    let conteudo = `
        <div class="panel">
            <h2>Ofertas enviadas</h2>
            <p>As ofertas são propostas de troca de livros, sem compra ou pagamento.</p>
            ${mensagem ? `<p class="message success">${escaparHtml(mensagem)}</p>` : ''}
            <button onclick="mostrarFormularioOferta()">Criar oferta</button>
        </div>
    `;

    if (ofertasEnviadas.length === 0) {
        conteudo += '<div class="panel"><p>Você ainda não enviou ofertas.</p></div>';
    }

    ofertasEnviadas.forEach((oferta) => {
        conteudo += `
            <div class="panel">
                <h3>Oferta ${Number(oferta.id)} — ${escaparHtml(oferta.status)}</h3>
                <p><strong>Livro desejado:</strong> ${escaparHtml(oferta.livroDesejadoTitulo)}</p>
                <p><strong>Proprietário:</strong> ${escaparHtml(oferta.proprietarioNome)}</p>
                <p><strong>Livros oferecidos:</strong></p>
                <ul>
                    ${oferta.livrosOferecidos.map((livro) => `
                        <li>${escaparHtml(livro.titulo)} — ${escaparHtml(livro.status)}</li>
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


async function mostrarFormularioOferta() {
    pageContent.innerHTML = '<div class="panel"><p>Carregando livros...</p></div>';

    try {
        livrosCadastrados = await getLivros();
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
                <p id="mensagemOferta" class="message" role="status" aria-live="polite"></p>
                <form id="formOferta">
                    <label for="livroDesejado">Livro que deseja receber:</label>
                    <select id="livroDesejado" required>
                        <option value="">Selecione um livro</option>
                        ${livrosDeOutros.map((livro) => `
                            <option value="${Number(livro.id)}">
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
    } catch (erro) {
        console.error('Erro ao preparar a oferta:', erro);
        pageContent.innerHTML = `
            <div class="panel">
                <p>Não foi possível carregar os livros. ${escaparHtml(erro.message)}</p>
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
    const livroDesejadoId = Number(document.getElementById('livroDesejado').value);
    const livrosOferecidosIds = [];
    document.querySelectorAll('input[name="livrosOferecidos"]:checked').forEach((item) => {
        livrosOferecidosIds.push(Number(item.value));
    });

    if (!livroDesejadoId || livrosOferecidosIds.length === 0) {
        mostrarMensagemOferta('Selecione o livro desejado e pelo menos um livro para oferecer.', 'error');
        return;
    }

    const tituloDesejado = document.getElementById('livroDesejado').selectedOptions[0].textContent.trim();
    if (!window.confirm(`Enviar oferta para ${tituloDesejado} com ${livrosOferecidosIds.length} livro(s)?`)) {
        return;
    }

    try {
        await criarOferta({ livroDesejadoId, livrosOferecidosIds });
        await carregarOfertas('Oferta enviada com sucesso.');
    } catch (erro) {
        mostrarMensagemOferta(`Não foi possível enviar a oferta: ${erro.message}`, 'error');
    }
}


async function cancelarOfertaEnviada(id) {
    if (!window.confirm('Tem certeza de que deseja cancelar esta oferta?')) {
        return;
    }

    try {
        await cancelarOferta(id);
        await carregarOfertas('Oferta cancelada.');
    } catch (erro) {
        alert(`Não foi possível cancelar a oferta: ${erro.message}`);
    }
}


async function carregarSolicitacoes(mensagem = '') {
    pageContent.innerHTML = '<div class="panel"><p>Carregando solicitações...</p></div>';

    try {
        solicitacoesRecebidas = await getSolicitacoesRecebidas();
        exibirSolicitacoes(mensagem);
    } catch (erro) {
        console.error('Erro ao carregar solicitações:', erro);
        pageContent.innerHTML = `
            <div class="panel">
                <p>Não foi possível carregar as solicitações. ${escaparHtml(erro.message)}</p>
                <button onclick="carregarSolicitacoes()">Tentar novamente</button>
            </div>
        `;
    }
}


function exibirSolicitacoes(mensagem = '') {
    let conteudo = `
        <div class="panel">
            <h2>Solicitações recebidas</h2>
            ${mensagem ? `<p class="message success">${escaparHtml(mensagem)}</p>` : ''}
        </div>
    `;

    if (solicitacoesRecebidas.length === 0) {
        conteudo += '<div class="panel"><p>Você não possui solicitações recebidas.</p></div>';
    }

    solicitacoesRecebidas.forEach((oferta) => {
        const pendente = oferta.status === 'PENDENTE';
        conteudo += `
            <div class="panel">
                <h3>Solicitação ${Number(oferta.id)} — ${escaparHtml(oferta.status)}</h3>
                <p><strong>Enviada por:</strong> ${escaparHtml(oferta.solicitanteNome)}</p>
                <p><strong>Livro desejado:</strong> ${escaparHtml(oferta.livroDesejadoTitulo)}</p>
                <p><strong>Livros oferecidos:</strong></p>
                <ul>
                    ${oferta.livrosOferecidos.map((livro) => `
                        <li>
                            ${pendente ? `
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
    const livrosAceitosIds = [];
    if (status === 'ACEITA') {
        document.querySelectorAll(`input[name="livroAceito-${id}"]:checked`).forEach((item) => {
            livrosAceitosIds.push(Number(item.value));
        });
        if (livrosAceitosIds.length === 0) {
            alert('Selecione pelo menos um livro ou escolha recusar todos.');
            return;
        }
    }

    const acao = status === 'ACEITA' ? 'aceitar os livros selecionados' : 'recusar toda a proposta';
    if (!window.confirm(`Deseja ${acao}?`)) {
        return;
    }

    try {
        await decidirSolicitacao(id, { status, livrosAceitosIds });
        await carregarSolicitacoes('A solicitação foi atualizada com sucesso.');
    } catch (erro) {
        alert(`Não foi possível atualizar a solicitação: ${erro.message}`);
    }
}


function mostrarMensagemOferta(mensagem, tipo) {
    const elemento = document.getElementById('mensagemOferta');
    elemento.textContent = mensagem;
    elemento.className = `message ${tipo}`;
}


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
    window.location.href = 'index.html';
}


renderSection('home');
