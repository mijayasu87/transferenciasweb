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
//import senadi.gob.ec.transfweb.model.cn.CambioNombre;
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepdep.PatentForms;
import senadi.gob.ec.transfweb.model.iepform.ModificacionApp;
import senadi.gob.ec.transfweb.model.iepform.PaymentReceipt;
import senadi.gob.ec.transfweb.model.iepform.Person;
import senadi.gob.ec.transfweb.model.iepform.PersonRenewal;
import senadi.gob.ec.transfweb.model.iepform.PersonRenewalName;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepform.Types;
import senadi.gob.ec.transfweb.model.licencia.SubLicenciaUso;
//import senadi.gob.ec.transfweb.model.licencia.LicenciaUso;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudPatente;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.modelp.PpdiTituloPatente;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import senadi.gob.ec.transfweb.modpat.LicenciaUsoPat;
import senadi.gob.ec.transfweb.modpat.SubLicenciaUsoPat;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author michael
 */
@ManagedBean(name = "sublicrezBean")
@ViewScoped
public class SublicenciaRezagoBean implements Serializable {

    private List<SubLicenciaUso> sublicenciasUso;
    private List<SubLicenciaUso> sublicenciasUsoFiltradas;
    private List<SubLicenciaUso> selectedSublicenciasUso;
    private UIData sublicenciaUsoDataTable;

    private SubLicenciaUso sublicenciaUso;
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

    public SublicenciaRezagoBean() {
        loadSublicenciasUsoRezagadas();
    }

    private void loadSublicenciasUsoRezagadas() {
        sublicenciasUso = new ArrayList<>();
        Controlador c = new Controlador();
        tramitesLog = "";
        loginBean = c.getLogin();
    }

