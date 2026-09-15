/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.util;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import senadi.gob.ec.transfweb.model.Abandono;
import senadi.gob.ec.transfweb.model.CambioCasillero;
import senadi.gob.ec.transfweb.model.Delegado;
import senadi.gob.ec.transfweb.model.Desistimiento;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.model.Prorroga;
import senadi.gob.ec.transfweb.model.RazonCorreccion;
import senadi.gob.ec.transfweb.model.Resolucion;
import senadi.gob.ec.transfweb.model.Transferencia;
import senadi.gob.ec.transfweb.model.cd.CambioDomicilio;
import senadi.gob.ec.transfweb.model.cn.CambioNombre;
import senadi.gob.ec.transfweb.model.licencia.LicenciaUso;
import senadi.gob.ec.transfweb.model.licencia.SubLicenciaUso;
import senadi.gob.ec.transfweb.model.prenda.PrendaComercial;

/**
 *
 * @author michael
 */
public class Report implements Serializable {

    private Connection conn;

    public Report() {
        try {
            //localhost
//            String user = "root";
//            String pass = "MichaRoot6*";
//            String basd = "senadi_transferencia";
//            String host = "localhost";

            //produccion
//            String user = "root";
//            String pass = "B8GJuaxu4Y:2020";
//            String basd = "senadi_transferencia";
//            String host = "10.0.20.140";
            Class.forName("com.mysql.jdbc.Driver"); //se carga el driver
            String url = "jdbc:mysql://" + Operaciones.host + "/" + Operaciones.basd + "?serverTimezone=GMT-5&autoReconnect=true&useSSL=false";
            conn = DriverManager.getConnection(url, Operaciones.user, Operaciones.pass);

        } catch (Exception ex) {
            System.out.println("Error conexion: " + ex);
            ex.printStackTrace();
        }
    }

    /*Cierra la conexión a la base de datos mysql*/
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewCambioDomicilioNotificadaMasterBytes(String path, InputStream rutaJrxml, CambioDomicilio cambio, String rutapdf,
            String resol, String fecha_resol, String resnot, String delegado, String delegacion, Delegado secretaria) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("id", cambio.getId());

            parametro.put("resoluciontransf", resol + ", de fecha " + fecha_resol);
            parametro.put("resolucionnot", resnot);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("casilla", cambio.getCasilleroSenadi() != null && !cambio.getCasilleroSenadi().trim().isEmpty() ? cambio.getCasilleroSenadi() : cambio.getCasilleroJudicial());

            parametro.put("SUBREPORT_DIR", path + "/");

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print notificación_cd separado: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewPrendaComercialNotificadaMasterBytes(String path, InputStream rutaJrxml, PrendaComercial prenda, String rutapdf,
            String resol, String resnot, String delegado, String delegacion, Delegado secretaria) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("id", prenda.getId());

            parametro.put("resolucionpren", resol);
            parametro.put("resolucionnot", resnot);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

            parametro.put("SUBREPORT_DIR", path + "/");

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print notificación_cn separado: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewSublicenciaUsoNotificadaMasterBytes(String path, InputStream rutaJrxml, SubLicenciaUso licencia, String rutapdf,
            String resol, String resnot, String delegado, String delegacion, Delegado secretaria) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("id", licencia.getId());

            parametro.put("resolucionlic", resol);
            parametro.put("resolucionnot", resnot);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

            parametro.put("SUBREPORT_DIR", path + "/");

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print notificación_sublicencia separado: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewLicenciaUsoNotificadaMasterBytes(String path, InputStream rutaJrxml, LicenciaUso licencia, String rutapdf,
            String resol, String resnot, String delegado, String delegacion, Delegado secretaria) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("id", licencia.getId());

            parametro.put("resolucionlic", resol);
            parametro.put("resolucionnot", resnot);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

