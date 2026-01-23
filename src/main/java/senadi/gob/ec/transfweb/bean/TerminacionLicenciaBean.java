/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.PrimeFaces;
import org.primefaces.component.api.UIData;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import senadi.gob.ec.transfweb.model.Documento;
import senadi.gob.ec.transfweb.model.Historial;
import senadi.gob.ec.transfweb.model.TituloCancelado;
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepform.PaymentReceipt;
import senadi.gob.ec.transfweb.model.iepform.Person;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepform.Types;
import senadi.gob.ec.transfweb.model.licencia.LicenciaUso;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author Michael
 */
@ManagedBean(name = "terminacionLicenciaBean")
@ViewScoped
public class TerminacionLicenciaBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private List<LicenciaUso> terminaciones;
    private List<LicenciaUso> terminacionesFiltradas;

    private UIData terminacionDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private LicenciaUso terminacion;

    private String estadoTemp;
    private String historial;

    private LoginBean loginBean;

    private String exportName;

    private List<Documento> archivos;

    private List<LicenciaUso> selectedLicencias;

    public TerminacionLicenciaBean() {
        loadTerminacionesLicencias();
    }

    private void loadTerminacionesLicencias() {
        Controlador c = new Controlador();
        terminaciones = c.getTerminacionesLicencia();
        numRegistros = "Número Registros Mostrados: " + terminaciones.size();
        exportName = "terminacion_lic_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
    }

    public void buscarTerminacionLicencia(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            terminaciones = c.getLicenciasUsoByCriteriaAndType(criterio, "TERMINACION");

            numRegistros = "Número Registros Mostrados: " + terminaciones.size();
            if (terminaciones.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;

        if (terminacion != null && terminacion.getSolicitud() != null && !terminacion.getSolicitud().trim().isEmpty()) {
//            System.out.println(transferencia.getSolicitud());
            String tramite = terminacion.getSolicitud();
            Controlador c = new Controlador();
            if (c.existeLicenciaUso(tramite)) {
                LicenciaUso aux = c.getLicenciaUsoBySolicitud(tramite);
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL TRÁMITE YA SE ENCUENTRA REGISTRADO EN LA PESTAÑA DE " + aux.getTipoEstado());
                terminacion.setSolicitud("");
            } else {

                RenewalForm rf = c.getRenewalFormsByApplicationNumber(tramite);

                if (rf.getId() != null) {
                    if (rf.getStatus().equals("DELIVERED")) {

                        Types t = c.getTypes(rf.getTransactionMotiveId());

                        Types ttp = c.getTypes(rf.getFormId());

                        if (t.getId() != null && ttp.getId() != null) {
                            if (!ttp.getAlias().equals("PI") && !ttp.getAlias().equals("MU") && !ttp.getAlias().equals("DI")) {
                                if (t.getName().trim().toLowerCase().contains("licencia de uso")) {
                                    if (rf.getLicenseType() == null || !rf.getLicenseType().equals("SUBLICENSE")) {
                                        PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                        terminacion.setComprobante(payment.getVoucherNumber());
                                        terminacion.setFechaPresentacion(rf.getApplicationDate());
//                                terminacion.setCertificado(c.getNextNumeroCertificadoTransferencia() + "");
                                        terminacion.setSigno(ttp.getAlias());
                                        terminacion.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");

                                        terminacion.setIdRenewalForm(rf.getId());
                                        if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                            HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                            if (hf.getId() != null) {
                                                terminacion.setDenominacion(hf.getDenomination());
                                                terminacion.setRegistro(hf.getExpedient());

                                                if (terminacion.getRegistro() != null && !terminacion.getRegistro().trim().isEmpty()) {
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(terminacion.getRegistro(), terminacion.getDenominacion());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        terminacion.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                    }
                                                }
                                                if (terminacion.getFechaRegistro() == null) {
                                                    terminacion.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                }
                                            }
                                        } else {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                if (ps.getCodigoSolicitudSigno() != null) {
                                                    terminacion.setDenominacion(ps.getDenominacionSigno());
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        terminacion.setRegistro(titulo.getNumeroTitulo());
                                                        terminacion.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                    }
                                                }
                                            }
                                        }

                                        Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "AGENT");
                                        if (apoder.getId() != null) {
                                            terminacion.setApoderadoRepresentante(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "AGENT"));
                                        }

                                        if (terminacion.getRegistro() != null && !terminacion.getRegistro().trim().isEmpty()) {
                                            if (terminacion.getDenominacion() != null && !terminacion.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(terminacion.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(terminacion.getRegistro(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + terminacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            terminacion = new LicenciaUso();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            terminacion.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + terminacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + terminacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            terminacion = new LicenciaUso();
                                                        }
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(terminacion.getRegistro(), terminacion.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(terminacion.getRegistro(), terminacion.getDenominacion());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + terminacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            terminacion = new LicenciaUso();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            terminacion.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + terminacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + terminacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + ";  CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            terminacion = new LicenciaUso();
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
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE " + rf.getApplicationNumber() + " ES UNA SUBLICENCIA");
                                    }
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UNA LICENCIA DE USO, SINO " + t.getName().toUpperCase());
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
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN TRÁMITE VÁLIDO");
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

    public void buscarTerminacionesPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            terminaciones = c.getLicenciasUsoByFechaAndType(fechaInicio, fechaFin, "TERMINACION");
            numRegistros = "Número Registros Mostrados: " + terminaciones.size();
            if (terminaciones.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarTerminacionLicencia(ActionEvent ae) {
        FacesMessage msg = null;
        terminacion = (LicenciaUso) terminacionDataTable.getRowData();
        if (terminacion != null) {
            Controlador c = new Controlador();
            if (c.removeLicenciaUso(terminacion)) {
//                c.saveHistorial("RENOVACIÓN", "RENOVACIÓN", renovacion.getSolicitudSenadi(), "ELIMINADO", loginBean.getUsuario().getId(), loginBean.getUsuario().getLogin());
                loadTerminacionesLicencias();
                System.out.println("Terminacion licencia " + terminacion.getSolicitud() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TERMINACION LICENCIA " + terminacion.getSolicitud() + "ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR TERMINACIÓN LICENCIA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR TERMINACIÓN LICENCIA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {

        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
        terminacion = (LicenciaUso) terminacionDataTable.getRowData();
        if (terminacion != null) {
            dialogTitle = "EDITAR TERMINACIÓN LICENCIA " + terminacion.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar la Terminación Licencia: " + terminacion.getSolicitud() + "?";

            Controlador c = new Controlador();
            RenewalForm rf = c.getRenewalFormsByApplicationNumber(terminacion.getSolicitud());
            if (rf.getId() != null) {
                terminacion.setIdRenewalForm(rf.getId());
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TERMINACIÓN LICENCIA CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR TERMINACIÓN LICENCIA");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararNuevo(ActionEvent ae) {
        dialogTitle = "NUEVA TERMINACIÓN LICENCIA";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar la nueva Terminación Licencia?";
        terminacion = new LicenciaUso();
        Controlador c = new Controlador();
        terminacion.setTipoEstado("TERMINACION");
        terminacion.setResponsable(loginBean.getUsuario().getAlias());
        edicion = false;
        if (terminacion != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void guardarTerminacionLicencia(ActionEvent ae) {
        FacesMessage msg = null;
        if (terminacion != null) {
            Controlador c = new Controlador();
            if (terminacion.getId() != null) {
                if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {

                    if (estadoTemp.equals("LICENCIA")) {
                        terminacion.setLicenciaNo(c.getNextLicenciaUsoNo());
                        terminacion.setFechaLicencia(Operaciones.cambiarFechaADiaDado(new Date(), 5));
                        terminacion.setTipoEstado("LICENCIA");
                    } else if (estadoTemp.equals("NOTIFICADA")) {
                        terminacion.setNotificacion(c.getNextNumeroNotificacionLicenciaUso(new Date()));
                        terminacion.setTipoEstado("NOTIFICADA");
                    } else if (estadoTemp.equals("DESISTIDA")) {
                        terminacion.setResolucionNo(c.getNextLicenciaUsoResolucionNoByTipo("DESISTIDA"));
                        terminacion.setTipoEstado("DESISTIDA");
                    } else if (estadoTemp.equals("CADUCADA")) {
                        terminacion.setResolucionNo(c.getNextLicenciaUsoResolucionNoByTipo("CADUCADA"));
                        terminacion.setTipoEstado("CADUCADA");
                    }

                    if (c.updateLicenciaUso(terminacion)) {
                        c.saveHistorial(terminacion.getTipoEstado() + "_LIC", "TERMINACIÓN_LIC", terminacion.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                        loadTerminacionesLicencias();
                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TERMINACION_LICENCIA EDITADA CON ÉXITO");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA TERMINACION_LICENCIA");
                    }
                } else {
                    //Editar Desistimiento
                    if (c.validarExistenciaLicenciaUso(terminacion)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                    } else {
                        terminacion.setSolicitud(terminacion.getSolicitud().toUpperCase());
                        if (c.updateLicenciaUso(terminacion)) {
                            c.saveHistorial("TERMINACION_LIC", "TERMINACION_LIC", terminacion.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TERMINACIÓN LICENCIA EDITADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA TERMINACIÓN LICENCIA");
                        }
                    }
                }
            } else {
                //Guardar Terminación Licencia
                if (c.existeLicenciaUso(terminacion.getSolicitud())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                } else {
                    boolean habilitado = true;
                    if (terminacion.getDenominacion() != null && !terminacion.getDenominacion().trim().isEmpty()
                            && terminacion.getRegistro() != null && !terminacion.getRegistro().trim().isEmpty()) {
                        if (c.existsTituloCanceladoByTituloAndDenominacion(terminacion.getRegistro(), terminacion.getDenominacion(), false)) {
                            TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(terminacion.getRegistro(), terminacion.getDenominacion());
                            if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN '"
                                        + terminacion.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                terminacion = new LicenciaUso();
                                habilitado = false;
                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                terminacion.setCancelado(titca.getTipoCancelacion());
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN '"
                                        + terminacion.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN '"
                                        + terminacion.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                terminacion = new LicenciaUso();
                                habilitado = false;
                            }
                        } else {
                            habilitado = true;
                        }
                    }
                    if (habilitado) {
                        terminacion.setTerminacionNo(c.getNextLicenciaTerminacionNo(new Date()));                        
                        terminacion.setSolicitud(terminacion.getSolicitud().toUpperCase());
                        terminacion.setTipoEstado("TERMINACION");
                        if (c.saveLicenciaUso(terminacion)) {
                            c.saveModificacionApp(terminacion.getDenominacion(), terminacion.getRegistro(), terminacion.getSolicitud(), "LICENCIA DE USO", loginBean.getNombre());
                            c.saveHistorial("TERMINACION_LIC", "TERMINACION_LIC", terminacion.getSolicitud(), "NUEVA TERMINACIÓN LICENCIA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            loadTerminacionesLicencias();
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TERMINACION_LIC GUARDADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL GUARDAR LA TERMINACION_LIC");
                        }
                    }
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        terminacion = (LicenciaUso) terminacionDataTable.getRowData();
        if (terminacion != null) {
            dialogTitle = "SEGUIMIENTO " + terminacion.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(terminacion.getSolicitud());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: TERMINACIÓN LICENCIA";
            }
        }
    }

    public void viewTerminacionLicencia(ActionEvent ae) {
        FacesMessage msg = null;
        terminacion = (LicenciaUso) terminacionDataTable.getRowData();
//        boolean correcto = false;
        if (terminacion != null) {

            if (terminacion.getRegistro() != null && !terminacion.getRegistro().trim().isEmpty()) {
                Controlador c = new Controlador();
                if (!c.validarDelegadoActivo("delegado")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarDelegacionActivo()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarResolucionActiva("transferencia")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!Operaciones.validarFecha(terminacion.getFechaVenceContrato())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE " + terminacion.getSolicitud() + " NO POSEE UNA FECHA DE VENCIMIENTO DE CONTRATO VÁLIDA");
                } else if (terminacion.getApoderadoRepresentante() == null || terminacion.getApoderadoRepresentante().trim().isEmpty()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "INGRESE UN APODERADO VÁLIDO PARA EL TRÁMITE " + terminacion.getSolicitud());
                } else if (!Operaciones.validarFecha(terminacion.getFechaNotiProvReg())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO HAY UNA FECHA DE NOTIFICACIÓN DE LICENCIA VÁLIDA");
                } else if (!Operaciones.validarFecha(terminacion.getFechaTerminacion())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE " + terminacion.getSolicitud() + " NO POSEE UNA FECHA DE RESOLUCIÓN VÁLIDA");
                } else {
                    if (terminacion.getRegistro() != null && !terminacion.getRegistro().trim().isEmpty()) {
                        if (terminacion.getFechaContrato() != null && validarFechaContrato(terminacion.getFechaContrato())) {
                            if (terminacion.getResponsable() != null && !terminacion.getResponsable().trim().isEmpty()) {
                                if (terminacion.getCasilleroSenadi() != null && !terminacion.getCasilleroSenadi().trim().isEmpty() && !terminacion.getCasilleroSenadi().equals("null")) {
                                    if (terminacion.getLicenciante() != null && !terminacion.getLicenciante().trim().isEmpty() && !terminacion.getLicenciante().equals("null")) {
                                        if ((terminacion.getCasilleroSenadiLicenciatario() != null && !terminacion.getCasilleroSenadiLicenciatario().trim().isEmpty())
                                                || (terminacion.getEmail() != null && !terminacion.getEmail().trim().isEmpty())) {
                                            System.out.println("Descargando Terminación_Licencia: " + terminacion.getSolicitud());
//                                            correcto = true;
                                            loginBean.setVarious(false);
                                            loginBean.setLicencia(terminacion);

                                            PrimeFaces.current().ajax().addCallbackParam("doit", true);

                                            System.out.println("envía certificado descargar");
                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO DOCUMENTO " + terminacion.getSolicitud());
                                        } else {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + terminacion.getSolicitud() + " DEBE TENER UN CASILLERO LICENCIATARIO O UN CORREO");
                                        }
                                    } else {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + terminacion.getSolicitud() + " NO TIENE LICENCIANTE");
                                    }
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + terminacion.getSolicitud() + " NO TIENE CASILLERO");
                                }
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + terminacion.getSolicitud() + " NO TIENE RESPONSABLE ASIGNADO");
                            }
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + terminacion.getSolicitud() + " NO TIENE UNA FECHA DE CONTRATO VÁLIDA");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + terminacion.getSolicitud() + " NO POSEE NÚMERO DE REGISTRO");
                    }
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL REGISTRO SELECCIONADO NO TIENE NÚMERO DE REGISTRO ASIGNADO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR LOS DATOS DEL REGISTRO SELECCIONADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public boolean validarFechaContrato(Date fechacontrato) {
        if (fechacontrato.getYear() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (terminacion != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + terminacion.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(terminacion.getIdRenewalForm(), terminacion.getSolicitud());
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

    public void downloadSelected(ActionEvent ae) {
        System.out.println("que onda");
        FacesMessage msg = null;

        if (!selectedLicencias.isEmpty()) {
            System.out.println("Descargando Múltiples terminación licencia...");

            String mensaje = "";
            boolean flag = true;
            Controlador c = new Controlador();
            if (!c.validarDelegadoActivo("delegado")) {
                mensaje = "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN";
                flag = false;
            } else if (!c.validarDelegacionActivo()) {
                mensaje = "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN";
                flag = false;
            } else if (!c.validarResolucionActiva("transferencia")) {
                mensaje = "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN";
                flag = false;
            }

            if (flag) {
                for (int i = 0; i < selectedLicencias.size(); i++) {
                    LicenciaUso termaux = selectedLicencias.get(i);
                    if (termaux.getRegistro() != null && !termaux.getRegistro().trim().isEmpty()) {

                        if (!Operaciones.validarFecha(termaux.getFechaVenceContrato())) {
                            mensaje = "EL TRÁMITE " + termaux.getSolicitud() + " NO POSEE UNA FECHA DE VENCIMIENTO DE CONTRATO VÁLIDA";
                            flag = false;
                            break;
                        } else if (termaux.getApoderadoRepresentante() == null || termaux.getApoderadoRepresentante().trim().isEmpty()) {
                            mensaje = "INGRESE UN APODERADO VÁLIDO PARA EL TRÁMITE " + termaux.getSolicitud();
                            flag = false;
                            break;
                        } else if (!Operaciones.validarFecha(termaux.getFechaNotiProvReg())) {
                            mensaje = "NO HAY UNA FECHA DE NOTIFICACIÓN DE LICENCIA VÁLIDA PARA EL TRÁMITE " + termaux.getSolicitud();
                            flag = false;
                            break;
                        } else if (!Operaciones.validarFecha(termaux.getFechaTerminacion())) {
                            mensaje = "NO HAY UNA FECHA DE RESOLUCIÓN VÁLIDA PARA EL TRÁMITE "+termaux.getSolicitud();
                            flag = false;
                            break;
                        } else {
                            if (termaux.getRegistro() != null && !termaux.getRegistro().trim().isEmpty()) {
                                if (termaux.getFechaContrato() != null && validarFechaContrato(termaux.getFechaContrato())) {
                                    if (termaux.getResponsable() != null && !termaux.getResponsable().trim().isEmpty()) {
                                        if (termaux.getCasilleroSenadi() != null && !termaux.getCasilleroSenadi().trim().isEmpty() && !termaux.getCasilleroSenadi().equals("null")) {
                                            if (termaux.getLicenciante() != null && !termaux.getLicenciante().trim().isEmpty() && !termaux.getLicenciante().equals("null")) {
                                                if ((termaux.getCasilleroSenadiLicenciatario() != null && !termaux.getCasilleroSenadiLicenciatario().trim().isEmpty())
                                                        || (termaux.getEmail() != null && !termaux.getEmail().trim().isEmpty())) {
                                                    flag = true;
                                                } else {
                                                    mensaje = "EL REGISTRO " + termaux.getSolicitud() + " DEBE TENER UN CASILLERO LICENCIATARIO O UN CORREO";
                                                    flag = false;
                                                    break;
                                                }
                                            } else {
                                                mensaje = "EL REGISTRO " + termaux.getSolicitud() + " NO TIENE LICENCIANTE";
                                                flag = false;
                                                break;
                                            }
                                        } else {
                                            mensaje = "EL REGISTRO " + termaux.getSolicitud() + " NO TIENE CASILLERO";
                                            flag = false;
                                            break;
                                        }
                                    } else {
                                        mensaje = "EL REGISTRO " + termaux.getSolicitud() + " NO TIENE RESPONSABLE ASIGNADO";
                                        flag = false;
                                        break;
                                    }
                                } else {
                                    mensaje = "EL REGISTRO " + termaux.getSolicitud() + " NO TIENE UNA FECHA DE CONTRATO VÁLIDA";
                                    flag = false;
                                    break;
                                }
                            } else {
                                mensaje = "EL REGISTRO " + termaux.getSolicitud() + " NO POSEE NÚMERO DE REGISTRO";
                                flag = false;
                                break;
                            }
                        }
                    } else {
                        mensaje = "EL REGISTRO SELECCIONADO NO TIENE NÚMERO DE REGISTRO ASIGNADO";
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) {
                System.out.println("Descargando " + selectedLicencias.size() + " terminaciones_licencia");
//                                            correcto = true;
                loginBean.setVarious(true);
                loginBean.setLicencias(selectedLicencias);

                PrimeFaces.current().ajax().addCallbackParam("doit", true);

                System.out.println("envía certificado descargar");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO TERMINACIÓN LICENCIAS DE USO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN REGISTRO", mensaje);
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
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
     * @return the terminaciones
     */
    public List<LicenciaUso> getTerminaciones() {
        return terminaciones;
    }

    /**
     * @param terminaciones the terminaciones to set
     */
    public void setTerminaciones(List<LicenciaUso> terminaciones) {
        this.terminaciones = terminaciones;
    }

    /**
     * @return the terminacionesFiltradas
     */
    public List<LicenciaUso> getTerminacionesFiltradas() {
        return terminacionesFiltradas;
    }

    /**
     * @param terminacionesFiltradas the terminacionesFiltradas to set
     */
    public void setTerminacionesFiltradas(List<LicenciaUso> terminacionesFiltradas) {
        this.terminacionesFiltradas = terminacionesFiltradas;
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
     * @return the terminacionDataTable
     */
    public UIData getTerminacionDataTable() {
        return terminacionDataTable;
    }

    /**
     * @param terminacionDataTable the terminacionDataTable to set
     */
    public void setTerminacionDataTable(UIData terminacionDataTable) {
        this.terminacionDataTable = terminacionDataTable;
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
     * @return the terminacion
     */
    public LicenciaUso getTerminacion() {
        return terminacion;
    }

    /**
     * @param terminacion the terminacion to set
     */
    public void setTerminacion(LicenciaUso terminacion) {
        this.terminacion = terminacion;
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
     * @return the selectedLicencias
     */
    public List<LicenciaUso> getSelectedLicencias() {
        return selectedLicencias;
    }

    /**
     * @param selectedLicencias the selectedLicencias to set
     */
    public void setSelectedLicencias(List<LicenciaUso> selectedLicencias) {
        this.selectedLicencias = selectedLicencias;
    }
}
