/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import senadi.gob.ec.transfweb.model.Abandono;
import senadi.gob.ec.transfweb.modelp.PpdiPersona;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import senadi.gob.ec.transfweb.model.Caducada;
import senadi.gob.ec.transfweb.model.Desistimiento;
import senadi.gob.ec.transfweb.model.Documento;
import senadi.gob.ec.transfweb.model.Historial;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.model.Rooptions;
import senadi.gob.ec.transfweb.model.TituloCancelado;
import senadi.gob.ec.transfweb.model.Transferencia;
import senadi.gob.ec.transfweb.model.UploadNotificacion;
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
 * @author Michael
 */
@ManagedBean(name = "notificacionBean")
@ViewScoped
public class NotificacionBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private Date fechaInicioCertificado;
    private Date fechaFinCertificado;

    private List<Notificacion> notificaciones;
    private List<Notificacion> notificacionesFiltradas;

    private UIData notificacionesDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private Notificacion notificacion;

    private LoginBean loginBean;

    private String estadoTemp;
    private String historial;

    private String exportName;

    private List<String> roRazones;
    private String razon;

    private boolean roselectable;

    private List<Notificacion> selectedNotificaciones;

    private List<Rooptions> roos;
    private UIData roDataTable;

    private String roChoose;

    private boolean separado;

    private String roshow;

    private List<Documento> archivos;

    private String rutaNotificacionCasillero;

    private String tipoAbandono;

    private boolean abandonosS;

    private Date fechaPuestaAbandono;

    public NotificacionBean() {
        loadNotificaciones();
    }

    private void loadNotificaciones() {
        Controlador c = new Controlador();
        notificaciones = c.getNotificaciones();
        numRegistros = "Número Registros Mostrados: " + notificaciones.size();
        exportName = "notificacion_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
        roRazones = Operaciones.getRazonesNotificar();
        razon = "";
        selectedNotificaciones = new ArrayList<>();
        abandonosS = false;
    }

    public void buscarNotificaciones(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            notificaciones = c.getNotificaicionByCriteria(criterio.trim());
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
            notificaciones = c.getNotificacionesByFecha(fechaInicio, fechaFin);
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
            notificaciones = c.getNotificacionByFechaNotificacion(fechaInicioCertificado, fechaFinCertificado);
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
        notificacion = (Notificacion) notificacionesDataTable.getRowData();
        if (notificacion != null) {
            Controlador c = new Controlador();
            if (c.removeNotificacion(notificacion)) {
//                c.saveHistorial("RENOVACIÓN", "RENOVACIÓN", renovacion.getSolicitudSenadi(), "ELIMINADO", loginBean.getUsuario().getId(), loginBean.getUsuario().getLogin());
                loadNotificaciones();
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
        notificacion = (Notificacion) notificacionesDataTable.getRowData();
        if (notificacion != null) {
//            System.out.println("fechaaaaaaaaaA: " + notificacion.getFechaPresentacion());
            roselectable = false;
            dialogTitle = "EDITAR NOTIFICACIÓN " + notificacion.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar la Notificación: " + notificacion.getSolicitud() + "?";
            Controlador c = new Controlador();
            RenewalForm rf = c.getRenewalFormsByApplicationNumber(notificacion.getSolicitud());
            if (rf.getId() != null) {
                notificacion.setIdRenewalForm(rf.getId());
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICACIÓN CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR NOTIFICACIÓN");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararNuevo(ActionEvent ae) {
        dialogTitle = "NUEVA NOTIFICACIÓN";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar la Nueva Notificación?";
        notificacion = new Notificacion();
        Controlador c = new Controlador();
        notificacion.setNotificacion(c.getNextNumeroNotificacion(new Date()));
        notificacion.setResponsable(loginBean.getUsuario().getAlias());

        roos = new ArrayList<>();

        edicion = false;
        roselectable = false;
        razon = "";
        if (notificacion != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void prepararParaAbandonos() {
        FacesMessage msg = null;
        if (selectedNotificaciones.isEmpty()) {
            abandonosS = true;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        } else {
            abandonosS = false;
            fechaPuestaAbandono = new Date();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TRÁMITES CARGADOS");
            PrimeFaces.current().ajax().addCallbackParam("abait", true);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararPasarAbandonos(ActionEvent ae) {
        FacesMessage msg = null;
        if (selectedNotificaciones.isEmpty()) {
            abandonosS = true;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        } else {
            abandonosS = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TRÁMITES CARGADOS");
            PrimeFaces.current().ajax().addCallbackParam("abait", true);
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
                        Notificacion notaux = selectedNotificaciones.get(i);
                        notaux.setTipoAbandono(tipoAbandono);
                        notaux.setFechaPuestaAbandono(fechaPuestaAbandono);
                        if (!c.updateNotificacion(notaux)) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO ESTABLECER PARA ABANDONO A " + notaux.getSolicitud());
                            FacesContext.getCurrentInstance().addMessage(null, msg);
                            return;
                        } else {
                            c.saveHistorial("NOTIFICADAS", "NOTIFICADAS", notaux.getSolicitud(), "PARA ABANDONO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            n++;
                        }
                    }                    
                    if (n > 0) {
                        loadNotificaciones();
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

    public void pasarAAbandonos(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedNotificaciones.isEmpty()) {
            if (tipoAbandono != null && !tipoAbandono.trim().isEmpty()) {
                Controlador c = new Controlador();
                int n = 0;
                for (int i = 0; i < selectedNotificaciones.size(); i++) {
                    System.out.println(selectedNotificaciones.get(i).getSolicitud());
                    Notificacion notaux = selectedNotificaciones.get(i);
                    Abandono abandono = new Abandono();
                    abandono.setSolicitud(notaux.getSolicitud().toUpperCase());
                    abandono.setFechaPresentacion(notaux.getFechaPresentacion());
                    abandono.setFechaAbandono(new Date());
                    abandono.setNumeroAbandono(c.getNextNumeroAbandono(abandono.getFechaAbandono()));
                    abandono.setFechaElaboraNotificacion(notaux.getFechaElaboraNotificacion());
                    abandono.setNotificacion(notaux.getNotificacion());
                    abandono.setFechaNotificacion(notaux.getFechaNotificacion());
                    abandono.setRegistro(notaux.getRegistro());
                    abandono.setFechaRegistro(notaux.getFechaRegistro());
                    abandono.setDenominacion(notaux.getDenominacion());
                    abandono.setSigno(notaux.getSigno());
                    abandono.setTitularAnterior(notaux.getTitularAnterior());
                    abandono.setTitularActual(notaux.getTitularActual());
                    abandono.setApeApodRepre(notaux.getApeApodRepre());
                    abandono.setRo(notaux.getRo());
                    abandono.setCasilleroSenadi(notaux.getCasilleroSenadi());
                    abandono.setCasilleroJudicial(notaux.getCasilleroJudicial());
                    abandono.setResponsable(notaux.getResponsable());
                    abandono.setIdentificacion(notaux.getIdentificacion());
                    abandono.setCertificado(notaux.getCertificado());
                    abandono.setFechaCertificado(notaux.getFechaCertificado());
                    abandono.setDomicilioTitularActual(notaux.getDomicilioTitularActual());
                    abandono.setComprobante(notaux.getComprobante());
                    abandono.setCertificadoEmitido(notaux.isCertificadoEmitido());
                    abandono.setNotificacionEmitida(notaux.isNotificacionEmitida());
                    abandono.setCancelado(notaux.getCancelado());
                    abandono.setSolicitante(notaux.getSolicitante());                    
                    
                    abandono.setTipoAbandono(tipoAbandono);
                    if (!c.saveAbandono(abandono)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GUARDAR EL ABANDONO DEL TRÁMITE " + abandono.getSolicitud());
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        return;
                    } else {
                        if (c.removeNotificacion(notaux)) {
                            c.saveHistorial("ABANDONO", "NOTIFICADAS", abandono.getSolicitud(), "PASADO A", loginBean.getUsuario().getId(), loginBean.getNombre());
                            n++;
                        }
                    }
                }
                if (n > 0) {
                    loadNotificaciones();
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

    public void guardarNotificacion(ActionEvent ae) {
        FacesMessage msg = null;
        if (notificacion != null) {
            Controlador c = new Controlador();
            if (notificacion.getId() != null) {
                if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                    if (estadoTemp.equals("TRANSFERENCIAS")) {
                        Transferencia transferencia = new Transferencia();
                        transferencia.setSolicitud(notificacion.getSolicitud());
                        transferencia.setFechaPresentacion(notificacion.getFechaPresentacion());
                        transferencia.setCertificado(c.getNextNumeroCertificadoTransferencia());
                        transferencia.setFechaCertificado(new Date());
                        transferencia.setRegistro(notificacion.getRegistro());
                        transferencia.setFechaRegistro(notificacion.getFechaRegistro());
                        transferencia.setDenominacion(notificacion.getDenominacion());
                        transferencia.setSigno(notificacion.getSigno());
                        transferencia.setTitularAnterior(notificacion.getTitularAnterior());
                        transferencia.setTitularActual(notificacion.getTitularActual());
                        transferencia.setApoderadoRepresentanteLegal(notificacion.getApeApodRepre());
                        transferencia.setNotificacion(notificacion.getNotificacion() + "");
                        transferencia.setFechaNotificacion(notificacion.getFechaNotificacion());
                        transferencia.setCasilleroSenadi(notificacion.getCasilleroSenadi());
                        transferencia.setCasilleroJudicial(notificacion.getCasilleroJudicial());
                        transferencia.setResponsable(notificacion.getResponsable());
                        transferencia.setIdentificacion(notificacion.getIdentificacion());
                        transferencia.setDomicilioTitularActual(notificacion.getDomicilioTitularActual());
                        transferencia.setFechaElaboraNotificacion(notificacion.getFechaElaboraNotificacion());
                        transferencia.setEmail(notificacion.getEmail());
                        transferencia.setRo(notificacion.getRo());
                        transferencia.setComprobante(notificacion.getComprobante());
                        transferencia.setCertificadoEmitido(notificacion.isCertificadoEmitido());
                        transferencia.setNotificacionEmitida(notificacion.isNotificacionEmitida());
                        transferencia.setCancelado(notificacion.getCancelado());
                        transferencia.setSolicitante(notificacion.getSolicitante());

                        if (c.validarExistenciaTransferencia(transferencia.getSolicitud())) {
                            PrimeFaces.current().ajax().addCallbackParam("saved", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en trasnferencias con el mismo número de solicitud");
                        } else {
                            if (c.saveTransferencia(transferencia)) {
                                c = new Controlador();
                                Notificacion notificas = c.getNotificacionBySolSenadi(transferencia.getSolicitud());
                                if (c.removeNotificacion(notificas)) {
                                    c.saveHistorial("TRANSFERENCIAS", "NOTIFICADAS", transferencia.getSolicitud(), "PASADO A", loginBean.getUsuario().getId(), loginBean.getNombre());
                                    loadNotificaciones();
                                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                    System.out.println("Se ha pasado la solicitud " + transferencia.getSolicitud() + " de Notificaciones a Transferencias");
                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                } else {
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA TRANSFERENCIA");
                                }
                            } else {
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                            }
                        }
                    } else if (estadoTemp.equals("DESISTIDAS")) {
                        Desistimiento desist = new Desistimiento();
                        desist.setSolicitud(notificacion.getSolicitud());
                        desist.setFechaSolicitud(notificacion.getFechaPresentacion());
                        desist.setResolucion(notificacion.getCertificado());
                        desist.setFechaResolucion(notificacion.getFechaCertificado());
                        desist.setTitulo(notificacion.getRegistro());
                        desist.setFechaTitulo(notificacion.getFechaRegistro());

                        desist.setDenominacion(notificacion.getDenominacion());
                        desist.setSigno(notificacion.getSigno());
                        desist.setTitularAnterior(notificacion.getTitularAnterior());
                        desist.setTitularActual(notificacion.getTitularActual());
                        desist.setAbogadoPatrocinador(notificacion.getApeApodRepre());

                        desist.setCasilleroSenadi(notificacion.getCasilleroSenadi());
                        desist.setCasilleroJudicial(notificacion.getCasilleroJudicial());
                        desist.setEmail(notificacion.getEmail());
                        desist.setRo(notificacion.getRo());
                        desist.setResponsable(notificacion.getResponsable());
                        desist.setIdentificacion(notificacion.getIdentificacion());
                        desist.setComprobante(notificacion.getComprobante());
                        desist.setCancelado(notificacion.getCancelado());
                        desist.setSolicitante(notificacion.getSolicitante());

                        if (c.validarExistenciaDesistimiento(desist.getSolicitud())) {
                            PrimeFaces.current().ajax().addCallbackParam("saved", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en desistimientos con el mismo número de solicitud");
                        } else {
                            if (c.saveDesistimiento(desist)) {
                                c = new Controlador();
                                Notificacion notificas = c.getNotificacionBySolSenadi(desist.getSolicitud());

                                if (c.removeNotificacion(notificas)) {

                                    c.saveHistorial("DESISTIDAS", "NOTIFICADAS", desist.getSolicitud(), "PASADO A", loginBean.getUsuario().getId(), loginBean.getNombre());
                                    loadNotificaciones();
                                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                    System.out.println("Se ha pasado la solicitud " + desist.getSolicitud() + " de notificaciones a desistidas");
                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                } else {
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA NOTIFICACION");
                                }
                            } else {
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                            }
                        }
                    } else {
                        Caducada caducada = new Caducada();
                        caducada.setSolicitud(notificacion.getSolicitud());
                        caducada.setFechaPresentacion(notificacion.getFechaPresentacion()); //<-----------
                        caducada.setResolucion(notificacion.getCertificado());
                        caducada.setFechaResolucion(notificacion.getFechaCertificado());
                        caducada.setRegistro(notificacion.getRegistro());
                        caducada.setFechaRegistro(notificacion.getFechaRegistro());
                        caducada.setDenominacion(notificacion.getDenominacion());
                        caducada.setSigno(notificacion.getSigno());
                        caducada.setTitularAnterior(notificacion.getTitularAnterior());
                        caducada.setTitularActual(notificacion.getTitularActual());
                        caducada.setApoderadoRepresetante(notificacion.getApeApodRepre());
                        caducada.setFechaNotificacion(notificacion.getFechaNotificacion());
                        caducada.setCasilleroSenadi(notificacion.getCasilleroSenadi());
                        caducada.setCasilleroJudicial(notificacion.getCasilleroJudicial());
                        caducada.setEmail(notificacion.getEmail());
                        caducada.setRo(notificacion.getRo());
                        caducada.setResponsable(notificacion.getResponsable());
                        caducada.setIdentificacion(notificacion.getIdentificacion());
                        caducada.setComprobante(notificacion.getComprobante());
                        caducada.setCancelado(notificacion.getCancelado());
                        caducada.setSolicitante(notificacion.getSolicitante());

                        if (c.validarExistenciaCaducada(caducada.getSolicitud())) {
                            PrimeFaces.current().ajax().addCallbackParam("saved", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en caducadas con el mismo número de solicitud");
                        } else {
                            if (c.saveCaducada(caducada)) {

                                c = new Controlador();
                                Notificacion notificas = c.getNotificacionBySolSenadi(caducada.getSolicitud());

                                if (c.removeNotificacion(notificas)) {

                                    c.saveHistorial("CADUCADAS-NEGADAS", "NOTIFICADAS", caducada.getSolicitud(), "PASADO A", loginBean.getUsuario().getId(), loginBean.getNombre());
                                    loadNotificaciones();
                                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                    System.out.println("Se ha pasado la solicitud " + caducada.getSolicitud() + " de notificaciones a caducada-negada");
                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                } else {
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA NOTIFICACIÓN");
                                }
                            } else {
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                            }
                        }
                    }
                } else {
                    //Editar Notificacion
                    if (c.validarExistenciaNotificacion(notificacion)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                    } else {

                        if (roselectable) {
                            notificacion.setRo(razon);
                        }
                        notificacion.setSolicitud(notificacion.getSolicitud().toUpperCase());
                        if (c.updateNotificacion(notificacion)) {
                            c.saveHistorial("NOTIFICADAS", "NOTIFICADAS", notificacion.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICACIÓN EDITADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA NOTIFICACIÓN");
                        }
                    }
                }

            } else {
                //Guardar Notificacion
                if (c.existeTramiteNotificacion(notificacion.getSolicitud())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                } else {
                    ModificacionApp mapp = c.getModificacionApp(notificacion.getSolicitud());
                    if (mapp.getId() != null) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE NO SE PUEDE REGISTRAR: " + mapp.getObservacion());
                    } else {
                        boolean habilitado = true;
                        if (notificacion.getDenominacion() != null && !notificacion.getDenominacion().trim().isEmpty()
                                && notificacion.getRegistro() != null && !notificacion.getRegistro().trim().isEmpty()) {
                            if (c.existsTituloCanceladoByTituloAndDenominacion(notificacion.getRegistro(), notificacion.getDenominacion(), false)) {
                                TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(notificacion.getRegistro(), notificacion.getDenominacion());
                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN '"
                                            + notificacion.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    notificacion = new Notificacion();
                                    habilitado = false;
                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                    notificacion.setCancelado("PARCIAL");
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN '"
                                            + notificacion.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN '"
                                            + notificacion.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    notificacion = new Notificacion();
                                    habilitado = false;
                                }
                            } else {
                                habilitado = true;
                            }
                        }
                        if (habilitado) {
                            if (c.saveNotificacion(notificacion)) {
                                c.saveModificacionApp(notificacion.getDenominacion(), notificacion.getRegistro(), notificacion.getSolicitud(), "TRANSFERENCIA", loginBean.getNombre());
                                c.saveHistorial("NOTIFICADAS", "NOTIFICADAS", notificacion.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                                loadNotificaciones();
                                PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICACIÓN GUARDADA CON ÉXITO");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL GUARDAR LA NOTIFICACIÓN");
                            }
                        }
                    }
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void validarNotificacion(Notificacion notif) {
        FacesMessage msg = null;
        if (notif != null) {
            rutaNotificacionCasillero = "";
            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionBySolicitud(notif.getSolicitud(), true);
            if (!uploads.isEmpty()) {
                if (uploads.size() > 1) {
                    for (int i = 0; i < uploads.size(); i++) {
                        UploadNotificacion unaux = uploads.get(i);
                        String rutaux = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                        int conf = Operaciones.validaTextoEnPdf(rutaux, "CERTIFICADO DE TRANSFERENCIA No");
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
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ NINGUNA NOTIFICACIÓN DEL TRÁMITE " + notif.getSolicitud());
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UNA NOTIFICACIÓN SELECCIONADA");
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

//            System.out.println(notificacion.getFechaCertificado());
            if (!c.validarDelegadoActivo("secretaria")) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA SECRETARIA ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else {
                if (!c.getRosBySolicitud(notificacion.getSolicitud()).isEmpty()) {
                    notificacion.setFechaNotificacion(new Date());
                    c.updateNotificacion(notificacion);
                    System.out.println("Descargando Notificación: " + notificacion.getSolicitud());
                    loginBean.setTransferenciaFlotante(null);
                    loginBean.setNotificacionFlotante(notificacion);

                    loginBean.setTransferenciasFlotantes(new ArrayList<Transferencia>());
                    loginBean.setNotificacionesFlotantes(new ArrayList<Notificacion>());
                    loginBean.setVarious(false);

                    PrimeFaces.current().ajax().addCallbackParam("doit", true);

                    System.out.println("envía notificación descargar");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CARGANDO REPORTE");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE " + notificacion.getSolicitud() + " NO TIENE MOTIVO DE NOTIFICACIÓN");
                }
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "VALIDE QUE LOS DATOS DE LA NOTIFICACIÓN SEAN CORRECTOS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /* Da la orden de visualizar el reporte, clase Informe (Webservlet)*/
    public void viewNotificacionNo(ActionEvent ae) {
        FacesMessage msg = null;
//        notificacion = (Notificacion) notificacionesDataTable.getRowData();
//        System.out.println("Algo tiene que hacer aquí");
        if (notificacion != null) {
            System.out.println("Descargando Notificación: " + notificacion.getSolicitud());
//            System.out.println(notificacion.getFechaCertificado());
            Controlador c = new Controlador();
            if (!c.validarDelegadoActivo("secretaria")) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA SECRETARIA ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else {

                if (!c.getRosBySolicitud(notificacion.getSolicitud()).isEmpty()) {
                    loginBean.setTransferenciaFlotante(null);
                    loginBean.setNotificacionFlotante(notificacion);

                    loginBean.setTransferenciasFlotantes(new ArrayList<Transferencia>());
                    loginBean.setNotificacionesFlotantes(new ArrayList<Notificacion>());
                    loginBean.setVarious(false);

                    PrimeFaces.current().ajax().addCallbackParam("doit", true);

                    System.out.println("envía notificación descargar");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CARGANDO REPORTE");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE " + notificacion.getSolicitud() + " NO TIENE MOTIVO DE NOTIFICACIÓN");
                }
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "VALIDE QUE LOS DATOS DE LA NOTIFICACIÓN SEAN CORRECTOS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        notificacion = (Notificacion) notificacionesDataTable.getRowData();
        if (notificacion != null) {
            dialogTitle = "SEGUIMIENTO " + notificacion.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(notificacion.getSolicitud());
            setHistorial("");
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: NOTIFICADA";
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
            if (c.existeTramiteTransferencia(tramite)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL TRÁMITE YA SE ENCUENTRA REGISTRADO EN TRANSFERENCIAS");
            } else if (c.existeTramiteNotificacion(tramite)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL TRÁMITE ESTÁ EN LA PESTAÑA DE NOTIFICADOS");
            } else if (c.existeTramiteDesistimiento(tramite)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL TRÁMITE ESTÁ EN LA PESTAÑA DE DESISTIMIENTOS");
            } else if (c.existeTramiteCaducada(tramite)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL TRÁMITE ESTÁ EN LA PESTAÑA DE CADUCADAS-NEGADAS");
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
                                    if (t.getName().trim().toLowerCase().contains("transferencia")
                                            || t.getName().trim().toLowerCase().contains("transmisión")) {

                                        PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                        notificacion.setComprobante(payment.getVoucherNumber());
                                        notificacion.setFechaPresentacion(rf.getApplicationDate());
                                        notificacion.setCertificado(c.getNextNumeroCertificadoTransferencia() + "");
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
                                                    if (hf.getExpYear() != null && !hf.getExpYear().trim().isEmpty()) {
                                                        notificacion.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                    }

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
                                                            notificacion = new Notificacion();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            notificacion.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + notificacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + notificacion.getDenominacion() + " SSE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            notificacion = new Notificacion();
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
                                                            notificacion = new Notificacion();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            notificacion.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + notificacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + notificacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + notificacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            notificacion = new Notificacion();
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
                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UNA TRANSFERENCIA, SINO " + t.getName());
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

            System.out.println("Descargando Múltiples Notificaciones...");

            boolean band = true;
            Controlador c = new Controlador();
            String msj = "";
            for (int i = 0; i < selectedNotificaciones.size(); i++) {
                Notificacion notaux = selectedNotificaciones.get(i);
                if (notaux.getRegistro() == null || notaux.getRegistro().trim().isEmpty()) {
                    band = false;
                    msj = "EL TRÁMITE " + notaux.getSolicitud() + " DEBE POSEER NÚMERO DE REGISTRO";
                    break;
                }
                if (c.getRosBySolicitud(notaux.getSolicitud()).isEmpty()) {
                    band = false;
                    msj = "EL TRÁMITE " + notaux.getSolicitud() + " NO TIENE MOTIVO DE NOTIFICACIÓN";
                    break;
                }
            }
            if (band) {
                loginBean.setNotificacionesFlotantes(selectedNotificaciones);
                loginBean.setTransferenciasFlotantes(new ArrayList<Transferencia>());
                loginBean.setVarious(true);
                loginBean.setAllInOne(!separado);

                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                PrimeFaces.current().ajax().addCallbackParam("view", "repnot");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "DESCARGA", "DESCARGANDO REGISTROS SELECCIONADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", msj);
            }

        } else {
            System.out.println("Sin selección...");
//            context.addCallbackParam("doit", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN SELECCIÓN", "SELECCIONE AL MENOS UN REGISTRO");
//            FacesContext.getCurrentInstance().addMessage(null, msg);
//            System.out.println("No hay seleccionadas");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararShowRo(ActionEvent ae) {
        if (notificacion != null) {
            Controlador c = new Controlador();
            if (notificacion.getId() != null) {
                roos = c.getRosBySolicitud(notificacion.getSolicitud());
            } else {
                roos = new ArrayList<>();
            }
            roshow = "";
            for (int i = 0; i < roos.size(); i++) {
                roshow += roos.get(i).getRo();
            }
            PrimeFaces.current().ajax().addCallbackParam("viewro", true);
        } else {
            PrimeFaces.current().ajax().addCallbackParam("viewro", false);
        }
    }

    public void prepararViewRo(ActionEvent ae) {
        notificacion = (Notificacion) notificacionesDataTable.getRowData();
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
        notificacion = (Notificacion) notificacionesDataTable.getRowData();
        if (notificacion != null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICACIÓN PREPARADA PARA DESCARGA");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE LA NOTIFICACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
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

    public void buscarCasillero(ActionEvent ae) {
        if (notificacion != null && notificacion.getId() != null) {
            Controlador c = new Controlador();
            notificacion.setCasilleroSenadi(c.buscarCasilleroBySolicitud(notificacion.getSolicitud()));
        }
    }

    public String getTooltipAbandono(Notificacion noti) {
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

    public String getTextoFaltanDias(Notificacion noti) {
        if (Operaciones.validarFecha(noti.getFechaPuestaAbandono())) {
            int diasPlazo = 0;
            switch (noti.getTipoAbandono()) {
                case "ERJAFE":
                    diasPlazo = 60;
                    break;
                case "REGLAMENTO":
                    diasPlazo = 10;
                    break;
                case "COA":
                    diasPlazo = 10;
                    break;
                default:
                    diasPlazo = 0;
                    break;
            }

            LocalDate fechaInicio = noti.getFechaPuestaAbandono().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fechaLimite = fechaInicio.plusDays(diasPlazo);
            long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaLimite);

            if (diasRestantes < 0) {
                return "Ya venció el plazo de abandono";
            }

            return "Faltan " + diasRestantes + " días para pasar a abandono";
        } else {
            return "";
        }
    }

    public void viewErjafe(ActionEvent ae) {
        FacesMessage msg = null;
        notificacion = (Notificacion) notificacionesDataTable.getRowData();
        if (notificacion != null) {
            if (notificacion.getRegistro() != null && !notificacion.getRegistro().trim().isEmpty()) {
                if (notificacion.getSolicitante() != null && !notificacion.getSolicitante().trim().isEmpty()) {
                    if (notificacion.getNotificacion() != null) {
                        if(Operaciones.validarFecha(notificacion.getFechaElaboraNotificacion())){
                            loginBean.setNotificacionFlotante(notificacion);
                        loginBean.setTipoTramite(1);
                        loginBean.setVarious(false);
                        PrimeFaces.current().ajax().addCallbackParam("doit", true);

                        System.out.println("envía caducada cambio nombre descargar");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO DOCUMENTO " + notificacion.getSolicitud());
                        }else{
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "INGRESE UNA FECHA DE ELABORACIÓN VÁLIDA");
                        }                        
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO EXISTE EL NÚMERO DE NOTIFICACION");
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
                Notificacion notificadaaux = selectedNotificaciones.get(i);
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
                if (!Operaciones.validarFecha(notificadaaux.getFechaElaboraNotificacion())) {
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
                loginBean.setNotificacionesFlotantes(selectedNotificaciones);
                loginBean.setVarious(true);
                loginBean.setTipoTramite(1);
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                System.out.println("envía notificada transferencia descargar");
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
    public List<Notificacion> getNotificaciones() {
        return notificaciones;
    }

    /**
     * @param notificaciones the notificaciones to set
     */
    public void setNotificaciones(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }

    /**
     * @return the notificacionesFiltradas
     */
    public List<Notificacion> getNotificacionesFiltradas() {
        return notificacionesFiltradas;
    }

    /**
     * @param notificacionesFiltradas the notificacionesFiltradas to set
     */
    public void setNotificacionesFiltradas(List<Notificacion> notificacionesFiltradas) {
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
    public Notificacion getNotificacion() {
        return notificacion;
    }

    /**
     * @param notificacion the notificacion to set
     */
    public void setNotificacion(Notificacion notificacion) {
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
    public List<Notificacion> getSelectedNotificaciones() {
        return selectedNotificaciones;
    }

    /**
     * @param selectedNotificaciones the selectedNotificaciones to set
     */
    public void setSelectedNotificaciones(List<Notificacion> selectedNotificaciones) {
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
}