    public void longRunning() throws InterruptedException {
        valorProgessBar = 0;
//        Integer k = valorProgessBar;
        if (!selectedSublicenciasUso.isEmpty()) {
            Controlador c = new Controlador();
            for (int i = 0; i < selectedSublicenciasUso.size(); i++) {

                SubLicenciaUso sublicaux = selectedSublicenciasUso.get(i);
                SubLicenciaUsoPat sublicusopat = new SubLicenciaUsoPat();

                if (c.existeLicenciaUso(sublicaux.getSolicitud())) {
                    tramitesLog += "TRÁMITE " + sublicaux.getSolicitud() + " YA EXISTE EN SUBLICENCIAS DE USO\n";
                    System.out.println("tramite: " + tramitesLog);
                } else {
                    RenewalForm rf = c.getRenewalFormsByApplicationNumber(sublicaux.getSolicitud());

                    if (rf.getId() != null) {
                        if (rf.getStatus().equals("DELIVERED")) {

                            Types t = c.getTypes(rf.getTransactionMotiveId());

                            Types ttp = c.getTypes(rf.getFormId());

                            if (t.getId() != null && ttp.getId() != null) {
                                if (!ttp.getAlias().equals("PI") && !ttp.getAlias().equals("MU") && !ttp.getAlias().equals("DI")) {
                                    if (t.getName().trim().toLowerCase().contains("licencia de uso")) {
                                        if (rf.getLicenseType() != null && rf.getLicenseType().equals("SUBLICENSE")) {
                                            PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                            sublicaux.setComprobante(payment.getVoucherNumber());
                                            sublicaux.setFechaPresentacion(rf.getApplicationDate());
                                            sublicaux.setSublicenciaNo(c.getNextSublicenciaUsoNo());

                                            sublicaux.setSigno(ttp.getAlias());
                                            sublicaux.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");
                                            sublicaux.setIdRenewalForm(rf.getId());

                                            if (rf.getDebugId() != null && rf.getDebugId() != 0) {
                                                HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                                if (hf.getId() != null) {
                                                    sublicaux.setDenominacion(hf.getDenomination());
                                                    sublicaux.setRegistro(hf.getExpedient());

                                                    if (sublicaux.getRegistro() != null && !sublicaux.getRegistro().trim().isEmpty()) {
                                                        PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(sublicaux.getRegistro(), sublicaux.getDenominacion());
                                                        if (titulo.getCodigoSolicitudSigno() != null) {
                                                            sublicaux.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        }
                                                    }
                                                    if (sublicaux.getFechaRegistro() == null) {
                                                        sublicaux.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                    }
                                                }
                                            } else {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                    PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                    if (ps.getCodigoSolicitudSigno() != null) {
                                                        sublicaux.setDenominacion(ps.getDenominacionSigno());
                                                        PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                        if (titulo.getCodigoSolicitudSigno() != null) {
                                                            sublicaux.setRegistro(titulo.getNumeroTitulo());
                                                            sublicaux.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        }
                                                    }
                                                }
                                            }

                                            Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "ATTORNEY");
                                            if (apoder.getId() != null) {
                                                sublicaux.setApoderadoRepresentante(apoder.getName());
                                                sublicaux.setEmail(apoder.getEmail());
                                            }

                                            List<Person> applicants = c.getPersonsByType(rf.getId(), "APPLICANT");
                                            sublicaux.setSublicenciante("");
                                            for (int j = 0; j < applicants.size(); j++) {
                                                String salto = ", ";
                                                if (j == applicants.size() - 1) {
                                                    salto = "";
                                                }
                                                sublicaux.setSublicenciante(sublicaux.getSublicenciante() + applicants.get(j).getName() + salto);
                                            }
                                            List<Person> beneficiaries = c.getPersonsByType(rf.getId(), "BENEFICIARY");
                                            sublicaux.setSublicenciatario("");
                                            for (int j = 0; j < beneficiaries.size(); j++) {
                                                String salto = ", ";
                                                if (j == beneficiaries.size() - 1) {
                                                    salto = "";
                                                }
                                                sublicaux.setSublicenciatario(sublicaux.getSublicenciatario() + beneficiaries.get(j).getName() + salto);
                                            }

                                            if (sublicaux.getRegistro() != null && !sublicaux.getRegistro().trim().isEmpty()) {
                                                if (sublicaux.getDenominacion() != null && !sublicaux.getDenominacion().trim().isEmpty()) {
                                                    if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                        if (c.existsTituloCanceladoByTituloAndExpediente(sublicaux.getRegistro(), rf.getExpedient(), false)) {
                                                            TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(sublicaux.getRegistro(), rf.getExpedient());
                                                            if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                                tramitesLog += "TÍTULO DE " + sublicaux.getSolicitud() + "\n";
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                sublicaux = new SubLicenciaUso();
                                                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                                sublicaux.setCancelado(titca.getTipoCancelacion());
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                tramitesLog += "TÍTULO DE " + sublicaux.getSolicitud() + "\n";
                                                            } else {
                                                                tramitesLog += "TÍTULO DE " + sublicaux.getSolicitud() + "\n";
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                sublicaux = new SubLicenciaUso();
                                                            }
                                                        } else {
//                                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                            System.out.println("sublicencia de uso: " + sublicaux.getSolicitud() + " CARGADA CORRECTAMENTE");
                                                        }
                                                    } else {
                                                        if (c.existsTituloCanceladoByTituloAndDenominacion(sublicaux.getRegistro(), sublicaux.getDenominacion(), false)) {
                                                            TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(sublicaux.getRegistro(), sublicaux.getDenominacion());
                                                            if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                tramitesLog += "TÍTULO DE " + sublicaux.getSolicitud() + "\n";
                                                                sublicaux = new SubLicenciaUso();
                                                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                                sublicaux.setCancelado(titca.getTipoCancelacion());
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                tramitesLog += "TÍTULO DE " + sublicaux.getSolicitud() + "\n";
                                                            } else {
                                                                tramitesLog += "TÍTULO DE " + sublicaux.getSolicitud() + "\n";
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                sublicaux = new SubLicenciaUso();
                                                            }
                                                        } else {
                                                            System.out.println("sublicencia de uso: " + sublicaux.getSolicitud() + " CARGADA CORRECTAMENTE");
                                                            //msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                        }
                                                    }
                                                } else {
                                                    System.out.println(sublicaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                                    //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                                }

                                            } else {
                                                System.out.println(sublicaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                                //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                            }
                                        } else {
                                            System.out.println(sublicaux.getSolicitud() + ": TRÁMITE ENCONTRADO PERO NO ES UNA SUBLICENCIA, SINO 'SUBLICENCIA'");
//                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE " + rf.getApplicationNumber() + " ES UNA SUBLICENCIA");
                                        }
                                    } else {
                                        System.out.println(sublicaux.getSolicitud() + ": TRÁMITE ENCONTRADO PERO NO ES UNA SUBLICENCIA, SINO '" + t.getName().toUpperCase() + "'");
                                        //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UNA SUBLICENCIA DE USO, SINO '" + t.getName().toUpperCase() + "'");
                                    }
                                } else {
                                    sublicaux = new SubLicenciaUso();
                                    sublicusopat.setSolicitud(rf.getApplicationNumber());
                                    if (t.getName().trim().toLowerCase().contains("licencia de uso")) {
                                        if (rf.getLicenseType() != null && rf.getLicenseType().equals("SUBLICENSE")) {
                                            PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                            sublicusopat.setComprobante(payment.getVoucherNumber());
                                            sublicusopat.setFechaPresentacion(rf.getApplicationDate());
//                                        licencia.setLicenciaNo(c.getNextLicenciaUsoNo());

                                            sublicusopat.setSigno(ttp.getName().toUpperCase());
                                            sublicusopat.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");
                                            sublicusopat.setIdRenewalForm(rf.getId());

                                            if (rf.getDebugId() != null && rf.getDebugId() != 0) {
                                                PatentForms pf = c.getPatentFormsDepurada(rf.getDebugId());

                                                if (pf.getId() != null) {
                                                    sublicusopat.setDenominacion(pf.getTitle());
                                                    sublicusopat.setRegistro(pf.getExpedient());

                                                    if (pf.getExpYear() != null && !pf.getExpYear().trim().isEmpty()) {
                                                        sublicusopat.setFechaRegistro(Operaciones.convertStringToDate(pf.getExpYear() + "-01-02"));
                                                    }

                                                    if (sublicusopat.getRegistro() != null && !sublicusopat.getRegistro().trim().isEmpty()) {
                                                        PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByNumeroTitulo(sublicusopat.getRegistro());
                                                        if (titulo.getCodigoSolicitudSigno() != null) {
                                                            sublicusopat.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        }

//                                                        PpdiSolicitudPatente patsol = c.getPpdiSolicitudPatenteByTramite(sublicusopat.getSolicitud());
//                                                        if (patsol.getCodigoSolicitudPatente() != null) {
//                                                            sublicusopat.setClaseInternacional(patsol.getClasificacionInternacional());
//                                                        }
                                                    }
                                                }
                                            } else {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                    PpdiSolicitudPatente ps = c.getPpdiSolicitudPatenteByExpedient(rf.getExpedient());
                                                    if (ps.getCodigoSolicitudPatente() != null) {
                                                        sublicusopat.setDenominacion(ps.getTitulo());
                                                        PpdiTituloPatente titulo = c.getPpdiTituloPatenteByCodigoSolicitudPatente(ps.getCodigoSolicitudPatente());
                                                        if (titulo.getCodigoSolicitudPatente() != null) {
                                                            sublicusopat.setRegistro(titulo.getNumeroTitulo());
                                                            sublicusopat.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        }
                                                    }
                                                }
                                            }

                                            //apoderado
                                            List<PersonRenewal> apoderados = c.getPersonRenewalByIdRenewalAndType(rf.getId(), "'AGENT','ATTORNEY'");
                                            if (!apoderados.isEmpty()) {
                                                sublicusopat.setApoderadoRepresentante(c.getNamesFromPersonRenewal(apoderados));
                                                sublicusopat.setEmail(c.getEmailsFromPersonRenewal(apoderados));
                                            }

                                            //titulares
                                            List<PersonRenewalName> tit_actuales = c.getTitularesActuales(rf.getId());
                                            if (!tit_actuales.isEmpty()) {
                                                sublicusopat.setSublicenciante(c.getNamesFromPersonRenewalName(tit_actuales, false));
                                            }

                                            //beneficiarios
                                            List<PersonRenewal> beneficiaries = c.getPersonRenewalByIdRenewalAndType(rf.getId(), "'BENEFICIARY'");
                                            if (!beneficiaries.isEmpty()) {
                                                sublicusopat.setSublicenciatario(c.getNamesFromPersonRenewal(beneficiaries));
                                            }

                                            if (sublicusopat.getRegistro() != null && !sublicusopat.getRegistro().trim().isEmpty()) {
                                                if (sublicusopat.getDenominacion() != null && !sublicusopat.getDenominacion().trim().isEmpty()) {
                                                    if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                        if (c.existsTituloCanceladoByTituloAndExpediente(sublicusopat.getRegistro(), rf.getExpedient(), false)) {
                                                            TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(sublicusopat.getRegistro(), rf.getExpedient());
                                                            if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licusopat.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licusopat.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                tramitesLog += "TÍTULO DE " + sublicaux.getSolicitud() + " CANCELADO\n";
                                                                sublicusopat = new SubLicenciaUsoPat();
                                                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                                sublicusopat.setCancelado(titca.getTipoCancelacion());
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licusopat.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licusopat.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                tramitesLog += "TÍTULO DE " + sublicaux.getSolicitud() + " CANCELADO\n";
                                                            } else {
                                                                tramitesLog += "TÍTULO DE " + sublicaux.getSolicitud() + " CANCELADO\n";
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licusopat.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licusopat.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                sublicusopat = new SubLicenciaUsoPat();
                                                            }
                                                        } else {
//                                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                            System.out.println("sublicencia de uso: " + sublicaux.getSolicitud() + " CARGADA CORRECTAMENTE");
                                                        }
                                                    } else {
                                                        if (c.existsTituloCanceladoByTituloAndDenominacion(sublicusopat.getRegistro(), sublicusopat.getDenominacion(), false)) {
                                                            TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(sublicusopat.getRegistro(), sublicusopat.getDenominacion());
                                                            if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licusopat.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licusopat.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                tramitesLog += "TÍTULO DE " + sublicaux.getSolicitud() + " CANCELADO\n";
                                                                sublicusopat = new SubLicenciaUsoPat();
                                                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                                sublicusopat.setCancelado(titca.getTipoCancelacion());
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licusopat.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licusopat.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                tramitesLog += "TÍTULO DE " + sublicaux.getSolicitud() + " CANCELADO\n";
                                                            } else {
                                                                tramitesLog += "TÍTULO DE " + sublicaux.getSolicitud() + " CANCELADO\n";
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licusopat.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licusopat.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                sublicusopat = new SubLicenciaUsoPat();
                                                            }
                                                        } else {
                                                            System.out.println("sublicencia de uso: " + sublicaux.getSolicitud() + " CARGADA CORRECTAMENTE");
//                                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                        }
                                                    }
                                                } else {
                                                    //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                                    System.out.println(sublicaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                                }

                                            } else {
                                                System.out.println(sublicaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                                //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                            }
                                        } else {
                                            System.out.println(sublicaux.getSolicitud() + ": EL TRÁMITE " + rf.getApplicationNumber() + " ES UNA SUBLICENCIA");
                                            //msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE " + rf.getApplicationNumber() + " ES UNA SUBLICENCIA");
                                        }
                                    } else {
                                        System.out.println(sublicaux.getSolicitud() + " TRÁMITE ENCONTRADO PERO NO ES UNA SUBLICENCIA DE USO, SINO '" + t.getName().toUpperCase() + "'");
//                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UNA SUBLICENCIA DE USO, SINO '" + t.getName().toUpperCase() + "'");
                                    }

                                    //msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE ES UN " + ttp.getName().toUpperCase());
                                }
                            } else {
                                //msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE PRESENTA UN PROBLEMA DE IDENTIDAD");
                                tramitesLog += sublicusopat.getSolicitud() + ": EL TRÁMITE PRESENTA UN PROBLEMA DE IDENTIDAD\n";
                            }

                        } else {
                            //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO, PERO NO REGISTRA INICIO DE PROCESO");
                            tramitesLog += sublicusopat.getSolicitud() + ":TRÁMITE ENCONTRADO, PERO NO REGISTRA INICIO DE PROCESO\n";
                        }
                    }

                    if (sublicaux.getSolicitud() != null) {
                        sublicaux.setFechaSublicencia(new Date());
                        sublicaux.setSublicenciaNo(c.getNextSublicenciaUsoNo());
                        sublicaux.setSolicitud(sublicaux.getSolicitud().trim().toUpperCase());
                        sublicaux.setTipoEstado("SUBLICENCIA");
                        sublicaux.setResponsable(loginBean.getUsuario().getAlias());
                        if (c.saveSublicenciaUso(sublicaux)) {
                            ModificacionApp mapp = new ModificacionApp();
                            mapp.setDenominacion(sublicaux.getDenominacion() != null ? sublicaux.getDenominacion() : "");
                            mapp.setFecha(new Timestamp(new Date().getTime()));
                            mapp.setObservacion("rezago pasado");
                            mapp.setRegistro(sublicaux.getRegistro());
                            mapp.setSolicitud(sublicaux.getSolicitud());
                            mapp.setTipo("SUBLICENCIA DE USO");
                            mapp.setUsuario(loginBean.getNombre());
                            mapp.setModo("MARCA");
                            mapp.setActivo(true);
//
                            if (c.saveModificacionApp(mapp)) {
                                c.saveHistorial("SUBLICENCIA DE USO", "SUBLICENCIA DE USO", sublicaux.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            }
                        }
                    } else if (sublicusopat.getSolicitud() != null) {
                        sublicusopat.setFechaSublicencia(new Date());
                        sublicusopat.setSublicenciaNo(c.getNextSublicenciaUsoPatCertificado());
                        sublicusopat.setSolicitud(sublicusopat.getSolicitud().trim().toUpperCase());
                        sublicusopat.setTipoEstado("SUBLICENCIA");
                        sublicusopat.setResponsable(loginBean.getUsuario().getAlias());
                        if (c.saveSublicenciaUsoPat(sublicusopat)) {
                            ModificacionApp mapp = new ModificacionApp();
                            mapp.setDenominacion(sublicusopat.getDenominacion() != null ? sublicusopat.getDenominacion() : "");
                            mapp.setFecha(new Timestamp(new Date().getTime()));
                            mapp.setObservacion("rezago pasado");
                            mapp.setRegistro(sublicusopat.getRegistro());
                            mapp.setSolicitud(sublicusopat.getSolicitud());
                            mapp.setTipo("SUBLICENCIA DE USO");
                            mapp.setUsuario(loginBean.getNombre());
                            mapp.setModo("PATENTE");
                            mapp.setActivo(true);
                            if (c.saveModificacionApp(mapp)) {
                                c.saveHistorialPat("SUBLICENCIA DE USO", "SUBLICENCIA DE USO", sublicusopat.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            }
                        }
                    }
                }
                int n = i + 1;
                valorProgessBar = (n * 100) / selectedSublicenciasUso.size();
//                System.out.println("valorprog: " + valorProgessBar);
                //Thread.sleep(500);
            }
            valorProgessBar = 100;
        }
        valorProgessBar = 100;
    }

    public void onComplete() {

        if (tramitesLog.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("SE HAN IMPORTADO CORRECTAMENTE LAS SUBLICENCIAS DE USO"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", tramitesLog));
        }
        loadSublicenciasUsoRezagadas();
    }

    public void cargarRezago(ActionEvent ae) {
        Controlador c = new Controlador();
        List<RenewalForm> renewals = c.getRenewalsRezagoSublicBytTypeAndCriteria("SUBLICENCIA DE USO", "27", "");
        sublicenciasUso = c.loadSublicenciaUsoFromRenewals(renewals);
        botonpasar = !sublicenciasUso.isEmpty();
        numRegistros = "Número Registros Mostrados: " + sublicenciasUso.size();

    }

    public void buscarRezagoSubLicenciaUCriterio(ActionEvent ae) {
        FacesMessage msg = null;
        if (!criterio.trim().isEmpty() && criterio.trim().length() > 3) {
            Controlador c = new Controlador();
            List<RenewalForm> renewals = c.getRenewalsRezagoSublicBytTypeAndCriteria("SUBLICENCIA DE USO", "27", criterio.trim().toUpperCase());
            sublicenciasUso = c.loadSublicenciaUsoFromRenewals(renewals);
            botonpasar = !sublicenciasUso.isEmpty();
            numRegistros = "Número Registros Mostrados: " + sublicenciasUso.size();
            if (sublicenciasUso.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO VÁLIDO (MÁS DE 3 CARACTERES)");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarLicenciaUPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            List<RenewalForm> renewals = c.getRenewalsRezagoSublicBytTypeAndFecha("SUBLICENCIA DE USO", "27", fechaInicio, fechaFin);
            sublicenciasUso = c.loadSublicenciaUsoFromRenewals(renewals);
            botonpasar = !sublicenciasUso.isEmpty();
            numRegistros = "Número Registros Mostrados: " + sublicenciasUso.size();
            if (sublicenciasUso.isEmpty()) {
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
        sublicenciaUso = (SubLicenciaUso) sublicenciaUsoDataTable.getRowData();
//        System.out.println("hereeeeeeee");
        if (sublicenciaUso != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + sublicenciaUso.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(sublicenciaUso.getIdRenewalForm(), sublicenciaUso.getSolicitud());
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
        if (selectedSublicenciasUso != null && !selectedSublicenciasUso.isEmpty()) {
            Controlador c = new Controlador();
            for (int i = 0; i < selectedSublicenciasUso.size(); i++) {

                SubLicenciaUso licus = selectedSublicenciasUso.get(i);

                ModificacionApp map = new ModificacionApp();
                map.setDenominacion("");
                map.setFecha(new Timestamp(new Date().getTime()));
                map.setObservacion(licus.getRo());
                map.setRegistro(licus.getRegistro());
                map.setSolicitud(licus.getSolicitud());
                map.setTipo("SUBLICENCIA DE USO");
                map.setUsuario(loginBean.getNombre());
                map.setModo(licus.getN_j());
                map.setActivo(false);
                c.saveModificacionApp(map);
            }

            loadSublicenciasUsoRezagadas();
            PrimeFaces.current().ajax().addCallbackParam("quitado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS SELECCIONADOS FUERON REMOVIDOS DE REZAGO");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararQuitar(ActionEvent ae) {
        FacesMessage msg;
        if (selectedSublicenciasUso != null && !selectedSublicenciasUso.isEmpty()) {
            for (int i = 0; i < selectedSublicenciasUso.size(); i++) {
                selectedSublicenciasUso.get(i).setRo("El trámite no pertenece a la Dirección de Modificaciones");
            }
            dialogTitle = "QUITAR DE REZAGADOS " + selectedSublicenciasUso.size() + " TRÁMITES?";
            PrimeFaces.current().ajax().addCallbackParam("pasado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS SELECCIONADOS FUERON  CARGADOS CORRECTAMENTE");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void pasarSeleccionados(ActionEvent ae) {
        FacesMessage msg;
        if (selectedSublicenciasUso != null && !selectedSublicenciasUso.isEmpty()) {
            dialogTitle = "PASAR " + selectedSublicenciasUso.size() + " TRÁMITES A SUBLICENCIAS DE USO?";
            PrimeFaces.current().ajax().addCallbackParam("pasado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS HAN SIDO CARGADOS CORRECTAMENTE");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return the sublicenciasUso
     */
    public List<SubLicenciaUso> getSublicenciasUso() {
        return sublicenciasUso;
    }

    /**
     * @param sublicenciasUso the sublicenciasUso to set
     */
    public void setSublicenciasUso(List<SubLicenciaUso> sublicenciasUso) {
        this.sublicenciasUso = sublicenciasUso;
    }

    /**
     * @return the sublicenciasUsoFiltradas
     */
    public List<SubLicenciaUso> getSublicenciasUsoFiltradas() {
        return sublicenciasUsoFiltradas;
    }

    /**
     * @param sublicenciasUsoFiltradas the sublicenciasUsoFiltradas to set
     */
    public void setSublicenciasUsoFiltradas(List<SubLicenciaUso> sublicenciasUsoFiltradas) {
        this.sublicenciasUsoFiltradas = sublicenciasUsoFiltradas;
    }

    /**
     * @return the selectedSublicenciasUso
     */
    public List<SubLicenciaUso> getSelectedSublicenciasUso() {
        return selectedSublicenciasUso;
    }

    /**
     * @param selectedSublicenciasUso the selectedSublicenciasUso to set
     */
    public void setSelectedSublicenciasUso(List<SubLicenciaUso> selectedSublicenciasUso) {
        this.selectedSublicenciasUso = selectedSublicenciasUso;
    }

    /**
     * @return the sublicenciaUsoDataTable
     */
    public UIData getSublicenciaUsoDataTable() {
        return sublicenciaUsoDataTable;
    }

    /**
     * @param sublicenciaUsoDataTable the sublicenciaUsoDataTable to set
     */
    public void setSublicenciaUsoDataTable(UIData sublicenciaUsoDataTable) {
        this.sublicenciaUsoDataTable = sublicenciaUsoDataTable;
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
     * @return the sublicenciaUso
     */
    public SubLicenciaUso getSublicenciaUso() {
        return sublicenciaUso;
    }

    /**
     * @param sublicenciaUso the sublicenciaUso to set
     */
    public void setSublicenciaUso(SubLicenciaUso sublicenciaUso) {
        this.sublicenciaUso = sublicenciaUso;
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
