$(function () {
    let tabla;
    let editandoId = null;
    let ordenActual = null;
    let detalleEditandoId = null;
    let filtroClienteOrden = null;
    let filtroPatenteOrden = null;
    let clienteSeleccionadoId = null;
    let vehiculoSeleccionadoId = null;
    let pagoEditandoId = null;
    let montoPagoAnterior = 0;
    let deudaActual = null;
    let pagosActuales = [];
    let imagenesActuales = [];

    const modalOrden = document.getElementById('modal-orden');
    const formOrden = document.getElementById('form-orden');
    const tituloOrden = document.getElementById('modal-orden-titulo');
    const seccionLineas = document.getElementById('seccion-lineas');
    const contenedorLineas = document.getElementById('contenedor-lineas');
    const totalPreview = document.getElementById('total-preview');
    const inputBuscarCliente = document.getElementById('orden-buscar-cliente');
    const divClienteSeleccionado = document.getElementById('orden-cliente-seleccionado');
    const spanClienteSeleccionado = document.getElementById('orden-cliente-nombre');
    const inputBuscarVehiculo = document.getElementById('orden-buscar-vehiculo');
    const divVehiculoSeleccionado = document.getElementById('orden-vehiculo-seleccionado');
    const spanVehiculoSeleccionado = document.getElementById('orden-vehiculo-nombre');
    const inputFechaIngreso = document.getElementById('orden-fechaIngreso');
    const inputFechaEntrega = document.getElementById('orden-fechaEntrega');
    const inputProblema = document.getElementById('orden-problema');
    const inputDiagnostico = document.getElementById('orden-diagnostico');
    const selectEstado = document.getElementById('orden-estado');

    const inputKilometraje = document.getElementById('orden-kilometraje');

    const modalVer = document.getElementById('modal-ver-orden');
    const modalDetalle = document.getElementById('modal-detalle');
    const formDetalle = document.getElementById('form-detalle');
    const tituloDetalle = document.getElementById('modal-detalle-titulo');
    const modalEstado = document.getElementById('modal-estado');
    const modalPago = document.getElementById('modal-pago');
    const formPago = document.getElementById('form-pago');
    const tituloPago = document.getElementById('modal-pago-titulo');
    const inputPagoFecha = document.getElementById('pago-fecha');
    const inputPagoMonto = document.getElementById('pago-monto');
    const selectPagoMetodo = document.getElementById('pago-metodo');
    const infoPago = document.getElementById('pago-info');
    const pagoOrdenInfo = document.getElementById('pago-orden-info');
    const inputImagenesOrden = document.getElementById('orden-imagenes');
    const btnAgregarImagenes = document.getElementById('btn-agregar-imagenes');
    const inputImagenesVer = document.getElementById('imagenes-archivos');
    const contenedorImagenes = document.getElementById('contenedor-imagenes');

    function mostrarClienteSeleccionado(texto) {
        spanClienteSeleccionado.textContent = texto;
        divClienteSeleccionado.classList.remove('d-none');
    }

    function limpiarClienteSeleccionado() {
        clienteSeleccionadoId = null;
        inputBuscarCliente.value = '';
        divClienteSeleccionado.classList.add('d-none');
        sugerenciasCliente.ocultar();
    }

    function mostrarVehiculoSeleccionado(texto) {
        spanVehiculoSeleccionado.textContent = texto;
        divVehiculoSeleccionado.classList.remove('d-none');
    }

    function limpiarVehiculoSeleccionado() {
        vehiculoSeleccionadoId = null;
        inputBuscarVehiculo.value = '';
        divVehiculoSeleccionado.classList.add('d-none');
        sugerenciasVehiculo.ocultar();
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
        inputImagenesOrden.value = '';
        contenedorLineas.innerHTML = '';
    }

    function abrirNuevaOrden() {
        editandoId = null;
        tituloOrden.textContent = 'Nueva orden de trabajo';
        seccionLineas.classList.remove('d-none');
        formOrden.reset();
        limpiarClienteSeleccionado();
        limpiarVehiculoSeleccionado();
        selectEstado.value = 'EN_REPARACION';
        inputFechaIngreso.value = isoToLocal(new Date().toISOString());
        contenedorLineas.innerHTML = '';
        agregarLinea();
        abrirModalOrden();
    }

    async function abrirEdicionOrden(id) {
        try {
            const orden = await apiRequest(`/ordenes-trabajo/${id}`);
            const [cliente, vehiculo] = await Promise.all([
                apiRequest(`/clientes/${orden.cliente.id}`),
                apiRequest(`/vehiculos/${orden.vehiculo.id}`),
            ]);
            editandoId = orden.id;
            tituloOrden.textContent = `Editar orden N° ${orden.id}`;
            seccionLineas.classList.add('d-none');
            formOrden.reset();
            limpiarClienteSeleccionado();
            limpiarVehiculoSeleccionado();
            inputBuscarCliente.value = textoCliente(cliente);
            clienteSeleccionadoId = cliente.id;
            mostrarClienteSeleccionado(`${cliente.nombre} ${cliente.apellido} (${cliente.dni})`);
            inputBuscarVehiculo.value = textoVehiculo(vehiculo);
            vehiculoSeleccionadoId = vehiculo.id;
            mostrarVehiculoSeleccionado(textoVehiculo(vehiculo));
            inputFechaIngreso.value = isoToLocal(orden.fechaIngreso);
            inputFechaEntrega.value = isoToLocal(orden.fechaEntrega);
            inputKilometraje.value = orden.kilometraje ?? '';
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
            kilometraje: inputKilometraje.value === '' ? null : Number(inputKilometraje.value),
            problemaInformado: inputProblema.value.trim(),
            diagnostico: inputDiagnostico.value.trim() || null,
            estado: selectEstado.value,
            clienteId: clienteSeleccionadoId,
            vehiculoId: vehiculoSeleccionadoId,
        };
        try {
            let ordenId = editandoId;
            if (editandoId !== null) {
                await apiRequest(`/ordenes-trabajo/${editandoId}`, { method: 'PUT', body: payload });
                showToast('Orden actualizada correctamente');
            } else {
                const lineas = recolectarLineas();
                if (lineas === null) {
                    return;
                }
                payload.detalles = lineas;
                const orden = await apiRequest('/ordenes-trabajo', { method: 'POST', body: payload });
                ordenId = orden.id;
                showToast('Orden creada correctamente');
            }
            if (ordenId !== null && inputImagenesOrden.files && inputImagenesOrden.files.length) {
                await subirImagenes(ordenId, inputImagenesOrden.files);
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

    async function renderPagos(ordenId) {
        const tbody = document.getElementById('tabla-pagos-body');
        try {
            const data = await apiRequest(`/pagos/orden/${ordenId}?page=0&size=100`);
            pagosActuales = data.content || [];
            if (pagosActuales.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">Sin pagos</td></tr>';
                return;
            }
            tbody.innerHTML = pagosActuales.map((p) => `
                <tr>
                    <td>${formatFecha(p.fechaPago)}</td>
                    <td>${formatMoneda(p.cantidadPagada)}</td>
                    <td>${metodoPagoLabel(p.metodoPago)}</td>
                    <td class="text-end">
                        <button type="button" class="btn btn-sm btn-outline-primary btn-accion" data-accion="editar-pago" data-id="${p.id}" title="Editar pago">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button type="button" class="btn btn-sm btn-outline-danger btn-accion" data-accion="eliminar-pago" data-id="${p.id}" title="Eliminar pago">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>`).join('');
        } catch (error) {
            pagosActuales = [];
            tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">Sin pagos</td></tr>';
        }
    }

    async function subirImagenes(ordenId, archivos) {
        if (!archivos || !archivos.length) {
            return;
        }
        const formData = new FormData();
        Array.from(archivos).forEach((archivo) => formData.append('imagenes', archivo));
        await apiRequest(`/ordenes-trabajo/${ordenId}/imagenes`, {
            method: 'POST',
            body: formData,
        });
    }

    async function renderImagenes(ordenId) {
        try {
            imagenesActuales = await apiRequest(`/ordenes-trabajo/${ordenId}/imagenes`);
        } catch (error) {
            imagenesActuales = [];
        }
        if (!imagenesActuales.length) {
            contenedorImagenes.innerHTML = '<div class="text-muted">Sin imágenes</div>';
            return;
        }
        contenedorImagenes.innerHTML = imagenesActuales.map((img) => `
            <div class="imagen-orden position-relative">
                <img src="${img.ruta}" class="img-thumbnail" alt="Imagen de la orden" data-accion="ver-imagen" data-ruta="${img.ruta}" title="Abrir imagen">
                <button type="button" class="btn btn-sm btn-danger btn-accion-imagen" data-accion="eliminar-imagen" data-id="${img.id}" title="Eliminar imagen">
                    <i class="bi bi-trash"></i>
                </button>
            </div>`).join('');
    }

    function eliminarImagen(id) {
        confirmarEliminar('Eliminar imagen', '¿Seguro que desea eliminar esta imagen?', async () => {
            try {
                await apiRequest(`/imagenes/${id}`, { method: 'DELETE' });
                showToast('Imagen eliminada correctamente');
                await renderImagenes(ordenActual.id);
            } catch (error) {
                showApiError(error);
            }
        });
    }

    function renderVerOrden(orden) {
        document.getElementById('ver-id').textContent = orden.id;
        document.getElementById('ver-cliente').textContent = `${orden.cliente.nombre} ${orden.cliente.apellido} (${orden.cliente.dni})`;
        document.getElementById('ver-vehiculo').textContent = `${orden.vehiculo.patente} - ${orden.vehiculo.marca.nombre} ${orden.vehiculo.modelo} (${orden.vehiculo.anio})${orden.vehiculo.color ? ` ${orden.vehiculo.color}` : ''}`;
        document.getElementById('ver-kilometraje').textContent = orden.kilometraje != null ? `${Number(orden.kilometraje).toLocaleString('es-AR')} km` : '-';
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

    async function refrescarOrdenActual() {
        const orden = await apiRequest(`/ordenes-trabajo/${ordenActual.id}/detalle`);
        ordenActual = orden;
        renderVerOrden(orden);
        await renderPagos(orden.id);
        await renderImagenes(orden.id);
    }

    async function verOrden(id) {
        try {
            const orden = await apiRequest(`/ordenes-trabajo/${id}/detalle`);
            ordenActual = orden;
            renderVerOrden(orden);
            await renderPagos(orden.id);
            await renderImagenes(orden.id);
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
            await refrescarOrdenActual();
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
                await refrescarOrdenActual();
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

    function mostrarInfoPago(orden) {
        deudaActual = Number(orden.deuda);
        pagoOrdenInfo.value = `N° ${orden.id} - ${orden.cliente.nombre} ${orden.cliente.apellido} (${orden.vehiculo.patente})`;
        infoPago.innerHTML = `
            <div class="row mb-0">
                <div class="col-4 text-muted small">Total</div>
                <div class="col-4 text-muted small">Pagado</div>
                <div class="col-4 text-muted small">Deuda</div>
                <div class="col-4 fw-bold">${formatMoneda(orden.total)}</div>
                <div class="col-4">${formatMoneda(orden.pagado)}</div>
                <div class="col-4 fw-bold text-danger">${formatMoneda(deudaActual)}</div>
            </div>`;
        infoPago.classList.remove('d-none');
    }

    function abrirNuevoPago() {
        if (!ordenActual) {
            return;
        }
        pagoEditandoId = null;
        montoPagoAnterior = 0;
        tituloPago.textContent = 'Nuevo pago';
        formPago.reset();
        inputPagoFecha.value = isoToLocal(new Date().toISOString());
        mostrarInfoPago(ordenActual);
        bootstrap.Modal.getOrCreateInstance(modalPago).show();
    }

    function abrirEdicionPago(pago) {
        if (!ordenActual) {
            return;
        }
        pagoEditandoId = pago.id;
        montoPagoAnterior = Number(pago.cantidadPagada);
        tituloPago.textContent = `Editar pago N° ${pago.id}`;
        inputPagoFecha.value = isoToLocal(pago.fechaPago);
        inputPagoMonto.value = pago.cantidadPagada;
        selectPagoMetodo.value = pago.metodoPago || 'EFECTIVO';
        mostrarInfoPago(ordenActual);
        bootstrap.Modal.getOrCreateInstance(modalPago).show();
    }

    function validarMontoPago() {
        const monto = Number(inputPagoMonto.value);
        const permitido = deudaActual + montoPagoAnterior;
        if (monto > permitido + 0.001) {
            showToast(`El monto no puede superar ${formatMoneda(permitido)}`, 'warning');
            return false;
        }
        return true;
    }

    async function guardarPago() {
        if (!validarMontoPago()) {
            return;
        }
        const payload = {
            fechaPago: localToIso(inputPagoFecha.value),
            cantidadPagada: Number(inputPagoMonto.value),
            metodoPago: selectPagoMetodo.value,
        };
        try {
            if (pagoEditandoId !== null) {
                await apiRequest(`/pagos/${pagoEditandoId}`, { method: 'PUT', body: payload });
                showToast('Pago actualizado correctamente');
            } else {
                payload.ordenTrabajoId = ordenActual.id;
                await apiRequest('/pagos', { method: 'POST', body: payload });
                showToast('Pago registrado correctamente');
            }
            bootstrap.Modal.getOrCreateInstance(modalPago).hide();
            formPago.reset();
            await refrescarOrdenActual();
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
                await refrescarOrdenActual();
                tabla.ajax.reload();
            } catch (error) {
                showApiError(error);
            }
        });
    }

    document.getElementById('btn-quitar-cliente').addEventListener('click', (event) => {
        event.preventDefault();
        limpiarClienteSeleccionado();
    });

    document.getElementById('btn-quitar-vehiculo').addEventListener('click', (event) => {
        event.preventDefault();
        limpiarVehiculoSeleccionado();
    });

    function debounce(funcion, demora) {
        let temporizador;
        return (...args) => {
            clearTimeout(temporizador);
            temporizador = setTimeout(() => funcion(...args), demora);
        };
    }

    function configurarAutocompletado({ input, contenedor, buscar, formatear, alSeleccionar }) {
        let elementos = [];
        let indiceActivo = -1;

        const ocultar = () => {
            contenedor.classList.add('d-none');
            contenedor.innerHTML = '';
            elementos = [];
            indiceActivo = -1;
        };

        const renderizar = () => {
            contenedor.innerHTML = elementos.map((elemento, i) => `
                <button type="button" class="list-group-item list-group-item-action${i === indiceActivo ? ' active' : ''}" data-indice="${i}">
                    ${formatear(elemento)}
                </button>`).join('');
            contenedor.classList.remove('d-none');
        };

        const seleccionar = (elemento) => {
            if (!elemento) {
                return;
            }
            ocultar();
            alSeleccionar(elemento);
        };

        const consultar = debounce(async () => {
            const texto = input.value.trim();
            if (!texto) {
                ocultar();
                return;
            }
            let resultados = [];
            try {
                resultados = await buscar(texto);
            } catch (error) {
                resultados = [];
            }
            elementos = resultados.slice(0, 8);
            indiceActivo = -1;
            if (elementos.length) {
                renderizar();
            } else {
                contenedor.innerHTML = '<span class="list-group-item text-muted">Sin coincidencias</span>';
                contenedor.classList.remove('d-none');
            }
        }, 300);

        input.addEventListener('input', () => consultar());

        contenedor.addEventListener('click', (event) => {
            const item = event.target.closest('[data-indice]');
            if (item) {
                seleccionar(elementos[Number(item.dataset.indice)]);
            }
        });

        input.addEventListener('keydown', (event) => {
            if (contenedor.classList.contains('d-none')) {
                return;
            }
            if (event.key === 'ArrowDown' && elementos.length) {
                indiceActivo = Math.min(indiceActivo + 1, elementos.length - 1);
                renderizar();
                event.preventDefault();
            } else if (event.key === 'ArrowUp' && elementos.length) {
                indiceActivo = Math.max(indiceActivo - 1, 0);
                renderizar();
                event.preventDefault();
            } else if (event.key === 'Enter') {
                if (indiceActivo >= 0 && elementos[indiceActivo]) {
                    seleccionar(elementos[indiceActivo]);
                    event.preventDefault();
                }
            } else if (event.key === 'Escape') {
                ocultar();
            }
        });

        document.addEventListener('click', (event) => {
            if (event.target !== input && !contenedor.contains(event.target)) {
                ocultar();
            }
        });

        return { ocultar };
    }

    function textoCliente(cliente) {
        return `${cliente.apellido}, ${cliente.nombre} (${cliente.dni})`;
    }

    function textoVehiculo(vehiculo) {
        return `${vehiculo.patente} - ${vehiculo.marca.nombre} ${vehiculo.modelo} (${vehiculo.anio})${vehiculo.color ? ` ${vehiculo.color}` : ''}`;
    }

    const sugerenciasCliente = configurarAutocompletado({
        input: inputBuscarCliente,
        contenedor: document.getElementById('sugerencias-cliente'),
        buscar: async (texto) => {
            const page = await apiRequest(`/clientes/buscar?texto=${encodeURIComponent(texto)}&size=8`);
            return page.content || [];
        },
        formatear: (cliente) => escapeHtml(`${cliente.apellido}, ${cliente.nombre} — ${cliente.dni}`),
        alSeleccionar: (cliente) => {
            clienteSeleccionadoId = cliente.id;
            inputBuscarCliente.value = textoCliente(cliente);
            mostrarClienteSeleccionado(`${cliente.nombre} ${cliente.apellido} (${cliente.dni})`);
        },
    });

    const sugerenciasVehiculo = configurarAutocompletado({
        input: inputBuscarVehiculo,
        contenedor: document.getElementById('sugerencias-vehiculo'),
        buscar: async (texto) => {
            const page = await apiRequest(`/vehiculos/buscar?texto=${encodeURIComponent(texto)}&size=8`);
            return page.content || [];
        },
        formatear: (vehiculo) => escapeHtml(`${vehiculo.patente} — ${vehiculo.marca.nombre} ${vehiculo.modelo} (${vehiculo.anio})`),
        alSeleccionar: (vehiculo) => {
            vehiculoSeleccionadoId = vehiculo.id;
            inputBuscarVehiculo.value = textoVehiculo(vehiculo);
            mostrarVehiculoSeleccionado(textoVehiculo(vehiculo));
        },
    });

    document.getElementById('buscar-orden-cliente').addEventListener('keyup', () => {
        const texto = document.getElementById('buscar-orden-cliente').value.trim();
        filtroClienteOrden = texto || null;
        if (filtroClienteOrden) {
            filtroPatenteOrden = null;
        }
        tabla.ajax.reload();
    });

    document.getElementById('buscar-orden-patente').addEventListener('keyup', () => {
        const patente = document.getElementById('buscar-orden-patente').value.trim();
        filtroPatenteOrden = patente || null;
        if (filtroPatenteOrden) {
            filtroClienteOrden = null;
        }
        tabla.ajax.reload();
    });

    formOrden.addEventListener('submit', (event) => {
        event.preventDefault();
        event.stopPropagation();
        formOrden.classList.add('was-validated');
        if (!formOrden.checkValidity()) {
            return;
        }
        if (clienteSeleccionadoId === null) {
            showToast('Debe buscar y seleccionar un cliente', 'warning');
            return;
        }
        if (vehiculoSeleccionadoId === null) {
            showToast('Debe buscar y seleccionar un vehículo', 'warning');
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

    formPago.addEventListener('submit', (event) => {
        event.preventDefault();
        event.stopPropagation();
        formPago.classList.add('was-validated');
        if (!formPago.checkValidity()) {
            return;
        }
        guardarPago();
    });

    document.getElementById('btn-nueva-orden').addEventListener('click', abrirNuevaOrden);
    document.getElementById('btn-agregar-detalle').addEventListener('click', () => abrirModalDetalle(null));
    document.getElementById('btn-cambiar-estado').addEventListener('click', abrirModalEstado);
    document.getElementById('btn-guardar-estado').addEventListener('click', guardarEstado);
    document.getElementById('btn-nuevo-pago').addEventListener('click', abrirNuevoPago);

    document.getElementById('btn-exportar-pdf').addEventListener('click', async () => {
        if (!ordenActual) {
            return;
        }
        try {
            await abrirPdf(`/ordenes-trabajo/${ordenActual.id}/pdf`);
        } catch (error) {
            showApiError(error);
        }
    });

    inputImagenesOrden.addEventListener('change', () => {
        const archivos = Array.from(inputImagenesOrden.files || []);
        if (!archivos.length) {
            return;
        }
        if (archivos.length > 6) {
            showToast('Máximo 6 imágenes por orden', 'warning');
            inputImagenesOrden.value = '';
            return;
        }
        if (archivos.some((archivo) => archivo.size > 5 * 1024 * 1024)) {
            showToast('Alguna imagen supera los 5 MB', 'warning');
            inputImagenesOrden.value = '';
        }
    });

    btnAgregarImagenes.addEventListener('click', () => inputImagenesVer.click());

    inputImagenesVer.addEventListener('change', async () => {
        const archivos = Array.from(inputImagenesVer.files || []);
        if (!archivos.length) {
            return;
        }
        if (imagenesActuales.length + archivos.length > 6) {
            showToast(`Máximo 6 imágenes (actuales: ${imagenesActuales.length})`, 'warning');
            inputImagenesVer.value = '';
            return;
        }
        if (archivos.some((archivo) => archivo.size > 5 * 1024 * 1024)) {
            showToast('Alguna imagen supera los 5 MB', 'warning');
            inputImagenesVer.value = '';
            return;
        }
        try {
            await subirImagenes(ordenActual.id, archivos);
            showToast('Imágenes agregadas correctamente');
            inputImagenesVer.value = '';
            await renderImagenes(ordenActual.id);
        } catch (error) {
            showApiError(error);
        }
    });

    contenedorImagenes.addEventListener('click', (event) => {
        const elemento = event.target.closest('[data-accion]');
        if (!elemento) {
            return;
        }
        if (elemento.dataset.accion === 'ver-imagen') {
            window.open(elemento.dataset.ruta, '_blank');
        } else if (elemento.dataset.accion === 'eliminar-imagen') {
            eliminarImagen(Number(elemento.dataset.id));
        }
    });

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

    $('#tabla-pagos-body').on('click', 'button[data-accion]', function () {
        const id = Number(this.dataset.id);
        const accion = this.dataset.accion;
        if (accion === 'editar-pago') {
            const pago = pagosActuales.find((p) => p.id === id);
            if (pago) {
                abrirEdicionPago(pago);
            }
        } else if (accion === 'eliminar-pago') {
            eliminarPago(id);
        }
    });

    tabla = $('#tabla-ordenes').DataTable({
        serverSide: true,
        processing: true,
        searching: false,
        ajax: (data, callback) => {
            const responder = (json) => callback({
                draw: data.draw,
                recordsTotal: json.totalElements,
                recordsFiltered: json.totalElements,
                data: json.content || [],
            });
            let ruta = '/ordenes-trabajo';
            const params = { page: data.start / data.length, size: data.length };
            if (filtroClienteOrden) {
                ruta = '/ordenes-trabajo/buscar/cliente';
                params.texto = filtroClienteOrden;
            } else if (filtroPatenteOrden) {
                ruta = '/ordenes-trabajo/buscar/patente-vehiculo';
                params.texto = filtroPatenteOrden;
            }
            if (data.order && data.order.length) {
                params.sort = `${data.columns[data.order[0].column].data},${data.order[0].dir}`;
            }
            $.ajax({
                url: `${API_BASE_URL}${ruta}`,
                method: 'GET',
                dataType: 'json',
                data: params,
                success: responder,
                error: (xhr) => {
                    mostrarErrorTabla(xhr);
                    responder({ content: [], totalElements: 0 });
                },
            });
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
                render: (data) => `${escapeHtml(data.patente)} - ${escapeHtml(data.marca.nombre)} ${escapeHtml(data.modelo)}${data.color ? ` (${escapeHtml(data.color)})` : ''}`,
            },
            {
                data: 'kilometraje',
                title: 'Km',
                orderable: false,
                render: (data) => (data != null ? `${Number(data).toLocaleString('es-AR')}` : '-'),
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
});
