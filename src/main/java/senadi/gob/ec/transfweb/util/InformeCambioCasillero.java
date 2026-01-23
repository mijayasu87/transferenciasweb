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
import senadi.gob.ec.transfweb.model.CambioCasillero;
import senadi.gob.ec.transfweb.model.Delegado;
import senadi.gob.ec.transfweb.model.Resolucion;

/**
 *
 * @author Michael Y.
 */
@WebServlet(name = "ServletCambioCasillero", urlPatterns = {"/cambiocas"})
public class InformeCambioCasillero extends HttpServlet {

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

//        String resol = res.getResolucion();
//        String fecha_resol = Operaciones.formatDateToLarge(res.getFecha());
//        System.out.println("Fecha: "+resnot.getFecha());
        String resolnot = resnot.getResolucion() + ", de " + Operaciones.formatDateToLarge(resnot.getFecha());
//        System.out.println("resolnot: "+resolnot);
        String resolucionData = "No. " + res.getResolucion() + ", expedida el " + Operaciones.formatDateToLarge(res.getFecha());;

        try {

            response.setHeader("Cache-Control", "max-age=18");
            response.setHeader("Pragma", "No-cache");
            response.setDateHeader("Expires", 0);

            response.setContentType("application/pdf");

            Report report = new Report();
            FileInputStream in = null;
            InputStream is = null;
            String nombre = "";

            CambioCasillero cambio = lb.getCambioCasillero();
            if (cambio != null) {
                nombre = cambio.getTramite() + "_" + cambio.getProvidencia() + "_cambio_casillero";
                nombre = nombre.trim().replace(" ", "_");
                response.setHeader("Content-disposition", "inline; filename=" + nombre + ".pdf");
                is = getServletContext().getResourceAsStream("/WEB-INF/report/CambioCasillero.jrxml");
                in = report.viewCambioCasillero(path, is, cambio, "archivo.xls", resolucionData, resolnot, delegado, delegacion, secretaria);
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

        } catch (Exception e) {
            System.out.println("error cambio casillero providencia: " + e.toString());
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
