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
import senadi.gob.ec.transfweb.model.cn.CambioNombre;
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
import senadi.gob.ec.transfweb.modpat.CambioNombrePat;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author michael
 */
@ManagedBean(name = "cnomrezBean")
@ViewScoped
public class CNomRezagoBean implements Serializable {

    private List<CambioNombre> cambiosNombre;
    private List<CambioNombre> cambiosNombreFiltradas;
    private List<CambioNombre> selectedCambiosNombre;
    private UIData cambioNombreDataTable;

    private CambioNombre cambioNombre;
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

    public CNomRezagoBean() {
        loadCambiosNombreRezagadas();
    }

    private void loadCambiosNombreRezagadas() {
        cambiosNombre = new ArrayList<>();
        Controlador c = new Controlador();
        tramitesLog = "";
        loginBean = c.getLogin();
    }

    public void longRunning() throws InterruptedException {
        valorProgessBar = 0;
//        Integer k = valorProgessBar;
        if (!selectedCambiosNombre.isEmpty()) {
            Controlador c = new Controlador();
            for (int i = 0; i < selectedCambiosNombre.size(); i++) {

                CambioNombre camnomaux = selectedCambiosNombre.get(i);
                CambioNombrePat cnompat = new CambioNombrePat();

                if (c.existeCambioNombre(camnomaux.getSolicitud())) {
                    tramitesLog += "TRÁMITE "+camnomaux.getSolicitud() + " YA EXISTE EN CAMBIOS DE NOMBRE\n";
                    System.out.println("tramite: "+tramitesLog);
                } else {
                    RenewalForm rf = c.getRenewalFormsByApplicationNumber(camnomaux.getSolicitud());

                    if (rf.getId() != null) {
                        if (rf.getStatus().equals("DELIVERED")) {

                            Types t = c.getTypes(rf.getTransactionMotiveId());

                            Types ttp = c.getTypes(rf.getFormId());

                            if (t.getId() != null && ttp.getId() != null) {
                                if (!ttp.getAlias().equals("PI") && !ttp.getAlias().equals("MU") && !ttp.getAlias().equals("DI")) {
                                    if (t.getName().trim().toLowerCase().contains("cambio de nombre")) {

                                        PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                        camnomaux.setComprobante(payment.getVoucherNumber());
                                        camnomaux.setFechaPresentacion(rf.getApplicationDate());
                                        //certificado.setCertificado(c.getNextNumeroCertificadoCD());
//                                    certificado.setCertificado(c.getNextCambioNombreCertificado());

                                        camnomaux.setSigno(ttp.getAlias());
                                        camnomaux.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");
                                        camnomaux.setIdRenewalForm(rf.getId());

                                        if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                            HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                            if (hf.getId() != null) {
                                                camnomaux.setDenominacion(hf.getDenomination());
                                                camnomaux.setRegistro(hf.getExpedient());

                                                if (camnomaux.getRegistro() != null && !camnomaux.getRegistro().trim().isEmpty()) {
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(camnomaux.getRegistro(), camnomaux.getDenominacion());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        camnomaux.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        camnomaux.setTitularAnterior(titulo.getTitular());
                                                    }
                                                }
                                                if (camnomaux.getFechaRegistro() == null) {
                                                    camnomaux.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                }
                                            }
                                        } else {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                if (ps.getCodigoSolicitudSigno() != null) {
                                                    camnomaux.setDenominacion(ps.getDenominacionSigno());
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        camnomaux.setRegistro(titulo.getNumeroTitulo());
                                                        camnomaux.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        camnomaux.setTitularAnterior(titulo.getTitular());
                                                    }
                                                }
                                                if (camnomaux.getTitularAnterior() == null || camnomaux.getTitularAnterior().trim().isEmpty()) {
                                                    PpdiPersona persona = c.getASolicitanteByCodigoSolicitud(ps.getCodigoSolicitudSigno());
                                                    if (persona.getCodigoPersona() != null) {
                                                        camnomaux.setTitularAnterior(persona.getNombrePersona());
                                                    }
                                                }
                                            }
                                        }

                                        Person titAct = c.getTitularActual(rf.getId());
                                        if (titAct.getId() != null) {
                                            camnomaux.setTitularActual(titAct.getName());
                                            camnomaux.setDomicilioTitularActual(titAct.getAddress());
                                            camnomaux.setIdentificacion(titAct.getIdentificationNumber());
                                        }

                                        Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "AGENT");
                                        if (apoder.getId() != null) {
                                            camnomaux.setApeApodRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "AGENT"));
                                        }

                                        if (camnomaux.getRegistro() != null && !camnomaux.getRegistro().trim().isEmpty()) {
                                            if (camnomaux.getDenominacion() != null && !camnomaux.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(camnomaux.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(camnomaux.getRegistro(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            tramitesLog += "TÍTULO DE " + camnomaux.getSolicitud() + "\n";
                                                            camnomaux = new CambioNombre();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            camnomaux.setCancelado(titca.getTipoCancelacion());
                                                            tramitesLog += "TÍTULO DE " + camnomaux.getSolicitud() + "\n";
                                                        } else {
                                                            tramitesLog += "TÍTULO DE " + camnomaux.getSolicitud() + "\n";
                                                            camnomaux = new CambioNombre();
                                                        }
                                                    } else {
                                                        System.out.println("cambio de nombre: " + camnomaux.getSolicitud() + " CARGADA CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(camnomaux.getRegistro(), camnomaux.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(camnomaux.getRegistro(), camnomaux.getDenominacion());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            tramitesLog += "TÍTULO DE " + camnomaux.getSolicitud() + "\n";
                                                            camnomaux = new CambioNombre();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            camnomaux.setCancelado(titca.getTipoCancelacion());
                                                            tramitesLog += "TÍTULO DE " + camnomaux.getSolicitud() + "\n";
                                                        } else {
                                                            tramitesLog += "TÍTULO DE " + camnomaux.getSolicitud() + "\n";
                                                            camnomaux = new CambioNombre();
                                                        }
                                                    } else {
                                                        System.out.println("cambio de nombre: " + camnomaux.getSolicitud() + " CARGADA CORRECTAMENTE");
                                                    }
                                                }
                                            } else {
                                                System.out.println(camnomaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                            }
                                        } else {
                                            System.out.println(camnomaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                        }
                                    } else {
                                        System.out.println(camnomaux.getSolicitud() + ": TRÁMITE ENCONTRADO PERO NO ES UN CAMBIO DE NOMBRE, SINO '" + t.getName().toUpperCase() + "'");
                                    }
                                } else {
                                    //poner aquí la parte de patentes

                                    camnomaux = new CambioNombre();
                                    cnompat.setSolicitud(rf.getApplicationNumber());
                                    if (t.getName().trim().toLowerCase().contains("cambio de nombre")) {

                                        PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                        cnompat.setComprobante(payment.getVoucherNumber());
                                        cnompat.setFechaPresentacion(rf.getApplicationDate());
                                        //certificado.setCertificado(c.getNextNumeroCertificadoCD());
//                                    certificado.setCertificado(c.getNextCambioNombreCertificado());

                                        cnompat.setSigno(ttp.getName().toUpperCase());
                                        cnompat.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");
                                        cnompat.setIdRenewalForm(rf.getId());

                                        if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                            PatentForms pf = c.getPatentFormsDepurada(rf.getDebugId());

                                            if (pf.getId() != null) {
                                                cnompat.setDenominacion(pf.getTitle());
                                                cnompat.setRegistro(pf.getExpedient());

                                                if (pf.getExpYear() != null && !pf.getExpYear().trim().isEmpty()) {
                                                    cnompat.setFechaRegistro(Operaciones.convertStringToDate(pf.getExpYear() + "-01-01"));
                                                }

                                                if (cnompat.getRegistro() != null && !cnompat.getRegistro().trim().isEmpty()) {
                                                    PpdiTituloPatente titulo = c.getPpdiTituloPatenteByNumeroTitulo(cnompat.getRegistro());
                                                    if (titulo.getCodigoSolicitudPatente() != null) {
                                                        cnompat.setFechaRegistro(titulo.getFechaEmisionDocumento());
//                                                    certificado.setTitularAnterior(titulo.getTitular());
                                                    }

                                                    PpdiSolicitudPatente patsol = c.getPpdiSolicitudPatenteByTramite(cnompat.getSolicitud());
                                                    if (patsol.getCodigoSolicitudPatente() != null) {
                                                        cnompat.setClaseInternacional(patsol.getClasificacionInternacional());
                                                    }
                                                }
                                            }
                                        } else {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                PpdiSolicitudPatente ps = c.getPpdiSolicitudPatenteByExpedient(rf.getExpedient());
                                                if (ps.getCodigoSolicitudPatente() != null) {
                                                    cnompat.setDenominacion(ps.getTitulo());
                                                    PpdiTituloPatente titulo = c.getPpdiTituloPatenteByCodigoSolicitudPatente(ps.getCodigoSolicitudPatente());
                                                    if (titulo.getCodigoSolicitudPatente() != null) {
                                                        cnompat.setRegistro(titulo.getNumeroTitulo());
                                                        cnompat.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        cnompat.setTitularAnterior("");
                                                    }
                                                }
                                                if (cnompat.getTitularAnterior() == null || cnompat.getTitularAnterior().trim().isEmpty()) {
                                                    String personas = c.getPersonasByTipoPersona(ps, "SOLICITANTE");
                                                    if (!personas.isEmpty()) {
                                                        cnompat.setTitularAnterior(personas);
                                                    }
                                                }
                                            }
                                        }

                                        //titulares
                                        List<PersonRenewalName> tit_actuales = c.getTitularesActuales(rf.getId());
                                        if (!tit_actuales.isEmpty()) {
                                            cnompat.setTitularActual(c.getNamesFromPersonRenewalName(tit_actuales, false));
                                            cnompat.setDomicilioTitularActual(c.getAddressFromPersonRenewalName(tit_actuales, false));
                                            cnompat.setIdentificacion(c.getIdentificationFromPersonRenewalName(tit_actuales));
                                        }

                                        //apoderado
                                        List<PersonRenewal> apoderados = c.getPersonRenewalByIdRenewalAndType(rf.getId(), "'AGENT','ATTORNEY'");
                                        if (!apoderados.isEmpty()) {
                                            cnompat.setApeApodRepre(c.getNamesFromPersonRenewal(apoderados));
                                            cnompat.setEmail(c.getEmailsFromPersonRenewal(apoderados));
                                        }

                                        if (cnompat.getRegistro() != null && !cnompat.getRegistro().trim().isEmpty()) {
                                            if (cnompat.getDenominacion() != null && !cnompat.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(cnompat.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(cnompat.getRegistro(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            tramitesLog += "TÍTULO DE " + cnompat.getSolicitud() + " CANCELADO\n";
                                                            cnompat = new CambioNombrePat();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            cnompat.setCancelado(titca.getTipoCancelacion());
                                                            tramitesLog += "TÍTULO DE " + cnompat.getSolicitud() + " CANCELADO\n";
                                                        } else {
                                                            tramitesLog += "TÍTULO DE " + cnompat.getSolicitud() + " CANCELADO\n";
                                                            cnompat = new CambioNombrePat();
                                                        }
                                                    } else {
                                                        System.out.println("DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(cnompat.getRegistro(), cnompat.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(cnompat.getRegistro(), cnompat.getDenominacion());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            tramitesLog += "TÍTULO DE " + cnompat.getSolicitud() + " CANCELADO\n";
                                                            cnompat = new CambioNombrePat();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            cnompat.setCancelado(titca.getTipoCancelacion());
                                                            tramitesLog += "TÍTULO DE " + cnompat.getSolicitud() + " CANCELADO\n";
                                                        } else {
                                                            tramitesLog += "TÍTULO DE " + cnompat.getSolicitud() + " CANCELADO\n";
                                                            cnompat = new CambioNombrePat();
                                                        }
                                                    } else {
                                                        System.out.println("DATOS CARGADOS CORRECTAMENTE PATENTE");
                                                    }
                                                }
                                            } else {
                                                System.out.println(cnompat.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                            }
                                        } else {
                                            System.out.println(cnompat.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                        }

                                    } else {
                                        System.out.println(cnompat.getSolicitud() + ": TRÁMITE ENCONTRADO PERO NO ES UN CAMBIO DE NOMBRE, SINO '" + t.getName().toUpperCase() + "'");
                                    }
                                }
                            } else {
                                tramitesLog += cnompat.getSolicitud() + ": EL TRÁMITE PRESENTA UN PROBLEMA DE IDENTIDAD\n";
                            }

                        } else {
                            tramitesLog += cnompat.getSolicitud() + ":TRÁMITE ENCONTRADO, PERO NO REGISTRA INICIO DE PROCESO\n";
                        }
                    }
                    if (camnomaux.getSolicitud() != null) {
                        camnomaux.setFechaCertificado(new Date());
                        camnomaux.setCertificado(c.getNextCambioNombreCertificado());
                        camnomaux.setSolicitud(camnomaux.getSolicitud().trim().toUpperCase());
                        camnomaux.setTipoEstado("CERTIFICADO");
                        camnomaux.setResponsable(loginBean.getUsuario().getAlias());
                        if (c.saveCambioNombre(camnomaux)) {
                            ModificacionApp mapp = new ModificacionApp();
                            mapp.setDenominacion(camnomaux.getDenominacion() != null ? camnomaux.getDenominacion() : "");
                            mapp.setFecha(new Timestamp(new Date().getTime()));
                            mapp.setObservacion("rezago pasado");
                            mapp.setRegistro(camnomaux.getRegistro());
                            mapp.setSolicitud(camnomaux.getSolicitud());
                            mapp.setTipo("CAMBIO DE NOMBRE");
                            mapp.setUsuario(loginBean.getNombre());
                            mapp.setModo("MARCA");
                            mapp.setActivo(true);
//
                            if (c.saveModificacionApp(mapp)) {
                                c.saveHistorial("CAMBIO DE NOMBRE", "CAMBIO DE NOMBRE", camnomaux.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            }
                        }
                    } else if (cnompat.getSolicitud() != null) {
                        cnompat.setFechaCertificado(new Date());
                        cnompat.setCertificado(c.getNextCambioNombrePatCertificado());
                        cnompat.setSolicitud(cnompat.getSolicitud().trim().toUpperCase());
                        cnompat.setTipoEstado("CERTIFICADO");
                        cnompat.setResponsable(loginBean.getUsuario().getAlias());
                        if (c.saveCambioNombrePat(cnompat)) {
                            ModificacionApp mapp = new ModificacionApp();
                            mapp.setDenominacion(cnompat.getDenominacion() != null ? cnompat.getDenominacion() : "");
                            mapp.setFecha(new Timestamp(new Date().getTime()));
                            mapp.setObservacion("rezago pasado");
                            mapp.setRegistro(cnompat.getRegistro());
                            mapp.setSolicitud(cnompat.getSolicitud());
                            mapp.setTipo("CAMBIO DE NOMBRE");
                            mapp.setUsuario(loginBean.getNombre());
                            mapp.setModo("PATENTE");
                            mapp.setActivo(true);
                            if (c.saveModificacionApp(mapp)) {
                                c.saveHistorialPat("CAMBIO DE NOMBRE", "CAMBIO DE NOMBRE", cnompat.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            }
                        }
                    }
                }
                int n = i + 1;
                valorProgessBar = (n * 100) / selectedCambiosNombre.size();
//                System.out.println("valorprog: " + valorProgessBar);
                //Thread.sleep(500);
            }
            valorProgessBar = 100;
        }
        valorProgessBar = 100;
    }

    public void onComplete() {
                
        if(tramitesLog.trim().isEmpty()){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("SE HAN IMPORTADO CORRECTAMENTE LOS CAMBIOS DE NOMBRE"));
        }else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", tramitesLog));
        }
        loadCambiosNombreRezagadas();        
    }

    public void cargarRezago(ActionEvent ae) {
        Controlador c = new Controlador();
        List<RenewalForm> renewals = c.getRenewalsRezagoBytType("CAMBIO DE NOMBRE", "25");
        cambiosNombre = c.loadCambiosNombreFromRenewals(renewals);
        botonpasar = !cambiosNombre.isEmpty();
        numRegistros = "Número Registros Mostrados: " + cambiosNombre.size();

    }

    public void buscarRezagoCambioNCriterio(ActionEvent ae) {
        FacesMessage msg = null;
        if (!criterio.trim().isEmpty() && criterio.trim().length() > 3) {
            Controlador c = new Controlador();
            List<RenewalForm> renewals = c.getRenewalsRezagoBytTypeAndCriterio("CAMBIO DE NOMBRE", "25", criterio.trim().toUpperCase());
            cambiosNombre = c.loadCambiosNombreFromRenewals(renewals);
            botonpasar = !cambiosNombre.isEmpty();
            numRegistros = "Número Registros Mostrados: " + cambiosNombre.size();
            if (cambiosNombre.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO VÁLIDO (MÁS DE 3 CARACTERES)");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarCambiosNPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            List<RenewalForm> renewals = c.getRenewalsRezagoBytTypeAndFecha("CAMBIO DE NOMBRE", "25", fechaInicio, fechaFin);
            cambiosNombre = c.loadCambiosNombreFromRenewals(renewals);
            botonpasar = !cambiosNombre.isEmpty();
            numRegistros = "Número Registros Mostrados: " + cambiosNombre.size();
            if (cambiosNombre.isEmpty()) {
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
        cambioNombre = (CambioNombre) cambioNombreDataTable.getRowData();
//        System.out.println("hereeeeeeee");
        if (cambioNombre != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + cambioNombre.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(cambioNombre.getIdRenewalForm(), cambioNombre.getSolicitud());
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
        if (selectedCambiosNombre != null && !selectedCambiosNombre.isEmpty()) {
            Controlador c = new Controlador();
            for (int i = 0; i < selectedCambiosNombre.size(); i++) {

                CambioNombre tran = selectedCambiosNombre.get(i);

                ModificacionApp map = new ModificacionApp();
                map.setDenominacion("");
                map.setFecha(new Timestamp(new Date().getTime()));
                map.setObservacion(tran.getRo());
                map.setRegistro(tran.getRegistro());
                map.setSolicitud(tran.getSolicitud());
                map.setTipo("CAMBIO DE NOMBRE");
                map.setUsuario(loginBean.getNombre());
                map.setModo(tran.getObservacion());
                map.setActivo(false);
                c.saveModificacionApp(map);
            }

            loadCambiosNombreRezagadas();
            PrimeFaces.current().ajax().addCallbackParam("quitado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS SELECCIONADOS FUERON REMOVIDOS DE REZAGO");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararQuitar(ActionEvent ae) {
        FacesMessage msg;
        if (selectedCambiosNombre != null && !selectedCambiosNombre.isEmpty()) {
            for (int i = 0; i < selectedCambiosNombre.size(); i++) {
                selectedCambiosNombre.get(i).setRo("El trámite no pertenece a la Dirección de Modificaciones");
            }
            dialogTitle = "QUITAR DE REZAGADOS " + selectedCambiosNombre.size() + " TRÁMITES?";
            PrimeFaces.current().ajax().addCallbackParam("pasado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS SELECCIONADOS FUERON  CARGADOS CORRECTAMENTE");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void pasarSeleccionados(ActionEvent ae) {
        FacesMessage msg;
        if (selectedCambiosNombre != null && !selectedCambiosNombre.isEmpty()) {
            dialogTitle = "PASAR " + selectedCambiosNombre.size() + " TRÁMITES A CAMBIOS DE NOMBRE?";
            PrimeFaces.current().ajax().addCallbackParam("pasado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS HAN SIDO CARGADOS CORRECTAMENTE");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return the cambiosNombre
     */
    public List<CambioNombre> getCambiosNombre() {
        return cambiosNombre;
    }

    /**
     * @param cambiosNombre the cambiosNombre to set
     */
    public void setCambiosNombre(List<CambioNombre> cambiosNombre) {
        this.cambiosNombre = cambiosNombre;
    }

    /**
     * @return the cambiosNombreFiltradas
     */
    public List<CambioNombre> getCambiosNombreFiltradas() {
        return cambiosNombreFiltradas;
    }

    /**
     * @param cambiosNombreFiltradas the cambiosNombreFiltradas to set
     */
    public void setCambiosNombreFiltradas(List<CambioNombre> cambiosNombreFiltradas) {
        this.cambiosNombreFiltradas = cambiosNombreFiltradas;
    }

    /**
     * @return the selectedCambiosNombre
     */
    public List<CambioNombre> getSelectedCambiosNombre() {
        return selectedCambiosNombre;
    }

    /**
     * @param selectedCambiosNombre the selectedCambiosNombre to set
     */
    public void setSelectedCambiosNombre(List<CambioNombre> selectedCambiosNombre) {
        this.selectedCambiosNombre = selectedCambiosNombre;
    }

    /**
     * @return the cambioNombreDataTable
     */
    public UIData getCambioNombreDataTable() {
        return cambioNombreDataTable;
    }

    /**
     * @param cambioNombreDataTable the cambioNombreDataTable to set
     */
    public void setCambioNombreDataTable(UIData cambioNombreDataTable) {
        this.cambioNombreDataTable = cambioNombreDataTable;
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
     * @return the cambioNombre
     */
    public CambioNombre getCambioNombre() {
        return cambioNombre;
    }

    /**
     * @param cambioNombre the cambioNombre to set
     */
    public void setCambioNombre(CambioNombre cambioNombre) {
        this.cambioNombre = cambioNombre;
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
