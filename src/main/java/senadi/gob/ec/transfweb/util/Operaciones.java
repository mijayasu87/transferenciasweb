/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author Michael Yanangómez
 */
public class Operaciones {
    
    //produccion
    public static String user = "root";
    public static String pass = "B8GJuaxu4Y:2020";
    public static String basd = "senadi_transferencia";
    public static String host = "10.0.20.140";

    //localhost
//    public static String user = "root";
//    public static String pass = "MichaRoot6*";
//    public static String basd = "senadi_transferencia";
//    public static String host = "localhost";
    
    //Producción    
    public static String USER = "iepi-solicitudes";
    public static String PASSWORD = "5ad0d5c3fced39d5048f";
    public static String iepi_formularios = "jdbc:mysql://10.0.20.130:3306/iepi_formularios";   
    public static String iepi_depurar = "jdbc:mysql://10.0.20.130:3306/iepi_depurar";
    public static String iepi_casilleros = "jdbc:mysql://10.0.20.130:3306/iepi_casilleros";
    public static String iepi_admin = "jdbc:mysql://10.0.20.130:3306/iepi_admin";

    //prueba
//    public static String USER = "iepi-solicitudes";
//    public static String PASSWORD = "5ad0d5c3fced39d5048f";
//    public static String iepi_formularios = "jdbc:mysql://10.0.26.130:3306/iepi_formularios";
//    public static String iepi_depurar = "jdbc:mysql://10.0.26.130:3306/iepi_depurar";
//    public static String iepi_casilleros = "jdbc:mysql://10.0.26.130:3306/iepi_casilleros";
//    public static String iepi_admin = "jdbc:mysql://10.0.26.130:3306/iepi_admin";
    
    public static String RUTA_RENEWAL = "https://registro.propiedadintelectual.gob.ec/solicitudes/media/files/renewal_forms/";    

    

    public static String getCurrentTimeStamp() {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(dt);
    }

