package com.ElSauce.demo.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.ElSauce.demo.model.Reserva;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class PdfService {

    public ByteArrayInputStream generarPDFReserva(Reserva reserva) {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font texto = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("REPORTE DE RESERVA", titulo));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("ID de Reserva: " + reserva.getId(), texto));
            document.add(new Paragraph("Cliente: " + reserva.getClienteNombre() + " " + reserva.getClienteApellidos(), texto));
            document.add(new Paragraph("Correo:"+reserva.getClienteEmail(),texto));
            document.add(new Paragraph("Teléfono: " + reserva.getClienteTelefono(), texto));
            document.add(new Paragraph("Fecha de reserva: " + reserva.getFechaReserva(), texto));
            document.add(new Paragraph("Hora: " + reserva.getHoraReserva(), texto));
            document.add(new Paragraph("Personas: " + reserva.getPersonas(), texto));

            if (reserva.getMesa() != null) {
                document.add(new Paragraph("Mesa: " + reserva.getMesa().getNumero(), texto));
            }

            if (reserva.getZona() != null) {
                document.add(new Paragraph("Zona: " + reserva.getZona().getNombre(), texto));
            }

            document.add(new Paragraph("Estado: " + reserva.getEstado(), texto));

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}     
