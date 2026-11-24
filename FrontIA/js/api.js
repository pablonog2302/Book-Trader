const API_BASE_URL = 'http://localhost:8080';

class ApiRequestError extends Error {
    constructor(mensagem, status = 0) {
        super(mensagem);
        this.name = 'ApiRequestError';
        this.status = status;
    }
}

function mensagemUsuarioDeErro(erro) {
    if (erro instanceof ApiRequestError) {
        return erro.message;
    }
    return 'Não foi possível concluir a operação. Tente novamente mais tarde.';
}

function getToken() {
    return localStorage.getItem('token');
}

function setToken(token) {
    if (!token) {
        return;
    }

    localStorage.setItem('token', token);
}

function setUsuarioId(usuarioId) {
    if (!usuarioId) {
        return;
    }

    localStorage.setItem('usuarioId', usuarioId);
}

function setUsuarioNome(nome) {
    if (!nome) {
        return;
    }

    localStorage.setItem('usuarioNome', nome);
}

function getUsuarioNome() {
    return localStorage.getItem('usuarioNome');
}

function getUsuarioId() {
    return localStorage.getItem('usuarioId');
}

function clearToken() {
    localStorage.removeItem('token');
    localStorage.removeItem('usuarioId');
    localStorage.removeItem('usuarioNome');
}

function getAuthHeaders(headers = {}) {
    const token = getToken();

    if (!token) {
        return headers;
    }

    return {
        ...headers,
        Authorization: `Bearer ${token}`,
    };
}

function extractTokenFromResponse(data) {
    if (!data || typeof data !== 'object') {
        return null;
    }

    if (data.token) return data.token;
    if (data.access_token) return data.access_token;
    if (data.data && typeof data.data === 'object') {
        if (data.data.token) return data.data.token;
        if (data.data.access_token) return data.data.access_token;
    }

    return null;
}

function mensagemPadraoHttp(status) {
    switch (status) {
        case 400:
            return 'Revise os dados informados e tente novamente.';
        case 401:
            return 'Sua sessão expirou ou os dados de acesso são inválidos. Faça login novamente.';
        case 403:
            return 'Você não tem permissão para realizar esta ação.';
        case 404:
            return 'O item solicitado não foi encontrado.';
        case 409:
            return 'A operação não pode ser concluída porque os dados estão em conflito.';
        default:
            return status >= 500
                ? 'Não foi possível concluir a operação. Tente novamente mais tarde.'
                : 'Não foi possível concluir a solicitação. Revise os dados e tente novamente.';
    }
}

// confirma os formatos usados pelo painel antes que eles sejam processados.
function validarObjetoApi(data, recurso) {
    if (!data || typeof data !== 'object' || Array.isArray(data)) {
        throw new ApiRequestError(`A resposta da API para ${recurso} está inválida.`);
    }

    return data;
}

function validarListaApi(data, recurso) {
    if (!Array.isArray(data)
        || data.some((item) => !item || typeof item !== 'object' || Array.isArray(item))) {
        throw new ApiRequestError(`A resposta da API para ${recurso} está inválida.`);
    }

    return data;
}

function validarListaDeOfertasApi(data, recurso) {
    const ofertas = validarListaApi(data, recurso);
    const ofertasInvalidas = ofertas.some((oferta) => {
        return !Array.isArray(oferta.livrosOferecidos)
            || oferta.livrosOferecidos.some((livro) => {
                return !livro || typeof livro !== 'object' || Array.isArray(livro);
            });
    });

    if (ofertasInvalidas) {
        throw new ApiRequestError(`A resposta da API para ${recurso} está inválida.`);
    }

    return ofertas;
}

