const loginForm = document.getElementById('loginForm');
const loginInput = document.getElementById('cpf');
const senhaInput = document.getElementById('senha');
const feedback = document.getElementById('feedback');
//essa linha faz com que ao clicar em entrar o formulario execute a funcao loginUsuario().
loginForm.addEventListener('submit', loginUsuario);

if (sessionStorage.getItem('isLoggedIn') === 'true' || localStorage.getItem('token')) {
    window.location.href = 'dashboard.html';
}


function exibirMensagem(mensagem, tipo = 'info') {
    feedback.textContent = mensagem;
    feedback.className = `feedback show ${tipo}`;
}


async function loginUsuario(event) {
    event.preventDefault();
    const loginValue = loginInput.value.trim();
    const senha = senhaInput.value.trim();
    if (!loginValue || !senha) {
        exibirMensagem('Preencha login e senha para continuar.', 'error');
        return;
    }

    exibirMensagem('Entrando...', 'info');
    try {
        const data = await login({ login: loginValue, senha });

        const token = localStorage.getItem('token');

        if (!token) {
            exibirMensagem('Login realizado, mas o token não foi recebido pela API.', 'error');
            return;
        }

        sessionStorage.setItem('isLoggedIn', 'true');
        exibirMensagem(
            data?.mensagem || 'Bem vindo ao BookTrader!',
            'success'
        );

        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 500);

        loginForm.reset();
    } catch (error) {
        if (error instanceof TypeError) {
            exibirMensagem('Servidor indisponível. Tente novamente mais tarde.', 'error');
            return;
        }

        exibirMensagem(error.message || 'Não foi possível realizar o login.', 'error');
    }
}