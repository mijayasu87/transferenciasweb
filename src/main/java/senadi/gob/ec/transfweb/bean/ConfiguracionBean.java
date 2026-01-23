/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.bean;

import com.jcraft.jsch.JSchException;
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
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.file.UploadedFile;
import senadi.gob.ec.transfweb.model.Caducada;
import senadi.gob.ec.transfweb.model.CambioCasillero;
import senadi.gob.ec.transfweb.model.Delegacion;
import senadi.gob.ec.transfweb.model.Delegado;
import senadi.gob.ec.transfweb.model.Desistimiento;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.model.RazonCorreccion;
import senadi.gob.ec.transfweb.model.Resolucion;
import senadi.gob.ec.transfweb.model.Transferencia;
import senadi.gob.ec.transfweb.model.cd.CambioDomicilio;
import senadi.gob.ec.transfweb.model.cn.CambioNombre;
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepicas.Owner;
import senadi.gob.ec.transfweb.model.licencia.LicenciaUso;
import senadi.gob.ec.transfweb.model.licencia.SubLicenciaUso;
import senadi.gob.ec.transfweb.model.prenda.PrendaComercial;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudModificacion;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author micharesp
 */
@ManagedBean(name = "configuracionBean")
@ViewScoped
public class ConfiguracionBean implements Serializable {

    private List<Delegado> delegados;
    private Delegado delegado;
    private UIData delegadoDataTable;

    private List<Delegado> secretarias;
    private UIData secretariaDataTable;

    private List<Delegacion> delegaciones;
    private UIData delegacionesDataTable;

    private List<Resolucion> resoluciones;
    private UIData resolucionDataTable;

    private List<Resolucion> resolucionesNotificacion;
    private UIData resolucionNotDataTable;

    private String numtramite;

    private String criterioOwner;
    private List<Owner> owners;
    private List<Owner> ownersFiltrados;
    private Owner owner;
    private UIData ownerDataTable;

    private RenewalForm renewal;
    private boolean cambioc;
    private String confirmChangeMessage;

    private LoginBean login;
    private CambioCasillero cambioCasillero;
    private UploadedFile file;

    private List<String> tiposModificacion;

    private RazonCorreccion razon;

    private String errorrazon;

    public ConfiguracionBean() {
        Controlador c = new Controlador();
        delegados = c.getAllDelegados();
        delegaciones = c.getAllDelegaciones();
        resoluciones = c.getResolucionesByTipo("transferencia");
        resolucionesNotificacion = c.getResolucionesByTipo("notificacion");
        secretarias = c.getAllDelegadosByTipo("secretaria");
        renewal = new RenewalForm();
        cambioc = false;
        confirmChangeMessage = "";
        tiposModificacion = Operaciones.getTiposModificacion();
        login = c.getLogin();
    }

    public void mostrarRazon(ActionEvent ae) {
        razon = new RazonCorreccion();
    }

    public String validarRepetidos(String[] trams) {
        List<String> tramaux = new ArrayList<>();
        for (int i = 0; i < trams.length; i++) {
            String tram = trams[i].trim();
            if (!tramaux.contains(tram)) {
                tramaux.add(tram);
            } else {
                return tram;
            }
        }
        return "no";
    }

