if (getToken()) {
    window.location.href = 'index.html';
}

async function iniciarSesion(event) {
    event.preventDefault();
    const form = document.getElementById('form-login');
    form.classList.add('was-validated');
    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value;
    if (!username || !password) {
        return;
    }
    const boton = document.getElementById('btn-login');
    boton.disabled = true;
    try {
        const sesion = await apiRequest('/auth/login', {
            method: 'POST',
            body: { username, password },
        });
        setSesion(sesion);
        window.location.href = 'index.html';
    } catch (error) {
        mostrarErrorAuth(error);
    } finally {
        boton.disabled = false;
    }
}

async function registrarUsuario(event) {
    event.preventDefault();
    const form = document.getElementById('form-registro');
    form.classList.add('was-validated');
    const username = document.getElementById('registro-username').value.trim();
    const password = document.getElementById('registro-password').value;
    const confirmacion = document.getElementById('registro-confirmar').value;
    const errorConfirmacion = document.getElementById('error-confirmacion');
    if (password !== confirmacion) {
        errorConfirmacion.textContent = 'Las contrasenas no coinciden';
        document.getElementById('registro-confirmar').setCustomValidity('invalid');
        errorConfirmacion.style.display = 'block';
        return;
    }
    errorConfirmacion.style.display = 'none';
    document.getElementById('registro-confirmar').setCustomValidity('');
    const boton = document.getElementById('btn-registro');
    boton.disabled = true;
    try {
        const sesion = await apiRequest('/auth/registro', {
            method: 'POST',
            body: { username, password },
        });
        setSesion(sesion);
        window.location.href = 'index.html';
    } catch (error) {
        mostrarErrorAuth(error);
    } finally {
        boton.disabled = false;
    }
}

function mostrarErrorAuth(error) {
    showToast((error && error.message) || 'Error en la solicitud', 'danger');
}

const formLogin = document.getElementById('form-login');
if (formLogin) {
    formLogin.addEventListener('submit', iniciarSesion);
}

const formRegistro = document.getElementById('form-registro');
if (formRegistro) {
    formRegistro.addEventListener('submit', registrarUsuario);
}