/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.bean;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.component.api.UIData;
import senadi.gob.ec.transfweb.modelp.PpdiPersona;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import senadi.gob.ec.transfweb.model.Documento;
import senadi.gob.ec.transfweb.model.Historial;
import senadi.gob.ec.transfweb.model.Rooptions;
import senadi.gob.ec.transfweb.model.TituloCancelado;
import senadi.gob.ec.transfweb.model.UploadNotificacion;
import senadi.gob.ec.transfweb.model.cn.CambioNombre;
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepform.ModificacionApp;
import senadi.gob.ec.transfweb.model.iepform.PaymentReceipt;
import senadi.gob.ec.transfweb.model.iepform.Person;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepform.Types;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author micharesp
 */
@ManagedBean(name = "notificacioncnBean")
@ViewScoped
public class NotificacionCNBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private Date fechaInicioCertificado;
    private Date fechaFinCertificado;

    private List<CambioNombre> notificaciones;
    private List<CambioNombre> notificacionesFiltradas;

    private UIData notificacionesDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private CambioNombre notificacion;

    private LoginBean loginBean;

    private String estadoTemp;
    private String historial;

    private String exportName;

    private List<String> roRazones;
    private String razon;

    private boolean roselectable;

    private List<CambioNombre> selectedNotificaciones;

    private List<Rooptions> roos;
    private UIData roDataTable;

    private String roChoose;

    private boolean separado;

    private String roshow;

    private List<Documento> archivos;

    private String rutaNotificacionCasillero;

    private boolean abandonosS;
    private Date fechaPuestaAbandono;
    private String tipoAbandono;
    private Integer diasProrroga;

    public NotificacionCNBean() {
        loadNotificacionesCN();
    }

    private void loadNotificacionesCN() {
        Controlador c = new Controlador();
        notificaciones = c.getCambiosNombreNotificada("NOTIFICADA");
        numRegistros = "Número Registros Mostrados: " + notificaciones.size();
        exportName = "notificacion_cn_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
        roRazones = Operaciones.getRazonesNotificar();
        razon = "";
        selectedNotificaciones = new ArrayList<>();
    }

    public void buscarNotificacionesCN(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            notificaciones = c.getCambiosNombreByCriteriaAndType(criterio, "NOTIFICADA");
            numRegistros = "Número Registros Mostrados: " + notificaciones.size();
            if (notificaciones.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
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

    public void validarNotificacion(CambioNombre cambion) {
        FacesMessage msg = null;
        if (cambion != null) {
            rutaNotificacionCasillero = "";
            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionBySolicitud(cambion.getSolicitud(), true);
            if (!uploads.isEmpty()) {
                if (uploads.size() > 1) {
                    for (int i = 0; i < uploads.size(); i++) {
                        UploadNotificacion unaux = uploads.get(i);
                        String rutaux = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                        int conf = Operaciones.validaTextoEnPdf(rutaux, "CERTIFICADO DE CAMBIO DE NOMBRE DEL");
                        if (conf == 0) {
                            rutaNotificacionCasillero = rutaux;
                            break;
                        }
                    }
                    if (!rutaNotificacionCasillero.trim().isEmpty()) {
                        PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                        PrimeFaces.current().ajax().addCallbackParam("view", rutaNotificacionCasillero);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICACIÓN CARGADA");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UNA NOTIFICACIÓN SELECCIONADA");
                    }
                } else {
                    UploadNotificacion unaux = uploads.get(0);
                    rutaNotificacionCasillero = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                    System.out.println("rutanotcas: " + rutaNotificacionCasillero);
                    PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                    PrimeFaces.current().ajax().addCallbackParam("view", rutaNotificacionCasillero);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICACIÓN CARGADA");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ NINGUNA NOTIFICACIÓN DEL TRÁMITE " + cambion.getSolicitud());
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UNA NOTIFICACIÓN SELECCIONADA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public boolean validarFechasCertificado() {
        try {
            fechaInicioCertificado.toString();
            fechaFinCertificado.toString();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void buscarNotificacionesPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            notificaciones = c.getCambiosNombreByFechaAndType(fechaInicio, fechaFin, "NOTIFICADA");
            numRegistros = "Número Registros Mostrados: " + notificaciones.size();
            if (notificaciones.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarNotificacionesPorFechaCertificado(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechasCertificado()) {
            Controlador c = new Controlador();
            notificaciones = c.getCambiosNombreByFechaNotificacionAndType(fechaInicioCertificado, fechaFinCertificado, "NOTIFICADA");
            numRegistros = "Número Registros Mostrados: " + notificaciones.size();
            if (notificaciones.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarNotificacion(ActionEvent ae) {
        FacesMessage msg = null;
        notificacion = (CambioNombre) notificacionesDataTable.getRowData();
        if (notificacion != null) {
            Controlador c = new Controlador();
            if (c.removeCambioNombre(notificacion)) {

                c.saveHistorial("NOTIFICADA_CN", "NOTIFICADA_CN ", notificacion.getSolicitud(), "ELIMINADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                loadNotificacionesCN();
                System.out.println("Notificación " + notificacion.getSolicitud() + " Eliminada");

                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICACIÓN " + notificacion.getSolicitud() + "ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR NOTIFICACIÓN");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR NOTIFICACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {
        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
        notificacion = (CambioNombre) notificacionesDataTable.getRowData();
        if (notificacion != null) {
//            System.out.println("fechaaaaaaaaaA: " + notificacion.getFechaPresentacion());
            roselectable = false;
            dialogTitle = "EDITAR NOTIFICACIÓN CAMBIO DE NOMBRE " + notificacion.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar la Notificación_CN: " + notificacion.getSolicitud() + "?";
            Controlador c = new Controlador();
            RenewalForm rf = c.getRenewalFormsByApplicationNumber(notificacion.getSolicitud());
            if (rf.getId() != null) {
                notificacion.setIdRenewalForm(rf.getId());
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICACIÓN_CN CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR NOTIFICACIÓN_CN");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararNuevo(ActionEvent ae) {        
        dialogTitle = "NUEVA NOTIFICACIÓN CAMBIO DE NOMBRE";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar la Nueva Notificación_CN?";
        notificacion = new CambioNombre();
        notificacion.setTipoEstado("NOTIFICADA");
        Controlador c = new Controlador();        
        //notificacion.setNotificacion(c.getNextNumeroNotificacionCN(new Date()));
        notificacion.setResponsable(loginBean.getUsuario().getAlias());
        edicion = false;
        roselectable = false;
        razon = "";
        if (notificacion != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void guardarNotificacion(ActionEvent ae) {
        FacesMessage msg = null;
        if (notificacion != null) {
            Controlador c = new Controlador();
            if (notificacion.getId() != null) {
                if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                    System.out.println("TipoEstado elegido: " + estadoTemp + ", estaba en: NOTIFICADA_CN");
                    if (estadoTemp.equals("CERTIFICADO")) {
                        notificacion.setFechaCertificado(new Date());
                        notificacion.setCertificado(c.getNextCambioNombreCertificado());
//                        notificacion.setObservacion((notificacion.getObservacion()!=null && notificacion.getObservacion().equals("NOTIFICADA")) ? notificacion.getTipoEstado() : notificacion.getObservacion());
                        notificacion.setTipoEstado("CERTIFICADO");
                    } else if (estadoTemp.equals("DESISTIDAS")) {
//                        notificacion.setObservacion((notificacion.getObservacion()!=null && notificacion.getObservacion().equals("NOTIFICADA")) ? notificacion.getTipoEstado() : notificacion.getObservacion());
                        notificacion.setTipoEstado("DESISTIDA");
                    } else {
//                        notificacion.setObservacion((notificacion.getObservacion()!=null && notificacion.getObservacion().equals("NOTIFICADA")) ? notificacion.getTipoEstado() : notificacion.getObservacion());
                        notificacion.setTipoEstado("CADUCADA");
                    }
                    if (c.updateCambioNombre(notificacion)) {
                        c.saveHistorial(notificacion.getTipoEstado() + "_CN", "NOTIFICADA_CN", notificacion.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                        loadNotificacionesCN();
                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICADA_CN EDITADA CON ÉXITO");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA NOTIFICADA_CN");
                    }

                } else {
                    //Editar Notificacion
                    if (c.validarExistenciaCambioNombre(notificacion)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                    } else {

                        if (roselectable) {
                            notificacion.setRo(razon);
                        }
                        notificacion.setSolicitud(notificacion.getSolicitud().trim().toUpperCase());
                        if (c.updateCambioNombre(notificacion)) {
                            c.saveHistorial("NOTIFICADA_CN", "NOTIFICADA_CN", notificacion.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICACIÓN EDITADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA NOTIFICACIÓN");
                        }
                    }
                }

            } else {
                //Guardar Notificacion_cn
                if (c.existeCambioNombre(notificacion.getSolicitud())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                } else {
                    boolean habilitado = true;
                    if (notificacion.getDenominacion() != null && !notificacion.getDenominacion().trim().isEmpty()
                            && notificacion.getRegistro() != null && !notificacion.getRegistro().trim().isEmpty()) {
                        if (c.existsTituloCanceladoByTituloAndDenominacion(notificacion.getRegistro(), notificacion.getDenominacion(), false)) {
                            TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(notificacion.getRegistro(), notificacion.getDenominacion());
                            if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN '"
                                        + notificacion.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                notificacion = new CambioNombre();
                                habilitado = false;
                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                notificacion.setCancelado(titca.getTipoCancelacion());
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN '"
                                        + notificacion.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN '"
                                        + notificacion.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                notificacion = new CambioNombre();
                                habilitado = false;
                            }
                        } else {
                            habilitado = true;
                        }
                    }
                    if (habilitado) {
                        notificacion.setNotificacion(c.getNextNumeroNotificacionCN(new Date()));
                        notificacion.setSolicitud(notificacion.getSolicitud().toUpperCase());
                        notificacion.setTipoEstado("NOTIFICADA");
                        if (c.saveCambioNombre(notificacion)) {
                            c.saveModificacionApp(notificacion.getDenominacion(), notificacion.getRegistro(), notificacion.getSolicitud(), "CAMBIO DE NOMBRE", loginBean.getNombre());
                            c.saveHistorial("NOTIFICADA_CN", "NOTIFICADA_CN", notificacion.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            loadNotificacionesCN();
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICACIÓN GUARDADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL GUARDAR LA NOTIFICACIÓN");
                        }
                    }

                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /* Da la orden de visualizar el reporte, clase Informe (Webservlet)*/
    public void viewNotificacionSi(ActionEvent ae) {
        FacesMessage msg = null;
//        notificacion = (Notificacion) notificacionesDataTable.getRowData();
//        System.out.println("Algo tiene que hacer aquí");
        if (notificacion != null) {

            Controlador c = new Controlador();

            if (!c.validarDelegadoActivo("delegado")) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else if (!c.validarDelegacionActivo()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else if (!c.validarResolucionActiva("transferencia")) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else if (!c.validarDelegadoActivo("secretaria")) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA SECRETARIA ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else {
                if (notificacion.getNotificacion() != null && notificacion.getNotificacion() != 0) {
                    if (!c.getRosBySolicitud(notificacion.getSolicitud()).isEmpty()) {
                        notificacion.setFechaNotificacion(new Date());
                        c.updateCambioNombre(notificacion);

                        System.out.println("Descargando Notificación_cn: " + notificacion.getSolicitud());

                        loginBean.setVarious(false);
                        loginBean.setCambioNombre(notificacion);

                        PrimeFaces.current().ajax().addCallbackParam("doit", true);

                        System.out.println("envía notificación_cn descargar");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO NOTIFICACIÓN_CN " + notificacion.getSolicitud());
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL REGISTRO DEBE TENER UN MOTIVO DE NOTIFICACIÓN");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL REGISTRO DEBE TENER NÚMERO DE NOTIFICACIÓN");
                }

            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "VALIDE QUE LOS DATOS DE LA NOTIFICACIÓN_CN SEAN CORRECTOS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /* Da la orden de visualizar el reporte, clase Informe (Webservlet)*/
    public void viewNotificacionNo(ActionEvent ae) {
        FacesMessage msg = null;
        if (notificacion != null) {
            Controlador c = new Controlador();
            if (!c.validarDelegadoActivo("delegado")) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else if (!c.validarDelegacionActivo()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else if (!c.validarResolucionActiva("transferencia")) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else if (!c.validarDelegadoActivo("secretaria")) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA SECRETARIA ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else {
                if (notificacion.getNotificacion() != null && notificacion.getNotificacion() != 0) {
                    if (!c.getRosBySolicitud(notificacion.getSolicitud()).isEmpty()) {
                        System.out.println("Descargando Notificación_cn: " + notificacion.getSolicitud());

                        loginBean.setVarious(false);
                        loginBean.setCambioNombre(notificacion);

                        PrimeFaces.current().ajax().addCallbackParam("doit", true);

                        System.out.println("envía notificación_cn descargar");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO NOTIFICACIÓN " + notificacion.getSolicitud());
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL REGISTRO DEBE TENER UN MOTIVO DE NOTIFICACIÓN");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL REGISTRO DEBE TENER NÚMERO DE NOTIFICACIÓN");
                }

            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "VALIDE QUE LOS DATOS DE LA NOTIFICACIÓN_CN SEAN CORRECTOS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        notificacion = (CambioNombre) notificacionesDataTable.getRowData();
        if (notificacion != null) {
            dialogTitle = "SEGUIMIENTO " + notificacion.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(notificacion.getSolicitud());
            setHistorial("");
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: NOTIFICADA_CN";
            }
        }
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;

//        System.out.println("LLegando por aquí");
        if (notificacion != null && notificacion.getSolicitud() != null && !notificacion.getSolicitud().trim().isEmpty()) {
//            System.out.println(transferencia.getSolicitud());
            String tramite = notificacion.getSolicitud();
            Controlador c = new Controlador();
            if (c.existeCambioNombre(tramite)) {
                CambioNombre aux = c.getCambioNombreBySolicitud(tramite);
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL TRÁMITE YA SE ENCUENTRA REGISTRADO EN LA PESTAÑA DE " + aux.getTipoEstado());
            } else {
                ModificacionApp mapp = c.getModificacionApp(tramite);
                if (mapp.getId() != null) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE NO SE PUEDE REGISTRAR: " + mapp.getObservacion());
                } else {
                    RenewalForm rf = c.getRenewalFormsByApplicationNumber(tramite);

                    if (rf.getId() != null) {
                        if (rf.getStatus().equals("DELIVERED")) {

                            Types t = c.getTypes(rf.getTransactionMotiveId());

                            Types ttp = c.getTypes(rf.getFormId());

                            if (t.getId() != null && ttp.getId() != null) {
                                if (!ttp.getAlias().equals("PI") && !ttp.getAlias().equals("MU") && !ttp.getAlias().equals("DI")) {
                                    if (t.getName().trim().toLowerCase().contains("cambio de nombre")) {

                                        PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                        notificacion.setComprobante(payment.getVoucherNumber());
                                        notificacion.setFechaPresentacion(rf.getApplicationDate());
                                        notificacion.setCertificado(c.getNextCambioNombreCertificado());
                                        notificacion.setSigno(ttp.getAlias());
                                        notificacion.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");

                                        notificacion.setIdRenewalForm(rf.getId());
                                        if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                            HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                            if (hf.getId() != null) {
                                                notificacion.setDenominacion(hf.getDenomination());
                                                notificacion.setRegistro(hf.getExpedient());

                                                if (notificacion.getRegistro() != null && !notificacion.getRegistro().trim().isEmpty()) {
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(notificacion.getRegistro(), notificacion.getDenominacion());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        notificacion.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        notificacion.setTitularAnterior(titulo.getTitular());
                                                    }
                                                }
                                                if (notificacion.getFechaRegistro() == null) {
                                                    notificacion.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                }
                                            }
                                        } else {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                if (ps.getCodigoSolicitudSigno() != null) {
                                                    notificacion.setDenominacion(ps.getDenominacionSigno());
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        notificacion.setRegistro(titulo.getNumeroTitulo());
                                                        notificacion.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        notificacion.setTitularAnterior(titulo.getTitular());
                                                    }

                                                    if (notificacion.getTitularAnterior() == null || notificacion.getTitularAnterior().trim().isEmpty()) {
                                                        PpdiPersona persona = c.getASolicitanteByCodigoSolicitud(ps.getCodigoSolicitudSigno());
                                                        if (persona.getCodigoPersona() != null) {
                                                            notificacion.setTitularAnterior(persona.getNombrePersona());
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        Person titAct = c.getTitularActual(rf.getId());
                                        if (titAct.getId() != null) {
                                            notificacion.setTitularActual(titAct.getName());
                                            notificacion.setDomicilioTitularActual(titAct.getAddress());
                                            notificacion.setIdentificacion(titAct.getIdentificationNumber());
                                        }

                                        Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "AGENT");
                                        if (apoder.getId() != null) {
                                            notificacion.setApeApodRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "AGENT"));
                                        }

                                        if (notificacion.getRegistro() != null && !notificacion.getRegistro().trim().isEmpty()) {
                                            if (notificacion.getDenominacion() != null && !notificacion.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(notificacion.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(notificacion.getRegistro(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + notificacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            notificacion = new CambioNombre();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            notificacion.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + notificacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + notificacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            notificacion = new CambioNombre();
                                                        }
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(notificacion.getRegistro(), notificacion.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(notificacion.getRegistro(), notificacion.getDenominacion());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + notificacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            notificacion = new CambioNombre();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            notificacion.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + notificacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + notificacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            notificacion = new CambioNombre();
                                                        }
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                }
                                            } else {
                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                            }
                                        } else {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                        }

                                    } else {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UN CAMBIO DE NOMBRE, SINO '" + t.getName().toUpperCase() + "'");
                                    }
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE ES UN " + ttp.getName().toUpperCase());
                                }
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE PRESENTA UN PROBLEMA DE IDENTIDAD");
                            }

                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO, PERO NO REGISTRA INICIO DE PROCESO");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE NO ENCONTRADO");
                    }
                }
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN TRÁMITE VÁLIDO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void validaSeleccion() {
        if (roselectable) {
            roChoose = "";
            razon = "";
        } else {
            razon = "";
            roChoose = "";
        }
    }

    public void downloadSelected(ActionEvent ae) {
        FacesMessage msg = null;
//      RequestContext context = RequestContext.getCurrentInstance();
        if (!selectedNotificaciones.isEmpty()) {

            Controlador c = new Controlador();
            boolean band = true;
            String msj = "";
            for (int i = 0; i < selectedNotificaciones.size(); i++) {
                if (selectedNotificaciones.get(i).getRegistro() == null || selectedNotificaciones.get(i).getRegistro().trim().isEmpty()) {
                    band = false;
                    msj = "EL TRÁMITE " + selectedNotificaciones.get(i).getSolicitud() + " NO TIENE NÚMERO DE REGISTRO";
                    break;
                }
                if (selectedNotificaciones.get(i).getNotificacion() == null || selectedNotificaciones.get(i).getNotificacion() == 0) {
                    band = false;
                    msj = "EL TRÁMITE " + selectedNotificaciones.get(i).getSolicitud() + " NO TIENE NÚMERO DE NOTIFICACIÓN";
                    break;
                }
                if (c.getRosBySolicitud(selectedNotificaciones.get(i).getSolicitud()).isEmpty()) {
                    band = false;
                    msj = "EL TRÁMITE " + selectedNotificaciones.get(i).getSolicitud() + " NO TIENE MOTIVO DE NOTIFICACIÓN";
                    System.out.println("El trámite " + selectedNotificaciones.get(i).getSolicitud() + " no tiene motivo de notificación");
                    break;
                }

            }
            if (band) {
                System.out.println("Descargando Múltiples Notificaciones_cn...");
                loginBean.setVarious(true);
                loginBean.setCambiosNombre(selectedNotificaciones);

                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                PrimeFaces.current().ajax().addCallbackParam("view", "cambionombrep");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "DESCARGA", "DESCARGANDO REGISTROS SELECCIONADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN REGISTRO", msj);
            }

        } else {
            System.out.println("Sin selección...");
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN SELECCIÓN", "SELECCIONE AL MENOS UN REGISTRO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararShowRo(ActionEvent ae) {
        FacesMessage msg = null;
        if (notificacion != null && notificacion.getSolicitud() != null && !notificacion.getSolicitud().trim().isEmpty()) {
            Controlador c = new Controlador();
            roos = c.getRosBySolicitud(notificacion.getSolicitud());
            roshow = "";
            for (int i = 0; i < roos.size(); i++) {
                roshow += roos.get(i).getRo();
            }
            PrimeFaces.current().ajax().addCallbackParam("viewro", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "MOTIVO DE NOTIFICACIÓN " + notificacion.getSolicitud());
        } else {
            PrimeFaces.current().ajax().addCallbackParam("viewro", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN TRÁMITE", "INGRESE UN NÚMERO DE TRÁMITE CORRECTO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararViewRo(ActionEvent ae) {
        notificacion = (CambioNombre) notificacionesDataTable.getRowData();
        if (notificacion != null) {
            Controlador c = new Controlador();
            roos = c.getRosBySolicitud(notificacion.getSolicitud());
            roshow = "";
            for (int i = 0; i < roos.size(); i++) {
                roshow += roos.get(i).getRo();
            }
            PrimeFaces.current().ajax().addCallbackParam("viewro", true);
        } else {
            PrimeFaces.current().ajax().addCallbackParam("viewro", false);
        }
    }

    public void agregarRo(ActionEvent ae) {
        FacesMessage msg = null;
        if (notificacion != null) {

            if (roselectable && razon != null && !razon.trim().isEmpty()) {
                Rooptions ro = new Rooptions();
                ro.setRo(razon);
                ro.setFecha(new Date());
                ro.setSolicitud(notificacion.getSolicitud());
                ro.setTipo("CAMBIO DE NOMBRE");
                Controlador c = new Controlador();
                if (c.saveRooptios(ro)) {
                    roos = c.getRosBySolicitud(notificacion.getSolicitud());
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "RO GUARDADO");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GUARDAR EL RO");
                }
            } else {
                if (roChoose != null && !roChoose.trim().isEmpty()) {
                    Rooptions ro = new Rooptions();
                    ro.setRo(roChoose);
                    ro.setFecha(new Date());
                    ro.setSolicitud(notificacion.getSolicitud());
                    ro.setTipo("CAMBIO DE NOMBRE");
                    Controlador c = new Controlador();
                    if (c.saveRooptios(ro)) {
                        roos = c.getRosBySolicitud(notificacion.getSolicitud());
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "RO GUARDADO");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GUARDAR EL RO");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GUARDAR EL RO");
                }
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ LA NOTIFICACION CORRECTAMENTE");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarRo(ActionEvent ae) {
        FacesMessage msg = null;
        if (notificacion != null) {
            Rooptions roo = (Rooptions) roDataTable.getRowData();
            if (roo != null) {
                Controlador c = new Controlador();
                if (c.removeRooptios(roo)) {
                    roos = c.getRosBySolicitud(notificacion.getSolicitud());
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "RO ELIMINADO CORRECTAMENTE");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO ELIMINAR EL RO");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE EL RO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE EL RO");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void postProcessXLS(Object document) {
        XSSFWorkbook book = (XSSFWorkbook) document;
        XSSFSheet sheet = book.getSheetAt(0);
        Controlador c = new Controlador();

        XSSFRow rowt = sheet.getRow(2);

        XSSFCell cr1 = rowt.createCell(14);
        cr1.setCellValue("Ro1");

        XSSFCell cr2 = rowt.createCell(15);
        cr2.setCellValue("Ro2");

        XSSFCell cr3 = rowt.createCell(16);
        cr3.setCellValue("Ro3");

        XSSFCell cr4 = rowt.createCell(17);
        cr4.setCellValue("Ro4");

        int lastCol = 13;
        for (int i = 0; i < notificaciones.size(); i++) {
            List<Rooptions> roops = c.getRosBySolicitud(notificaciones.get(i).getSolicitud());
            for (int j = 0; j < roops.size(); j++) {
                Rooptions roo = roops.get(j);
                if (roo != null) {
                    XSSFRow row = sheet.getRow(i + 3);
                    lastCol++;
                    XSSFCell rc = row.createCell(lastCol);
                    rc.setCellValue(roo.getRo());
                }
            }
            lastCol = 13;
        }
    }

    public void prepararDescarga(ActionEvent ae) {
        FacesMessage msg = null;
        notificacion = (CambioNombre) notificacionesDataTable.getRowData();
        if (notificacion != null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICACIÓN_CN PREPARADA PARA DESCARGA");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE LA NOTIFICACIÓN_CN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarCasillero(ActionEvent ae) {
        if (notificacion != null && notificacion.getId() != null) {
            Controlador c = new Controlador();
            notificacion.setCasilleroSenadi(c.buscarCasilleroBySolicitud(notificacion.getSolicitud()));
        }
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (notificacion != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + notificacion.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(notificacion.getIdRenewalForm(), notificacion.getSolicitud());
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

    public void prepararParaAbandonos() {
        FacesMessage msg = null;
        if (selectedNotificaciones.isEmpty()) {
            abandonosS = true;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        } else {
            String paraProrroga = getSolicitudesParaProrroga();
            if (!paraProrroga.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL/LOS TRÁMITE(S) " + paraProrroga + " ESTÁ(N) PARA PRÓRROGA, POR LO QUE NO SE PUEDE(N) ESTABLECER PARA ABANDONO");
            } else {
                abandonosS = false;
                fechaPuestaAbandono = new Date();
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TRÁMITES CARGADOS");
                PrimeFaces.current().ajax().addCallbackParam("abait", true);
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararPasarAbandonos(ActionEvent ae) {
        FacesMessage msg = null;
        if (selectedNotificaciones.isEmpty()) {
            abandonosS = true;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        } else {
            String paraProrroga = getSolicitudesParaProrroga();
            if (!paraProrroga.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL/LOS TRÁMITE(S) " + paraProrroga + " ESTÁ(N) PARA PRÓRROGA, POR LO QUE NO SE PUEDE(N) PASAR A ABANDONO");
            } else {
                abandonosS = false;
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TRÁMITES CARGADOS");
                PrimeFaces.current().ajax().addCallbackParam("abait", true);
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    private String getSolicitudesParaProrroga() {
        String paraProrroga = "";
        for (int i = 0; i < selectedNotificaciones.size(); i++) {
            CambioNombre notaux = selectedNotificaciones.get(i);
            if (notaux.getFechaPuestaProrroga() != null) {
                paraProrroga += (paraProrroga.isEmpty() ? "" : ", ") + notaux.getSolicitud();
            }
        }
        return paraProrroga;
    }

    public void prepararParaProrrogas() {
        FacesMessage msg = null;
        if (selectedNotificaciones.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        } else {
            String noEmitidas = "";
            String paraAbandono = "";
            for (int i = 0; i < selectedNotificaciones.size(); i++) {
                CambioNombre notaux = selectedNotificaciones.get(i);
                if (!notaux.isNotificacionEmitida()) {
                    noEmitidas += (noEmitidas.isEmpty() ? "" : ", ") + notaux.getSolicitud();
                }
                if (notaux.getFechaPuestaAbandono() != null) {
                    paraAbandono += (paraAbandono.isEmpty() ? "" : ", ") + notaux.getSolicitud();
                }
            }
            if (!noEmitidas.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "SOLO SE PUEDE ESTABLECER PARA PRÓRROGA NOTIFICACIONES YA EMITIDAS. REVISE: " + noEmitidas);
            } else if (!paraAbandono.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL/LOS TRÁMITE(S) " + paraAbandono + " ESTÁ(N) PARA ABANDONO, POR LO QUE NO SE PUEDE(N) ESTABLECER PARA PRÓRROGA");
            } else {
                String yaProrroga = getSolicitudesParaProrroga();
                if (!yaProrroga.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO",
                            "LOS SIGUIENTES TRÁMITES YA FUERON ESTABLECIDOS PARA PRÓRROGA ANTERIORMENTE (SE ACTUALIZARÁN LOS DÍAS SI CONTINÚA): " + yaProrroga));
                }
                diasProrroga = 10;
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TRÁMITES CARGADOS");
                PrimeFaces.current().ajax().addCallbackParam("proit", true);
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void paraProrrogas(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedNotificaciones.isEmpty()) {
            if (diasProrroga != null && diasProrroga > 0) {
                Controlador c = new Controlador();
                int n = 0;
                for (int i = 0; i < selectedNotificaciones.size(); i++) {
                    CambioNombre notaux = selectedNotificaciones.get(i);
                    if (!notaux.isNotificacionEmitida()) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "LA NOTIFICACIÓN " + notaux.getSolicitud() + " NO ESTÁ EMITIDA");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        return;
                    }
                    if (notaux.getFechaPuestaAbandono() != null) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE " + notaux.getSolicitud() + " ESTÁ PARA ABANDONO, POR LO QUE NO SE PUEDE ESTABLECER PARA PRÓRROGA");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        return;
                    }
                    notaux.setFechaPuestaProrroga(new Date());
                    notaux.setDiasProrroga(diasProrroga);
                    if (!c.updateCambioNombre(notaux)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO ESTABLECER PARA PRÓRROGA A " + notaux.getSolicitud());
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        return;
                    } else {
                        c.saveHistorial("NOTIFICADAS", "NOTIFICADAS", notaux.getSolicitud(), "PARA PRÓRROGA (" + diasProrroga + " DÍAS)", loginBean.getUsuario().getId(), loginBean.getNombre());
                        n++;
                    }
                }
                if (n > 0) {
                    loadNotificacionesCN();
                    PrimeFaces.current().ajax().addCallbackParam("proit", true);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE HA ESTABLECIDO SATISFACTORIAMENTE LOS NOTIFICADOS SELECCIONADOS PARA PRÓRROGA");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL GUARDAR LAS PRÓRROGAS, CONSULTE AL ADMINISTRADOR DEL SISTEMA");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN NÚMERO DE DÍAS DE PRÓRROGA VÁLIDO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void pasarAAbandonos(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedNotificaciones.isEmpty()) {
            if (tipoAbandono != null && !tipoAbandono.trim().isEmpty()) {
                Controlador c = new Controlador();
                int n = 0;
                for (int i = 0; i < selectedNotificaciones.size(); i++) {
                    System.out.println(selectedNotificaciones.get(i).getSolicitud());
                    CambioNombre notaux = selectedNotificaciones.get(i);
                    if (notaux.getFechaPuestaProrroga() != null) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE " + notaux.getSolicitud() + " ESTÁ PARA PRÓRROGA, POR LO QUE NO SE PUEDE PASAR A ABANDONO");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        return;
                    }
                    notaux.setTipoAbandono(tipoAbandono);
                    notaux.setTipoEstado("ABANDONO");
                    notaux.setFechaAbandono(new Date());
                    int nextabandono = c.getNextNumeroAbandonoCN(new Date());
                    System.out.println("nextabandono: " + nextabandono);
                    notaux.setNumeroAbandono(nextabandono);
                    if (!c.updateCambioNombre(notaux)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GUARDAR EL ABANDONO DEL TRÁMITE " + notaux.getSolicitud());
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        return;
                    } else {
                        c.saveHistorial("ABANDONO_CN", "NOTIFICADAS_CN", notaux.getSolicitud(), "PASADO A", loginBean.getUsuario().getId(), loginBean.getNombre());
                        n++;

                    }
                }
                if (n > 0) {
                    loadNotificacionesCN();
                    PrimeFaces.current().ajax().addCallbackParam("abit", true);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE HA PASADO SATISFACTORIAMENTE LOS NOTIFICADOS SELECCIONADOS A ABANDONOS");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL GUARDAR LOS ABANDONOS, CONSULTE AL ADMINISTRADOR DEL SISTEMA");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE UN TIPO DE ABANDONO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void paraAbandonos(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedNotificaciones.isEmpty()) {
            if (Operaciones.validarFecha(fechaPuestaAbandono)) {
                if (tipoAbandono != null && !tipoAbandono.trim().isEmpty()) {
                    Controlador c = new Controlador();
                    int n = 0;
                    for (int i = 0; i < selectedNotificaciones.size(); i++) {
                        CambioNombre notaux = selectedNotificaciones.get(i);
                        if (notaux.getFechaPuestaProrroga() != null) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE " + notaux.getSolicitud() + " ESTÁ PARA PRÓRROGA, POR LO QUE NO SE PUEDE ESTABLECER PARA ABANDONO");
                            FacesContext.getCurrentInstance().addMessage(null, msg);
                            return;
                        }
                        notaux.setTipoAbandono(tipoAbandono);
                        notaux.setFechaPuestaAbandono(fechaPuestaAbandono);
                        if (!c.updateCambioNombre(notaux)) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO ESTABLECER PARA ABANDONO A " + notaux.getSolicitud());
                            FacesContext.getCurrentInstance().addMessage(null, msg);
                            return;
                        } else {
                            c.saveHistorial("NOTIFICADAS", "NOTIFICADAS", notaux.getSolicitud(), "PARA ABANDONO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            n++;
                        }
                    }
                    if (n > 0) {
                        loadNotificacionesCN();
                        PrimeFaces.current().ajax().addCallbackParam("abit", true);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE HA ESTABLECIDO SATISFACTORIAMENTE LOS NOTIFICADOS SELECCIONADOS PARA ABANDONOS");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL GUARDAR LOS ABANDONOS, CONSULTE AL ADMINISTRADOR DEL SISTEMA");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE UN TIPO DE ABANDONO");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UNA FECHA DE ABANDONO VÁLIDA");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String getTooltipAbandono(CambioNombre noti) {
        if (noti.getFechaPuestaProrroga() != null && noti.getDiasProrroga() != null) {
            LocalDate limiteProrroga = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(noti.getFechaPuestaProrroga(), noti.getDiasProrroga());
            long faltanPro = ChronoUnit.DAYS.between(LocalDate.now(), limiteProrroga);
            if (faltanPro >= 0) {
                return "Faltan " + faltanPro + " días para pasar el trámite " + noti.getSolicitud() + " a prórroga";
            } else {
                return "La prórroga del trámite " + noti.getSolicitud() + " ya venció hace " + Math.abs(faltanPro) + " días";
            }
        }

        if (noti.getFechaPuestaAbandono() == null || noti.getTipoAbandono() == null) {
            return "";
        }

        LocalDate fechaLimite = null;
        switch (noti.getTipoAbandono()) {
            case "ERJAFE":
                fechaLimite = noti.getFechaPuestaAbandono().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(60);
                break;
            case "REGLAMENTO":
                fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(noti.getFechaPuestaAbandono(), 10);
                break;
            case "COA":
                fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(noti.getFechaPuestaAbandono(), 10);
                break;
            default:
                break;
        }

        long faltan = ChronoUnit.DAYS.between(LocalDate.now(), fechaLimite);

        if (faltan >= 0) {
            return "Faltan " + faltan + " días para pasar el trámite " + noti.getSolicitud() + " a abandono";
        } else {
            return "Ya venció hace " + Math.abs(faltan) + " días";
        }
    }

    public void viewErjafe(ActionEvent ae) {
        FacesMessage msg = null;
        notificacion = (CambioNombre) notificacionesDataTable.getRowData();
        if (notificacion != null) {
            if (notificacion.getRegistro() != null && !notificacion.getRegistro().trim().isEmpty()) {
                if (notificacion.getSolicitante() != null && !notificacion.getSolicitante().trim().isEmpty()) {
                    if (notificacion.getApeApodRepre() != null && !notificacion.getApeApodRepre().trim().isEmpty()) {
                        if (notificacion.getNotificacion() != null) {
                            loginBean.setCambioNombre(notificacion);
                            loginBean.setVarious(false);
                            loginBean.setTipoTramite(3);
                            PrimeFaces.current().ajax().addCallbackParam("doit", true);

                            System.out.println("envía caducada cambio nombre descargar");
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO DOCUMENTO " + notificacion.getSolicitud());
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO EXISTE EL NÚMERO DE NOTIFICACION");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO EXISTE UN APODERADO EN EL TRÁMITE");
                    }

                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "INGRESE UN SOLICITANTE VÁLIDO");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO EXISTE EL NúMERO DE REGISTRO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE LA NOTIFICACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void downloadSelectedErjafe(ActionEvent ae) {
        FacesMessage msg = null;
        if (selectedNotificaciones != null && !selectedNotificaciones.isEmpty()) {
            boolean flag = true;
            String msj = "";
            for (int i = 0; i < selectedNotificaciones.size(); i++) {
                CambioNombre notificadaaux = selectedNotificaciones.get(i);
                System.out.println("notificada: "+notificadaaux.getSolicitud());
                if (notificadaaux.getRegistro() == null || notificadaaux.getRegistro().trim().isEmpty()) {
                    msj = "NO EXISTE EL NÚMERO DE REGISTRO PARA EL TRÁMITE " + notificadaaux.getSolicitud();
                    flag = false;
                    break;
                }
                if (notificadaaux.getSolicitante() == null || notificadaaux.getSolicitante().trim().isEmpty()) {
                    msj = "INGRESE UN SOLICITANTE VÁLIDO PARA EL TRÁMITE " + notificadaaux.getSolicitud();
                    flag = false;
                    break;
                }

                if (notificadaaux.getApeApodRepre() == null || notificadaaux.getApeApodRepre().trim().isEmpty()) {
                    msj = "NO EXISTE UN APODERADO EN EL TRÁMITE " + notificadaaux.getSolicitud();
                    flag = false;
                    break;
                }
                if (!Operaciones.validarFecha(notificadaaux.getFechaRegistro())) {
                    msj = "LA FECHA DE REGISTRO NO ES CORRECTA EN EL TRÁMITE " + notificadaaux.getSolicitud();;
                    flag = false;
                    break;
                }
                if (!Operaciones.validarFecha(notificadaaux.getFechaNotificacion())) {
                    msj = "LA FECHA DE NOTIFICACIÓN NO ES CORRECTA EN EL TRÁMITE " + notificadaaux.getSolicitud();
                    flag = false;
                    break;
                }
                if (notificadaaux.getNotificacion() == null) {
                    msj = "NO EXISTE EL NÚMERO DE NOTIFICACION EN EL TRÁMITE " + notificadaaux.getSolicitud();;
                    flag = false;
                    break;
                }
                Controlador c = new Controlador();
                List<Rooptions> ros = c.getRosBySolicitud(notificadaaux.getSolicitud());
                if(ros.isEmpty()){
                    msj = "NO EXISTE LA RAZÓN DE NOTIFICACIÓN EN EL TRÁMITE " + notificadaaux.getSolicitud();
                    flag = false;
                    break;
                }
            }
            if (flag) {
                loginBean.setCambiosNombre(selectedNotificaciones);
                loginBean.setVarious(true);
                loginBean.setTipoTramite(3);
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                System.out.println("envía notificada cambio nombr descargar");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE ENVIARON LAS NOTIFICACIONES A DESCARGA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", msj);
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        }

        FacesContext.getCurrentInstance()
                .addMessage(null, msg);
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
     * @return the notificaciones
     */
    public List<CambioNombre> getNotificaciones() {
        return notificaciones;
    }

    /**
     * @param notificaciones the notificaciones to set
     */
    public void setNotificaciones(List<CambioNombre> notificaciones) {
        this.notificaciones = notificaciones;
    }

    /**
     * @return the notificacionesFiltradas
     */
    public List<CambioNombre> getNotificacionesFiltradas() {
        return notificacionesFiltradas;
    }

    /**
     * @param notificacionesFiltradas the notificacionesFiltradas to set
     */
    public void setNotificacionesFiltradas(List<CambioNombre> notificacionesFiltradas) {
        this.notificacionesFiltradas = notificacionesFiltradas;
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
     * @return the dialogTitle
     */
    public String getDialogTitle() {
        return dialogTitle;
    }

    /**
     * @param dialogTitle the dialogTitle to set
     */
    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    /**
     * @return the saveEdit
     */
    public String getSaveEdit() {
        return saveEdit;
    }

    /**
     * @param saveEdit the saveEdit to set
     */
    public void setSaveEdit(String saveEdit) {
        this.saveEdit = saveEdit;
    }

    /**
     * @return the mensajeConfirmacion
     */
    public String getMensajeConfirmacion() {
        return mensajeConfirmacion;
    }

    /**
     * @param mensajeConfirmacion the mensajeConfirmacion to set
     */
    public void setMensajeConfirmacion(String mensajeConfirmacion) {
        this.mensajeConfirmacion = mensajeConfirmacion;
    }

    /**
     * @return the edicion
     */
    public boolean isEdicion() {
        return edicion;
    }

    /**
     * @param edicion the edicion to set
     */
    public void setEdicion(boolean edicion) {
        this.edicion = edicion;
    }

    /**
     * @return the notificacion
     */
    public CambioNombre getNotificacion() {
        return notificacion;
    }

    /**
     * @param notificacion the notificacion to set
     */
    public void setNotificacion(CambioNombre notificacion) {
        this.notificacion = notificacion;
    }

    /**
     * @return the numRegistros
     */
    public String getNumRegistros() {
        return numRegistros;
    }

    /**
     * @param numRegistros the numRegistros to set
     */
    public void setNumRegistros(String numRegistros) {
        this.numRegistros = numRegistros;
    }

    /**
     * @return the loginBean
     */
    public LoginBean getLoginBean() {
        return loginBean;
    }

    /**
     * @param loginBean the loginBean to set
     */
    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    /**
     * @return the historial
     */
    public String getHistorial() {
        return historial;
    }

    /**
     * @param historial the historial to set
     */
    public void setHistorial(String historial) {
        this.historial = historial;
    }

    /**
     * @return the estadoTemp
     */
    public String getEstadoTemp() {
        return estadoTemp;
    }

    /**
     * @param estadoTemp the estadoTemp to set
     */
    public void setEstadoTemp(String estadoTemp) {
        this.estadoTemp = estadoTemp;
    }

    /**
     * @return the exportName
     */
    public String getExportName() {
        return exportName;
    }

    /**
     * @param exportName the exportName to set
     */
    public void setExportName(String exportName) {
        this.exportName = exportName;
    }

    /**
     * @return the roRazones
     */
    public List<String> getRoRazones() {
        return roRazones;
    }

    /**
     * @param roRazones the roRazones to set
     */
    public void setRoRazones(List<String> roRazones) {
        this.roRazones = roRazones;
    }

    /**
     * @return the razon
     */
    public String getRazon() {
        return razon;
    }

    /**
     * @param razon the razon to set
     */
    public void setRazon(String razon) {
        this.razon = razon;
    }

    /**
     * @return the roselectable
     */
    public boolean isRoselectable() {
        return roselectable;
    }

    /**
     * @param roselectable the roselectable to set
     */
    public void setRoselectable(boolean roselectable) {
        this.roselectable = roselectable;
    }

    /**
     * @return the selectedNotificaciones
     */
    public List<CambioNombre> getSelectedNotificaciones() {
        return selectedNotificaciones;
    }

    /**
     * @param selectedNotificaciones the selectedNotificaciones to set
     */
    public void setSelectedNotificaciones(List<CambioNombre> selectedNotificaciones) {
        this.selectedNotificaciones = selectedNotificaciones;
    }

    /**
     * @return the roos
     */
    public List<Rooptions> getRoos() {
        return roos;
    }

    /**
     * @param roos the roos to set
     */
    public void setRoos(List<Rooptions> roos) {
        this.roos = roos;
    }

    /**
     * @return the roDataTable
     */
    public UIData getRoDataTable() {
        return roDataTable;
    }

    /**
     * @param roDataTable the roDataTable to set
     */
    public void setRoDataTable(UIData roDataTable) {
        this.roDataTable = roDataTable;
    }

    /**
     * @return the roChoose
     */
    public String getRoChoose() {
        return roChoose;
    }

    /**
     * @param roChoose the roChoose to set
     */
    public void setRoChoose(String roChoose) {
        this.roChoose = roChoose;
    }

    /**
     * @return the separado
     */
    public boolean isSeparado() {
        return separado;
    }

    /**
     * @param separado the separado to set
     */
    public void setSeparado(boolean separado) {
        this.separado = separado;
    }

    /**
     * @return the roshow
     */
    public String getRoshow() {
        return roshow;
    }

    /**
     * @param roshow the roshow to set
     */
    public void setRoshow(String roshow) {
        this.roshow = roshow;
    }

    /**
     * @return the fechaInicioCertificado
     */
    public Date getFechaInicioCertificado() {
        return fechaInicioCertificado;
    }

    /**
     * @param fechaInicioCertificado the fechaInicioCertificado to set
     */
    public void setFechaInicioCertificado(Date fechaInicioCertificado) {
        this.fechaInicioCertificado = fechaInicioCertificado;
    }

    /**
     * @return the fechaFinCertificado
     */
    public Date getFechaFinCertificado() {
        return fechaFinCertificado;
    }

    /**
     * @param fechaFinCertificado the fechaFinCertificado to set
     */
    public void setFechaFinCertificado(Date fechaFinCertificado) {
        this.fechaFinCertificado = fechaFinCertificado;
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
     * @return the rutaNotificacionCasillero
     */
    public String getRutaNotificacionCasillero() {
        return rutaNotificacionCasillero;
    }

    /**
     * @param rutaNotificacionCasillero the rutaNotificacionCasillero to set
     */
    public void setRutaNotificacionCasillero(String rutaNotificacionCasillero) {
        this.rutaNotificacionCasillero = rutaNotificacionCasillero;
    }

    /**
     * @return the abandonosS
     */
    public boolean isAbandonosS() {
        return abandonosS;
    }

    /**
     * @param abandonosS the abandonosS to set
     */
    public void setAbandonosS(boolean abandonosS) {
        this.abandonosS = abandonosS;
    }

    /**
     * @return the fechaPuestaAbandono
     */
    public Date getFechaPuestaAbandono() {
        return fechaPuestaAbandono;
    }

    /**
     * @param fechaPuestaAbandono the fechaPuestaAbandono to set
     */
    public void setFechaPuestaAbandono(Date fechaPuestaAbandono) {
        this.fechaPuestaAbandono = fechaPuestaAbandono;
    }

    /**
     * @return the tipoAbandono
     */
    public String getTipoAbandono() {
        return tipoAbandono;
    }

    /**
     * @param tipoAbandono the tipoAbandono to set
     */
    public void setTipoAbandono(String tipoAbandono) {
        this.tipoAbandono = tipoAbandono;
    }

    public Integer getDiasProrroga() {
        return diasProrroga;
    }

    public void setDiasProrroga(Integer diasProrroga) {
        this.diasProrroga = diasProrroga;
    }
}
