$(function () {
    let tabla;
    let filtroOrden = null;
    let pagoEditandoId = null;
    let deudaActual = null;

    const modalPago = document.getElementById('modal-pago');
    const formPago = document.getElementById('form-pago');
    const tituloPago = document.getElementById('modal-pago-titulo');
    const selectPagoOrden = document.getElementById('pago-orden');
    const inputPagoFecha = document.getElementById('pago-fecha');
    const inputPagoMonto = document.getElementById('pago-monto');
    const infoPago = document.getElementById('pago-info');

    async function cargarOrdenes() {
        try {
            const ordenes = await fetchAll('/ordenes-trabajo');
            const opciones = ordenes
                .map((o) => `<option value="${o.id}">N° ${o.id} - ${escapeHtml(o.cliente.nombre)} ${escapeHtml(o.cliente.apellido)} (${escapeHtml(o.vehiculo.patente)})</option>`)
                .join('');
            selectPagoOrden.innerHTML = '<option value="">Seleccione una orden</option>' + opciones;
            document.getElementById('filtro-orden').innerHTML = '<option value="">Todas las órdenes</option>' + opciones;
        } catch (error) {
            showApiError(error);
        }
    }

    function mostrarInfoOrden(orden) {
        deudaActual = Number(orden.deuda);
        const total = Number(orden.total);
        const pagado = Number(orden.pagado);
        infoPago.innerHTML = `
            <div class="row mb-0">
                <div class="col-4 text-muted small">Total</div>
                <div class="col-4 text-muted small">Pagado</div>
                <div class="col-4 text-muted small">Deuda</div>
                <div class="col-4 fw-bold">${formatMoneda(total)}</div>
                <div class="col-4">${formatMoneda(pagado)}</div>
                <div class="col-4 fw-bold text-danger">${formatMoneda(deudaActual)}</div>
            </div>`;
        infoPago.classList.remove('d-none');
    }

    async function actualizarInfoOrden(ordenId) {
        if (!ordenId) {
            deudaActual = null;
            infoPago.classList.add('d-none');
            return;
        }
        try {
            const orden = await apiRequest(`/ordenes-trabajo/${ordenId}`);
            mostrarInfoOrden(orden);
        } catch (error) {
            showApiError(error);
        }
    }

    function abrirModalPago() {
        formPago.classList.remove('was-validated');
        bootstrap.Modal.getOrCreateInstance(modalPago).show();
    }

    function cerrarModalPago() {
        bootstrap.Modal.getOrCreateInstance(modalPago).hide();
        formPago.reset();
        deudaActual = null;
        infoPago.classList.add('d-none');
        selectPagoOrden.disabled = false;
    }

    async function abrirNuevoPago() {
        await cargarOrdenes();
        pagoEditandoId = null;
        tituloPago.textContent = 'Nuevo pago';
        formPago.reset();
        selectPagoOrden.disabled = false;
        inputPagoFecha.value = isoToLocal(new Date().toISOString());
        abrirModalPago();
    }

    async function abrirEdicionPago(pago) {
        await cargarOrdenes();
        pagoEditandoId = pago.id;
        tituloPago.textContent = `Editar pago N° ${pago.id}`;
        selectPagoOrden.disabled = true;
        selectPagoOrden.value = pago.ordenTrabajoId;
        inputPagoFecha.value = isoToLocal(pago.fechaPago);
        inputPagoMonto.value = pago.cantidadPagada;
        await actualizarInfoOrden(pago.ordenTrabajoId);
        abrirModalPago();
    }

    function validarMonto() {
        const monto = Number(inputPagoMonto.value);
        if (deudaActual !== null && monto > deudaActual + 0.001) {
            showToast(`El monto no puede superar la deuda de ${formatMoneda(deudaActual)}`, 'warning');
            return false;
        }
        return true;
    }

    async function guardarPago() {
        if (!validarMonto()) {
            return;
        }
        const payload = {
            fechaPago: localToIso(inputPagoFecha.value),
            cantidadPagada: Number(inputPagoMonto.value),
        };
        try {
            if (pagoEditandoId !== null) {
                await apiRequest(`/pagos/${pagoEditandoId}`, { method: 'PUT', body: payload });
                showToast('Pago actualizado correctamente');
            } else {
                payload.ordenTrabajoId = Number(selectPagoOrden.value);
                await apiRequest('/pagos', { method: 'POST', body: payload });
                showToast('Pago registrado correctamente');
            }
            cerrarModalPago();
            tabla.ajax.reload();
        } catch (error) {
            showApiError(error);
        }
    }

    function eliminarPago(id) {
        confirmarEliminar('Eliminar pago', '¿Seguro que desea eliminar este pago?', async () => {
            try {
                await apiRequest(`/pagos/${id}`, { method: 'DELETE' });
                showToast('Pago eliminado correctamente');
                tabla.ajax.reload();
            } catch (error) {
                showApiError(error);
            }
        });
    }

    selectPagoOrden.addEventListener('change', () => actualizarInfoOrden(Number(selectPagoOrden.value) || null));

    formPago.addEventListener('submit', (event) => {
        event.preventDefault();
        event.stopPropagation();
        formPago.classList.add('was-validated');
        if (!formPago.checkValidity()) {
            return;
        }
        guardarPago();
    });

    document.getElementById('btn-nuevo-pago').addEventListener('click', abrirNuevoPago);

    document.getElementById('filtro-orden').addEventListener('change', (event) => {
        filtroOrden = event.target.value ? Number(event.target.value) : null;
        tabla.ajax.reload();
    });

    tabla = $('#tabla-pagos').DataTable({
        serverSide: true,
        processing: true,
        ajax: {
            url: () => (filtroOrden ? `${API_BASE_URL}/pagos/orden/${filtroOrden}` : `${API_BASE_URL}/pagos`),
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
            { data: 'fechaPago', title: 'Fecha', render: (data) => formatFecha(data) },
            { data: 'cantidadPagada', title: 'Monto', render: (data) => formatMoneda(data) },
            { data: 'ordenTrabajoId', title: 'Orden N°' },
            {
                data: 'id',
                title: 'Acciones',
                orderable: false,
                className: 'text-center',
                render: (data, type, row) => `
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

    $('#tabla-pagos').on('click', 'tbody button[data-accion]', async function () {
        const id = Number(this.dataset.id);
        if (this.dataset.accion === 'editar') {
            const rowData = tabla.row($(this).closest('tr')).data();
            await abrirEdicionPago(rowData);
        } else if (this.dataset.accion === 'eliminar') {
            eliminarPago(id);
        }
    });

    cargarOrdenes();
});
