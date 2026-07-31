/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.bean;

import com.jcraft.jsch.JSchException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.PrimeFaces;
import org.primefaces.component.api.UIData;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFileWrapper;
import org.primefaces.model.file.UploadedFiles;
import org.primefaces.model.file.UploadedFilesWrapper;
import senadi.gob.ec.transfweb.model.Abandono;
import senadi.gob.ec.transfweb.model.Caducada;
import senadi.gob.ec.transfweb.model.Documento;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.model.Transferencia;
import senadi.gob.ec.transfweb.model.UploadNotificacion;
import senadi.gob.ec.transfweb.model.Prorroga;
import senadi.gob.ec.transfweb.model.cd.CambioDomicilio;
import senadi.gob.ec.transfweb.model.cn.CambioNombre;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepform.Types;
import senadi.gob.ec.transfweb.model.iepicas.LockerNotifications;
import senadi.gob.ec.transfweb.model.iepicas.Notifications;
import senadi.gob.ec.transfweb.model.iepicas.NotificationsDAO;
import senadi.gob.ec.transfweb.model.licencia.LicenciaUso;
import senadi.gob.ec.transfweb.model.licencia.SubLicenciaUso;
import senadi.gob.ec.transfweb.model.prenda.PrendaComercial;
import senadi.gob.ec.transfweb.util.Codekru;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.FTPFiles;
import senadi.gob.ec.transfweb.util.Mailer;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author micharesp
 */
@ManagedBean(name = "uploadBean")
@ViewScoped
public class UploadCertBean implements Serializable {

    private UploadedFilesWrapper filesw;
    private UploadedFileWrapper filew;

    private UploadedFiles files;
    private UploadedFile file;

    private boolean activo;
    private String log;

    private List<UploadNotificacion> notificaciones;
    private List<UploadNotificacion> notificacionesFiltradas;
    private UIData notificacionesDataTable;

    private List<UploadNotificacion> notificados;
    private List<UploadNotificacion> notificadosFiltradas;
    private UploadNotificacion notificado;

    private UIData notificadosDataTable;

    private LoginBean login;

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private List<Documento> documentos;
    private List<Documento> documentosFiltrados;

    private String localpath;

    private boolean obsaviso;

    private String expedienteText;
    private String tipoTramite;
    private String casillero;

    private RenewalForm renewalForm;

    private double progressValue;
    private boolean cargaHecha;
    private boolean notexistentes;

    String rutaarchivo = "/var/www/html/solicitudes/media/files/renewal_forms/";
    String rutacasillero = "/var/www/html/casilleros/media/files/";

    private String avisonot;

    private List<Documento> archivos;

    public UploadCertBean() {
        loadData();
    }

    private void loadData() {

        obsaviso = false;

        validaExistenciaPorNotificar();
        log = "";
        file = null;
        notificaciones = new ArrayList<>();
        notificados = new ArrayList<>();
        localpath = "/opt/transferencias_doc/";
        renewalForm = new RenewalForm();
        tipoTramite = "";
        casillero = "";
        avisonot = "Notificados";
    }

    public void validaExistenciaPorNotificar() {
        Controlador c = new Controlador();
        List<UploadNotificacion> ups = c.getNotificacionesByEstado(false);
        if (ups.isEmpty()) {
            activo = false;
        } else {
            activo = true;
        }
        login = c.getLogin();
    }

