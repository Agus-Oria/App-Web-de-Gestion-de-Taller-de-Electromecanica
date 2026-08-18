package com.taller.gestion_taller.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.taller.gestion_taller.entity.OrdenTrabajo;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenTrabajoPdfService {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("es", "AR"));

    private final OrdenTrabajoService ordenTrabajoService;
    private final ConfiguracionService configuracionService;

    public OrdenTrabajoPdfService(
            OrdenTrabajoService ordenTrabajoService,
            ConfiguracionService configuracionService) {
        this.ordenTrabajoService = ordenTrabajoService;
        this.configuracionService = configuracionService;
    }

    @Transactional(readOnly = true)
    public byte[] generarPdf(Long id) {
        OrdenTrabajo ordenTrabajo = ordenTrabajoService.buscarEntidadConDetalles(id);
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, salida);
            document.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL);
            Font negrita = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);
            Font encabezadoTabla = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);

            Paragraph taller = new Paragraph(configuracionService.obtenerNombreTaller(), titulo);
            taller.setAlignment(Element.ALIGN_CENTER);
            document.add(taller);

            Paragraph tituloOrden = new Paragraph("Orden de trabajo N° " + ordenTrabajo.getId(), normal);
            tituloOrden.setAlignment(Element.ALIGN_CENTER);
            document.add(tituloOrden);

            Paragraph fecha = new Paragraph(
                    "Fecha: " + LocalDate.now(ZoneOffset.UTC).format(FORMATO_FECHA), normal);
            fecha.setAlignment(Element.ALIGN_RIGHT);
            document.add(fecha);

            document.add(new Paragraph(" ", normal));

            Paragraph datos = new Paragraph();
            datos.add(new Phrase("Cliente: ", negrita));
            datos.add(new Phrase(ordenTrabajo.getCliente().getNombre() + " "
                    + ordenTrabajo.getCliente().getApellido()
                    + " (DNI: " + ordenTrabajo.getCliente().getDni() + ")", normal));
            datos.add(new Phrase("\nVehículo: ", negrita));
            datos.add(new Phrase(ordenTrabajo.getVehiculo().getPatente() + " - "
                    + ordenTrabajo.getVehiculo().getMarca().getNombre() + " "
                    + ordenTrabajo.getVehiculo().getModelo() + " ("
                    + ordenTrabajo.getVehiculo().getAnio() + ")", normal));
            document.add(datos);

            PdfPTable tabla = new PdfPTable(3);
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(12);
            tabla.setSpacingAfter(12);

            PdfPCell celdaDescripcion = new PdfPCell(new Phrase("Descripción", encabezadoTabla));
            PdfPCell celdaCantidad = new PdfPCell(new Phrase("Cantidad", encabezadoTabla));
            celdaCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell celdaPrecio = new PdfPCell(new Phrase("Precio unitario", encabezadoTabla));
            celdaPrecio.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tabla.addCell(celdaDescripcion);
            tabla.addCell(celdaCantidad);
            tabla.addCell(celdaPrecio);

            for (var detalle : ordenTrabajo.getDetalles()) {
                PdfPCell celdaDesc = new PdfPCell(new Phrase(detalle.getDescripcion(), normal));
                PdfPCell celdaCant = new PdfPCell(new Phrase(String.valueOf(detalle.getCantidad()), normal));
                celdaCant.setHorizontalAlignment(Element.ALIGN_CENTER);
                PdfPCell celdaPrecioDetalle = new PdfPCell(
                        new Phrase(formatearMoneda(detalle.getPrecioUnitario()), normal));
                celdaPrecioDetalle.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tabla.addCell(celdaDesc);
                tabla.addCell(celdaCant);
                tabla.addCell(celdaPrecioDetalle);
            }
            document.add(tabla);

            Paragraph total = new Paragraph("Total: " + formatearMoneda(ordenTrabajo.getTotal()), negrita);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();
        } catch (Exception exception) {
            throw new IllegalStateException("Error al generar el PDF de la orden de trabajo", exception);
        }
        return salida.toByteArray();
    }

    private String formatearMoneda(BigDecimal monto) {
        return "$ " + String.format(new Locale("es", "AR"), "%,.2f", monto);
    }
}