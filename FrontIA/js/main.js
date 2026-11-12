const loginForm = document.getElementById('loginForm');
const loginInput = document.getElementById('cpf');
const senhaInput = document.getElementById('senha');
const feedback = document.getElementById('feedback');
const cadastroForm = document.getElementById('cadastroForm');
const cadastroFeedback = document.getElementById('cadastroFeedback');
const mostrarCadastroButton = document.getElementById('mostrarCadastro');
const voltarLoginButton = document.getElementById('voltarLogin');
//essa linha faz com que ao clicar em entrar o formulario execute a funcao loginUsuario().
loginForm.addEventListener('submit', loginUsuario);
cadastroForm.addEventListener('submit', cadastrarNovoUsuario);
mostrarCadastroButton.addEventListener('click', mostrarFormularioCadastro);
voltarLoginButton.addEventListener('click', mostrarFormularioLogin);

if (sessionStorage.getItem('isLoggedIn') === 'true' || localStorage.getItem('token')) {
    window.location.href = 'dashboard.html';
}


function exibirMensagem(mensagem, tipo = 'info') {
    feedback.textContent = mensagem;
    feedback.className = `feedback show ${tipo}`;
}

function mostrarFormularioCadastro() {
    loginForm.hidden = true;
    mostrarCadastroButton.hidden = true;
    cadastroForm.hidden = false;
    cadastroFeedback.textContent = '';
}

function mostrarFormularioLogin() {
    cadastroForm.hidden = true;
    loginForm.hidden = false;
    mostrarCadastroButton.hidden = false;
    feedback.textContent = '';
}

async function cadastrarNovoUsuario(event) {
    event.preventDefault();
    cadastroFeedback.textContent = '';

    const dadosUsuario = {
        nome: document.getElementById('novoNome').value.trim(),
        cpf: document.getElementById('novoCpf').value.trim(),
        dataNascimento: document.getElementById('novaDataNascimento').value.trim(),
        senha: document.getElementById('novaSenha').value,
    };

    try {
        const resposta = await cadastrarUsuario(dadosUsuario);
        cadastroFeedback.textContent = resposta.mensagem || 'Conta criada com sucesso. Faça login.';
        cadastroFeedback.className = 'message success';
        cadastroForm.reset();
        mostrarFormularioLogin();
        exibirMensagem('Conta criada com sucesso. Entre com seu CPF e senha.', 'success');
    } catch (error) {
        cadastroFeedback.textContent = error.message || 'Não foi possível criar a conta.';
        cadastroFeedback.className = 'message error';
    }
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