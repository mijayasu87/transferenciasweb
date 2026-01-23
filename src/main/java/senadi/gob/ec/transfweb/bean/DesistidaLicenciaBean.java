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
import senadi.gob.ec.transfweb.model.licencia.LicenciaUso;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author Michael
 */
@ManagedBean(name = "desistidaLicenciaBean")
@ViewScoped
public class DesistidaLicenciaBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private List<LicenciaUso> desistimientos;
    private List<LicenciaUso> desistimientosFiltradas;

    private UIData desistimientoDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private LicenciaUso desistimiento;

    private String estadoTemp;
    private String historial;

    private LoginBean loginBean;

    private String exportName;

    private List<Documento> archivos;

    public DesistidaLicenciaBean() {
        loadDesistimientosLicencia();
    }

    private void loadDesistimientosLicencia() {
        Controlador c = new Controlador();
        desistimientos = c.getLicenciasUsoByTipo("DESISTIDA");
        numRegistros = "Número Registros Mostrados: " + desistimientos.size();
        exportName = "desistimiento_lic_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
    }

    public void buscarDesistimiento(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            desistimientos = c.getLicenciasUsoByCriteriaAndType(criterio, "DESISTIDA");

            numRegistros = "Número Registros Mostrados: " + desistimientos.size();
            if (desistimientos.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;

        if (desistimiento != null && desistimiento.getSolicitud() != null && !desistimiento.getSolicitud().trim().isEmpty()) {
//            System.out.println(transferencia.getSolicitud());
            String tramite = desistimiento.getSolicitud();
            Controlador c = new Controlador();
            if (c.existeLicenciaUso(tramite)) {
                LicenciaUso aux = c.getLicenciaUsoBySolicitud(tramite);
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL TRÁMITE YA SE ENCUENTRA REGISTRADO EN LA PESTAÑA DE " + aux.getTipoEstado());
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

                                        desistimiento.setComprobante(payment.getVoucherNumber());
                                        desistimiento.setFechaPresentacion(rf.getApplicationDate());
//                                desistimiento.setCertificado(c.getNextNumeroCertificadoTransferencia() + "");
                                        desistimiento.setSigno(ttp.getAlias());
                                        desistimiento.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");

                                        desistimiento.setIdRenewalForm(rf.getId());
                                        if (rf.getDebugId() != null && rf.getDebugId() != 0) {
                                            HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                            if (hf.getId() != null) {
                                                desistimiento.setDenominacion(hf.getDenomination());
                                                desistimiento.setRegistro(hf.getExpedient());

                                                if (desistimiento.getRegistro() != null && !desistimiento.getRegistro().trim().isEmpty()) {
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(desistimiento.getRegistro(), desistimiento.getDenominacion());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        desistimiento.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                    }
                                                }
                                                if (desistimiento.getFechaRegistro() == null) {
                                                    desistimiento.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                }
                                            }
                                        } else {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                if (ps.getCodigoSolicitudSigno() != null) {
                                                    desistimiento.setDenominacion(ps.getDenominacionSigno());
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        desistimiento.setRegistro(titulo.getNumeroTitulo());
                                                        desistimiento.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                    }
                                                }
                                            }
                                        }

                                        Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "AGENT");
                                        if (apoder.getId() != null) {
                                            desistimiento.setApoderadoRepresentante(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "AGENT"));
                                        }

                                        if (desistimiento.getRegistro() != null && !desistimiento.getRegistro().trim().isEmpty()) {
                                            if (desistimiento.getDenominacion() != null && !desistimiento.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(desistimiento.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(desistimiento.getRegistro(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getRegistro() + " CON DENOMINACIÓN "
                                                                    + desistimiento.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            desistimiento = new LicenciaUso();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            desistimiento.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getRegistro() + " CON DENOMINACIÓN "
                                                                    + desistimiento.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getRegistro() + " CON DENOMINACIÓN "
                                                                    + desistimiento.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            desistimiento = new LicenciaUso();
                                                        }
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(desistimiento.getRegistro(), desistimiento.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(desistimiento.getRegistro(), desistimiento.getDenominacion());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getRegistro() + " CON DENOMINACIÓN "
                                                                    + desistimiento.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            desistimiento = new LicenciaUso();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            desistimiento.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getRegistro() + " CON DENOMINACIÓN "
                                                                    + desistimiento.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getRegistro() + " CON DENOMINACIÓN "
                                                                    + desistimiento.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            desistimiento = new LicenciaUso();
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

    public void buscarDesistimientosPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            desistimientos = c.getLicenciasUsoByFechaAndType(fechaInicio, fechaFin, "DESISTIDA");
            numRegistros = "Número Registros Mostrados: " + desistimientos.size();
            if (desistimientos.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarDesistimiento(ActionEvent ae) {
        FacesMessage msg = null;
        desistimiento = (LicenciaUso) desistimientoDataTable.getRowData();
        if (desistimiento != null) {
            Controlador c = new Controlador();
            if (c.removeLicenciaUso(desistimiento)) {
                c.saveHistorial("DESITIDA", "DESISTIDA", desistimiento.getSolicitud(), "ELIMINADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                loadDesistimientosLicencia();
                System.out.println("Desistimiento " + desistimiento.getSolicitud() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESISTIMIENTO " + desistimiento.getSolicitud() + "ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR DESISTIMIENTO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR DESISTIMIENTO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {

        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
        desistimiento = (LicenciaUso) desistimientoDataTable.getRowData();
        if (desistimiento != null) {
            dialogTitle = "EDITAR DESISTIMIENTO " + desistimiento.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar la Desistimiento: " + desistimiento.getSolicitud() + "?";

            Controlador c = new Controlador();
            RenewalForm rf = c.getRenewalFormsByApplicationNumber(desistimiento.getSolicitud());
            if (rf.getId() != null) {
                desistimiento.setIdRenewalForm(rf.getId());
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESISTIMIENTO CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR DESISTIMIENTO");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararNuevo(ActionEvent ae) {
        dialogTitle = "NUEVO DESISTIMIENTO LICENCIA";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar el Nuevo Desistimiento Licencia?";
        desistimiento = new LicenciaUso();
        Controlador c = new Controlador();
        desistimiento.setResolucionNo(c.getNextLicenciaUsoResolucionNoByTipo("DESISTIDA"));
        desistimiento.setTipoEstado("DESISTIDA");
        desistimiento.setResponsable(loginBean.getUsuario().getAlias());
        edicion = false;
        if (desistimiento != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void guardarDesistimiento(ActionEvent ae) {
        FacesMessage msg = null;
        if (desistimiento != null) {
            Controlador c = new Controlador();
            if (desistimiento.getId() != null) {
                if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {

                    if (estadoTemp.equals("LICENCIA")) {
                        desistimiento.setTipoEstado("LICENCIA");
                        desistimiento.setLicenciaNo(c.getNextLicenciaUsoNo());
                        desistimiento.setFechaLicencia(Operaciones.cambiarFechaADiaDado(new Date(), 5));
                    } else if (estadoTemp.equals("NOTIFICADA")) {
                        desistimiento.setNotificacion(c.getNextNumeroNotificacionLicenciaUso(new Date()));
                        desistimiento.setTipoEstado("NOTIFICADA");
                    } else if (estadoTemp.equals("TERMINACION")) {
                        desistimiento.setTerminacionNo(c.getNextLicenciaTerminacionNo(new Date()));
                        desistimiento.setFechaTerminacion(new Date());
                        desistimiento.setTipoEstado("TERMINACION");
                    } else if (estadoTemp.equals("CADUCADA")) {
                        desistimiento.setResolucionNo(c.getNextLicenciaUsoResolucionNoByTipo("CADUCADA"));
                        desistimiento.setTipoEstado("CADUCADA");
                    }

                    if (c.updateLicenciaUso(desistimiento)) {
                        c.saveHistorial(desistimiento.getTipoEstado() + "_LICENCIA", "DESISTIDA_LICENCIA", desistimiento.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                        loadDesistimientosLicencia();
                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESISTIDA_LICENCIA EDITADA CON ÉXITO");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA DESISTIDA_LICENCIA");
                    }
                } else {
                    //Editar Desistimiento
                    if (c.validarExistenciaLicenciaUso(desistimiento)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                    } else {
                        desistimiento.setSolicitud(desistimiento.getSolicitud().toUpperCase());
                        if (c.updateLicenciaUso(desistimiento)) {
                            c.saveHistorial("DESISTIDA_LICENCIA", "DESISTIDA_LICENCIA", desistimiento.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESISTIMIENTO EDITADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR EL DESISTIMIENTO");
                        }
                    }
                }
            } else {
                //Guardar Desistimiento
                if (c.existeLicenciaUso(desistimiento.getSolicitud())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                } else {
                    boolean habilitado = true;
                    if (desistimiento.getDenominacion() != null && !desistimiento.getDenominacion().trim().isEmpty()
                            && desistimiento.getRegistro() != null && !desistimiento.getRegistro().trim().isEmpty()) {
                        if (c.existsTituloCanceladoByTituloAndDenominacion(desistimiento.getRegistro(), desistimiento.getDenominacion(), false)) {
                            TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(desistimiento.getRegistro(), desistimiento.getDenominacion());
                            if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getRegistro() + " CON DENOMINACIÓN '"
                                        + desistimiento.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                desistimiento = new LicenciaUso();
                                habilitado = false;
                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                desistimiento.setCancelado(titca.getTipoCancelacion());
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getRegistro() + " CON DENOMINACIÓN '"
                                        + desistimiento.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getRegistro() + " CON DENOMINACIÓN '"
                                        + desistimiento.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                desistimiento = new LicenciaUso();
                                habilitado = false;
                            }
                        } else {
                            habilitado = true;
                        }
                    }
                    if (habilitado) {
                        desistimiento.setSolicitud(desistimiento.getSolicitud().toUpperCase());
                        desistimiento.setTipoEstado("DESISTIDA");
                        if (c.saveLicenciaUso(desistimiento)) {
                            c.saveModificacionApp(desistimiento.getDenominacion(), desistimiento.getRegistro(), desistimiento.getSolicitud(), "LICENCIA DE USO", loginBean.getNombre());
                            c.saveHistorial("DESISTIDA_CN", "DESISTIDA_CN", desistimiento.getSolicitud(), "NUEVO DESISTIMIENTO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            loadDesistimientosLicencia();
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESISTIDA_LICENCIA GUARDADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL GUARDAR LA DESISTIDA_LICENCIA");
                        }
                    }

                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        desistimiento = (LicenciaUso) desistimientoDataTable.getRowData();
        if (desistimiento != null) {
            dialogTitle = "SEGUIMIENTO " + desistimiento.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(desistimiento.getSolicitud());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: DESISTIDA_LICENCIA";
            }
        }
    }

    public void viewDesistimiento(ActionEvent ae) {
        FacesMessage msg = null;
        desistimiento = (LicenciaUso) desistimientoDataTable.getRowData();

        if (desistimiento != null) {
            System.out.println("Descargando Desistimiento Licencia: " + desistimiento.getSolicitud());
//            System.out.println(notificacion.getFechaCertificado());

            loginBean.setTransferenciaFlotante(null);
            loginBean.setNotificacionFlotante(null);
//            loginBean.setDesistimientoFlotante(desistimiento);

            loginBean.setTransferenciasFlotantes(new ArrayList<Transferencia>());
            loginBean.setNotificacionesFlotantes(new ArrayList<Notificacion>());
            loginBean.setDesistimientosFlotantes(new ArrayList<Desistimiento>());
            loginBean.setVarious(false);

            PrimeFaces.current().ajax().addCallbackParam("doit", true);

            System.out.println("envía desistimiento descargar");
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CARGANDO REPORTE");

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "VALIDE QUE LOS DATOS DEL DESISTIMIENTO SEAN CORRECTOS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (desistimiento != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + desistimiento.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(desistimiento.getIdRenewalForm(), desistimiento.getSolicitud());
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
     * @return the desistimientos
     */
    public List<LicenciaUso> getDesistimientos() {
        return desistimientos;
    }

    /**
     * @param desistimientos the desistimientos to set
     */
    public void setDesistimientos(List<LicenciaUso> desistimientos) {
        this.desistimientos = desistimientos;
    }

    /**
     * @return the desistimientosFiltradas
     */
    public List<LicenciaUso> getDesistimientosFiltradas() {
        return desistimientosFiltradas;
    }

    /**
     * @param desistimientosFiltradas the desistimientosFiltradas to set
     */
    public void setDesistimientosFiltradas(List<LicenciaUso> desistimientosFiltradas) {
        this.desistimientosFiltradas = desistimientosFiltradas;
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
     * @return the desistimientoDataTable
     */
    public UIData getDesistimientoDataTable() {
        return desistimientoDataTable;
    }

    /**
     * @param desistimientoDataTable the desistimientoDataTable to set
     */
    public void setDesistimientoDataTable(UIData desistimientoDataTable) {
        this.desistimientoDataTable = desistimientoDataTable;
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
     * @return the desistimiento
     */
    public LicenciaUso getDesistimiento() {
        return desistimiento;
    }

    /**
     * @param desistimiento the desistimiento to set
     */
    public void setDesistimiento(LicenciaUso desistimiento) {
        this.desistimiento = desistimiento;
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
