(function () {
    const paginas = [
        { href: 'index.html', label: 'Inicio', icon: 'bi-house-door' },
        { href: 'clientes.html', label: 'Clientes', icon: 'bi-people' },
        { href: 'vehiculos.html', label: 'Vehículos', icon: 'bi-car-front' },
        { href: 'ordenes.html', label: 'Órdenes de trabajo', icon: 'bi-tools' },
        { href: 'pagos.html', label: 'Pagos', icon: 'bi-cash-coin' },
    ];
    const actual = window.location.pathname.split('/').pop() || 'index.html';
    const contenedor = document.getElementById('app-navbar');
    if (!contenedor) {
        return;
    }
    contenedor.innerHTML = `
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
            <div class="container-fluid">
                <a class="navbar-brand" href="index.html"><i class="bi bi-wrench-adjustable me-2"></i>Gestión Taller</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navPrincipal" aria-controls="navPrincipal" aria-expanded="false" aria-label="Abrir menú">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navPrincipal">
                    <ul class="navbar-nav">
                        ${paginas.map((p) => `
                            <li class="nav-item">
                                <a class="nav-link ${p.href === actual ? 'active' : ''}" href="${p.href}">
                                    <i class="bi ${p.icon} me-1"></i>${p.label}
                                </a>
                            </li>`).join('')}
                    </ul>
                </div>
            </div>
        </nav>`;
})();
