/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.bean;

import java.io.Serializable;
import java.sql.Timestamp;
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
import senadi.gob.ec.transfweb.model.TituloCancelado;
import senadi.gob.ec.transfweb.model.Transferencia;
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepdep.PatentForms;
import senadi.gob.ec.transfweb.model.iepform.ModificacionApp;
import senadi.gob.ec.transfweb.model.iepform.PaymentReceipt;
import senadi.gob.ec.transfweb.model.iepform.Person;
import senadi.gob.ec.transfweb.model.iepform.PersonRenewal;
import senadi.gob.ec.transfweb.model.iepform.PersonRenewalName;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepform.Types;
import senadi.gob.ec.transfweb.modelp.PpdiPersona;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudPatente;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.modelp.PpdiTituloPatente;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import senadi.gob.ec.transfweb.modpat.TransferenciaPat;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author michael
 */
@ManagedBean(name = "transfrezBean")
@ViewScoped
public class TransfRezagoBean implements Serializable {

    private List<Transferencia> transferencias;
    private List<Transferencia> transferenciasFiltradas;
    private List<Transferencia> selectedTransferencias;
    private UIData transferenciasDataTable;

    private Transferencia transferencia;
    private String dialogTitle;

    private String numRegistros;
    private String criterio;
    private Date fechaInicio;
    private Date fechaFin;

    private Integer valorProgessBar;

    private List<Documento> archivos;

    private String tramitesLog;

    private LoginBean loginBean;
    private boolean botonpasar;

    public TransfRezagoBean() {
        loadTransferenciasRezagadas();
    }

    private void loadTransferenciasRezagadas() {
        transferencias = new ArrayList<>();
        Controlador c = new Controlador();
        tramitesLog = "";
        loginBean = c.getLogin();
    }

    public void longRunning() throws InterruptedException {
        valorProgessBar = 0;
//        Integer k = valorProgessBar;
        if (!selectedTransferencias.isEmpty()) {
            Controlador c = new Controlador();
            for (int i = 0; i < selectedTransferencias.size(); i++) {

                Transferencia transferaux = selectedTransferencias.get(i);
                TransferenciaPat transfpat = new TransferenciaPat();

                if (c.validarExistenciaTransferencia(transferaux.getSolicitud())) {
                    tramitesLog += transferaux.getSolicitud() + "\n";
                } else {
                    RenewalForm rf = c.getRenewalFormsByApplicationNumber(transferaux.getSolicitud());

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

                                        transferaux.setComprobante(payment.getVoucherNumber());
                                        transferaux.setFechaPresentacion(rf.getApplicationDate());
//                                transferencia.setCertificado(c.getNextNumeroCertificadoTransferencia());

                                        transferaux.setSigno(ttp.getAlias());
                                        transferaux.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");
                                        transferaux.setIdRenewalForm(rf.getId());

                                        if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                            HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                            if (hf.getId() != null) {
                                                transferaux.setDenominacion(hf.getDenomination());
                                                transferaux.setRegistro(hf.getExpedient());

                                                if (transferaux.getRegistro() != null && !transferaux.getRegistro().trim().isEmpty()) {
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(transferaux.getRegistro(), transferaux.getDenominacion());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        transferaux.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        transferaux.setTitularAnterior(titulo.getTitular());
                                                    }
                                                }
                                                if (transferaux.getFechaRegistro() == null) {
                                                    if (hf.getExpYear() != null && !hf.getExpYear().trim().isEmpty()) {
                                                        transferaux.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                    }
                                                }
                                            }
                                        } else {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                if (ps.getCodigoSolicitudSigno() != null) {
                                                    transferaux.setDenominacion(ps.getDenominacionSigno());
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        transferaux.setRegistro(titulo.getNumeroTitulo());
                                                        transferaux.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        transferaux.setTitularAnterior(titulo.getTitular());
                                                    }
                                                }
                                                if (transferaux.getTitularAnterior() == null || transferaux.getTitularAnterior().trim().isEmpty()) {
                                                    PpdiPersona persona = c.getASolicitanteByCodigoSolicitud(ps.getCodigoSolicitudSigno());
                                                    if (persona.getCodigoPersona() != null) {
                                                        transferaux.setTitularAnterior(persona.getNombrePersona());
                                                    }
                                                }
                                            }
                                        }

