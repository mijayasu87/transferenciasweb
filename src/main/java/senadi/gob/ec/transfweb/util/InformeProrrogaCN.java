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
import senadi.gob.ec.transfweb.model.Resolucion;
import senadi.gob.ec.transfweb.model.cn.CambioNombre;

/**
 *
 * @author Michael Y.
 */
@WebServlet(name = "ServletProrrogaCN", urlPatterns = {"/prorrogacnreport"})
public class InformeProrrogaCN extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init(); //To change body of generated methods, choose Tools | Templates.
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

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

        Resolucion resnot = c.getResolucionActiva("notificacion");

        try {

            response.setHeader("Cache-Control", "max-age=18");
            response.setHeader("Pragma", "No-cache");
            response.setDateHeader("Expires", 0);

            if (lb.isVarious()) {
                List<CambioNombre> prorrogas = lb.getCambiosNombre();
                String carp = "prorrogas_cn_" + Operaciones.formatDate(new Date());
                response.setHeader("Content-disposition", "inline; filename=" + carp + ".zip");
                response.setContentType("application/x-download");
                List<File> files = new ArrayList<>();

                for (int i = 0; i < prorrogas.size(); i++) {
                    CambioNombre prorroga = prorrogas.get(i);
                    InputStream is = getServletContext().getResourceAsStream("/WEB-INF/report/ProrrogaReportCD.jrxml");

                    Report report = new Report();
                    String nombre = prorroga.getSolicitud() + "_prorroga_cn_" + prorroga.getNumeroProrroga();
                    byte[] arb = report.viewAbandonoProrrogaAllMasterBytes(path, is, prorroga.getId(), "archivo.xls", delegado, delegacion, secretaria, "cambio_nombre", resnot);
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
                CambioNombre prorroga = lb.getCambioNombre();
                response.setContentType("application/pdf");
                Report report = new Report();
                FileInputStream in = null;
                InputStream is = null;
                String nombre = "";

                if (prorroga != null && prorroga.getId() != null) {
                    nombre = prorroga.getSolicitud() + "_prorroga_cn_" + prorroga.getNumeroProrroga();
                    nombre = nombre.trim().replace(" ", "_");
                    response.setHeader("Content-disposition", "inline; filename=" + nombre + ".pdf");
                    is = getServletContext().getResourceAsStream("/WEB-INF/report/ProrrogaReportCD.jrxml");
                    in = report.viewAbandonoProrrogaAll(path, is, prorroga.getId(), "archivo.xls", delegado, delegacion, secretaria, "cambio_nombre", resnot);
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
            System.out.println("error prorroga cn : " + e.toString());
        } finally {
            out.close();
        }
    }

    public File zip(List<File> files, String filename) {
        File zipfile = new File(filename);
        byte[] buf = new byte[1024];
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
            for (int i = 0; i < files.size(); i++) {
                FileInputStream in = new FileInputStream(files.get(i).getCanonicalFile());
                out.putNextEntry(new ZipEntry(files.get(i).getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
            return zipfile;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
