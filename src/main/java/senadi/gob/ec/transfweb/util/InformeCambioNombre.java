/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import senadi.gob.ec.transfweb.bean.LoginBean;
import senadi.gob.ec.transfweb.model.Delegado;
import senadi.gob.ec.transfweb.model.RazonCorreccion;
import senadi.gob.ec.transfweb.model.Resolucion;
import senadi.gob.ec.transfweb.model.cn.CambioNombre;

/**
 *
 * @author Michael Y.
 */
@WebServlet(name = "ServletCambioNombre", urlPatterns = {"/cambionombrep"})
public class InformeCambioNombre extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession mises = (HttpSession) request.getSession();

        LoginBean lb = (LoginBean) mises.getValue("loginBean");
        ServletOutputStream out = response.getOutputStream();

        ServletContext context = request.getServletContext();
        String path = context.getRealPath("/WEB-INF/report/");

        Controlador c = new Controlador();

        Delegado secretaria = c.getDelegadoActivo("secretaria");

        String delegado = c.getDelegadoActivo("delegado").getNombre();
        String delegacion = c.getDelegacionActiva().getNombre();
        Resolucion res = c.getResolucionActiva("transferencia");
        Resolucion resnot = c.getResolucionActiva("notificacion");

        String resol = res.getResolucion();
        String fecha_resol = Operaciones.formatDateToLarge(res.getFecha());
//        System.out.println("Fecha: "+resnot.getFecha());
        String resolnot = resnot.getResolucion() + ", " + Operaciones.formatDateToLarge(resnot.getFecha());
//        System.out.println("resolnot: "+resolnot);
        String resolucionData = res.getResolucion() + ", " + Operaciones.formatDateToLarge(res.getFecha());

        RazonCorreccion razon = null;
        if (lb.getRazon() != null) {
            razon = lb.getRazon();
        }

        try {

            response.setHeader("Cache-Control", "max-age=18");
            response.setHeader("Pragma", "No-cache");
            response.setDateHeader("Expires", 0);

            if (lb.isVarious() && !lb.getCambiosNombre().isEmpty()) {
                List<CambioNombre> cambios = lb.getCambiosNombre();
                response.setHeader("Content-disposition", "inline; filename=" + cambios.get(0).getTipoEstado().toLowerCase() + "s_cn_" + Operaciones.formatDate(new Date()) + ".zip");
                response.setContentType("application/x-download");
                List<File> files = new ArrayList<>();

                for (int i = 0; i < cambios.size(); i++) {
                    CambioNombre cambio = cambios.get(i);
                    InputStream is = null;

                    Report report = new Report();
                    byte[] arb = null;
                    String nombre = "";
                    if (cambio.getTipoEstado().equals("CERTIFICADO")) {
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/CertificadoCambioNombre.jrxml");
                        arb = report.viewCambioNombreCertificadoMasterBytes(path, is, cambio, "archivo.xls", resol, fecha_resol, delegado, delegacion);
                        nombre = cambio.getSolicitud() + "_certificado_cn_" + cambio.getCertificado();

                        File fileTemp = null;

                        if (razon != null) {
                            List<Object[]> estos = new ArrayList<>();
                            Report rep = new Report();
                            InputStream isaux = getServletContext().getResourceAsStream("/WEB-INF/report/RazonCorreccion.jrxml");
                            byte[] rac = rep.viewRazonCorreccion(path, isaux, razon);
                            rep.closeConnection();
                            isaux.close();

                            estos.add(new Object[]{arb, "PÁGINA CERTIFICADO "+cambio.getSolicitud()});
                            estos.add(new Object[]{rac, "PÁGINA RAZÓN "+cambio.getSolicitud()});

                            fileTemp = c.concatenarPdfDoFile(estos, nombre.trim().replace(" ", "_") + ".pdf");

                        } else {
                            fileTemp = new File(nombre.trim().replace(" ", "_") + ".pdf");
                            FileOutputStream outs = new FileOutputStream(fileTemp);
                            outs.write(arb);
                            outs.close();
                        }

                        files.add(fileTemp);
                    } else if (cambio.getTipoEstado().equals("NOTIFICADA")) {
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/NotificacionCambioNombre.jrxml");
                        arb = report.viewCambioNombreNotificadaMasterBytes(path, is, cambio, "archivo.xls", resol, fecha_resol, resolnot, delegado, delegacion, secretaria);
                        nombre = cambio.getSolicitud() + "_notificacion_cn_" + cambio.getNotificacion();

                        File fileTemp = new File(nombre.trim().replace(" ", "_") + ".pdf");
                        FileOutputStream outs = new FileOutputStream(fileTemp);
                        outs.write(arb);
                        outs.close();

                        files.add(fileTemp);
                    }

                    report.closeConnection();
                    is.close();

                }

                File all = zip(files, "micharoto");

                byte[] content = Files.readAllBytes(all.toPath());

                response.getOutputStream().write(content);
                response.getOutputStream().flush();
                response.getOutputStream().close();

            } else {
                response.setContentType("application/pdf");
                Report report = new Report();
                FileInputStream in = null;
                InputStream is = null;
                String nombre = "";
                CambioNombre cambio = lb.getCambioNombre();
                if (cambio != null) {
                    System.out.println(cambio.getSolicitud() + ", tipo: " + cambio.getTipoEstado());
                    if (cambio.getTipoEstado().equals("CERTIFICADO")) {
                        nombre = cambio.getSolicitud() + "_certificado_cn_" + cambio.getCertificado();
                        response.setHeader("Content-disposition", "inline; filename=" + nombre + ".pdf");
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/CertificadoCambioNombre.jrxml");
                        in = report.viewCambioNombreCertificado(path, is, cambio, "archivo.xls", resol, fecha_resol, delegado, delegacion);
                    } else if (cambio.getTipoEstado().equals("NOTIFICADA")) {
                        nombre = cambio.getSolicitud() + "_notificacion_cn_" + cambio.getNotificacion();
                        response.setHeader("Content-disposition", "inline; filename=" + nombre + ".pdf");
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/NotificacionCambioNombre.jrxml");
                        in = report.viewCambioNombreNotificada(path, is, cambio, "archivo.xls", resol, fecha_resol, resolnot, delegado, delegacion, secretaria);
                    }
                }

                int bit;
                bit = 256;
                while ((bit) >= 0) {
                    bit = in.read();
                    out.write(bit);
                }

                out.flush();
                out.close();
                report.closeConnection();
                is.close();
            }

        } catch (Exception e) {
            System.out.println("error cambio_nombre-notif: " + e.toString());
        } finally {
            out.close();
        }
    }

    public File zip(List<File> files, String filename) {
        File zipfile = new File(filename);
        // Create a buffer for reading the files
        byte[] buf = new byte[1024];
        try {
            // create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
            // compress the files
            for (int i = 0; i < files.size(); i++) {
                FileInputStream in = new FileInputStream(files.get(i).getCanonicalFile());
                // add ZIP entry to output stream
                out.putNextEntry(new ZipEntry(files.get(i).getName()));
                // transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                // complete the entry
                out.closeEntry();
                in.close();
            }
            // complete the ZIP file
            out.close();
            return zipfile;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
