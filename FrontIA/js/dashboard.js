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
    }
}


async function carregarLivros() {
    pageContent.innerHTML = `
        <div class="panel">
            <h2>Livros cadastrados</h2>
            <p>Carregando livros...</p>
        </div>
    `;

    try {
        livrosCadastrados = await getLivros();
        mostrarTodosLivros();

    } catch (erro) {
        console.error('Erro ao carregar livros:', erro);

        pageContent.innerHTML = `
            <div class="panel">
                <h2>Erro</h2>
                <p>Não foi possível carregar os livros. Tente novamente.</p>
            </div>
        `;
    }
}


function mostrarTodosLivros() {
    exibirLivros(livrosCadastrados);
}



function mostrarMeusLivros() {
    const usuarioId = getUsuarioId();

    const meusLivros = livrosCadastrados.filter((livro) => {
        return livro.usuario && livro.usuario.id == usuarioId;
    });

    exibirLivros(meusLivros);
}



function mostrarFormularioCadastro() {
    pageContent.innerHTML = `
        <div class="panel">
            <h2>Cadastrar livro</h2>

            <form id="formCadastroLivro">
                <label for="titulo">Título:</label>
                <input type="text" id="titulo" required>

                <label for="autor">Autor:</label>
                <input type="text" id="autor" required>

                <label for="descricao">Descrição:</label>
                <textarea id="descricao"></textarea>

                <label for="isbn">ISBN:</label>
                <input type="text" id="isbn">

                <label for="fotoCapa">Foto da capa (URL):</label>
                <input type="text" id="fotoCapa">

                <button type="submit">Cadastrar livro</button>
                <button type="button" onclick="carregarLivros()">Cancelar</button>
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
            descricao: document.getElementById('descricao').value,
            isbn: document.getElementById('isbn').value,
            fotoCapa: document.getElementById('fotoCapa').value
        };

        await cadastrarLivro(dadosLivro);

        alert('Livro cadastrado com sucesso!');

        carregarLivros();
    });
}


function exibirLivros(listaLivros) {
    let conteudoLivros = `
        <div class="panel">
            <h2>Livros</h2>

            <button onclick="mostrarTodosLivros()">
                Todos os livros
            </button>

            <button onclick="mostrarMeusLivros()">
                Meus livros
            </button>
            
            <button onclick="mostrarFormularioCadastro()">
            Cadastrar livro
            </button>
        
        </div>
    `;

    if (listaLivros.length === 0) {
        conteudoLivros += `
            <div class="panel">
                <p>Nenhum livro encontrado.</p>
            </div>
        `;

        pageContent.innerHTML = conteudoLivros;
        return;
    }

    listaLivros.forEach((livro) => {
        conteudoLivros += `
            <div class="panel">
                <h3>${livro.titulo || 'Título não informado'}</h3>

                ${livro.fotoCapa ? `
                    <img src="${livro.fotoCapa}" alt="Capa do livro" width="120">
                ` : ''}

                <p><strong>Autor:</strong> ${livro.autor || 'Não informado'}</p>
                <p><strong>ISBN:</strong> ${livro.isbn || 'Não informado'}</p>
                <p><strong>Descrição:</strong> ${livro.descricao || 'Sem descrição'}</p>
            </div>
        `;
    });

    pageContent.innerHTML = conteudoLivros;
}


function logoutUsuario() {
    sessionStorage.removeItem('isLoggedIn');
    localStorage.removeItem('token');
    localStorage.removeItem('usuarioId');
    window.location.href = 'index.html';
}


renderSection('home');
