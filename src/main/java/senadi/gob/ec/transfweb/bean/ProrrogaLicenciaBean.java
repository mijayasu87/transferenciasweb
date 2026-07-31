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
import senadi.gob.ec.transfweb.model.licencia.LicenciaUso;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author Michael
 */
@ManagedBean(name = "prorrogalicenciaBean")
@ViewScoped
public class ProrrogaLicenciaBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private List<LicenciaUso> prorrogas;
    private List<LicenciaUso> prorrogasFiltradas;

    private UIData prorrogaDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private LicenciaUso prorroga;

    private String estadoTemp;

    private List<LicenciaUso> selectedProrrogas;

    private String historial;

    private LoginBean loginBean;

    private String exportName;

    private List<Documento> archivos;

    public ProrrogaLicenciaBean() {
        loadProrrogasLicencia();
        selectedProrrogas = new ArrayList<>();
    }

    public void validarProrroga(LicenciaUso cambion) {
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

    private boolean faltanDatosAlcance(LicenciaUso p) {
        return p.getNumeroProrroga() == null || p.getNumeroAlcance() == null || p.getNumeroAlcance().trim().isEmpty() || p.getFechaAlcance() == null;
    }

    public void viewProrroga(ActionEvent ae) {
        FacesMessage msg = null;
        prorroga = (LicenciaUso) prorrogaDataTable.getRowData();
        if (prorroga != null && prorroga.getId() != null) {
            if (faltanDatosAlcance(prorroga)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "SE NECESITAN EL NÚMERO DE PRÓRROGA, EL NÚMERO DE ESCRITO (ALCANCE) Y LA FECHA DEL ESCRITO PARA VER EL PDF DEL TRÁMITE " + prorroga.getSolicitud());
            } else {
                loginBean.setVarious(false);
                loginBean.setLicencia(prorroga);
                loginBean.setLicencias(new ArrayList<LicenciaUso>());
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
                LicenciaUso p = selectedProrrogas.get(i);
                if (faltanDatosAlcance(p)) {
                    faltan += (faltan.isEmpty() ? "" : ", ") + p.getSolicitud();
                }
            }
            if (!faltan.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "SE NECESITAN EL NÚMERO DE PRÓRROGA, EL NÚMERO DE ESCRITO (ALCANCE) Y LA FECHA DEL ESCRITO PARA VER EL PDF DE: " + faltan);
            } else {
                loginBean.setLicencia(null);
                loginBean.setLicencias(selectedProrrogas);
                loginBean.setVarious(true);
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CARGANDO REPORTES");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    private void loadProrrogasLicencia() {
        Controlador c = new Controlador();
        prorrogas = c.getLicenciasUsoByTipo("PRORROGA");
        numRegistros = "Número Registros Mostrados: " + prorrogas.size();
        exportName = "prorroga_licencia_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
    }

    public void buscarProrroga(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            prorrogas = c.getLicenciasUsoByCriteriaAndType(criterio, "PRORROGA");
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
            prorrogas = c.getLicenciasUsoByFechaAndType(fechaInicio, fechaFin, "PRORROGA");
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
        prorroga = (LicenciaUso) prorrogaDataTable.getRowData();
        if (prorroga != null) {
            Controlador c = new Controlador();
            if (c.removeLicenciaUso(prorroga)) {
                loadProrrogasLicencia();
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
        prorroga = (LicenciaUso) prorrogaDataTable.getRowData();
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
            if (c.validarExistenciaLicenciaUso(prorroga)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
            } else {
                prorroga.setSolicitud(prorroga.getSolicitud().toUpperCase());
                if (prorroga.getNumeroProrroga() == null) {
                    prorroga.setNumeroProrroga(c.getNextNumeroProrrogaLicencia(new Date()));
                }
                if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                    if (estadoTemp.equals("CERTIFICADO")) {
                        prorroga.setTipoEstado("LICENCIA");
                        prorroga.setLicenciaNo(c.getNextLicenciaUsoNo());
                    } else {
                        prorroga.setTipoEstado("NOTIFICADA");
                    }
                }
                if (c.updateLicenciaUso(prorroga)) {
                    c.saveHistorial(estadoTemp != null && !estadoTemp.trim().isEmpty() ? prorroga.getTipoEstado() + "_LICENCIA" : "PRORROGA_LICENCIA", "PRORROGA_LICENCIA", prorroga.getSolicitud(), estadoTemp != null && !estadoTemp.trim().isEmpty() ? "PASADO A " + prorroga.getTipoEstado() : "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                    loadProrrogasLicencia();
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
        prorroga = (LicenciaUso) prorrogaDataTable.getRowData();
        if (prorroga != null) {
            dialogTitle = "SEGUIMIENTO " + prorroga.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(prorroga.getSolicitud());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: PRORROGA_LICENCIA";
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

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<LicenciaUso> getProrrogas() {
        return prorrogas;
    }

    public void setProrrogas(List<LicenciaUso> prorrogas) {
        this.prorrogas = prorrogas;
    }

    public List<LicenciaUso> getProrrogasFiltradas() {
        return prorrogasFiltradas;
    }

    public void setProrrogasFiltradas(List<LicenciaUso> prorrogasFiltradas) {
        this.prorrogasFiltradas = prorrogasFiltradas;
    }

    public String getCriterio() {
        return criterio;
    }

    public void setCriterio(String criterio) {
        this.criterio = criterio;
    }

    public UIData getProrrogaDataTable() {
        return prorrogaDataTable;
    }

    public void setProrrogaDataTable(UIData prorrogaDataTable) {
        this.prorrogaDataTable = prorrogaDataTable;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public String getSaveEdit() {
        return saveEdit;
    }

    public void setSaveEdit(String saveEdit) {
        this.saveEdit = saveEdit;
    }

    public String getMensajeConfirmacion() {
        return mensajeConfirmacion;
    }

    public void setMensajeConfirmacion(String mensajeConfirmacion) {
        this.mensajeConfirmacion = mensajeConfirmacion;
    }

    public boolean isEdicion() {
        return edicion;
    }

    public void setEdicion(boolean edicion) {
        this.edicion = edicion;
    }

    public LicenciaUso getProrroga() {
        return prorroga;
    }

    public void setProrroga(LicenciaUso prorroga) {
        this.prorroga = prorroga;
    }

    public String getNumRegistros() {
        return numRegistros;
    }

    public void setNumRegistros(String numRegistros) {
        this.numRegistros = numRegistros;
    }

    public String getHistorial() {
        return historial;
    }

    public void setHistorial(String historial) {
        this.historial = historial;
    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public String getExportName() {
        return exportName;
    }

    public void setExportName(String exportName) {
        this.exportName = exportName;
    }

    public List<LicenciaUso> getSelectedProrrogas() {
        return selectedProrrogas;
    }

    public void setSelectedProrrogas(List<LicenciaUso> selectedProrrogas) {
        this.selectedProrrogas = selectedProrrogas;
    }

    public List<Documento> getArchivos() {
        return archivos;
    }

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
