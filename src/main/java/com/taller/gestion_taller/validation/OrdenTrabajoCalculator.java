package com.taller.gestion_taller.validation;

import com.taller.gestion_taller.entity.DetalleOrdenTrabajo;
import com.taller.gestion_taller.entity.OrdenTrabajo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class OrdenTrabajoCalculator {

    public BigDecimal calcularSubtotal(Integer cantidad, BigDecimal precioUnitario) {
        return precioUnitario
                .multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void recalcularTotales(OrdenTrabajo ordenTrabajo) {
        ordenTrabajo.getDetalles().forEach(detalle ->
                detalle.setSubtotal(calcularSubtotal(detalle.getCantidad(), detalle.getPrecioUnitario())));

        BigDecimal total = ordenTrabajo.getDetalles().stream()
                .map(DetalleOrdenTrabajo::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        ordenTrabajo.setTotal(total);
    }

    public BigDecimal calcularDeuda(OrdenTrabajo ordenTrabajo) {
        BigDecimal pagado = ordenTrabajo.getPagado() == null ? BigDecimal.ZERO : ordenTrabajo.getPagado();
        return ordenTrabajo.getTotal()
                .subtract(pagado)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