                                        Person titAct = c.getTitularActual(rf.getId());
                                        if (titAct.getId() != null) {
                                            transferaux.setTitularActual(titAct.getName());
                                            transferaux.setDomicilioTitularActual(titAct.getAddress());
                                            transferaux.setIdentificacion(titAct.getIdentificationNumber());
                                        }

                                        Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "AGENT");
                                        if (apoder.getId() != null) {
                                            transferaux.setApoderadoRepresentanteLegal(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "AGENT"));
                                        }

                                        if (transferaux.getRegistro() != null && !transferaux.getRegistro().trim().isEmpty()) {
                                            if (transferaux.getDenominacion() != null && !transferaux.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(transferaux.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(transferaux.getRegistro(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            tramitesLog += "TÍTULO DE " + transferaux.getSolicitud() + "\n";
                                                            transferaux = new Transferencia();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            transferaux.setCancelado(titca.getTipoCancelacion());
                                                            tramitesLog += "TÍTULO DE " + transferaux.getSolicitud() + "\n";
                                                        } else {
                                                            tramitesLog += "TÍTULO DE " + transferaux.getSolicitud() + "\n";
                                                            transferaux = new Transferencia();
                                                        }
                                                    } else {
                                                        System.out.println("transferencia: " + transferaux.getSolicitud() + " CARGADA CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(transferaux.getRegistro(), transferaux.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(transferaux.getRegistro(), transferaux.getDenominacion());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            tramitesLog += "TÍTULO DE " + transferaux.getSolicitud() + "\n";
                                                            transferaux = new Transferencia();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            transferaux.setCancelado(titca.getTipoCancelacion());
                                                            tramitesLog += "TÍTULO DE " + transferaux.getSolicitud() + "\n";
                                                        } else {
                                                            tramitesLog += "TÍTULO DE " + transferaux.getSolicitud() + "\n";
                                                            transferaux = new Transferencia();
                                                        }
                                                    } else {
                                                        System.out.println("transferencia: " + transferaux.getSolicitud() + " CARGADA CORRECTAMENTE");
                                                    }
                                                }
                                            } else {
                                                System.out.println(transferaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                            }
                                        } else {
                                            System.out.println(transferaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                        }
                                    } else {
                                        System.out.println(transferaux.getSolicitud() + ": TRÁMITE ENCONTRADO PERO NO ES UNA TRANSFERENCIA, SINO '" + t.getName().toUpperCase() + "'");
                                    }
                                } else {
                                    System.out.println("Estoy ingresando en patentes");
                                    //System.out.println(transferaux.getSolicitud()+": EL TRÁMITE ES UN " + ttp.getName().toUpperCase());
//                                    tramitesLog += transferaux.getSolicitud() + ": EL TRÁMITE ES UN " + ttp.getName().toUpperCase() + "\n";
                                    transferaux = new Transferencia();
                                    transfpat.setSolicitud(rf.getApplicationNumber());

                                    if (t.getName().trim().toLowerCase().contains("transferencia")
                                            || t.getName().trim().toLowerCase().contains("transmisión")) {

                                        PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                        transfpat.setComprobante(payment.getVoucherNumber());
                                        transfpat.setFechaPresentacion(rf.getApplicationDate());
//                                transferencia.setCertificado(c.getNextNumeroCertificadoTransferencia());

                                        transfpat.setSigno(ttp.getName().toUpperCase());
                                        transfpat.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");
                                        transfpat.setIdRenewalForm(rf.getId());

                                        if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                            PatentForms pf = c.getPatentFormsDepurada(rf.getDebugId());

                                            if (pf.getId() != null) {
                                                transfpat.setDenominacion(pf.getTitle());
                                                transfpat.setRegistro(pf.getExpedient());
                                                //transferencia.setClas
                                                if (pf.getExpYear() != null && !pf.getExpYear().trim().isEmpty()) {
                                                    transfpat.setFechaRegistro(Operaciones.convertStringToDate(pf.getExpYear() + "-01-01"));
                                                }

                                                if (transfpat.getRegistro() != null && !transfpat.getRegistro().trim().isEmpty()) {

                                                    PpdiTituloPatente titulo = c.getPpdiTituloPatenteByNumeroTitulo(transfpat.getRegistro());
                                                    if (titulo.getCodigoSolicitudPatente() != null) {
                                                        transfpat.setFechaRegistro(titulo.getFechaEmisionDocumento());
//                                                    transferencia.setTitularAnterior(titulo.getTitular());
                                                    }

                                                    PpdiSolicitudPatente patsol = c.getPpdiSolicitudPatenteByTramite(transfpat.getSolicitud());
                                                    if (patsol.getCodigoSolicitudPatente() != null) {
                                                        transfpat.setClaseInternacional(patsol.getClasificacionInternacional());
                                                    }
                                                }
                                            }

                                        } else {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                PpdiSolicitudPatente psp = c.getPpdiSolicitudPatenteByExpedient(rf.getExpedient());
                                                if (psp.getCodigoSolicitudPatente() != null) {
                                                    transfpat.setDenominacion(psp.getTitulo());
                                                    PpdiTituloPatente titulo = c.getPpdiTituloPatenteByCodigoSolicitudPatente(psp.getCodigoSolicitudPatente());
                                                    if (titulo.getCodigoSolicitudPatente() != null) {
                                                        transfpat.setRegistro(titulo.getNumeroTitulo());
                                                        transfpat.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        transfpat.setTitularAnterior("");
                                                    }
                                                }
                                                if (transfpat.getTitularAnterior() == null || transfpat.getTitularAnterior().trim().isEmpty()) {
                                                    String personas = c.getPersonasByTipoPersona(psp, "SOLICITANTE");
                                                    if (!personas.isEmpty()) {
                                                        transfpat.setTitularAnterior(personas);
                                                    }
                                                }
                                            }
                                        }

                                        //apoderado
                                        List<PersonRenewal> apoderados = c.getPersonRenewalByIdRenewalAndType(rf.getId(), "'AGENT','ATTORNEY'");
                                        if (!apoderados.isEmpty()) {
                                            transfpat.setApoderadoRepresentanteLegal(c.getNamesFromPersonRenewal(apoderados));
                                        }

                                        //solicitante
                                        List<PersonRenewalName> tit_actuales = c.getTitularesActuales(rf.getId());
                                        if (!tit_actuales.isEmpty()) {
                                            transfpat.setTitularActual(c.getNamesFromPersonRenewalName(tit_actuales, false));
                                            transfpat.setDomicilioTitularActual(c.getAddressFromPersonRenewalName(tit_actuales, false));
                                            transfpat.setIdentificacion(c.getIdentificationFromPersonRenewalName(tit_actuales));
                                        }
                                        if (transfpat.getRegistro() != null && !transfpat.getRegistro().trim().isEmpty()) {
                                            if (transfpat.getDenominacion() != null && !transfpat.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(transfpat.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(transfpat.getRegistro(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            tramitesLog += "TÍTULO DE " + transfpat.getSolicitud() + " CANCELADO\n";
                                                            transfpat = new TransferenciaPat();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            transfpat.setCancelado(titca.getTipoCancelacion());
                                                            tramitesLog += "TÍTULO DE " + transfpat.getSolicitud() + " CANCELADO\n";
                                                        } else {
                                                            tramitesLog += "TÍTULO DE " + transfpat.getSolicitud() + " CANCELADO\n";
                                                            transfpat = new TransferenciaPat();
                                                        }
                                                    } else {
                                                        System.out.println("DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(transfpat.getRegistro(), transfpat.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(transfpat.getRegistro(), transfpat.getDenominacion());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            tramitesLog += "TÍTULO DE " + transfpat.getSolicitud() + " CANCELADO\n";
                                                            transfpat = new TransferenciaPat();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            transfpat.setCancelado(titca.getTipoCancelacion());
                                                            tramitesLog += "TÍTULO DE " + transfpat.getSolicitud() + " CANCELADO\n";
                                                        } else {
                                                            tramitesLog += "TÍTULO DE " + transfpat.getSolicitud() + " CANCELADO\n";
                                                            transfpat = new TransferenciaPat();
                                                        }
                                                    } else {
                                                        System.out.println("DATOS CARGADOS CORRECTAMENTE PATENTE");
                                                    }
                                                }
                                            } else {
                                                System.out.println(transfpat.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                            }
                                        } else {
                                            System.out.println(transfpat.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                        }
                                    } else {
                                        System.out.println(transfpat.getSolicitud() + ": TRÁMITE ENCONTRADO PERO NO ES UNA TRANSFERENCIA, SINO '" + t.getName().toUpperCase() + "'");
                                    }
                                }
                            } else {
                                tramitesLog += transfpat.getSolicitud() + ": EL TRÁMITE PRESENTA UN PROBLEMA DE IDENTIDAD\n";
//                                System.out.println(transferaux.getSolicitud()+": EL TRÁMITE PRESENTA UN PROBLEMA DE IDENTIDAD");
                            }
                        } else {
                            //System.out.println(transferaux.getSolicitud()+": TRÁMITE ENCONTRADO, PERO NO REGISTRA INICIO DE PROCESO");
                            tramitesLog += transfpat.getSolicitud() + ":TRÁMITE ENCONTRADO, PERO NO REGISTRA INICIO DE PROCESO\n";
                        }
                    }
                    if (transferaux.getSolicitud() != null) {
                        transferaux.setFechaCertificado(new Date());
                        transferaux.setCertificado(c.getNextNumeroCertificadoTransferencia());
                        transferaux.setSolicitud(transferaux.getSolicitud().trim().toUpperCase());
                        transferaux.setResponsable(loginBean.getUsuario().getAlias());
                        if (c.saveTransferencia(transferaux)) {
                            ModificacionApp mapp = new ModificacionApp();
                            mapp.setDenominacion(transferaux.getDenominacion() != null ? transferaux.getDenominacion() : "");
                            mapp.setFecha(new Timestamp(new Date().getTime()));
                            mapp.setObservacion("rezago pasado");
                            mapp.setRegistro(transferaux.getRegistro());
                            mapp.setSolicitud(transferaux.getSolicitud());
                            mapp.setTipo("TRANSFERENCIA");
                            mapp.setUsuario(loginBean.getNombre());
                            mapp.setModo("MARCA");
                            mapp.setActivo(true);

                            if (c.saveModificacionApp(mapp)) {
                                c.saveHistorial("TRANSFERENCIA", "TRANSFERENCIA", transferaux.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            }
                        }
                    } else if (transfpat.getSolicitud() != null) {
                        transfpat.setFechaCertificado(new Date());
                        transfpat.setCertificado(c.getNextNumeroCertificadoTransferenciaPat());
                        transfpat.setSolicitud(transfpat.getSolicitud().trim().toUpperCase());
                        transfpat.setResponsable(loginBean.getUsuario().getAlias());
                        if (c.saveTransferenciaPat(transfpat)) {
                            ModificacionApp mapp = new ModificacionApp();
                            mapp.setDenominacion(transfpat.getDenominacion() != null ? transfpat.getDenominacion() : "");
                            mapp.setFecha(new Timestamp(new Date().getTime()));
                            mapp.setObservacion("rezago pasado");
                            mapp.setRegistro(transfpat.getRegistro());
                            mapp.setSolicitud(transfpat.getSolicitud());
                            mapp.setTipo("TRANSFERENCIA");
                            mapp.setUsuario(loginBean.getNombre());
                            mapp.setModo("PATENTE");
                            mapp.setActivo(true);
                            if (c.saveModificacionApp(mapp)) {
                                c.saveHistorialPat("CAMBIO DE NOMBRE", "CAMBIO DE NOMBRE", transfpat.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            }
                        }
                    }
                }
                int n = i + 1;
                valorProgessBar = (n * 100) / selectedTransferencias.size();
//                System.out.println("valorprog: " + valorProgessBar);
                //Thread.sleep(500);
            }
            valorProgessBar = 100;
        }
        valorProgessBar = 100;
    }

    public void onComplete() {
        loadTransferenciasRezagadas();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("SE HAN IMPORTADO CORRECTAMENTE LAS TRANSFERENCIAS"));
    }

    public void cargarRezago(ActionEvent ae) {
        Controlador c = new Controlador();
        List<RenewalForm> renewals = c.getRenewalsRezagoBytType("TRANSFERENCIA", "23,24");
        transferencias = c.loadTransferenciasFromRenewals(renewals);
        botonpasar = !transferencias.isEmpty();
        numRegistros = "Número Registros Mostrados: " + transferencias.size();
    }

    public void buscarRezagoTransfCriterio(ActionEvent ae) {
        FacesMessage msg = null;
        if (!criterio.trim().isEmpty() && criterio.trim().length() > 3) {
            Controlador c = new Controlador();
            List<RenewalForm> renewals = c.getRenewalsRezagoBytTypeAndCriterio("TRANSFERENCIA", "23,24", criterio.trim().toUpperCase());
            transferencias = c.loadTransferenciasFromRenewals(renewals);
            botonpasar = !transferencias.isEmpty();
            numRegistros = "Número Registros Mostrados: " + transferencias.size();
            if (transferencias.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO VÁLIDO (MÁS DE 3 CARACTERES)");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarTransferenciasPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            List<RenewalForm> renewals = c.getRenewalsRezagoBytTypeAndFecha("TRANSFERENCIA", "23", fechaInicio, fechaFin);
            transferencias = c.loadTransferenciasFromRenewals(renewals);
            botonpasar = !transferencias.isEmpty();
            numRegistros = "Número Registros Mostrados: " + transferencias.size();
            if (transferencias.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN RANGO DE FECHAS VÁLIDO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg;
        transferencia = (Transferencia) transferenciasDataTable.getRowData();
//        System.out.println("hereeeeeeee");
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

    public boolean validarFechas() {
        try {
            fechaInicio.toString();
            fechaFin.toString();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void quitarTramites(ActionEvent ae) {
        FacesMessage msg;
        if (selectedTransferencias != null && !selectedTransferencias.isEmpty()) {
            Controlador c = new Controlador();
            for (int i = 0; i < selectedTransferencias.size(); i++) {

                Transferencia tran = selectedTransferencias.get(i);

                ModificacionApp map = new ModificacionApp();
                map.setDenominacion("");
                map.setFecha(new Timestamp(new Date().getTime()));
                map.setObservacion(tran.getR2());
                map.setRegistro(tran.getRegistro());
                map.setSolicitud(tran.getSolicitud());
                map.setTipo("TRANSFERENCIA");
                map.setUsuario(loginBean.getNombre());
                map.setModo(tran.getR1());
                map.setActivo(false);
                c.saveModificacionApp(map);
            }

            loadTransferenciasRezagadas();
            PrimeFaces.current().ajax().addCallbackParam("quitado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS SELECCIONADOS FUERON REMOVIDOS DE REZAGO");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararQuitar(ActionEvent ae) {
        FacesMessage msg;
        if (selectedTransferencias != null && !selectedTransferencias.isEmpty()) {
            for (int i = 0; i < selectedTransferencias.size(); i++) {
                selectedTransferencias.get(i).setR2("El trámite no pertenece a la Dirección de Modificaciones");
            }
            dialogTitle = "QUITAR DE REZAGADOS " + selectedTransferencias.size() + " TRÁMITES?";
            PrimeFaces.current().ajax().addCallbackParam("pasado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS SELECCIONADOS FUERON  CARGADOS CORRECTAMENTE");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void pasarSeleccionados(ActionEvent ae) {
        FacesMessage msg;
        if (selectedTransferencias != null && !selectedTransferencias.isEmpty()) {
            dialogTitle = "PASAR " + selectedTransferencias.size() + " TRÁMITES A TRANSFERENCIAS?";
            PrimeFaces.current().ajax().addCallbackParam("pasado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS HAN SIDO CARGADOS CORRECTAMENTE");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
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
     * @return the valorProgessBar
     */
    public Integer getValorProgessBar() {
        return valorProgessBar;
    }

    /**
     * @param valorProgessBar the valorProgessBar to set
     */
    public void setValorProgessBar(Integer valorProgessBar) {
        this.valorProgessBar = valorProgessBar;
    }

    /**
     * @return the tramitesLog
     */
    public String getTramitesLog() {
        return tramitesLog;
    }

    /**
     * @param tramitesLog the tramitesLog to set
     */
    public void setTramitesLog(String tramitesLog) {
        this.tramitesLog = tramitesLog;
    }

    /**
     * @return the botonpasar
     */
    public boolean isBotonpasar() {
        return botonpasar;
    }

    /**
     * @param botonpasar the botonpasar to set
     */
    public void setBotonpasar(boolean botonpasar) {
        this.botonpasar = botonpasar;
    }

}