    public void ejecutarRazon(ActionEvent ae) {
        FacesMessage msg = null;
        if (razon != null) {
            System.out.println(razon.getTramites());
            String[] tramites = razon.getTramites().split(",");
            String res = validarRepetidos(tramites);
            if (res.equals("no")) {
                Object[] datos = null;
                if (razon.getTipoModificacion().equals("Transferencias")) {
                    datos = transferencias(tramites);
                    if (!datos[0].toString().isEmpty()) {
                        errorrazon = datos[0].toString();
                        System.out.println("error---------------\n" + errorrazon);
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR AL LEER LOS TRÁMITES INGRESADOS: " + errorrazon);
                    } else {
                        List<Transferencia> transferencias = (List<Transferencia>) datos[1];
                        login.setTransferenciasFlotantes(transferencias);
                        login.setNotificacionesFlotantes(new ArrayList<Notificacion>());
                        login.setCambiosNombre(null);
                        login.setVarious(true);
                        login.setAllInOne(false);
                        login.setRazon(razon);

                        PrimeFaces.current().ajax().addCallbackParam("doit", true);
                        PrimeFaces.current().ajax().addCallbackParam("tipo", "transfer");
                        PrimeFaces.current().ajax().addCallbackParam("view", "reportes");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "DESCARGA", "DESCARGANDO TRANSFERENCIAS SELECCIONADAS");
                    }
                } else if (razon.getTipoModificacion().equals("Cambios de Nombre")) {
                    datos = cambiosNombre(tramites);
                    if (!datos[0].toString().isEmpty()) {
                        errorrazon = datos[0].toString();
                        System.out.println("error---------------\n" + errorrazon);
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR AL LEER LOS TRÁMITES INGRESADOS: " + errorrazon);
                    } else {

                        List<CambioNombre> cambiosn = (List<CambioNombre>) datos[1];
                        login.setCambiosNombre(cambiosn);
                        login.setTransferenciasFlotantes(null);
                        login.setNotificacionesFlotantes(new ArrayList<Notificacion>());
                        login.setVarious(true);
                        login.setAllInOne(false);
                        login.setRazon(razon);

                        PrimeFaces.current().ajax().addCallbackParam("doit", true);
                        PrimeFaces.current().ajax().addCallbackParam("tipo", "cambion");
                        PrimeFaces.current().ajax().addCallbackParam("view", "cambionombrep");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "DESCARGA", "DESCARGANDO CAMBIOS DE NOMBRE SELECCIONADOS");
                    }
                } else if (razon.getTipoModificacion().equals("Licencias de Uso")) {
                    datos = licenciasUso(tramites);
                    if (!datos[0].toString().isEmpty()) {
                        errorrazon = datos[0].toString();
                        System.out.println("error---------------\n" + errorrazon);
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR AL LEER LOS TRÁMITES INGRESADOS: " + errorrazon);
                    } else {
                        List<LicenciaUso> licenciasu = (List<LicenciaUso>) datos[1];
                        login.setLicencias(licenciasu);
                        login.setTransferenciasFlotantes(null);
                        login.setNotificacionesFlotantes(new ArrayList<Notificacion>());
                        login.setVarious(true);
                        login.setAllInOne(false);
                        login.setRazon(razon);

                        PrimeFaces.current().ajax().addCallbackParam("doit", true);
                        PrimeFaces.current().ajax().addCallbackParam("tipo", "licur");
                        PrimeFaces.current().ajax().addCallbackParam("view", "licenciausor");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "DESCARGA", "DESCARGANDO LICENCIAS INGRESADAS");
                    }

                } else if (razon.getTipoModificacion().equals("Cambios de Domicilio")) {
                    datos = cambiosDomicilio(tramites);
                    if (!datos[0].toString().isEmpty()) {
                        errorrazon = datos[0].toString();
                        System.out.println("error---------------\n" + errorrazon);
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR AL LEER LOS TRÁMITES INGRESADOS: " + errorrazon);
                    } else {
                        List<CambioDomicilio> cambiosdoc = (List<CambioDomicilio>) datos[1];
                        login.setCambiosDomicilio(cambiosdoc);
                        login.setTransferenciasFlotantes(null);
                        login.setNotificacionesFlotantes(new ArrayList<Notificacion>());
                        login.setVarious(true);
                        login.setAllInOne(false);
                        login.setRazon(razon);

                        PrimeFaces.current().ajax().addCallbackParam("doit", true);
                        PrimeFaces.current().ajax().addCallbackParam("tipo", "cambd");
                        PrimeFaces.current().ajax().addCallbackParam("view", "cambiodomiciliop");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "DESCARGA", "DESCARGANDO CAMBIOS DE DOMICILIO INGRESADAS");
                    }
                } else if(razon.getTipoModificacion().equals("Prendas Comerciales")){
                    datos = prendasComerciales(tramites);
                    if (!datos[0].toString().isEmpty()) {
                        errorrazon = datos[0].toString();
                        System.out.println("error---------------\n" + errorrazon);
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR AL LEER LOS TRÁMITES INGRESADOS: " + errorrazon);
                    } else {
                        List<PrendaComercial> prendasc = (List<PrendaComercial>) datos[1];
                        login.setPrendasComerciales(prendasc);
                        login.setTransferenciasFlotantes(null);
                        login.setNotificacionesFlotantes(new ArrayList<Notificacion>());
                        login.setVarious(true);
                        login.setAllInOne(false);
                        login.setRazon(razon);

                        PrimeFaces.current().ajax().addCallbackParam("doit", true);
                        PrimeFaces.current().ajax().addCallbackParam("tipo", "prendc");
                        PrimeFaces.current().ajax().addCallbackParam("view", "prendacomercialr");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "DESCARGA", "DESCARGANDO PRENDAS COMERCIALES INGRESADAS");
                    }
                } else {
                    //SUBLICENCIAS
                    datos = sublicenciasUso(tramites);
                    if (!datos[0].toString().isEmpty()) {
                        errorrazon = datos[0].toString();
                        System.out.println("error---------------\n" + errorrazon);
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR AL LEER LOS TRÁMITES INGRESADOS: " + errorrazon);
                    } else {
                        List<SubLicenciaUso> sublics = (List<SubLicenciaUso>) datos[1];
                        login.setSublicencias(sublics);
                        login.setTransferenciasFlotantes(null);
                        login.setNotificacionesFlotantes(new ArrayList<Notificacion>());
                        login.setVarious(true);
                        login.setAllInOne(false);
                        login.setRazon(razon);

                        PrimeFaces.current().ajax().addCallbackParam("doit", true);
                        PrimeFaces.current().ajax().addCallbackParam("tipo", "sublics");
                        PrimeFaces.current().ajax().addCallbackParam("view", "sublicusor");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "DESCARGA", "DESCARGANDO SUBLICENCIAS DE USO INGRESADAS");
                    }
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE " + res + " ESTÁ REPETIDO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GENERAR LOS DOCUMENTOS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public Object[] sublicenciasUso(String[] tramites) {
        List<SubLicenciaUso> sublicsu = new ArrayList<>();
        String error = "";
        Controlador c = new Controlador();
        for (int i = 0; i < tramites.length; i++) {
            SubLicenciaUso sub = c.getSublicenciaUsoBySolicitud(tramites[i].trim());
            if (sub.getId() != null) {
                if (sub.getRegistro() == null || sub.getRegistro().trim().isEmpty()) {
                    error += "El trámite " + sub.getSolicitud() + " no tiene número de registro\n";
                } else {
                    sublicsu.add(sub);
                }
            } else {
                error += "No se encontró la sublicencia: " + tramites[i].trim() + "\n";
            }
        }
        return new Object[]{error, sublicsu};
    }
    
    public Object[] prendasComerciales(String[] tramites) {
        List<PrendaComercial> prendasc = new ArrayList<>();
        String error = "";
        Controlador c = new Controlador();
        for (int i = 0; i < tramites.length; i++) {
            PrendaComercial prend = c.getPrendaComercialBySolicitud(tramites[i].trim());
            if (prend.getId() != null) {
                if (prend.getRegistro() == null || prend.getRegistro().trim().isEmpty()) {
                    error += "El trámite " + prend.getSolicitud() + " no tiene número de registro\n";
                } else {
                    prendasc.add(prend);
                }
            } else {
                error += "No se encontró la prenda: " + tramites[i].trim() + "\n";
            }
        }
        return new Object[]{error, prendasc};
    }
    
    public Object[] cambiosDomicilio(String[] tramites) {
        List<CambioDomicilio> cambiosd = new ArrayList<>();
        String error = "";
        Controlador c = new Controlador();
        for (int i = 0; i < tramites.length; i++) {
            CambioDomicilio cam = c.getCambioDomicilioBySolicitud(tramites[i].trim());
            if (cam.getId() != null) {
                if (cam.getRegistro() == null || cam.getRegistro().trim().isEmpty()) {
                    error += "El trámite " + cam.getSolicitud() + " no tiene número de registro\n";
                } else {
                    cambiosd.add(cam);
                }
            } else {
                error += "No se encontró el cambio de domicilio: " + tramites[i].trim() + "\n";
            }
        }
        return new Object[]{error, cambiosd};
    }

    public Object[] licenciasUso(String[] tramites) {
        List<LicenciaUso> licus = new ArrayList<>();
        String error = "";
        Controlador c = new Controlador();
        for (int i = 0; i < tramites.length; i++) {
            LicenciaUso lu = c.getLicenciaUsoBySolicitud(tramites[i].trim());
            if (lu.getId() != null) {
                if (lu.getRegistro() == null || lu.getRegistro().trim().isEmpty()) {
                    error += "El trámite " + lu.getSolicitud() + " no tiene número de registro\n";
                } else {
                    licus.add(lu);
                }
            } else {
                error += "No se encontró la licencia de uso: " + tramites[i].trim() + "\n";
            }
        }
        return new Object[]{error, licus};
    }

    public Object[] cambiosNombre(String[] tramites) {
        List<CambioNombre> cambiosn = new ArrayList<>();
        String error = "";
        Controlador c = new Controlador();
        for (int i = 0; i < tramites.length; i++) {
            CambioNombre cn = c.getCambioNombreBySolicitud(tramites[i].trim());
            if (cn.getId() != null) {
                if (cn.getRegistro() == null || cn.getRegistro().trim().isEmpty()) {
                    error += "El trámite " + cn.getSolicitud() + " no tiene número de registro\n";
                } else {
                    cambiosn.add(cn);
                }
            } else {
                error += "No se encontró el cambio de nombre: " + tramites[i].trim() + "\n";
            }
        }
        return new Object[]{error, cambiosn};
    }

    public Object[] transferencias(String[] tramites) {

        List<Transferencia> transferencias = new ArrayList<>();
        String error = "";
        Controlador c = new Controlador();
        for (int i = 0; i < tramites.length; i++) {
            Transferencia transf = c.getTransferenciaBySolSenadi(tramites[i].trim());
            if (transf.getId() != null) {
                if (transf.getRegistro() == null || transf.getRegistro().trim().isEmpty()) {
                    error += "El trámite " + transf.getSolicitud() + " no tiene número de registro\n";
                } else {
                    transferencias.add(transf);
                }
            } else {
                error += "No se encontró la transferencia: " + tramites[i] + "\n";
            }
        }
        return new Object[]{error, transferencias};
    }

    public void onAddNewDelegado() {
        FacesMessage msg = null;
        Delegado dele = new Delegado();
        dele.setTipo("delegado");
        Controlador c = new Controlador();
        if (c.saveDelegado(dele)) {
            delegados = c.getAllDelegados();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "NUEVO DELEGADO", "AGREGADA");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO AGREGAR UN NUEVO DELEGADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onAddNewSecretaria() {
        FacesMessage msg = null;
        Delegado dele = new Delegado();
        dele.setTipo("secretaria");
        Controlador c = new Controlador();
        if (c.saveDelegado(dele)) {
            secretarias = c.getAllDelegadosByTipo("secretaria");
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "NUEVO SECRETARIA", "AGREGADA");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO AGREGAR UNA NUEVA SECRETARIA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onAddNewDelegacion() {
        FacesMessage msg = null;
        Delegacion dele = new Delegacion();
        Controlador c = new Controlador();
        if (c.saveDelegacion(dele)) {
            delegaciones = c.getAllDelegaciones();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "NUEVA DELEGACIÓN", "AGREGADA");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO AGREGAR UNA NUEVA DELEGACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onAddNewResolucion() {
        FacesMessage msg = null;
        Resolucion resol = new Resolucion();
        Controlador c = new Controlador();
        resol.setTipo("transferencia");
        if (c.saveResolucion(resol)) {
            resoluciones = c.getResolucionesByTipo("transferencia");
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "NUEVA RESOLUCIÓN", "AGREGADA");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO AGREGAR UNA NUEVA RESOLUCIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onAddNewResolucionNotificacion() {
        FacesMessage msg = null;
        Resolucion resol = new Resolucion();
        Controlador c = new Controlador();
        resol.setTipo("notificacion");
        if (c.saveResolucion(resol)) {
            resolucionesNotificacion = c.getResolucionesByTipo("notificacion");
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "NUEVA RESOLUCIÓN", "AGREGADA");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO AGREGAR UNA NUEVA RESOLUCIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowEditDelegado(RowEditEvent<Delegado> event) {
        FacesMessage msg = null;
        Delegado dele = event.getObject();
        if (dele != null) {
            Controlador c = new Controlador();
            if (c.updateDelegado(dele)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "DELEGADO EDITADO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL INTENTAR EDITAR EL DELEGADO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO EDITAR EL DELEGADO");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancelDelegado(RowEditEvent<Delegado> event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CANCELADO", "PROCESO CANCELADO");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowEditSecretaria(RowEditEvent<Delegado> event) {
        FacesMessage msg = null;
        Delegado dele = event.getObject();
        if (dele != null) {
            Controlador c = new Controlador();
            if (c.updateDelegado(dele)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "DELEGADO EDITADO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL INTENTAR EDITAR LA SECRETARIA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO EDITAR LA SECRETARIA");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancelSecretaria(RowEditEvent<Delegado> event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CANCELADO", "PROCESO CANCELADO");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowEditDelegacion(RowEditEvent<Delegacion> event) {
        FacesMessage msg = null;
        Delegacion dele = event.getObject();
        if (dele != null) {
            Controlador c = new Controlador();
            if (c.updateDelegacion(dele)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "DELEGACIÓN EDITADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL INTENTAR EDITAR LA DELEGACIÓN");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO EDITAR LA DELEGACIÓN");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancelDelegacion(RowEditEvent<Delegacion> event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CANCELADO", "PROCESO CANCELADO");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowEditResolucion(RowEditEvent<Resolucion> event) {
        FacesMessage msg = null;
        Resolucion resol = event.getObject();
        if (resol != null) {
            Controlador c = new Controlador();
            if (c.updateResolucion(resol)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "RESOLUCIÓN EDITADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL INTENTAR EDITAR LA RESOLUCIÓN");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO EDITAR LA RESOLUCIÓN");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancelResolucion(RowEditEvent<Resolucion> event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CANCELADO", "PROCESO CANCELADO");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void enableDelegado(Delegado dele) {
        Controlador c = new Controlador();
        if (dele != null) {
            if (dele.isEstado()) {
                dele.setEstado(true);
                c.activeADelegado(dele, delegados);
                delegados = c.getAllDelegados();
            } else {
                dele.setEstado(false);
                c.updateDelegado(dele);
            }

        } else {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE PUDO CARGAR EL DELEGADO");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void eliminarDelegado(ActionEvent ae) {
        FacesMessage msg = null;
        Delegado dele = (Delegado) delegadoDataTable.getRowData();
        if (dele != null) {
            Controlador c = new Controlador();
            if (c.removeDelegado(dele)) {
                delegados = c.getAllDelegados();
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "REMOVIDA", "DELEGADO REMOVIDO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER EL DELEGADO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER EL DELEGADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void enableSecretaria(Delegado dele) {
        Controlador c = new Controlador();
        if (dele != null) {
            if (dele.isEstado()) {
                dele.setEstado(true);
                c.activeADelegado(dele, secretarias);
                secretarias = c.getAllDelegadosByTipo("secretaria");
            } else {
                dele.setEstado(false);
                c.updateDelegado(dele);
            }

        } else {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE PUDO CARGAR LA SECRETARIA");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void eliminarSecretaria(ActionEvent ae) {
        FacesMessage msg = null;
        Delegado dele = (Delegado) secretariaDataTable.getRowData();
        if (dele != null) {
            Controlador c = new Controlador();
            if (c.removeDelegado(dele)) {
                secretarias = c.getAllDelegadosByTipo("secretaria");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "REMOVIDA", "SECRETARIA REMOVIDA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER LA SECRETARIA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER LA SECRETARIA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void enableDelegacion(Delegacion dele) {
        Controlador c = new Controlador();
        if (dele != null) {
            if (dele.isActivo()) {
                dele.setActivo(true);
                c.activeADelegacion(dele, delegaciones);
                delegados = c.getAllDelegados();
            } else {
                dele.setActivo(false);
                c.updateDelegacion(dele);
            }

        } else {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE PUDO CARGAR LA DELEGACIÓN");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void eliminarDelegacion(ActionEvent ae) {
        FacesMessage msg = null;
        Delegacion dele = (Delegacion) delegacionesDataTable.getRowData();
        if (dele != null) {
            Controlador c = new Controlador();
            if (c.removeDelegacion(dele)) {
                delegaciones = c.getAllDelegaciones();
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "REMOVIDA", "DELEGACIÓN REMOVIDA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER LA DELEGACIÓN");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER LA DELEGACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void enableResolucion(Resolucion resol) {
        Controlador c = new Controlador();
        if (resol != null) {
            if (resol.isActivo()) {
                resol.setActivo(true);
                c.activeAResolucion(resol, resoluciones);
                resoluciones = c.getResolucionesByTipo("transferencia");
            } else {
                resol.setActivo(false);
                c.updateResolucion(resol);
            }

        } else {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE PUDO CARGAR LA DELEGACIÓN");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void eliminarResolucion(ActionEvent ae) {
        FacesMessage msg = null;
        Resolucion resol = (Resolucion) resolucionDataTable.getRowData();
        if (resol != null) {
            Controlador c = new Controlador();
            if (c.removeResolucion(resol)) {
                resoluciones = c.getResolucionesByTipo("transferencia");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "REMOVIDA", "RESOLUCIÓN REMOVIDA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER LA RESOLUCIÓN");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER LA RESOLUCIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void enableResolucionNotificacion(Resolucion resol) {
        Controlador c = new Controlador();
        if (resol != null) {
            if (resol.isActivo()) {
                resol.setActivo(true);
                c.activeAResolucion(resol, resolucionesNotificacion);
                resolucionesNotificacion = c.getResolucionesByTipo("notificacion");
            } else {
                resol.setActivo(false);
                c.updateResolucion(resol);
            }

        } else {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE PUDO CARGAR LA DELEGACIÓN");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void eliminarResolucionNotificacion(ActionEvent ae) {
        FacesMessage msg = null;
        Resolucion resol = (Resolucion) resolucionNotDataTable.getRowData();
        if (resol != null) {
            Controlador c = new Controlador();
            if (c.removeResolucion(resol)) {
                resolucionesNotificacion = c.getResolucionesByTipo("notificacion");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "REMOVIDA", "RESOLUCIÓN-NOTIFICACIÓN REMOVIDA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER LA RESOLUCIÓN-NOTIFICACIÓN");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REMOVER LA RESOLUCIÓN-NOTIFICACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararCambioCasillero(ActionEvent ae) {
        FacesMessage message = null;
        //Controlador c = new Controlador();
        numtramite = "";
        renewal = new RenewalForm();
//        casilleroActual = "";
//        nuevoCasillero = "";
//        tipoModificacion = "";
        cambioCasillero = new CambioCasillero();
        cambioc = false;
        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CAMBIO DE CASILLERO INICIADO");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage message = null;
        if (numtramite != null && !numtramite.trim().isEmpty()) {
            Controlador c = new Controlador();
            renewal = c.getRenewalFormsByApplicationNumber(numtramite);
            if (renewal.getId() != null) {

                Owner ownaux = c.getOwnersById(renewal.getOwnerId());

                cambioCasillero = new CambioCasillero();

                if (renewal.getDebugId() != null && renewal.getDebugId() != 0) {
                    HallmarkForms hf = c.getHallmarkFormDepurada(renewal.getDebugId());
                    if (hf.getId() != null) {
                        cambioCasillero.setDenominacion(hf.getDenomination());
                    }
                } else {
                    if (renewal.getExpedient() != null && !renewal.getExpedient().trim().isEmpty()) {
                        PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(renewal.getExpedient());
                        if (ps.getCodigoSolicitudSigno() != null) {
                            cambioCasillero.setDenominacion(ps.getDenominacionSigno());
                        }
                    }
                }

                cambioCasillero.setTramite(numtramite);
                cambioCasillero.setTipoModificacion(c.getTipoModificacion(cambioCasillero.getTramite()));
                cambioCasillero.setTitularCasilleroAnterior(ownaux.getFirstname() + " " + ownaux.getLastname());
                cambioCasillero.setCasilleroAnterior(ownaux.getCasillero());
                cambioCasillero.setCorreoTitularAnterior(ownaux.getEmail());

                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TRÁMITE ENCONTRADO");
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ EL TRÁMITE: " + renewal.getApplicationNumber());
            }

        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN NÚMERO DE TRÁMITE VÁLIDO");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void prepararBusquedaCasillero(ActionEvent ae) {
        owners = new ArrayList<>();
        criterioOwner = "";
    }

    public void buscarCasillero(ActionEvent ae) {
        FacesMessage msg = null;
        if (renewal.getId() != null) {
            if (criterioOwner != null && !criterioOwner.trim().isEmpty()) {
                System.out.println("criterio owner: " + criterioOwner);
                Controlador c = new Controlador();
                owners = new ArrayList<>();

                owners = c.getOwnersByCriteria(criterioOwner);

                if (owners.isEmpty()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CONSULTA REALIZADA");
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ LA MODIFICACIÓN CORRECTAMENTE");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void seleccionarCasillero(ActionEvent ae) {
        FacesMessage msg = null;
        if (cambioCasillero != null) {
            owner = (Owner) ownerDataTable.getRowData();
            if (owner.getId() != null) {
                cambioCasillero.setNuevoCasillero(owner.getCasillero());
                cambioCasillero.setTitularCasilleroNuevo(owner.getFirstname() + " " + owner.getLastname());
                cambioCasillero.setCorreoTitularNuevo(owner.getEmail());
                owners = new ArrayList<>();
                criterioOwner = "";
                cambioc = true;
                confirmChangeMessage = "SEGURO DE CAMBIAR EL CASILLERO " + cambioCasillero.getCasilleroAnterior() + " DEL TRÁMITE " + cambioCasillero.getTramite() + "\nPOR EL CASILLERO " + cambioCasillero.getCasilleroAnterior() + " ?";
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CASILLERO " + owner.getCasillero() + " SELECCIONADO CORRECTAMENTE");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL SELECCIONAR EL CASILLERO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ EL CERTIFICADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void cambiarCasillero() throws IOException {
        FacesMessage msg = null;
        if (renewal.getId() != null) {
//            if (owner.getId() != null) {
            if (cambioCasillero.getId() != null) {

                Controlador c = new Controlador();
//                    System.out.println("nuevocas: "+nuevoCasillero);
                if (!cambioCasillero.getCasilleroAnterior().equals(cambioCasillero.getNuevoCasillero())) {
                    int n = 0;
                    try {
                        n = Integer.parseInt(cambioCasillero.getNuevoCasillero());
                    } catch (NumberFormatException ex) {
                        n = 0;
                    }

                    if (c.existsOwner(n)) {
                        System.out.println("si llego aquí 1");
                        if (file != null) {
                            System.out.println("si llego aquí 2");
                            if (file.getInputStream() != null) {
                                System.out.println("Si existe el archivo: " + file.getFileName());
                            }

                        } else {
                            System.out.println("No existe el archivo");
                        }

                        renewal.setOwnerId(owner.getId());

                        if (c.updateRenewalForm(renewal)) {
                            PpdiSolicitudModificacion modificacion = c.getPpdiSolicitudModificacionByNumTramite(renewal.getApplicationNumber());

                            if (modificacion.getCodigoSolicitudModificacion() != null) {
                                modificacion.setCasilleroIepi(owner.getCasillero());
                                c.updatePpdiSolicitudModificacion(modificacion);
                            }
                            updateModificacion(renewal.getApplicationNumber(), owner.getCasillero());
                            c.saveHistorial("CAMBIO CASILLERO", "CAMBIO CASILLERO", cambioCasillero.getTramite(), "CAMBIO CAS " + cambioCasillero.getCasilleroAnterior() + " -> " + cambioCasillero.getNuevoCasillero(), login.getUsuario().getId(), login.getNombre());
                            PrimeFaces.current().ajax().addCallbackParam("hecho", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "EL TRÁMITE " + renewal.getApplicationNumber() + " HA SIDO ASIGNADO\n"
                                    + "AL CASILLERO " + owner.getCasillero() + " DE " + owner.getFirstname() + " " + owner.getLastname() + " SATISFACTORIAMENTE");
                        } else {
                            PrimeFaces.current().ajax().addCallbackParam("hecho", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL REALIZAR EL CAMBIO DE CASILLERO INTENTE MÁS TARDE");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ EL NUEVO CASILLERO");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL NUEVO CASILLERO DEBE SER DIFERENTE AL CASILLERO ACTUAL DEL TRÁMITE");
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HAY UN PROBLEMA AL REALIZAR EL CAMBIO DE CASILLERO");
            }
//            } else {
//                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR EL CASILLERO");
//            }
//
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ EL CERTIFICADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void handleFileUpload(FileUploadEvent event) throws JSchException, IllegalAccessException, IOException {
        file = event.getFile();
        FacesMessage message = null;
        if (file.getInputStream() != null) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DOCUMENTO ADJUNTADO CORRECTAMENTE");
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ADJUNTAR EL DOCUMENTO");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public boolean updateModificacion(String solicitud, String casillero) {
        Controlador c = new Controlador();
        CambioDomicilio camd = c.getCambioDomicilioBySolicitud(solicitud);
        if (camd.getId() != null) {
            camd.setCasilleroSenadi(casillero);
            c.updateCambioDomicilio(camd);
        } else {
            CambioNombre cambn = c.getCambioNombreBySolicitud(solicitud);
            if (cambn.getId() != null) {
                cambn.setCasilleroSenadi(casillero);
                c.updateCambioNombre(cambn);
            } else {
                PrendaComercial prend = c.getPrendaComercialBySolicitud(solicitud);
                if (prend.getId() != null) {
                    prend.setCasilleroSenadi(casillero);
                    c.updatePrendaComercial(prend);
                } else {
                    LicenciaUso licu = c.getLicenciaUsoBySolicitud(solicitud);
                    if (licu.getId() != null) {
                        licu.setCasilleroSenadi(casillero);
                        c.updateLicenciaUso(licu);
                    } else {
                        SubLicenciaUso subl = c.getSublicenciaUsoBySolicitud(solicitud);
                        if (subl.getId() != null) {
                            subl.setCasilleroSenadi(casillero);
                            c.updateSublicenciaUso(subl);
                        } else {
                            //para indicar que se ha entregado certificado digital firmado
                            Transferencia tranaux = c.getTransferenciaBySolSenadi(solicitud);
                            if (tranaux.getId() != null) {
                                tranaux.setCasilleroSenadi(casillero);
                                c.updateTransferencia(tranaux);
                            } else {
                                //para indicar que se ha realizado una notificación digital firmado
                                Notificacion notaux = c.getNotificacionBySolSenadi(solicitud);
                                if (notaux.getId() != null) {
                                    notaux.setCasilleroSenadi(casillero);
                                    c.updateNotificacion(notaux);
                                } else {
                                    Desistimiento desaux = c.getDesistidasBySolSenadi(solicitud);
                                    if (desaux.getId() != null) {
                                        desaux.setCasilleroSenadi(casillero);
                                        c.updateDesistimiento(desaux);
                                    } else {
                                        Caducada cadaux = c.getCaducadaBySolSenadi(solicitud);
                                        if (cadaux.getId() != null) {
                                            cadaux.setCasilleroSenadi(casillero);
                                            c.updateCaducada(cadaux);
                                        } else {
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public void viewProvidenciaCambioCasillero(ActionEvent ae) {
        FacesMessage msg = null;
        if (cambioCasillero != null && !cambioCasillero.getCasilleroAnterior().trim().isEmpty()) {
            if (cambioCasillero.getNuevoCasillero() != null && !cambioCasillero.getNuevoCasillero().trim().isEmpty()) {

                Controlador c = new Controlador();

                if (!c.validarDelegadoActivo("delegado")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarDelegacionActivo()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarResolucionActiva("transferencia")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else {
                    if (cambioCasillero.getNuevoCasillero().equals(cambioCasillero.getCasilleroAnterior())) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL CASILLERO NUEVO DEBE SER DIFERENTE AL CASILLERO ANTERIOR");
                    } else {
                        if (cambioCasillero.getId() == null) {
                            cambioCasillero.setFechaNotificacion(new Date());
                            cambioCasillero.setFechaProvidencia(new Date());
                            cambioCasillero.setProvidencia(c.nextProvidenciaCambioCasillero());
                            cambioCasillero.setEstado("PENDIENTE");
                            cambioCasillero.setUsuario(login.getNombre());

                            if (cambioCasillero.getDenominacion() == null || cambioCasillero.getDenominacion().trim().isEmpty()) {
                                cambioCasillero.setDenominacion("-denominación no encontrada-");
                            }

                            if (c.saveCambioCasillero(cambioCasillero)) {
                                cambioCasillero = c.getCambioCasilleroWhenNotId(cambioCasillero);
                                System.out.println("CambioCasillero: " + cambioCasillero.getProvidencia());
                            }
                        }

                        login.setCambioCasillero(cambioCasillero);

                        PrimeFaces.current().ajax().addCallbackParam("doit", true);

                        System.out.println("envía providencia cambio casillero descargar");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO PROVIDENCIA CAMBIO CASILLERO " + cambioCasillero.getProvidencia());
                    }

                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "DEBE SELECCIONAR UN CASILLERO A ASIGNAR VÁLIDO");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "CARGE EL TRÁMITE A CAMBIAR EL CASILLERO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return the delegados
     */
    public List<Delegado> getDelegados() {
        return delegados;
    }

    /**
     * @param delegados the delegados to set
     */
    public void setDelegados(List<Delegado> delegados) {
        this.delegados = delegados;
    }

    /**
     * @return the delegado
     */
    public Delegado getDelegado() {
        return delegado;
    }

    /**
     * @param delegado the delegado to set
     */
    public void setDelegado(Delegado delegado) {
        this.delegado = delegado;
    }

    /**
     * @return the delegadoDataTable
     */
    public UIData getDelegadoDataTable() {
        return delegadoDataTable;
    }

    /**
     * @param delegadoDataTable the delegadoDataTable to set
     */
    public void setDelegadoDataTable(UIData delegadoDataTable) {
        this.delegadoDataTable = delegadoDataTable;
    }

    /**
     * @return the delegaciones
     */
    public List<Delegacion> getDelegaciones() {
        return delegaciones;
    }

    /**
     * @param delegaciones the delegaciones to set
     */
    public void setDelegaciones(List<Delegacion> delegaciones) {
        this.delegaciones = delegaciones;
    }

    /**
     * @return the delegacionesDataTable
     */
    public UIData getDelegacionesDataTable() {
        return delegacionesDataTable;
    }

    /**
     * @param delegacionesDataTable the delegacionesDataTable to set
     */
    public void setDelegacionesDataTable(UIData delegacionesDataTable) {
        this.delegacionesDataTable = delegacionesDataTable;
    }

    /**
     * @return the resoluciones
     */
    public List<Resolucion> getResoluciones() {
        return resoluciones;
    }

    /**
     * @param resoluciones the resoluciones to set
     */
    public void setResoluciones(List<Resolucion> resoluciones) {
        this.resoluciones = resoluciones;
    }

    /**
     * @return the resolucionDataTable
     */
    public UIData getResolucionDataTable() {
        return resolucionDataTable;
    }

    /**
     * @param resolucionDataTable the resolucionDataTable to set
     */
    public void setResolucionDataTable(UIData resolucionDataTable) {
        this.resolucionDataTable = resolucionDataTable;
    }

    /**
     * @return the resolucionesNotificacion
     */
    public List<Resolucion> getResolucionesNotificacion() {
        return resolucionesNotificacion;
    }

    /**
     * @param resolucionesNotificacion the resolucionesNotificacion to set
     */
    public void setResolucionesNotificacion(List<Resolucion> resolucionesNotificacion) {
        this.resolucionesNotificacion = resolucionesNotificacion;
    }

    /**
     * @return the resolucionNotDataTable
     */
    public UIData getResolucionNotDataTable() {
        return resolucionNotDataTable;
    }

    /**
     * @param resolucionNotDataTable the resolucionNotDataTable to set
     */
    public void setResolucionNotDataTable(UIData resolucionNotDataTable) {
        this.resolucionNotDataTable = resolucionNotDataTable;
    }

    /**
     * @return the secretarias
     */
    public List<Delegado> getSecretarias() {
        return secretarias;
    }

    /**
     * @param secretarias the secretarias to set
     */
    public void setSecretarias(List<Delegado> secretarias) {
        this.secretarias = secretarias;
    }

    /**
     * @return the secretariaDataTable
     */
    public UIData getSecretariaDataTable() {
        return secretariaDataTable;
    }

    /**
     * @param secretariaDataTable the secretariaDataTable to set
     */
    public void setSecretariaDataTable(UIData secretariaDataTable) {
        this.secretariaDataTable = secretariaDataTable;
    }

    /**
     * @return the numtramite
     */
    public String getNumtramite() {
        return numtramite;
    }

    /**
     * @param numtramite the numtramite to set
     */
    public void setNumtramite(String numtramite) {
        this.numtramite = numtramite;
    }

    /**
     * @return the criterioOwner
     */
    public String getCriterioOwner() {
        return criterioOwner;
    }

    /**
     * @param criterioOwner the criterioOwner to set
     */
    public void setCriterioOwner(String criterioOwner) {
        this.criterioOwner = criterioOwner;
    }

    /**
     * @return the owners
     */
    public List<Owner> getOwners() {
        return owners;
    }

    /**
     * @param owners the owners to set
     */
    public void setOwners(List<Owner> owners) {
        this.owners = owners;
    }

    /**
     * @return the ownersFiltrados
     */
    public List<Owner> getOwnersFiltrados() {
        return ownersFiltrados;
    }

    /**
     * @param ownersFiltrados the ownersFiltrados to set
     */
    public void setOwnersFiltrados(List<Owner> ownersFiltrados) {
        this.ownersFiltrados = ownersFiltrados;
    }

    /**
     * @return the ownerDataTable
     */
    public UIData getOwnerDataTable() {
        return ownerDataTable;
    }

    /**
     * @param ownerDataTable the ownerDataTable to set
     */
    public void setOwnerDataTable(UIData ownerDataTable) {
        this.ownerDataTable = ownerDataTable;
    }

    /**
     * @return the renewal
     */
    public RenewalForm getRenewal() {
        return renewal;
    }

    /**
     * @param renewal the renewal to set
     */
    public void setRenewal(RenewalForm renewal) {
        this.renewal = renewal;
    }

    /**
     * @return the owner
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * @return the cambioc
     */
    public boolean isCambioc() {
        return cambioc;
    }

    /**
     * @param cambioc the cambioc to set
     */
    public void setCambioc(boolean cambioc) {
        this.cambioc = cambioc;
    }

    /**
     * @return the confirmChangeMessage
     */
    public String getConfirmChangeMessage() {
        return confirmChangeMessage;
    }

    /**
     * @param confirmChangeMessage the confirmChangeMessage to set
     */
    public void setConfirmChangeMessage(String confirmChangeMessage) {
        this.confirmChangeMessage = confirmChangeMessage;
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
     * @return the cambioCasillero
     */
    public CambioCasillero getCambioCasillero() {
        return cambioCasillero;
    }

    /**
     * @param cambioCasillero the cambioCasillero to set
     */
    public void setCambioCasillero(CambioCasillero cambioCasillero) {
        this.cambioCasillero = cambioCasillero;
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
     * @return the tiposModificacion
     */
    public List<String> getTiposModificacion() {
        return tiposModificacion;
    }

    /**
     * @param tiposModificacion the tiposModificacion to set
     */
    public void setTiposModificacion(List<String> tiposModificacion) {
        this.tiposModificacion = tiposModificacion;
    }

    /**
     * @return the razon
     */
    public RazonCorreccion getRazon() {
        return razon;
    }

    /**
     * @param razon the razon to set
     */
    public void setRazon(RazonCorreccion razon) {
        this.razon = razon;
    }

    /**
     * @return the errorrazon
     */
    public String getErrorrazon() {
        return errorrazon;
    }

    /**
     * @param errorrazon the errorrazon to set
     */
    public void setErrorrazon(String errorrazon) {
        this.errorrazon = errorrazon;
    }
}
