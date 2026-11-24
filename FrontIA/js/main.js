const loginForm = document.getElementById('loginForm');
const loginInput = document.getElementById('cpf');
const senhaInput = document.getElementById('senha');
const feedback = document.getElementById('feedback');
const cadastroForm = document.getElementById('cadastroForm');
const mostrarCadastroButton = document.getElementById('mostrarCadastro');
const voltarLoginButton = document.getElementById('voltarLogin');

loginForm.addEventListener('submit', loginUsuario);
cadastroForm.addEventListener('submit', cadastrarNovoUsuario);
mostrarCadastroButton.addEventListener('click', mostrarFormularioCadastro);
voltarLoginButton.addEventListener('click', mostrarFormularioLogin);

configurarCampoNumerico(loginInput, document.getElementById('cpfAjuda'), 11, 'CPF');
configurarCampoNumerico(senhaInput, document.getElementById('senhaAjuda'), 8, 'Senha');
configurarCampoNumerico(
    document.getElementById('novoCpf'),
    document.getElementById('novoCpfAjuda'),
    11,
    'CPF');
configurarCampoNumerico(
    document.getElementById('novaSenha'),
    document.getElementById('novaSenhaAjuda'),
    8,
    'Senha');

if (sessionStorage.getItem('isLoggedIn') === 'true' || localStorage.getItem('token')) {
    window.location.href = 'dashboard.html';
}

function exibirMensagem(mensagem, tipo = 'info') {
    feedback.textContent = mensagem;
    feedback.className = `message ${tipo}`;
}

function atualizarAjudaCampo(elemento, mensagem, erro = false) {
    elemento.textContent = mensagem;
    elemento.classList.toggle('is-error', erro);
}

// combina digitacao, colagem e entrada para filtrar caracteres e aplicar o limite do campo.
function configurarCampoNumerico(input, ajuda, limite, nomeCampo) {
    const mensagemPadrao = nomeCampo === 'CPF'
        ? 'Use exatamente 11 números.'
        : 'Use somente números, com até 8 dígitos.';

    input.maxLength = limite;

    input.addEventListener('beforeinput', (event) => {
        if (!event.inputType || !event.inputType.startsWith('insert') || event.data === null) {
            return;
        }

        if (!/^[0-9]+$/.test(event.data)) {
            event.preventDefault();
            atualizarAjudaCampo(ajuda, `${nomeCampo} aceita somente números.`, true);
            return;
        }

        const inicio = input.selectionStart === null ? input.value.length : input.selectionStart;
        const fim = input.selectionEnd === null ? input.value.length : input.selectionEnd;
        const tamanhoRestante = input.value.length - (fim - inicio);
        if (tamanhoRestante + event.data.length > limite) {
            event.preventDefault();
            atualizarAjudaCampo(ajuda, `${nomeCampo} aceita no máximo ${limite} dígitos.`, true);
        }
    });

    input.addEventListener('paste', (event) => {
        if (!event.clipboardData) {
            return;
        }

        event.preventDefault();
        const textoColado = event.clipboardData.getData('text/plain')
            || event.clipboardData.getData('text');
        const numerosColados = textoColado.replace(/[^0-9]/g, '');
        const inicio = input.selectionStart === null ? input.value.length : input.selectionStart;
        const fim = input.selectionEnd === null ? input.value.length : input.selectionEnd;
        const prefixo = input.value.slice(0, inicio);
        const sufixo = input.value.slice(fim);
        const disponivel = Math.max(0, limite - prefixo.length - sufixo.length);
        const inseridos = numerosColados.slice(0, disponivel);

        input.value = `${prefixo}${inseridos}${sufixo}`;
        const posicaoCursor = prefixo.length + inseridos.length;
        input.setSelectionRange(posicaoCursor, posicaoCursor);
        input.dispatchEvent(new Event('input', { bubbles: true }));

        if (textoColado !== numerosColados) {
            atualizarAjudaCampo(ajuda, 'Caracteres inválidos foram removidos; use somente números.', true);
        } else if (inseridos.length < numerosColados.length) {
            atualizarAjudaCampo(ajuda, `${nomeCampo} aceita no máximo ${limite} dígitos.`, true);
        }
    });

    input.addEventListener('input', () => {
        const valorAtual = input.value;
        const valorNumerico = valorAtual.replace(/[^0-9]/g, '').slice(0, limite);
        if (valorAtual !== valorNumerico) {
            input.value = valorNumerico;
            atualizarAjudaCampo(ajuda, 'Use somente números.', true);
            return;
        }
        atualizarAjudaCampo(ajuda, mensagemPadrao);
    });
}

