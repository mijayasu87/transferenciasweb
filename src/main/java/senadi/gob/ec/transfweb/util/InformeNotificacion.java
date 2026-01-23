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
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.model.Resolucion;

/**
 *
 * @author Michael Y.
 */
@WebServlet(name = "ServletInformeNotif", urlPatterns = {"/repnot"})
public class InformeNotificacion extends HttpServlet {

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
        
        //String secretaria = c.getDelegadoActivo("secretaria").getNombre();
        Delegado delegadosec = c.getDelegadoActivo("secretaria");
        String delegado = c.getDelegadoActivo("delegado").getNombre();
        String delegacion = c.getDelegacionActiva().getNombre();
        Resolucion res = c.getResolucionActiva("transferencia");
        Resolucion resnot = c.getResolucionActiva("notificacion");
        String resolnot = resnot.getResolucion() + ", " + Operaciones.formatDateToLarge(resnot.getFecha());
        String resolucionData = res.getResolucion() + ", " + Operaciones.formatDateToLarge(res.getFecha());;

        try {

            response.setHeader("Cache-Control", "max-age=18");
            response.setHeader("Pragma", "No-cache");
            response.setDateHeader("Expires", 0);

            if (lb.isVarious() && !lb.getNotificacionesFlotantes().isEmpty()) {
                response.setHeader("Content-disposition", "inline; filename=tr_notificaciones_" + Operaciones.formatDate(new Date()) + ".zip");
                response.setContentType("application/x-download");
                List<File> files = new ArrayList<File>();

                if (lb.isAllInOne()) {
                    InputStream is = null;

                    is = getServletContext().getResourceAsStream("/WEB-INF/report/NotificacionReporteUnido.jrxml");

                    Report report = new Report();
                    String certName = "tr_notificaciones_" + Operaciones.formatDate(new Date()) + ".pdf";
                    certName = certName.trim().replace(" ", "_");
                    byte[] arb = report.pdfUnidoNotificacion(is, path, new NotificacionTableModel(lb.getNotificacionesFlotantes()), certName, delegado, delegacion, resolucionData, resolnot, delegadosec);
                    report.closeConnection();
                    is.close();
                    File fileTemp = new File(certName);
                    FileOutputStream outs = new FileOutputStream(fileTemp);
                    outs.write(arb);
                    outs.close();
                    files.add(fileTemp);
                }else{
                    System.out.println("varias notificaciones");
                    for (int i = 0; i < lb.getNotificacionesFlotantes().size(); i++) {
                        Notificacion naux = lb.getNotificacionesFlotantes().get(i);
                        InputStream is = getServletContext().getResourceAsStream("/WEB-INF/report/NotificacionReporte.jrxml");

                        Report report = new Report();
                        byte[] arb = report.viewNotificacionMasterBytes(path, is, naux, "archivo.xls", delegado, delegacion,resolucionData, resolnot, delegadosec);
                        report.closeConnection();
                        is.close();
                        String name = naux.getSolicitud() + "_not_"+naux.getRegistro()+".pdf";
                        File fileTemp = new File(name.trim().replace(" ", "_"));
                        FileOutputStream outs = new FileOutputStream(fileTemp);
                        outs.write(arb);
                        outs.close();
                        files.add(fileTemp);
                    }
                }

                File all = zip(files, "micharoto");

                byte[] content = Files.readAllBytes(all.toPath());

                response.getOutputStream().write(content);
                response.getOutputStream().flush();
                response.getOutputStream().close();

            } else {
                System.out.println("llega aquí");
            }

        } catch (Exception e) {
            System.out.println("error transfer-notif: " + e.toString());
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
