function formatMoneda(valor) {
    return new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS' }).format(valor);
}

function formatFecha(iso) {
    if (!iso) {
        return '-';
    }
    const d = new Date(iso);
    if (isNaN(d.getTime())) {
        return '-';
    }
    return d.toLocaleString('es-AR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
    });
}

function formatFechaCorta(iso) {
    if (!iso) {
        return '-';
    }
    const d = new Date(iso);
    if (isNaN(d.getTime())) {
        return '-';
    }
    return d.toLocaleDateString('es-AR');
}

function localToIso(value) {
    if (!value) {
        return null;
    }
    const d = new Date(value);
    return isNaN(d.getTime()) ? null : d.toISOString();
}

function isoToLocal(iso) {
    if (!iso) {
        return '';
    }
    const d = new Date(iso);
    if (isNaN(d.getTime())) {
        return '';
    }
    const pad = (n) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

const ESTADOS = {
    EN_REPARACION: 'En reparación',
    FINALIZADA: 'Finalizada',
    CANCELADA: 'Cancelada',
};

function estadoBadge(estado) {
    const clases = {
        EN_REPARACION: 'bg-primary',
        FINALIZADA: 'bg-success',
        CANCELADA: 'bg-danger',
    };
    return `<span class="badge ${clases[estado] || 'bg-secondary'}">${ESTADOS[estado] || estado}</span>`;
}

function escapeHtml(valor) {
    return String(valor === null || valor === undefined ? '' : valor)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

function showToast(message, tipo = 'success') {
    const container = document.getElementById('toast-container');
    if (!container) {
        return;
    }
    const iconos = {
        success: 'bi-check-circle-fill text-success',
        danger: 'bi-x-circle-fill text-danger',
        warning: 'bi-exclamation-triangle-fill text-warning',
    };
    const toast = document.createElement('div');
    toast.className = 'toast align-items-center text-bg-light border-0';
    toast.setAttribute('role', 'alert');
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                <i class="bi ${iconos[tipo] || iconos.success} me-2"></i>${message}
            </div>
            <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Cerrar"></button>
        </div>`;
    container.appendChild(toast);
    const instancia = new bootstrap.Toast(toast, { delay: 4000 });
    instancia.show();
    toast.addEventListener('hidden.bs.toast', () => toast.remove());
}

function showApiError(error) {
    if (error && error.validationErrors && Object.keys(error.validationErrors).length) {
        const detalles = Object.entries(error.validationErrors)
            .map(([campo, mensaje]) => `${campo}: ${mensaje}`)
            .join('<br>');
        showToast(`${error.message}<br>${detalles}`, 'danger');
    } else {
        showToast((error && error.message) || 'Error en la solicitud', 'danger');
    }
}

function mostrarErrorTabla(xhr) {
    const mensaje = (xhr && xhr.responseJSON && xhr.responseJSON.message) || 'Error al cargar los datos';
    showToast(mensaje, 'danger');
}

function confirmarEliminar(titulo, mensaje, onConfirm) {
    const modal = document.getElementById('modal-confirmar');
    if (!modal) {
        onConfirm();
        return;
    }
    document.getElementById('modal-confirmar-titulo').textContent = titulo;
    document.getElementById('modal-confirmar-mensaje').textContent = mensaje;
    const boton = document.getElementById('modal-confirmar-boton');
    const instancia = bootstrap.Modal.getOrCreateInstance(modal);
    boton.onclick = () => {
        onConfirm();
        instancia.hide();
    };
    instancia.show();
}

function asegurarElementosComunes() {
    if (!document.getElementById('toast-container')) {
        const contenedor = document.createElement('div');
        contenedor.id = 'toast-container';
        contenedor.className = 'toast-container position-fixed top-0 end-0 p-3';
        contenedor.style.zIndex = '1090';
        document.body.appendChild(contenedor);
    }
    if (!document.getElementById('modal-confirmar')) {
        const div = document.createElement('div');
        div.innerHTML = `
            <div class="modal fade" id="modal-confirmar" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="modal-confirmar-titulo"></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                        </div>
                        <div class="modal-body" id="modal-confirmar-mensaje"></div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                            <button type="button" class="btn btn-danger" id="modal-confirmar-boton">Eliminar</button>
                        </div>
                    </div>
                </div>
            </div>`;
        document.body.appendChild(div);
    }
}

if (window.jQuery) {
    $.fn.dataTable.ext.errMode = 'none';
}

asegurarElementosComunes();
