/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.bean;

import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.PrimeFaces;
import org.primefaces.component.api.UIData;
import senadi.gob.ec.transfweb.model.ModificationScope;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author michael
 */
@ManagedBean(name = "scopeBean")
@ViewScoped
public class ModificationScopeBean {

    private List<ModificationScope> scopes;
    private List<ModificationScope> scopesFilter;
    private ModificationScope scope;
    private UIData scopeDataTable;
    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;
    
    private String labelAttended;
    
    private LoginBean login;

    public ModificationScopeBean() {
        loadData();
    }

    private void loadData() {
        Controlador c = new Controlador();
        scopes = c.getModificationScopesSent();
        login = c.getLogin();
    }

    public void viewScope(ActionEvent ae) {
        FacesMessage msg;
        scope = (ModificationScope) scopeDataTable.getRowData();
        if (scope != null && scope.getId() != null) {

            if (scope.getPathScope() != null && !scope.getPathScope().trim().isEmpty()) {
                Controlador c = new Controlador();
                RenewalForm rfaux = c.getRenewalFormsByApplicationNumber(scope.getAffectedApplicationNumber());
                String rutaScope = Operaciones.RUTA_RENEWAL + rfaux.getId() + "/" + scope.getPathScope();
                PrimeFaces.current().ajax().addCallbackParam("viewscope", true);
                PrimeFaces.current().ajax().addCallbackParam("view", rutaScope);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACION", "DOCUMENTO CARGADO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRO EL DOCUMENTO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA A CARGAR EL DOCUMENTO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void viewFormulario(ActionEvent ae) {
        FacesMessage msg;
        scope = (ModificationScope) scopeDataTable.getRowData();
        if (scope != null && scope.getId() != null) {
            Controlador c = new Controlador();
            RenewalForm rfaux = c.getRenewalFormsByApplicationNumber(scope.getAffectedApplicationNumber());
            String rutaScope = Operaciones.RUTA_RENEWAL + rfaux.getId() + "/" + "pdf_scope_renewalfrm_" + scope.getId() + ".pdf";
            PrimeFaces.current().ajax().addCallbackParam("viewform", true);
            PrimeFaces.current().ajax().addCallbackParam("view", rutaScope);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACION", "DOCUMENTO CARGADO");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA A CARGAR EL DOCUMENTO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarPorCriterio(ActionEvent ae) {
        FacesMessage msg;
        if (criterio != null && !criterio.trim().isEmpty()) {
            Controlador c = new Controlador();
            scopes = c.getScopesByCriterio(criterio);
            if (scopes.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACION", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACION", "INFORMACIÓN CARGADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarPorFechaPresentacion(ActionEvent ae) {
        FacesMessage msg;
        if (Operaciones.validarFecha(fechaInicio) && Operaciones.validarFecha(fechaFin)) {
            Controlador c = new Controlador();
            scopes = c.getScopesBySubmissionDate(fechaInicio, fechaFin);
            if (scopes.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACION", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACION", "INFORMACIÓN CARGADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE FECHAS CORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void putAttended(ActionEvent ae) {
        scope = (ModificationScope) scopeDataTable.getRowData();
        if (scope != null && scope.getId() != null) {
            labelAttended = "Ingrese una observación para registrar el alcance como atendido:";
            PrimeFaces.current().ajax().addCallbackParam("viewat", true);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE RECONOCE EL REGISTRO SELECCIONADO, INTENTE MÁS TARDE");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }
    
    public void showAttended(ActionEvent ae) {
        scope = (ModificationScope) scopeDataTable.getRowData();
        if (scope != null && scope.getId() != null) {
            labelAttended = "Razón:";
            PrimeFaces.current().ajax().addCallbackParam("viewat", true);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE RECONOCE EL REGISTRO SELECCIONADO, INTENTE MÁS TARDE");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void saveAttended(ActionEvent ae) {
        FacesMessage msg;
        if (scope != null && scope.getId() != null) {
            scope.setAttended(true);
            scope.setAttendedDate(new Date());
            if (!scope.getAttendedObservation().trim().isEmpty()) {
                Controlador c = new Controlador();
                if (c.updateScope(scope)) {
                    c.saveHistorial("ALCANCE", "ALCANCE", scope.getAffectedApplicationNumber(), "ATENDIDO", login.getUsuario().getId(), login.getNombre());
                    PrimeFaces.current().ajax().addCallbackParam("scopesa", true);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE HA GUARDADO CORRECTAMENTE");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GUARDAR, INTENTE MÁS TARDE");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "CAMPO VACÍO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GUARDAR, INTENTE MÁS TARDE");

        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return the scope
     */
    public ModificationScope getScope() {
        return scope;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(ModificationScope scope) {
        this.scope = scope;
    }

    /**
     * @return the scopeDataTable
     */
    public UIData getScopeDataTable() {
        return scopeDataTable;
    }

    /**
     * @param scopeDataTable the scopeDataTable to set
     */
    public void setScopeDataTable(UIData scopeDataTable) {
        this.scopeDataTable = scopeDataTable;
    }

    /**
     * @return the scopes
     */
    public List<ModificationScope> getScopes() {
        return scopes;
    }

    /**
     * @param scopes the scopes to set
     */
    public void setScopes(List<ModificationScope> scopes) {
        this.scopes = scopes;
    }

    /**
     * @return the scopesFilter
     */
    public List<ModificationScope> getScopesFilter() {
        return scopesFilter;
    }

    /**
     * @param scopesFilter the scopesFilter to set
     */
    public void setScopesFilter(List<ModificationScope> scopesFilter) {
        this.scopesFilter = scopesFilter;
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
     * @return the login
     */
    public LoginBean getLogin() {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(LoginBean login) {
        this.login = login;
    }

    /**
     * @return the labelAttended
     */
    public String getLabelAttended() {
        return labelAttended;
    }

    /**
     * @param labelAttended the labelAttended to set
     */
    public void setLabelAttended(String labelAttended) {
        this.labelAttended = labelAttended;
    }
}