// centraliza autenticacao, leitura das respostas e mensagens; em 401 tambem limpa a sessao.
async function request({ method = 'GET', endpoint, body = null, headers = {}, requireAuth = true }) {
    const token = getToken();

    if (requireAuth && !token) {
        if (typeof window !== 'undefined') {
            window.location.href = 'index.html';
        }
        throw new ApiRequestError('Sua sessão expirou. Faça login novamente.', 401);
    }

    const config = {
        method,
        headers: {
            'Content-Type': 'application/json',
            ...getAuthHeaders(headers),
        },
    };

    if (body !== null) {
        config.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
        const contentType = response.headers.get('content-type') || '';
        const responseText = await response.text();
        let data = responseText;
        if (contentType.includes('application/json') || contentType.includes('+json')) {
            try {
                data = responseText ? JSON.parse(responseText) : null;
            } catch {
                if (response.ok) {
                    throw new ApiRequestError(
                        'A API retornou uma resposta JSON inválida.',
                        response.status);
                }
                data = null;
            }
        }

        if (response.status === 401) {
            clearToken();
            if (requireAuth && typeof window !== 'undefined') {
                window.location.href = 'index.html';
            }
            const mensagem = data && typeof data.mensagem === 'string'
                ? data.mensagem
                : mensagemPadraoHttp(response.status);
            throw new ApiRequestError(mensagem, response.status);
        }

        if (!response.ok) {
            const mensagem = response.status < 500
            && data
            && typeof data.mensagem === 'string'
            && data.mensagem.trim()
                ? data.mensagem
                : mensagemPadraoHttp(response.status);
            throw new ApiRequestError(mensagem, response.status);
        }

        return data;
    } catch (error) {
        if (error instanceof ApiRequestError) {
            throw error;
        }
        throw new ApiRequestError(
            'Não foi possível conectar ao BookTrader. Verifique sua conexão e tente novamente.',
            0);
    }
}

// guarda o token e a identificacao retornados para autenticar chamadas e personalizar o painel.
async function login(payload) {
    const responseData = await request({
        method: 'POST',
        endpoint: '/login',
        body: payload,
        requireAuth: false,
    });

    // a resposta esperada segue o loginresponse atual; validar antes de salvar dados parciais da sessao.
    if (!responseData
        || typeof responseData !== 'object'
        || Array.isArray(responseData)
        || typeof responseData.usuarioId !== 'number'
        || typeof responseData.usuarioNome !== 'string'
        || !responseData.usuarioNome.trim()) {
        throw new ApiRequestError('A API retornou uma resposta inválida ao fazer login.');
    }

    const token = extractTokenFromResponse(responseData);

    if (typeof token !== 'string' || !token.trim()) {
        throw new ApiRequestError('A API não retornou um token de acesso válido.');
    }

    setToken(token);
    setUsuarioId(responseData.usuarioId);
    setUsuarioNome(responseData.usuarioNome);

    return responseData;
}

async function getUsuarioAtual() {
    const data = await request({
        method: 'GET',
        endpoint: '/usuarios/me',
    });
    const usuario = validarObjetoApi(data, 'usuário');
    if (typeof usuario.id !== 'number'
        || typeof usuario.nome !== 'string'
        || !usuario.nome.trim()) {
        throw new ApiRequestError('A resposta da API para usuário está inválida.');
    }

    return usuario;
}

async function getUsuarios() {
    return request({
        method: 'GET',
        endpoint: '/usuarios',
    });
}

async function getLivros() {
    const data = await request({
        method: 'GET',
        endpoint: '/livro',
    });
    return validarListaApi(data, 'livros');
}

async function getLivroPorId(id) {
    const data = await request({
        method: 'GET',
        endpoint: `/livro/${id}`,
    });
    return validarObjetoApi(data, 'livro');
}

async function cadastrarLivro(payload) {
    return request({
        method: 'POST',
        endpoint: '/livro',
        body: payload,
    });
}

async function atualizarLivro(id, payload) {
    return request({
        method: 'PUT',
        endpoint: `/livro/${id}`,
        body: payload,
    });
}

async function excluirLivro(id) {
    return request({
        method: 'DELETE',
        endpoint: `/livro/${id}`,
    });
}

async function cadastrarUsuario(payload) {
    return request({
        method: 'POST',
        endpoint: '/usuarios',
        body: payload,
        requireAuth: false,
    });
}

async function getOfertasEnviadas() {
    const data = await request({
        method: 'GET',
        endpoint: '/oferta',
    });
    return validarListaDeOfertasApi(data, 'ofertas');
}

async function criarOferta(payload) {
    return request({
        method: 'POST',
        endpoint: '/oferta',
        body: payload,
    });
}

async function cancelarOferta(id, motivo) {
    return request({
        method: 'DELETE',
        endpoint: `/oferta/${id}`,
        body: { motivo },
    });
}

async function removerLivroOferta(ofertaId, livroId) {
    return request({
        method: 'DELETE',
        endpoint: `/oferta/${ofertaId}/livros/${livroId}`,
    });
}

async function getSolicitacoesRecebidas() {
    const data = await request({
        method: 'GET',
        endpoint: '/solicitacao',
    });
    return validarListaDeOfertasApi(data, 'solicitações');
}

async function decidirSolicitacao(id, payload) {
    return request({
        method: 'PATCH',
        endpoint: `/solicitacao/${id}/status`,
        body: payload,
    });
}
