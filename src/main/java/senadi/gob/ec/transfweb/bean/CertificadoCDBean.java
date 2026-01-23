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
import senadi.gob.ec.transfweb.model.cd.CambioDomicilio;
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
 * @author micharesp
 */
@ManagedBean(name = "certificadocdBean")
@ViewScoped
public class CertificadoCDBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private Date fechaInicioCertificado;
    private Date fechaFinCertificado;

    private List<CambioDomicilio> certificados;
    private List<CambioDomicilio> certificadosFiltradas;

    private UIData certificadosDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private CambioDomicilio certificado;

    private LoginBean loginBean;

    private String estadoTemp;
    private String historial;

    private String exportName;

    private List<CambioDomicilio> selectedCertificados;

    private boolean separado;

    private List<Documento> archivos;

    public CertificadoCDBean() {
        loadCertificadosCD();

    }

    private void loadCertificadosCD() {
        Controlador c = new Controlador();
        certificados = c.getCambiosDomicilioByTipo("CERTIFICADO");
        numRegistros = "Número Registros Mostrados: " + certificados.size();
        exportName = "certificado_cd_" + Operaciones.formatDate(new Date());
        selectedCertificados = new ArrayList<>();
        loginBean = c.getLogin();
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;
        if (certificado != null && certificado.getSolicitud() != null && !certificado.getSolicitud().trim().isEmpty()) {

            String tramite = certificado.getSolicitud();
            Controlador c = new Controlador();
            if (c.existeCambioDomicilio(tramite)) {
                CambioDomicilio aux = c.getCambioDomicilioBySolicitud(tramite);
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL TRÁMITE YA SE ENCUENTRA REGISTRADO EN LA PESTAÑA DE " + aux.getTipoEstado());
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
                                    if (t.getName().trim().toLowerCase().contains("domicilio")) {

                                        PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                        certificado.setComprobante(payment.getVoucherNumber());
                                        certificado.setFechaPresentacion(rf.getApplicationDate());
//                                    certificado.setCertificado(c.getNextNumeroCertificadoCD());

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
//                                                certificado.setTitularAnterior(titulo.getTitular());
//                                                certificado.setTitularActual(titulo.getTitular());
//                                                certificado.setDomicilioTitularActual(titulo.get);
                                                    }
                                                }
                                                if (certificado.getTitularActual() == null || certificado.getTitularActual().trim().isEmpty()) {
                                                    PpdiPersona persona = c.getASolicitanteByCodigoSolicitud(ps.getCodigoSolicitudSigno());
                                                    if (persona.getCodigoPersona() != null) {
                                                        certificado.setTitularActual(persona.getNombrePersona());
                                                        certificado.setDomicilioTitularActual(persona.getDireccionPersona());
                                                    }
                                                }
                                            }
                                        }

                                        Person lawyer = c.getPersonRenewalByType(rf.getId(), "LAWYER");
                                        if (lawyer.getId() != null) {
                                            certificado.setNomApodRepre(lawyer.getName());
                                        }

