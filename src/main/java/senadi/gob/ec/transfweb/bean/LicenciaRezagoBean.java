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
import senadi.gob.ec.transfweb.model.licencia.LicenciaUso;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudPatente;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.modelp.PpdiTituloPatente;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import senadi.gob.ec.transfweb.modpat.LicenciaUsoPat;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author michael
 */
@ManagedBean(name = "licurezBean")
@ViewScoped
public class LicenciaRezagoBean implements Serializable {

    private List<LicenciaUso> licenciasUso;
    private List<LicenciaUso> licenciasUsoFiltradas;
    private List<LicenciaUso> selectedLicenciasUso;
    private UIData licenciaUsoDataTable;

    private LicenciaUso licenciaUso;
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

    public LicenciaRezagoBean() {
        loadLicenciasUsoRezagadas();
    }

    private void loadLicenciasUsoRezagadas() {
        licenciasUso = new ArrayList<>();
        Controlador c = new Controlador();
        tramitesLog = "";
        loginBean = c.getLogin();
    }

    public void longRunning() throws InterruptedException {
        valorProgessBar = 0;
//        Integer k = valorProgessBar;
        if (!selectedLicenciasUso.isEmpty()) {
            Controlador c = new Controlador();
            for (int i = 0; i < selectedLicenciasUso.size(); i++) {

                LicenciaUso licaux = selectedLicenciasUso.get(i);
                LicenciaUsoPat licusopat = new LicenciaUsoPat();

                if (c.existeLicenciaUso(licaux.getSolicitud())) {
                    tramitesLog += "TRÁMITE " + licaux.getSolicitud() + " YA EXISTE EN LICENCIAS DE USO\n";
                    System.out.println("tramite: " + tramitesLog);
                } else {
                    RenewalForm rf = c.getRenewalFormsByApplicationNumber(licaux.getSolicitud());

                    if (rf.getId() != null) {
                        if (rf.getStatus().equals("DELIVERED")) {

                            Types t = c.getTypes(rf.getTransactionMotiveId());

                            Types ttp = c.getTypes(rf.getFormId());

                            if (t.getId() != null && ttp.getId() != null) {
                                if (!ttp.getAlias().equals("PI") && !ttp.getAlias().equals("MU") && !ttp.getAlias().equals("DI")) {
                                    if (t.getName().trim().toLowerCase().contains("licencia de uso")) {
                                        if (rf.getLicenseType() == null || !rf.getLicenseType().equals("SUBLICENSE")) {
                                            PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                            licaux.setComprobante(payment.getVoucherNumber());
                                            licaux.setFechaPresentacion(rf.getApplicationDate());
                                            licaux.setLicenciaNo(c.getNextLicenciaUsoNo());

                                            licaux.setSigno(ttp.getAlias());
                                            licaux.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");
                                            licaux.setIdRenewalForm(rf.getId());

                                            if (rf.getDebugId() != null && rf.getDebugId() != 0) {
                                                HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                                if (hf.getId() != null) {
                                                    licaux.setDenominacion(hf.getDenomination());
                                                    licaux.setRegistro(hf.getExpedient());

                                                    if (licaux.getRegistro() != null && !licaux.getRegistro().trim().isEmpty()) {
                                                        PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(licaux.getRegistro(), licaux.getDenominacion());
                                                        if (titulo.getCodigoSolicitudSigno() != null) {
                                                            licaux.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        }
                                                    }
                                                    if (licaux.getFechaRegistro() == null) {
                                                        licaux.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                    }
                                                }
                                            } else {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                    PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                    if (ps.getCodigoSolicitudSigno() != null) {
                                                        licaux.setDenominacion(ps.getDenominacionSigno());
                                                        PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                        if (titulo.getCodigoSolicitudSigno() != null) {
                                                            licaux.setRegistro(titulo.getNumeroTitulo());
                                                            licaux.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        }
                                                    }
                                                }
                                            }

                                            Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "ATTORNEY");
                                            if (apoder.getId() != null) {
                                                licaux.setApoderadoRepresentante(apoder.getName());
                                                licaux.setEmail(apoder.getEmail());
                                            }

                                            List<Person> applicants = c.getPersonsByType(rf.getId(), "APPLICANT");
                                            licaux.setLicenciante("");
                                            for (int j = 0; j < applicants.size(); j++) {
                                                String salto = ", ";
                                                if (j == applicants.size() - 1) {
                                                    salto = "";
                                                }
                                                licaux.setLicenciante(licaux.getLicenciante() + applicants.get(j).getName() + salto);
                                            }
                                            List<Person> beneficiaries = c.getPersonsByType(rf.getId(), "BENEFICIARY");
                                            licaux.setLicenciatario("");
                                            for (int j = 0; j < beneficiaries.size(); j++) {
                                                String salto = ", ";
                                                if (j == beneficiaries.size() - 1) {
                                                    salto = "";
                                                }
                                                licaux.setLicenciatario(licaux.getLicenciatario() + beneficiaries.get(j).getName() + salto);
                                            }

                                            if (licaux.getRegistro() != null && !licaux.getRegistro().trim().isEmpty()) {
                                                if (licaux.getDenominacion() != null && !licaux.getDenominacion().trim().isEmpty()) {
                                                    if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                        if (c.existsTituloCanceladoByTituloAndExpediente(licaux.getRegistro(), rf.getExpedient(), false)) {
                                                            TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(licaux.getRegistro(), rf.getExpedient());
                                                            if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                                tramitesLog += "TÍTULO DE " + licaux.getSolicitud() + "\n";
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                licaux = new LicenciaUso();
                                                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                                licaux.setCancelado(titca.getTipoCancelacion());
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                tramitesLog += "TÍTULO DE " + licaux.getSolicitud() + "\n";
                                                            } else {
                                                                tramitesLog += "TÍTULO DE " + licaux.getSolicitud() + "\n";
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                licaux = new LicenciaUso();
                                                            }
                                                        } else {
//                                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                            System.out.println("licencia de uso: " + licaux.getSolicitud() + " CARGADA CORRECTAMENTE");
                                                        }
                                                    } else {
                                                        if (c.existsTituloCanceladoByTituloAndDenominacion(licaux.getRegistro(), licaux.getDenominacion(), false)) {
                                                            TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(licaux.getRegistro(), licaux.getDenominacion());
                                                            if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                tramitesLog += "TÍTULO DE " + licaux.getSolicitud() + "\n";
                                                                licaux = new LicenciaUso();
                                                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                                licaux.setCancelado(titca.getTipoCancelacion());
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                tramitesLog += "TÍTULO DE " + licaux.getSolicitud() + "\n";
                                                            } else {
                                                                tramitesLog += "TÍTULO DE " + licaux.getSolicitud() + "\n";
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                licaux = new LicenciaUso();
                                                            }
                                                        } else {
                                                            System.out.println("licencia de uso: " + licaux.getSolicitud() + " CARGADA CORRECTAMENTE");
                                                            //msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                        }
                                                    }
                                                } else {
                                                    System.out.println(licaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                                    //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                                }

                                            } else {
                                                System.out.println(licaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                                //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                            }
                                        } else {
                                            System.out.println(licaux.getSolicitud() + ": TRÁMITE ENCONTRADO PERO NO ES UNA LICENCIA, SINO 'SUBLICENCIA'");
//                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE " + rf.getApplicationNumber() + " ES UNA SUBLICENCIA");
                                        }
                                    } else {
                                        System.out.println(licaux.getSolicitud() + ": TRÁMITE ENCONTRADO PERO NO ES UNA LICENCIA, SINO '" + t.getName().toUpperCase() + "'");
                                        //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UNA LICENCIA DE USO, SINO '" + t.getName().toUpperCase() + "'");
                                    }
                                } else {
                                    licaux = new LicenciaUso();
                                    licusopat.setSolicitud(rf.getApplicationNumber());
                                    if (t.getName().trim().toLowerCase().contains("licencia de uso")) {
                                        if (rf.getLicenseType() == null || !rf.getLicenseType().equals("SUBLICENSE")) {
                                            PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                            licusopat.setComprobante(payment.getVoucherNumber());
                                            licusopat.setFechaPresentacion(rf.getApplicationDate());
//                                        licencia.setLicenciaNo(c.getNextLicenciaUsoNo());

                                            licusopat.setSigno(ttp.getName().toUpperCase());
                                            licusopat.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");
                                            licusopat.setIdRenewalForm(rf.getId());

                                            if (rf.getDebugId() != null && rf.getDebugId() != 0) {
                                                PatentForms pf = c.getPatentFormsDepurada(rf.getDebugId());

                                                if (pf.getId() != null) {
                                                    licusopat.setDenominacion(pf.getTitle());
                                                    licusopat.setRegistro(pf.getExpedient());

                                                    if (pf.getExpYear() != null && !pf.getExpYear().trim().isEmpty()) {
                                                        licusopat.setFechaRegistro(Operaciones.convertStringToDate(pf.getExpYear() + "-01-02"));
                                                    }

                                                    if (licusopat.getRegistro() != null && !licusopat.getRegistro().trim().isEmpty()) {
                                                        PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByNumeroTitulo(licusopat.getRegistro());
                                                        if (titulo.getCodigoSolicitudSigno() != null) {
                                                            licusopat.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        }

                                                        PpdiSolicitudPatente patsol = c.getPpdiSolicitudPatenteByTramite(licusopat.getSolicitud());
                                                        if (patsol.getCodigoSolicitudPatente() != null) {
                                                            licusopat.setClaseInternacional(patsol.getClasificacionInternacional());
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                    PpdiSolicitudPatente ps = c.getPpdiSolicitudPatenteByExpedient(rf.getExpedient());
                                                    if (ps.getCodigoSolicitudPatente() != null) {
                                                        licusopat.setDenominacion(ps.getTitulo());
                                                        PpdiTituloPatente titulo = c.getPpdiTituloPatenteByCodigoSolicitudPatente(ps.getCodigoSolicitudPatente());
                                                        if (titulo.getCodigoSolicitudPatente() != null) {
                                                            licusopat.setRegistro(titulo.getNumeroTitulo());
                                                            licusopat.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        }
                                                    }
                                                }
                                            }

                                            //apoderado
                                            List<PersonRenewal> apoderados = c.getPersonRenewalByIdRenewalAndType(rf.getId(), "'AGENT','ATTORNEY'");
                                            if (!apoderados.isEmpty()) {
                                                licusopat.setApoderadoRepresentante(c.getNamesFromPersonRenewal(apoderados));
                                                licusopat.setEmail(c.getEmailsFromPersonRenewal(apoderados));
                                            }

                                            //titulares
                                            List<PersonRenewalName> tit_actuales = c.getTitularesActuales(rf.getId());
                                            if (!tit_actuales.isEmpty()) {
                                                licusopat.setLicenciante(c.getNamesFromPersonRenewalName(tit_actuales, false));
                                            }

                                            //beneficiarios
                                            List<PersonRenewal> beneficiaries = c.getPersonRenewalByIdRenewalAndType(rf.getId(), "'BENEFICIARY'");
                                            if (!beneficiaries.isEmpty()) {
                                                licusopat.setLicenciatario(c.getNamesFromPersonRenewal(beneficiaries));
                                            }

                                            if (licusopat.getRegistro() != null && !licusopat.getRegistro().trim().isEmpty()) {
                                                if (licusopat.getDenominacion() != null && !licusopat.getDenominacion().trim().isEmpty()) {
                                                    if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                        if (c.existsTituloCanceladoByTituloAndExpediente(licusopat.getRegistro(), rf.getExpedient(), false)) {
                                                            TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(licusopat.getRegistro(), rf.getExpedient());
                                                            if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licusopat.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licusopat.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                tramitesLog += "TÍTULO DE " + licaux.getSolicitud() + " CANCELADO\n";
                                                                licusopat = new LicenciaUsoPat();
                                                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                                licusopat.setCancelado(titca.getTipoCancelacion());
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licusopat.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licusopat.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                tramitesLog += "TÍTULO DE " + licaux.getSolicitud() + " CANCELADO\n";
                                                            } else {
                                                                tramitesLog += "TÍTULO DE " + licaux.getSolicitud() + " CANCELADO\n";
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licusopat.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licusopat.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                licusopat = new LicenciaUsoPat();
                                                            }
                                                        } else {
//                                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                            System.out.println("licencia de uso: " + licaux.getSolicitud() + " CARGADA CORRECTAMENTE");
                                                        }
                                                    } else {
                                                        if (c.existsTituloCanceladoByTituloAndDenominacion(licusopat.getRegistro(), licusopat.getDenominacion(), false)) {
                                                            TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(licusopat.getRegistro(), licusopat.getDenominacion());
                                                            if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licusopat.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licusopat.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                tramitesLog += "TÍTULO DE " + licaux.getSolicitud() + " CANCELADO\n";
                                                                licusopat = new LicenciaUsoPat();
                                                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                                licusopat.setCancelado(titca.getTipoCancelacion());
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licusopat.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licusopat.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                tramitesLog += "TÍTULO DE " + licaux.getSolicitud() + " CANCELADO\n";
                                                            } else {
                                                                tramitesLog += "TÍTULO DE " + licaux.getSolicitud() + " CANCELADO\n";
//                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licusopat.getRegistro() + " CON DENOMINACIÓN "
//                                                                        + licusopat.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                licusopat = new LicenciaUsoPat();
                                                            }
                                                        } else {
                                                            System.out.println("licencia de uso: " + licaux.getSolicitud() + " CARGADA CORRECTAMENTE");
//                                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                        }
                                                    }
                                                } else {
                                                    //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                                    System.out.println(licaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                                }

                                            } else {
                                                System.out.println(licaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                                //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                            }
                                        } else {
                                            System.out.println(licaux.getSolicitud() + ": EL TRÁMITE " + rf.getApplicationNumber() + " ES UNA SUBLICENCIA");
                                            //msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE " + rf.getApplicationNumber() + " ES UNA SUBLICENCIA");
                                        }
                                    } else {
                                        System.out.println(licaux.getSolicitud() + " TRÁMITE ENCONTRADO PERO NO ES UNA LICENCIA DE USO, SINO '" + t.getName().toUpperCase() + "'");
//                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UNA LICENCIA DE USO, SINO '" + t.getName().toUpperCase() + "'");
                                    }

                                    //msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE ES UN " + ttp.getName().toUpperCase());
                                }
                            } else {
                                //msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE PRESENTA UN PROBLEMA DE IDENTIDAD");
                                tramitesLog += licusopat.getSolicitud() + ": EL TRÁMITE PRESENTA UN PROBLEMA DE IDENTIDAD\n";
                            }

                        } else {
                            //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO, PERO NO REGISTRA INICIO DE PROCESO");
                            tramitesLog += licusopat.getSolicitud() + ":TRÁMITE ENCONTRADO, PERO NO REGISTRA INICIO DE PROCESO\n";
                        }
                    }

                    if (licaux.getSolicitud() != null) {
                        licaux.setFechaLicencia(new Date());
                        licaux.setLicenciaNo(c.getNextLicenciaUsoNo());
                        licaux.setSolicitud(licaux.getSolicitud().trim().toUpperCase());
                        licaux.setTipoEstado("LICENCIA");
                        licaux.setResponsable(loginBean.getUsuario().getAlias());
                        if (c.saveLicenciaUso(licaux)) {
                            ModificacionApp mapp = new ModificacionApp();
                            mapp.setDenominacion(licaux.getDenominacion() != null ? licaux.getDenominacion() : "");
                            mapp.setFecha(new Timestamp(new Date().getTime()));
                            mapp.setObservacion("rezago pasado");
                            mapp.setRegistro(licaux.getRegistro());
                            mapp.setSolicitud(licaux.getSolicitud());
                            mapp.setTipo("LICENCIA DE USO");
                            mapp.setUsuario(loginBean.getNombre());
                            mapp.setModo("MARCA");
                            mapp.setActivo(true);
//
                            if (c.saveModificacionApp(mapp)) {
                                c.saveHistorial("LICENCIA DE USO", "LICENCIA DE USO", licaux.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            }
                        }
                    } else if (licusopat.getSolicitud() != null) {
                        licusopat.setFechaLicencia(new Date());
                        licusopat.setLicenciaNo(c.getNextLicenciaUsoPatCertificado());
                        licusopat.setSolicitud(licusopat.getSolicitud().trim().toUpperCase());
                        licusopat.setTipoEstado("CERTIFICADO");
                        licusopat.setResponsable(loginBean.getUsuario().getAlias());
                        if (c.saveLicenciaUsoPat(licusopat)) {
                            ModificacionApp mapp = new ModificacionApp();
                            mapp.setDenominacion(licusopat.getDenominacion() != null ? licusopat.getDenominacion() : "");
                            mapp.setFecha(new Timestamp(new Date().getTime()));
                            mapp.setObservacion("rezago pasado");
                            mapp.setRegistro(licusopat.getRegistro());
                            mapp.setSolicitud(licusopat.getSolicitud());
                            mapp.setTipo("LICENCIA DE USO");
                            mapp.setUsuario(loginBean.getNombre());
                            mapp.setModo("PATENTE");
                            mapp.setActivo(true);
                            if (c.saveModificacionApp(mapp)) {
                                c.saveHistorialPat("LICENCIA DE USO", "LICENCIA DE USO", licusopat.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            }
                        }
                    }
                }
                int n = i + 1;
                valorProgessBar = (n * 100) / selectedLicenciasUso.size();
//                System.out.println("valorprog: " + valorProgessBar);
                //Thread.sleep(500);
            }
            valorProgessBar = 100;
        }
        valorProgessBar = 100;
    }

    public void onComplete() {

        if (tramitesLog.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("SE HAN IMPORTADO CORRECTAMENTE LAS LICENCIAS DE USO"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", tramitesLog));
        }
        loadLicenciasUsoRezagadas();
    }

    public void cargarRezago(ActionEvent ae) {
        Controlador c = new Controlador();
        List<RenewalForm> renewals = c.getLicenciasRezagoBytType("LICENCIA DE USO", "27");
        licenciasUso = c.loadLicenciaUsoFromRenewals(renewals);
        botonpasar = !licenciasUso.isEmpty();
        numRegistros = "Número Registros Mostrados: " + licenciasUso.size();

    }

    public void buscarRezagoLicenciaUCriterio(ActionEvent ae) {
        FacesMessage msg = null;
        if (!criterio.trim().isEmpty() && criterio.trim().length() > 3) {
            Controlador c = new Controlador();
            List<RenewalForm> renewals = c.getLicenciasRezagoBytTypeAndCriteria("LICENCIA DE USO", "27", criterio.trim().toUpperCase());
            licenciasUso = c.loadLicenciaUsoFromRenewals(renewals);
            botonpasar = !licenciasUso.isEmpty();
            numRegistros = "Número Registros Mostrados: " + licenciasUso.size();
            if (licenciasUso.isEmpty()) {
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
            List<RenewalForm> renewals = c.getLicenciasRezagoBytTypeAndFecha("LICENCIA DE USO", "27", fechaInicio, fechaFin);
            licenciasUso = c.loadLicenciaUsoFromRenewals(renewals);
            botonpasar = !licenciasUso.isEmpty();
            numRegistros = "Número Registros Mostrados: " + licenciasUso.size();
            if (licenciasUso.isEmpty()) {
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
        licenciaUso = (LicenciaUso) licenciaUsoDataTable.getRowData();
//        System.out.println("hereeeeeeee");
        if (licenciaUso != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + licenciaUso.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(licenciaUso.getIdRenewalForm(), licenciaUso.getSolicitud());
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
        if (selectedLicenciasUso != null && !selectedLicenciasUso.isEmpty()) {
            Controlador c = new Controlador();
            for (int i = 0; i < selectedLicenciasUso.size(); i++) {

                LicenciaUso licus = selectedLicenciasUso.get(i);

                ModificacionApp map = new ModificacionApp();
                map.setDenominacion("");
                map.setFecha(new Timestamp(new Date().getTime()));
                map.setObservacion(licus.getRo());
                map.setRegistro(licus.getRegistro());
                map.setSolicitud(licus.getSolicitud());
                map.setTipo("LICENCIA DE USO");
                map.setUsuario(loginBean.getNombre());
                map.setModo(licus.getR1());
                map.setActivo(false);
                c.saveModificacionApp(map);
            }

            loadLicenciasUsoRezagadas();
            PrimeFaces.current().ajax().addCallbackParam("quitado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS SELECCIONADOS FUERON REMOVIDOS DE REZAGO");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararQuitar(ActionEvent ae) {
        FacesMessage msg;
        if (selectedLicenciasUso != null && !selectedLicenciasUso.isEmpty()) {
            for (int i = 0; i < selectedLicenciasUso.size(); i++) {
                selectedLicenciasUso.get(i).setRo("El trámite no pertenece a la Dirección de Modificaciones");
            }
            dialogTitle = "QUITAR DE REZAGADOS " + selectedLicenciasUso.size() + " TRÁMITES?";
            PrimeFaces.current().ajax().addCallbackParam("pasado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS SELECCIONADOS FUERON  CARGADOS CORRECTAMENTE");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void pasarSeleccionados(ActionEvent ae) {
        FacesMessage msg;
        if (selectedLicenciasUso != null && !selectedLicenciasUso.isEmpty()) {
            dialogTitle = "PASAR " + selectedLicenciasUso.size() + " TRÁMITES A LICENCIAS DE USO?";
            PrimeFaces.current().ajax().addCallbackParam("pasado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS HAN SIDO CARGADOS CORRECTAMENTE");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return the licenciasUso
     */
    public List<LicenciaUso> getLicenciasUso() {
        return licenciasUso;
    }

    /**
     * @param licenciasUso the licenciasUso to set
     */
    public void setLicenciasUso(List<LicenciaUso> licenciasUso) {
        this.licenciasUso = licenciasUso;
    }

    /**
     * @return the licenciasUsoFiltradas
     */
    public List<LicenciaUso> getLicenciasUsoFiltradas() {
        return licenciasUsoFiltradas;
    }

    /**
     * @param licenciasUsoFiltradas the licenciasUsoFiltradas to set
     */
    public void setLicenciasUsoFiltradas(List<LicenciaUso> licenciasUsoFiltradas) {
        this.licenciasUsoFiltradas = licenciasUsoFiltradas;
    }

    /**
     * @return the selectedLicenciasUso
     */
    public List<LicenciaUso> getSelectedLicenciasUso() {
        return selectedLicenciasUso;
    }

    /**
     * @param selectedLicenciasUso the selectedLicenciasUso to set
     */
    public void setSelectedLicenciasUso(List<LicenciaUso> selectedLicenciasUso) {
        this.selectedLicenciasUso = selectedLicenciasUso;
    }

    /**
     * @return the licenciaUsoDataTable
     */
    public UIData getLicenciaUsoDataTable() {
        return licenciaUsoDataTable;
    }

    /**
     * @param licenciaUsoDataTable the licenciaUsoDataTable to set
     */
    public void setLicenciaUsoDataTable(UIData licenciaUsoDataTable) {
        this.licenciaUsoDataTable = licenciaUsoDataTable;
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
     * @return the licenciaUso
     */
    public LicenciaUso getLicenciaUso() {
        return licenciaUso;
    }

    /**
     * @param licenciaUso the licenciaUso to set
     */
    public void setLicenciaUso(LicenciaUso licenciaUso) {
        this.licenciaUso = licenciaUso;
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
