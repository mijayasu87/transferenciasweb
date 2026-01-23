/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
import senadi.gob.ec.transfweb.model.Documento;
import senadi.gob.ec.transfweb.model.Historial;
import senadi.gob.ec.transfweb.model.TituloCancelado;
import senadi.gob.ec.transfweb.model.UploadNotificacion;
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepform.PaymentReceipt;
import senadi.gob.ec.transfweb.model.iepform.Person;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepform.Types;
import senadi.gob.ec.transfweb.model.iepicas.Owner;
import senadi.gob.ec.transfweb.model.prenda.PrendaComercial;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author micharesp
 */
@ManagedBean(name = "certificadoPrendaBean")
@ViewScoped
public class CertificadoPrendaBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private Date fechaInicioCertificado;
    private Date fechaFinCertificado;

    private List<PrendaComercial> certificados;
    private List<PrendaComercial> certificadosFiltradas;

    private UIData certificadosDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private PrendaComercial certificado;

    private LoginBean loginBean;

    private String estadoTemp;
    private String historial;

    private String exportName;

    private List<PrendaComercial> selectedCertificados;

    private boolean separado;

    private List<Documento> archivos;

    private String criterioOwner;
    private List<Owner> owners;
    private List<Owner> ownersFiltrados;
    private UIData ownerDataTable;

    public CertificadoPrendaBean() {
        loadCertificadosPrendaComercial();
    }

    private void loadCertificadosPrendaComercial() {
        Controlador c = new Controlador();
        certificados = c.getPrendasComercialesByTipo("CERTIFICADO");
        numRegistros = "Número Registros Mostrados: " + certificados.size();
        exportName = "certificado_prenda_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
        selectedCertificados = new ArrayList<>();
        owners = new ArrayList<>();
        criterioOwner = "";
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;
//        System.out.println("LLegando por aquí");
        if (certificado != null && certificado.getSolicitud() != null && !certificado.getSolicitud().trim().isEmpty()) {

            String tramite = certificado.getSolicitud();
            Controlador c = new Controlador();
            if (c.existePrendaComercial(tramite)) {
                PrendaComercial aux = c.getPrendaComercialBySolicitud(tramite);
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL TRÁMITE YA SE ENCUENTRA REGISTRADO EN LA PESTAÑA DE " + aux.getTipoEstado());
            } else {

                RenewalForm rf = c.getRenewalFormsByApplicationNumber(tramite);

                if (rf.getId() != null) {
                    if (rf.getStatus().equals("DELIVERED")) {

                        Types t = c.getTypes(rf.getTransactionMotiveId());

                        Types ttp = c.getTypes(rf.getFormId());

                        if (t.getId() != null && ttp.getId() != null) {
                            if (!ttp.getAlias().equals("PI") && !ttp.getAlias().equals("MU") && !ttp.getAlias().equals("DI")) {
                                if (t.getName().trim().toLowerCase().contains("prenda comercial")) {

                                    PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                    certificado.setComprobante(payment.getVoucherNumber());
                                    certificado.setFechaPresentacion(rf.getApplicationDate());
                                    //certificado.setCertificado(c.getNextNumeroCertificadoCD());
//                                    certificado.setPrendaNo(c.getNextPrendaComercialPrendaNo());

                                    certificado.setSigno(ttp.getAlias());
                                    certificado.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");
                                    certificado.setIdRenewalForm(rf.getId());

                                    if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                        HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                        if (hf.getId() != null) {
                                            certificado.setDenominacion(hf.getDenomination());
                                            certificado.setRegistro(hf.getExpedient());

                                            if (certificado.getRegistro() != null && !certificado.getRegistro().trim().isEmpty()) {
                                                PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(certificado.getRegistro(), certificado.getDenominacion());
                                                if (titulo.getCodigoSolicitudSigno() != null) {
                                                    certificado.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                    certificado.setTitularAnterior(titulo.getTitular());
                                                }
                                            }
                                            if (certificado.getFechaRegistro() == null) {
                                                certificado.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                            }
                                        }
                                    } else {
                                        if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                            PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                            if (ps.getCodigoSolicitudSigno() != null) {
                                                certificado.setDenominacion(ps.getDenominacionSigno());
                                                PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                if (titulo.getCodigoSolicitudSigno() != null) {
                                                    certificado.setRegistro(titulo.getNumeroTitulo());
                                                    certificado.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                    certificado.setTitularAnterior(titulo.getTitular());
                                                }
                                            }
                                            if (certificado.getTitularAnterior() == null || certificado.getTitularAnterior().trim().isEmpty()) {
                                                PpdiPersona persona = c.getASolicitanteByCodigoSolicitud(ps.getCodigoSolicitudSigno());
                                                if (persona.getCodigoPersona() != null) {
                                                    certificado.setTitularAnterior(persona.getNombrePersona());
                                                }
                                            }
                                        }
                                    }

                                    Person titAct = c.getTitularActual(rf.getId());
                                    if (titAct.getId() != null) {
                                        certificado.setTitularActual(titAct.getName());
                                        certificado.setDomicilioTitularActual(titAct.getAddress());
                                        certificado.setIdentificacion(titAct.getIdentificationNumber());
                                    }

                                    Person abog = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "LAWYER");
                                    if (abog.getId() != null) {
                                        certificado.setAbogadoPatrocinador(abog.getName());
                                        certificado.setEmail(abog.getEmail());
                                    }

                                    Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "AGENT");
                                    if (apoder.getId() != null) {
                                        certificado.setTitApodRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "AGENT"));
                                    }

                                    Person acreed = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "BENEFICIARY");
                                    if (acreed.getId() != null) {
                                        certificado.setPrendariaAcreedora(acreed.getName());
                                    }
                                    Person deudP = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "APPLICANT");
                                    if (deudP.getId() != null) {
                                        certificado.setDeudoraPrendaria(deudP.getName());
                                    }
                                    if ((certificado.getRegistro() == null || certificado.getRegistro().trim().isEmpty()) && rf.getTransactionNumber() != null && !rf.getTransactionNumber().trim().isEmpty()) {
                                        certificado.setRegistro(rf.getTransactionNumber());
                                    }

                                    if (certificado.getRegistro() != null && !certificado.getRegistro().trim().isEmpty()) {
                                        if (certificado.getDenominacion() != null && !certificado.getDenominacion().trim().isEmpty()) {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                if (c.existsTituloCanceladoByTituloAndExpediente(certificado.getRegistro(), rf.getExpedient(), false)) {
                                                    TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(certificado.getRegistro(), rf.getExpedient());
                                                    if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN "
                                                                + certificado.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        certificado = new PrendaComercial();
                                                    } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                        certificado.setCancelado(titca.getTipoCancelacion());
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN "
                                                                + certificado.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN "
                                                                + certificado.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        certificado = new PrendaComercial();
                                                    }
                                                } else {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                }
                                            } else {
                                                if (c.existsTituloCanceladoByTituloAndDenominacion(certificado.getRegistro(), certificado.getDenominacion(), false)) {
                                                    TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(certificado.getRegistro(), certificado.getDenominacion());
                                                    if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN "
                                                                + certificado.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        certificado = new PrendaComercial();
                                                    } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                        certificado.setCancelado(titca.getTipoCancelacion());
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN "
                                                                + certificado.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN "
                                                                + certificado.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        certificado = new PrendaComercial();
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
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UNA PRENDA COMERCIAL, SINO '" + t.getName().toUpperCase() + "'");
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

    public void buscarCertificadosPrendas(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            certificados = c.getPrendasComercialesByCriteriaAndType(criterio, "CERTIFICADO");
            numRegistros = "Número Registros Mostrados: " + certificados.size();
            if (certificados.isEmpty()) {
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

    public void validarCertificado(PrendaComercial cambi) {
        FacesMessage msg = null;
        if (cambi != null) {
            String rutaNotificacionCasillero = "";
            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionBySolicitud(cambi.getSolicitud(), true);
            if (!uploads.isEmpty()) {
                if (uploads.size() > 1) {
                    for (int i = 0; i < uploads.size(); i++) {
                        UploadNotificacion unaux = uploads.get(i);
                        String rutaux = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                        int conf = Operaciones.validaTextoEnPdf(rutaux, "INSCRIPCIÓN PRENDA COMERCIAL");
                        if (conf == 1) {
                            rutaNotificacionCasillero = rutaux;
                            break;
                        } else {
                            conf = Operaciones.validaTextoEnPdf(rutaux, "INSCRIPCIÓN DE CONTRATO DE PRENDA");
                            if (conf == 1) {
                                rutaNotificacionCasillero = rutaux;
                                break;
                            } else {
                                conf = Operaciones.validaTextoEnPdf(rutaux, "INSCRIPCIÓN DE ADENDA AL CONTRATO DE PRENDA");
                                if (conf == 1) {
                                    rutaNotificacionCasillero = rutaux;
                                    break;
                                }
                            }
                        }
                    }
                    System.out.println("rutnoc: " + rutaNotificacionCasillero);
                    if (!rutaNotificacionCasillero.trim().isEmpty()) {
                        PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                        PrimeFaces.current().ajax().addCallbackParam("view", rutaNotificacionCasillero);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO CARGAD0");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UN CERTIFICADO SELECCIONADO");
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
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ NINGÚN CERTIFICADO DEL TRÁMITE " + cambi.getSolicitud());
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UN CERTIFICADO SELECCIONADA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarCertificadoPrendaPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            certificados = c.getPrendasComercialesByFechaAndType(fechaInicio, fechaFin, "CERTIFICADO");
            numRegistros = "Número Registros Mostrados: " + certificados.size();
            if (certificados.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarCertificadoPrendaPorFechaPrenda(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechasCertificado()) {
            Controlador c = new Controlador();
            certificados = c.getPrendasComercialesByFechaPrendaAndType(fechaInicioCertificado, fechaFinCertificado, "CERTIFICADO");
            numRegistros = "Número Registros Mostrados: " + certificados.size();
            if (certificados.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarCertificadoCN(ActionEvent ae) {
        FacesMessage msg = null;
        certificado = (PrendaComercial) certificadosDataTable.getRowData();
        if (certificado != null) {
            Controlador c = new Controlador();
            if (c.removePrendaComercial(certificado)) {
                c.saveHistorial("CERTIFICADO_PRENDA", "CERTIFICADO_PRENDA ", certificado.getSolicitud(), "ELIMINADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                loadCertificadosPrendaComercial();
                System.out.println("Certificado_prenda " + certificado.getSolicitud() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_PRENDA " + certificado.getSolicitud() + "ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR CERTIFICADO_PRENDA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR CERTIFICADO_PRENDA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {

        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
        certificado = (PrendaComercial) certificadosDataTable.getRowData();
        if (certificado != null) {
            dialogTitle = "EDITAR CERTIFICADO PRENDA COMERCIAL " + certificado.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar la Certificado_Prenda: " + certificado.getSolicitud() + "?";
            Controlador c = new Controlador();
            RenewalForm rf = c.getRenewalFormsByApplicationNumber(certificado.getSolicitud());
            if (rf.getId() != null) {
                certificado.setIdRenewalForm(rf.getId());
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_CN CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR CERTIFICADO_CN");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararNuevo(ActionEvent ae) {
        dialogTitle = "NUEVO CERTIFICADO PRENDA COMERCIAL";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar la Nueva Certificado_prenda?";
        certificado = new PrendaComercial();
        certificado.setTipoEstado("CERTIFICADO");
        Controlador c = new Controlador();
//        certificado.setPrendaNo(c.getNextPrendaComercialPrendaNo());
        certificado.setResponsable(loginBean.getUsuario().getAlias());
        edicion = false;
        if (certificado != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void guardarCertificadoPrenda(ActionEvent ae) {
        FacesMessage msg = null;
        if (certificado != null) {
            Controlador c = new Controlador();
            if (certificado.getId() != null) {
                if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                    if (estadoTemp.equals("NOTIFICADAS")) {
                        certificado.setTipoEstado("NOTIFICADA");
                        certificado.setNotificacion(c.getNextNumeroNotificacionPrendaComercial(new Date()));
                        certificado.setFechaNotificacion(new Date());
                    } else if (estadoTemp.equals("DESISTIDAS")) {
                        certificado.setTipoEstado("DESISTIDA");
                    } else if (estadoTemp.equals("CADUCADAS")) {
                        certificado.setTipoEstado("CADUCADA");
                    } else if (estadoTemp.equals("LEVANTAMIENTO")) {
                        certificado.setTipoEstado("LEVANTAMIENTO");   
                        certificado.setFechaPrenda(new Date());
                        certificado.setLevantamientoPrendaNo(c.getNextNumeroLevantamientoPrenda(new Date()));
                    }

                    if (c.updatePrendaComercial(certificado)) {
                        c.saveHistorial(certificado.getTipoEstado() + "_PRENDA", "CERTIFICADO_PRENDA", certificado.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                        loadCertificadosPrendaComercial();
                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_PRENDA EDITADA CON ÉXITO");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA CERTIFICADO_PRENDA");
                    }

                } else {
                    //Editar CertificadoCD
                    if (c.validarExistenciaPrendaComercial(certificado)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                    } else {
                        certificado.setSolicitud(certificado.getSolicitud().toUpperCase());
                        if (c.updatePrendaComercial(certificado)) {
                            c.saveHistorial("CERTIFICADO_PRENDA", "CERTIFICADO_PRENDA", certificado.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_PRENDA EDITADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA CERTIFICADO_PRENDA");
                        }
                    }
                }
            } else {
//                //Guardar Certificado Prenda
                if (c.existePrendaComercial(certificado.getSolicitud())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                } else {
                    boolean habilitado = true;
                    certificado.setPrendaNo(c.getNextPrendaComercialPrendaNo());
                    if (certificado.getDenominacion() != null && !certificado.getDenominacion().trim().isEmpty()
                            && certificado.getRegistro() != null && !certificado.getRegistro().trim().isEmpty()) {
                        if (c.existsTituloCanceladoByTituloAndDenominacion(certificado.getRegistro(), certificado.getDenominacion(), false)) {
                            TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(certificado.getRegistro(), certificado.getDenominacion());
                            if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN '"
                                        + certificado.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                certificado = new PrendaComercial();
                                habilitado = false;
                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                certificado.setCancelado(titca.getTipoCancelacion());
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN '"
                                        + certificado.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN '"
                                        + certificado.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                certificado = new PrendaComercial();
                                habilitado = false;
                            }
                        } else {
                            habilitado = true;
                        }
                    }
                    if (habilitado) {
                        certificado.setSolicitud(certificado.getSolicitud().toUpperCase());
                        certificado.setTipoEstado("CERTIFICADO");
                        if (c.savePrendaComercial(certificado)) {
                            c.saveModificacionApp(certificado.getDenominacion(), certificado.getRegistro(), certificado.getSolicitud(), "PRENDA COMERCIAL", loginBean.getNombre());
                            c.saveHistorial("CERTIFICADO_PRENDA", "CERTIFICADO_PRENDA", certificado.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            loadCertificadosPrendaComercial();
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_PRENDA GUARDADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL GUARDAR EL CERTIFICADO_PRENDA");
                        }
                    }
                }
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO CARGAR EL CERTIFICADO_CN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    /* Da la orden de visualizar el reporte, clase Informe (Webservlet)*/
    public void viewCertificadoPrendaAdenda(ActionEvent ae) {
        FacesMessage msg = null;
        certificado = (PrendaComercial) certificadosDataTable.getRowData();
        if (certificado != null) {

            if (certificado.getRegistro() != null && !certificado.getRegistro().trim().isEmpty()) {
                Controlador c = new Controlador();

                if (!c.validarDelegadoActivo("delegado")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarDelegacionActivo()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarResolucionActiva("transferencia")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else {

                    if (certificado.getPrendaNo() != null && certificado.getPrendaNo() != 0) {
                        if (certificado.getRegistro() != null && !certificado.getRegistro().trim().isEmpty()) {
                            if (certificado.getFechaContrato() != null && validarFecha(certificado.getFechaContrato())) {
                                if (certificado.getResponsable() != null && !certificado.getResponsable().trim().isEmpty()) {
                                    if (certificado.getFechaPrenda() != null && validarFecha(certificado.getFechaPrenda())) {
                                        if (certificado.getEmail() != null && !certificado.getEmail().trim().isEmpty() && certificado.getEmail().contains("@")) {

                                            if (certificado.getTitApodRepre() != null && !certificado.getTitApodRepre().trim().isEmpty() && !certificado.getTitApodRepre().equals("null")) {
                                                System.out.println("Descargando Certificado_Prenda: " + certificado.getSolicitud());
                                                loginBean.setVarious(false);
                                                certificado.setAdenda(true);
                                                loginBean.setPrendaComercial(certificado);
                                                loginBean.setPrendasComerciales(new ArrayList<PrendaComercial>());

                                                PrimeFaces.current().ajax().addCallbackParam("doit", true);

                                                System.out.println("envía certificado descargar: " + certificado.getPrendaNo());
                                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO DOCUMENTO " + certificado.getSolicitud());
                                            } else {
                                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + certificado.getSolicitud() + " NO TIENE TIT APOD REPRE");
                                            }
                                        } else {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + certificado.getSolicitud() + " NO TIENE CORREO ACREEDOR PRENDARIO");
                                        }
                                    } else {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + certificado.getSolicitud() + " NO TIENE FECHA PRENDA");
                                    }
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + certificado.getSolicitud() + " NO TIENE RESPONSABLE ASIGNADO");
                                }
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + certificado.getSolicitud() + " NO TIENE UNA FECHA DE CONTRATO VÁLIDA");
                            }
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + certificado.getSolicitud() + " NO POSEE NÚMERO DE REGISTRO");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + certificado.getSolicitud() + " NO UN NÚMERO DE PRENDA VÁLIDO");
                    }
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL REGISTRO SELECCIONADO NO TIENE NÚMERO DE REGISTRO ASIGNADO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR LOS DATOS DEL REGISTRO SELECCIONADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /* Da la orden de visualizar el reporte, clase Informe (Webservlet)*/
    public void viewCertificadoPrenda(ActionEvent ae) {
        FacesMessage msg = null;
        certificado = (PrendaComercial) certificadosDataTable.getRowData();
        if (certificado != null) {

            if (certificado.getRegistro() != null && !certificado.getRegistro().trim().isEmpty()) {
                Controlador c = new Controlador();

                if (!c.validarDelegadoActivo("delegado")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarDelegacionActivo()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarResolucionActiva("transferencia")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else {

                    if (certificado.getPrendaNo() != null && certificado.getPrendaNo() != 0) {
                        if (certificado.getRegistro() != null && !certificado.getRegistro().trim().isEmpty()) {
                            if (certificado.getFechaContrato() != null && validarFecha(certificado.getFechaContrato())) {
                                if (certificado.getResponsable() != null && !certificado.getResponsable().trim().isEmpty()) {
                                    if (certificado.getFechaPrenda() != null && validarFecha(certificado.getFechaPrenda())) {
                                        if (certificado.getEmail() != null && !certificado.getEmail().trim().isEmpty() && certificado.getEmail().contains("@")) {

                                            if (certificado.getTitApodRepre() != null && !certificado.getTitApodRepre().trim().isEmpty() && !certificado.getTitApodRepre().equals("null")) {
                                                System.out.println("Descargando Certificado_Prenda: " + certificado.getSolicitud());
                                                loginBean.setVarious(false);
                                                certificado.setAdenda(false);
                                                loginBean.setPrendaComercial(certificado);
                                                loginBean.setPrendasComerciales(new ArrayList<PrendaComercial>());

                                                PrimeFaces.current().ajax().addCallbackParam("doit", true);

                                                System.out.println("envía certificado descargar: " + certificado.getPrendaNo());
                                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO DOCUMENTO " + certificado.getSolicitud());
                                            } else {
                                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + certificado.getSolicitud() + " NO TIENE TIT APOD REPRE");
                                            }
                                        } else {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + certificado.getSolicitud() + " NO TIENE CORREO ACREEDOR PRENDARIO");
                                        }
                                    } else {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + certificado.getSolicitud() + " NO TIENE FECHA PRENDA");
                                    }
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + certificado.getSolicitud() + " NO TIENE RESPONSABLE ASIGNADO");
                                }
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + certificado.getSolicitud() + " NO TIENE UNA FECHA DE CONTRATO VÁLIDA");
                            }
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + certificado.getSolicitud() + " NO POSEE NÚMERO DE REGISTRO");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + certificado.getSolicitud() + " NO UN NÚMERO DE PRENDA VÁLIDO");
                    }
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL REGISTRO SELECCIONADO NO TIENE NÚMERO DE REGISTRO ASIGNADO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR LOS DATOS DEL REGISTRO SELECCIONADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public boolean validarFecha(Date fechacontrato) {
        if (fechacontrato.getYear() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public void prepararHistorial(ActionEvent ae) {
        certificado = (PrendaComercial) certificadosDataTable.getRowData();
        if (certificado != null) {
            dialogTitle = "SEGUIMIENTO " + certificado.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(certificado.getSolicitud());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (getHistorial().trim().isEmpty()) {
                historial = "Estado actual: CERTIFICADO_PRENDA";
            }
        }
    }

    public void downloadSelectedAdenda(ActionEvent ae) {
        FacesMessage msg = null;
//        RequestContext context = RequestContext.getCurrentInstance();
        if (!selectedCertificados.isEmpty()) {

            System.out.println("Descargando Múltiples Certificados_prenda...");
            String mensaje = "";

            boolean flag = false;
            for (int i = 0; i < selectedCertificados.size(); i++) {
                PrendaComercial aux = selectedCertificados.get(i);
                aux.setAdenda(true);
                if (aux.getPrendaNo() != null && aux.getPrendaNo() != 0) {
                    if (aux.getRegistro() != null && !aux.getRegistro().trim().isEmpty()) {
                        if (aux.getFechaContrato() != null && validarFecha(aux.getFechaContrato())) {
                            if (aux.getResponsable() != null && !aux.getResponsable().trim().isEmpty()) {
                                if (aux.getFechaPrenda() != null && validarFecha(aux.getFechaPrenda())) {
//                                    if (aux.getEmail() != null && !aux.getEmail().trim().isEmpty() && aux.getEmail().contains("@")) {
                                    if (aux.getTitApodRepre() != null && !aux.getTitApodRepre().trim().isEmpty() && !aux.getTitApodRepre().equals("null")) {
                                        flag = true;
                                    } else {
                                        mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE TIT APOD REPRE";
                                        flag = false;
                                        break;
                                    }
//                                    } else {
//                                        mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE CORREO ACREEDOR PRENDARIO";
//                                        flag = false;
//                                        break;
//                                    }
                                } else {
                                    mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE FECHA PRENDA";
                                    flag = false;
                                    break;
                                }
                            } else {
                                mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE RESPONSABLE ASIGNADO";
                                flag = false;
                                break;
                            }
                        } else {
                            mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE UNA FECHA DE CONTRATO VÁLIDA";
                            flag = false;
                            break;
                        }
                    } else {
                        mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO POSEE NÚMERO DE REGISTRO";
                        flag = false;
                        break;
                    }
                } else {
                    mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO UN NÚMERO DE PRENDA VÁLIDO";
                    flag = false;
                    break;
                }
//                System.out.println("auxpre 2: "+aux.getSolicitud());
            }
            if (flag) {
                loginBean.setVarious(true);
                loginBean.setPrendasComerciales(selectedCertificados);

                PrimeFaces.current().ajax().addCallbackParam("doit", true);
//                PrimeFaces.current().ajax().addCallbackParam("view", "prendacomercialr");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "DESCARGA", "DESCARGANDO REGISTROS SELECCIONADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN REGISTRO", mensaje);
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
    
    public void downloadSelected(ActionEvent ae) {
        FacesMessage msg = null;
//        RequestContext context = RequestContext.getCurrentInstance();
        if (!selectedCertificados.isEmpty()) {

            System.out.println("Descargando Múltiples Certificados_prenda...");
            String mensaje = "";

            boolean flag = false;
            for (int i = 0; i < selectedCertificados.size(); i++) {
                PrendaComercial aux = selectedCertificados.get(i);
                aux.setAdenda(false);
                if (aux.getPrendaNo() != null && aux.getPrendaNo() != 0) {
                    if (aux.getRegistro() != null && !aux.getRegistro().trim().isEmpty()) {
                        if (aux.getFechaContrato() != null && validarFecha(aux.getFechaContrato())) {
                            if (aux.getResponsable() != null && !aux.getResponsable().trim().isEmpty()) {
                                if (aux.getFechaPrenda() != null && validarFecha(aux.getFechaPrenda())) {
//                                    if (aux.getEmail() != null && !aux.getEmail().trim().isEmpty() && aux.getEmail().contains("@")) {
                                    if (aux.getTitApodRepre() != null && !aux.getTitApodRepre().trim().isEmpty() && !aux.getTitApodRepre().equals("null")) {
                                        flag = true;
                                    } else {
                                        mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE TIT APOD REPRE";
                                        flag = false;
                                        break;
                                    }
//                                    } else {
//                                        mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE CORREO ACREEDOR PRENDARIO";
//                                        flag = false;
//                                        break;
//                                    }
                                } else {
                                    mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE FECHA PRENDA";
                                    flag = false;
                                    break;
                                }
                            } else {
                                mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE RESPONSABLE ASIGNADO";
                                flag = false;
                                break;
                            }
                        } else {
                            mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE UNA FECHA DE CONTRATO VÁLIDA";
                            flag = false;
                            break;
                        }
                    } else {
                        mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO POSEE NÚMERO DE REGISTRO";
                        flag = false;
                        break;
                    }
                } else {
                    mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO UN NÚMERO DE PRENDA VÁLIDO";
                    flag = false;
                    break;
                }
//                System.out.println("auxpre 2: "+aux.getSolicitud());
            }
            if (flag) {
                loginBean.setVarious(true);
                loginBean.setPrendasComerciales(selectedCertificados);

                PrimeFaces.current().ajax().addCallbackParam("doit", true);
//                PrimeFaces.current().ajax().addCallbackParam("view", "prendacomercialr");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "DESCARGA", "DESCARGANDO REGISTROS SELECCIONADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN REGISTRO", mensaje);
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

    public void verNotificacionPrendaComercial(ActionEvent ae) {
        FacesMessage msg = null;
        certificado = (PrendaComercial) certificadosDataTable.getRowData();
        if (certificado != null) {

            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionByCriterio(certificado.getSolicitud(), true);
            if (uploads.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCUENTRA UN CERTIFICADO REALIZADO PARA ESTE TRÁMITE");
            } else {

                String rutaCertCasillero = "";
                for (int i = 0; i < uploads.size(); i++) {
                    UploadNotificacion un = uploads.get(i);
                    String rutaux = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + un.getCasillero() + "/" + un.getDocumento();
                    int conf = Operaciones.validaTextoEnPdf(rutaux, "INSCRIPCIÓN PRENDA COMERCIAL No");
                    if (conf == 1) {
                        rutaCertCasillero = rutaux;
                        break;
                    }
                }
                if (!rutaCertCasillero.trim().isEmpty()) {
                    PrimeFaces.current().ajax().addCallbackParam("notit", true);
                    PrimeFaces.current().ajax().addCallbackParam("rutanot", rutaCertCasillero);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "PRENDA COMERCIAL", "VISUALIZANDO CERTIFICADO");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCUENTRA UN CERTIFICADO REALIZADO PARA ESTE TRÁMITE");
                }
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "FECHA", "EL TRÁMITE NO POSEE FECHA DE VENCIMIENTO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (certificado != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + certificado.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(certificado.getIdRenewalForm(), certificado.getSolicitud());
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

    public void buscarCasillero(ActionEvent ae) {
        FacesMessage msg = null;
        if (certificado != null) {
            if (criterioOwner != null && !criterioOwner.trim().isEmpty()) {
                System.out.println("criterio owner: " + criterioOwner);
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

    public void seleccionarCasillero(ActionEvent ae) {
        FacesMessage msg = null;
        if (certificado != null) {
            Owner own = (Owner) ownerDataTable.getRowData();
            if (own != null) {
                certificado.setCasilleroSenadiAcreedor(own.getCasillero());
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

    public void prepararBusquedaCasillero(ActionEvent ae) {
        owners = new ArrayList<>();
        criterioOwner = "";
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
     * @return the certificados
     */
    public List<PrendaComercial> getCertificados() {
        return certificados;
    }

    /**
     * @param certificados the certificados to set
     */
    public void setCertificados(List<PrendaComercial> certificados) {
        this.certificados = certificados;
    }

    /**
     * @return the certificadosFiltradas
     */
    public List<PrendaComercial> getCertificadosFiltradas() {
        return certificadosFiltradas;
    }

    /**
     * @param certificadosFiltradas the certificadosFiltradas to set
     */
    public void setCertificadosFiltradas(List<PrendaComercial> certificadosFiltradas) {
        this.certificadosFiltradas = certificadosFiltradas;
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
     * @return the certificadosDataTable
     */
    public UIData getCertificadosDataTable() {
        return certificadosDataTable;
    }

    /**
     * @param certificadosDataTable the certificadosDataTable to set
     */
    public void setCertificadosDataTable(UIData certificadosDataTable) {
        this.certificadosDataTable = certificadosDataTable;
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
     * @return the certificado
     */
    public PrendaComercial getCertificado() {
        return certificado;
    }

    /**
     * @param certificado the certificado to set
     */
    public void setCertificado(PrendaComercial certificado) {
        this.certificado = certificado;
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
     * @return the selectedCertificados
     */
    public List<PrendaComercial> getSelectedCertificados() {
        return selectedCertificados;
    }

    /**
     * @param selectedCertificados the selectedCertificados to set
     */
    public void setSelectedCertificados(List<PrendaComercial> selectedCertificados) {
        this.selectedCertificados = selectedCertificados;
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

}