function validarCpf(cpf) {
    if (!/^[0-9]{11}$/.test(cpf)) {
        return 'O CPF deve conter exatamente 11 números, sem letras ou símbolos.';
    }
    return null;
}

function validarSenha(senha) {
    if (!senha) {
        return 'Informe sua senha.';
    }
    if (!/^[0-9]+$/.test(senha)) {
        return 'A senha deve conter somente números.';
    }
    if (senha.length > 8) {
        return 'A senha deve ter no máximo 8 dígitos.';
    }
    return null;
}

async function exibirErroDeCampo(mensagem, campo) {
    await mostrarErro(mensagem);
    campo.focus();
}

function mostrarFormularioCadastro() {
    loginForm.hidden = true;
    mostrarCadastroButton.hidden = true;
    cadastroForm.hidden = false;
    document.getElementById('login-heading').textContent = 'Crie sua conta';
    document.getElementById('page-intro').textContent = 'Cadastre seus dados para começar a trocar livros.';
    document.getElementById('novoNome').focus();
}

function mostrarFormularioLogin() {
    cadastroForm.hidden = true;
    loginForm.hidden = false;
    mostrarCadastroButton.hidden = false;
    feedback.textContent = '';
    document.getElementById('login-heading').textContent = 'Entrar no BookTrader';
    document.getElementById('page-intro').textContent = 'Informe seus dados para acessar sua comunidade de troca de livros.';
    loginInput.focus();
}

async function cadastrarNovoUsuario(event) {
    event.preventDefault();

    const dadosUsuario = {
        nome: document.getElementById('novoNome').value.trim(),
        cpf: document.getElementById('novoCpf').value,
        dataNascimento: document.getElementById('novaDataNascimento').value.trim(),
        senha: document.getElementById('novaSenha').value,
    };

    if (!dadosUsuario.nome || !dadosUsuario.dataNascimento) {
        await mostrarErro('Preencha seu nome e sua data de nascimento.');
        return;
    }

    const erroCpf = validarCpf(dadosUsuario.cpf);
    if (erroCpf) {
        await exibirErroDeCampo(erroCpf, document.getElementById('novoCpf'));
        return;
    }

    const erroSenha = validarSenha(dadosUsuario.senha);
    if (erroSenha) {
        await exibirErroDeCampo(erroSenha, document.getElementById('novaSenha'));
        return;
    }

    const botaoCadastrar = cadastroForm.querySelector('button[type="submit"]');
    if (botaoCadastrar.disabled) {
        return;
    }

    botaoCadastrar.disabled = true;
    try {
        await cadastrarUsuario(dadosUsuario);
        cadastroForm.reset();
        mostrarFormularioLogin();
        exibirMensagem('Conta criada com sucesso. Entre com seu CPF e senha.', 'success');
    } catch (error) {
        await mostrarErro(mensagemUsuarioDeErro(error));
    } finally {
        botaoCadastrar.disabled = false;
    }
}


async function loginUsuario(event) {
    event.preventDefault();
    const loginValue = loginInput.value;
    const senha = senhaInput.value;

    const erroCpf = validarCpf(loginValue);
    if (erroCpf) {
        await exibirErroDeCampo(erroCpf, loginInput);
        return;
    }

    const erroSenha = validarSenha(senha);
    if (erroSenha) {
        await exibirErroDeCampo(erroSenha, senhaInput);
        return;
    }

    exibirMensagem('Entrando...', 'info');
    try {
        await login({ login: loginValue, senha });

        const token = localStorage.getItem('token');

        if (!token) {
            await mostrarErro('Não foi possível iniciar sua sessão. Tente novamente.');
            return;
        }

        sessionStorage.setItem('isLoggedIn', 'true');
        exibirMensagem('Bem-vindo ao BookTrader!', 'success');

        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 500);

        loginForm.reset();
    } catch (error) {
        await mostrarErro(mensagemUsuarioDeErro(error));
    }
}