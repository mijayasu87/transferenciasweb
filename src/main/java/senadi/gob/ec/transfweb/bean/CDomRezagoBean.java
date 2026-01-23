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
import senadi.gob.ec.transfweb.model.cd.CambioDomicilio;
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
import senadi.gob.ec.transfweb.modpat.CambioDomicilioPat;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author michael
 */
@ManagedBean(name = "cdomrezBean")
@ViewScoped
public class CDomRezagoBean implements Serializable {

    private List<CambioDomicilio> cambiosDomicilio;
    private List<CambioDomicilio> cambiosDomicilioFiltradas;
    private List<CambioDomicilio> selectedCambiosDomicilio;
    private UIData cambioDomicilioDataTable;

    private CambioDomicilio cambioDomicilio;
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

    public CDomRezagoBean() {
        loadCambiosDomicilioRezagadas();
    }

    private void loadCambiosDomicilioRezagadas() {
        cambiosDomicilio = new ArrayList<>();
        Controlador c = new Controlador();
        tramitesLog = "";
        loginBean = c.getLogin();
    }

    public void longRunning() throws InterruptedException {
        valorProgessBar = 0;
//        Integer k = valorProgessBar;
        if (!selectedCambiosDomicilio.isEmpty()) {
            Controlador c = new Controlador();
            for (int i = 0; i < selectedCambiosDomicilio.size(); i++) {

                CambioDomicilio camdomaux = selectedCambiosDomicilio.get(i);
                CambioDomicilioPat cdompat = new CambioDomicilioPat();

                if (c.existeCambioDomicilio(camdomaux.getSolicitud())) {
                    tramitesLog += "TRÁMITE "+camdomaux.getSolicitud() + " YA EXISTE EN CAMBIOS DE DOMICILIO\n";
                    System.out.println("tramite: "+tramitesLog);
                } else {
                    RenewalForm rf = c.getRenewalFormsByApplicationNumber(camdomaux.getSolicitud());

                    if (rf.getId() != null) {
                        if (rf.getStatus().equals("DELIVERED")) {

                            Types t = c.getTypes(rf.getTransactionMotiveId());

                            Types ttp = c.getTypes(rf.getFormId());

                            if (t.getId() != null && ttp.getId() != null) {
                                if (!ttp.getAlias().equals("PI") && !ttp.getAlias().equals("MU") && !ttp.getAlias().equals("DI")) {
                                    if (t.getName().trim().toLowerCase().contains("cambio de domicilio")) {

                                        PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                        camdomaux.setComprobante(payment.getVoucherNumber());
                                        camdomaux.setFechaPresentacion(rf.getApplicationDate());
                                        //certificado.setCertificado(c.getNextNumeroCertificadoCD());
//                                    certificado.setCertificado(c.getNextCambioNombreCertificado());

                                        camdomaux.setSigno(ttp.getAlias());
                                        camdomaux.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");
                                        camdomaux.setIdRenewalForm(rf.getId());

                                        if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                            HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                            if (hf.getId() != null) {
                                                camdomaux.setDenominacion(hf.getDenomination());
                                                camdomaux.setRegistro(hf.getExpedient());

                                                if (camdomaux.getRegistro() != null && !camdomaux.getRegistro().trim().isEmpty()) {
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(camdomaux.getRegistro(), camdomaux.getDenominacion());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        camdomaux.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        camdomaux.setTitularAnterior(titulo.getTitular());
                                                    }
                                                }
                                                if (camdomaux.getFechaRegistro() == null) {
                                                    camdomaux.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                }
                                            }
                                        } else {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                if (ps.getCodigoSolicitudSigno() != null) {
                                                    camdomaux.setDenominacion(ps.getDenominacionSigno());
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        camdomaux.setRegistro(titulo.getNumeroTitulo());
                                                        camdomaux.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        camdomaux.setTitularAnterior(titulo.getTitular());
                                                    }
                                                }
                                                if (camdomaux.getTitularAnterior() == null || camdomaux.getTitularAnterior().trim().isEmpty()) {
                                                    PpdiPersona persona = c.getASolicitanteByCodigoSolicitud(ps.getCodigoSolicitudSigno());
                                                    if (persona.getCodigoPersona() != null) {
                                                        camdomaux.setTitularAnterior(persona.getNombrePersona());
                                                    }
                                                }
                                            }
                                        }

                                        Person titAct = c.getTitularActual(rf.getId());
                                        if (titAct.getId() != null) {
                                            camdomaux.setTitularActual(titAct.getName());
                                            camdomaux.setDomicilioTitularActual(titAct.getAddress());
                                            camdomaux.setIdentificacion(titAct.getIdentificationNumber());
                                        }

                                        Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "AGENT");
                                        if (apoder.getId() != null) {
                                            camdomaux.setApeApodRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "AGENT"));
                                        }

                                        if (camdomaux.getRegistro() != null && !camdomaux.getRegistro().trim().isEmpty()) {
                                            if (camdomaux.getDenominacion() != null && !camdomaux.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(camdomaux.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(camdomaux.getRegistro(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            tramitesLog += "TÍTULO DE " + camdomaux.getSolicitud() + "\n";
                                                            camdomaux = new CambioDomicilio();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            camdomaux.setCancelado(titca.getTipoCancelacion());
                                                            tramitesLog += "TÍTULO DE " + camdomaux.getSolicitud() + "\n";
                                                        } else {
                                                            tramitesLog += "TÍTULO DE " + camdomaux.getSolicitud() + "\n";
                                                            camdomaux = new CambioDomicilio();
                                                        }
                                                    } else {
                                                        System.out.println("cambio de domicilio: " + camdomaux.getSolicitud() + " CARGADA CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(camdomaux.getRegistro(), camdomaux.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(camdomaux.getRegistro(), camdomaux.getDenominacion());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            tramitesLog += "TÍTULO DE " + camdomaux.getSolicitud() + "\n";
                                                            camdomaux = new CambioDomicilio();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            camdomaux.setCancelado(titca.getTipoCancelacion());
                                                            tramitesLog += "TÍTULO DE " + camdomaux.getSolicitud() + "\n";
                                                        } else {
                                                            tramitesLog += "TÍTULO DE " + camdomaux.getSolicitud() + "\n";
                                                            camdomaux = new CambioDomicilio();
                                                        }
                                                    } else {
                                                        System.out.println("cambio de domicilio: " + camdomaux.getSolicitud() + " CARGADA CORRECTAMENTE");
                                                    }
                                                }
                                            } else {
                                                System.out.println(camdomaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                            }
                                        } else {
                                            System.out.println(camdomaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                        }
                                    } else {
                                        System.out.println(camdomaux.getSolicitud() + ": TRÁMITE ENCONTRADO PERO NO ES UN CAMBIO DE DOMICILIO, SINO '" + t.getName().toUpperCase() + "'");
                                    }
                                } else {
                                    //poner aquí la parte de patentes

                                    camdomaux = new CambioDomicilio();
                                    cdompat.setSolicitud(rf.getApplicationNumber());
                                    if (t.getName().trim().toLowerCase().contains("cambio de domicilio")) {

                                        PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                        cdompat.setComprobante(payment.getVoucherNumber());
                                        cdompat.setFechaPresentacion(rf.getApplicationDate());
                                        //certificado.setCertificado(c.getNextNumeroCertificadoCD());
//                                    certificado.setCertificado(c.getNextCambioNombreCertificado());

                                        cdompat.setSigno(ttp.getName().toUpperCase());
                                        cdompat.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");
                                        cdompat.setIdRenewalForm(rf.getId());

                                        if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                            PatentForms pf = c.getPatentFormsDepurada(rf.getDebugId());

                                            if (pf.getId() != null) {
                                                cdompat.setDenominacion(pf.getTitle());
                                                cdompat.setRegistro(pf.getExpedient());

                                                if (pf.getExpYear() != null && !pf.getExpYear().trim().isEmpty()) {
                                                    cdompat.setFechaRegistro(Operaciones.convertStringToDate(pf.getExpYear() + "-01-01"));
                                                }

                                                if (cdompat.getRegistro() != null && !cdompat.getRegistro().trim().isEmpty()) {
                                                    PpdiTituloPatente titulo = c.getPpdiTituloPatenteByNumeroTitulo(cdompat.getRegistro());
                                                    if (titulo.getCodigoSolicitudPatente() != null) {
                                                        cdompat.setFechaRegistro(titulo.getFechaEmisionDocumento());
//                                                    certificado.setTitularAnterior(titulo.getTitular());
                                                    }

                                                    PpdiSolicitudPatente patsol = c.getPpdiSolicitudPatenteByTramite(cdompat.getSolicitud());
                                                    if (patsol.getCodigoSolicitudPatente() != null) {
                                                        cdompat.setClaseInternacional(patsol.getClasificacionInternacional());
                                                    }
                                                }
                                            }
                                        } else {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                PpdiSolicitudPatente ps = c.getPpdiSolicitudPatenteByExpedient(rf.getExpedient());
                                                if (ps.getCodigoSolicitudPatente() != null) {
                                                    cdompat.setDenominacion(ps.getTitulo());
                                                    PpdiTituloPatente titulo = c.getPpdiTituloPatenteByCodigoSolicitudPatente(ps.getCodigoSolicitudPatente());
                                                    if (titulo.getCodigoSolicitudPatente() != null) {
                                                        cdompat.setRegistro(titulo.getNumeroTitulo());
                                                        cdompat.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        cdompat.setTitularAnterior("");
                                                    }
                                                }
                                                if (cdompat.getTitularAnterior() == null || cdompat.getTitularAnterior().trim().isEmpty()) {
                                                    String personas = c.getPersonasByTipoPersona(ps, "SOLICITANTE");
                                                    if (!personas.isEmpty()) {
                                                        cdompat.setTitularAnterior(personas);
                                                    }
                                                }
                                            }
                                        }

                                        //titulares
                                        List<PersonRenewalName> tit_actuales = c.getTitularesActuales(rf.getId());
                                        if (!tit_actuales.isEmpty()) {
                                            cdompat.setTitularActual(c.getNamesFromPersonRenewalName(tit_actuales, false));
                                            cdompat.setDomicilioTitularActual(c.getAddressFromPersonRenewalName(tit_actuales, false));
                                            cdompat.setIdentificacion(c.getIdentificationFromPersonRenewalName(tit_actuales));
                                        }

                                        //apoderado
                                        List<PersonRenewal> apoderados = c.getPersonRenewalByIdRenewalAndType(rf.getId(), "'AGENT','ATTORNEY'");
                                        if (!apoderados.isEmpty()) {
                                            cdompat.setApeApodRepre(c.getNamesFromPersonRenewal(apoderados));
                                            cdompat.setEmail(c.getEmailsFromPersonRenewal(apoderados));
                                        }

                                        if (cdompat.getRegistro() != null && !cdompat.getRegistro().trim().isEmpty()) {
                                            if (cdompat.getDenominacion() != null && !cdompat.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(cdompat.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(cdompat.getRegistro(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            tramitesLog += "TÍTULO DE " + cdompat.getSolicitud() + " CANCELADO\n";
                                                            cdompat = new CambioDomicilioPat();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            cdompat.setCancelado(titca.getTipoCancelacion());
                                                            tramitesLog += "TÍTULO DE " + cdompat.getSolicitud() + " CANCELADO\n";
                                                        } else {
                                                            tramitesLog += "TÍTULO DE " + cdompat.getSolicitud() + " CANCELADO\n";
                                                            cdompat = new CambioDomicilioPat();
                                                        }
                                                    } else {
                                                        System.out.println("DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(cdompat.getRegistro(), cdompat.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(cdompat.getRegistro(), cdompat.getDenominacion());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            tramitesLog += "TÍTULO DE " + cdompat.getSolicitud() + " CANCELADO\n";
                                                            cdompat = new CambioDomicilioPat();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            cdompat.setCancelado(titca.getTipoCancelacion());
                                                            tramitesLog += "TÍTULO DE " + cdompat.getSolicitud() + " CANCELADO\n";
                                                        } else {
                                                            tramitesLog += "TÍTULO DE " + cdompat.getSolicitud() + " CANCELADO\n";
                                                            cdompat = new CambioDomicilioPat();
                                                        }
                                                    } else {
                                                        System.out.println("DATOS CARGADOS CORRECTAMENTE PATENTE");
                                                    }
                                                }
                                            } else {
                                                System.out.println(cdompat.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                            }
                                        } else {
                                            System.out.println(cdompat.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                        }

                                    } else {
                                        System.out.println(cdompat.getSolicitud() + ": TRÁMITE ENCONTRADO PERO NO ES UN CAMBIO DE DOMICILIO, SINO '" + t.getName().toUpperCase() + "'");
                                    }
                                }
                            } else {
                                tramitesLog += cdompat.getSolicitud() + ": EL TRÁMITE PRESENTA UN PROBLEMA DE IDENTIDAD\n";
                            }

                        } else {
                            tramitesLog += cdompat.getSolicitud() + ":TRÁMITE ENCONTRADO, PERO NO REGISTRA INICIO DE PROCESO\n";
                        }
                    }
                    if (camdomaux.getSolicitud() != null) {
                        camdomaux.setFechaCertificado(new Date());
                        camdomaux.setCertificado(c.getNextCambioDomicilioCertificado());
                        camdomaux.setSolicitud(camdomaux.getSolicitud().trim().toUpperCase());
                        camdomaux.setTipoEstado("CERTIFICADO");
                        camdomaux.setResponsable(loginBean.getUsuario().getAlias());
                        if (c.saveCambioDomicilio(camdomaux)) {
                            ModificacionApp mapp = new ModificacionApp();
                            mapp.setDenominacion(camdomaux.getDenominacion() != null ? camdomaux.getDenominacion() : "");
                            mapp.setFecha(new Timestamp(new Date().getTime()));
                            mapp.setObservacion("rezago pasado");
                            mapp.setRegistro(camdomaux.getRegistro());
                            mapp.setSolicitud(camdomaux.getSolicitud());
                            mapp.setTipo("CAMBIO DE DOMICILIO");
                            mapp.setUsuario(loginBean.getNombre());
                            mapp.setModo("MARCA");
                            mapp.setActivo(true);
//
                            if (c.saveModificacionApp(mapp)) {
                                c.saveHistorial("CAMBIO DE DOMICILIO", "CAMBIO DE DOMICILIO", camdomaux.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            }
                        }
                    } else if (cdompat.getSolicitud() != null) {
                        cdompat.setFechaCertificado(new Date());
                        cdompat.setCertificado(c.getNextCambioDomicilioPatCertificado());
                        cdompat.setSolicitud(cdompat.getSolicitud().trim().toUpperCase());
                        cdompat.setTipoEstado("CERTIFICADO");
                        cdompat.setResponsable(loginBean.getUsuario().getAlias());
                        if (c.saveCambioDomicilioPat(cdompat)) {
                            ModificacionApp mapp = new ModificacionApp();
                            mapp.setDenominacion(cdompat.getDenominacion() != null ? cdompat.getDenominacion() : "");
                            mapp.setFecha(new Timestamp(new Date().getTime()));
                            mapp.setObservacion("rezago pasado");
                            mapp.setRegistro(cdompat.getRegistro());
                            mapp.setSolicitud(cdompat.getSolicitud());
                            mapp.setTipo("CAMBIO DE DOMICILIO");
                            mapp.setUsuario(loginBean.getNombre());
                            mapp.setModo("PATENTE");
                            mapp.setActivo(true);
                            if (c.saveModificacionApp(mapp)) {
                                c.saveHistorialPat("CAMBIO DE DOMICILIO", "CAMBIO DE DOMICILIO", cdompat.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            }
                        }
                    }
                }
                int n = i + 1;
                valorProgessBar = (n * 100) / selectedCambiosDomicilio.size();
//                System.out.println("valorprog: " + valorProgessBar);
                //Thread.sleep(500);
            }
            valorProgessBar = 100;
        }
        valorProgessBar = 100;
    }

    public void onComplete() {
                
        if(tramitesLog.trim().isEmpty()){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("SE HAN IMPORTADO CORRECTAMENTE LOS CAMBIOS DE DOMICILIO"));
        }else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", tramitesLog));
        }
        loadCambiosDomicilioRezagadas();        
    }

    public void cargarRezago(ActionEvent ae) {
        Controlador c = new Controlador();
        List<RenewalForm> renewals = c.getRenewalsRezagoBytType("CAMBIO DE DOMICILIO", "26");
        cambiosDomicilio = c.loadCambiosDomicilioFromRenewals(renewals);
        botonpasar = !cambiosDomicilio.isEmpty();
        numRegistros = "Número Registros Mostrados: " + cambiosDomicilio.size();

    }

    public void buscarRezagoCambioDCriterio(ActionEvent ae) {
        FacesMessage msg = null;
        if (!criterio.trim().isEmpty() && criterio.trim().length() > 3) {
            Controlador c = new Controlador();
            List<RenewalForm> renewals = c.getRenewalsRezagoBytTypeAndCriterio("CAMBIO DE DOMICILIO", "26", criterio.trim().toUpperCase());
            cambiosDomicilio = c.loadCambiosDomicilioFromRenewals(renewals);
            botonpasar = !cambiosDomicilio.isEmpty();
            numRegistros = "Número Registros Mostrados: " + cambiosDomicilio.size();
            if (cambiosDomicilio.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO VÁLIDO (MÁS DE 3 CARACTERES)");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarCambiosDPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            List<RenewalForm> renewals = c.getRenewalsRezagoBytTypeAndFecha("CAMBIO DE DOMICILIO", "26", fechaInicio, fechaFin);
            cambiosDomicilio = c.loadCambiosDomicilioFromRenewals(renewals);
            botonpasar = !cambiosDomicilio.isEmpty();
            numRegistros = "Número Registros Mostrados: " + cambiosDomicilio.size();
            if (cambiosDomicilio.isEmpty()) {
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
        cambioDomicilio = (CambioDomicilio) cambioDomicilioDataTable.getRowData();
//        System.out.println("hereeeeeeee");
        if (cambioDomicilio != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + cambioDomicilio.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(cambioDomicilio.getIdRenewalForm(), cambioDomicilio.getSolicitud());
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
        if (selectedCambiosDomicilio != null && !selectedCambiosDomicilio.isEmpty()) {
            Controlador c = new Controlador();
            for (int i = 0; i < selectedCambiosDomicilio.size(); i++) {

                CambioDomicilio tran = selectedCambiosDomicilio.get(i);

                ModificacionApp map = new ModificacionApp();
                map.setDenominacion("");
                map.setFecha(new Timestamp(new Date().getTime()));
                map.setObservacion(tran.getRo());
                map.setRegistro(tran.getRegistro());
                map.setSolicitud(tran.getSolicitud());
                map.setTipo("CAMBIO DE DOMICILIO");
                map.setUsuario(loginBean.getNombre());
                map.setModo(tran.getObservacion());
                map.setActivo(false);
                c.saveModificacionApp(map);
            }

            loadCambiosDomicilioRezagadas();
            PrimeFaces.current().ajax().addCallbackParam("quitado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS SELECCIONADOS FUERON REMOVIDOS DE REZAGO");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararQuitar(ActionEvent ae) {
        FacesMessage msg;
        if (selectedCambiosDomicilio != null && !selectedCambiosDomicilio.isEmpty()) {
            for (int i = 0; i < selectedCambiosDomicilio.size(); i++) {
                selectedCambiosDomicilio.get(i).setRo("El trámite no pertenece a la Dirección de Modificaciones");
            }
            dialogTitle = "QUITAR DE REZAGADOS " + selectedCambiosDomicilio.size() + " TRÁMITES?";
            PrimeFaces.current().ajax().addCallbackParam("pasado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS SELECCIONADOS FUERON  CARGADOS CORRECTAMENTE");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void pasarSeleccionados(ActionEvent ae) {
        FacesMessage msg;
        if (selectedCambiosDomicilio != null && !selectedCambiosDomicilio.isEmpty()) {
            dialogTitle = "PASAR " + selectedCambiosDomicilio.size() + " TRÁMITES A CAMBIOS DE DOMICILIO?";
            PrimeFaces.current().ajax().addCallbackParam("pasado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS HAN SIDO CARGADOS CORRECTAMENTE");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return the cambiosDomicilio
     */
    public List<CambioDomicilio> getCambiosDomicilio() {
        return cambiosDomicilio;
    }

    /**
     * @param cambiosDomicilio the cambiosDomicilio to set
     */
    public void setCambiosDomicilio(List<CambioDomicilio> cambiosDomicilio) {
        this.cambiosDomicilio = cambiosDomicilio;
    }

    /**
     * @return the cambiosDomicilioFiltradas
     */
    public List<CambioDomicilio> getCambiosDomicilioFiltradas() {
        return cambiosDomicilioFiltradas;
    }

    /**
     * @param cambiosDomicilioFiltradas the cambiosDomicilioFiltradas to set
     */
    public void setCambiosDomicilioFiltradas(List<CambioDomicilio> cambiosDomicilioFiltradas) {
        this.cambiosDomicilioFiltradas = cambiosDomicilioFiltradas;
    }

    /**
     * @return the selectedCambiosDomicilio
     */
    public List<CambioDomicilio> getSelectedCambiosDomicilio() {
        return selectedCambiosDomicilio;
    }

    /**
     * @param selectedCambiosDomicilio the selectedCambiosDomicilio to set
     */
    public void setSelectedCambiosDomicilio(List<CambioDomicilio> selectedCambiosDomicilio) {
        this.selectedCambiosDomicilio = selectedCambiosDomicilio;
    }

    /**
     * @return the cambioDomicilioDataTable
     */
    public UIData getCambioDomicilioDataTable() {
        return cambioDomicilioDataTable;
    }

    /**
     * @param cambioDomicilioDataTable the cambioDomicilioDataTable to set
     */
    public void setCambioDomicilioDataTable(UIData cambioDomicilioDataTable) {
        this.cambioDomicilioDataTable = cambioDomicilioDataTable;
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
     * @return the cambioDomicilio
     */
    public CambioDomicilio getCambioDomicilio() {
        return cambioDomicilio;
    }

    /**
     * @param cambioDomicilio the cambioDomicilio to set
     */
    public void setCambioDomicilio(CambioDomicilio cambioDomicilio) {
        this.cambioDomicilio = cambioDomicilio;
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
