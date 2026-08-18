const API_BASE_URL = 'http://localhost:8080/api';

const SESSION_KEY = 'gestion_taller_sesion';

function getSesion() {
    try {
        return JSON.parse(localStorage.getItem(SESSION_KEY));
    } catch (e) {
        return null;
    }
}

function getToken() {
    const sesion = getSesion();
    return sesion && sesion.token ? sesion.token : null;
}

function setSesion(sesion) {
    localStorage.setItem(SESSION_KEY, JSON.stringify(sesion));
}

function clearSesion() {
    localStorage.removeItem(SESSION_KEY);
}

function irALogin() {
    clearSesion();
    const actual = window.location.pathname.split('/').pop();
    if (actual === 'login.html' || actual === 'register.html') {
        return;
    }
    window.location.href = 'login.html';
}

function cerrarSesion() {
    clearSesion();
    window.location.href = 'login.html';
}

function requiereAutenticacion() {
    if (!getToken()) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

async function apiRequest(path, options = {}) {
    const url = path.startsWith('http') ? path : API_BASE_URL + path;
    const esAutenticacion = path.includes('/auth/');
    const headers = { ...(options.headers || {}) };
    if (!(options.body instanceof FormData)) {
        headers['Content-Type'] = 'application/json';
    }
    const token = getToken();
    if (token && !esAutenticacion) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    const config = { ...options, headers };
    if (options.body && typeof options.body !== 'string' && !(options.body instanceof FormData)) {
        config.body = JSON.stringify(options.body);
    }
    const response = await fetch(url, config);
    if (response.status === 401 && !esAutenticacion) {
        irALogin();
    }
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

if (window.jQuery) {
    $.ajaxSetup({
        beforeSend: function (xhr) {
            const token = getToken();
            if (token) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + token);
            }
        },
    });
    $(document).ajaxError(function (event, xhr) {
        if (xhr.status === 401) {
            irALogin();
        }
    });
}