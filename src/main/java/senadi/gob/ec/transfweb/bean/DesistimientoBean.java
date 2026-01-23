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
import senadi.gob.ec.transfweb.modelp.PpdiPersona;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import senadi.gob.ec.transfweb.model.Caducada;
import senadi.gob.ec.transfweb.model.Desistimiento;
import senadi.gob.ec.transfweb.model.Documento;
import senadi.gob.ec.transfweb.model.Historial;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.model.TituloCancelado;
import senadi.gob.ec.transfweb.model.Transferencia;
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
@ManagedBean(name = "desistimientoBean")
@ViewScoped
public class DesistimientoBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private List<Desistimiento> desistimientos;
    private List<Desistimiento> desistimientosFiltradas;

    private UIData desistimientoDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private Desistimiento desistimiento;

    private String estadoTemp;
    private String historial;

    private LoginBean loginBean;

    private String exportName;

    private List<Documento> archivos;

    public DesistimientoBean() {
        loadDesistimientos();
    }

    private void loadDesistimientos() {
        Controlador c = new Controlador();
        desistimientos = c.getDesistimientos();
        numRegistros = "Número Registros Mostrados: " + desistimientos.size();
        exportName = "desistimiento_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
    }

    public void buscarDesistimiento(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            desistimientos = c.getDesistimientosByCriteria(criterio);
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

                                        desistimiento.setComprobante(payment.getVoucherNumber());
                                        desistimiento.setFechaSolicitud(rf.getApplicationDate());
//                                desistimiento.setCertificado(c.getNextNumeroCertificadoTransferencia() + "");
                                        desistimiento.setSigno(ttp.getAlias());
                                        desistimiento.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");

                                        desistimiento.setIdRenewalForm(rf.getId());
                                        if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                            HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                            if (hf.getId() != null) {
                                                desistimiento.setDenominacion(hf.getDenomination());
                                                desistimiento.setTitulo(hf.getExpedient());

                                                if (desistimiento.getFechaTitulo() != null && !desistimiento.getTitulo().trim().isEmpty()) {
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(desistimiento.getTitulo(), desistimiento.getDenominacion());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        desistimiento.setFechaTitulo(titulo.getFechaEmisionDocumento());
                                                        desistimiento.setTitularAnterior(titulo.getTitular());
                                                    }
                                                }
                                                if (desistimiento.getFechaTitulo() == null) {
                                                    if (hf.getExpYear() != null && !hf.getExpYear().trim().isEmpty()) {
                                                        desistimiento.setFechaTitulo(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                    }
                                                }
                                            }
                                        } else {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                if (ps.getCodigoSolicitudSigno() != null) {
                                                    desistimiento.setDenominacion(ps.getDenominacionSigno());
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        desistimiento.setTitulo(titulo.getNumeroTitulo());
                                                        desistimiento.setFechaTitulo(titulo.getFechaEmisionDocumento());
                                                        desistimiento.setTitularAnterior(titulo.getTitular());
                                                    }

                                                    if (desistimiento.getTitularAnterior() == null || desistimiento.getTitularAnterior().trim().isEmpty()) {
                                                        PpdiPersona persona = c.getASolicitanteByCodigoSolicitud(ps.getCodigoSolicitudSigno());
                                                        if (persona.getCodigoPersona() != null) {
                                                            desistimiento.setTitularAnterior(persona.getNombrePersona());
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        Person titAct = c.getTitularActual(rf.getId());
                                        if (titAct.getId() != null) {
                                            desistimiento.setTitularActual(titAct.getName());
//                                    desistimiento.setDomicilioTitularActual(titAct.getAddress());
                                            desistimiento.setIdentificacion(titAct.getIdentificationNumber());
                                        }

                                        Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "AGENT");
                                        if (apoder.getId() != null) {
                                            desistimiento.setAbogadoPatrocinador(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "AGENT"));
                                        }

                                        if (desistimiento.getTitulo() != null && !desistimiento.getTitulo().trim().isEmpty()) {
                                            if (desistimiento.getDenominacion() != null && !desistimiento.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(desistimiento.getTitulo(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(desistimiento.getTitulo(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getTitulo() + " CON DENOMINACIÓN "
                                                                    + desistimiento.getDenominacion() + " SE ENCUENTRA CANCELADO; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            desistimiento = new Desistimiento();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            desistimiento.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getTitulo() + " CON DENOMINACIÓN "
                                                                    + desistimiento.getDenominacion() + " SE ENCUENTRA CANCELADO PARCIALMENTE; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getTitulo() + " CON DENOMINACIÓN "
                                                                    + desistimiento.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            desistimiento = new Desistimiento();
                                                        }
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(desistimiento.getTitulo(), desistimiento.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(desistimiento.getTitulo(), desistimiento.getDenominacion());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getTitulo() + " CON DENOMINACIÓN "
                                                                    + desistimiento.getDenominacion() + " SE ENCUENTRA CANCELADO; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            desistimiento = new Desistimiento();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            desistimiento.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getTitulo() + " CON DENOMINACIÓN "
                                                                    + desistimiento.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getTitulo() + " CON DENOMINACIÓN "
                                                                    + desistimiento.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            desistimiento = new Desistimiento();
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
            desistimientos = c.getDesistimientosByFecha(fechaInicio, fechaFin);
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
        desistimiento = (Desistimiento) desistimientoDataTable.getRowData();
        if (desistimiento != null) {
            Controlador c = new Controlador();
            if (c.removeDesistimiento(desistimiento)) {
//                c.saveHistorial("RENOVACIÓN", "RENOVACIÓN", renovacion.getSolicitudSenadi(), "ELIMINADO", loginBean.getUsuario().getId(), loginBean.getUsuario().getLogin());
                loadDesistimientos();
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
        desistimiento = (Desistimiento) desistimientoDataTable.getRowData();
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
        dialogTitle = "NUEVO DESISTIMIENTO";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar el Nuevo Desistimiento?";
        desistimiento = new Desistimiento();
        Controlador c = new Controlador();
        desistimiento.setResolucion(c.getNextNumeroDesistimiento() + "");
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
                    if (estadoTemp.equals("TRANSFERENCIAS")) {
                        Transferencia transferencia = new Transferencia();
                        transferencia.setSolicitud(desistimiento.getSolicitud().toUpperCase());
                        transferencia.setFechaPresentacion(desistimiento.getFechaSolicitud());
                        transferencia.setCertificado(Integer.valueOf(desistimiento.getResolucion()));
                        transferencia.setFechaCertificado(desistimiento.getFechaResolucion());
                        transferencia.setRegistro(desistimiento.getTitulo());
                        transferencia.setFechaRegistro(desistimiento.getFechaTitulo());
                        transferencia.setDenominacion(desistimiento.getDenominacion());
                        transferencia.setSigno(desistimiento.getSigno());
                        transferencia.setTitularAnterior(desistimiento.getTitularAnterior());
                        transferencia.setTitularActual(desistimiento.getTitularActual());
                        transferencia.setApoderadoRepresentanteLegal(desistimiento.getAbogadoPatrocinador());
                        transferencia.setRo(desistimiento.getRo());
                        transferencia.setCasilleroSenadi(desistimiento.getCasilleroSenadi());
                        transferencia.setCasilleroJudicial(desistimiento.getCasilleroJudicial());
                        transferencia.setResponsable(desistimiento.getResponsable());
                        transferencia.setIdentificacion(desistimiento.getIdentificacion());
                        transferencia.setEmail(desistimiento.getEmail());
                        transferencia.setComprobante(desistimiento.getComprobante());
                        transferencia.setCancelado(desistimiento.getCancelado());

                        if (c.validarExistenciaTransferencia(transferencia.getSolicitud())) {
                            PrimeFaces.current().ajax().addCallbackParam("saved", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en trasnferencias con el mismo número de solicitud");
                        } else {
                            if (c.saveTransferencia(transferencia)) {

                                c = new Controlador();
                                Desistimiento desists = c.getDesistidasBySolSenadi(transferencia.getSolicitud());

                                if (c.removeDesistimiento(desists)) {

                                    c.saveHistorial("TRANSFERENCIAS", "DESISTIDAS", transferencia.getSolicitud(), "PASADO A", getLoginBean().getUsuario().getId(), getLoginBean().getNombre());

                                    loadDesistimientos();
                                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                    System.out.println("Se ha pasado la solicitud " + transferencia.getSolicitud() + " de Desistidas a Transferencias");
                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "TRANSFERENCIA DE DATOS SATISFACTORIA");
                                } else {
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER EL DESISTIMIENTO");
                                }
                            } else {
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                            }
                        }
                    } else if (estadoTemp.equals("NOTIFICADAS")) {
                        Notificacion notificacion = new Notificacion();
                        notificacion.setSolicitud(desistimiento.getSolicitud().toUpperCase());
                        notificacion.setFechaPresentacion(desistimiento.getFechaSolicitud());
                        notificacion.setFechaElaboraNotificacion(new Date());
                        notificacion.setNotificacion(c.getNextNumeroNotificacion(notificacion.getFechaElaboraNotificacion()));
                        notificacion.setRegistro(desistimiento.getTitulo());
                        notificacion.setFechaRegistro(desistimiento.getFechaTitulo());
                        notificacion.setDenominacion(desistimiento.getDenominacion());
                        notificacion.setSigno(desistimiento.getSigno());
                        notificacion.setTitularAnterior(desistimiento.getTitularAnterior());
                        notificacion.setTitularActual(desistimiento.getTitularActual());
                        notificacion.setApeApodRepre(desistimiento.getAbogadoPatrocinador());
                        notificacion.setRo(desistimiento.getRo());
                        notificacion.setCasilleroSenadi(desistimiento.getCasilleroSenadi());
                        notificacion.setCasilleroJudicial(desistimiento.getCasilleroJudicial());
                        notificacion.setResponsable(desistimiento.getResponsable());
                        notificacion.setIdentificacion(desistimiento.getIdentificacion());
                        notificacion.setCertificado(desistimiento.getResolucion());
                        notificacion.setFechaCertificado(desistimiento.getFechaResolucion());
                        notificacion.setComprobante(desistimiento.getComprobante());
                        notificacion.setCancelado(desistimiento.getCancelado());

                        if (c.validarExistenciaNotificacion(notificacion.getSolicitud())) {
                            PrimeFaces.current().ajax().addCallbackParam("saved", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en notificaciones con el mismo número de solicitud");
                        } else {
                            if (c.saveNotificacion(notificacion)) {

                                c = new Controlador();
                                Desistimiento desists = c.getDesistidasBySolSenadi(notificacion.getSolicitud());

                                if (c.removeDesistimiento(desists)) {

                                    c.saveHistorial("NOTIFICADAS", "DESISTIMIENTO", notificacion.getSolicitud(), "PASADO A", loginBean.getUsuario().getId(), loginBean.getNombre());

                                    loadDesistimientos();
                                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                    System.out.println("Se ha pasado la solicitud " + notificacion.getSolicitud() + " de Desistimiento a Notificaciones");
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
                    } else {
                        Caducada caducada = new Caducada();
                        caducada.setSolicitud(desistimiento.getSolicitud().toUpperCase());
                        caducada.setFechaPresentacion(desistimiento.getFechaSolicitud()); //<-----------
                        caducada.setResolucion(desistimiento.getResolucion());
                        caducada.setFechaResolucion(desistimiento.getFechaResolucion());
                        caducada.setRegistro(desistimiento.getTitulo());
                        caducada.setFechaRegistro(desistimiento.getFechaTitulo());
                        caducada.setDenominacion(desistimiento.getDenominacion());
                        caducada.setSigno(desistimiento.getSigno());
                        caducada.setTitularAnterior(desistimiento.getTitularAnterior());
                        caducada.setTitularActual(desistimiento.getTitularActual());
                        caducada.setApoderadoRepresetante(desistimiento.getAbogadoPatrocinador());
                        caducada.setCasilleroSenadi(desistimiento.getCasilleroSenadi());
                        caducada.setCasilleroJudicial(desistimiento.getCasilleroJudicial());
                        caducada.setEmail(desistimiento.getEmail());
                        caducada.setRo(desistimiento.getRo());
                        caducada.setResponsable(desistimiento.getResponsable());
                        caducada.setIdentificacion(desistimiento.getIdentificacion());
                        caducada.setEtiqueta(desistimiento.getEtiqueta());
                        caducada.setComprobante(desistimiento.getComprobante());
                        caducada.setCancelado(desistimiento.getCancelado());

                        if (c.validarExistenciaCaducada(caducada.getSolicitud())) {
                            PrimeFaces.current().ajax().addCallbackParam("saved", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en caducadas con el mismo número de solicitud");
                        } else {
                            if (c.saveCaducada(caducada)) {

                                c = new Controlador();
                                Desistimiento desists = c.getDesistidasBySolSenadi(caducada.getSolicitud());

                                if (c.removeDesistimiento(desists)) {

                                    c.saveHistorial("CADUCADAS-NEGADAS", "DESISTIMIENTO", caducada.getSolicitud(), "PASADO A", loginBean.getUsuario().getId(), loginBean.getNombre());
                                    loadDesistimientos();
                                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                    System.out.println("Se ha pasado la solicitud " + caducada.getSolicitud() + " de desistimientos a caducada-negada");
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
                    //Editar Desistimiento
                    if (c.validarExistenciaDesistimiento(desistimiento)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                    } else {
                        desistimiento.setSolicitud(desistimiento.getSolicitud().toUpperCase());
                        if (c.updateDesistimiento(desistimiento)) {
                            c.saveHistorial("DESISTIMIENTO", "DESISTIMIENTO", desistimiento.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESISTIMIENTO EDITADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR EL DESISTIMIENTO");
                        }
                    }
                }
            } else {
                //Guardar Desistimiento
                if (c.existeTramiteDesistimiento(desistimiento.getSolicitud())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                } else {
                    ModificacionApp mapp = c.getModificacionApp(desistimiento.getSolicitud());
                    if (mapp.getId() != null) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE NO SE PUEDE REGISTRAR: " + mapp.getObservacion());
                    } else {
                        boolean habilitado = true;
                        if (desistimiento.getDenominacion() != null && !desistimiento.getDenominacion().trim().isEmpty()
                                && desistimiento.getTitulo() != null && !desistimiento.getTitulo().trim().isEmpty()) {
                            if (c.existsTituloCanceladoByTituloAndDenominacion(desistimiento.getTitulo(), desistimiento.getDenominacion(), false)) {
                                TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(desistimiento.getTitulo(), desistimiento.getDenominacion());
                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getTitulo() + " CON DENOMINACIÓN '"
                                            + desistimiento.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    desistimiento = new Desistimiento();
                                    habilitado = false;
                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                    desistimiento.setCancelado(titca.getTipoCancelacion());
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getTitulo() + " CON DENOMINACIÓN '"
                                            + desistimiento.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + desistimiento.getTitulo() + " CON DENOMINACIÓN '"
                                            + desistimiento.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    desistimiento = new Desistimiento();
                                    habilitado = false;
                                }
                            } else {
                                habilitado = true;
                            }
                        }
                        if (habilitado) {
                            desistimiento.setSolicitud(desistimiento.getSolicitud().toUpperCase());
                            if (c.saveDesistimiento(desistimiento)) {
                                c.saveModificacionApp(desistimiento.getDenominacion(), desistimiento.getTitulo(), desistimiento.getSolicitud(), "TRANSFERENCIA", loginBean.getNombre());
                                c.saveHistorial("DESISTIMIENTO", "DESISTIMIENTO", desistimiento.getSolicitud(), "NUEVO DESISTIMIENTO", loginBean.getUsuario().getId(), loginBean.getNombre());
                                loadDesistimientos();
                                PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESISTIMIENTO GUARDADO CON ÉXITO");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL GUARDAR EL DESISTIMIENTO");
                            }
                        }
                    }
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        desistimiento = (Desistimiento) desistimientoDataTable.getRowData();
        if (desistimiento != null) {
            dialogTitle = "SEGUIMIENTO " + desistimiento.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(desistimiento.getSolicitud());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: DESISTIDAS";
            }
        }
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

    public void viewDesistimiento(ActionEvent ae) {
        FacesMessage msg = null;
        desistimiento = (Desistimiento) desistimientoDataTable.getRowData();

        if (desistimiento != null) {
            System.out.println("Descargando Desistimiento: " + desistimiento.getSolicitud());
//            System.out.println(notificacion.getFechaCertificado());

            loginBean.setTransferenciaFlotante(null);
            loginBean.setNotificacionFlotante(null);
            loginBean.setDesistimientoFlotante(desistimiento);

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
    public List<Desistimiento> getDesistimientos() {
        return desistimientos;
    }

    /**
     * @param desistimientos the desistimientos to set
     */
    public void setDesistimientos(List<Desistimiento> desistimientos) {
        this.desistimientos = desistimientos;
    }

    /**
     * @return the desistimientosFiltradas
     */
    public List<Desistimiento> getDesistimientosFiltradas() {
        return desistimientosFiltradas;
    }

    /**
     * @param desistimientosFiltradas the desistimientosFiltradas to set
     */
    public void setDesistimientosFiltradas(List<Desistimiento> desistimientosFiltradas) {
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
    public Desistimiento getDesistimiento() {
        return desistimiento;
    }

    /**
     * @param desistimiento the desistimiento to set
     */
    public void setDesistimiento(Desistimiento desistimiento) {
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
