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
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import senadi.gob.ec.transfweb.model.Documento;
import senadi.gob.ec.transfweb.model.Historial;
import senadi.gob.ec.transfweb.model.TituloCancelado;
import senadi.gob.ec.transfweb.model.UploadNotificacion;
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepform.ModificacionApp;
import senadi.gob.ec.transfweb.model.iepform.PaymentReceipt;
import senadi.gob.ec.transfweb.model.iepform.Person;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepform.Types;
import senadi.gob.ec.transfweb.model.licencia.SubLicenciaUso;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author micharesp
 */
@ManagedBean(name = "certificadoSublicenciaBean")
@ViewScoped
public class CertificadoSubLicenciaBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private Date fechaInicioCertificado;
    private Date fechaFinCertificado;

    private List<SubLicenciaUso> sublicencias;
    private List<SubLicenciaUso> sublicenciasFiltradas;

    private UIData sublicenciasDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private SubLicenciaUso sublicencia;

    private LoginBean loginBean;

    private String estadoTemp;
    private String historial;

    private String exportName;

    private List<SubLicenciaUso> selectedSublicencias;

    private boolean separado;

    private List<Documento> archivos;

    public CertificadoSubLicenciaBean() {
        loadCertificadosSublicenciasUso();
    }

    private void loadCertificadosSublicenciasUso() {
        Controlador c = new Controlador();
        sublicencias = c.getSublicenciasUsoByTipo("SUBLICENCIA");
        numRegistros = "Número Registros Mostrados: " + sublicencias.size();
        exportName = "cert_sublicencia_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
        selectedSublicencias = new ArrayList<>();
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;

        if (sublicencia != null && sublicencia.getSolicitud() != null && !sublicencia.getSolicitud().trim().isEmpty()) {

            String tramite = sublicencia.getSolicitud();
            Controlador c = new Controlador();
            if (c.existeSublicenciaUso(tramite)) {
                SubLicenciaUso aux = c.getSublicenciaUsoBySolicitud(tramite);
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL TRÁMITE YA SE ENCUENTRA REGISTRADO EN LA PESTAÑA DE " + aux.getTipoEstado());
            } else {
                if (c.existeLicenciaUso(tramite)) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL TRÁMITE YA SE ENCUENTRA REGISTRADO EN LICENCIAS DE USO");
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
                                        if (t.getName().trim().toLowerCase().contains("licencia de uso")) {
                                            if (rf.getLicenseType() != null && rf.getLicenseType().equals("SUBLICENSE")) {
                                                PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                                sublicencia.setComprobante(payment.getVoucherNumber());
                                                sublicencia.setFechaPresentacion(rf.getApplicationDate());
                                                //certificado.setCertificado(c.getNextNumeroCertificadoCD());
                                                sublicencia.setSublicenciaNo(c.getNextSublicenciaUsoNo());

                                                sublicencia.setSigno(ttp.getAlias());
                                                sublicencia.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");
                                                sublicencia.setIdRenewalForm(rf.getId());

                                                if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                                    HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                                    if (hf.getId() != null) {
                                                        sublicencia.setDenominacion(hf.getDenomination());
                                                        sublicencia.setRegistro(hf.getExpedient());

                                                        if (sublicencia.getRegistro() != null && !sublicencia.getRegistro().trim().isEmpty()) {
                                                            PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(sublicencia.getRegistro(), sublicencia.getDenominacion());
                                                            if (titulo.getCodigoSolicitudSigno() != null) {
                                                                sublicencia.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                            }
                                                        }
                                                        if (sublicencia.getFechaRegistro() == null) {
                                                            sublicencia.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                        }
                                                    }
                                                } else {
                                                    if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                        PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                        if (ps.getCodigoSolicitudSigno() != null) {
                                                            sublicencia.setDenominacion(ps.getDenominacionSigno());
                                                            PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                            if (titulo.getCodigoSolicitudSigno() != null) {
                                                                sublicencia.setRegistro(titulo.getNumeroTitulo());
                                                                sublicencia.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                            }
                                                        }
                                                    }
                                                }

                                                Person abog = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "LAWYER");
                                                if (abog.getId() != null) {
                                                    sublicencia.setAbogadoPatrocinador(abog.getName());
                                                }
                                                Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "ATTORNEY");
                                                if (apoder.getId() != null) {
                                                    sublicencia.setApoderadoRepresentante(apoder.getName());
                                                    sublicencia.setEmail(abog.getEmail());
                                                }

                                                Person sublic = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "APPLICANT");
                                                if (sublic.getId() != null) {
                                                    sublicencia.setSublicenciante(sublic.getName());
                                                }

                                                Person sublicenciat = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "BENEFICIARY");
                                                if (sublicenciat.getId() != null) {
                                                    sublicencia.setSublicenciatario(sublicenciat.getName());
                                                }

                                                if (sublicencia.getRegistro() != null && !sublicencia.getRegistro().trim().isEmpty()) {
                                                    if (sublicencia.getDenominacion() != null && !sublicencia.getDenominacion().trim().isEmpty()) {
                                                        if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                            if (c.existsTituloCanceladoByTituloAndExpediente(sublicencia.getRegistro(), rf.getExpedient(), false)) {
                                                                TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(sublicencia.getRegistro(), rf.getExpedient());
                                                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + sublicencia.getRegistro() + " CON DENOMINACIÓN "
                                                                            + sublicencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                    sublicencia = new SubLicenciaUso();
                                                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                                    sublicencia.setCancelado(titca.getTipoCancelacion());
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + sublicencia.getRegistro() + " CON DENOMINACIÓN "
                                                                            + sublicencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                } else {
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + sublicencia.getRegistro() + " CON DENOMINACIÓN "
                                                                            + sublicencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                    sublicencia = new SubLicenciaUso();
                                                                }
                                                            } else {
                                                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                            }
                                                        } else {
                                                            if (c.existsTituloCanceladoByTituloAndDenominacion(sublicencia.getRegistro(), sublicencia.getDenominacion(), false)) {
                                                                TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(sublicencia.getRegistro(), sublicencia.getDenominacion());
                                                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + sublicencia.getRegistro() + " CON DENOMINACIÓN "
                                                                            + sublicencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                    sublicencia = new SubLicenciaUso();
                                                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                                    sublicencia.setCancelado(titca.getTipoCancelacion());
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + sublicencia.getRegistro() + " CON DENOMINACIÓN "
                                                                            + sublicencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                } else {
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + sublicencia.getRegistro() + " CON DENOMINACIÓN "
                                                                            + sublicencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                    sublicencia = new SubLicenciaUso();
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
                                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE " + rf.getApplicationNumber() + " NO ES UNA SUBLICENCIA");
                                            }

                                        } else {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UNA SUBLICENCIA DE USO, SINO '" + t.getName().toUpperCase() + "'");
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
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN TRÁMITE VÁLIDO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarCertificadosSublicencias(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            sublicencias = c.getSublicenciasUsoByCriteriaAndType(criterio, "SUBLICENCIA");
            numRegistros = "Número Registros Mostrados: " + sublicencias.size();
            if (sublicencias.isEmpty()) {
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

    public void validarCertificadoSublicencia(SubLicenciaUso licen) {
        FacesMessage msg = null;
        if (licen != null) {
            String rutaNotificacionCasillero = "";
            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionBySolicitud(licen.getSolicitud(), true);
            if (!uploads.isEmpty()) {
                if (uploads.size() > 1) {
                    for (int i = 0; i < uploads.size(); i++) {
                        UploadNotificacion unaux = uploads.get(i);
                        String rutaux = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                        int conf = Operaciones.validaTextoEnPdf(rutaux, "CERTIFICADO DE SUBLICENCIA");
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
                    System.out.println("rutasubcertcas: " + rutaNotificacionCasillero);
                    PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                    PrimeFaces.current().ajax().addCallbackParam("view", rutaNotificacionCasillero);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO CARGADO");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ NINGÚN CERTIFICADO DEL TRÁMITE " + licen.getSolicitud());
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UN CERTIFICADO SELECCIONADA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarCertificadoLicenciaPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            sublicencias = c.getSublicenciasUsoByFechaAndType(fechaInicio, fechaFin, "SUBLICENCIA");
            numRegistros = "Número Registros Mostrados: " + sublicencias.size();
            if (sublicencias.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarCertificadoLicenciaPorFechaSubLicencia(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechasCertificado()) {
            Controlador c = new Controlador();
            sublicencias = c.getSubLicenciasUsoByFechaLicenciaAndType(fechaInicioCertificado, fechaFinCertificado, "SUBLICENCIA");
            numRegistros = "Número Registros Mostrados: " + sublicencias.size();
            if (sublicencias.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarCertificadoSubLicencia(ActionEvent ae) {
        FacesMessage msg = null;
        sublicencia = (SubLicenciaUso) sublicenciasDataTable.getRowData();
        if (sublicencia != null) {
            Controlador c = new Controlador();
            if (c.removeSublicenciaUso(sublicencia)) {
                c.saveHistorial("CERTIFICADO_SUBLICENCIA", "CERTIFICADO_SUBLICENCIA ", sublicencia.getSolicitud(), "ELIMINADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                loadCertificadosSublicenciasUso();
                System.out.println("Certificado_Sublicencia " + sublicencia.getSolicitud() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_SUBLICENCIA " + sublicencia.getSolicitud() + "ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR CERTIFICADO_SUBLICENCIA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR CERTIFICADO_SUBLICENCIA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {

        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
        sublicencia = (SubLicenciaUso) sublicenciasDataTable.getRowData();
        if (sublicencia != null) {
            dialogTitle = "EDITAR CERTIFICADO SUBLICENCIA " + sublicencia.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar la Certificado_Sublicencia: " + sublicencia.getSolicitud() + "?";
            Controlador c = new Controlador();
            RenewalForm rf = c.getRenewalFormsByApplicationNumber(sublicencia.getSolicitud());
            if (rf.getId() != null) {
                sublicencia.setIdRenewalForm(rf.getId());
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_SUBLICENCIA CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR CERTIFICADO_SUBLICENCIA");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararNuevo(ActionEvent ae) {
        dialogTitle = "NUEVO CERTIFICADO SUBLICENCIA";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar la Nueva Certificado_sublicencia?";
        sublicencia = new SubLicenciaUso();
        sublicencia.setTipoEstado("CERTIFICADO");
        Controlador c = new Controlador();
//        sublicencia.setSublicenciaNo(c.getNextSublicenciaUsoNo());
        System.out.println("---------------------------> " + c.getNextSublicenciaUsoNo());
        sublicencia.setResponsable(loginBean.getUsuario().getAlias());
        edicion = false;
        if (sublicencia != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void guardarCertificadoSublicencia(ActionEvent ae) {
        FacesMessage msg = null;
        if (sublicencia != null) {
            Controlador c = new Controlador();
            if (Operaciones.validarFecha(sublicencia.getFechaVenceContrato()) || (sublicencia.getVenceContrato() != null && !sublicencia.getVenceContrato().trim().isEmpty())) {
                if (sublicencia.getId() != null) {
                    if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                        if (estadoTemp.equals("NOTIFICADAS")) {
                            sublicencia.setNotificacion(c.getNextNumeroNotificacionSublicenciaUso(new Date()));
                            sublicencia.setTipoEstado("NOTIFICADA");
                        } else if (estadoTemp.equals("TERMINACION")) {
                            sublicencia.setResolucionNo(c.getNextSublicenciaUsoResolucionNoByTipo("TERMINACION"));
                            sublicencia.setTipoEstado("TERMINACION");
                        } else if (estadoTemp.equals("DESISTIDAS")) {
                            sublicencia.setResolucionNo(c.getNextSublicenciaUsoResolucionNoByTipo("DESISTIDA"));
                            sublicencia.setTipoEstado("DESISTIDA");
                        } else if (estadoTemp.equals("CADUCADAS")) {
                            sublicencia.setResolucionNo(c.getNextSublicenciaUsoResolucionNoByTipo("CADUCADA"));
                            sublicencia.setTipoEstado("CADUCADA");
                        }

                        if (c.updateSublicenciaUso(sublicencia)) {
                            c.saveHistorial(sublicencia.getTipoEstado() + "_SUBLICENCIA", "CERTIFICADO_SUBLICENCIA", sublicencia.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            loadCertificadosSublicenciasUso();
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_SUBLICENCIA EDITADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA CERTIFICADO_SUBLICENCIA");
                        }

                    } else {
                        //Editar CertificadoCD
                        if (c.validarExistenciaSublicenciaUso(sublicencia)) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                        } else {

                            sublicencia.setSolicitud(sublicencia.getSolicitud().toUpperCase());
                            if (c.updateSublicenciaUso(sublicencia)) {
                                c.saveHistorial("CERTIFICADO_SUBLICENCIA", "CERTIFICADO_SUBLICENCIA", sublicencia.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                                PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_SUBLICENCIA EDITADA CON ÉXITO");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA CERTIFICADO_SUBLICENCIA");
                            }
                        }
                    }
                } else {
//                //Guardar Certificado Licencia
                    if (c.existeSublicenciaUso(sublicencia.getSolicitud())) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                    } else {
                        boolean habilitado = true;
                        if (sublicencia.getDenominacion() != null && !sublicencia.getDenominacion().trim().isEmpty()
                                && sublicencia.getRegistro() != null && !sublicencia.getRegistro().trim().isEmpty()) {
                            if (c.existsTituloCanceladoByTituloAndDenominacion(sublicencia.getRegistro(), sublicencia.getDenominacion(), false)) {
                                TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(sublicencia.getRegistro(), sublicencia.getDenominacion());
                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + sublicencia.getRegistro() + " CON DENOMINACIÓN '"
                                            + sublicencia.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    sublicencia = new SubLicenciaUso();
                                    habilitado = false;
                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                    sublicencia.setCancelado(titca.getTipoCancelacion());
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + sublicencia.getRegistro() + " CON DENOMINACIÓN '"
                                            + sublicencia.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + sublicencia.getRegistro() + " CON DENOMINACIÓN '"
                                            + sublicencia.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    sublicencia = new SubLicenciaUso();
                                    habilitado = false;
                                }
                            } else {
                                habilitado = true;
                            }
                        }
                        if (habilitado) {
                            sublicencia.setSolicitud(sublicencia.getSolicitud().toUpperCase());
                            sublicencia.setTipoEstado("SUBLICENCIA");
                            sublicencia.setSublicenciaNo(c.getNextSublicenciaUsoNo());
                            if (c.saveSublicenciaUso(sublicencia)) {
                                c.saveModificacionApp(sublicencia.getDenominacion(), sublicencia.getRegistro(), sublicencia.getSolicitud(), "SUBLICENCIA DE USO", loginBean.getNombre());
                                c.saveHistorial("CERTIFICADO_SUBLICENCIA", "CERTIFICADO_SUBLICENCIA", sublicencia.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                                loadCertificadosSublicenciasUso();
                                PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_SUBLICENCIA GUARDADA CON ÉXITO");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL GUARDAR EL CERTIFICADO_SUBLICENCIA");
                            }
                        }

                    }
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "DEBE EXISTIR UNA ESPECIFICACIÓN DE VENCIMIENTO DE CONTRATO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO CARGAR EL CERTIFICADO_CN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /* Da la orden de visualizar el reporte, clase Informe (Webservlet)*/
    public void viewCertificadoLicencia(ActionEvent ae) {
        FacesMessage msg = null;
        sublicencia = (SubLicenciaUso) sublicenciasDataTable.getRowData();
        boolean correcto = false;
        if (sublicencia != null) {

            if (sublicencia.getRegistro() != null && !sublicencia.getRegistro().trim().isEmpty()) {
                System.out.println("Descargando Certificado_Sublicencia: " + sublicencia.getSolicitud());

                Controlador c = new Controlador();

                if (!c.validarDelegadoActivo("delegado")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarDelegacionActivo()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarResolucionActiva("transferencia")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else {

                    if (sublicencia.getSublicenciaNo() != null && !sublicencia.getSublicenciaNo().trim().isEmpty()) {
                        if (sublicencia.getRegistro() != null && !sublicencia.getRegistro().trim().isEmpty()) {
                            if (sublicencia.getFechaContrato() != null && validarFechaContrato(sublicencia.getFechaContrato())) {
                                if (sublicencia.getResponsable() != null && !sublicencia.getResponsable().trim().isEmpty()) {
                                    if (sublicencia.getCasilleroSenadi() != null && !sublicencia.getCasilleroSenadi().trim().isEmpty() && !sublicencia.getCasilleroSenadi().equals("null")) {
                                        if (sublicencia.getSublicenciante() != null && !sublicencia.getSublicenciante().trim().isEmpty() && !sublicencia.getSublicenciante().equals("null")) {
                                            correcto = true;
                                            loginBean.setVarious(false);
                                            loginBean.setSublicencia(sublicencia);

                                            PrimeFaces.current().ajax().addCallbackParam("doit", true);

                                            System.out.println("envía certificado descargar");
                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO DOCUMENTO " + sublicencia.getSolicitud());
                                        } else {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + sublicencia.getSolicitud() + " NO TIENE SUBLICENCIANTE");
                                        }
                                    } else {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + sublicencia.getSolicitud() + " NO TIENE CASILLERO");
                                    }
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + sublicencia.getSolicitud() + " NO TIENE RESPONSABLE ASIGNADO");
                                }
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + sublicencia.getSolicitud() + " NO TIENE UNA FECHA DE CONTRATO VÁLIDA");
                            }
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + sublicencia.getSolicitud() + " NO POSEE NÚMERO DE REGISTRO");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + sublicencia.getSolicitud() + " NO TIENE UN NÚMERO DE LICENCIA VÁLIDO");
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

    public void buscarCasillero(ActionEvent ae) {
        if (sublicencia != null && sublicencia.getId() != null) {
            Controlador c = new Controlador();
            sublicencia.setCasilleroSenadi(c.buscarCasilleroBySolicitud(sublicencia.getSolicitud()));
        }
    }

    public boolean validarFechaContrato(Date fechacontrato) {
        if (fechacontrato.getYear() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public void prepararHistorial(ActionEvent ae) {
        sublicencia = (SubLicenciaUso) sublicenciasDataTable.getRowData();
        if (sublicencia != null) {
            dialogTitle = "SEGUIMIENTO " + sublicencia.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(sublicencia.getSolicitud());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (getHistorial().trim().isEmpty()) {
                historial = "Estado actual: CERTIFICADO_SUBLICENCIA";
            }
        }
    }

    public void downloadSelected(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedSublicencias.isEmpty()) {

            System.out.println("Descargando Múltiples Certificados_sublicencia...");
            String mensaje = "";

            boolean flag = false;
            for (int i = 0; i < selectedSublicencias.size(); i++) {
                SubLicenciaUso aux = selectedSublicencias.get(i);
                if (aux.getSublicenciaNo() != null && !aux.getSublicenciaNo().trim().isEmpty()) {
                    if (aux.getRegistro() != null && !aux.getRegistro().trim().isEmpty()) {
                        if (aux.getFechaContrato() != null && validarFechaContrato(aux.getFechaContrato())) {
                            if (aux.getResponsable() != null && !aux.getResponsable().trim().isEmpty()) {
                                if (aux.getCasilleroSenadi() != null && !aux.getCasilleroSenadi().trim().isEmpty() && !aux.getCasilleroSenadi().equals("null")) {
                                    if (aux.getSublicenciante() != null && !aux.getSublicenciante().trim().isEmpty() && !aux.getSublicenciante().equals("null")) {
                                        flag = true;
                                    } else {
                                        mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE SUBLICENCIANTE";
                                        flag = false;
                                        break;
                                    }
                                } else {
                                    mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE CASILLERO ASIGNADO";
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
                    mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO POSEE UN NÚMERO DE LICENCIA VÁLIDO";
                    flag = false;
                    break;
                }
            }
            if (flag) {
                loginBean.setVarious(true);
                loginBean.setSublicencias(selectedSublicencias);

                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "DESCARGA", "DESCARGANDO REGISTROS SELECCIONADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN REGISTRO", mensaje);
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN SELECCIÓN", "SELECCIONE AL MENOS UN REGISTRO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void verNotificacionSubLicenciaUso(ActionEvent ae) {
        FacesMessage msg = null;
        sublicencia = (SubLicenciaUso) sublicenciasDataTable.getRowData();
        if (sublicencia != null) {

            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionByCriterio(sublicencia.getSolicitud(), true);
            if (uploads.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCUENTRA UN CERTIFICADO REALIZADO PARA ESTE TRÁMITE");
            } else {

                String rutaCertCasillero = "";
                for (int i = 0; i < uploads.size(); i++) {
                    UploadNotificacion un = uploads.get(i);
                    String rutaux = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + un.getCasillero() + "/" + un.getDocumento();
                    int conf = Operaciones.validaTextoEnPdf(rutaux, "INSCRIPCIÓN SUBLICENCIA DE USO N");
                    if (conf == 1) {
                        rutaCertCasillero = rutaux;
                        break;
                    }
                }
                if (!rutaCertCasillero.trim().isEmpty()) {
                    PrimeFaces.current().ajax().addCallbackParam("notit", true);
                    PrimeFaces.current().ajax().addCallbackParam("rutanot", rutaCertCasillero);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SUBLICENCIA USO", "VISUALIZANDO CERTIFICADO");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCUENTRA UN CERTIFICADO REALIZADO PARA ESTE TRÁMITE");
                }
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "FECHA", "HUBO UN PROBLEMA AL CARGAR EL REGISTRO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (sublicencia != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + sublicencia.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(sublicencia.getIdRenewalForm(), sublicencia.getSolicitud());
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
     * @return the sublicencias
     */
    public List<SubLicenciaUso> getSublicencias() {
        return sublicencias;
    }

    /**
     * @param sublicencias the sublicencias to set
     */
    public void setSublicencias(List<SubLicenciaUso> sublicencias) {
        this.sublicencias = sublicencias;
    }

    /**
     * @return the sublicenciasFiltradas
     */
    public List<SubLicenciaUso> getSublicenciasFiltradas() {
        return sublicenciasFiltradas;
    }

    /**
     * @param sublicenciasFiltradas the sublicenciasFiltradas to set
     */
    public void setSublicenciasFiltradas(List<SubLicenciaUso> sublicenciasFiltradas) {
        this.sublicenciasFiltradas = sublicenciasFiltradas;
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
     * @return the sublicenciasDataTable
     */
    public UIData getSublicenciasDataTable() {
        return sublicenciasDataTable;
    }

    /**
     * @param sublicenciasDataTable the sublicenciasDataTable to set
     */
    public void setSublicenciasDataTable(UIData sublicenciasDataTable) {
        this.sublicenciasDataTable = sublicenciasDataTable;
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
     * @return the sublicencia
     */
    public SubLicenciaUso getSublicencia() {
        return sublicencia;
    }

    /**
     * @param sublicencia the certificado to set
     */
    public void setSublicencia(SubLicenciaUso sublicencia) {
        this.sublicencia = sublicencia;
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
    public List<SubLicenciaUso> getSelectedCertificados() {
        return selectedSublicencias;
    }

    /**
     * @param selectedCertificados the selectedCertificados to set
     */
    public void setSelectedCertificados(List<SubLicenciaUso> selectedCertificados) {
        this.selectedSublicencias = selectedCertificados;
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
