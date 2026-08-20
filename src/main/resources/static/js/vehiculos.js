$(function () {
    let tabla;
    let editandoId = null;
    let filtroPatente = null;

    const modalVehiculo = document.getElementById('modal-vehiculo');
    const formVehiculo = document.getElementById('form-vehiculo');
    const tituloModal = document.getElementById('modal-vehiculo-titulo');
    const selectMarca = document.getElementById('vehiculo-marca');

    async function cargarMarcas() {
        try {
            const marcas = await apiRequest('/marcas');
            selectMarca.innerHTML = '<option value="">Seleccione una marca</option>' + marcas
                .map((m) => `<option value="${m.id}">${escapeHtml(m.nombre)}</option>`)
                .join('');
        } catch (error) {
            showApiError(error);
        }
    }

    function abrirModalVehiculo() {
        formVehiculo.classList.remove('was-validated');
        bootstrap.Modal.getOrCreateInstance(modalVehiculo).show();
    }

    function cerrarModalVehiculo() {
        bootstrap.Modal.getOrCreateInstance(modalVehiculo).hide();
        formVehiculo.reset();
    }

    function abrirNuevoVehiculo() {
        editandoId = null;
        tituloModal.textContent = 'Nuevo vehículo';
        formVehiculo.reset();
        abrirModalVehiculo();
    }

    async function abrirEdicionVehiculo(id) {
        try {
            const vehiculo = await apiRequest(`/vehiculos/${id}`);
            editandoId = vehiculo.id;
            tituloModal.textContent = 'Editar vehículo';
            document.getElementById('vehiculo-patente').value = vehiculo.patente;
            selectMarca.value = vehiculo.marca ? vehiculo.marca.id : '';
            document.getElementById('vehiculo-modelo').value = vehiculo.modelo;
            document.getElementById('vehiculo-anio').value = vehiculo.anio;
            document.getElementById('vehiculo-color').value = vehiculo.color || '';
            document.getElementById('vehiculo-chasis').value = vehiculo.numeroChasis || '';
            document.getElementById('vehiculo-motor').value = vehiculo.numeroMotor || '';
            abrirModalVehiculo();
        } catch (error) {
            showApiError(error);
        }
    }

    async function guardarVehiculo() {
        const payload = {
            patente: document.getElementById('vehiculo-patente').value.trim(),
            marcaId: Number(selectMarca.value),
            modelo: document.getElementById('vehiculo-modelo').value.trim(),
            anio: Number(document.getElementById('vehiculo-anio').value),
            color: document.getElementById('vehiculo-color').value.trim() || null,
            numeroChasis: document.getElementById('vehiculo-chasis').value.trim() || null,
            numeroMotor: document.getElementById('vehiculo-motor').value.trim() || null,
        };
        try {
            if (editandoId !== null) {
                await apiRequest(`/vehiculos/${editandoId}`, { method: 'PUT', body: payload });
                showToast('Vehículo actualizado correctamente');
            } else {
                await apiRequest('/vehiculos', { method: 'POST', body: payload });
                showToast('Vehículo creado correctamente');
            }
            cerrarModalVehiculo();
            tabla.ajax.reload();
        } catch (error) {
            showApiError(error);
        }
    }

    function eliminarVehiculo(id) {
        confirmarEliminar('Eliminar vehículo', '¿Seguro que desea eliminar este vehículo?', async () => {
            try {
                await apiRequest(`/vehiculos/${id}`, { method: 'DELETE' });
                showToast('Vehículo eliminado correctamente');
                tabla.ajax.reload();
            } catch (error) {
                showApiError(error);
            }
        });
    }

    async function verVehiculo(id) {
        try {
            const vehiculo = await apiRequest(`/vehiculos/${id}`);
            document.getElementById('ver-vehiculo-id').textContent = vehiculo.id;
            document.getElementById('ver-vehiculo-patente').textContent = vehiculo.patente;
            document.getElementById('ver-vehiculo-marca').textContent = vehiculo.marca ? vehiculo.marca.nombre : '-';
            document.getElementById('ver-vehiculo-modelo').textContent = vehiculo.modelo;
            document.getElementById('ver-vehiculo-anio').textContent = vehiculo.anio;
            document.getElementById('ver-vehiculo-color').textContent = vehiculo.color || '-';
            document.getElementById('ver-vehiculo-chasis').textContent = vehiculo.numeroChasis || '-';
            document.getElementById('ver-vehiculo-motor').textContent = vehiculo.numeroMotor || '-';
            bootstrap.Modal.getOrCreateInstance(document.getElementById('modal-ver-vehiculo')).show();
        } catch (error) {
            showApiError(error);
        }
    }

    formVehiculo.addEventListener('submit', (event) => {
        event.preventDefault();
        event.stopPropagation();
        formVehiculo.classList.add('was-validated');
        if (!formVehiculo.checkValidity()) {
            return;
        }
        guardarVehiculo();
    });

    document.getElementById('btn-nuevo-vehiculo').addEventListener('click', abrirNuevoVehiculo);
    cargarMarcas();

    document.getElementById('btn-buscar-patente').addEventListener('click', async () => {
        const patente = document.getElementById('buscar-vehiculo-patente').value.trim();
        if (!patente) {
            showToast('Ingrese una patente para buscar', 'warning');
            return;
        }
        try {
            await apiRequest(`/vehiculos/patente/${encodeURIComponent(patente)}`);
            filtroPatente = patente;
            tabla.ajax.reload();
        } catch (error) {
            showToast('No se encontró un vehículo con esa patente', 'warning');
        }
    });

    document.getElementById('btn-limpiar-busqueda').addEventListener('click', () => {
        filtroPatente = null;
        document.getElementById('buscar-vehiculo-patente').value = '';
        tabla.ajax.reload();
    });

    tabla = $('#tabla-vehiculos').DataTable({
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
            if (filtroPatente) {
                apiRequest(`/vehiculos/patente/${encodeURIComponent(filtroPatente)}`)
                    .then((vehiculo) => responder({ content: [vehiculo], totalElements: 1 }))
                    .catch(() => responder({ content: [], totalElements: 0 }));
                return;
            }
            const params = { page: data.start / data.length, size: data.length };
            if (data.order && data.order.length) {
                params.sort = `${data.columns[data.order[0].column].data},${data.order[0].dir}`;
            }
            $.ajax({
                url: `${API_BASE_URL}/vehiculos`,
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
            { data: 'patente', title: 'Patente', render: (data) => escapeHtml(data) },
            { data: 'marca.nombre', title: 'Marca', render: (data) => escapeHtml(data) },
            { data: 'modelo', title: 'Modelo', render: (data) => escapeHtml(data) },
            { data: 'anio', title: 'Año' },
            { data: 'color', title: 'Color', render: (data) => (data ? escapeHtml(data) : '-') },
            {
                data: 'id',
                title: 'Acciones',
                orderable: false,
                className: 'text-center',
                render: (data) => `
                    <button type="button" class="btn btn-sm btn-outline-secondary btn-accion" data-accion="ver" data-id="${data}" title="Ver">
                        <i class="bi bi-eye"></i>
                    </button>
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

    $('#tabla-vehiculos').on('click', 'tbody button[data-accion]', function () {
        const id = Number(this.dataset.id);
        if (this.dataset.accion === 'ver') {
            verVehiculo(id);
        } else if (this.dataset.accion === 'editar') {
            abrirEdicionVehiculo(id);
        } else if (this.dataset.accion === 'eliminar') {
            eliminarVehiculo(id);
        }
    });
});
