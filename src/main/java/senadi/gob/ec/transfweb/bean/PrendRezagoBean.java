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
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepform.ModificacionApp;
import senadi.gob.ec.transfweb.model.iepform.PaymentReceipt;
import senadi.gob.ec.transfweb.model.iepform.Person;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepform.Types;
import senadi.gob.ec.transfweb.model.prenda.PrendaComercial;
import senadi.gob.ec.transfweb.modelp.PpdiPersona;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author michael
 */
@ManagedBean(name = "prendrezBean")
@ViewScoped
public class PrendRezagoBean implements Serializable {

    private List<PrendaComercial> prendasComerciales;
    private List<PrendaComercial> prendasComercialesFiltradas;
    private List<PrendaComercial> selectedPrendasComerciales;
    private UIData prendaComercialDataTable;

    private PrendaComercial prendaComercial;
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

    public PrendRezagoBean() {
        loadPrendasComercialesRezagadas();
    }

    private void loadPrendasComercialesRezagadas() {
        prendasComerciales = new ArrayList<>();
        Controlador c = new Controlador();
        tramitesLog = "";
        loginBean = c.getLogin();
    }

    public void longRunning() throws InterruptedException {
        valorProgessBar = 0;
//        Integer k = valorProgessBar;
        if (!selectedPrendasComerciales.isEmpty()) {
            Controlador c = new Controlador();
            for (int i = 0; i < selectedPrendasComerciales.size(); i++) {

                PrendaComercial prendaux = selectedPrendasComerciales.get(i);//prendaaux

                if (c.existePrendaComercial(prendaux.getSolicitud())) {
                    tramitesLog += "TRÁMITE " + prendaux.getSolicitud() + " YA EXISTE EN PRENDAS COMERCIALES\n";
                    System.out.println("tramite: " + tramitesLog);
                } else {
                    RenewalForm rf = c.getRenewalFormsByApplicationNumber(prendaux.getSolicitud());

                    if (rf.getId() != null) {
                        if (rf.getStatus().equals("DELIVERED")) {

                            Types t = c.getTypes(rf.getTransactionMotiveId());

                            Types ttp = c.getTypes(rf.getFormId());

                            if (t.getId() != null && ttp.getId() != null) {
                                if (!ttp.getAlias().equals("PI") && !ttp.getAlias().equals("MU") && !ttp.getAlias().equals("DI")) {
                                    if (t.getName().trim().toLowerCase().contains("prenda comercial")) {

                                        PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                        prendaux.setComprobante(payment.getVoucherNumber());
                                        prendaux.setFechaPresentacion(rf.getApplicationDate());
                                        //certificado.setCertificado(c.getNextNumeroCertificadoCD());
                                        prendaux.setPrendaNo(c.getNextPrendaComercialPrendaNo());

                                        prendaux.setSigno(ttp.getAlias());
                                        prendaux.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");
                                        prendaux.setIdRenewalForm(rf.getId());

                                        if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                            HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                            if (hf.getId() != null) {
                                                prendaux.setDenominacion(hf.getDenomination());
                                                prendaux.setRegistro(hf.getExpedient());

                                                if (prendaux.getRegistro() != null && !prendaux.getRegistro().trim().isEmpty()) {
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(prendaux.getRegistro(), prendaux.getDenominacion());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        prendaux.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        prendaux.setTitularAnterior(titulo.getTitular());
                                                    }
                                                }
                                                if (prendaux.getFechaRegistro() == null) {
                                                    prendaux.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                }
                                            }
                                        } else {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                if (ps.getCodigoSolicitudSigno() != null) {
                                                    prendaux.setDenominacion(ps.getDenominacionSigno());
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        prendaux.setRegistro(titulo.getNumeroTitulo());
                                                        prendaux.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        prendaux.setTitularAnterior(titulo.getTitular());
                                                    }
                                                }
                                                if (prendaux.getTitularAnterior() == null || prendaux.getTitularAnterior().trim().isEmpty()) {
                                                    PpdiPersona persona = c.getASolicitanteByCodigoSolicitud(ps.getCodigoSolicitudSigno());
                                                    if (persona.getCodigoPersona() != null) {
                                                        prendaux.setTitularAnterior(persona.getNombrePersona());
                                                    }
                                                }
                                            }
                                        }

                                        Person titAct = c.getTitularActual(rf.getId());
                                        if (titAct.getId() != null) {
                                            prendaux.setTitularActual(titAct.getName());
                                            prendaux.setDomicilioTitularActual(titAct.getAddress());
                                            prendaux.setIdentificacion(titAct.getIdentificationNumber());
                                        }

                                        Person abog = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "LAWYER");
                                        if (abog.getId() != null) {
                                            prendaux.setAbogadoPatrocinador(abog.getName());
                                            prendaux.setEmail(abog.getEmail());
                                        }

                                        Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "AGENT");
                                        if (apoder.getId() != null) {
                                            prendaux.setTitApodRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "AGENT"));
                                        }

                                        Person acreed = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "BENEFICIARY");
                                        if (acreed.getId() != null) {
                                            prendaux.setPrendariaAcreedora(acreed.getName());
                                        }
                                        Person deudP = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "APPLICANT");
                                        if (deudP.getId() != null) {
                                            prendaux.setDeudoraPrendaria(deudP.getName());
                                        }
                                        if ((prendaux.getRegistro() == null || prendaux.getRegistro().trim().isEmpty()) && rf.getTransactionNumber() != null && !rf.getTransactionNumber().trim().isEmpty()) {
                                            prendaux.setRegistro(rf.getTransactionNumber());
                                        }

                                        if (prendaux.getRegistro() != null && !prendaux.getRegistro().trim().isEmpty()) {
                                            if (prendaux.getDenominacion() != null && !prendaux.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(prendaux.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(prendaux.getRegistro(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
//                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + prendaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                    + prendaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            tramitesLog += "TÍTULO DE " + prendaux.getSolicitud() + "\n";
                                                            prendaux = new PrendaComercial();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            prendaux.setCancelado(titca.getTipoCancelacion());
//                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + prendaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                    + prendaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            tramitesLog += "TÍTULO DE " + prendaux.getSolicitud() + "\n";
                                                        } else {
//                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + prendaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                    + prendaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            tramitesLog += "TÍTULO DE " + prendaux.getSolicitud() + "\n";
                                                            prendaux = new PrendaComercial();
                                                        }
                                                    } else {
                                                        System.out.println("prenda comercial: " + prendaux.getSolicitud() + " CARGADA CORRECTAMENTE");
//                                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(prendaux.getRegistro(), prendaux.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(prendaux.getRegistro(), prendaux.getDenominacion());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
//                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + prendaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                    + prendaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            tramitesLog += "TÍTULO DE " + prendaux.getSolicitud() + "\n";
                                                            prendaux = new PrendaComercial();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            prendaux.setCancelado(titca.getTipoCancelacion());
                                                            tramitesLog += "TÍTULO DE " + prendaux.getSolicitud() + "\n";
//                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + prendaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                    + prendaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
//                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + prendaux.getRegistro() + " CON DENOMINACIÓN "
//                                                                    + prendaux.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            tramitesLog += "TÍTULO DE " + prendaux.getSolicitud() + "\n";
                                                            prendaux = new PrendaComercial();
                                                        }
                                                    } else {
                                                        System.out.println("prenda comercial: " + prendaux.getSolicitud() + " CARGADA CORRECTAMENTE");
                                                        //msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                }

                                            } else {
                                                //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                                System.out.println(prendaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ LA DENOMINACIÓN");
                                            }

                                        } else {
                                            //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                            System.out.println(prendaux.getSolicitud() + ": DATOS CARGADOS, PERO NO SE ENCONTRÓ EL NÚMERO DE TÍTULO");
                                        }

                                    } else {
                                        //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UNA PRENDA COMERCIAL, SINO '" + t.getName().toUpperCase() + "'");
                                        System.out.println(prendaux.getSolicitud() + ": TRÁMITE ENCONTRADO PERO NO ES UN PRENDA COMERCIAL, SINO '" + t.getName().toUpperCase() + "'");
                                    }
                                } else {
                                    System.out.println("EL TRÁMITE ES UN " + ttp.getName().toUpperCase());
                                    //msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE ES UN " + ttp.getName().toUpperCase());
                                }
                            } else {
                                tramitesLog += prendaux.getSolicitud() + ": EL TRÁMITE PRESENTA UN PROBLEMA DE IDENTIDAD\n";
                                //msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE PRESENTA UN PROBLEMA DE IDENTIDAD");
                            }

                        } else {
                            tramitesLog += prendaux.getSolicitud() + ":TRÁMITE ENCONTRADO, PERO NO REGISTRA INICIO DE PROCESO\n";
                            //msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO, PERO NO REGISTRA INICIO DE PROCESO");
                        }
                    }

                    
                    if (prendaux.getSolicitud() != null) {
                        prendaux.setFechaCertificado(new Date());
                        prendaux.setPrendaNo(c.getNextPrendaComercialPrendaNo());
                        prendaux.setSolicitud(prendaux.getSolicitud().trim().toUpperCase());
                        prendaux.setTipoEstado("CERTIFICADO");
                        prendaux.setResponsable(loginBean.getUsuario().getAlias());
                        if (c.savePrendaComercial(prendaux)) {
                            ModificacionApp mapp = new ModificacionApp();
                            mapp.setDenominacion(prendaux.getDenominacion() != null ? prendaux.getDenominacion() : "");
                            mapp.setFecha(new Timestamp(new Date().getTime()));
                            mapp.setObservacion("rezago pasado");
                            mapp.setRegistro(prendaux.getRegistro());
                            mapp.setSolicitud(prendaux.getSolicitud());
                            mapp.setTipo("PRENDA COMERCIAL");
                            mapp.setUsuario(loginBean.getNombre());
                            mapp.setModo("MARCA");
                            mapp.setActivo(true);
//
                            if (c.saveModificacionApp(mapp)) {
                                c.saveHistorial("PRENDA COMERCIAL", "PRENDA COMERCIAL", prendaux.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            }
                        }
                    }
                }
                int n = i + 1;
                valorProgessBar = (n * 100) / selectedPrendasComerciales.size();
