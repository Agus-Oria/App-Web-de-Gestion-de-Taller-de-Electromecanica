$(function () {
    let tabla;
    let editandoId = null;
    let filtroDni = null;
    let filtroNombre = null;

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
            document.getElementById('cliente-localidad').value = cliente.localidad || '';
            document.getElementById('cliente-direccion').value = cliente.direccion || '';
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
            localidad: document.getElementById('cliente-localidad').value.trim() || null,
            direccion: document.getElementById('cliente-direccion').value.trim() || null,
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

    document.getElementById('btn-buscar-cliente').addEventListener('click', async () => {
        const dni = document.getElementById('buscar-cliente-dni').value.trim();
        const nombre = document.getElementById('buscar-cliente-nombre').value.trim();
        if (dni && nombre) {
            showToast('Complete solo uno de los campos de búsqueda', 'warning');
            return;
        }
        if (dni) {
            try {
                await apiRequest(`/clientes/dni/${encodeURIComponent(dni)}`);
                filtroDni = dni;
                filtroNombre = null;
                tabla.ajax.reload();
            } catch (error) {
                showToast('No se encontró un cliente con ese DNI', 'warning');
            }
            return;
        }
        if (nombre) {
            filtroNombre = nombre;
            filtroDni = null;
            tabla.ajax.reload();
            return;
        }
        showToast('Ingrese un DNI o un nombre para buscar', 'warning');
    });

    document.getElementById('btn-limpiar-busqueda').addEventListener('click', () => {
        filtroDni = null;
        filtroNombre = null;
        document.getElementById('buscar-cliente-dni').value = '';
        document.getElementById('buscar-cliente-nombre').value = '';
        tabla.ajax.reload();
    });

    tabla = $('#tabla-clientes').DataTable({
        serverSide: true,
        processing: true,
        searching: false,
        ajax: (data, callback) => {
            const responder = (json) => callback({
                draw: data.draw,
                recordsTotal: json.totalElements,
                recordsFiltered: json.totalElements,
                data: json.content || json.data || [],
            });
            if (filtroDni) {
                apiRequest(`/clientes/dni/${encodeURIComponent(filtroDni)}`)
                    .then((cliente) => responder({ content: [cliente], totalElements: 1 }))
                    .catch(() => responder({ content: [], totalElements: 0 }));
                return;
            }
            const params = { page: data.start / data.length, size: data.length };
            if (data.order && data.order.length) {
                params.sort = `${data.columns[data.order[0].column].data},${data.order[0].dir}`;
            }
            const url = filtroNombre
                ? `${API_BASE_URL}/clientes/buscar?texto=${encodeURIComponent(filtroNombre)}`
                : `${API_BASE_URL}/clientes`;
            $.ajax({
                url,
                method: 'GET',
                dataType: 'json',
                data: params,
                success: (json) => responder(json),
                error: (xhr) => {
                    mostrarErrorTabla(xhr);
                    responder({ content: [], totalElements: 0 });
                },
            });
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
