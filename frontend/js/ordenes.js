$(function () {
    let tabla;
    let editandoId = null;
    let ordenActual = null;
    let detalleEditandoId = null;

    const modalOrden = document.getElementById('modal-orden');
    const formOrden = document.getElementById('form-orden');
    const tituloOrden = document.getElementById('modal-orden-titulo');
    const seccionLineas = document.getElementById('seccion-lineas');
    const contenedorLineas = document.getElementById('contenedor-lineas');
    const totalPreview = document.getElementById('total-preview');
    const selectCliente = document.getElementById('orden-cliente');
    const selectVehiculo = document.getElementById('orden-vehiculo');
    const inputFechaIngreso = document.getElementById('orden-fechaIngreso');
    const inputFechaEntrega = document.getElementById('orden-fechaEntrega');
    const inputProblema = document.getElementById('orden-problema');
    const inputDiagnostico = document.getElementById('orden-diagnostico');
    const selectEstado = document.getElementById('orden-estado');

    const modalVer = document.getElementById('modal-ver-orden');
    const modalDetalle = document.getElementById('modal-detalle');
    const formDetalle = document.getElementById('form-detalle');
    const tituloDetalle = document.getElementById('modal-detalle-titulo');
    const modalEstado = document.getElementById('modal-estado');

    async function cargarSelects() {
        try {
            const [clientes, vehiculos] = await Promise.all([
                fetchAll('/clientes'),
                fetchAll('/vehiculos'),
            ]);
            selectCliente.innerHTML = '<option value="">Seleccione un cliente</option>' + clientes
                .map((c) => `<option value="${c.id}">${escapeHtml(c.nombre)} ${escapeHtml(c.apellido)} (${escapeHtml(c.dni)})</option>`)
                .join('');
            selectVehiculo.innerHTML = '<option value="">Seleccione un vehículo</option>' + vehiculos
                .map((v) => `<option value="${v.id}">${escapeHtml(v.patente)} - ${escapeHtml(v.marca)} ${escapeHtml(v.modelo)} (${v.anio})</option>`)
                .join('');
        } catch (error) {
            showApiError(error);
        }
    }

    function crearFilaLinea(descripcion = '', cantidad = '', precio = '') {
        const fila = document.createElement('div');
        fila.className = 'row g-2 align-items-center linea-detalle mb-2';
        fila.innerHTML = `
            <div class="col-12 col-md-4">
                <input type="text" class="form-control form-control-sm linea-descripcion" maxlength="500" placeholder="Descripción" value="${escapeHtml(descripcion)}">
            </div>
            <div class="col-4 col-md-2">
                <input type="number" class="form-control form-control-sm linea-cantidad" min="1" step="1" placeholder="Cant." value="${escapeHtml(cantidad)}">
            </div>
            <div class="col-4 col-md-2">
                <input type="number" class="form-control form-control-sm linea-precio" min="0.01" step="0.01" placeholder="Precio" value="${escapeHtml(precio)}">
            </div>
            <div class="col-3 col-md-3 linea-subtotal text-end fw-bold"></div>
            <div class="col-1 text-end">
                <button type="button" class="btn btn-sm btn-outline-danger btn-quitar-linea" title="Quitar línea">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>`;
        contenedorLineas.appendChild(fila);
        recomputarTotales();
    }

    function agregarLinea() {
        crearFilaLinea();
    }

    function recomputarTotales() {
        let total = 0;
        contenedorLineas.querySelectorAll('.linea-detalle').forEach((fila) => {
            const cantidad = Number(fila.querySelector('.linea-cantidad').value) || 0;
            const precio = Number(fila.querySelector('.linea-precio').value) || 0;
            const subtotal = cantidad * precio;
            fila.querySelector('.linea-subtotal').textContent = formatMoneda(subtotal);
            total += subtotal;
        });
        totalPreview.textContent = formatMoneda(total);
    }

    contenedorLineas.addEventListener('input', recomputarTotales);
    contenedorLineas.addEventListener('click', (event) => {
        const boton = event.target.closest('.btn-quitar-linea');
        if (boton) {
            boton.closest('.linea-detalle').remove();
            recomputarTotales();
        }
    });
    document.getElementById('btn-agregar-linea').addEventListener('click', agregarLinea);

    function recolectarLineas() {
        const lineas = [];
        let incompleta = false;
        contenedorLineas.querySelectorAll('.linea-detalle').forEach((fila) => {
            const descripcion = fila.querySelector('.linea-descripcion').value.trim();
            const cantidad = Number(fila.querySelector('.linea-cantidad').value);
            const precio = Number(fila.querySelector('.linea-precio').value);
            const vacia = !descripcion && !fila.querySelector('.linea-cantidad').value && !fila.querySelector('.linea-precio').value;
            if (vacia) {
                return;
            }
            if (!descripcion || !(cantidad >= 1) || !(precio > 0)) {
                incompleta = true;
                return;
            }
            lineas.push({ descripcion, cantidad, precioUnitario: precio });
        });
        if (incompleta) {
            showToast('Hay líneas de detalle incompletas. Complételas o elimínelas.', 'warning');
            return null;
        }
        return lineas;
    }

    function abrirModalOrden() {
        formOrden.classList.remove('was-validated');
        bootstrap.Modal.getOrCreateInstance(modalOrden).show();
    }

    function cerrarModalOrden() {
        bootstrap.Modal.getOrCreateInstance(modalOrden).hide();
        formOrden.reset();
        contenedorLineas.innerHTML = '';
    }

    async function abrirNuevaOrden() {
        await cargarSelects();
        editandoId = null;
        tituloOrden.textContent = 'Nueva orden de trabajo';
        seccionLineas.classList.remove('d-none');
        formOrden.reset();
        selectEstado.value = 'EN_REPARACION';
        inputFechaIngreso.value = isoToLocal(new Date().toISOString());
        contenedorLineas.innerHTML = '';
        agregarLinea();
        abrirModalOrden();
    }

    async function abrirEdicionOrden(id) {
        await cargarSelects();
        try {
            const orden = await apiRequest(`/ordenes-trabajo/${id}`);
            editandoId = orden.id;
            tituloOrden.textContent = `Editar orden N° ${orden.id}`;
            seccionLineas.classList.add('d-none');
            selectCliente.value = orden.cliente.id;
            selectVehiculo.value = orden.vehiculo.id;
            inputFechaIngreso.value = isoToLocal(orden.fechaIngreso);
            inputFechaEntrega.value = isoToLocal(orden.fechaEntrega);
            inputProblema.value = orden.problemaInformado;
            inputDiagnostico.value = orden.diagnostico || '';
            selectEstado.value = orden.estado;
            contenedorLineas.innerHTML = '';
            abrirModalOrden();
        } catch (error) {
            showApiError(error);
        }
    }

    async function guardarOrden() {
        const payload = {
            fechaIngreso: localToIso(inputFechaIngreso.value),
            fechaEntrega: localToIso(inputFechaEntrega.value),
            problemaInformado: inputProblema.value.trim(),
            diagnostico: inputDiagnostico.value.trim() || null,
            estado: selectEstado.value,
            clienteId: Number(selectCliente.value),
            vehiculoId: Number(selectVehiculo.value),
        };
        try {
            if (editandoId !== null) {
                await apiRequest(`/ordenes-trabajo/${editandoId}`, { method: 'PUT', body: payload });
                showToast('Orden actualizada correctamente');
            } else {
            if (editandoId === null) {
                const lineas = recolectarLineas();
                if (lineas === null) {
                    return;
                }
                payload.detalles = lineas;
            }
                await apiRequest('/ordenes-trabajo', { method: 'POST', body: payload });
                showToast('Orden creada correctamente');
            }
            cerrarModalOrden();
            tabla.ajax.reload();
        } catch (error) {
            showApiError(error);
        }
    }

    function eliminarOrden(id) {
        confirmarEliminar('Eliminar orden de trabajo', '¿Seguro que desea eliminar esta orden de trabajo?', async () => {
            try {
                await apiRequest(`/ordenes-trabajo/${id}`, { method: 'DELETE' });
                showToast('Orden eliminada correctamente');
                tabla.ajax.reload();
            } catch (error) {
                showApiError(error);
            }
        });
    }

    function renderDetalles(orden) {
        const tbody = document.getElementById('tabla-detalles-body');
        if (!orden.detalles || orden.detalles.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Sin detalles</td></tr>';
            return;
        }
        tbody.innerHTML = orden.detalles.map((d) => `
            <tr>
                <td>${escapeHtml(d.descripcion)}</td>
                <td>${d.cantidad}</td>
                <td>${formatMoneda(d.precioUnitario)}</td>
                <td>${formatMoneda(d.subtotal)}</td>
                <td class="text-end">
                    <button type="button" class="btn btn-sm btn-outline-primary btn-accion" data-accion="editar-detalle" data-id="${d.id}" title="Editar detalle">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-danger btn-accion" data-accion="eliminar-detalle" data-id="${d.id}" title="Eliminar detalle">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>`).join('');
    }

    function renderVerOrden(orden) {
        document.getElementById('ver-id').textContent = orden.id;
        document.getElementById('ver-cliente').textContent = `${orden.cliente.nombre} ${orden.cliente.apellido}`;
        document.getElementById('ver-vehiculo').textContent = `${orden.vehiculo.patente} - ${orden.vehiculo.marca} ${orden.vehiculo.modelo} (${orden.vehiculo.anio})`;
        document.getElementById('ver-fechaIngreso').textContent = formatFecha(orden.fechaIngreso);
        document.getElementById('ver-fechaEntrega').textContent = formatFecha(orden.fechaEntrega);
        document.getElementById('ver-problema').textContent = orden.problemaInformado;
        document.getElementById('ver-diagnostico').textContent = orden.diagnostico || '-';
        document.getElementById('ver-total').textContent = formatMoneda(orden.total);
        document.getElementById('ver-pagado').textContent = formatMoneda(orden.pagado);
        document.getElementById('ver-deuda').textContent = formatMoneda(orden.deuda);
        document.getElementById('ver-estado').innerHTML = estadoBadge(orden.estado);
        renderDetalles(orden);
    }

    async function verOrden(id) {
        try {
            const orden = await apiRequest(`/ordenes-trabajo/${id}/detalle`);
            ordenActual = orden;
            renderVerOrden(orden);
            bootstrap.Modal.getOrCreateInstance(modalVer).show();
        } catch (error) {
            showApiError(error);
        }
    }

    function abrirModalDetalle(detalle) {
        formDetalle.classList.remove('was-validated');
        if (detalle) {
            detalleEditandoId = detalle.id;
            tituloDetalle.textContent = 'Editar detalle';
            document.getElementById('detalle-descripcion').value = detalle.descripcion;
            document.getElementById('detalle-cantidad').value = detalle.cantidad;
            document.getElementById('detalle-precio').value = detalle.precioUnitario;
        } else {
            detalleEditandoId = null;
            tituloDetalle.textContent = 'Nuevo detalle';
            formDetalle.reset();
        }
        bootstrap.Modal.getOrCreateInstance(modalDetalle).show();
    }

    async function guardarDetalle() {
        const payload = {
            descripcion: document.getElementById('detalle-descripcion').value.trim(),
            cantidad: Number(document.getElementById('detalle-cantidad').value),
            precioUnitario: Number(document.getElementById('detalle-precio').value),
        };
        try {
            if (detalleEditandoId !== null) {
                await apiRequest(`/detalles-orden/${detalleEditandoId}`, { method: 'PUT', body: payload });
                showToast('Detalle actualizado correctamente');
            } else {
                payload.ordenTrabajoId = ordenActual.id;
                await apiRequest('/detalles-orden', { method: 'POST', body: payload });
                showToast('Detalle agregado correctamente');
            }
            bootstrap.Modal.getOrCreateInstance(modalDetalle).hide();
            formDetalle.reset();
            const orden = await apiRequest(`/ordenes-trabajo/${ordenActual.id}/detalle`);
            ordenActual = orden;
            renderVerOrden(orden);
            tabla.ajax.reload();
        } catch (error) {
            showApiError(error);
        }
    }

    function eliminarDetalle(id) {
        confirmarEliminar('Eliminar detalle', '¿Seguro que desea eliminar este detalle?', async () => {
            try {
                await apiRequest(`/detalles-orden/${id}`, { method: 'DELETE' });
                showToast('Detalle eliminado correctamente');
                const orden = await apiRequest(`/ordenes-trabajo/${ordenActual.id}/detalle`);
                ordenActual = orden;
                renderVerOrden(orden);
                tabla.ajax.reload();
            } catch (error) {
                showApiError(error);
            }
        });
    }

    function abrirModalEstado() {
        if (!ordenActual) {
            return;
        }
        document.getElementById('estado-nuevo').value = ordenActual.estado;
        bootstrap.Modal.getOrCreateInstance(modalEstado).show();
    }

    async function guardarEstado() {
        const nuevoEstado = document.getElementById('estado-nuevo').value;
        if (nuevoEstado === ordenActual.estado) {
            bootstrap.Modal.getOrCreateInstance(modalEstado).hide();
            return;
        }
        try {
            const orden = await apiRequest(`/ordenes-trabajo/${ordenActual.id}/estado`, {
                method: 'PATCH',
                body: { estado: nuevoEstado },
            });
            ordenActual = orden;
            renderVerOrden(orden);
            bootstrap.Modal.getOrCreateInstance(modalEstado).hide();
            showToast('Estado actualizado correctamente');
            tabla.ajax.reload();
        } catch (error) {
            showApiError(error);
        }
    }

    formOrden.addEventListener('submit', (event) => {
        event.preventDefault();
        event.stopPropagation();
        formOrden.classList.add('was-validated');
        if (!formOrden.checkValidity()) {
            return;
        }
        guardarOrden();
    });

    formDetalle.addEventListener('submit', (event) => {
        event.preventDefault();
        event.stopPropagation();
        formDetalle.classList.add('was-validated');
        if (!formDetalle.checkValidity()) {
            return;
        }
        guardarDetalle();
    });

    document.getElementById('btn-nueva-orden').addEventListener('click', abrirNuevaOrden);
    document.getElementById('btn-agregar-detalle').addEventListener('click', () => abrirModalDetalle(null));
    document.getElementById('btn-cambiar-estado').addEventListener('click', abrirModalEstado);
    document.getElementById('btn-guardar-estado').addEventListener('click', guardarEstado);

    $('#tabla-ordenes').on('click', 'tbody button[data-accion]', function () {
        const id = Number(this.dataset.id);
        const accion = this.dataset.accion;
        if (accion === 'ver') {
            verOrden(id);
        } else if (accion === 'editar') {
            abrirEdicionOrden(id);
        } else if (accion === 'eliminar') {
            eliminarOrden(id);
        }
    });

    $('#tabla-detalles-body').on('click', 'button[data-accion]', function () {
        const id = Number(this.dataset.id);
        const accion = this.dataset.accion;
        if (accion === 'editar-detalle') {
            const detalle = ordenActual.detalles.find((d) => d.id === id);
            if (detalle) {
                abrirModalDetalle(detalle);
            }
        } else if (accion === 'eliminar-detalle') {
            eliminarDetalle(id);
        }
    });

    tabla = $('#tabla-ordenes').DataTable({
        serverSide: true,
        processing: true,
        ajax: {
            url: `${API_BASE_URL}/ordenes-trabajo`,
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
                data: 'cliente',
                title: 'Cliente',
                orderable: false,
                render: (data) => `${escapeHtml(data.nombre)} ${escapeHtml(data.apellido)}`,
            },
            {
                data: 'vehiculo',
                title: 'Vehículo',
                orderable: false,
                render: (data) => `${escapeHtml(data.patente)} - ${escapeHtml(data.marca)} ${escapeHtml(data.modelo)}`,
            },
            { data: 'estado', title: 'Estado', render: (data) => estadoBadge(data) },
            { data: 'total', title: 'Total', render: (data) => formatMoneda(data) },
            { data: 'pagado', title: 'Pagado', render: (data) => formatMoneda(data) },
            {
                data: 'deuda',
                title: 'Deuda',
                orderable: false,
                render: (data) => formatMoneda(data),
            },
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
        order: [[0, 'desc']],
    });

    cargarSelects();
});