    public static String getGivenTimeStamp(Timestamp time) {
        Date dt = new Date(time.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(dt);
    }

    /*Pasa un String Ej: '2020-05-15' a java.util.Date */
    public static Date convertStringToDate(String fec) {
        String an = fec.substring(0, fec.indexOf("-"));
        String aux = fec.substring(fec.indexOf("-") + 1);

        int a = Integer.parseInt(an);
        int m = Integer.parseInt(aux.substring(0, aux.indexOf("-")));
        int d = Integer.parseInt(aux.substring(aux.indexOf("-") + 1));

        return new Date(a - 1900, m - 1, d);
    }

    /**
     * Da formato a la fecha recibida en el siguiente orden 'yyyy-mm-dd'
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
//        System.out.println("--> "+date.toString());
        int dia = date.getDate();
        int mes = date.getMonth() + 1;
        int año = date.getYear() + 1900;
        String d = dia + "";
        String m = mes + "";
        if (dia < 10) {
            d = "0" + dia;
        }
        if (mes < 10) {
            m = "0" + mes;
        }

        String fecha = año + "-" + m + "-" + d;
//        System.out.println("<-- "+fecha+"\n");
        return fecha;
    }

    /**
     * Da formato a una fecha recibida, en el siguiente orden 'Dddddd dd de
     * Mmmmm de yyyy'
     */
    public static String formatDateToLarge(Date fecha) {

        Date aux = fecha;

//        aux.setDate(aux.getDate()+1);
        int dia = aux.getDate();
        int mes = aux.getMonth();
        int año = aux.getYear() + 1900;
//        int diasem = fecha.getDay();
        String fec = dia + " de " + getMes(mes) + " de " + año;  //getDia(diasem) + " " + 
        return fec.toLowerCase();
    }

    public static String getDia(int dia) {
        if (dia == 1) {
            return "Lunes";
        } else if (dia == 2) {
            return "Martes";
        } else if (dia == 3) {
            return "Miércoles";
        } else if (dia == 4) {
            return "Jueves";
        } else if (dia == 5) {
            return "Viernes";
        } else if (dia == 6) {
            return "Sábado";
        } else if (dia == 0) {
            return "Domingo";
        } else {
            return "Error";
        }
    }

    public static String getMes(int mes) {
        if (mes == 0) {
            return "Enero";
        } else if (mes == 1) {
            return "Febrero";
        } else if (mes == 2) {
            return "Marzo";
        } else if (mes == 3) {
            return "Abril";
        } else if (mes == 4) {
            return "Mayo";
        } else if (mes == 5) {
            return "Junio";
        } else if (mes == 6) {
            return "Julio";
        } else if (mes == 7) {
            return "Agosto";
        } else if (mes == 8) {
            return "Septiembre";
        } else if (mes == 9) {
            return "Octubre";
        } else if (mes == 10) {
            return "Noviembre";
        } else if (mes == 11) {
            return "Diciembre";
        } else {
            return "Error";
        }
    }

    /* Retorna un hash MD5 a partir de un texto */
    public static String md5(String txt) {
        return getHash(txt, "MD5");
    }

    /* Retorna un hash a partir de un tipo y un texto */
    public static String getHash(String txt, String hashType) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance(hashType);
            byte[] array = md.digest(txt.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static List<String> getRazonesNotificar() {
        List<String> razones = new ArrayList<>();
        razones.add("Titular no corresponde. El Cedente no coincide con el titular actual del signo distintivo.");
        razones.add("Adjuntar el poder vigente, debidamente legalizado por la autoridad competente, de acuerdo al art. 152 del Código Orgánico Administrativo COA.");
        razones.add("Adjuntar el contrato de cesión donde se indentifique la marca a transferir, con el debido reconocimiento de firmas entre cedente y cesionario.");
        razones.add("Adjuntar ratificación del abogado patrocinador, en la que conste la firma del titular o representante de la marca, de acuerdo al art. 152 del Código Orgánico Administrativo COA.");
        razones.add("Número de titulo no corresponde.");
        razones.add("Adjuntar nombramiento del representante legal vigente, debidamente legalizado por la autoridad competente, de acuerdo al art. 152 del Código Orgánico Administrativo COA.");
        razones.add("Adjuntar el Anexo  que se hace referencia en el contrato, puesto que es necesario el mismo para determinar la marca y el signo a transferir.");
        razones.add("Adjuntar el certificado hasta la fecha que se ingreso la solicitud de transferencia, que avala el descuento aplicado al pago de la tasa, emitido por el Servicio Nacional de Derechos Intelectuales (SENADI).");

        return razones;
    }

    public static List<String> getTiposModificacion() {
        List<String> modificaciones = new ArrayList<>();
        modificaciones.add("Transferencias");
        modificaciones.add("Cambios de Nombre");
        modificaciones.add("Cambios de Domicilio");
        modificaciones.add("Prendas Comerciales");
        modificaciones.add("Licencias de Uso");
        modificaciones.add("Sublicencias de Uso");
        return modificaciones;

    }

    public static String getTramiteFromPdfName(String pdfName) {
        String tramite = "";
        for (int i = 0; i < pdfName.length(); i++) {
            if (pdfName.charAt(i) == '_') {
                tramite = pdfName.substring(0, i);
                break;
            }
        }
        return tramite;
    }

    public static boolean copyFile(String fileName, InputStream stream, String rutaCarpeta) {
        try {
            // write the inputStream to a FileOutputStream
            String nombreDoc = fileName;
            String rutaCompleta = rutaCarpeta + nombreDoc;
            removeSimilarFiles(nombreDoc, rutaCarpeta);
//            System.out.println("rutacompleta: "+rutaCarpeta);
            FileOutputStream fichero = new FileOutputStream(rutaCompleta);
            // Lectura de la url de la web y escritura en fichero local
            byte[] buffer = new byte[1024]; // buffer temporal de lectura.
            int readed = stream.read(buffer);
            while (readed > 0) {
                fichero.write(buffer, 0, readed);
                readed = stream.read(buffer);
            }
            // cierre de conexion y fichero.
            stream.close();
            fichero.close();
            return true;
//            System.out.println("New file uploaded: " + (rutaCarpeta + fileName));
        } catch (IOException e) {
            System.out.println("Error al guardar documento: " + e.getMessage());
            return false;
        }
    }

    public static boolean removeSimilarFiles(String logo, String rutaCarpeta) {
        String[] extensiones = {".pdf"};

        boolean aviso = false;
        for (String extension : extensiones) {
            File file = new File(rutaCarpeta + logo + extension);
            if (file.exists()) {
                file.delete();
                aviso = true;
            }
        }

        return aviso;
    }

    public static int validaTextoEnPdf(String url, String textoEnPdf) {
        PDDocument pdDocument = null;

        try {
            URL ur = new URL(url);

            pdDocument = PDDocument.load(ur.openStream());

            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(5);
            String parsedText = pdfStripper.getText(pdDocument);

//            System.out.println("Texto:\n\n\n" + parsedText);
            if (parsedText.contains(textoEnPdf)) {
                return 1;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("a: Error leyendo documento: " + e);
            return -1;
        } finally {
            if (pdDocument != null) {
                try {
                    pdDocument.close();
                } catch (IOException e) {
                    System.out.println("b: Error leyendo documento: " + e);
                    return -1;
                }
            }
        }
        return 0;
    }

    public static boolean validarFecha(Date fecha) {
        try {
            fecha.toString();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /*diasemana: se refiere al día de la semana al cual se va a cambiar la fecha*/
    public static Date cambiarFechaADiaDado(Date fecha, int diasemana) {
//        System.out.println("dia-semana: " + fecha.getDay());
//        System.out.println("dia-mes: " + fecha.getDate());
//        System.out.println("mes: " + fecha.getMonth());
//        System.out.println("año: " + (fecha.getYear() + 1900));
//        System.out.println("fecha 1: " + fecha.toString());
//        System.out.println("-----------------");

        int diasem = fecha.getDay();
        if (diasem == diasemana) {
//            System.out.println("entra 1");
            fecha.setDate(fecha.getDate() + 7);
        } else if (diasemana < diasem) {
//            System.out.println("entra 2");
            fecha.setDate(fecha.getDate() + (7 - (diasem - diasemana)));
        } else {
//            System.out.println("entra 3");
            fecha.setDate(fecha.getDate() + (diasemana - diasem));
        }
//
//        System.out.println("dia-semana: " + fecha.getDay());
//        System.out.println("dia-mes: " + fecha.getDate());
//        System.out.println("mes: " + fecha.getMonth());
//        System.out.println("año: " + (fecha.getYear() + 1900));
//        System.out.println("fecha 2: " + fecha.toString());
        return fecha;
    }

    public static String getStringMes(int mes) {
        if (mes < 10) {
            return "0" + mes;
        } else {
            return mes + "";
        }
    }

    public static LocalDate calcularFechaLimiteExcluyendoFinesSemana(Date fechaInicio, int diasHabiles) {
        LocalDate fecha = fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int agregados = 0;
        while (agregados < diasHabiles) {
            fecha = fecha.plusDays(1);
            DayOfWeek dia = fecha.getDayOfWeek();
            if (dia != DayOfWeek.SATURDAY && dia != DayOfWeek.SUNDAY) {
                agregados++;
            }
        }
        return fecha;
    }

    public static LocalDate calcularFechaLimiteExcluyendoFinesSemana(int diasHabiles) {
        LocalDate fecha = LocalDate.now();
        int cont = 0;

        while (cont < diasHabiles) {
            fecha = fecha.minusDays(1);
            DayOfWeek diaSemana = fecha.getDayOfWeek();
            if (diaSemana != DayOfWeek.SATURDAY && diaSemana != DayOfWeek.SUNDAY) {
                cont++;
            }
        }

        return fecha;
    }

}
