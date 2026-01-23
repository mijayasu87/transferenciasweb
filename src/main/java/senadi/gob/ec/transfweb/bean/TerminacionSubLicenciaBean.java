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
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import senadi.gob.ec.transfweb.model.Desistimiento;
import senadi.gob.ec.transfweb.model.Documento;
import senadi.gob.ec.transfweb.model.Historial;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.model.TituloCancelado;
import senadi.gob.ec.transfweb.model.Transferencia;
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepform.PaymentReceipt;
import senadi.gob.ec.transfweb.model.iepform.Person;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepform.Types;
import senadi.gob.ec.transfweb.model.licencia.SubLicenciaUso;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author Michael
 */
@ManagedBean(name = "termSubLicenciaBean")
@ViewScoped
public class TerminacionSubLicenciaBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private List<SubLicenciaUso> terminaciones;
    private List<SubLicenciaUso> terminacionesFiltradas;

    private UIData terminacionDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private SubLicenciaUso terminacion;

    private String estadoTemp;
    private String historial;

    private LoginBean loginBean;

    private String exportName;

    private List<Documento> archivos;

    public TerminacionSubLicenciaBean() {
        loadTerminacionesSublicencias();
    }

    private void loadTerminacionesSublicencias() {
        Controlador c = new Controlador();
        terminaciones = c.getSublicenciasUsoByTipo("TERMINACION");
        numRegistros = "Número Registros Mostrados: " + terminaciones.size();
        exportName = "term_sublic_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
    }

    public void buscarTerminacionLicencia(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            terminaciones = c.getSublicenciasUsoByCriteriaAndType(criterio, "TERMINACION");

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
            if (c.existeSublicenciaUso(tramite)) {
                SubLicenciaUso aux = c.getSublicenciaUsoBySolicitud(tramite);
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
                                    if (rf.getLicenseType() != null && rf.getLicenseType().equals("SUBLICENSE")) {
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

                                        Person abog = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "LAWYER");
                                        if (abog.getId() != null) {
                                            terminacion.setAbogadoPatrocinador(abog.getName());
                                        }
                                        Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "ATTORNEY");
                                        if (apoder.getId() != null) {
                                            terminacion.setApoderadoRepresentante(apoder.getName());
                                            terminacion.setEmail(abog.getEmail());
                                        }

                                        Person sublic = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "APPLICANT");
                                        if (sublic.getId() != null) {
                                            terminacion.setSublicenciante(sublic.getName());
                                        }

                                        Person sublicenciat = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "BENEFICIARY");
                                        if (sublicenciat.getId() != null) {
                                            terminacion.setSublicenciatario(sublicenciat.getName());
                                        }

                                        if (terminacion.getRegistro() != null && !terminacion.getRegistro().trim().isEmpty()) {
                                            if (terminacion.getDenominacion() != null && !terminacion.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(terminacion.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(terminacion.getRegistro(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + terminacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            terminacion = new SubLicenciaUso();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            terminacion.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + terminacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + terminacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            terminacion = new SubLicenciaUso();
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
                                                            terminacion = new SubLicenciaUso();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            terminacion.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + terminacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN "
                                                                    + terminacion.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            terminacion = new SubLicenciaUso();
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
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE " + rf.getApplicationNumber() + " NO ES UNA SUBLICENCIA");
                                    }
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UNA SUBLICENCIA DE USO, SINO " + t.getName().toUpperCase());
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
            terminaciones = c.getSublicenciasUsoByFechaAndType(fechaInicio, fechaFin, "TERMINACION");
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

    public void eliminarTerminacionSublicencia(ActionEvent ae) {
        FacesMessage msg = null;
        terminacion = (SubLicenciaUso) terminacionDataTable.getRowData();
        if (terminacion != null) {
            Controlador c = new Controlador();
            if (c.removeSublicenciaUso(terminacion)) {
//                c.saveHistorial("RENOVACIÓN", "RENOVACIÓN", renovacion.getSolicitudSenadi(), "ELIMINADO", loginBean.getUsuario().getId(), loginBean.getUsuario().getLogin());
                loadTerminacionesSublicencias();
                System.out.println("Terminacion Sublicencia " + terminacion.getSolicitud() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TERMINACION SUBLICENCIA " + terminacion.getSolicitud() + "ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR TERMINACIÓN SUBLICENCIA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR TERMINACIÓN SUBLICENCIA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {

        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
        terminacion = (SubLicenciaUso) terminacionDataTable.getRowData();
        if (terminacion != null) {
            dialogTitle = "EDITAR TERMINACIÓN SUBLICENCIA " + terminacion.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar la Terminación Sublicencia: " + terminacion.getSolicitud() + "?";

            Controlador c = new Controlador();
            RenewalForm rf = c.getRenewalFormsByApplicationNumber(terminacion.getSolicitud());
            if (rf.getId() != null) {
                terminacion.setIdRenewalForm(rf.getId());
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TERMINACIÓN SUBLICENCIA CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR TERMINACIÓN SUBLICENCIA");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararNuevo(ActionEvent ae) {
        dialogTitle = "NUEVA TERMINACIÓN SUBLICENCIA";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar la nueva Terminación Sublicencia?";
        terminacion = new SubLicenciaUso();
        Controlador c = new Controlador();
        terminacion.setResolucionNo(c.getNextLicenciaUsoResolucionNoByTipo("TERMINACION"));
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

                    if (estadoTemp.equals("SUBLICENCIA")) {
                        terminacion.setSublicenciaNo(c.getNextSublicenciaUsoNo());
                        terminacion.setTipoEstado("SUBLICENCIA");
                    } else if (estadoTemp.equals("NOTIFICADA")) {
                        terminacion.setNotificacion(c.getNextNumeroNotificacionSublicenciaUso(new Date()));
                        terminacion.setTipoEstado("NOTIFICADA");
                    } else if (estadoTemp.equals("DESISTIDA")) {
                        terminacion.setResolucionNo(c.getNextSublicenciaUsoResolucionNoByTipo("DESISTIDA"));
                        terminacion.setTipoEstado("DESISTIDA");
                    } else if (estadoTemp.equals("CADUCADA")) {
                        terminacion.setResolucionNo(c.getNextSublicenciaUsoResolucionNoByTipo("CADUCADA"));
                        terminacion.setTipoEstado("CADUCADA");
                    }

                    if (c.updateSublicenciaUso(terminacion)) {
                        c.saveHistorial(terminacion.getTipoEstado() + "_SUBLIC", "TERMINACIÓN_SUBLIC", terminacion.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                        loadTerminacionesSublicencias();
                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TERMINACION_SUBLICENCIA EDITADA CON ÉXITO");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA TERMINACION_SUBLICENCIA");
                    }
                } else {
                    //Editar Desistimiento
                    if (c.validarExistenciaSublicenciaUso(terminacion)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                    } else {
                        terminacion.setSolicitud(terminacion.getSolicitud().toUpperCase());
                        if (c.updateSublicenciaUso(terminacion)) {
                            c.saveHistorial("TERMINACION_SUBLIC", "TERMINACION_SUBLIC", terminacion.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TERMINACIÓN SUBLICENCIA EDITADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA TERMINACIÓN SUBLICENCIA");
                        }
                    }
                }
            } else {
                //Guardar Terminación Sublicencia
                if (c.existeSublicenciaUso(terminacion.getSolicitud())) {
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
                                terminacion = new SubLicenciaUso();
                                habilitado = false;
                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                terminacion.setCancelado(titca.getTipoCancelacion());
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN '"
                                        + terminacion.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + terminacion.getRegistro() + " CON DENOMINACIÓN '"
                                        + terminacion.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                terminacion = new SubLicenciaUso();
                                habilitado = false;
                            }
                        } else {
                            habilitado = true;
                        }
                    }
                    if (habilitado) {
                        terminacion.setSolicitud(terminacion.getSolicitud().toUpperCase());
                        terminacion.setTipoEstado("TERMINACION");
                        if (c.saveSublicenciaUso(terminacion)) {
                            c.saveModificacionApp(terminacion.getDenominacion(), terminacion.getRegistro(), terminacion.getSolicitud(), "SUBLICENCIA DE USO", loginBean.getNombre());
                            c.saveHistorial("TERMINACION_SUBLIC", "TERMINACION_SUBLIC", terminacion.getSolicitud(), "NUEVA TERMINACIÓN SUBLICENCIA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            loadTerminacionesSublicencias();
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TERMINACION_SUBLIC GUARDADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL GUARDAR LA TERMINACION_SUBLIC");
                        }
                    }
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        terminacion = (SubLicenciaUso) terminacionDataTable.getRowData();
        if (terminacion != null) {
            dialogTitle = "SEGUIMIENTO " + terminacion.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(terminacion.getSolicitud());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: TERMINACIÓN SUBLICENCIA";
            }
        }
    }

    public void viewTerminacionSubLicencia(ActionEvent ae) {
        FacesMessage msg = null;
        terminacion = (SubLicenciaUso) terminacionDataTable.getRowData();

        if (terminacion != null) {
            System.out.println("Descargando Terminación Sublicencia: " + terminacion.getSolicitud());
//            System.out.println(notificacion.getFechaCertificado());

            loginBean.setTransferenciaFlotante(null);
            loginBean.setNotificacionFlotante(null);
//            loginBean.setDesistimientoFlotante(terminacion);

            loginBean.setTransferenciasFlotantes(new ArrayList<Transferencia>());
            loginBean.setNotificacionesFlotantes(new ArrayList<Notificacion>());
            loginBean.setDesistimientosFlotantes(new ArrayList<Desistimiento>());
            loginBean.setVarious(false);

            PrimeFaces.current().ajax().addCallbackParam("doit", true);

            System.out.println("envía terminación sublicencia descargar");
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CARGANDO REPORTE");

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "VALIDE QUE LOS DATOS DE LA TERMINACIÓN SUBLICENCIA SEAN CORRECTOS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
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
    public List<SubLicenciaUso> getTerminaciones() {
        return terminaciones;
    }

    /**
     * @param terminaciones the terminaciones to set
     */
    public void setTerminaciones(List<SubLicenciaUso> terminaciones) {
        this.terminaciones = terminaciones;
    }

    /**
     * @return the terminacionesFiltradas
     */
    public List<SubLicenciaUso> getTerminacionesFiltradas() {
        return terminacionesFiltradas;
    }

    /**
     * @param terminacionesFiltradas the terminacionesFiltradas to set
     */
    public void setTerminacionesFiltradas(List<SubLicenciaUso> terminacionesFiltradas) {
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
    public SubLicenciaUso getTerminacion() {
        return terminacion;
    }

    /**
     * @param terminacion the terminacion to set
     */
    public void setTerminacion(SubLicenciaUso terminacion) {
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
}
