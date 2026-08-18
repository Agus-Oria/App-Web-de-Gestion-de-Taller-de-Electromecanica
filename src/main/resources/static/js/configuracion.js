(function () {
    let modal;
    let inputNombre;

    function construirModal() {
        if (modal) {
            return;
        }
        const div = document.createElement('div');
        div.className = 'modal fade';
        div.id = 'modal-configuracion';
        div.tabIndex = -1;
        div.setAttribute('aria-hidden', 'true');
        div.innerHTML = `
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Configuración del taller</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="config-nombre-taller" class="form-label">Nombre del taller</label>
                            <input type="text" class="form-control" id="config-nombre-taller" maxlength="100" required>
                            <div class="invalid-feedback">El nombre es obligatorio (máx. 100 caracteres).</div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-primary" id="btn-guardar-configuracion">Guardar</button>
                    </div>
                </div>
            </div>`;
        document.body.appendChild(div);
        modal = bootstrap.Modal.getOrCreateInstance(div);
        inputNombre = div.querySelector('#config-nombre-taller');
        div.querySelector('#btn-guardar-configuracion').addEventListener('click', guardar);
        div.addEventListener('shown.bs.modal', () => inputNombre.focus());
    }

    async function abrir() {
        construirModal();
        try {
            const config = await apiRequest('/configuracion/taller');
            inputNombre.value = config.valor;
            inputNombre.classList.remove('is-invalid');
            modal.show();
        } catch (error) {
            showApiError(error);
        }
    }

    async function guardar() {
        const nombre = inputNombre.value.trim();
        if (!nombre) {
            inputNombre.classList.add('is-invalid');
            return;
        }
        try {
            await apiRequest('/configuracion/taller', { method: 'PUT', body: { valor: nombre } });
            modal.hide();
            showToast('Configuración actualizada correctamente');
        } catch (error) {
            showApiError(error);
        }
    }

    window.abrirConfiguracion = abrir;
})();