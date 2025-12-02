package com.ElSauce.demo.service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


import org.springframework.stereotype.Service;

import com.ElSauce.demo.model.Reserva;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class PdfService {

    // Colores corporativos (puedes ajustar el RGB)
    private static final Color COLOR_PRIMARIO = new Color(34, 139, 34); // Un verde tipo "Sauce"
    private static final Color COLOR_FONDO_GRIS = new Color(245, 245, 245);

    public ByteArrayInputStream generarPDFReserva(Reserva reserva) {

        // Usamos márgenes estrechos para simular un ticket/boleta
        Document document = new Document(com.lowagie.text.PageSize.A4, 30, 30, 30, 30);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // --- FUENTES ---
            Font fontEmpresa = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
            Font fontDireccion = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);
            Font fontTituloSeccion = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, COLOR_PRIMARIO);
            Font fontLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.DARK_GRAY); // Fuente más pequeña
            Font fontValue = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK); // Fuente más pequeña
            Font fontMonto = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);

            // ==========================================
            // 1. ENCABEZADO (Logo + Datos Empresa)
            // ==========================================
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);

            // A. LOGO
            try {
                // Ajusta la ruta a donde tengas tus imagenes
                Image logo = Image.getInstance("demo\\src\\main\\resources\\static\\imgs\\logo_el_sauce.png");
                logo.scaleToFit(80, 80); 
                logo.setAlignment(Element.ALIGN_CENTER);
                
                PdfPCell cellLogo = new PdfPCell(logo);
                cellLogo.setBorder(Rectangle.NO_BORDER);
                cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellLogo.setPaddingBottom(10);
                headerTable.addCell(cellLogo);
            } catch (Exception e) { 
                /* Si falla, no rompemos el PDF */ 
            }

            // B. NOMBRE EMPRESA
            PdfPCell cellNombre = new PdfPCell(new Phrase("RESTAURANTE EL SAUCE", fontEmpresa));
            cellNombre.setBorder(Rectangle.NO_BORDER);
            cellNombre.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.addCell(cellNombre);

            // C. DIRECCIÓN
            PdfPCell cellDir = new PdfPCell(new Phrase("Av. Leticia 324, Sauce, San Martín", fontDireccion));
            cellDir.setBorder(Rectangle.NO_BORDER);
            cellDir.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellDir.setPaddingBottom(5);
            headerTable.addCell(cellDir);

            // D. CONTACTO CON ÍCONOS
            PdfPTable contactoTable = new PdfPTable(1); 
            contactoTable.setWidthPercentage(100);
            
            String pathPhone = "src/main/resources/static/images/ico_phone.png";
            String pathMail  = "src/main/resources/static/images/ico_email.png";
            
            contactoTable.addCell(crearCeldaConIcono(pathPhone, "+51 987 654 321", fontDireccion));
            contactoTable.addCell(crearCeldaConIcono(pathMail, "restaurante_el_sauce@gmail.com", fontDireccion));
            
            headerTable.addCell(contactoTable); 

            document.add(headerTable);
            document.add(new Paragraph("\n"));
            
            drawDashedLine(document);

            // ==========================================
            // 2. DATOS DE LA RESERVA (2 COLUMNAS DE INFORMACIÓN)
            // ==========================================
            // Usamos 4 columnas: [Label] [Valor] [Label] [Valor]
            PdfPTable tablaDatos = new PdfPTable(4); 
            tablaDatos.setWidthPercentage(100);
            // Definimos el ancho: Label (chico), Valor (mediano), Label (chico), Valor (mediano)
            tablaDatos.setWidths(new float[]{0.8f, 1.2f, 0.8f, 1.2f}); 
            tablaDatos.setSpacingBefore(10);
            
            // Título de sección
            PdfPCell cellTitReserva = new PdfPCell(new Phrase("DATOS DE RESERVA", fontTituloSeccion));
            cellTitReserva.setColspan(4); // Ocupa las 4 columnas
            cellTitReserva.setBorder(Rectangle.NO_BORDER);
            cellTitReserva.setPaddingBottom(5);
            tablaDatos.addCell(cellTitReserva);
            
            // FILA 1: ID & Cliente
            addCompactRow(tablaDatos, 
                "NRO TICKET:", String.format("#%06d", reserva.getId()), 
                "CLIENTE:", reserva.getClienteNombre() + " " + reserva.getClienteApellidos(),
                fontLabel, fontValue);

            // FILA 2: Fecha & Hora
            addCompactRow(tablaDatos, 
                "FECHA:", reserva.getFechaReserva().toString(), 
                "HORA:", reserva.getHoraReserva().toString(), 
                fontLabel, fontValue);

            // FILA 3: Personas & Estado
            String estadoStr = (reserva.getEstado() != null) ? reserva.getEstado().toString() : "PENDIENTE";
            addCompactRow(tablaDatos, 
                "PERSONAS:", String.valueOf(reserva.getPersonas()), 
                "ESTADO:", estadoStr,
                fontLabel, fontValue);
            
            // FILA 4: Mesa/Zona (Si existen, usamos 2 columnas completas)
            String mesaStr = (reserva.getMesa() != null) ? reserva.getMesa().getNumero() : "-";
            String zonaStr = (reserva.getZona() != null) ? reserva.getZona().getNombre() : "-";
            
            // Añadir Mesa/Zona usando la tabla de 4 columnas
            PdfPCell cellMesaLabel = new PdfPCell(new Phrase("MESA/ZONA:", fontLabel));
            cellMesaLabel.setBorder(Rectangle.NO_BORDER);
            cellMesaLabel.setPaddingBottom(2);
            tablaDatos.addCell(cellMesaLabel);
            
            // Valor: combina las 3 celdas restantes
            PdfPCell cellMesaValue = new PdfPCell(new Phrase(mesaStr + " / " + zonaStr, fontValue));
            cellMesaValue.setColspan(3); 
            cellMesaValue.setBorder(Rectangle.NO_BORDER);
            cellMesaValue.setHorizontalAlignment(Element.ALIGN_LEFT); // Alineamos a la izquierda para que empiece justo después del label
            cellMesaValue.setPaddingBottom(2);
            tablaDatos.addCell(cellMesaValue);
            
            document.add(tablaDatos);
            document.add(new Paragraph("\n"));

            // ==========================================
            // 3. DETALLE DE PAGO (Estilo Caja)
            // ==========================================
            if (reserva.getPagos() != null) {
                PdfPTable tablaPago = new PdfPTable(2);
                tablaPago.setWidthPercentage(100);
                
                // Header gris
                PdfPCell headerPago = new PdfPCell(new Phrase("DETALLE DE PAGO", fontLabel));
                headerPago.setColspan(2);
                headerPago.setBackgroundColor(COLOR_FONDO_GRIS);
                headerPago.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                headerPago.setPadding(6);
                tablaPago.addCell(headerPago);

                // Info
                addCleanRow(tablaPago, "Método/ID:", reserva.getPagos().getIdTransaccion(), fontLabel, fontValue);
                
                // Total Grande
                PdfPCell celdaTotalLabel = new PdfPCell(new Phrase("TOTAL", fontLabel));
                celdaTotalLabel.setBorder(Rectangle.TOP);
                celdaTotalLabel.setPaddingTop(10);
                tablaPago.addCell(celdaTotalLabel);

                PdfPCell celdaTotalValue = new PdfPCell(new Phrase("S/ " + reserva.getPagos().getMonto(), fontMonto));
                celdaTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                celdaTotalValue.setBorder(Rectangle.TOP);
                celdaTotalValue.setPaddingTop(10);
                tablaPago.addCell(celdaTotalValue);

                document.add(tablaPago);
            }

            // ==========================================
            // 4. FOOTER CON WHATSAPP
            // ==========================================
            document.add(new Paragraph("\n\n"));
            drawDashedLine(document);
            
            PdfPTable footerTable = new PdfPTable(1);
            footerTable.setWidthPercentage(100);
            
            String pathWsp = "src/main/resources/static/images/ico_whatsapp.png";
            PdfPCell cellWsp = crearCeldaConIcono(pathWsp, "Contáctanos al WhatsApp: 987 654 321", fontDireccion);
            cellWsp.setPaddingTop(10);
            footerTable.addCell(cellWsp);

            PdfPCell cellGracias = new PdfPCell(new Phrase("¡Gracias por su preferencia!", fontTituloSeccion));
            cellGracias.setBorder(Rectangle.NO_BORDER);
            cellGracias.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellGracias.setPaddingTop(5);
            footerTable.addCell(cellGracias);

            document.add(footerTable);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // -----------------------------------------------------------
    // MÉTODOS AUXILIARES (ACTUALIZADOS)
    // -----------------------------------------------------------

    /**
     * Crea una celda que contiene un icono pequeño seguido de texto, centrados.
     */
    private PdfPCell crearCeldaConIcono(String imagePath, String texto, Font fuente) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); 
        
        Paragraph p = new Paragraph();
        p.setAlignment(Element.ALIGN_CENTER);
        
        try {
            Image icon = Image.getInstance(imagePath);
            icon.scaleToFit(10, 10);
            
            Chunk chunkIcon = new Chunk(icon, 0, -1);
            p.add(chunkIcon);
            p.add(new Chunk("  "));
            p.add(new Phrase(texto, fuente));
            
        } catch (Exception e) {
            p.add(new Phrase(texto, fuente));
        }
        
        cell.addElement(p);
        return cell;
    }

    /**
     * Agrega una fila de datos que usa 2 columnas de la tabla PDF. (Ej: Label | Valor)
     * Utilizado para la sección de Pago donde queremos que ocupe el ancho completo.
     */
    private void addCleanRow(PdfPTable table, String label, String value, Font fLabel, Font fValue) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, fLabel));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPaddingBottom(2); // Menor padding
        
        PdfPCell c2 = new PdfPCell(new Phrase(value != null ? value : "", fValue));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c2.setPaddingBottom(2); // Menor padding
        
        table.addCell(c1);
        table.addCell(c2);
    }
    
    /**
     * Agrega 4 celdas a la tabla PDF, organizando 2 datos en una sola fila.
     * [Label 1] [Valor 1] [Label 2] [Valor 2]
     */
    private void addCompactRow(PdfPTable table, 
                               String label1, String value1, 
                               String label2, String value2, 
                               Font fLabel, Font fValue) {
        
        // Celda 1 (Label 1)
        PdfPCell c1 = new PdfPCell(new Phrase(label1, fLabel));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPaddingBottom(2);
        table.addCell(c1);

        // Celda 2 (Valor 1 - Derecha)
        PdfPCell c2 = new PdfPCell(new Phrase(value1 != null ? value1 : "", fValue));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setHorizontalAlignment(Element.ALIGN_LEFT); // Alineamos a la izquierda para que quede compacto
        c2.setPaddingBottom(2);
        table.addCell(c2);

        // Celda 3 (Label 2)
        PdfPCell c3 = new PdfPCell(new Phrase(label2, fLabel));
        c3.setBorder(Rectangle.NO_BORDER);
        c3.setPaddingBottom(2);
        table.addCell(c3);

        // Celda 4 (Valor 2 - Derecha)
        PdfPCell c4 = new PdfPCell(new Phrase(value2 != null ? value2 : "", fValue));
        c4.setBorder(Rectangle.NO_BORDER);
        c4.setHorizontalAlignment(Element.ALIGN_LEFT); // Alineamos a la izquierda para que quede compacto
        c4.setPaddingBottom(2);
        table.addCell(c4);
    }
    
    private void drawDashedLine(Document document) throws DocumentException {
        Paragraph p = new Paragraph("----------------------------------------------------------------------------------------------------------------");
        p.setAlignment(Element.ALIGN_CENTER);
        p.getFont().setSize(7);
        p.getFont().setColor(Color.LIGHT_GRAY);
        document.add(p);
    }
}