//                                if (certificado.getTitularActual() == null || certificado.getTitularActual().trim().isEmpty()) {
                                        Person titular = c.getPersonRenewalByType(rf.getId(), "APPLICANT");
                                        if (titular.getId() != null) {
                                            certificado.setTitularActual(titular.getName());
                                            certificado.setDomicilioTitularActual(titular.getAddress());
                                        }

                                        Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "AGENT");
                                        if (apoder.getId() != null) {
                                            certificado.setApeApodRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "AGENT"));
                                        }

                                        if (certificado.getRegistro() != null && !certificado.getRegistro().trim().isEmpty()) {
                                            if (certificado.getDenominacion() != null && !certificado.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(certificado.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(certificado.getRegistro(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN "
                                                                    + certificado.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            certificado = new CambioDomicilio();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            certificado.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN "
                                                                    + certificado.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN "
                                                                    + certificado.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            certificado = new CambioDomicilio();
                                                        }
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(certificado.getRegistro(), certificado.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(certificado.getRegistro(), certificado.getDenominacion());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN "
                                                                    + certificado.getDenominacion() + " SE ENCUENTRA CANCELADO; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            certificado = new CambioDomicilio();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            certificado.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN "
                                                                    + certificado.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN "
                                                                    + certificado.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + ";CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            certificado = new CambioDomicilio();
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
                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UN CAMBIO DE DOMICILIO, SINO '" + t.getName().toUpperCase() + "'");
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

    public void buscarCertificadosCD(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            certificados = c.getCambiosDomicilioByCriteriaAndType(criterio.trim(), "CERTIFICADO");
            numRegistros = "Número Registros Mostrados: " + certificados.size();
            if (certificados.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void validarCertificado(CambioDomicilio cambi) {
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
                        int conf = Operaciones.validaTextoEnPdf(rutaux, "CERTIFICADO DE CAMBIO DE DOMICILIO");
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
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ NINGÚN CERTIFICADO DEL TRÁMITE " + cambi.getSolicitud());
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UN CERTIFICADO SELECCIONADA");
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

    public void buscarCertificadoCDPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            certificados = c.getCambiosDomicilioByFechaAndType(fechaInicio, fechaFin, "CERTIFICADO");
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

    public void buscarCertificadoCDPorFechaCertificado(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechasCertificado()) {
            Controlador c = new Controlador();
            certificados = c.getCambiosDomicilioByFechaCertificadoAndType(fechaInicioCertificado, fechaFinCertificado, "CERTIFICADO");
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

    public void eliminarCertificadoCD(ActionEvent ae) {
        FacesMessage msg = null;
        certificado = (CambioDomicilio) certificadosDataTable.getRowData();
        if (certificado != null) {
            Controlador c = new Controlador();
            if (c.removeCambioDomicilio(certificado)) {
                c.saveHistorial("CERTIFICADO_CD", "CERTIFICADO_CD ", certificado.getSolicitud(), "ELIMINADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                loadCertificadosCD();
                System.out.println("Certificado_cd " + certificado.getSolicitud() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_CD " + certificado.getSolicitud() + "ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR CERTIFICADO_CD");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR CERTIFICADO_CD");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {

        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
        certificado = (CambioDomicilio) certificadosDataTable.getRowData();
        if (certificado != null) {
            dialogTitle = "EDITAR CERTIFICADO CAMBIO DE DOMICILIO " + certificado.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar la Certificado_cd: " + certificado.getSolicitud() + "?";
            Controlador c = new Controlador();
            RenewalForm rf = c.getRenewalFormsByApplicationNumber(certificado.getSolicitud());
            if (rf.getId() != null) {
                certificado.setIdRenewalForm(rf.getId());
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_CD CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR CERTIFICADO_CD");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararNuevo(ActionEvent ae) {
        dialogTitle = "NUEVO CERTIFICADO CAMBIO DE DOMICILIO";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar la Nueva Certificado_cd?";
        certificado = new CambioDomicilio();
        certificado.setTipoEstado("CERTIFICADO");
        Controlador c = new Controlador();
//        certificado.setCertificado(c.getNextCambioDomicilioCertificado());
        certificado.setResponsable(loginBean.getUsuario().getAlias());
        edicion = false;
        if (certificado != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void guardarCertificadoCD(ActionEvent ae) {
        FacesMessage msg = null;
        if (certificado != null) {
            Controlador c = new Controlador();
            if (certificado.getId() != null) {
                if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                    if (estadoTemp.equals("NOTIFICADA")) {
                        certificado.setNotificacion(c.getNextNumeroNotificacionCD(new Date()));
                        certificado.setTipoEstado("NOTIFICADA");
                    } else if (estadoTemp.equals("DESISTIDA")) {
                        certificado.setTipoEstado("DESISTIDA");
                    } else if (estadoTemp.equals("CADUCADA")) {
                        certificado.setTipoEstado("CADUCADA");
                    } else if (estadoTemp.equals("RESOLUCION")) {
                        certificado.setTipoEstado("RESOLUCION");
                    }
//                    certificado.setObservacion((certificado.getObservacion() != null && certificado.getObservacion().equals("CERTIFICADO")) ? certificado.getTipoEstado() : certificado.getObservacion());

                    if (c.updateCambioDomicilio(certificado)) {
                        c.saveHistorial(certificado.getTipoEstado() + "_CD", "CERTIFICADO_CD", certificado.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                        loadCertificadosCD();
                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_CD EDITADA CON ÉXITO");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA CERTIFICADO_CD");
                    }

                } else {
                    //Editar CertificadoCD
                    if (c.validarExistenciaCambioDomicilio(certificado)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                    } else {
                        certificado.setSolicitud(certificado.getSolicitud().toUpperCase());
                        if (c.updateCambioDomicilio(certificado)) {
                            c.saveHistorial("CERTIFICADO_CD", "CERTIFICADO_CD", certificado.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_CD EDITADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA CERTIFICADO_CD");
                        }
                    }
                }
            } else {
//                //Guardar Certificado_CD
                if (c.existeCambioDomicilio(certificado.getSolicitud())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                } else {

                    boolean habilitado = true;
                    if (certificado.getDenominacion() != null && !certificado.getDenominacion().trim().isEmpty()
                            && certificado.getRegistro() != null && !certificado.getRegistro().trim().isEmpty()) {
                        if (c.existsTituloCanceladoByTituloAndDenominacion(certificado.getRegistro(), certificado.getDenominacion(), false)) {
                            TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(certificado.getRegistro(), certificado.getDenominacion());
                            if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN '"
                                        + certificado.getDenominacion() + "' SE ENCUENTRA CANCELADO; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                certificado = new CambioDomicilio();
                                habilitado = false;
                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                certificado.setCancelado(titca.getTipoCancelacion());
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN '"
                                        + certificado.getDenominacion() + "' SE ENCUENTRA CANCELADO PARCIALMENTE; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + certificado.getRegistro() + " CON DENOMINACIÓN '"
                                        + certificado.getDenominacion() + "' SE ENCUENTRA CANCELADO; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                certificado = new CambioDomicilio();
                                habilitado = false;
                            }
                        } else {
                            habilitado = true;
                        }
                    }
                    if (habilitado) {
                        int next = c.getNextCambioDomicilioCertificado();
                        if (next != -1) {
                            certificado.setCertificado(next);
                            certificado.setSolicitud(certificado.getSolicitud().toUpperCase());
                            certificado.setTipoEstado("CERTIFICADO");
                            if (c.saveCambioDomicilio(certificado)) {
                                c.saveModificacionApp(certificado.getDenominacion(), certificado.getRegistro(), certificado.getSolicitud(), "CAMBIO DE DOMICILIO", loginBean.getNombre());
                                c.saveHistorial("CERTIFICADO_CD", "CERTIFICADO_CD", certificado.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                                loadCertificadosCD();
                                PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_CD GUARDADA CON ÉXITO");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL GUARDAR EL CERTIFICADO_CD");
                            }
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN ERROR AL OBTENER EL NÚMERO DE CERTIFICADO_cd");
                        }

                    }

                }
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO CARGAR EL CERTIFICADO_CD");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /* Da la orden de visualizar el reporte, clase Informe (Webservlet)*/
    public void viewCertificadoCD(ActionEvent ae) {
        FacesMessage msg = null;
        certificado = (CambioDomicilio) certificadosDataTable.getRowData();
        if (certificado != null) {

            if (certificado.getRegistro() != null && !certificado.getRegistro().trim().isEmpty()) {
                System.out.println("Descargando Certificado_CD: " + certificado.getSolicitud());

                Controlador c = new Controlador();

                if (!c.validarDelegadoActivo("delegado")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarDelegacionActivo()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarResolucionActiva("transferencia")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else {
                    loginBean.setVarious(false);
                    loginBean.setCambioDomicilio(certificado);

                    PrimeFaces.current().ajax().addCallbackParam("doit", true);

                    System.out.println("envía certificado descargar");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO DOCUMENTO " + certificado.getSolicitud());
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL REGISTRO SELECCIONADO NO TIENE NÚMERO DE REGISTRO ASIGNADO");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR LOS DATOS DEL REGISTRO SELECCIONADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        certificado = (CambioDomicilio) certificadosDataTable.getRowData();
        if (certificado != null) {
            dialogTitle = "SEGUIMIENTO " + certificado.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(certificado.getSolicitud());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (getHistorial().trim().isEmpty()) {
                historial = "Estado actual: CERTIFICADO_CD";
            }
        }
    }

    public void downloadSelected(ActionEvent ae) {
        FacesMessage msg = null;
//        RequestContext context = RequestContext.getCurrentInstance();
        if (!selectedCertificados.isEmpty()) {

            System.out.println("Descargando Múltiples Certificados_cd...");

            boolean band = true;
            for (int i = 0; i < selectedCertificados.size(); i++) {
                if (selectedCertificados.get(i).getRegistro() == null || selectedCertificados.get(i).getRegistro().trim().isEmpty()) {
                    band = false;
                    break;
                }
            }
            if (band) {
                loginBean.setVarious(true);
                loginBean.setCambiosDomicilio(selectedCertificados);

                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                PrimeFaces.current().ajax().addCallbackParam("view", "cambiodomiciliop");
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

    public void verNotificacionCambioDomicilio(ActionEvent ae) {
        FacesMessage msg = null;
        certificado = (CambioDomicilio) certificadosDataTable.getRowData();
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
                    int conf = Operaciones.validaTextoEnPdf(rutaux, "CERTIFICADO DE CAMBIO DE DOMICILIO");
                    if (conf == 1) {
                        rutaCertCasillero = rutaux;
                        break;
                    }

                }
                if (!rutaCertCasillero.trim().isEmpty()) {
                    PrimeFaces.current().ajax().addCallbackParam("notit", true);
                    PrimeFaces.current().ajax().addCallbackParam("rutanot", rutaCertCasillero);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CAMBIO DE DOMICILIO", "VISUALIZANDO CERTIFICADO");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCUENTRA UN CERTIFICADO REALIZADO PARA ESTE TRÁMITE");
                }

            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "FECHA", "EL TRÁMITE NO POSEE FECHA DE VENCIMIENTO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarCasillero(ActionEvent ae) {
        if (certificado != null && certificado.getId() != null) {
            Controlador c = new Controlador();
            certificado.setCasilleroSenadi(c.buscarCasilleroBySolicitud(certificado.getSolicitud()));
        }
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
    public List<CambioDomicilio> getCertificados() {
        return certificados;
    }

    /**
     * @param certificados the certificados to set
     */
    public void setCertificados(List<CambioDomicilio> certificados) {
        this.certificados = certificados;
    }

    /**
     * @return the certificadosFiltradas
     */
    public List<CambioDomicilio> getCertificadosFiltradas() {
        return certificadosFiltradas;
    }

    /**
     * @param certificadosFiltradas the certificadosFiltradas to set
     */
    public void setCertificadosFiltradas(List<CambioDomicilio> certificadosFiltradas) {
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
    public CambioDomicilio getCertificado() {
        return certificado;
    }

    /**
     * @param certificado the certificado to set
     */
    public void setCertificado(CambioDomicilio certificado) {
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
    public List<CambioDomicilio> getSelectedCertificados() {
        return selectedCertificados;
    }

    /**
     * @param selectedCertificados the selectedCertificados to set
     */
    public void setSelectedCertificados(List<CambioDomicilio> selectedCertificados) {
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

}