    public void handleFileUpload(FileUploadEvent event) throws JSchException, IllegalAccessException, IOException {
        file = event.getFile();
        FacesMessage message = null;
        if (uploadDocumentoToExpedient()) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DOCUMENTO SUBIDO CORRECTAMENTE");
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL SUBIR EL DOCUMENTO");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public boolean uploadDocumentoToExpedient() throws IOException {
        if (file != null) {
            String pdfName = file.getFileName();
            pdfName = pdfName.trim().replace(" ", "_");
            String ruta = "/var/www/html/solicitudes/media/files/renewal_forms/" + renewalForm.getId() + "/" + pdfName;
            Codekru cod = new Codekru(130);
            cod.copyAInputStreamToRemoteMachine(file.getInputStream(), ruta);
//                Files.copy(file.getInputStream(), fileaux.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } else {
            return false;
        }
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (renewalForm != null && renewalForm.getId() != null) {
            //cia.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(renewalForm.getId(), renewalForm.getApplicationNumber());
            if (archivos.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRÓ EL EXPEDIENTE");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "EXPEDIENTE CARGADO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR EL EXPEDIENTE");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarExpediente(ActionEvent ae) {
        FacesMessage message = null;
        if (expedienteText != null && !expedienteText.trim().isEmpty()) {
            Controlador c = new Controlador();
            renewalForm = c.getRenewalFormsByApplicationNumber(expedienteText);
            if (renewalForm.getId() != null) {
                Types t = c.getTypes(renewalForm.getTransactionMotiveId());
                tipoTramite = t.getName().trim().toUpperCase();
                casillero = c.getCasilleroSenadiByOwnerId(renewalForm.getOwnerId()) + "";

                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE ENCONTRÓ EL EXPEDIENTE");
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCUENTRA EL EXPEDIENTE");
            }
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "CAMPO VACÍO");
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void subirDocumentos(ActionEvent ae) throws JSchException, IllegalAccessException, IOException {
        FacesMessage message = null;
        if (documentos != null && !documentos.isEmpty()) {

            double val = 100.0 / documentos.size();
            progressValue = 0;

            for (int i = 0; i < documentos.size(); i++) {
                Documento doc = documentos.get(i);
                if (uploadDocumentToCorrespondingFolder(doc)) {
                    documentos.get(i).setEstado(true);
                    notexistentes = true;
                }
                obsaviso = true;
                progressValue += val;
//                System.out.println(progressValue + "%");
            }
            progressValue = 100;
//            System.out.println(progressValue + "%");
            cargaHecha = false;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "Acción Completada.");
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO EXISTEN DOCUMENTOS EN LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onComplete() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Se ha terminado de analizar/subir todos los documentos"));
    }

    public void onCompleteN() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Se ha terminado de notificar los documentos"));
    }

    public boolean uploadDocumentToCorrespondingFolder(Documento doc) throws JSchException, IllegalAccessException, IOException {

        String pdfName = doc.getDocumento();

        if (pdfName.contains("signed")) {
            String tramite = Operaciones.getTramiteFromPdfName(pdfName);
            if (tramite.trim().isEmpty()) {
                log += "No se encuentra el casillero para archivo: " + pdfName + "\n";
                doc.setObservacion("No se encuentra el casillero para el archivo");
                removerDocumentoDeFolder(doc);
            } else {
                Controlador c = new Controlador();
                if (!c.validarExistenciaUploadDocumento(pdfName, true)) {
                    RenewalForm rd = c.getRenewalFormsByApplicationNumber(tramite);
                    if (rd.getId() != null) {
                        String ruta = rutaarchivo + rd.getId() + "/";

                        FTPFiles ftpf = new FTPFiles(130);

                        if (ftpf.validateFolderExists(ruta)) {

                            if (ftpf.doCopyFromLocalToRemote(localpath + pdfName, ruta)) {
                                System.out.println("Si se copió " + tramite + " en: " + ruta);

                                UploadNotificacion not = new UploadNotificacion();
                                not.setRenewalFormId(rd.getId());
                                not.setDocumento(pdfName);
                                not.setSolicitud(tramite);
                                not.setCasillero(c.getCasilleroSenadiByOwnerId(rd.getOwnerId()));
                                not.setEstado(false);
                                not.setUsuario(login.getNombre());
                                not.setActivo(true);

                                if (!c.validarExistenciaUploadNotificacion(not.getSolicitud(), pdfName)) {
                                    if (c.saveUploadNotificacion(not)) {
                                        log += "Se subió correctamente el archivo: " + pdfName + "\n";
                                        notificaciones.add(not);
                                        removerDocumentoDeFolder(doc);
                                        return true;
                                    } else {
                                        log += "Se subió correctamente el archivo: " + pdfName + ", pero no se guardo en el historial, 'INFORMAR'\n";
                                        System.out.println("Se subió correctamente el archivo: " + pdfName + ", pero no se guardo en el historial, 'INFORMAR'");
                                        doc.setObservacion("Se subió correctamente el archivo, pero no se guardo en el historial, 'INFORMAR'");
                                        removerDocumentoDeFolder(doc);
                                    }
                                } else {
                                    log += "Ya existe el uploadnotificacion del archivo: " + pdfName + "\n";
                                    doc.setObservacion("Ya existe el uploadnotificacion del archivo:");
                                    removerDocumentoDeFolder(doc);
                                }
                            } else {
                                log += "No se subió el archivo: " + pdfName + "\n";
                                doc.setObservacion("No se subió el archivo.");
                                removerDocumentoDeFolder(doc);
                            }

                        } else {
//                            System.out.println("No existe ruta para archivo " + pdfName);
                            doc.setObservacion("No existe ruta para archivo.");
                            log += "No existe ruta para archivo " + pdfName + "\n";
                            removerDocumentoDeFolder(doc);
                        }
                    } else {
                        log += "No se encontró la ruta del expediente del documento " + pdfName + "\n";
                        doc.setObservacion("No se encontró la ruta del expediente del documento.");
                        removerDocumentoDeFolder(doc);
                    }
                } else {
                    log += "El trámite " + tramite + ", ya ha sido notificado anteriormente\n";
                    doc.setObservacion("El trámite " + tramite + ", ya ha sido notificado anteriormente");
                    removerDocumentoDeFolder(doc);
                }
            }
        } else {
            log += "El documento " + pdfName + " debe estar firmado para notificarse\n";
            doc.setObservacion("El documento " + pdfName + " debe estar firmado para notificarse\n");
            removerDocumentoDeFolder(doc);
            //System.out.println("El documento " + pdfName + " debe estar firmado para notificarse\n");
        }
        return false;
    }

    public boolean removerDocumentoDeFolder(Documento doc) {
        System.out.println("quitando archivo de carpeta: " + doc.getPath());
        File archivo = new File(doc.getPath());
        return archivo.delete();
    }

    public void prepararNuevo(ActionEvent ae) {
        loadData();
    }

    public void notificarCertificados(ActionEvent ae) throws JSchException, IllegalAccessException, IOException {
        FacesMessage message = null;
        if (notificaciones != null && !notificaciones.isEmpty()) {
//            String rutaarchivo = "/var/www/html/solicitudes/media/files/renewal_forms/";
//            String rutacasillero = "/var/www/html/casilleros/media/files/";

            FTPFiles ftpf = new FTPFiles(130);
            Controlador c = new Controlador();

            double val = 100.0 / notificaciones.size();
            progressValue = 0;

            for (int i = 0; i < notificaciones.size(); i++) {
                System.out.println(i + ":------------------------------");
                UploadNotificacion un = notificaciones.get(i);

                if (ftpf.validateFolderExists(rutaarchivo + un.getRenewalFormId())) {
                    if (ftpf.validateFolderExists(rutacasillero + un.getCasillero())) {
                        String renewalcert = rutaarchivo + un.getRenewalFormId() + "/" + un.getDocumento();
                        String casillerofolder = rutacasillero + un.getCasillero() + "/"+un.getDocumento().trim().replace(" ", "_");
                        System.out.println(renewalcert + "\n" + casillerofolder);

                        if (ftpf.exeComando("cp " + renewalcert + " " + casillerofolder + " && echo 'movido'")) {
                            System.out.println("Se ha copiado el archivo de " + renewalcert + " a " + casillerofolder + " correctamente");

                            NotificationsDAO nd = new NotificationsDAO();

                            Notifications n = new Notifications();
                            n.setNot_id(3);
                            n.setMat_id(13);
                            n.setMatter(un.getSolicitud());
                            n.setDocument(un.getDocumento());
                            n.setCreateDt(Operaciones.getCurrentTimeStamp());
                            n.setSource("LOCAL");
                            n.setCreated_id(1);

                            if (nd.saveNotifications(n)) {
                                LockerNotifications ln = new LockerNotifications();
                                ln.setLockerId(un.getCasillero());
                                ln.setStatus("SENT");
                                ln.setDocument(un.getDocumento());
                                Notifications nbd = nd.getNotificationsByMatterAndCreateDt(n.getMatter(), n.getCreateDt());
                                if (nbd.getId() != null) {
                                    ln.setNotification_id(nbd.getId());
                                    un.setNotificacionsId(nbd.getId());
                                    boolean encontrardoc = true;
                                    if (nd.saveLockerNotifications(ln)) {

                                        String rutadoccas = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + ln.getLockerId() + "/" + ln.getDocument();
                                        CambioDomicilio camd = c.getCambioDomicilioBySolicitud(un.getSolicitud());
                                        if (camd.getId() != null) {
                                            int conf = Operaciones.validaTextoEnPdf(rutadoccas, "CERTIFICADO DE CAMBIO DE DOMICILIO");
                                            if (conf == 1) {
                                                camd.setCertificadoEmitido(true);
                                                c.saveHistorial("CERTIFICADO_CD", "CERTIFICADO_CD", camd.getSolicitud(), "CERTIFICADO EMITIDO " + un.getDocumento(), 0, login.getNombre());
                                                System.out.println("certificado para " + camd.getSolicitud() + " emitido");
                                                un.setTipo("CERTIFICADO CAMBIO DOMICILIO");
                                            }
                                            int confPro = Operaciones.validaTextoEnPdf(rutadoccas, "ampliación al término");
                                            if (confPro == 1) {
                                                camd.setProrrogaNotificada(true);
                                                c.saveHistorial("PRORROGA_CD", "PRORROGA_CD", camd.getSolicitud(), "PRÓRROGA NOTIFICADA " + un.getDocumento(), 0, login.getNombre());
                                                System.out.println("prórroga para " + camd.getSolicitud() + " notificada");
                                                un.setTipo("PRORROGA CAMBIO DOMICILIO");
                                                c.updateCambioDomicilio(camd);
                                            } else {
                                                conf = Operaciones.validaTextoEnPdf(rutadoccas, "Declarar el abandono de la solicitud");
                                                if (conf == 1) {
                                                    camd.setAbandonoNotificado(true);
                                                    c.saveHistorial("ABANDONO_CD", "ABANDONO_CD", camd.getSolicitud(), "ABANDONO EMITIDA " + un.getDocumento(), 0, login.getNombre());
                                                    System.out.println("abandono para " + camd.getSolicitud() + " emitido");
                                                    un.setTipo("ABANDONO CAMBIO DOMICILIO");
                                                } else if (conf == 0) {
                                                    camd.setNotificacionEmitida(true);
                                                    c.saveHistorial("NOTIFICACION_CD", "NOTIFICACION_CD", camd.getSolicitud(), "NOTIFICACIÓN EMITIDA " + un.getDocumento(), 0, login.getNombre());
                                                    System.out.println("notificación para " + camd.getSolicitud() + " emitido");
                                                    un.setTipo("NOTIFICADA CAMBIO DOMICILIO");
                                                }

                                                if (conf == 1 || conf == 0) {
                                                    c.updateCambioDomicilio(camd);
                                                }
                                            }
                                        } else {
                                            CambioNombre cambn = c.getCambioNombreBySolicitud(un.getSolicitud());
                                            if (cambn.getId() != null) {
                                                int conf = Operaciones.validaTextoEnPdf(rutadoccas, "CERTIFICADO DE CAMBIO DE NOMBRE DEL");
                                                if (conf == 1) {
                                                    cambn.setCertificadoEmitido(true);
                                                    c.saveHistorial("CERTIFICADO_CN", "CERTIFICADO_CN", cambn.getSolicitud(), "CERTIFICADO EMITIDO " + un.getDocumento(), 0, login.getNombre());
                                                    System.out.println("certificado para " + cambn.getSolicitud() + " emitido");
                                                    un.setTipo("CERTIFICADO CAMBIO NOMBRE");
                                                }
                                                int confProCN = Operaciones.validaTextoEnPdf(rutadoccas, "ampliación al término");
                                                if (confProCN == 1) {
                                                    cambn.setProrrogaNotificada(true);
                                                    c.saveHistorial("PRORROGA_CN", "PRORROGA_CN", cambn.getSolicitud(), "PRÓRROGA NOTIFICADA " + un.getDocumento(), 0, login.getNombre());
                                                    System.out.println("prórroga para " + cambn.getSolicitud() + " notificada");
                                                    un.setTipo("PRORROGA CAMBIO NOMBRE");
                                                    c.updateCambioNombre(cambn);
                                                } else {
                                                    conf = Operaciones.validaTextoEnPdf(rutadoccas, "Declarar el abandono de la solicitud");
                                                    if (conf == 1) {
                                                        cambn.setAbandonoNotificado(true);
                                                        c.saveHistorial("ABANDONO_CN", "ABANDONO_CN", cambn.getSolicitud(), "ABANDONO EMITIDA " + un.getDocumento(), 0, login.getNombre());
                                                        System.out.println("abandono para " + cambn.getSolicitud() + " emitido");
                                                        un.setTipo("ABANDONO CAMBIO NOMBRE");
                                                    } else if (conf == 0) {
                                                        cambn.setNotificacionEmitida(true);
                                                        c.saveHistorial("NOTIFICACION_CN", "NOTIFICACION_CN", cambn.getSolicitud(), "NOTIFICACIÓN EMITIDA " + un.getDocumento(), 0, login.getNombre());
                                                        System.out.println("notificación para " + cambn.getSolicitud() + " emitido");
                                                        un.setTipo("NOTIFICADA CAMBIO NOMBRE");
                                                    }
                                                    if (conf == 1 || conf == 0) {
                                                        c.updateCambioNombre(cambn);
                                                    }
                                                }
                                            } else {
                                                PrendaComercial prend = c.getPrendaComercialBySolicitud(un.getSolicitud());
                                                if (prend.getId() != null) {
                                                    //Cambiar aquí, si ya cambia el contendido en el certificado de prenda comercial
                                                    int conf = Operaciones.validaTextoEnPdf(rutadoccas, "DE PRENDA COMERCIAL");
                                                    if (conf == 1) {
                                                        if (prend.getEmail() != null && !prend.getEmail().isEmpty()) {
                                                            sendMail(un, prend.getEmail());
                                                        }
                                                        prend.setCertificadoEmitido(true);
                                                        c.saveHistorial("CERTIFICADO_PRENDA", "CERTIFICADO_PRENDA", prend.getSolicitud(), "CERTIFICADO EMITIDO " + un.getDocumento(), 0, login.getNombre());
                                                        un.setTipo("CERTFICADO PRENDA");
                                                    }
                                                    int confProPre = Operaciones.validaTextoEnPdf(rutadoccas, "ampliación al término");
                                                    if (confProPre == 1) {
                                                        prend.setProrrogaNotificada(true);
                                                        c.saveHistorial("PRORROGA_PRENDA", "PRORROGA_PRENDA", prend.getSolicitud(), "PRÓRROGA NOTIFICADA " + un.getDocumento(), 0, login.getNombre());
                                                        System.out.println("prórroga para " + prend.getSolicitud() + " notificada");
                                                        un.setTipo("PRORROGA PRENDA COMERCIAL");
                                                        c.updatePrendaComercial(prend);
                                                    } else {
                                                        conf = Operaciones.validaTextoEnPdf(rutadoccas, "Declarar el abandono de la solicitud");
                                                        if (conf == 1) {
                                                            prend.setAbandonoNotificado(true);
                                                            c.saveHistorial("ABANDONO_PRENDA", "ABANDONO_PRENDA", prend.getSolicitud(), "ABANDONO EMITIDA " + un.getDocumento(), 0, login.getNombre());
                                                            System.out.println("abandono para " + prend.getSolicitud() + " emitido");
                                                            un.setTipo("ABANDONO PRENDA COMERCIAL");
                                                        } else if (conf == 0) {
                                                            prend.setNotificacionEmitida(true);
                                                            c.saveHistorial("NOTIFICACION_PRENDA", "NOTIFICACION_PRENDA", prend.getSolicitud(), "NOTIFICACIÓN EMITIDA " + un.getDocumento(), 0, login.getNombre());
                                                            un.setTipo("NOTIFICADA PRENDA");
                                                        }
                                                        if (conf == 1 || conf == 0) {
                                                            c.updatePrendaComercial(prend);
                                                        }
                                                    }
                                                } else {
                                                    LicenciaUso licu = c.getLicenciaUsoBySolicitud(un.getSolicitud());
                                                    if (licu.getId() != null) {
                                                        int conf = Operaciones.validaTextoEnPdf(rutadoccas, "CONTRATO DE LICENCIA DE USO DE MARCA ENTRE:");
                                                        if (conf == 1) {
                                                            if (licu.getCasilleroSenadiLicenciatario() != null && !licu.getCasilleroSenadiLicenciatario().trim().isEmpty()) {
                                                                notificarAlCasillero(un, licu.getCasilleroSenadiLicenciatario());
                                                            } else if (licu.getEmail() != null && !licu.getEmail().trim().isEmpty()) {
                                                                sendMail(un, licu.getEmail());
                                                            }

                                                            licu.setCertificadoEmitido(true);
                                                            c.saveHistorial("CERTIFICADO_LICENCIA", "CERTIFICADO_LICENCIA", licu.getSolicitud(), "INSCRIPCIÓN EMITIDA " + un.getDocumento(), 0, login.getNombre());
                                                            un.setTipo("CERTIFICADO LICENCIA");
                                                        }
                                                        conf = Operaciones.validaTextoEnPdf(rutadoccas, "LEVANTAMIENTO DE CONVENIO DE LICENCIA");
                                                        if (conf == 1) {
                                                            if (licu.getCasilleroSenadi() != null && !licu.getCasilleroSenadi().trim().isEmpty()) {
                                                                notificarAlCasillero(un, licu.getCasilleroSenadi());
                                                            } else if (licu.getEmail() != null && !licu.getEmail().trim().isEmpty()) {
                                                                sendMail(un, licu.getEmail());
                                                            }
                                                            licu.setTerminacionNotificada(true);
                                                            c.saveHistorial("TERMINACION_LICENCIA", "TERMINACION_LICENCIA", licu.getSolicitud(), "INSCRIPCIÓN EMITIDA " + un.getDocumento(), 0, login.getNombre());
                                                            un.setTipo("TERMINACION LICENCIA");
                                                        }
                                                        int confProLic = Operaciones.validaTextoEnPdf(rutadoccas, "ampliación al término");
                                                        if (confProLic == 1) {
                                                            licu.setProrrogaNotificada(true);
                                                            c.saveHistorial("PRORROGA_LICENCIA", "PRORROGA_LICENCIA", licu.getSolicitud(), "PRÓRROGA NOTIFICADA " + un.getDocumento(), 0, login.getNombre());
                                                            System.out.println("prórroga para " + licu.getSolicitud() + " notificada");
                                                            un.setTipo("PRORROGA LICENCIA USO");
                                                            c.updateLicenciaUso(licu);
                                                        } else {
                                                            conf = Operaciones.validaTextoEnPdf(rutadoccas, "Declarar el abandono de la solicitud");
                                                            if (conf == 1) {
                                                                licu.setAbandonoNotificado(true);
                                                                c.saveHistorial("ABANDONO_LICENCIA", "ABANDONO_LICENCIA", licu.getSolicitud(), "ABANDONO EMITIDA " + un.getDocumento(), 0, login.getNombre());
                                                                System.out.println("abandono para " + licu.getSolicitud() + " emitido");
                                                                un.setTipo("ABANDONO LICENCIA USO");
                                                            } else if (conf == 0) {
                                                                licu.setNotificacionEmitida(true);
                                                                c.saveHistorial("NOTIFICACION_LICENCIA", "NOTIFICACION_LICENCIA", licu.getSolicitud(), "NOTIFICACIÓN EMITIDA " + un.getDocumento(), 0, login.getNombre());
                                                                un.setTipo("NOTIFICADA LICENCIA");
                                                            }
                                                            if (conf == 1 || conf == 0) {
                                                                c.updateLicenciaUso(licu);
                                                            }
                                                        }
                                                    } else {
                                                        SubLicenciaUso subl = c.getSublicenciaUsoBySolicitud(un.getSolicitud());
                                                        if (subl.getId() != null) {
                                                            int conf = Operaciones.validaTextoEnPdf(rutadoccas, "INSCRIPCIÓN SUBLICENCIA DE USO No.");
                                                            if (conf == 1) {
                                                                subl.setCertificadoEmitido(true);
                                                                c.saveHistorial("CERTIFICADO_SUBLICENCIA", "CERTIFICADO_SUBLICENCIA", subl.getSolicitud(), "INSCRIPCIÓN EMITIDA " + un.getDocumento(), 0, login.getNombre());
                                                                un.setTipo("CERTIFICADO SUBLICENCIA");
                                                            }
                                                            int confProSub = Operaciones.validaTextoEnPdf(rutadoccas, "ampliación al término");
                                                            if (confProSub == 1) {
                                                                subl.setProrrogaNotificada(true);
                                                                c.saveHistorial("PRORROGA_SUBLICENCIA", "PRORROGA_SUBLICENCIA", subl.getSolicitud(), "PRÓRROGA NOTIFICADA " + un.getDocumento(), 0, login.getNombre());
                                                                System.out.println("prórroga para " + subl.getSolicitud() + " notificada");
                                                                un.setTipo("PRORROGA SUBLICENCIA USO");
                                                                c.updateSublicenciaUso(subl);
                                                            } else {
                                                                conf = Operaciones.validaTextoEnPdf(rutadoccas, "Declarar el abandono de la solicitud");
                                                                if (conf == 1) {
                                                                    subl.setAbandonoNotificado(true);
                                                                    c.saveHistorial("ABANDONO_SUBLICENCIA", "ABANDONO_SUBLICENCIA", subl.getSolicitud(), "ABANDONO EMITIDA " + un.getDocumento(), 0, login.getNombre());
                                                                    System.out.println("abandono para " + subl.getSolicitud() + " emitido");
                                                                    un.setTipo("ABANDONO SUBLICENCIA USO");
                                                                } else if (conf == 0) {
                                                                    subl.setNotificacionEmitida(true);
                                                                    c.saveHistorial("NOTIFICACION_SUBLICENCIA", "NOTIFICACION_SUBLICENCIA", subl.getSolicitud(), "NOTIFICACIÓN EMITIDA " + un.getDocumento(), 0, login.getNombre());
                                                                    un.setTipo("NOTIFICADA SUBLICENCIA");
                                                                }
                                                                if (conf == 1 || conf == 0) {
                                                                    c.updateSublicenciaUso(subl);
                                                                }
                                                            }
                                                        } else {
                                                            //para indicar que se ha entregado certificado digital firmado
                                                            Transferencia tranaux = c.getTransferenciaBySolSenadi(un.getSolicitud());
                                                            if (tranaux.getId() != null) {
                                                                tranaux.setCertificadoEmitido(true);
                                                                c.updateTransferencia(tranaux);
                                                                c.saveHistorial("TRANSFERENCIA", "TRANSFERENCIA", tranaux.getSolicitud(), "CERTIFICADO EMITIDO " + un.getDocumento(), 0, login.getNombre());
                                                                un.setTipo("CERTIFICADO TRANSFERENCIA");
                                                            } else {
                                                                //para indicar que se ha realizado una notificación digital firmado
                                                                Notificacion notaux = c.getNotificacionBySolSenadi(un.getSolicitud());
                                                                if (notaux.getId() != null) {
                                                                    notaux.setNotificacionEmitida(true);
                                                                    c.updateNotificacion(notaux);
                                                                    c.saveHistorial("NOTIFICACION", "NOTIFICACION", notaux.getSolicitud(), "NOTIFICACIÓN EMITIDA " + un.getDocumento(), 0, login.getNombre());
                                                                    un.setTipo("NOTIFICADA TRANSFERENCIA");
                                                                } else {
                                                                    Caducada cadaux = c.getCaducadaBySolSenadi(un.getSolicitud());
                                                                    if (cadaux.getId() != null) {
                                                                        cadaux.setNotificadaCaducada(true);
                                                                        c.updateCaducada(cadaux);
                                                                        c.saveHistorial("CADUCADA", "CADUCADA", cadaux.getSolicitud(), "NOTIFICACIÓN CADUCADA EMITIDA " + un.getDocumento(), 0, login.getNombre());
                                                                        un.setTipo("CADUCADA TRANSFERENCIA");
                                                                    } else {
                                                                        Abandono abaxu = c.getAbandonoBySolSenadi(un.getSolicitud());
                                                                        if (abaxu.getId() != null) {
                                                                            abaxu.setAbandonoNotificado(true);
                                                                            c.updateAbandono(abaxu);
                                                                            c.saveHistorial("ABANDONO", "ABANDONO", abaxu.getSolicitud(), "ABANDONO EMITIDO " + un.getDocumento(), 0, login.getNombre());
                                                                            un.setTipo("ABANDONO TRANSFERENCIA");
                                                                        } else {
                                                                            Prorroga proaux = c.getProrrogaBySolicitud(un.getSolicitud());
                                                                            if (proaux.getId() != null) {
                                                                                proaux.setProrrogaNotificada(true);
                                                                                c.updateProrroga(proaux);
                                                                                c.saveHistorial("PRORROGA", "PRORROGA", proaux.getSolicitud(), "PRÓRROGA NOTIFICADA " + un.getDocumento(), 0, login.getNombre());
                                                                                un.setTipo("PRORROGA TRANSFERENCIA");
                                                                            } else {
                                                                                encontrardoc = false;
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (encontrardoc) {
                                            un.setObservacion("Notificado correctamente");
                                            un.setEstado(true);
                                            un.setFechaNotificacion(new Date());
                                        } else {
                                            un.setObservacion("No se encontró el registro en bd");
                                            un.setEstado(false);
                                        }

                                    } else {
                                        System.out.println("Si se guardó notifications pero locker no");
                                        un.setObservacion("No se completó la notificación " + un.getDocumento() + ", al casillero " + un.getCasillero() + ", no se guardó ln");
                                    }
                                } else {
                                    System.err.println("No se encontró la notificación " + n.getMatter() + ", " + n.getCreateDt());
                                    un.setObservacion("No se completó la notificación " + un.getDocumento() + ", al casillero " + un.getCasillero());
                                }
                            } else {
                                System.err.println("No se guardó la notificación " + n.getMatter());
                                un.setObservacion("No se completó la notificación " + un.getDocumento() + ", al casillero " + un.getCasillero() + "no se guardó n");
                            }
                        } else {
                            System.out.println("No se copió/movió el documento " + un.getDocumento() + ", al casillero " + un.getCasillero());
                            un.setObservacion("No se pudo notificar el documento " + un.getDocumento() + ", al casillero " + un.getCasillero());
                        }
                    } else {
                        un.setObservacion("No se encuentra la ruta al casillero " + un.getCasillero() + ", del trámite " + un.getDocumento());
                    }
                } else {
                    un.setObservacion("No se encuentra ruta al archivo subido " + un.getDocumento());
                }

                c.updateUploadNotificacion(un);
                progressValue += val;
            }
            progressValue = 100;

            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "NOTIFICADOS", "ACCIÓN COMPLETADA");

        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY DOCUMENTOS CARGADOS");
        }

        FacesContext.getCurrentInstance()
                .addMessage(null, message);
    }

    public boolean notificarAlCasillero(UploadNotificacion un, String casillero) {

        String renewalcert = rutaarchivo + un.getRenewalFormId() + "/" + un.getDocumento();
        String casillerofolder = rutacasillero + casillero + "/";

        FTPFiles ftpf = new FTPFiles(130);

        if (ftpf.exeComando("cp " + renewalcert + " " + casillerofolder + " && echo 'movido'")) {
            System.out.println("Se ha copiado el archivo de " + renewalcert + " a " + casillerofolder + " correctamente");

            NotificationsDAO nd = new NotificationsDAO();

            Notifications n = new Notifications();
            n.setNot_id(3);
            n.setMat_id(13);
            n.setMatter(un.getSolicitud());
            n.setDocument(un.getDocumento());
            n.setCreateDt(Operaciones.getCurrentTimeStamp());
            n.setSource("LOCAL");
            n.setCreated_id(1);
            if (nd.saveNotifications(n)) {
                LockerNotifications ln = new LockerNotifications();
                ln.setLockerId(Integer.valueOf(casillero));
                ln.setStatus("SENT");
                ln.setDocument(un.getDocumento());
                Notifications nbd = nd.getNotificationsByMatterAndCreateDt(n.getMatter(), n.getCreateDt());
                if (nbd.getId() != null) {
                    ln.setNotification_id(nbd.getId());
//                    un.setNotificacionsId(nbd.getId());
                    if (nd.saveLockerNotifications(ln)) {
                        System.out.println("Se ha guardado la notificación al casillero " + casillero + " del documento " + un.getDocumento() + " satisfactoriamente");
                        return true;
                    } else {
                        System.err.println("Se guardó la notificación, pero no se guardó la locker_notification");
                        un.setObservacion("Se registró la notificación, pero no el locker_notification (segundo casillero), notification: " + nbd.getId());
                    }
                } else {
                    System.err.println("Se guardó notification, pero no se recuperó su id asociado en bd " + n.getCreateDt());
                    un.setObservacion("Se guardó notification, pero no se recuperó su id asociado en bd " + n.getCreateDt() + " (segundo casillero)");
                }
            } else {
                System.err.println("No se guardó la notificación en la base de datos ");
                un.setObservacion("No se registró la notificación " + un.getDocumento() + ", al casillero " + un.getCasillero() + " (segundo casillero)");
            }

        } else {
            System.err.println("Hubo un problema al copiar el archivo de " + renewalcert + " a " + casillerofolder + ".");

        }

        return false;
    }

    public boolean sendMail(UploadNotificacion un, String mailTo) throws IOException {
        Mailer mailer = new Mailer();
        String url = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + un.getCasillero() + "/" + un.getDocumento();
        String htmlBody = "<br/>Estimado usuario,<br/><br/>Sírvase a encontrar la notificación adjunta.";
        String mailFrom = "casilleros@senadi.gob.ec";
//        String mailTo = "micjays6887@gmail.com";

        if (mailer.sendEmail(url, mailFrom, mailTo, "NOTIFICACIÓN - SENADI", htmlBody, un.getDocumento())) {

            System.out.println("Correo con documento " + un.getDocumento() + " enviado a: " + mailTo);
            return true;
        } else {
            System.out.println("No se envió el correo a " + mailTo + ", documento: " + un.getDocumento());
            return false;
        }
    }

    public void prepararNotificar(ActionEvent ae) {
        FacesMessage message = null;
        Controlador c = new Controlador();
        notificaciones = c.getNotificacionesByEstado(false);
        progressValue = 0;

        PrimeFaces.current().ajax().addCallbackParam("uploadn", true);
        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "NOTIFICADOS", "MOSTRANDO DOCUMENTOS SUBIDOS");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void prepararSubirDocumento() {
        renewalForm = new RenewalForm();
        tipoTramite = "";
        casillero = "";
        expedienteText = "";
    }

    public void prepararNotificados(ActionEvent ae) {
        FacesMessage message = null;
        Controlador c = new Controlador();
        notificados = new ArrayList<>();
        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "NOTIFICADOS", "MOSTRANDO DOCUMENTOS NOTIFICADOS");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void buscarNotificados(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {

            Controlador c = new Controlador();
            boolean aux = true;
            if (!avisonot.equals("Notificados")) {
                aux = false;
            }
            notificados = c.getUploadNotificacionByCriterio(criterio, aux);
            if (notificados.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarNotificadosPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            boolean aux = true;
            if (!avisonot.equals("Notificados")) {
                aux = false;
            }

            notificados = c.getUploadNotificacionByDate(fechaInicio, fechaFin, aux);
            if (notificados.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public boolean validarFechas() {
        try {
            fechaInicio.toString();
            fechaFin.toString();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void cargarDocumentosFtp(ActionEvent ae) throws IOException, JSchException, IllegalAccessException {
        FacesMessage message = null;
        File filelocal = new File(localpath);
        documentos = new ArrayList<>();
        if (filelocal.exists()) {
            File[] docs = filelocal.listFiles();
            if (docs.length == 0) {
                activo = false;
                obsaviso = false;
            } else {
                for (int i = 0; i < docs.length; i++) {
                    Documento documento = new Documento();
                    documento.setDocumento(docs[i].getName());
                    documento.setPath(docs[i].getAbsolutePath());
                    documentos.add(documento);
                }
                activo = true;
                cargaHecha = true;
                obsaviso = true;
            }
        }
//        } else {
        ////            validaExistenciaPorNotificar()
//            //activo = false;
//        }
//        validaExistenciaPorNotificar();
//        Controlador c = new Controlador();
//        if (c.getNotificacionesByEstado(false).isEmpty()) {
//            notexistentes = false;
//        } else {
//            notexistentes = true;
//        }

//        obsaviso = false;
        if (obsaviso) {

            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE CARGARON TODOS LOS DOCUMENTOS CORRECTAMENTE");
        } else if (activo && !obsaviso) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO EXISTEN DOCUMENTOS PARA CARGAR, PERO EXISTEN NOTIFICACIONES PENDIENTES, HAGA CLIC EN NOTIFICAR");
        } else if (!activo && !notexistentes) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON DOCUMENTOS PARA CARGAR");
        } else if (notexistentes) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "EXISTEN TRÁMITES PENDIENTES PARA NOTIFICAR, DE CLIC EN 'NOTIFICAR'");
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "Se cargaron todos los documentos correctamente");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void darDeBaja(ActionEvent ae) {
        FacesMessage msg = null;
        notificado = (UploadNotificacion) notificadosDataTable.getRowData();
        if (notificado != null) {
            System.out.println("llega por aquí");
            Controlador c = new Controlador();
            if (c.downLockerNotifications(notificado)) {

                notificado.setActivo(false);
                notificado.setEstado(false);
                notificado.setObservacion("");
                String tramite = notificado.getSolicitud();
                notificado.setSolicitud(notificado.getSolicitud() + "_bad");

                if (c.updateUploadNotificacion(notificado)) {
                    // quita de la visualización de la pestaña que le corresponde el documento dado de baja
                    c.anularDocumentoModificacion(notificado, tramite, login.getNombre());
                    if (validarFechas()) {
                        notificados = c.getUploadNotificacionByDate(fechaInicio, fechaFin, true);
                    } else {
                        notificados = c.getUploadNotificacionByCriterio(criterio, true);
                    }

                    System.out.println(notificado.getSolicitud() + " dado de baja de notificadas");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE HA DADO DE BAJA A LA NOTIFICACIÓN DEL TRÁMITE " + notificado.getSolicitud());
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE HA DADO DE BAJA A LA NOTIFICACIÓN DEL TRÁMITE " + notificado.getSolicitud() + ", PERO NO SE HA DESHABILITADO DE LA APP DE TRANSFERENCIAS");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO DAR DE BAJA A LA NOTIFICACIÓN DEL TRÁMITE " + notificado.getSolicitud());
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR LA NOTIFICACIÓN");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return the files
     */
    public UploadedFiles getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(UploadedFiles files) {
        this.files = files;
    }

    /**
     * @return the file
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }

    /**
     * @return the activo
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * @param activo the activo to set
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * @return the log
     */
    public String getLog() {
        return log;
    }

    /**
     * @param log the log to set
     */
    public void setLog(String log) {
        this.log = log;
    }

    /**
     * @return the notificaciones
     */
    public List<UploadNotificacion> getNotificaciones() {
        return notificaciones;
    }

    /**
     * @param notificaciones the notificaciones to set
     */
    public void setNotificaciones(List<UploadNotificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }

    /**
     * @return the notificacionesFiltradas
     */
    public List<UploadNotificacion> getNotificacionesFiltradas() {
        return notificacionesFiltradas;
    }

    /**
     * @param notificacionesFiltradas the notificacionesFiltradas to set
     */
    public void setNotificacionesFiltradas(List<UploadNotificacion> notificacionesFiltradas) {
        this.notificacionesFiltradas = notificacionesFiltradas;
    }

    /**
     * @return the notificacionesDataTable
     */
    public UIData getNotificacionesDataTable() {
        return notificacionesDataTable;
    }

    /**
     * @param notificacionesDataTable the notificacionesDataTable to set
     */
    public void setNotificacionesDataTable(UIData notificacionesDataTable) {
        this.notificacionesDataTable = notificacionesDataTable;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(LoginBean login) {
        this.login = login;
    }

    /**
     * @return the notificados
     */
    public List<UploadNotificacion> getNotificados() {
        return notificados;
    }

    /**
     * @param notificados the notificados to set
     */
    public void setNotificados(List<UploadNotificacion> notificados) {
        this.notificados = notificados;
    }

    /**
     * @return the notificadosFiltradas
     */
    public List<UploadNotificacion> getNotificadosFiltradas() {
        return notificadosFiltradas;
    }

    /**
     * @param notificadosFiltradas the notificadosFiltradas to set
     */
    public void setNotificadosFiltradas(List<UploadNotificacion> notificadosFiltradas) {
        this.notificadosFiltradas = notificadosFiltradas;
    }

    /**
     * @return the notificadosDataTable
     */
    public UIData getNotificadosDataTable() {
        return notificadosDataTable;
    }

    /**
     * @param notificadosDataTable the notificadosDataTable to set
     */
    public void setNotificadosDataTable(UIData notificadosDataTable) {
        this.notificadosDataTable = notificadosDataTable;
    }

    /**
     * @return the criterio
     */
    public String getCriterio() {
        return criterio;
    }

    /**
     * @param criterio the criterio to set
     */
    public void setCriterio(String criterio) {
        this.criterio = criterio;
    }

    /**
     * @return the fechaInicio
     */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /**
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * @return the fechaFin
     */
    public Date getFechaFin() {
        return fechaFin;
    }

    /**
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * @return the filesw
     */
    public UploadedFilesWrapper getFilesw() {
        return filesw;
    }

    /**
     * @param filesw the filesw to set
     */
    public void setFilesw(UploadedFilesWrapper filesw) {
        this.filesw = filesw;
    }

    /**
     * @return the filew
     */
    public UploadedFileWrapper getFilew() {
        return filew;
    }

    /**
     * @param filew the filew to set
     */
    public void setFilew(UploadedFileWrapper filew) {
        this.filew = filew;
    }

    /**
     * @return the documentos
     */
    public List<Documento> getDocumentos() {
        return documentos;
    }

    /**
     * @param documentos the documentos to set
     */
    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }

    /**
     * @return the documentosFiltrados
     */
    public List<Documento> getDocumentosFiltrados() {
        return documentosFiltrados;
    }

    /**
     * @param documentosFiltrados the documentosFiltrados to set
     */
    public void setDocumentosFiltrados(List<Documento> documentosFiltrados) {
        this.documentosFiltrados = documentosFiltrados;
    }

    /**
     * @return the localpath
     */
    public String getLocalpath() {
        return localpath;
    }

    /**
     * @param localpath the localpath to set
     */
    public void setLocalpath(String localpath) {
        this.localpath = localpath;
    }

    /**
     * @return the obsaviso
     */
    public boolean isObsaviso() {
        return obsaviso;
    }

    /**
     * @param obsaviso the obsaviso to set
     */
    public void setObsaviso(boolean obsaviso) {
        this.obsaviso = obsaviso;
    }

    /**
     * @return the notificado
     */
    public UploadNotificacion getNotificado() {
        return notificado;
    }

    /**
     * @param notificado the notificado to set
     */
    public void setNotificado(UploadNotificacion notificado) {
        this.notificado = notificado;
    }

    /**
     * @return the expedienteText
     */
    public String getExpedienteText() {
        return expedienteText;
    }

    /**
     * @param expedienteText the expedienteText to set
     */
    public void setExpedienteText(String expedienteText) {
        this.expedienteText = expedienteText;
    }

    /**
     * @return the renewalForm
     */
    public RenewalForm getRenewalForm() {
        return renewalForm;
    }

    /**
     * @param renewalForm the renewalForm to set
     */
    public void setRenewalForm(RenewalForm renewalForm) {
        this.renewalForm = renewalForm;
    }

    /**
     * @return the tipoTramite
     */
    public String getTipoTramite() {
        return tipoTramite;
    }

    /**
     * @param tipoTramite the tipoTramite to set
     */
    public void setTipoTramite(String tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    /**
     * @return the casillero
     */
    public String getCasillero() {
        return casillero;
    }

    /**
     * @param casillero the casillero to set
     */
    public void setCasillero(String casillero) {
        this.casillero = casillero;
    }

    /**
     * @return the progressValue
     */
    public double getProgressValue() {
        return progressValue;
    }

    /**
     * @param progressValue the progressValue to set
     */
    public void setProgressValue(double progressValue) {
        this.progressValue = progressValue;
    }

    /**
     * @return the cargaHecha
     */
    public boolean isCargaHecha() {
        return cargaHecha;
    }

    /**
     * @param cargaHecha the cargaHecha to set
     */
    public void setCargaHecha(boolean cargaHecha) {
        this.cargaHecha = cargaHecha;
    }

    /**
     * @return the archivos
     */
    public List<Documento> getArchivos() {
        return archivos;
    }

    /**
     * @param archivos the archivos to set
     */
    public void setArchivos(List<Documento> archivos) {
        this.archivos = archivos;
    }

    /**
     * @return the avisonot
     */
    public String getAvisonot() {
        return avisonot;
    }

    /**
     * @param avisonot the avisonot to set
     */
    public void setAvisonot(String avisonot) {
        this.avisonot = avisonot;
    }
}
