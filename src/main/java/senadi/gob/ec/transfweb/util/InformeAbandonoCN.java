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
import senadi.gob.ec.transfweb.model.Abandono;
import senadi.gob.ec.transfweb.model.Delegado;
import senadi.gob.ec.transfweb.model.Resolucion;
import senadi.gob.ec.transfweb.model.cn.CambioNombre;

/**
 *
 * @author Michael Y.
 */
@WebServlet(name = "ServletAbandonoCN", urlPatterns = {"/abancnreport"})
public class InformeAbandonoCN extends HttpServlet {

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

        try {

            response.setHeader("Cache-Control", "max-age=18");
            response.setHeader("Pragma", "No-cache");
            response.setDateHeader("Expires", 0);

            if (lb.isVarious()) {
                List<CambioNombre> abandonos = lb.getCambiosNombre();
//                List<CambioDomicilio> cambios = lb.getCambiosDomicilio();
                String carp = "abandonos_cn_" + Operaciones.formatDate(new Date());
                response.setHeader("Content-disposition", "inline; filename=" + carp + ".zip");
                response.setContentType("application/x-download");
                List<File> files = new ArrayList<>();

                for (int i = 0; i < abandonos.size(); i++) {
                    CambioNombre abandono = abandonos.get(i);
                    InputStream is = null;

                    Report report = new Report();
                    byte[] arb = null;
                    String nombre = "";
                    if (abandono.getTipoAbandono().equals("ERJAFE")) {
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/AbandonoReportERJAFEO.jrxml");
                        nombre = abandono.getSolicitud() + "_ab_erj_cn_" + abandono.getNumeroAbandono();
                    } else if (abandono.getTipoAbandono().equals("REGLAMENTO")) {
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/AbandonoReportREGLAMENTOO.jrxml");
                        nombre = abandono.getSolicitud() + "_ab_reg_cn_" + abandono.getNumeroAbandono();
                    } else {
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/AbandonoReportCOAO.jrxml");
                        nombre = abandono.getSolicitud() + "_ab_coa_cn_" + abandono.getNumeroAbandono();
                    }
                    arb = report.viewAbandonoProrrogaAllMasterBytes(path, is, abandono.getId(), "archivo.xls", delegado, delegacion, secretaria, "cambio_nombre", new Resolucion());
                    File fileTemp = new File(nombre.trim().replace(" ", "_") + ".pdf");
                    FileOutputStream outs = new FileOutputStream(fileTemp);
                    outs.write(arb);
                    outs.close();

                    files.add(fileTemp);

                    report.closeConnection();
                    is.close();

                }

                File all = zip(files, "micharoto");

                byte[] content = Files.readAllBytes(all.toPath());

                response.getOutputStream().write(content);
                response.getOutputStream().flush();
                response.getOutputStream().close();

            } else {
                CambioNombre abandono = lb.getCambioNombre();
                response.setContentType("application/pdf");
                Report report = new Report();
                FileInputStream in = null;
                InputStream is = null;
                String nombre = "";

                if (abandono != null && abandono.getId() != null) {
                    if (abandono.getTipoAbandono().equals("REGLAMENTO")) {
                        System.out.println("reglamento");
                        nombre = abandono.getSolicitud() + "_abandono_cn_reg_" + abandono.getNumeroAbandono();
                        nombre = nombre.trim().replace(" ", "_");
                        response.setHeader("Content-disposition", "inline; filename=" + nombre + ".pdf");
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/AbandonoReportREGLAMENTOO.jrxml");
                        in = report.viewAbandonoProrrogaAll(path, is, abandono.getId(), "archivo.xls", delegado, delegacion, secretaria, "cambio_nombre", new Resolucion());
                    } else if (abandono.getTipoAbandono().equals("ERJAFE")) {
                        System.out.println("erjafe");
                        nombre = abandono.getSolicitud() + "_abandono_cn_erj_" + abandono.getNumeroAbandono();
                        nombre = nombre.trim().replace(" ", "_");
                        response.setHeader("Content-disposition", "inline; filename=" + nombre + ".pdf");
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/AbandonoReportERJAFEO.jrxml");
                        in = report.viewAbandonoProrrogaAll(path, is, abandono.getId(), "archivo.xls", delegado, delegacion, secretaria, "cambio_nombre", new Resolucion());
                    } else {
                        System.out.println("coa");
                        nombre = abandono.getSolicitud() + "_abandono_cn_coa_" + abandono.getNumeroAbandono();
                        nombre = nombre.trim().replace(" ", "_");
                        response.setHeader("Content-disposition", "inline; filename=" + nombre + ".pdf");
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/AbandonoReportCOAO.jrxml");
                        in = report.viewAbandonoProrrogaAll(path, is, abandono.getId(), "archivo.xls", delegado, delegacion, secretaria, "cambio_nombre", new Resolucion());
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
            System.out.println("error abandono : " + e.toString());
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
