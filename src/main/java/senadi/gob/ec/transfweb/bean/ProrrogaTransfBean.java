/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.bean;

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
import senadi.gob.ec.transfweb.model.Documento;
import senadi.gob.ec.transfweb.model.Historial;
import senadi.gob.ec.transfweb.model.UploadNotificacion;
import senadi.gob.ec.transfweb.model.Prorroga;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.model.Transferencia;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author Michael
 */
@ManagedBean(name = "prorrogatransfBean")
@ViewScoped
public class ProrrogaTransfBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private List<Prorroga> prorrogas;
    private List<Prorroga> prorrogasFiltradas;

    private UIData prorrogaDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private Prorroga prorroga;

    private String estadoTemp;

    private List<Prorroga> selectedProrrogas;

    private String historial;

    private LoginBean loginBean;

    private String exportName;

    private List<Documento> archivos;

    public ProrrogaTransfBean() {
        loadProrrogasTransf();
        selectedProrrogas = new ArrayList<>();
    }

    public void validarProrroga(Prorroga cambion) {
        FacesMessage msg = null;
        if (cambion != null) {
            String ruta = "";
            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionBySolicitud(cambion.getSolicitud(), true);
            for (int i = 0; i < uploads.size(); i++) {
                UploadNotificacion unaux = uploads.get(i);
                if (unaux.getTipo() != null && unaux.getTipo().contains("PRORROGA")) {
                    ruta = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                    break;
                }
            }
            if (!ruta.trim().isEmpty()) {
                PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                PrimeFaces.current().ajax().addCallbackParam("view", ruta);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "PRÓRROGA CARGADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ EL DOCUMENTO NOTIFICADO DE LA PRÓRROGA DEL TRÁMITE " + cambion.getSolicitud());
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UNA PRÓRROGA SELECCIONADA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    private boolean faltanDatosAlcance(Prorroga p) {
        return p.getNumeroProrroga() == null || p.getNumeroAlcance() == null || p.getNumeroAlcance().trim().isEmpty() || p.getFechaAlcance() == null;
    }

    public void viewProrroga(ActionEvent ae) {
        FacesMessage msg = null;
        prorroga = (Prorroga) prorrogaDataTable.getRowData();
        if (prorroga != null && prorroga.getId() != null) {
            if (faltanDatosAlcance(prorroga)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "SE NECESITAN EL NÚMERO DE PRÓRROGA, EL NÚMERO DE ESCRITO (ALCANCE) Y LA FECHA DEL ESCRITO PARA VER EL PDF DEL TRÁMITE " + prorroga.getSolicitud());
            } else {
                loginBean.setVarious(false);
                loginBean.setProrroga(prorroga);
                loginBean.setProrrogas(new ArrayList<Prorroga>());
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CARGANDO REPORTE");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "VALIDE QUE LOS DATOS DE LA PRÓRROGA SEAN CORRECTOS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void downloadSelected(ActionEvent ae) {
        FacesMessage msg = null;
        if (selectedProrrogas != null && !selectedProrrogas.isEmpty()) {
            String faltan = "";
            for (int i = 0; i < selectedProrrogas.size(); i++) {
                Prorroga p = selectedProrrogas.get(i);
                if (faltanDatosAlcance(p)) {
                    faltan += (faltan.isEmpty() ? "" : ", ") + p.getSolicitud();
                }
            }
            if (!faltan.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "SE NECESITAN EL NÚMERO DE PRÓRROGA, EL NÚMERO DE ESCRITO (ALCANCE) Y LA FECHA DEL ESCRITO PARA VER EL PDF DE: " + faltan);
            } else {
                loginBean.setProrroga(null);
                loginBean.setProrrogas(selectedProrrogas);
                loginBean.setVarious(true);
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CARGANDO REPORTES");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    private void loadProrrogasTransf() {
        Controlador c = new Controlador();
        prorrogas = c.getProrrogas();
        numRegistros = "Número Registros Mostrados: " + prorrogas.size();
        exportName = "prorroga_transf_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
    }

    public void buscarProrroga(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            prorrogas = c.getProrrogasByCriteria(criterio);
            numRegistros = "Número Registros Mostrados: " + prorrogas.size();
            if (prorrogas.isEmpty()) {
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

    public void buscarProrrogasPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            prorrogas = c.getProrrogasByFecha(fechaInicio, fechaFin);
            numRegistros = "Número Registros Mostrados: " + prorrogas.size();
            if (prorrogas.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarProrroga(ActionEvent ae) {
        FacesMessage msg = null;
        prorroga = (Prorroga) prorrogaDataTable.getRowData();
        if (prorroga != null) {
            Controlador c = new Controlador();
            if (c.removeProrroga(prorroga)) {
                loadProrrogasTransf();
                System.out.println("Prórroga " + prorroga.getSolicitud() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "PRÓRROGA " + prorroga.getSolicitud() + " ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR PRÓRROGA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR PRÓRROGA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {
        saveEdit = "EDITAR";
        edicion = true;
        estadoTemp = "";

        FacesMessage msg = null;
        prorroga = (Prorroga) prorrogaDataTable.getRowData();
        if (prorroga != null) {
            dialogTitle = "EDITAR PRÓRROGA " + prorroga.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar la Prórroga: " + prorroga.getSolicitud() + "?";

            Controlador c = new Controlador();
            RenewalForm rf = c.getRenewalFormsByApplicationNumber(prorroga.getSolicitud());
            if (rf.getId() != null) {
                prorroga.setIdRenewalForm(rf.getId());
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "PRÓRROGA CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR PRÓRROGA");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void guardarProrroga(ActionEvent ae) {
        FacesMessage msg = null;
        if (prorroga != null && prorroga.getId() != null) {
            Controlador c = new Controlador();
            if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                boolean movido;
                String destino;
                if (estadoTemp.equals("CERTIFICADO")) {
                    destino = "TRANSFERENCIA";
                    Transferencia t = new Transferencia();
                    t.setSolicitud(prorroga.getSolicitud());
                    t.setFechaPresentacion(prorroga.getFechaPresentacion());
                    t.setFechaCertificado(new Date());
                    t.setRegistro(prorroga.getRegistro());
                    t.setFechaRegistro(prorroga.getFechaRegistro());
                    t.setDenominacion(prorroga.getDenominacion());
                    t.setSigno(prorroga.getSigno());
                    t.setTitularAnterior(prorroga.getTitularAnterior());
                    t.setTitularActual(prorroga.getTitularActual());
                    t.setApoderadoRepresentanteLegal(prorroga.getApeApodRepre());
                    t.setFechaNotificacion(prorroga.getFechaNotificacion());
                    t.setCasilleroSenadi(prorroga.getCasilleroSenadi());
                    t.setCasilleroJudicial(prorroga.getCasilleroJudicial());
                    t.setRo(prorroga.getRo());
                    t.setResponsable(prorroga.getResponsable());
                    t.setIdentificacion(prorroga.getIdentificacion());
                    t.setCertificado(c.getNextNumeroCertificadoTransferencia());
                    t.setNotificacion(prorroga.getNotificacion() + "");
                    t.setDomicilioTitularActual(prorroga.getDomicilioTitularActual());
                    t.setFechaElaboraNotificacion(prorroga.getFechaElaboraNotificacion());
                    t.setEmail(prorroga.getEmail());
                    t.setComprobante(prorroga.getComprobante());
                    t.setCertificadoEmitido(prorroga.isCertificadoEmitido());
                    t.setNotificacionEmitida(prorroga.isNotificacionEmitida());
                    t.setCancelado(prorroga.getCancelado());
                    t.setSolicitante(prorroga.getSolicitante());
                    movido = c.saveTransferencia(t);
                } else {
                    destino = "NOTIFICADA";
                    Notificacion n = new Notificacion();
                    n.setSolicitud(prorroga.getSolicitud());
                    n.setFechaPresentacion(prorroga.getFechaPresentacion());
                    n.setNotificacion(prorroga.getNotificacion());
                    n.setFechaNotificacion(prorroga.getFechaNotificacion());
                    n.setRegistro(prorroga.getRegistro());
                    n.setFechaRegistro(prorroga.getFechaRegistro());
                    n.setDenominacion(prorroga.getDenominacion());
                    n.setSigno(prorroga.getSigno());
                    n.setTitularAnterior(prorroga.getTitularAnterior());
                    n.setTitularActual(prorroga.getTitularActual());
                    n.setApeApodRepre(prorroga.getApeApodRepre());
                    n.setRo(prorroga.getRo());
                    n.setCasilleroSenadi(prorroga.getCasilleroSenadi());
                    n.setCasilleroJudicial(prorroga.getCasilleroJudicial());
                    n.setResponsable(prorroga.getResponsable());
                    n.setIdentificacion(prorroga.getIdentificacion());
                    n.setDomicilioTitularActual(prorroga.getDomicilioTitularActual());
                    n.setFechaElaboraNotificacion(prorroga.getFechaElaboraNotificacion());
                    n.setEmail(prorroga.getEmail());
                    n.setFechaCertificado(prorroga.getFechaCertificado());
                    n.setComprobante(prorroga.getComprobante());
                    n.setCertificado(prorroga.getCertificado());
                    n.setCertificadoEmitido(prorroga.isCertificadoEmitido());
                    n.setNotificacionEmitida(prorroga.isNotificacionEmitida());
                    n.setCancelado(prorroga.getCancelado());
                    n.setSolicitante(prorroga.getSolicitante());
                    movido = c.saveNotificacion(n);
                }
                if (movido && c.removeProrroga(prorroga)) {
                    c.saveHistorial(destino, "PRORROGA_TRANSF", prorroga.getSolicitud(), "PASADO A", loginBean.getUsuario().getId(), loginBean.getNombre());
                    loadProrrogasTransf();
                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "PRÓRROGA PASADA A " + destino + " CON ÉXITO");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL PASAR LA PRÓRROGA A " + destino);
                }
            } else if (c.validarExistenciaProrroga(prorroga)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
            } else {
                prorroga.setSolicitud(prorroga.getSolicitud().toUpperCase());
                if (prorroga.getNumeroProrroga() == null) {
                    prorroga.setNumeroProrroga(c.getNextNumeroProrrogaTransf(new Date()));
                }
                if (c.updateProrroga(prorroga)) {
                    c.saveHistorial("PRORROGA_TRANSF", "PRORROGA_TRANSF", prorroga.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                    loadProrrogasTransf();
                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "PRÓRROGA EDITADA CON ÉXITO");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL EDITAR LA PRÓRROGA");
                }
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR PRÓRROGA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        prorroga = (Prorroga) prorrogaDataTable.getRowData();
        if (prorroga != null) {
            dialogTitle = "SEGUIMIENTO " + prorroga.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(prorroga.getSolicitud());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: PRORROGA_TRANSF";
            }
        }
    }

    public void buscarCasillero(ActionEvent ae) {
        if (prorroga != null && prorroga.getId() != null) {
            Controlador c = new Controlador();
            prorroga.setCasilleroSenadi(c.buscarCasilleroBySolicitud(prorroga.getSolicitud()));
        }
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (prorroga != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + prorroga.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(prorroga.getIdRenewalForm(), prorroga.getSolicitud());
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
     * @return the prorrogas
     */
    public List<Prorroga> getProrrogas() {
        return prorrogas;
    }

    /**
     * @param prorrogas the prorrogas to set
     */
    public void setProrrogas(List<Prorroga> prorrogas) {
        this.prorrogas = prorrogas;
    }

    /**
     * @return the prorrogasFiltradas
     */
    public List<Prorroga> getProrrogasFiltradas() {
        return prorrogasFiltradas;
    }

    /**
     * @param prorrogasFiltradas the prorrogasFiltradas to set
     */
    public void setProrrogasFiltradas(List<Prorroga> prorrogasFiltradas) {
        this.prorrogasFiltradas = prorrogasFiltradas;
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
     * @return the prorrogaDataTable
     */
    public UIData getProrrogaDataTable() {
        return prorrogaDataTable;
    }

    /**
     * @param prorrogaDataTable the prorrogaDataTable to set
     */
    public void setProrrogaDataTable(UIData prorrogaDataTable) {
        this.prorrogaDataTable = prorrogaDataTable;
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
     * @return the prorroga
     */
    public Prorroga getProrroga() {
        return prorroga;
    }

    /**
     * @param prorroga the prorroga to set
     */
    public void setProrroga(Prorroga prorroga) {
        this.prorroga = prorroga;
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
     * @return the selectedProrrogas
     */
    public List<Prorroga> getSelectedProrrogas() {
        return selectedProrrogas;
    }

    /**
     * @param selectedProrrogas the selectedProrrogas to set
     */
    public void setSelectedProrrogas(List<Prorroga> selectedProrrogas) {
        this.selectedProrrogas = selectedProrrogas;
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

    public String getEstadoTemp() {
        return estadoTemp;
    }

    public void setEstadoTemp(String estadoTemp) {
        this.estadoTemp = estadoTemp;
    }
}