            parametro.put("SUBREPORT_DIR", path + "/");

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print notificación_licencia separado: " + ex);
            return null;
        }
    }


    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewCambioNombreNotificadaMasterBytes(String path, InputStream rutaJrxml, CambioNombre cambio, String rutapdf,
            String resol, String fecha_resol, String resnot, String delegado, String delegacion, Delegado secretaria) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("id", cambio.getId());

            parametro.put("resoluciontransf", resol + ", de fecha " + fecha_resol);
            parametro.put("resolucionnot", resnot);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

            parametro.put("SUBREPORT_DIR", path + "/");

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print notificación_cn separado: " + ex);
            return null;
        }
    }

    public FileInputStream viewCambioDomicilioNotificada(String path, InputStream rutaJrxml, CambioDomicilio cambio, String rutapdf,
            String resol, String fecha_resol, String resnot, String delegado, String delegacion, Delegado secretaria) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("id", cambio.getId());

            parametro.put("resoluciontransf", resol + ", de fecha " + fecha_resol);
            parametro.put("resolucionnot", resnot);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("casilla", cambio.getCasilleroSenadi() != null && !cambio.getCasilleroSenadi().trim().isEmpty() ? cambio.getCasilleroSenadi() : cambio.getCasilleroJudicial());

            parametro.put("SUBREPORT_DIR", path + "/");

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);

                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                exporter.exportReport();

            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print notificada_cd: " + ex);
            return null;
        }
    }

    public FileInputStream viewPrendaComercialNotificada(String path, InputStream rutaJrxml, PrendaComercial prenda, String rutapdf,
            String resol, String resnot, String delegado, String delegacion, Delegado secretaria) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("id", prenda.getId());

            parametro.put("resolucionpren", resol);
            parametro.put("resolucionnot", resnot);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

            parametro.put("SUBREPORT_DIR", path + "/");

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);

                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                exporter.exportReport();

            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print notificada_prenda: " + ex);
            return null;
        }
    }

    public FileInputStream viewLicenciaUsoNotificada(String path, InputStream rutaJrxml, LicenciaUso licencia, String rutapdf,
            String resol, String resnot, String delegado, String delegacion, Delegado secretaria) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("id", licencia.getId());

            parametro.put("resolucionlic", resol);
            parametro.put("resolucionnot", resnot);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

            parametro.put("SUBREPORT_DIR", path + "/");

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);

                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                exporter.exportReport();

            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print notificada_licencia: " + ex);
            return null;
        }
    }

    public FileInputStream viewSublicenciaUsoNotificada(String path, InputStream rutaJrxml, SubLicenciaUso licencia, String rutapdf,
            String resol, String resnot, String delegado, String delegacion, Delegado secretaria) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("id", licencia.getId());

            parametro.put("resolucionlic", resol);
            parametro.put("resolucionnot", resnot);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

            parametro.put("SUBREPORT_DIR", path + "/");

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);

                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                exporter.exportReport();

            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print notificada_sublicencia: " + ex);
            return null;
        }
    }

    public FileInputStream viewCambioNombreNotificada(String path, InputStream rutaJrxml, CambioNombre cambio, String rutapdf,
            String resol, String fecha_resol, String resnot, String delegado, String delegacion, Delegado secretaria) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("id", cambio.getId());

            parametro.put("resoluciontransf", resol + ", de fecha " + fecha_resol);
            parametro.put("resolucionnot", resnot);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

            parametro.put("SUBREPORT_DIR", path + "/");

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);

                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                exporter.exportReport();

            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print notificada_cn: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewCambioDomicilioCertificadoMasterBytes(String path, InputStream rutaJrxml, CambioDomicilio cambio, String rutapdf,
            String resol, String fecha_resol, String delegado, String delegacion) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();

            parametro.put("title", "CERTIFICADO DE CAMBIO DE DOMICILIO No. " + cambio.getCertificado() + " - SENADI");
            parametro.put("resol", resol + ", de fecha " + fecha_resol);

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);

            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idCambioDomicilio", cambio.getId());

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print certificado_cd separado: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewCambioNombreCertificadoMasterBytes(String path, InputStream rutaJrxml, CambioNombre cambio, String rutapdf,
            String resol, String fecha_resol, String delegado, String delegacion) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();

            parametro.put("title", "CERTIFICADO DE CAMBIO DE NOMBRE DEL TITULAR No. " + cambio.getCertificado() + " - SENADI");
            parametro.put("resol", resol + ", de fecha " + fecha_resol);

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);

            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idCambioNombre", cambio.getId());

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print certificado_cn separado: " + ex);
            return null;
        }
    }

    public FileInputStream viewCambioDomicilioCertificado(String path, InputStream rutaJrxml, CambioDomicilio cambio, String rutapdf,
            String resol, String fecha_resol, String delegado, String delegacion) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();
            parametro.put("title", "CERTIFICADO DE CAMBIO DE DOMICILIO No. " + cambio.getCertificado() + " - SENADI");
            parametro.put("resol", resol + ", de fecha " + fecha_resol);

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);

            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idCambioDomicilio", cambio.getId());

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);

                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                exporter.exportReport();

            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print transferencia: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewAbandonoMasterBytes(String path, InputStream rutaJrxml, Abandono abandono,
            String delegado, String delegacion, Delegado secretaria) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", abandono.getId());

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print abandono separado: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewCaducadaMasterBytes(String path, InputStream rutaJrxml, Date fechaPresentacion, Integer idCaducada,
            String delegado, String delegacion, Delegado secretaria, String tipoTramite, String resnot, String fecharesnot) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {

            String tercero;
            String cuarto;
            String resuelve;
            Date date = new Date(2016 - 1900, 11, 9);
            if (fechaPresentacion.before(date)) {
                tercero = "La solicitud fue ingresada por la parte interesada, acompañada "
                        + "de documentación para su análisis dentro del procedimiento administrativo correspondiente y, realizado el examen "
                        + "preliminar de la documentación aportada, se procedió a verificar el cumplimiento de los requisitos formales y "
                        + "sustanciales establecidos en la Ley de Propiedad Intelectual y su Reglamento General.";
                cuarto = "En el presente caso, de la revisión efectuada a la documentación ingresada, no se "
                        + "ha verificado el cumplimiento de lo dispuesto en la Ley de Propiedad Intelectual "
                        + "ni en su Reglamento, lo cual impide que la administración corrobore el cumplimiento "
                        + "de los requisitos necesarios para la continuación o admisión del trámite.";
                resuelve = "El presente acto administrativo es susceptible de los recursos administrativos establecidos en el Art. 357 "
                        + "de la Ley de Propiedad Intelectual; o por vía jurisdiccional ante uno de los Tribunales Distritales de lo "
                        + "Contencioso Administrativo.";
            } else {
                tercero = "La solicitud fue ingresada por la parte interesada, acompañada de documentación "
                        + "para su análisis dentro del procedimiento administrativo correspondiente y, realizado "
                        + "el examen preliminar de la documentación aportada, se procedió a verificar el cumplimiento "
                        + "de los requisitos formales y sustanciales establecidos en el Código Orgánico de la "
                        + "Economía Social de los Conocimientos, Creatividad e Innovación.";
                cuarto = "En el presente caso, de la revisión efectuada a la documentación ingresada, "
                        + "no se ha verificado el cumplimiento de lo dispuesto en la Código Orgánico de "
                        + "la Economía Social de los Conocimientos, Creatividad e Innovación, lo cual impide "
                        + "que la administración corrobore el cumplimiento de los requisitos necesarios para "
                        + "la continuación o admisión del trámite.";
                resuelve = "El presente acto administrativo es susceptible de los recursos administrativos correspondientes "
                        + "de conformidad con lo establecido en el Art. 597 del Código Orgánico de la Economía Social de los "
                        + "Conocimientos, Creatividad e Innovación y en el Art. 488 del Reglamento de Gestión de los "
                        + "Conocimientos; o por vía jurisdiccional ante uno de los Tribunales Distritales de lo Contencioso "
                        + "Administrativo.";
            }

            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("tercero", tercero);
            parametro.put("cuarto", cuarto);
            parametro.put("resuelve", resuelve);
            String el_la = "";
            if (tipoTramite.equals("TRANSFERENCIA")) {
                parametro.put("tablabd", "caducada");
                el_la = "la ";
            } else if (tipoTramite.equals("CAMBIO DE DOMICILIO")) {
                parametro.put("tablabd", "cambio_domicilio");
                el_la = "el ";
            } else if (tipoTramite.equals("CAMBIO DE NOMBRE")) {
                parametro.put("tablabd", "cambio_nombre");
                el_la = "el ";
            } else if (tipoTramite.equals("LICENCIA DE USO")) {
                parametro.put("tablabd", "licencia_uso");
                el_la = "la ";
            } else if (tipoTramite.equals("PRENDA COMERCIAL")) {
                parametro.put("tablabd", "prenda_comercial");
                el_la = "la ";
            }
            parametro.put("tipotramite", el_la + tipoTramite);
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", idCaducada);
            parametro.put("resolucionnot", resnot);
            parametro.put("fecharesolnot", fecharesnot);

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print caducada " + tipoTramite + " separado: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewErjafeMasterBytes(String path, InputStream rutaJrxml, Date fechaPresentacion, Integer idCaducada, String rutapdf,
            String delegado, String delegacion, Delegado secretaria, String tipoTramite, Resolucion resolucion, Date fechaElabora) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {

            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("tablabd", "caducada");
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
            String fechaFormateada = sdf.format(fechaElabora);
            parametro.put("fecha_elabora", fechaFormateada);
            switch (tipoTramite) {
                case "TRANSFERENCIA":
                    parametro.put("tablabd", "notificacion");
                    break;
                case "CAMBIO DE DOMICILIO":
                    parametro.put("tablabd", "cambio_domicilio");
                    break;
                case "CAMBIO DE NOMBRE":
                    parametro.put("tablabd", "cambio_nombre");
                    break;
                case "LICENCIA DE USO":
                    parametro.put("tablabd", "licencia_uso");
                    break;
                default:
                    parametro.put("tablabd", "prenda_comercial");
                    break;
            }
            parametro.put("tipotramite", tipoTramite);
            parametro.put("resolucionnot", resolucion.getResolucion());
            parametro.put("id", idCaducada);

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print notificada erjafe " + tipoTramite + " separado: " + ex);
            return null;
        }
    }

    public FileInputStream viewErjafe(String path, InputStream rutaJrxml, Date fechaPresentacion, Integer idCaducada, String rutapdf,
            String delegado, String delegacion, Delegado secretaria, String tipoTramite, Resolucion resolucion, Date fechaElabora) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("tablabd", "caducada");

            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
            String fechaFormateada = sdf.format(fechaElabora);
            parametro.put("fecha_elabora", fechaFormateada);
            switch (tipoTramite) {
                case "TRANSFERENCIA":
                    System.out.println("fecha Formateada: " + fechaFormateada);
                    parametro.put("tablabd", "notificacion");
                    break;
                case "CAMBIO DE DOMICILIO":
                    parametro.put("tablabd", "cambio_domicilio");
                    break;
                case "CAMBIO DE NOMBRE":
                    parametro.put("tablabd", "cambio_nombre");
                    break;
                case "LICENCIA DE USO":
                    parametro.put("tablabd", "licencia_uso");
                    break;
                default:
                    parametro.put("tablabd", "prenda_comercial");
                    break;
            }
            parametro.put("tipotramite", tipoTramite);
            parametro.put("resolucionnot", resolucion.getResolucion());
            parametro.put("id", idCaducada);
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                System.out.println("Hay un error con el jasperprint");
                return null;
            }
            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);
                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                exporter.exportReport();
            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print erjafe: " + ex);
            return null;
        }
    }

    public FileInputStream viewCaducada(String path, InputStream rutaJrxml, Date fechaPresentacion, Integer idCaducada, String rutapdf,
            String delegado, String delegacion, Delegado secretaria, String tipoTramite, String resnot, String fecharesnot) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            String tercero;
            String cuarto;
            String resuelve;
            Date date = new Date(2016 - 1900, 11, 9);
            System.out.println("date: " + date);
            if (fechaPresentacion.before(date)) {
                tercero = "La solicitud fue ingresada por la parte interesada, acompañada "
                        + "de documentación para su análisis dentro del procedimiento administrativo correspondiente y, realizado el examen "
                        + "preliminar de la documentación aportada, se procedió a verificar el cumplimiento de los requisitos formales y "
                        + "sustanciales establecidos en la Ley de Propiedad Intelectual y su Reglamento General.";
                cuarto = "En el presente caso, de la revisión efectuada a la documentación ingresada, no se "
                        + "ha verificado el cumplimiento de lo dispuesto en la Ley de Propiedad Intelectual "
                        + "ni en su Reglamento, lo cual impide que la administración corrobore el cumplimiento "
                        + "de los requisitos necesarios para la continuación o admisión del trámite.";
                resuelve = "El presente acto administrativo es susceptible de los recursos administrativos establecidos en el Art. 357 "
                        + "de la Ley de Propiedad Intelectual; o por vía jurisdiccional ante uno de los Tribunales Distritales de lo "
                        + "Contencioso Administrativo.";
            } else {
                tercero = "La solicitud fue ingresada por la parte interesada, acompañada de documentación "
                        + "para su análisis dentro del procedimiento administrativo correspondiente y, realizado "
                        + "el examen preliminar de la documentación aportada, se procedió a verificar el cumplimiento "
                        + "de los requisitos formales y sustanciales establecidos en el Código Orgánico de la "
                        + "Economía Social de los Conocimientos, Creatividad e Innovación.";
                cuarto = "En el presente caso, de la revisión efectuada a la documentación ingresada, "
                        + "no se ha verificado el cumplimiento de lo dispuesto en la Código Orgánico de "
                        + "la Economía Social de los Conocimientos, Creatividad e Innovación, lo cual impide "
                        + "que la administración corrobore el cumplimiento de los requisitos necesarios para "
                        + "la continuación o admisión del trámite.";
                resuelve = "El presente acto administrativo es susceptible de los recursos administrativos correspondientes "
                        + "de conformidad con lo establecido en el Art. 597 del Código Orgánico de la Economía Social de los "
                        + "Conocimientos, Creatividad e Innovación y en el Art. 488 del Reglamento de Gestión de los "
                        + "Conocimientos; o por vía jurisdiccional ante uno de los Tribunales Distritales de lo Contencioso "
                        + "Administrativo.";
            }

            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("tercero", tercero);
            parametro.put("cuarto", cuarto);
            parametro.put("resuelve", resuelve);
            String el_la = "";
            if (tipoTramite.equals("TRANSFERENCIA")) {
                parametro.put("tablabd", "caducada");
                el_la = "la ";
            } else if (tipoTramite.equals("CAMBIO DE DOMICILIO")) {
                parametro.put("tablabd", "cambio_domicilio");
                el_la = "el ";
            } else if (tipoTramite.equals("CAMBIO DE NOMBRE")) {
                parametro.put("tablabd", "cambio_nombre");
                el_la = "el ";
            } else if (tipoTramite.equals("LICENCIA DE USO")) {
                parametro.put("tablabd", "licencia_uso");
                el_la = "la ";
            } else {
                parametro.put("tablabd", "prenda_comercial");
                el_la = "la ";
            }
            parametro.put("tipotramite", el_la + tipoTramite);
            
            parametro.put("resolucionnot", resnot);
            parametro.put("fecharesolnot", fecharesnot);

            parametro.put("id", idCaducada);
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                System.out.println("Hay un error con el jasperprint");
                return null;
            }
            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);
                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                exporter.exportReport();
            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print abandono: " + ex);
            return null;
        }
    }

    public FileInputStream viewAbandono(String path, InputStream rutaJrxml, Abandono abandono, String rutapdf,
            String delegado, String delegacion, Delegado secretaria) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", abandono.getId());
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                System.out.println("Hay un error con el jasperprint");
                return null;
            }
            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);
                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                exporter.exportReport();
            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print abandono: " + ex);
            return null;
        }
    }
    
    public byte[] viewAbandonoProrrogaAllMasterBytes(String path, InputStream rutaJrxml, Integer id, String rutapdf,
            String delegado, String delegacion, Delegado secretaria, String tipoMod, Resolucion resnot) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", id);
            parametro.put("tipo_mod", tipoMod);
            Controlador c = new Controlador();
            if (tipoMod.equals("prorroga")) {
                parametro.put("cambio", "TRANSFERENCIA");
                Prorroga p = c.getProrrogaById(id);
                parametro.put("dias_letras", Operaciones.convertirNumero(p.getDiasProrroga()));
            } else if (tipoMod.equals("cambio_nombre")) {
                parametro.put("cambio", "CAMBIO DE NOMBRE");
                CambioNombre cn = c.getCambioNombreById(id);
                parametro.put("dias_letras", Operaciones.convertirNumero(cn.getDiasProrroga()));
            } else if (tipoMod.equals("cambio_domicilio")) {
                parametro.put("cambio", "CAMBIO DE DOMICILIO");
                CambioDomicilio cd = c.getCambioDomicilioById(id);
                parametro.put("dias_letras", Operaciones.convertirNumero(cd.getDiasProrroga()));
            } else if (tipoMod.equals("prenda_comercial")) {
                parametro.put("cambio", "PRENDA COMERCIAL");
                PrendaComercial pc = c.getPrendaComercialById(id);
                parametro.put("dias_letras", Operaciones.convertirNumero(pc.getDiasProrroga()));
            } else if (tipoMod.equals("licencia_uso")) {
                parametro.put("cambio", "LICENCIA DE USO");
                LicenciaUso lu = c.getLicenciaUsoById(id);
                parametro.put("dias_letras", Operaciones.convertirNumero(lu.getDiasProrroga()));
            } else {
                parametro.put("cambio", "SUBLICENCIA DE USO");
                SubLicenciaUso su = c.getSublicenciaUsoById(id);
                parametro.put("dias_letras", Operaciones.convertirNumero(su.getDiasProrroga()));
            }

            if (resnot.getId() != null) {
                parametro.put("resolucionnot", resnot.getResolucion() + " de fecha " + Operaciones.formatDateToLarge(resnot.getFecha()));
            }

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print abandono " + tipoMod + " separado: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewAbandonoAllMasterBytes(String path, InputStream rutaJrxml, Integer id, String rutapdf,
            String delegado, String delegacion, Delegado secretaria, String tipoMod, Resolucion resnot) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", id);
            parametro.put("tipo_mod", tipoMod);
            Controlador c = new Controlador();
            if (tipoMod.equals("prorroga")) {
                parametro.put("cambio", "TRANSFERENCIA");                
            } else if (tipoMod.equals("cambio_nombre")) {
                parametro.put("cambio", "CAMBIO DE NOMBRE");                
            } else if (tipoMod.equals("cambio_domicilio")) {
                parametro.put("cambio", "CAMBIO DE DOMICILIO");                
            } else if (tipoMod.equals("prenda_comercial")) {
                parametro.put("cambio", "PRENDA COMERCIAL");                
            } else if (tipoMod.equals("licencia_uso")) {
                parametro.put("cambio", "LICENCIA DE USO");                
            } else {
                parametro.put("cambio", "SUBLICENCIA DE USO");                
            }

            if (resnot.getId() != null) {
                parametro.put("resolucionnot", resnot.getResolucion() + " de fecha " + Operaciones.formatDateToLarge(resnot.getFecha()));
            }

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print abandono " + tipoMod + " separado: " + ex);
            return null;
        }
    }

    public FileInputStream viewAbandonoProrrogaAll(String path, InputStream rutaJrxml, Integer id, String rutapdf,
            String delegado, String delegacion, Delegado secretaria, String tipoMod, Resolucion resnot) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", id);
            parametro.put("tipo_mod", tipoMod);
            Controlador c = new Controlador();
            if (tipoMod.equals("prorroga")) {
                parametro.put("cambio", "TRANSFERENCIA");
                Prorroga p = c.getProrrogaById(id);
                parametro.put("dias_letras", Operaciones.convertirNumero(p.getDiasProrroga()));
            } else if (tipoMod.equals("cambio_nombre")) {
                parametro.put("cambio", "CAMBIO DE NOMBRE");
                CambioNombre cn = c.getCambioNombreById(id);
                parametro.put("dias_letras", Operaciones.convertirNumero(cn.getDiasProrroga()));
            } else if (tipoMod.equals("cambio_domicilio")) {
                parametro.put("cambio", "CAMBIO DE DOMICILIO");
                CambioDomicilio cd = c.getCambioDomicilioById(id);
                parametro.put("dias_letras", Operaciones.convertirNumero(cd.getDiasProrroga()));
            } else if (tipoMod.equals("prenda_comercial")) {
                parametro.put("cambio", "PRENDA COMERCIAL");
                PrendaComercial pc = c.getPrendaComercialById(id);
                parametro.put("dias_letras", Operaciones.convertirNumero(pc.getDiasProrroga()));
            } else if (tipoMod.equals("licencia_uso")) {
                parametro.put("cambio", "LICENCIA DE USO");
                LicenciaUso lu = c.getLicenciaUsoById(id);
                parametro.put("dias_letras", Operaciones.convertirNumero(lu.getDiasProrroga()));
            } else {
                parametro.put("cambio", "SUBLICENCIA DE USO");
                SubLicenciaUso su = c.getSublicenciaUsoById(id);
                parametro.put("dias_letras", Operaciones.convertirNumero(su.getDiasProrroga()));
            }            

            if (resnot.getId() != null) {
                parametro.put("resolucionnot", resnot.getResolucion() + " de fecha " + Operaciones.formatDateToLarge(resnot.getFecha()));
            }            
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                System.out.println("Hay un error con el jasperprint");
                return null;
            }            
            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);
                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                exporter.exportReport();
            }            
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print abandono: " + ex);
            return null;
        }
    }
    
    public FileInputStream viewAbandonoAll(String path, InputStream rutaJrxml, Integer id, String rutapdf,
            String delegado, String delegacion, Delegado secretaria, String tipoMod, Resolucion resnot) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", id);
            parametro.put("tipo_mod", tipoMod);
            Controlador c = new Controlador();            
            if (tipoMod.equals("prorroga")) {
                parametro.put("cambio", "TRANSFERENCIA");
            } else if (tipoMod.equals("cambio_nombre")) {
                parametro.put("cambio", "CAMBIO DE NOMBRE");                
            } else if (tipoMod.equals("cambio_domicilio")) {
                parametro.put("cambio", "CAMBIO DE DOMICILIO");
            } else if (tipoMod.equals("prenda_comercial")) {
                parametro.put("cambio", "PRENDA COMERCIAL");
            } else if (tipoMod.equals("licencia_uso")) {
                parametro.put("cambio", "LICENCIA DE USO");
            } else {
                parametro.put("cambio", "SUBLICENCIA DE USO");
            }            

            if (resnot.getId() != null) {
                parametro.put("resolucionnot", resnot.getResolucion() + " de fecha " + Operaciones.formatDateToLarge(resnot.getFecha()));
            }            
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                System.out.println("Hay un error con el jasperprint");
                return null;
            }            
            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);
                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                exporter.exportReport();
            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print abandono: " + ex);
            return null;
        }
    }

    public FileInputStream viewCambioNombreCertificado(String path, InputStream rutaJrxml, CambioNombre cambio, String rutapdf,
            String resol, String fecha_resol, String delegado, String delegacion) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();
            parametro.put("title", "CERTIFICADO DE CAMBIO DE NOMBRE DEL TITULAR No. " + cambio.getCertificado() + " - SENADI");
            parametro.put("resol", resol + ", de fecha " + fecha_resol);

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);

            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idCambioNombre", cambio.getId());

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);

                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                exporter.exportReport();

            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print transferencia: " + ex);
            return null;
        }
    }

    public FileInputStream viewCambioCasillero(String path, InputStream rutaJrxml, CambioCasillero cambio, String rutapdf,
            String resol, String resolnot, String delegado, String delegacion, Delegado secretaria) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();
            //parametro.put("title", "INSCRIPCIÓN LICENCIA DE USO No. " + licencia.getLicenciaNo() + " - SENADI");
            parametro.put("resol", resol);
            parametro.put("resolnot", resolnot);

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);

            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idCambioCasillero", cambio.getId());

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);

                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                exporter.exportReport();

            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print cambio casillero: " + ex);
            return null;
        }

    }

    public FileInputStream viewLicenciaUso(String path, InputStream rutaJrxml, LicenciaUso licencia, String rutapdf,
            String resol, String delegado, String delegacion, String resolnot, Delegado secretaria) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();
            //parametro.put("title", "INSCRIPCIÓN LICENCIA DE USO No. " + licencia.getLicenciaNo() + " - SENADI");
            parametro.put("resol", resol);
            parametro.put("resolnot", resolnot);

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);

            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

            if (licencia.getVenceContrato() != null && !licencia.getVenceContrato().trim().isEmpty()) {
                parametro.put("vence_contrato", licencia.getVenceContrato() + ",");
            } else {
                parametro.put("vence_contrato", "el " + Operaciones.formatDateToLarge(licencia.getFechaVenceContrato()) + ",");
            }

            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idLicenciaUso", licencia.getId());

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);

                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                exporter.exportReport();

            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print licencia unique: " + ex);
            return null;
        }
    }

    public FileInputStream viewSublicenciaUso(String path, InputStream rutaJrxml, SubLicenciaUso licencia, String rutapdf,
            String resol, String delegado, String delegacion, String resolnot, Delegado secretaria) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();
            parametro.put("title", "INSCRIPCIÓN SUBLICENCIA DE USO No. " + licencia.getSublicenciaNo() + " - SENADI");
            parametro.put("resol", resol);
            parametro.put("resolnot", resolnot);

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);

            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

            if (licencia.getVenceContrato() != null && !licencia.getVenceContrato().trim().isEmpty()) {
                parametro.put("vence_contrato", licencia.getVenceContrato() + ",");
            } else {
                parametro.put("vence_contrato", "el " + Operaciones.formatDateToLarge(licencia.getFechaVenceContrato()) + ",");
            }

            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idLicenciaUso", licencia.getId());

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);

                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                exporter.exportReport();

            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print sublicencia unique: " + ex);
            return null;
        }
    }

    public FileInputStream viewPrendaComercialCertificado(String path, InputStream rutaJrxml, PrendaComercial prenda, String rutapdf,
            String resol, String delegado, String delegacion, String resolnot, Delegado secretaria, String titulo) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();
            parametro.put("title", titulo);
            parametro.put("resol", resol);
            parametro.put("resolnot", resolnot);

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);

            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idPrendaComercial", prenda.getId());
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);

                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                exporter.exportReport();

            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print prenda unique: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewLicenciaUsoMasterBytes(String path, InputStream rutaJrxml, LicenciaUso licencia, String rutapdf,
            String resol, String delegado, String delegacion, String resolnot, Delegado secretaria) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();

