$(function () {
    let tabla;
    let editandoId = null;

    const modalCliente = document.getElementById('modal-cliente');
    const formCliente = document.getElementById('form-cliente');
    const tituloModal = document.getElementById('modal-cliente-titulo');

    function abrirModalCliente() {
        formCliente.classList.remove('was-validated');
        bootstrap.Modal.getOrCreateInstance(modalCliente).show();
    }

    function cerrarModalCliente() {
        bootstrap.Modal.getOrCreateInstance(modalCliente).hide();
        formCliente.reset();
    }

    function abrirNuevoCliente() {
        editandoId = null;
        tituloModal.textContent = 'Nuevo cliente';
        formCliente.reset();
        abrirModalCliente();
    }

    async function abrirEdicionCliente(id) {
        try {
            const cliente = await apiRequest(`/clientes/${id}`);
            editandoId = cliente.id;
            tituloModal.textContent = 'Editar cliente';
            document.getElementById('cliente-nombre').value = cliente.nombre;
            document.getElementById('cliente-apellido').value = cliente.apellido;
            document.getElementById('cliente-dni').value = cliente.dni;
            document.getElementById('cliente-telefono').value = cliente.telefono;
            abrirModalCliente();
        } catch (error) {
            showApiError(error);
        }
    }

    async function guardarCliente() {
        const payload = {
            nombre: document.getElementById('cliente-nombre').value.trim(),
            apellido: document.getElementById('cliente-apellido').value.trim(),
            dni: document.getElementById('cliente-dni').value.trim(),
            telefono: document.getElementById('cliente-telefono').value.trim(),
        };
        try {
            if (editandoId !== null) {
                await apiRequest(`/clientes/${editandoId}`, { method: 'PUT', body: payload });
                showToast('Cliente actualizado correctamente');
            } else {
                await apiRequest('/clientes', { method: 'POST', body: payload });
                showToast('Cliente creado correctamente');
            }
            cerrarModalCliente();
            tabla.ajax.reload();
        } catch (error) {
            showApiError(error);
        }
    }

    function eliminarCliente(id) {
        confirmarEliminar('Eliminar cliente', '¿Seguro que desea eliminar este cliente?', async () => {
            try {
                await apiRequest(`/clientes/${id}`, { method: 'DELETE' });
                showToast('Cliente eliminado correctamente');
                tabla.ajax.reload();
            } catch (error) {
                showApiError(error);
            }
        });
    }

    formCliente.addEventListener('submit', (event) => {
        event.preventDefault();
        event.stopPropagation();
        formCliente.classList.add('was-validated');
        if (!formCliente.checkValidity()) {
            return;
        }
        guardarCliente();
    });

    document.getElementById('btn-nuevo-cliente').addEventListener('click', abrirNuevoCliente);

    tabla = $('#tabla-clientes').DataTable({
        serverSide: true,
        processing: true,
        ajax: {
            url: `${API_BASE_URL}/clientes`,
            data: (d) => {
                d.page = d.start / d.length;
                d.size = d.length;
                if (d.order && d.order.length) {
                    d.sort = `${d.columns[d.order[0].column].data},${d.order[0].dir}`;
                }
            },
            dataSrc: (json) => {
                json.recordsTotal = json.totalElements;
                json.recordsFiltered = json.totalElements;
                return json.content;
            },
            error: mostrarErrorTabla,
        },
        columns: [
            { data: 'id', title: 'ID' },
            {
                data: 'nombre',
                title: 'Nombre',
                render: (data, type, row) => `${escapeHtml(row.nombre)} ${escapeHtml(row.apellido)}`,
            },
            { data: 'dni', title: 'DNI', render: (data) => escapeHtml(data) },
            { data: 'telefono', title: 'Teléfono', render: (data) => escapeHtml(data) },
            {
                data: 'id',
                title: 'Acciones',
                orderable: false,
                className: 'text-center',
                render: (data) => `
                    <button type="button" class="btn btn-sm btn-outline-primary btn-accion" data-accion="editar" data-id="${data}" title="Editar">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-danger btn-accion" data-accion="eliminar" data-id="${data}" title="Eliminar">
                        <i class="bi bi-trash"></i>
                    </button>`,
            },
        ],
        language: {
            url: 'https://cdn.datatables.net/plug-ins/1.13.8/i18n/es-AR.json',
        },
        order: [[0, 'asc']],
    });

    $('#tabla-clientes').on('click', 'tbody button[data-accion]', function () {
        const id = Number(this.dataset.id);
        if (this.dataset.accion === 'editar') {
            abrirEdicionCliente(id);
        } else if (this.dataset.accion === 'eliminar') {
            eliminarCliente(id);
        }
    });
});
