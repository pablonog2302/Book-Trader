const formularioLogin = document.querySelector('#login-form');
const mensagem = document.querySelector('#message');
const botaoEntrar = document.querySelector('#submit-button');

async function enviarFormulario(evento) {
    evento.preventDefault();

    const dadosFormulario = new FormData(formularioLogin);
    const login = dadosFormulario.get('login').trim();
    const senha = dadosFormulario.get('senha');

    mensagem.className = 'message';
    mensagem.textContent = 'Carregando...';
    botaoEntrar.disabled = true;

    try {
        const resposta = await window.api.fazerLogin(login, senha);
        mensagem.className = 'message success';
        mensagem.textContent = resposta.mensagem;
    } catch (erro) {
        mensagem.className = 'message error';
        mensagem.textContent = erro.message || 'Não foi possível conectar à API. Tente novamente.';
    } finally {
        botaoEntrar.disabled = false;
    }
}