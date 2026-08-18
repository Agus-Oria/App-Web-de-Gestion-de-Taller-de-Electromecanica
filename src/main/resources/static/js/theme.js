(function () {
    const KEY = 'gestion_taller_tema';

    function temaInicial() {
        const guardado = localStorage.getItem(KEY);
        if (guardado === 'light' || guardado === 'dark') {
            return guardado;
        }
        return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }

    function aplicar(tema) {
        document.documentElement.setAttribute('data-bs-theme', tema);
    }

    function obtener() {
        return document.documentElement.getAttribute('data-bs-theme') || 'light';
    }

    function alternar() {
        const nuevo = obtener() === 'dark' ? 'light' : 'dark';
        localStorage.setItem(KEY, nuevo);
        aplicar(nuevo);
        return nuevo;
    }

    aplicar(temaInicial());

    window.Tema = { aplicar, obtener, alternar };
})();