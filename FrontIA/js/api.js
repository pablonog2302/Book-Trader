const API_BASE_URL = 'http://localhost:8080';

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

function getUsuarioId() {
    return localStorage.getItem('usuarioId');
}

function clearToken() {
    localStorage.removeItem('token');
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

async function request({ method = 'GET', endpoint, body = null, headers = {}, requireAuth = true }) {
    const token = getToken();

    if (requireAuth && !token) {
        if (typeof window !== 'undefined') {
            window.location.href = 'index.html';
        }
        throw new Error('Token de autenticação não encontrado. Faça login novamente.');
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
        const data = contentType.includes('application/json') || contentType.includes('+json')
            ? await response.json()
            : await response.text();

        if (response.status === 401) {
            clearToken();
            if (typeof window !== 'undefined') {
                window.location.href = 'index.html';
            }
            throw new Error('Sessão expirada ou não autorizada.');
        }

        if (response.status === 403) {
            throw new Error('Você não tem permissão para realizar esta ação.');
        }

        if (!response.ok) {
            throw new Error(
                typeof data === 'string'
                    ? data
                    : data.mensagem || data.message || data.detail || JSON.stringify(data, null, 2)
            );
        }

        return data;
    } catch (error) {
        console.error('Erro na requisição:', error);
        throw error;
    }
}

async function login(payload) {
    const responseData = await request({
        method: 'POST',
        endpoint: '/login',
        body: payload,
        requireAuth: false,
    });

    const token = extractTokenFromResponse(responseData);

    if (token) {
        setToken(token);
    }

    if (responseData.usuarioId) {
        setUsuarioId(responseData.usuarioId);
    }

    return responseData;
}

async function getUsuarios() {
    return request({
        method: 'GET',
        endpoint: '/usuarios',
    });
}

async function getLivros() {
    return request({
        method: 'GET',
        endpoint: '/livro',
    });
}

async function getLivroPorId(id) {
    return request({
        method: 'GET',
        endpoint: `/livro/${id}`,
    });
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
    return request({
        method: 'GET',
        endpoint: '/oferta',
    });
}

async function criarOferta(payload) {
    return request({
        method: 'POST',
        endpoint: '/oferta',
        body: payload,
    });
}

async function cancelarOferta(id) {
    return request({
        method: 'DELETE',
        endpoint: `/oferta/${id}`,
    });
}

async function getSolicitacoesRecebidas() {
    return request({
        method: 'GET',
        endpoint: '/solicitacao',
    });
}

async function decidirSolicitacao(id, payload) {
    return request({
        method: 'PATCH',
        endpoint: `/solicitacao/${id}/status`,
        body: payload,
    });
}
