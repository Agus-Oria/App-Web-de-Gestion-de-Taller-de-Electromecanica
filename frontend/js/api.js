const API_BASE_URL = 'http://localhost:8080/api';

async function apiRequest(path, options = {}) {
    const url = path.startsWith('http') ? path : API_BASE_URL + path;
    const config = {
        headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
        ...options,
    };
    if (options.body && typeof options.body !== 'string') {
        config.body = JSON.stringify(options.body);
    }
    const response = await fetch(url, config);
    if (response.status === 204) {
        return null;
    }
    const data = await response.json().catch(() => null);
    if (!response.ok) {
        const error = new Error(data && data.message ? data.message : 'Error en la solicitud');
        error.status = response.status;
        error.validationErrors = data && data.validationErrors ? data.validationErrors : null;
        throw error;
    }
    return data;
}

async function fetchAll(path, pageSize = 100) {
    const results = [];
    let page = 0;
    let total = Infinity;
    while (results.length < total) {
        const separator = path.includes('?') ? '&' : '?';
        const data = await apiRequest(`${path}${separator}page=${page}&size=${pageSize}`);
        results.push(...data.content);
        total = data.totalElements;
        if (!data.content || data.content.length < pageSize) {
            break;
        }
        page++;
    }
    return results;
}
