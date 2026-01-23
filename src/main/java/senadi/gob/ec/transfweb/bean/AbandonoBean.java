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
import senadi.gob.ec.transfweb.model.Abandono;
import senadi.gob.ec.transfweb.modelp.PpdiPersona;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import senadi.gob.ec.transfweb.model.Documento;
import senadi.gob.ec.transfweb.model.Historial;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.model.Rooptions;
import senadi.gob.ec.transfweb.model.TituloCancelado;
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepform.ModificacionApp;
import senadi.gob.ec.transfweb.model.iepform.PaymentReceipt;
import senadi.gob.ec.transfweb.model.iepform.Person;
import senadi.gob.ec.transfweb.model.iepform.PersonRenewal;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepform.Types;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author Michael
 */
@ManagedBean(name = "abandonoBean")
@ViewScoped
public class AbandonoBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private Date fechaInicioCertificado;
    private Date fechaFinCertificado;

    private List<Abandono> abandonos;
    private List<Abandono> abandonosFiltradas;

    private UIData abandonosDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private Abandono abandono;

    private LoginBean loginBean;

    private String estadoTemp;
    private String historial;

    private String exportName;

    private List<String> roRazones;
    private String razon;

    private boolean roselectable;

    private List<Abandono> selectedAbandonos;

    private List<Rooptions> roos;
    private UIData roDataTable;

    private String roChoose;

    private boolean separado;

    private String roshow;

    private List<Documento> archivos;

    private String rutaNotificacionCasillero;

    private boolean paraEnviar;

    public AbandonoBean() {
        loadAbandonos();
    }

    private void loadAbandonos() {
        Controlador c = new Controlador();
        abandonos = c.getAbandonos();
        numRegistros = "Número Registros Mostrados: " + abandonos.size();
        exportName = "abandono_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
        roRazones = Operaciones.getRazonesNotificar();
        razon = "";
        selectedAbandonos = new ArrayList<>();
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

    public void buscarAbandonos(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            abandonos = c.getAbandonoByCriteria(criterio.trim());
            numRegistros = "Número Registros Mostrados: " + abandonos.size();
            if (abandonos.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarAbandonosPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            abandonos = c.getAbandonosByFecha(fechaInicio, fechaFin);
            numRegistros = "Número Registros Mostrados: " + abandonos.size();
            if (abandonos.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararNuevo(ActionEvent ae) {
        dialogTitle = "NUEVO ABANDONO";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar el Nuevo Abandono?";
        abandono = new Abandono();
//        Controlador c = new Controlador();
        //abandono.setNotificacion(c.getNextNumeroAbandono(new Date())); //<--- revisar el next 
        abandono.setResponsable(loginBean.getUsuario().getAlias());
        abandono.setFechaAbandono(new Date());
        roos = new ArrayList<>();

        edicion = false;
        roselectable = false;
        razon = "";
        if (abandono != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;

//        System.out.println("LLegando por aquí");
        if (abandono != null && abandono.getSolicitud() != null && !abandono.getSolicitud().trim().isEmpty()) {
//            System.out.println(transferencia.getSolicitud());
            String tramite = abandono.getSolicitud();
            Controlador c = new Controlador();
            //revisar esto
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

                                        abandono.setComprobante(payment.getVoucherNumber());
                                        abandono.setFechaPresentacion(rf.getApplicationDate());
                                        abandono.setCertificado(c.getNextNumeroCertificadoTransferencia() + "");
                                        abandono.setSigno(ttp.getAlias());
                                        abandono.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");

                                        List<PersonRenewal> solicitantes = c.getPersonRenewalByIdRenewalAndType(rf.getId(), "'APPLICANT'");
                                        if (!solicitantes.isEmpty()) {
                                            abandono.setSolicitante(solicitantes.get(0).getName());
                                        }

                                        abandono.setIdRenewalForm(rf.getId());

                                        if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                            HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                            if (hf.getId() != null) {
                                                abandono.setDenominacion(hf.getDenomination());
                                                abandono.setRegistro(hf.getExpedient());

                                                if (abandono.getRegistro() != null && !abandono.getRegistro().trim().isEmpty()) {
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(abandono.getRegistro(), abandono.getDenominacion());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        abandono.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        abandono.setTitularAnterior(titulo.getTitular());
                                                    }
                                                }
                                                if (abandono.getFechaRegistro() == null) {
                                                    if (hf.getExpYear() != null && !hf.getExpYear().trim().isEmpty()) {
                                                        abandono.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                    }

                                                }
                                            }
                                        } else {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                if (ps.getCodigoSolicitudSigno() != null) {
                                                    abandono.setDenominacion(ps.getDenominacionSigno());
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        abandono.setRegistro(titulo.getNumeroTitulo());
                                                        abandono.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        abandono.setTitularAnterior(titulo.getTitular());
                                                    }

                                                    if (abandono.getTitularAnterior() == null || abandono.getTitularAnterior().trim().isEmpty()) {
                                                        PpdiPersona persona = c.getASolicitanteByCodigoSolicitud(ps.getCodigoSolicitudSigno());
                                                        if (persona.getCodigoPersona() != null) {
                                                            abandono.setTitularAnterior(persona.getNombrePersona());
                                                        }
                                                    }
                                                }

                                            }
                                        }

                                        Person titAct = c.getTitularActual(rf.getId());
                                        if (titAct.getId() != null) {
                                            abandono.setTitularActual(titAct.getName());
                                            abandono.setDomicilioTitularActual(titAct.getAddress());
                                            abandono.setIdentificacion(titAct.getIdentificationNumber());
                                        }

                                        Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "AGENT");
                                        if (apoder.getId() != null) {
                                            abandono.setApeApodRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "AGENT"));
                                        }

                                        if (abandono.getRegistro() != null && !abandono.getRegistro().trim().isEmpty()) {
                                            if (abandono.getDenominacion() != null && !abandono.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(abandono.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(abandono.getRegistro(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN "
                                                                    + abandono.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            abandono = new Abandono();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            abandono.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN "
                                                                    + abandono.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN "
                                                                    + abandono.getDenominacion() + " SSE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            abandono = new Abandono();
                                                        }
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(abandono.getRegistro(), abandono.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(abandono.getRegistro(), abandono.getDenominacion());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN "
                                                                    + abandono.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            abandono = new Abandono();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            abandono.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN "
                                                                    + abandono.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN "
                                                                    + abandono.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            abandono = new Abandono();
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

    public void guardarAbandono(ActionEvent ae) {
        FacesMessage msg = null;
        if (abandono != null) {
            Controlador c = new Controlador();
            if (abandono.getId() != null) {
                if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                    if (estadoTemp.equals("NOTIFICADAS")) {
                        Notificacion notificacion = new Notificacion();
                        notificacion.setSolicitud(abandono.getSolicitud().toUpperCase());
                        notificacion.setFechaPresentacion(abandono.getFechaPresentacion());
                        notificacion.setFechaElaboraNotificacion(new Date());
                        notificacion.setNotificacion(abandono.getNotificacion());
                        notificacion.setFechaNotificacion(abandono.getFechaNotificacion());
                        notificacion.setRegistro(abandono.getRegistro());
                        notificacion.setFechaRegistro(abandono.getFechaRegistro());
                        notificacion.setDenominacion(abandono.getDenominacion());
                        notificacion.setSigno(abandono.getSigno());
                        notificacion.setTitularAnterior(abandono.getTitularAnterior());
                        notificacion.setTitularActual(abandono.getTitularActual());
                        notificacion.setApeApodRepre(abandono.getApeApodRepre());
                        notificacion.setRo(abandono.getRo());
                        notificacion.setCasilleroSenadi(abandono.getCasilleroSenadi());
                        notificacion.setCasilleroJudicial(abandono.getCasilleroJudicial());
                        notificacion.setResponsable(abandono.getResponsable());
                        notificacion.setIdentificacion(abandono.getIdentificacion());
                        notificacion.setCertificado(abandono.getCertificado() + "");
                        notificacion.setFechaCertificado(abandono.getFechaCertificado());
                        notificacion.setDomicilioTitularActual(abandono.getDomicilioTitularActual());
                        notificacion.setComprobante(abandono.getComprobante());
                        notificacion.setCertificadoEmitido(abandono.isCertificadoEmitido());
                        notificacion.setNotificacionEmitida(abandono.isNotificacionEmitida());
                        notificacion.setCancelado(abandono.getCancelado());

                        if (c.validarExistenciaNotificacion(abandono.getSolicitud())) {
                            PrimeFaces.current().ajax().addCallbackParam("saved", false);
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "EXISTENCIA", "Ya existe un trámite en notificaciones con el mismo número de solicitud");
                        } else {
                            if (c.saveNotificacion(notificacion)) {
                                c = new Controlador();
                                Abandono abanonore = c.getAbandonoBySolSenadi(notificacion.getSolicitud());
                                if (c.removeAbandono(abanonore)) {
                                    c.saveHistorial("NOTIFICADAS", "ABANDONOS", notificacion.getSolicitud(), "PASADO A", loginBean.getUsuario().getId(), loginBean.getNombre());
                                    loadAbandonos();
                                    PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                    System.out.println("Se ha pasado la solicitud " + notificacion.getSolicitud() + " de Abandonos a Notificaciones");
                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "EL ABANDONO SE HA PASADO A NOTIFICACIONES SATISFACTORIAMENTE");
                                } else {
                                    PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE HA PODIDO REMOVER LA ABANDONO");
                                }
                            } else {
                                PrimeFaces.current().ajax().addCallbackParam("saved", false);
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR, INTÉNTELO MÁS TARDE.");
                            }
                        }
                    }
                } else {
                    //Editar Abandono
                    if (c.validarExistenciaAbandono(abandono)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                    } else {

                        if (roselectable) {
                            abandono.setRo(razon);
                        }
                        abandono.setSolicitud(abandono.getSolicitud().toUpperCase());
                        if (c.updateAbandono(abandono)) {
                            c.saveHistorial("ABANDONO", "ABANDONO", abandono.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            loadAbandonos();
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANDONO EDITADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA ABANDONO");
                        }
                    }
                }
            } else {
                //Guardar Abandono
                if (c.existeTramiteAbando(abandono.getSolicitud())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                } else {
                    ModificacionApp mapp = c.getModificacionApp(abandono.getSolicitud());
                    if (mapp.getId() != null) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE NO SE PUEDE REGISTRAR: " + mapp.getObservacion());
                    } else {
                        boolean habilitado = true;
                        if (abandono.getDenominacion() != null && !abandono.getDenominacion().trim().isEmpty()
                                && abandono.getRegistro() != null && !abandono.getRegistro().trim().isEmpty()) {
                            if (c.existsTituloCanceladoByTituloAndDenominacion(abandono.getRegistro(), abandono.getDenominacion(), false)) {
                                TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(abandono.getRegistro(), abandono.getDenominacion());
                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN '"
                                            + abandono.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    abandono = new Abandono();
                                    habilitado = false;
                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                    abandono.setCancelado("PARCIAL");
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN '"
                                            + abandono.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN '"
                                            + abandono.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    abandono = new Abandono();
                                    habilitado = false;
                                }
                            } else {
                                habilitado = true;
                            }
                        }
                        if (habilitado) {
                            abandono.setNumeroAbandono(c.getNextNumeroAbandono(new Date()));
                            if (c.saveAbandono(abandono)) {
                                c.saveModificacionApp(abandono.getDenominacion(), abandono.getRegistro(), abandono.getSolicitud(), "ABANDONO", loginBean.getNombre());
                                c.saveHistorial("ABANDONO", "ABANDONO", abandono.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                                loadAbandonos();
                                PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANDONO GUARDADO CON ÉXITO");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL GUARDAR EL ABANDONO");
                            }
                        }
                    }
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararDescarga(ActionEvent ae) {
        FacesMessage msg = null;
        abandono = (Abandono) abandonosDataTable.getRowData();
        if (abandono != null && abandono.getId() != null) {
            if (abandono.getSolicitante() != null && !abandono.getSolicitante().trim().isEmpty()) {
                if (abandono.getRegistro() != null && !abandono.getRegistro().trim().isEmpty()) {
                    if (Operaciones.validarFecha(abandono.getFechaRegistro())) {
                        Controlador c = new Controlador();
                        List<Rooptions> roosaux = c.getRosBySolicitud(abandono.getSolicitud());
                        if (roosaux.isEmpty()) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "DEBE INGRESAR UN MOTIVO DE NOTIFICACIÓN");
                        } else {
                            loginBean.setAbandono(abandono);
                            loginBean.setVarious(false);
                            System.out.println("envía abandono descargar");
                            PrimeFaces.current().ajax().addCallbackParam("doit", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANDONO PREPARADO PARA DESCARGA");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL ABANDONO " + abandono.getSolicitud() + " NO POSEE FECHA DE REGISTRO");
                    }

                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL ABANDONO " + abandono.getSolicitud() + " NO POSEE NÚMERO DE REGISTRO");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "DEBE INGRESAR UN SOLICITANTE VÁLIDO PARA EL TRÁMITE " + abandono.getSolicitud());
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE LA NOTIFICACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void downloadSelected(ActionEvent ae) {
        FacesMessage msg = null;
//      RequestContext context = RequestContext.getCurrentInstance();
        if (!selectedAbandonos.isEmpty()) {

            System.out.println("Descargando Múltiples Abandonos... " + selectedAbandonos.size());

            boolean flag = true;
            Controlador c = new Controlador();
            String msj = "";
            for (int i = 0; i < selectedAbandonos.size(); i++) {
                Abandono abandonoaux = selectedAbandonos.get(i);
                if (abandonoaux != null && abandonoaux.getId() != null) {
                    if (abandonoaux.getSolicitante() != null && !abandonoaux.getSolicitante().trim().isEmpty()) {
                        if (abandonoaux.getRegistro() != null && !abandonoaux.getRegistro().trim().isEmpty()) {
                            if (Operaciones.validarFecha(abandonoaux.getFechaRegistro())) {
                                    List<Rooptions> roosaux = c.getRosBySolicitud(abandonoaux.getSolicitud());
                                    if (roosaux.isEmpty()) {
                                        flag = false;
                                        msj = "DEBE INGRESAR UN MOTIVO DE NOTIFICACIÓN PARA EL TRÁMITE " + abandonoaux.getSolicitud();
                                        break;
                                    }

                            } else {
                                flag = false;
                                msj = "EL ABANDONO " + abandonoaux.getSolicitud() + " NO POSEE FECHA DE REGISTRO";
                                break;
                            }
                        } else {
                            flag = false;
                            msj = "EL ABANDONO " + abandonoaux.getSolicitud() + " NO POSEE NÚMERO DE REGISTRO";
                            break;
                        }
                    } else {
                        flag = false;
                        msj = "DEBE INGRESAR UN SOLICITANTE VÁLIDO PARA EL TRÁMITE " + abandonoaux.getSolicitud();
                        break;
                    }
                } else {
                    flag = false;
                    msj = "NO SE CARGÓ CORRECTAMENTE LA NOTIFICACIÓN";
                    break;
                }
            }
            if (flag) {
                loginBean.setAbandonos(selectedAbandonos);
                loginBean.setVarious(true);
                System.out.println("envía abandonos seleccionados a descargar");
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANDONOS CARGADOS PARA DESCARGA, ESPERE...");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", msj);
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN SELECCIÓN", "SELECCIONE AL MENOS UN REGISTRO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {
        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
        abandono = (Abandono) abandonosDataTable.getRowData();
        if (abandono != null) {
            Controlador c = new Controlador();
            abandono = c.getAbandonoBySolSenadi(abandono.getSolicitud());
            c.refreshAbandono(abandono);

            roselectable = false;
            dialogTitle = "EDITAR ABANDONO " + abandono.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar el Abandono: " + abandono.getSolicitud() + "?";

            RenewalForm rf = c.getRenewalFormsByApplicationNumber(abandono.getSolicitud());
            if (rf.getId() != null) {
                abandono.setIdRenewalForm(rf.getId());
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANBDONO CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR ABANDONO");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarCasillero(ActionEvent ae) {
        if (abandono != null && abandono.getId() != null) {
            Controlador c = new Controlador();
            abandono.setCasilleroSenadi(c.buscarCasilleroBySolicitud(abandono.getSolicitud()));
        }
    }

    public void prepararShowRo(ActionEvent ae) {
        if (abandono != null) {
            Controlador c = new Controlador();
            if (abandono.getId() != null) {
                roos = c.getRosBySolicitud(abandono.getSolicitud());
            } else {
                roos = new ArrayList<>();
            }
            roshow = "";
            for (int i = 0; i < roos.size(); i++) {
                roshow += roos.get(i).getRo();
            }
            PrimeFaces.current().ajax().addCallbackParam("viewro", true);
        } else {
            PrimeFaces.current().ajax().addCallbackParam("viewro", false);
        }
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (abandono != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + abandono.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(abandono.getIdRenewalForm(), abandono.getSolicitud());
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

    public void validaSeleccion() {
        if (roselectable) {
            roChoose = "";
            razon = "";
        } else {
            razon = "";
            roChoose = "";
        }
    }

    public void eliminarRo(ActionEvent ae) {
        FacesMessage msg = null;
        if (abandono != null) {
            Rooptions roo = (Rooptions) roDataTable.getRowData();
            if (roo != null) {
                Controlador c = new Controlador();
                if (c.removeRooptios(roo)) {
                    roos = c.getRosBySolicitud(abandono.getSolicitud());
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "RO ELIMINADO CORRECTAMENTE");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO ELIMINAR EL RO");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE EL RO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE EL RO");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void agregarRo(ActionEvent ae) {
        FacesMessage msg = null;
        if (abandono != null) {

            if (roselectable && razon != null && !razon.trim().isEmpty()) {
                Rooptions ro = new Rooptions();
                ro.setRo(razon);
                ro.setFecha(new Date());
                ro.setSolicitud(abandono.getSolicitud());
                Controlador c = new Controlador();
                if (c.saveRooptios(ro)) {
                    roos = c.getRosBySolicitud(abandono.getSolicitud());
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "RO GUARDADO");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GUARDAR EL RO");
                }
            } else {
                if (roChoose != null && !roChoose.trim().isEmpty()) {
                    Rooptions ro = new Rooptions();
                    ro.setRo(roChoose);
                    ro.setFecha(new Date());
                    ro.setSolicitud(abandono.getSolicitud());
                    Controlador c = new Controlador();
                    if (c.saveRooptios(ro)) {
                        roos = c.getRosBySolicitud(abandono.getSolicitud());
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "RO GUARDADO");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GUARDAR EL RO");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GUARDAR EL RO");
                }
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ EL ABANDONO CORRECTAMENTE");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararViewRo(ActionEvent ae) {
        abandono = (Abandono) abandonosDataTable.getRowData();
        if (abandono != null) {
            Controlador c = new Controlador();
            roos = c.getRosBySolicitud(abandono.getSolicitud());
            roshow = "";
            for (int i = 0; i < roos.size(); i++) {
                roshow += roos.get(i).getRo();
            }
            PrimeFaces.current().ajax().addCallbackParam("viewro", true);
        } else {
            PrimeFaces.current().ajax().addCallbackParam("viewro", false);
        }
    }

    public void eliminarAbandono(ActionEvent ae) {
        FacesMessage msg = null;
        abandono = (Abandono) abandonosDataTable.getRowData();
        if (abandono != null) {
            Controlador c = new Controlador();
            if (c.removeAbandono(abandono)) {
                c.saveHistorial("ABANDONO", "ABANDONO", abandono.getSolicitud(), "ELIMINADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                loadAbandonos();
                System.out.println("Abandono " + abandono.getSolicitud() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANDONO " + abandono.getSolicitud() + "ELIMINADO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR ABANDONO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR ABANDONO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        abandono = (Abandono) abandonosDataTable.getRowData();
        if (abandono != null) {
            dialogTitle = "SEGUIMIENTO " + abandono.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(abandono.getSolicitud());
            setHistorial("");
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: ABANDONO";
            }
        }
    }

    public void onTipoAbandonoSelectedListener() {
        System.out.println(estadoTemp);
        if (estadoTemp != null && estadoTemp.equals("NOTIFICADAS")) {
            saveEdit = "ENVIAR";
        } else {
            saveEdit = "EDITAR";
        }
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
     * @return the abandonos
     */
    public List<Abandono> getAbandonos() {
        return abandonos;
    }

    /**
     * @param abandonos the abandonos to set
     */
    public void setAbandonos(List<Abandono> abandonos) {
        this.abandonos = abandonos;
    }

    /**
     * @return the abandonosFiltradas
     */
    public List<Abandono> getAbandonosFiltradas() {
        return abandonosFiltradas;
    }

    /**
     * @param abandonosFiltradas the abandonosFiltradas to set
     */
    public void setAbandonosFiltradas(List<Abandono> abandonosFiltradas) {
        this.abandonosFiltradas = abandonosFiltradas;
    }

    /**
     * @return the abandonosDataTable
     */
    public UIData getAbandonosDataTable() {
        return abandonosDataTable;
    }

    /**
     * @param abandonosDataTable the abandonosDataTable to set
     */
    public void setAbandonosDataTable(UIData abandonosDataTable) {
        this.abandonosDataTable = abandonosDataTable;
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
     * @return the abandono
     */
    public Abandono getAbandono() {
        return abandono;
    }

    /**
     * @param abandono the abandono to set
     */
    public void setAbandono(Abandono abandono) {
        this.abandono = abandono;
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
     * @return the roRazones
     */
    public List<String> getRoRazones() {
        return roRazones;
    }

    /**
     * @param roRazones the roRazones to set
     */
    public void setRoRazones(List<String> roRazones) {
        this.roRazones = roRazones;
    }

    /**
     * @return the razon
     */
    public String getRazon() {
        return razon;
    }

    /**
     * @param razon the razon to set
     */
    public void setRazon(String razon) {
        this.razon = razon;
    }

    /**
     * @return the roselectable
     */
    public boolean isRoselectable() {
        return roselectable;
    }

    /**
     * @param roselectable the roselectable to set
     */
    public void setRoselectable(boolean roselectable) {
        this.roselectable = roselectable;
    }

    /**
     * @return the selectedAbandonos
     */
    public List<Abandono> getSelectedAbandonos() {
        return selectedAbandonos;
    }

    /**
     * @param selectedAbandonos the selectedAbandonos to set
     */
    public void setSelectedAbandonos(List<Abandono> selectedAbandonos) {
        this.selectedAbandonos = selectedAbandonos;
    }

    /**
     * @return the roos
     */
    public List<Rooptions> getRoos() {
        return roos;
    }

    /**
     * @param roos the roos to set
     */
    public void setRoos(List<Rooptions> roos) {
        this.roos = roos;
    }

    /**
     * @return the roDataTable
     */
    public UIData getRoDataTable() {
        return roDataTable;
    }

    /**
     * @param roDataTable the roDataTable to set
     */
    public void setRoDataTable(UIData roDataTable) {
        this.roDataTable = roDataTable;
    }

    /**
     * @return the roChoose
     */
    public String getRoChoose() {
        return roChoose;
    }

    /**
     * @param roChoose the roChoose to set
     */
    public void setRoChoose(String roChoose) {
        this.roChoose = roChoose;
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
     * @return the roshow
     */
    public String getRoshow() {
        return roshow;
    }

    /**
     * @param roshow the roshow to set
     */
    public void setRoshow(String roshow) {
        this.roshow = roshow;
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
     * @return the paraEnviar
     */
    public boolean isParaEnviar() {
        return paraEnviar;
    }

    /**
     * @param paraEnviar the paraEnviar to set
     */
    public void setParaEnviar(boolean paraEnviar) {
        this.paraEnviar = paraEnviar;
    }

}
