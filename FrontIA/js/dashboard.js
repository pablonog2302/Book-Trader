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
                <h2>Meus livros</h2>
                <p>Gerencie os livros cadastrados na plataforma.</p>
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
}


function logoutUsuario() {
    sessionStorage.removeItem('isLoggedIn');
    localStorage.removeItem('token');
    window.location.href = 'index.html';
}


renderSection('home');