//            parametro.put("title", "INSCRIPCIÓN LICENCIA DE USO No. " + licencia.getLicenciaNo() + " - SENADI");
            parametro.put("resol", resol);
            parametro.put("resolnot", resolnot);

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);

            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

            if (licencia.getVenceContrato() != null && !licencia.getVenceContrato().trim().isEmpty()) {
                parametro.put("vence_contrato", licencia.getVenceContrato() + ",");
            } else {
                parametro.put("vence_contrato", "el " + Operaciones.formatDateToLarge(licencia.getFechaVenceContrato()) + ",");
            }

            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idLicenciaUso", licencia.getId());

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print cert_licencia various: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewSublicenciaUsoMasterBytes(String path, InputStream rutaJrxml, SubLicenciaUso licencia, String rutapdf,
            String resol, String delegado, String delegacion, String resolnot, Delegado secretaria) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();

            parametro.put("title", "INSCRIPCIÓN SUBLICENCIA DE USO No. " + licencia.getSublicenciaNo() + " - SENADI");
            parametro.put("resol", resol);
            parametro.put("resolnot", resolnot);

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);

            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

            if (licencia.getVenceContrato() != null && !licencia.getVenceContrato().trim().isEmpty()) {
                parametro.put("vence_contrato", licencia.getVenceContrato() + ",");
            } else {
                parametro.put("vence_contrato", "el " + Operaciones.formatDateToLarge(licencia.getFechaVenceContrato()) + ",");
            }

            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idLicenciaUso", licencia.getId());

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print cert_sublicencia various: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewPrendaComercialMasterBytes(String path, InputStream rutaJrxml, PrendaComercial prenda, String rutapdf,
            String resol, String delegado, String delegacion, String resolnot, Delegado secretaria, String title) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();

            parametro.put("title", title);
            parametro.put("resol", resol);
            parametro.put("resolnot", resolnot);

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);

            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idPrendaComercial", prenda.getId());
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print certificado_prenda various: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public FileInputStream viewTransferencia(String path, InputStream rutaJrxml, Transferencia transferencia,
            String rutaArchivoPdf, String resol, String fecha_resol, String delegado, String delegacion) {

        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();
            parametro.put("title", "CERTIFICADO DE TRANSFERENCIA No. " + transferencia.getCertificado() + " - SENADI");
//            System.out.println("FC: "+transferencia.getFechaCertificado());
            parametro.put("fecha_certificado", Operaciones.formatDateToLarge(transferencia.getFechaCertificado()));
            parametro.put("fecha_presentacion", Operaciones.formatDateToLarge(transferencia.getFechaPresentacion()));
            parametro.put("fecha_registro", Operaciones.formatDateToLarge(transferencia.getFechaRegistro()));

            parametro.put("resol", resol);
            parametro.put("fecha_resol", fecha_resol);

            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);

            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idTransferencia", transferencia.getId());

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutaArchivoPdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);

                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                exporter.exportReport();

            }
            entrada = new FileInputStream(rutaArchivoPdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print transferencia: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public FileInputStream viewDesistimientoMaster(String path, InputStream rutaJrxml, Desistimiento des, String rutaArchivoXLS, String delegado, String delegacion,
            String resoluciontransf) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", des.getId());
            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("resol_label", resoluciontransf);

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            OutputStream out = new FileOutputStream(rutaArchivoXLS + ".pdf");
            JRPdfExporter exporter = new JRPdfExporter();

            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            ExporterInput inp = new SimpleExporterInput(jasperPrint);
            configuration.setCreatingBatchModeBookmarks(true);
            configuration.set128BitKey(Boolean.TRUE);

            exporter.setConfiguration(configuration);
            exporter.setExporterInput(inp);
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

            exporter.exportReport();

            entrada = new FileInputStream(rutaArchivoXLS + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print desistimiento: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public FileInputStream viewNotificacionMaster(String path, InputStream rutaJrxml, Notificacion n, String rutaArchivoXLS, String delegado, String delegacion,
            String resoluciontransf, String resolucionnot, Delegado secretaria) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", n.getId());
            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("resoluciontransf", resoluciontransf);
            parametro.put("resolucionnot", resolucionnot);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            OutputStream out = new FileOutputStream(rutaArchivoXLS + ".pdf");
            JRPdfExporter exporter = new JRPdfExporter();

            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            ExporterInput inp = new SimpleExporterInput(jasperPrint);
            configuration.setCreatingBatchModeBookmarks(true);
            configuration.set128BitKey(Boolean.TRUE);

            exporter.setConfiguration(configuration);
            exporter.setExporterInput(inp);
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

            exporter.exportReport();

            entrada = new FileInputStream(rutaArchivoXLS + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print notificación: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public FileInputStream viewNotificacion(String path, InputStream rutaJrxml, Notificacion n, String rutaArchivoXLS, String delegado,
            String delegacion, String secretaria) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idNotificada", n.getId());
            parametro.put("fecha_el_not", Operaciones.formatDateToLarge(n.getFechaNotificacion()));
            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria);

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            OutputStream out = new FileOutputStream(rutaArchivoXLS + ".pdf");
            JRPdfExporter exporter = new JRPdfExporter();

            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            ExporterInput inp = new SimpleExporterInput(jasperPrint);
            configuration.setCreatingBatchModeBookmarks(true);
            configuration.set128BitKey(Boolean.TRUE);

            exporter.setConfiguration(configuration);
            exporter.setExporterInput(inp);
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

            exporter.exportReport();

            entrada = new FileInputStream(rutaArchivoXLS + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print notificación: " + ex);
            return null;
        }
    }

    public byte[] pdfUnidoTransferencia(InputStream rutaJrxml, String path, TransferenciaTableModel rtm, String nombre, String nombrePersona, String delegacion, String resData) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("nombrePersona", nombrePersona);
            parametro.put("delegacion", delegacion);
            parametro.put("resolucionData", resData);

            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            JRTableModelDataSource datos = new JRTableModelDataSource(rtm);

            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, datos);
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;

        } catch (JRException ex) {
            System.err.println("Error pdfunidotransferencia: " + ex.getMessage() + " - " + ex);
            return null;

        }
    }

    public byte[] pdfUnidoNotificacion(InputStream rutaJrxml, String path, NotificacionTableModel ntm, String nombre, String delegado,
            String delegacion, String resoluciontransf, String resolucionnot, Delegado secre) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("resoluciontransf", resoluciontransf);
            parametro.put("resolucionnot", resolucionnot);
            parametro.put("secretaria", secre.getNombre());
            parametro.put("denosecre", secre.getDenominacion());

            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            JRTableModelDataSource datos = new JRTableModelDataSource(ntm);

            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, datos);
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;

        } catch (JRException ex) {
            System.err.println("Error pdfunidonotificacion: " + ex.getMessage() + " - " + ex);
            return null;

        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewNotificacionMasterBytes(String path, InputStream rutaJrxml, Notificacion n, String rutaArchivoXLS, String delegado, String delegacion,
            String resoluciontransf, String resolucionnot, Delegado secre) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", n.getId());
            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("resoluciontransf", resoluciontransf);
            parametro.put("resolucionnot", resolucionnot);
            System.out.println("secre: " + secre.getNombre());
            System.out.println("denosecre: " + secre.getDenominacion());
            parametro.put("secretaria", secre.getNombre());
            parametro.put("denosecre", secre.getDenominacion());

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print notificación separado: " + ex);
            return null;
        }
    }

    public byte[] viewTransferenciaMasterBytes(String path, InputStream rutaJrxml, Transferencia transferencia,
            String rutaArchivoPdf, String resol, String fecha_resol, String delegado, String delegacion) {

        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();
            parametro.put("title", "CERTIFICADO DE TRANSFERENCIA No. " + transferencia.getCertificado() + " - SENADI");
            parametro.put("fecha_certificado", Operaciones.formatDateToLarge(transferencia.getFechaCertificado()));
            parametro.put("fecha_presentacion", Operaciones.formatDateToLarge(transferencia.getFechaPresentacion()));
            parametro.put("fecha_registro", Operaciones.formatDateToLarge(transferencia.getFechaRegistro()));
            parametro.put("resol", resol);
            parametro.put("fecha_resol", fecha_resol);
            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idTransferencia", transferencia.getId());

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print transferencia separado: " + ex);
            return null;
        }
    }

    public byte[] viewRazonCorreccion(String path, InputStream ruta, RazonCorreccion razon) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();
            parametro.put("suscriptor", razon.getSuscriptor());
            parametro.put("denominacion", razon.getDenominacion());
            parametro.put("razon", razon.getRazon());
            parametro.put("idCambioCasillero", 1);
            parametro.put("SUBREPORT_DIR", path + "/");

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(ruta);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print transferencia separado: " + ex);
            return null;
        }
    }

}
