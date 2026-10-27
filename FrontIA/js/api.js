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
        const data = contentType.includes('application/json')
            ? await response.json()
            : await response.text();

        if (response.status === 401 || response.status === 403) {
            clearToken();
            if (typeof window !== 'undefined') {
                window.location.href = 'index.html';
            }
            throw new Error('Sessão expirada ou não autorizada.');
        }

        if (!response.ok) {
            throw new Error(
                typeof data === 'string'
                    ? data
                    : JSON.stringify(data, null, 2)
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

    return responseData;
}

async function getUsuarios() {
    return request({
        method: 'GET',
        endpoint: '/usuarios',
    });
}