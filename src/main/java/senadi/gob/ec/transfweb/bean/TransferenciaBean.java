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
import senadi.gob.ec.transfweb.model.UploadNotificacion;
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepform.ModificacionApp;
import senadi.gob.ec.transfweb.model.iepform.PaymentReceipt;
import senadi.gob.ec.transfweb.model.iepform.Person;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepform.Types;
import senadi.gob.ec.transfweb.model.iepicas.Owner;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author Michael
 */
@ManagedBean(name = "transferenciaBean")
@ViewScoped
public class TransferenciaBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private Date fechaInicioCertificado;
    private Date fechaFinCertificado;

    private List<Transferencia> transferencias;
    private List<Transferencia> transferenciasFiltradas;

    private UIData transferenciasDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private Transferencia transferencia;

    private LoginBean loginBean;

    private String estadoTemp;
    private String historial;

    private String exportName;

    private List<Transferencia> selectedTransferencias;

    private boolean separado;

    private List<Documento> archivos;

    private String rutaNotificacionCasillero;

    private String criterioOwner;
    private List<Owner> owners;
    private List<Owner> ownersFiltrados;
    private UIData ownerDataTable;

    public TransferenciaBean() {
        loadTransferencias();
    }

    //        c.llenarTablaModificacionesFormularios();
    private void loadTransferencias() {
        Controlador c = new Controlador();

//        c.depurarModificacionesApp();
        transferencias = c.getTransferencias();
        numRegistros = "Número Registros Mostrados: " + transferencias.size();
        exportName = "transferencia_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
        selectedTransferencias = new ArrayList<>();
    }

    public void prepararBusquedaCasillero(ActionEvent ae) {
        owners = new ArrayList<>();
        criterioOwner = "";
    }

    public void buscarCasillero(ActionEvent ae) {
        FacesMessage msg = null;
        if (transferencia != null) {
            if (criterioOwner != null && !criterioOwner.trim().isEmpty()) {
                System.out.println("criterio owner lic: " + criterioOwner);
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
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ EL CERTIFICADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void limpiarBusquedaCasillero(ActionEvent ae) {
        owners = new ArrayList<>();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "HECHO");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void seleccionarCasillero(ActionEvent ae) {
        FacesMessage msg = null;
        if (transferencia != null) {
            Owner own = (Owner) ownerDataTable.getRowData();
            if (own != null) {
                transferencia.setCasilleroSenadi(own.getCasillero());
                owners = new ArrayList<>();
                criterioOwner = "";
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CASILLERO " + own.getCasillero() + " SELECCIONADO CORRECTAMENTE");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL SELECCIONAR EL CASILLERO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ EL CERTIFICADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;
//        System.out.println("LLegando por aquí");
        if (transferencia != null && transferencia.getSolicitud() != null && !transferencia.getSolicitud().trim().isEmpty()) {
//            System.out.println(transferencia.getSolicitud());
            transferencia.setSolicitud(transferencia.getSolicitud().trim().toUpperCase());
            String tramite = transferencia.getSolicitud();

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
                    System.out.println("M-BUSCANDO " + tramite);
                    RenewalForm rf = c.getRenewalFormsByApplicationNumber(tramite);

                    if (rf.getId() != null) {
                        if (rf.getStatus().equals("DELIVERED")) {

                            Types t = c.getTypes(rf.getTransactionMotiveId());

                            Types ttp = c.getTypes(rf.getFormId());

                            if (t.getId() != null && ttp.getId() != null) {
                                System.out.println("---> " + ttp.getAlias());
                                if (!ttp.getAlias().equals("PI") && !ttp.getAlias().equals("MU") && !ttp.getAlias().equals("DI")) {
                                    if (t.getName().trim().toLowerCase().contains("transferencia")
                                            || t.getName().trim().toLowerCase().contains("transmisión")) {

                                        PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                        transferencia.setComprobante(payment.getVoucherNumber());
                                        transferencia.setFechaPresentacion(rf.getApplicationDate());
//                                transferencia.setCertificado(c.getNextNumeroCertificadoTransferencia());

                                        transferencia.setSigno(ttp.getAlias());
                                        transferencia.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");
                                        transferencia.setIdRenewalForm(rf.getId());

                                        if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                            HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                            if (hf.getId() != null) {
                                                transferencia.setDenominacion(hf.getDenomination());
                                                transferencia.setRegistro(hf.getExpedient());
//                                                System.out.println("debug_id: "+rf.getDebugId());
                                                if (transferencia.getRegistro() != null && !transferencia.getRegistro().trim().isEmpty()) {
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(transferencia.getRegistro(), transferencia.getDenominacion());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        transferencia.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        transferencia.setTitularAnterior(titulo.getTitular());
                                                    }
                                                }
                                                if (transferencia.getFechaRegistro() == null) {
                                                    if (hf.getExpYear() != null && !hf.getExpYear().trim().isEmpty()) {
                                                        transferencia.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                    }
                                                }
                                            }
                                        } else {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                if (ps.getCodigoSolicitudSigno() != null) {
                                                    transferencia.setDenominacion(ps.getDenominacionSigno());
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        transferencia.setRegistro(titulo.getNumeroTitulo());
                                                        transferencia.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        transferencia.setTitularAnterior(titulo.getTitular());
                                                    }
                                                }
                                                if (transferencia.getTitularAnterior() == null || transferencia.getTitularAnterior().trim().isEmpty()) {
                                                    PpdiPersona persona = c.getASolicitanteByCodigoSolicitud(ps.getCodigoSolicitudSigno());
                                                    if (persona.getCodigoPersona() != null) {
                                                        transferencia.setTitularAnterior(persona.getNombrePersona());
                                                    }
                                                }
                                            }
                                        }
//                                        System.out.println("1111111");
                                        Person titAct = c.getTitularActual(rf.getId());
                                        if (titAct.getId() != null) {
                                            transferencia.setTitularActual(titAct.getName());
                                            transferencia.setDomicilioTitularActual(titAct.getAddress());
                                            transferencia.setIdentificacion(titAct.getIdentificationNumber());
                                        }
//System.out.println("2222");
                                        Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "AGENT");
                                        if (apoder.getId() != null) {
                                            transferencia.setApoderadoRepresentanteLegal(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "AGENT"));
                                        }

                                        if (transferencia.getRegistro() != null && !transferencia.getRegistro().trim().isEmpty()) {
                                            if (transferencia.getDenominacion() != null && !transferencia.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(transferencia.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(transferencia.getRegistro(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + transferencia.getRegistro() + " CON DENOMINACIÓN "
                                                                    + transferencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            transferencia = new Transferencia();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            transferencia.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + transferencia.getRegistro() + " CON DENOMINACIÓN "
                                                                    + transferencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + transferencia.getRegistro() + " CON DENOMINACIÓN "
                                                                    + transferencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            transferencia = new Transferencia();
                                                        }
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(transferencia.getRegistro(), transferencia.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(transferencia.getRegistro(), transferencia.getDenominacion());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + transferencia.getRegistro() + " CON DENOMINACIÓN "
                                                                    + transferencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            transferencia = new Transferencia();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            transferencia.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + transferencia.getRegistro() + " CON DENOMINACIÓN "
                                                                    + transferencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + transferencia.getRegistro() + " CON DENOMINACIÓN "
                                                                    + transferencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            transferencia = new Transferencia();
                                                        }
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                }
                                            } else {
                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                            }
//System.out.println("3333");
                                        } else {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                        }
                                    } else {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UNA TRANSFERENCIA, SINO '" + t.getName().toUpperCase() + "'");
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

    public void buscarTransferencias(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            transferencias = c.getTransferenciaByCriteria(criterio.trim());
            numRegistros = "Número Registros Mostrados: " + transferencias.size();
            if (transferencias.isEmpty()) {
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

    public void buscarTransferenciasPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            transferencias = c.getTransferenciasByFecha(fechaInicio, fechaFin);
            numRegistros = "Número Registros Mostrados: " + transferencias.size();
            if (transferencias.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarTransferenciasPorFechaCertificado(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechasCertificado()) {
            Controlador c = new Controlador();
            transferencias = c.getTransferenciaByFechaCertificado(fechaInicioCertificado, fechaFinCertificado);
            numRegistros = "Número Registros Mostrados: " + transferencias.size();
            if (transferencias.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarTransferencia(ActionEvent ae) {
        FacesMessage msg = null;
        transferencia = (Transferencia) transferenciasDataTable.getRowData();
        if (transferencia != null) {
            Controlador c = new Controlador();
            if (c.removeTransferencia(transferencia)) {
                c.saveHistorial("TRANSFERENCIA", "TRANSFERENCIA", transferencia.getSolicitud(), "ELIMINADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                loadTransferencias();
                System.out.println("Transferencia " + transferencia.getSolicitud() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TRANSFERENCIA " + transferencia.getSolicitud() + " ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR TRANSFERENCIA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR TRANSFERENCIA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {

        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
        transferencia = (Transferencia) transferenciasDataTable.getRowData();
        if (transferencia != null) {
            dialogTitle = "EDITAR TRANSFERENCIA " + transferencia.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar la Transferencia: " + transferencia.getSolicitud() + "?";
            Controlador c = new Controlador();
            RenewalForm rf = c.getRenewalFormsByApplicationNumber(transferencia.getSolicitud());
            if (rf.getId() != null) {
                transferencia.setIdRenewalForm(rf.getId());
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TRANSFERENCIA CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR TRANSFERENCIA");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararNuevo(ActionEvent ae) {
        dialogTitle = "NUEVA TRANSFERENCIA";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar la Nueva Transferencia?";
        transferencia = new Transferencia();
        Controlador c = new Controlador();
//        transferencia.setCertificado(c.getNextNumeroCertificadoTransferencia());
        transferencia.setResponsable(loginBean.getUsuario().getAlias());
        edicion = false;
        if (transferencia != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void guardarTransferencia(ActionEvent ae) {
        FacesMessage msg = null;
        if (transferencia != null) {
            Controlador c = new Controlador();
            if (transferencia.getId() != null) {
                if (transferencia.getCertificado() == null || transferencia.getCertificado() == 0) {
                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "CERTIFICADO", "INGRESE UN NÚMERO DE CERTIFICADO VÁLIDO");
                } else {
                    if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                        if (estadoTemp.equals("NOTIFICADAS")) {
                            Notificacion notificacion = new Notificacion();
                            notificacion.setSolicitud(transferencia.getSolicitud().toUpperCase());
                            notificacion.setFechaPresentacion(transferencia.getFechaPresentacion());
                            notificacion.setFechaElaboraNotificacion(new Date());
                            notificacion.setNotificacion(c.getNextNumeroNotificacion(notificacion.getFechaElaboraNotificacion()));
                            notificacion.setFechaNotificacion(transferencia.getFechaNotificacion());
                            notificacion.setRegistro(transferencia.getRegistro());
                            notificacion.setFechaRegistro(transferencia.getFechaRegistro());
                            notificacion.setDenominacion(transferencia.getDenominacion());
                            notificacion.setSigno(transferencia.getSigno());
                            notificacion.setTitularAnterior(transferencia.getTitularAnterior());
                            notificacion.setTitularActual(transferencia.getTitularActual());
                            notificacion.setApeApodRepre(transferencia.getApoderadoRepresentanteLegal());
                            notificacion.setRo(transferencia.getRo());
                            notificacion.setCasilleroSenadi(transferencia.getCasilleroSenadi());
                            notificacion.setCasilleroJudicial(transferencia.getCasilleroJudicial());
                            notificacion.setResponsable(transferencia.getResponsable());
                            notificacion.setIdentificacion(transferencia.getIdentificacion());
                            notificacion.setCertificado(transferencia.getCertificado() + "");
                            notificacion.setFechaCertificado(transferencia.getFechaCertificado());
                            notificacion.setDomicilioTitularActual(transferencia.getDomicilioTitularActual());
                            notificacion.setComprobante(transferencia.getComprobante());
                            notificacion.setCertificadoEmitido(transferencia.isCertificadoEmitido());
                            notificacion.setNotificacionEmitida(transferencia.isNotificacionEmitida());
                            notificacion.setCancelado(transferencia.getCancelado());

                            if (c.validarExistenciaNotificacion(notificacion.getSolicitud())) {
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en notificaciones con el mismo número de solicitud");
                            } else {
                                if (c.saveNotificacion(notificacion)) {
                                    c = new Controlador();
                                    Transferencia transferes = c.getTransferenciaBySolSenadi(notificacion.getSolicitud());
                                    if (c.removeTransferencia(transferes)) {
                                        c.saveHistorial("NOTIFICADAS", "TRANSFERENCIAS", notificacion.getSolicitud(), "PASADO A", loginBean.getUsuario().getId(), loginBean.getNombre());
                                        loadTransferencias();
                                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                        System.out.println("Se ha pasado la solicitud " + notificacion.getSolicitud() + " de Transferencias a Notificaciones");
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
                            desist.setSolicitud(transferencia.getSolicitud().toUpperCase());
                            desist.setFechaSolicitud(transferencia.getFechaPresentacion());
                            desist.setResolucion(transferencia.getCertificado() + "");
                            desist.setFechaResolucion(transferencia.getFechaCertificado());
                            desist.setTitulo(transferencia.getRegistro());
                            desist.setFechaTitulo(transferencia.getFechaRegistro());

//                        desist.setFechaVencimiento();
                            desist.setDenominacion(transferencia.getDenominacion());
                            desist.setSigno(transferencia.getSigno());
                            desist.setTitularAnterior(transferencia.getTitularAnterior());
                            desist.setTitularActual(transferencia.getTitularActual());
                            desist.setAbogadoPatrocinador(transferencia.getApoderadoRepresentanteLegal());

                            desist.setCasilleroSenadi(transferencia.getCasilleroSenadi());
                            desist.setCasilleroJudicial(transferencia.getCasilleroJudicial());
                            desist.setEmail(transferencia.getEmail());
                            desist.setRo(transferencia.getRo());
                            desist.setResponsable(transferencia.getResponsable());
                            desist.setIdentificacion(transferencia.getIdentificacion());
                            desist.setComprobante(transferencia.getComprobante());
                            desist.setCancelado(transferencia.getCancelado());

                            if (c.validarExistenciaDesistimiento(desist.getSolicitud())) {
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en desistimientos con el mismo número de solicitud");
                            } else {
                                if (c.saveDesistimiento(desist)) {
                                    c = new Controlador();
                                    Transferencia transfers = c.getTransferenciaBySolSenadi(desist.getSolicitud());
                                    if (c.removeTransferencia(transfers)) {
                                        c.saveHistorial("DESISTIDAS", "TRANSFERENCIAS", desist.getSolicitud(), "PASADO A", loginBean.getUsuario().getId(), loginBean.getNombre());
                                        loadTransferencias();
                                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                        System.out.println("Se ha pasado la solicitud " + desist.getSolicitud() + " de transferencia a desistida");
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
                            caducada.setSolicitud(transferencia.getSolicitud().toUpperCase());
                            caducada.setFechaPresentacion(transferencia.getFechaPresentacion()); //<-----------
                            caducada.setResolucion(transferencia.getCertificado() + "");
                            caducada.setFechaResolucion(transferencia.getFechaCertificado());
                            caducada.setRegistro(transferencia.getRegistro());
                            caducada.setFechaRegistro(transferencia.getFechaRegistro());
                            caducada.setDenominacion(transferencia.getDenominacion());
                            caducada.setSigno(transferencia.getSigno());
                            caducada.setTitularAnterior(transferencia.getTitularAnterior());
                            caducada.setTitularActual(transferencia.getTitularActual());
                            caducada.setApoderadoRepresetante(transferencia.getApoderadoRepresentanteLegal());
                            caducada.setFechaNotificacion(transferencia.getFechaNotificacion());
                            caducada.setCasilleroSenadi(transferencia.getCasilleroSenadi());
                            caducada.setCasilleroJudicial(transferencia.getCasilleroJudicial());
                            caducada.setEmail(transferencia.getEmail());
                            caducada.setRo(transferencia.getRo());
                            caducada.setResponsable(transferencia.getResponsable());
                            caducada.setIdentificacion(transferencia.getIdentificacion());
                            caducada.setComprobante(transferencia.getComprobante());
                            caducada.setCancelado(transferencia.getCancelado());

                            if (c.validarExistenciaCaducada(caducada.getSolicitud())) {
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en caducadas con el mismo número de solicitud");
                            } else {
                                if (c.saveCaducada(caducada)) {
                                    c = new Controlador();
                                    Transferencia trasnferes = c.getTransferenciaBySolSenadi(caducada.getSolicitud());

                                    if (c.removeTransferencia(trasnferes)) {
                                        c.saveHistorial("CADUCADAS-NEGADAS", "TRANSFERENCIAS", caducada.getSolicitud(), "PASADO A", loginBean.getUsuario().getId(), loginBean.getNombre());
                                        loadTransferencias();
                                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                        System.out.println("Se ha pasado la solicitud " + caducada.getSolicitud() + " de transferencia a caducada-negada");
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
                        }
                    } else {
                        //Editar Transferencia
                        if (c.validarExistenciaTransferencia(transferencia)) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                        } else {
                            transferencia.setSolicitud(transferencia.getSolicitud());
                            if (c.updateTransferencia(transferencia)) {
                                c.saveHistorial("TRANSFERENCIA", "TRANSFERENCIA", transferencia.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                                PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TRANSFERENCIA EDITADA CON ÉXITO");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA TRANSFERENCIA");
                            }
                        }
                    }
                }
            } else {
                //Guardar Transferencia
                if (c.existeTramiteTransferencia(transferencia.getSolicitud())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                } else {
                    ModificacionApp mapp = c.getModificacionApp(transferencia.getSolicitud());
                    if (mapp.getId() != null) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE NO SE PUEDE REGISTRAR: " + mapp.getObservacion());
                    } else {
                        boolean habilitado = true;
                        if (transferencia.getDenominacion() != null && !transferencia.getDenominacion().trim().isEmpty()
                                && transferencia.getRegistro() != null && !transferencia.getRegistro().trim().isEmpty()) {
                            if (c.existsTituloCanceladoByTituloAndDenominacion(transferencia.getRegistro(), transferencia.getDenominacion(), false)) {
                                TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(transferencia.getRegistro(), transferencia.getDenominacion());
                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + transferencia.getRegistro() + " CON DENOMINACIÓN '"
                                            + transferencia.getDenominacion() + "' SE ENCUENTRA CANCELADO; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    transferencia = new Transferencia();
                                    habilitado = false;
                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                    transferencia.setCancelado(titca.getTipoCancelacion());
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + transferencia.getRegistro() + " CON DENOMINACIÓN '"
                                            + transferencia.getDenominacion() + "' SE ENCUENTRA CANCELADO PARCIALMENTE; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + transferencia.getRegistro() + " CON DENOMINACIÓN '"
                                            + transferencia.getDenominacion() + "' SE ENCUENTRA CANCELADO; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    transferencia = new Transferencia();
                                    habilitado = false;
                                }
                            } else {
                                habilitado = true;
                            }
                        }

                        if (habilitado) {
                            transferencia.setCertificado(c.getNextNumeroCertificadoTransferencia());
                            transferencia.setSolicitud(transferencia.getSolicitud().toUpperCase());
                            if (c.saveTransferencia(transferencia)) {
                                c.saveModificacionApp(transferencia.getDenominacion(), transferencia.getRegistro(), transferencia.getSolicitud(), "TRANSFERENCIA", loginBean.getNombre());
                                c.saveHistorial("TRANSFERENCIA", "TRANSFERENCIA", transferencia.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                                loadTransferencias();
                                PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TRANSFERENCIA GUARDADA CON ÉXITO");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL GUARDAR LA TRANSFERENCIA");
                            }
                        }
                    }
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /* Da la orden de visualizar el reporte, clase Informe (Webservlet)*/
    public void viewTransferencia(ActionEvent ae) {
        FacesMessage msg = null;
        transferencia = (Transferencia) transferenciasDataTable.getRowData();
        boolean correcto = false;
        if (transferencia != null) {

            if (transferencia.getRegistro() != null && !transferencia.getRegistro().trim().isEmpty()) {
                System.out.println("Descargando Transferenia: " + transferencia.getSolicitud());

                Controlador c = new Controlador();

                if (!c.validarDelegadoActivo("delegado")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarDelegacionActivo()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarResolucionActiva("transferencia")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else {
                    correcto = true;
                    loginBean.setTransferenciaFlotante(transferencia);
                    loginBean.setNotificacionFlotante(null);
                    loginBean.setTransferenciasFlotantes(new ArrayList<Transferencia>());
                    loginBean.setNotificacionesFlotantes(new ArrayList<Notificacion>());
                    loginBean.setVarious(false);

                    PrimeFaces.current().ajax().addCallbackParam("doit", true);

                    System.out.println("envía transferencia descargar");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO DOCUMENTO " + transferencia.getSolicitud());
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL REGISTRO SELECCIONADO NO TIENE NÚMERO DE REGISTRO ASIGNADO");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR LOS DATOS DEL REGISTRO SELECCIONADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void verNotificacionTransferencia(ActionEvent ae) {
        FacesMessage msg = null;
        transferencia = (Transferencia) transferenciasDataTable.getRowData();
        if (transferencia != null) {

            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionByCriterio(transferencia.getSolicitud(), true);
            if (uploads.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCUENTRA UN CERTIFICADO REALIZADO PARA ESTE TRÁMITE");
            } else {
                String rutaCertCasillero = "";
                for (int i = 0; i < uploads.size(); i++) {
                    UploadNotificacion un = uploads.get(i);
                    String rutaux = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + un.getCasillero() + "/" + un.getDocumento();
                    int conf = Operaciones.validaTextoEnPdf(rutaux, "CERTIFICADO DE TRANSFERENCIA No");
                    if (conf == 1) {
                        rutaCertCasillero = rutaux;
                        break;
                    }

                }
                if (!rutaCertCasillero.trim().isEmpty()) {
                    PrimeFaces.current().ajax().addCallbackParam("notit", true);
                    PrimeFaces.current().ajax().addCallbackParam("rutanot", rutaCertCasillero);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "TRANSFERENCIA", "VISUALIZANDO CERTIFICADO");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCUENTRA UN CERTIFICADO REALIZADO PARA ESTE TRÁMITE");
                }

            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "FECHA", "EL TRÁMITE NO POSEE FECHA DE VENCIMIENTO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void validarTransferencia(Transferencia transf) {
        FacesMessage msg = null;
        if (transf != null) {
            rutaNotificacionCasillero = "";
            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionBySolicitud(transf.getSolicitud(), true);
            if (!uploads.isEmpty()) {
                if (uploads.size() > 1) {
                    for (int i = 0; i < uploads.size(); i++) {
                        UploadNotificacion unaux = uploads.get(i);
                        String rutaux = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                        int conf = Operaciones.validaTextoEnPdf(rutaux, "CERTIFICADO DE TRANSFERENCIA No");
                        if (conf == 1) {
                            rutaNotificacionCasillero = rutaux;
                            break;
                        }
                    }
                    if (!rutaNotificacionCasillero.trim().isEmpty()) {
                        PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                        PrimeFaces.current().ajax().addCallbackParam("view", rutaNotificacionCasillero);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO CARGAD0");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UN CERTIFICADO SELECCIONADA");
                    }
                } else {
                    UploadNotificacion unaux = uploads.get(0);
                    rutaNotificacionCasillero = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                    System.out.println("rutacertcas: " + rutaNotificacionCasillero);
                    PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                    PrimeFaces.current().ajax().addCallbackParam("view", rutaNotificacionCasillero);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO CARGADO");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ NINGÚN CERTIFICADO DEL TRÁMITE " + transf.getSolicitud());
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UN CERTIFICADO SELECCIONADA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        transferencia = (Transferencia) transferenciasDataTable.getRowData();
        if (transferencia != null) {
            dialogTitle = "SEGUIMIENTO " + transferencia.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(transferencia.getSolicitud());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (getHistorial().trim().isEmpty()) {
                historial = "Estado actual: TRANSFERENCIAS";
            }
        }
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (transferencia != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + transferencia.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(transferencia.getIdRenewalForm(), transferencia.getSolicitud());
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
        FacesMessage msg = null;
//        RequestContext context = RequestContext.getCurrentInstance();
        if (!selectedTransferencias.isEmpty()) {

            System.out.println("Descargando Múltiples Transferencias...");

            boolean band = true;
            for (int i = 0; i < selectedTransferencias.size(); i++) {
                if (selectedTransferencias.get(i).getRegistro() == null || selectedTransferencias.get(i).getRegistro().trim().isEmpty()) {
                    band = false;
                    break;
                }
            }
            if (band) {
                loginBean.setTransferenciasFlotantes(selectedTransferencias);
                loginBean.setNotificacionesFlotantes(new ArrayList<Notificacion>());
                loginBean.setVarious(true);
                loginBean.setAllInOne(!separado);

                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                PrimeFaces.current().ajax().addCallbackParam("view", "reportes");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "DESCARGA", "DESCARGANDO REGISTROS SELECCIONADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN REGISTRO", "TODOS LOS REGISTROS SELECCIONADOS DEBEN POSEER NÚMERO DE REGISTRO");
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
     * @return the transferencias
     */
    public List<Transferencia> getTransferencias() {
        return transferencias;
    }

    /**
     * @param transferencias the transferencias to set
     */
    public void setTransferencias(List<Transferencia> transferencias) {
        this.transferencias = transferencias;
    }

    /**
     * @return the transferenciasFiltradas
     */
    public List<Transferencia> getTransferenciasFiltradas() {
        return transferenciasFiltradas;
    }

    /**
     * @param transferenciasFiltradas the transferenciasFiltradas to set
     */
    public void setTransferenciasFiltradas(List<Transferencia> transferenciasFiltradas) {
        this.transferenciasFiltradas = transferenciasFiltradas;
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
     * @return the transferenciasDataTable
     */
    public UIData getTransferenciasDataTable() {
        return transferenciasDataTable;
    }

    /**
     * @param transferenciasDataTable the transferenciasDataTable to set
     */
    public void setTransferenciasDataTable(UIData transferenciasDataTable) {
        this.transferenciasDataTable = transferenciasDataTable;
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
     * @return the transferencia
     */
    public Transferencia getTransferencia() {
        return transferencia;
    }

    /**
     * @param transferencia the transferencia to set
     */
    public void setTransferencia(Transferencia transferencia) {
        this.transferencia = transferencia;
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
     * @return the selectedTransferencias
     */
    public List<Transferencia> getSelectedTransferencias() {
        return selectedTransferencias;
    }

    /**
     * @param selectedTransferencias the selectedTransferencias to set
     */
    public void setSelectedTransferencias(List<Transferencia> selectedTransferencias) {
        this.selectedTransferencias = selectedTransferencias;
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
}
