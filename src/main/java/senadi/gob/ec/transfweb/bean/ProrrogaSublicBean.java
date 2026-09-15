/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.bean;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
import senadi.gob.ec.transfweb.model.licencia.SubLicenciaUso;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author Michael
 */
@ManagedBean(name = "prorrogasublicBean")
@ViewScoped
public class ProrrogaSublicBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private List<SubLicenciaUso> prorrogas;
    private List<SubLicenciaUso> prorrogasFiltradas;

    private UIData prorrogaDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private SubLicenciaUso prorroga;

    private String estadoTemp;

    private List<SubLicenciaUso> selectedProrrogas;

    private String historial;

    private LoginBean loginBean;

    private String exportName;

    private List<Documento> archivos;

    private String alertaVencidas;

    public ProrrogaSublicBean() {
        loadProrrogasSublicencia();
        selectedProrrogas = new ArrayList<>();
    }

    public void validarProrroga(SubLicenciaUso cambion) {
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

    private boolean faltanDatosAlcance(SubLicenciaUso p) {
        return p.getNumeroProrroga() == null || p.getNumeroAlcance() == null || p.getNumeroAlcance().trim().isEmpty() || p.getFechaAlcance() == null
                || p.getSolicitante() == null || p.getSolicitante().trim().isEmpty();
    }

    public void viewProrroga(ActionEvent ae) {
        FacesMessage msg = null;
        prorroga = (SubLicenciaUso) prorrogaDataTable.getRowData();
        if (prorroga != null && prorroga.getId() != null) {
            if (faltanDatosAlcance(prorroga)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "SE NECESITAN EL NÚMERO DE PRÓRROGA, EL NÚMERO DE ESCRITO (ALCANCE), LA FECHA DEL ESCRITO Y EL SOLICITANTE PARA VER EL PDF DEL TRÁMITE " + prorroga.getSolicitud());
            } else {
                loginBean.setVarious(false);
                loginBean.setSublicencia(prorroga);
                loginBean.setSublicencias(new ArrayList<SubLicenciaUso>());
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
                SubLicenciaUso p = selectedProrrogas.get(i);
                if (faltanDatosAlcance(p)) {
                    faltan += (faltan.isEmpty() ? "" : ", ") + p.getSolicitud();
                }
            }
            if (!faltan.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "SE NECESITAN EL NÚMERO DE PRÓRROGA, EL NÚMERO DE ESCRITO (ALCANCE), LA FECHA DEL ESCRITO Y EL SOLICITANTE PARA VER EL PDF DE: " + faltan);
            } else {
                loginBean.setSublicencia(null);
                loginBean.setSublicencias(selectedProrrogas);
                loginBean.setVarious(true);
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CARGANDO REPORTES");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    private void loadProrrogasSublicencia() {
        Controlador c = new Controlador();
        prorrogas = c.getSublicenciasUsoByTipo("PRORROGA");
        numRegistros = "Número Registros Mostrados: " + prorrogas.size();
        calcularAlertaVencidas();
        exportName = "prorroga_sublicencia_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
    }

    public boolean isProrrogaVencida(SubLicenciaUso p) {
        if (p == null || p.getFechaPuestaProrroga() == null || p.getDiasProrroga() == null) {
            return false;
        }
        LocalDate limite = Operaciones.calcularFechaLimiteProrroga(p.getSolicitud(), p.getFechaPuestaProrroga(), p.getDiasProrroga());
        return !LocalDate.now().isBefore(limite);
    }

    public String getEstiloProrroga(SubLicenciaUso p) {
        if (p == null || p.getFechaPuestaProrroga() == null || p.getDiasProrroga() == null) {
            return "";
        }
        return isProrrogaVencida(p) ? "row-prorroga-vencida" : "row-prorroga";
    }

    public String getDiasRestantes(SubLicenciaUso p) {
        if (p == null || p.getFechaPuestaProrroga() == null || p.getDiasProrroga() == null) {
            return "";
        }
        LocalDate limite = Operaciones.calcularFechaLimiteProrroga(p.getSolicitud(), p.getFechaPuestaProrroga(), p.getDiasProrroga());
        long faltan = ChronoUnit.DAYS.between(LocalDate.now(), limite);
        if (faltan > 0) {
            return "Faltan " + faltan + (faltan == 1 ? " día" : " días");
        }
        if (faltan == 0) {
            return "Vence hoy";
        }
        return "Vencida hace " + Math.abs(faltan) + (Math.abs(faltan) == 1 ? " día" : " días");
    }

    public String getTooltipProrroga(SubLicenciaUso p) {
        if (p == null || p.getFechaPuestaProrroga() == null || p.getDiasProrroga() == null) {
            return "";
        }
        LocalDate limite = Operaciones.calcularFechaLimiteProrroga(p.getSolicitud(), p.getFechaPuestaProrroga(), p.getDiasProrroga());
        long faltan = ChronoUnit.DAYS.between(LocalDate.now(), limite);
        String tipoPlazo = Operaciones.esSolicitudIepi(p.getSolicitud()) ? "días de corrido" : "días laborables";
        if (faltan > 0) {
            return "Faltan " + faltan + " días para que venza la prórroga del trámite " + p.getSolicitud()
                    + " (plazo de " + p.getDiasProrroga() + " " + tipoPlazo + ")";
        }
        if (faltan == 0) {
            return "La prórroga del trámite " + p.getSolicitud() + " vence hoy";
        }
        return "La prórroga del trámite " + p.getSolicitud() + " venció hace " + Math.abs(faltan)
                + " días. Revise el expediente y remítalo a Certificados, Notificaciones o Abandonos";
    }

    private void calcularAlertaVencidas() {
        int vencidas = 0;
        for (int i = 0; prorrogas != null && i < prorrogas.size(); i++) {
            if (isProrrogaVencida(prorrogas.get(i))) {
                vencidas++;
            }
        }
        if (vencidas > 0) {
            alertaVencidas = "ALERTA: " + vencidas + " PRÓRROGA(S) CON EL PLAZO VENCIDO. REVISE EL EXPEDIENTE Y REMÍTALO A CERTIFICADOS, NOTIFICACIONES O ABANDONOS";
        } else {
            alertaVencidas = "";
        }
    }

    public void buscarProrroga(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            prorrogas = c.getSublicenciasUsoByCriteriaAndType(criterio, "PRORROGA");
            numRegistros = "Número Registros Mostrados: " + prorrogas.size();
        calcularAlertaVencidas();
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
            prorrogas = c.getSublicenciasUsoByFechaAndType(fechaInicio, fechaFin, "PRORROGA");
            numRegistros = "Número Registros Mostrados: " + prorrogas.size();
        calcularAlertaVencidas();
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
        prorroga = (SubLicenciaUso) prorrogaDataTable.getRowData();
        if (prorroga != null) {
            Controlador c = new Controlador();
            if (c.removeSublicenciaUso(prorroga)) {
                loadProrrogasSublicencia();
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
        prorroga = (SubLicenciaUso) prorrogaDataTable.getRowData();
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
            if (prorroga.getSolicitante() == null || prorroga.getSolicitante().trim().isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "DEBE INGRESAR EL SOLICITANTE PARA EDITAR LA PRÓRROGA");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return;
            }
            if (c.validarExistenciaSublicenciaUso(prorroga)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
            } else {
                prorroga.setSolicitud(prorroga.getSolicitud().toUpperCase());
                if (prorroga.getNumeroProrroga() == null) {
                    prorroga.setNumeroProrroga(c.getNextNumeroProrrogaSublicencia(new Date()));
                }
                if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                    if (estadoTemp.equals("CERTIFICADO")) {
                        prorroga.setTipoEstado("SUBLICENCIA");
                        prorroga.setSublicenciaNo(c.getNextSublicenciaUsoNo());
                    } else if (estadoTemp.equals("ABANDONO")) {
                        prorroga.setTipoEstado("ABANDONO");
                        prorroga.setFechaAbandono(new Date());
                        if (prorroga.getNumeroAbandono() == null) {
                            prorroga.setNumeroAbandono(c.getNextNumeroAbandonoSublicencia(new Date()));
                        }
                    } else {
                        prorroga.setTipoEstado("NOTIFICADA");
                    }
                }
                if (c.updateSublicenciaUso(prorroga)) {
                    c.saveHistorial(estadoTemp != null && !estadoTemp.trim().isEmpty() ? prorroga.getTipoEstado() + "_SUBLICENCIA" : "PRORROGA_SUBLICENCIA", "PRORROGA_SUBLICENCIA", prorroga.getSolicitud(), estadoTemp != null && !estadoTemp.trim().isEmpty() ? "PASADO A " + prorroga.getTipoEstado() : "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                    loadProrrogasSublicencia();
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
        prorroga = (SubLicenciaUso) prorrogaDataTable.getRowData();
        if (prorroga != null) {
            dialogTitle = "SEGUIMIENTO " + prorroga.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(prorroga.getSolicitud());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: PRORROGA_SUBLICENCIA";
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

    public List<SubLicenciaUso> getProrrogas() {
        return prorrogas;
    }

    public void setProrrogas(List<SubLicenciaUso> prorrogas) {
        this.prorrogas = prorrogas;
    }

    public List<SubLicenciaUso> getProrrogasFiltradas() {
        return prorrogasFiltradas;
    }

    public void setProrrogasFiltradas(List<SubLicenciaUso> prorrogasFiltradas) {
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

    public SubLicenciaUso getProrroga() {
        return prorroga;
    }

    public void setProrroga(SubLicenciaUso prorroga) {
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

    public List<SubLicenciaUso> getSelectedProrrogas() {
        return selectedProrrogas;
    }

    public void setSelectedProrrogas(List<SubLicenciaUso> selectedProrrogas) {
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

    /**
     * @return the alertaVencidas
     */
    public String getAlertaVencidas() {
        return alertaVencidas;
    }

    /**
     * @param alertaVencidas the alertaVencidas to set
     */
    public void setAlertaVencidas(String alertaVencidas) {
        this.alertaVencidas = alertaVencidas;
    }
}
