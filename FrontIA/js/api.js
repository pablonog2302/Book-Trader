const API_URL = 'http://localhost:8080/login';

async function fazerLogin(login, senha) {
    const resposta = await fetch(API_URL, {
        method: 'POST',
        headers: {
            accept: '*/*',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ login, senha })
    });

    if (!resposta.ok) {
        let mensagemErro = `Não foi possível entrar (${resposta.status}).`;

        try {
            const dadosErro = await resposta.json();
            if (dadosErro.mensagem) {
                mensagemErro = dadosErro.mensagem;
            }
        } catch {
            if (resposta.statusText) {
                mensagemErro = `${mensagemErro} ${resposta.statusText}`;
            }
        }

        throw new Error(mensagemErro);
    }

    return resposta.json();
}

window.api = { fazerLogin };