//                System.out.println("valorprog: " + valorProgessBar);
                //Thread.sleep(500);
            }
            valorProgessBar = 100;
        }
        valorProgessBar = 100;
    }

    public void onComplete() {

        if (tramitesLog.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("SE HAN IMPORTADO CORRECTAMENTE LAS PRENDAS COMERCIALES"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", tramitesLog));
        }
        loadPrendasComercialesRezagadas();
    }

    public void cargarRezago(ActionEvent ae) {
        Controlador c = new Controlador();
        List<RenewalForm> renewals = c.getRenewalsRezagoBytType("PRENDA COMERCIAL", "28");
        prendasComerciales = c.loadPrendasComercialesFromRenewals(renewals);
        botonpasar = !prendasComerciales.isEmpty();
        numRegistros = "Número Registros Mostrados: " + prendasComerciales.size();

    }

    public void buscarRezagoPrendaComercialCriterio(ActionEvent ae) {
        FacesMessage msg = null;
        if (!criterio.trim().isEmpty() && criterio.trim().length() > 3) {
            Controlador c = new Controlador();
            List<RenewalForm> renewals = c.getRenewalsRezagoBytTypeAndCriterio("PRENDA COMERCIAL", "28", criterio.trim().toUpperCase());
            prendasComerciales = c.loadPrendasComercialesFromRenewals(renewals);
            botonpasar = !prendasComerciales.isEmpty();
            numRegistros = "Número Registros Mostrados: " + prendasComerciales.size();
            if (prendasComerciales.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS.");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO VÁLIDO (MÁS DE 3 CARACTERES)");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarPrendasComercialesPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            List<RenewalForm> renewals = c.getRenewalsRezagoBytTypeAndFecha("PRENDA COMERCIAL", "28", fechaInicio, fechaFin);
            prendasComerciales = c.loadPrendasComercialesFromRenewals(renewals);
            botonpasar = !prendasComerciales.isEmpty();
            numRegistros = "Número Registros Mostrados: " + prendasComerciales.size();
            if (prendasComerciales.isEmpty()) {
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
        prendaComercial = (PrendaComercial) prendaComercialDataTable.getRowData();
//        System.out.println("hereeeeeeee");
        if (prendaComercial != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + prendaComercial.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(prendaComercial.getIdRenewalForm(), prendaComercial.getSolicitud());
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
        if (selectedPrendasComerciales != null && !selectedPrendasComerciales.isEmpty()) {
            Controlador c = new Controlador();
            for (int i = 0; i < selectedPrendasComerciales.size(); i++) {

                PrendaComercial prendc = selectedPrendasComerciales.get(i);

                ModificacionApp map = new ModificacionApp();
                map.setDenominacion("");
                map.setFecha(new Timestamp(new Date().getTime()));
                map.setObservacion(prendc.getRo());
                map.setRegistro(prendc.getRegistro());
                map.setSolicitud(prendc.getSolicitud());
                map.setTipo("PRENDA COMERCIAL");
                map.setUsuario(loginBean.getNombre());
                map.setModo(prendc.getR1());
                map.setActivo(false);
                c.saveModificacionApp(map);
            }

            loadPrendasComercialesRezagadas();
            PrimeFaces.current().ajax().addCallbackParam("quitado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS SELECCIONADOS FUERON REMOVIDOS DE REZAGO");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararQuitar(ActionEvent ae) {
        FacesMessage msg;
        if (selectedPrendasComerciales != null && !selectedPrendasComerciales.isEmpty()) {
            for (int i = 0; i < selectedPrendasComerciales.size(); i++) {
                selectedPrendasComerciales.get(i).setRo("El trámite no pertenece a la Dirección de Modificaciones");
            }
            dialogTitle = "QUITAR DE REZAGADOS " + selectedPrendasComerciales.size() + " TRÁMITES?";
            PrimeFaces.current().ajax().addCallbackParam("pasado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS SELECCIONADOS FUERON  CARGADOS CORRECTAMENTE");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void pasarSeleccionados(ActionEvent ae) {
        FacesMessage msg;
        if (selectedPrendasComerciales != null && !selectedPrendasComerciales.isEmpty()) {
            dialogTitle = "PASAR " + selectedPrendasComerciales.size() + " TRÁMITES A PRENDAS COMERCIALES?";
            PrimeFaces.current().ajax().addCallbackParam("pasado", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TODOS LOS REGISTROS HAN SIDO CARGADOS CORRECTAMENTE");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return the prendasComerciales
     */
    public List<PrendaComercial> getPrendasComerciales() {
        return prendasComerciales;
    }

    /**
     * @param prendasComerciales the prendasComerciales to set
     */
    public void setPrendasComerciales(List<PrendaComercial> prendasComerciales) {
        this.prendasComerciales = prendasComerciales;
    }

    /**
     * @return the prendasComercialesFiltradas
     */
    public List<PrendaComercial> getPrendasComercialesFiltradas() {
        return prendasComercialesFiltradas;
    }

    /**
     * @param prendasComercialesFiltradas the prendasComercialesFiltradas to set
     */
    public void setPrendasComercialesFiltradas(List<PrendaComercial> prendasComercialesFiltradas) {
        this.prendasComercialesFiltradas = prendasComercialesFiltradas;
    }

    /**
     * @return the selectedPrendasComerciales
     */
    public List<PrendaComercial> getSelectedPrendasComerciales() {
        return selectedPrendasComerciales;
    }

    /**
     * @param selectedPrendasComerciales the selectedPrendasComerciales to set
     */
    public void setSelectedPrendasComerciales(List<PrendaComercial> selectedPrendasComerciales) {
        this.selectedPrendasComerciales = selectedPrendasComerciales;
    }

    /**
     * @return the prendaComercialDataTable
     */
    public UIData getPrendaComercialDataTable() {
        return prendaComercialDataTable;
    }

    /**
     * @param prendaComercialDataTable the prendaComercialDataTable to set
     */
    public void setPrendaComercialDataTable(UIData prendaComercialDataTable) {
        this.prendaComercialDataTable = prendaComercialDataTable;
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
     * @return the prendaComercial
     */
    public PrendaComercial getPrendaComercial() {
        return prendaComercial;
    }

    /**
     * @param prendaComercial the prendaComercial to set
     */
    public void setPrendaComercial(PrendaComercial prendaComercial) {
        this.prendaComercial = prendaComercial;
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
