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
import senadi.gob.ec.transfweb.model.PersonaLicenciaUso;
import senadi.gob.ec.transfweb.model.TituloCancelado;
import senadi.gob.ec.transfweb.model.UploadNotificacion;
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepform.ModificacionApp;
import senadi.gob.ec.transfweb.model.iepform.PaymentReceipt;
import senadi.gob.ec.transfweb.model.iepform.Person;
import senadi.gob.ec.transfweb.model.iepform.PersonRenewal;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepform.Types;
import senadi.gob.ec.transfweb.model.iepicas.Owner;
import senadi.gob.ec.transfweb.model.licencia.LicenciaUso;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author micharesp
 */
@ManagedBean(name = "certificadoLicenciaBean")
@ViewScoped
public class CertificadoLicenciaBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private Date fechaInicioCertificado;
    private Date fechaFinCertificado;

    private List<LicenciaUso> licencias;
    private List<LicenciaUso> licenciasFiltradas;

    private UIData licenciasDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private LicenciaUso licencia;

    private LoginBean loginBean;

    private String estadoTemp;
    private String historial;

    private String exportName;

    private List<LicenciaUso> selectedLicencias;

    private boolean separado;

    private List<Documento> archivos;

    private String criterioOwner;
    private List<Owner> owners;
    private List<Owner> ownersFiltrados;
    private UIData ownerDataTable;

    private List<PersonaLicenciaUso> personas;

    public CertificadoLicenciaBean() {
        loadCertificadosLicenciasUso();
    }

    private void loadCertificadosLicenciasUso() {
        Controlador c = new Controlador();
        licencias = c.getLicenciasUsoByTipo("LICENCIA");
        numRegistros = "Número Registros Mostrados: " + licencias.size();
        exportName = "certificado_licencia_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
        selectedLicencias = new ArrayList<>();

        owners = new ArrayList<>();
        criterioOwner = "";
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;
        if (licencia != null && licencia.getSolicitud() != null && !licencia.getSolicitud().trim().isEmpty()) {

            String tramite = licencia.getSolicitud();
            Controlador c = new Controlador();
            if (c.existeLicenciaUso(tramite)) {
                LicenciaUso aux = c.getLicenciaUsoBySolicitud(tramite);
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL TRÁMITE YA SE ENCUENTRA REGISTRADO EN LA PESTAÑA DE " + aux.getTipoEstado());
            } else {

                ModificacionApp mapp = c.getModificacionApp(tramite);
                if (mapp.getId() != null) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TRÁMITE NO SE PUEDE REGISTRAR: " + mapp.getObservacion());
                } else {
                    if (c.existeSublicenciaUso(tramite)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL TRÁMITE YA SE ENCUENTRA REGISTRADO EN SUBLICENCIAS USO");
                    } else {
                        RenewalForm rf = c.getRenewalFormsByApplicationNumber(tramite);

                        if (rf.getId() != null) {
                            if (rf.getStatus().equals("DELIVERED")) {

                                Types t = c.getTypes(rf.getTransactionMotiveId());

                                Types ttp = c.getTypes(rf.getFormId());

                                if (t.getId() != null && ttp.getId() != null) {
                                    if (!ttp.getAlias().equals("PI") && !ttp.getAlias().equals("MU") && !ttp.getAlias().equals("DI")) {
                                        if (t.getName().trim().toLowerCase().contains("licencia de uso")) {
                                            if (rf.getLicenseType() == null || !rf.getLicenseType().equals("SUBLICENSE")) {
                                                PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                                licencia.setComprobante(payment.getVoucherNumber());
                                                licencia.setFechaPresentacion(rf.getApplicationDate());
                                                licencia.setLicenciaNo(c.getNextLicenciaUsoNo());

                                                licencia.setSigno(ttp.getAlias());
                                                licencia.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");

                                                List<PersonRenewal> solicitantes = c.getPersonRenewalByIdRenewalAndType(rf.getId(), "'APPLICANT'");
                                                if (!solicitantes.isEmpty()) {
                                                    licencia.setSolicitante(solicitantes.get(0).getName());
                                                }

                                                licencia.setIdRenewalForm(rf.getId());

                                                if (rf.getDebugId() != null && rf.getDebugId() != 0) {
                                                    HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                                    if (hf.getId() != null) {
                                                        licencia.setDenominacion(hf.getDenomination());
                                                        licencia.setRegistro(hf.getExpedient());

                                                        if (licencia.getRegistro() != null && !licencia.getRegistro().trim().isEmpty()) {
                                                            PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(licencia.getRegistro(), licencia.getDenominacion());
                                                            if (titulo.getCodigoSolicitudSigno() != null) {
                                                                licencia.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                            }
                                                        }
                                                        if (licencia.getFechaRegistro() == null) {
                                                            licencia.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                        }
                                                    }
                                                } else {
                                                    if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                        PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                        if (ps.getCodigoSolicitudSigno() != null) {
                                                            licencia.setDenominacion(ps.getDenominacionSigno());
                                                            PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                            if (titulo.getCodigoSolicitudSigno() != null) {
                                                                licencia.setRegistro(titulo.getNumeroTitulo());
                                                                licencia.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                            }
                                                        }
                                                    }
                                                }

                                                Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "ATTORNEY");
                                                if (apoder.getId() != null) {
                                                    licencia.setApoderadoRepresentante(apoder.getName());
                                                    licencia.setEmail(apoder.getEmail());
                                                }

                                                List<Person> applicants = c.getPersonsByType(rf.getId(), "APPLICANT");
                                                licencia.setLicenciante("");
                                                for (int i = 0; i < applicants.size(); i++) {
                                                    String salto = ", ";
                                                    if (i == applicants.size() - 1) {
                                                        salto = "";
                                                    }
                                                    licencia.setLicenciante(licencia.getLicenciante() + applicants.get(i).getName() + salto);
                                                }
                                                List<Person> beneficiaries = c.getPersonsByType(rf.getId(), "BENEFICIARY");
                                                licencia.setLicenciatario("");
                                                for (int i = 0; i < beneficiaries.size(); i++) {
                                                    String salto = ", ";
                                                    if (i == beneficiaries.size() - 1) {
                                                        salto = "";
                                                    }
                                                    licencia.setLicenciatario(licencia.getLicenciatario() + beneficiaries.get(i).getName() + salto);
                                                }

                                                if (licencia.getRegistro() != null && !licencia.getRegistro().trim().isEmpty()) {
                                                    if (licencia.getDenominacion() != null && !licencia.getDenominacion().trim().isEmpty()) {
                                                        if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                            if (c.existsTituloCanceladoByTituloAndExpediente(licencia.getRegistro(), rf.getExpedient(), false)) {
                                                                TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(licencia.getRegistro(), rf.getExpedient());
                                                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licencia.getRegistro() + " CON DENOMINACIÓN "
                                                                            + licencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                    licencia = new LicenciaUso();
                                                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                                    licencia.setCancelado(titca.getTipoCancelacion());
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licencia.getRegistro() + " CON DENOMINACIÓN "
                                                                            + licencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                } else {
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licencia.getRegistro() + " CON DENOMINACIÓN "
                                                                            + licencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                    licencia = new LicenciaUso();
                                                                }
                                                            } else {
                                                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                            }
                                                        } else {
                                                            if (c.existsTituloCanceladoByTituloAndDenominacion(licencia.getRegistro(), licencia.getDenominacion(), false)) {
                                                                TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(licencia.getRegistro(), licencia.getDenominacion());
                                                                if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licencia.getRegistro() + " CON DENOMINACIÓN "
                                                                            + licencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                    licencia = new LicenciaUso();
                                                                } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                                    licencia.setCancelado(titca.getTipoCancelacion());
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licencia.getRegistro() + " CON DENOMINACIÓN "
                                                                            + licencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                } else {
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licencia.getRegistro() + " CON DENOMINACIÓN "
                                                                            + licencia.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                                    licencia = new LicenciaUso();
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
                                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TRÁMITE " + rf.getApplicationNumber() + " ES UNA SUBLICENCIA");
                                            }
                                        } else {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UNA LICENCIA DE USO, SINO '" + t.getName().toUpperCase() + "'");
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

    public void buscarCertificadosLicencias(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            licencias = c.getLicenciasUsoByCriteriaAndType(criterio, "LICENCIA");
            numRegistros = "Número Registros Mostrados: " + licencias.size();
            if (licencias.isEmpty()) {
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

    public void validarCertificadoLicencia(LicenciaUso licen) {
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
                        int conf = Operaciones.validaTextoEnPdf(rutaux, "CONTRATO DE LICENCIA");
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
            licencias = c.getLicenciasUsoByFechaAndType(fechaInicio, fechaFin, "LICENCIA");
            numRegistros = "Número Registros Mostrados: " + licencias.size();
            if (licencias.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarCertificadoLicenciaPorFechaLicencia(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechasCertificado()) {
            Controlador c = new Controlador();
            licencias = c.getLicenciasUsoByFechaLicenciaAndType(fechaInicioCertificado, fechaFinCertificado, "LICENCIA");
            numRegistros = "Número Registros Mostrados: " + licencias.size();
            if (licencias.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarCertificadoLicencia(ActionEvent ae) {
        FacesMessage msg = null;
        licencia = (LicenciaUso) licenciasDataTable.getRowData();
        if (licencia != null) {
            Controlador c = new Controlador();
            if (c.removeLicenciaUso(licencia)) {
                c.saveHistorial("CERTIFICADO_LICENCIA", "CERTIFICADO_LICENCIA ", licencia.getSolicitud(), "ELIMINADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                loadCertificadosLicenciasUso();
                System.out.println("Certificado_licencia " + licencia.getSolicitud() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_LICENCIA " + licencia.getSolicitud() + "ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR CERTIFICADO_LICENCIA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR CERTIFICADO_LICENCIA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {

        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
        licencia = (LicenciaUso) licenciasDataTable.getRowData();
        if (licencia != null) {
            dialogTitle = "EDITAR CERTIFICADO LICENCIA " + licencia.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar la Certificado_Licencia: " + licencia.getSolicitud() + "?";
            Controlador c = new Controlador();
            RenewalForm rf = c.getRenewalFormsByApplicationNumber(licencia.getSolicitud());
            if (rf.getId() != null) {
                licencia.setIdRenewalForm(rf.getId());
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_LICENCIA CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR CERTIFICADO_LICENCIA");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararNuevo(ActionEvent ae) {
        dialogTitle = "NUEVO CERTIFICADO LICENCIA";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar la Nueva Certificado_licencia?";
        licencia = new LicenciaUso();
        licencia.setTipoEstado("CERTIFICADO");

        licencia.setFechaLicencia(Operaciones.cambiarFechaADiaDado(new Date(), 5));//Se pone 5 porque se hace referencia al día viernes

//        Controlador c = new Controlador();
//        licencia.setLicenciaNo(c.getNextLicenciaUsoNo());
        licencia.setResponsable(loginBean.getUsuario().getAlias());
        edicion = false;

//        Controlador c = new Controlador();
//        c.depurarLicencias();       
        if (licencia != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void guardarCertificadoLicencia(ActionEvent ae) {
        FacesMessage msg = null;
        if (licencia != null) {
            Controlador c = new Controlador();
            if (Operaciones.validarFecha(licencia.getFechaVenceContrato()) || (licencia.getVenceContrato() != null && !licencia.getVenceContrato().trim().isEmpty())) {
                if (licencia.getId() != null) {
                    if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                        if (estadoTemp.equals("NOTIFICADAS")) {
                            licencia.setNotificacion(c.getNextNumeroNotificacionLicenciaUso(new Date()));
                            licencia.setTipoEstado("NOTIFICADA");
                        } else if (estadoTemp.equals("TERMINACION")) {
                            licencia.setTerminacionNo(c.getNextLicenciaTerminacionNo(new Date()));
                            licencia.setFechaTerminacion(new Date());
                            licencia.setTipoEstado("TERMINACION");
                        } else if (estadoTemp.equals("DESISTIDAS")) {
                            licencia.setResolucionNo(c.getNextLicenciaUsoResolucionNoByTipo("DESISTIDA"));
                            licencia.setTipoEstado("DESISTIDA");
                        } else if (estadoTemp.equals("CADUCADAS")) {
                            licencia.setResolucionNo(c.getNextLicenciaUsoResolucionNoByTipo("CADUCADA"));
                            licencia.setTipoEstado("CADUCADA");
                        }
                        if (c.updateLicenciaUso(licencia)) {
                            c.saveHistorial(licencia.getTipoEstado() + "_LICENCIA", "CERTIFICADO_LICENCIA", licencia.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            loadCertificadosLicenciasUso();
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_LICENCIA EDITADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA CERTIFICADO_LICENCIA");
                        }

                    } else {
//                        System.out.println("vencecontrato: " + licencia.getVenceContrato());
                        //Editar CertificadoCD
                        if (c.validarExistenciaLicenciaUso(licencia)) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                        } else {
                            licencia.setSolicitud(licencia.getSolicitud().toUpperCase());
                            if (c.updateLicenciaUso(licencia)) {
                                c.saveHistorial("CERTIFICADO_LICENCIA", "CERTIFICADO_LICENCIA", licencia.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                                PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_LICENCIA " + licencia.getSolicitud() + " EDITADA CON ÉXITO");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA CERTIFICADO_LICENCIA");
                            }
                        }
                    }
                } else {
//                //Guardar Certificado Licencia
                    if (c.existeLicenciaUso(licencia.getSolicitud())) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                    } else {
                        boolean habilitado = true;
                        if (licencia.getDenominacion() != null && !licencia.getDenominacion().trim().isEmpty()
                                && licencia.getRegistro() != null && !licencia.getRegistro().trim().isEmpty()) {
                            if (c.existsTituloCanceladoByTituloAndDenominacion(licencia.getRegistro(), licencia.getDenominacion(), false)) {
                                TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(licencia.getRegistro(), licencia.getDenominacion());
                                if (titca.getId() != null && titca.getTipoCancelacion().equals("TOTAL")) {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licencia.getRegistro() + " CON DENOMINACIÓN '"
                                            + licencia.getDenominacion() + "' SE ENCUENTRA CANCELADO; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    licencia = new LicenciaUso();
                                    habilitado = false;
                                } else if (titca.getId() != null && titca.getTipoCancelacion().equals("PARCIAL")) {
                                    licencia.setCancelado("PARCIAL");
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licencia.getRegistro() + " CON DENOMINACIÓN '"
                                            + licencia.getDenominacion() + "' SE ENCUENTRA CANCELADO PARCIALMENTE; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + licencia.getRegistro() + " CON DENOMINACIÓN '"
                                            + licencia.getDenominacion() + "' SE ENCUENTRA CANCELADO; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                    licencia = new LicenciaUso();
                                    habilitado = false;
                                }
                            } else {
                                habilitado = true;
                            }
                        }
                        if (habilitado) {
                            licencia.setSolicitud(licencia.getSolicitud().toUpperCase());
                            licencia.setTipoEstado("LICENCIA");
                            licencia.setLicenciaNo(c.getNextLicenciaUsoNo());
                            if (c.saveLicenciaUso(licencia)) {
                                c.saveModificacionApp(licencia.getDenominacion(), licencia.getRegistro(), licencia.getSolicitud(), "LICENCIA DE USO", loginBean.getNombre());
                                c.saveHistorial("CERTIFICADO_LICENCIA", "CERTIFICADO_LICENCIA", licencia.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                                loadCertificadosLicenciasUso();
                                PrimeFaces.current().ajax().addCallbackParam("saved", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO_LICENCIA " + licencia.getSolicitud() + " GUARDADA CON ÉXITO");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL GUARDAR EL CERTIFICADO_LICENCIA");
                            }
                        }
                    }
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "DEBE EXISTIR UNA ESPECIFICACIÓN DE VENCIMIENTO DE CONTRATO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO CARGAR LA LICENCIA DE USO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /* Da la orden de visualizar el reporte, clase Informe (Webservlet)*/
    public void viewCertificadoLicencia(ActionEvent ae) {
        FacesMessage msg = null;
        licencia = (LicenciaUso) licenciasDataTable.getRowData();
        boolean correcto = false;
        if (licencia != null) {

            if (licencia.getRegistro() != null && !licencia.getRegistro().trim().isEmpty()) {
                Controlador c = new Controlador();

                if (!c.validarDelegadoActivo("delegado")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarDelegacionActivo()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarResolucionActiva("transferencia")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else {

                    if (licencia.getLicenciaNo() != null && !licencia.getLicenciaNo().trim().isEmpty()) {
                        if (licencia.getRegistro() != null && !licencia.getRegistro().trim().isEmpty()) {
                            if (licencia.getFechaContrato() != null && validarFechaContrato(licencia.getFechaContrato())) {
                                if (licencia.getResponsable() != null && !licencia.getResponsable().trim().isEmpty()) {
                                    if (licencia.getCasilleroSenadi() != null && !licencia.getCasilleroSenadi().trim().isEmpty() && !licencia.getCasilleroSenadi().equals("null")) {
                                        if (licencia.getLicenciante() != null && !licencia.getLicenciante().trim().isEmpty() && !licencia.getLicenciante().equals("null")) {
                                            if ((licencia.getCasilleroSenadiLicenciatario() != null && !licencia.getCasilleroSenadiLicenciatario().trim().isEmpty())
                                                    || (licencia.getEmail() != null && !licencia.getEmail().trim().isEmpty())) {
                                                if (Operaciones.validarFecha(licencia.getFechaVenceContrato()) || (licencia.getVenceContrato() != null && !licencia.getVenceContrato().trim().isEmpty())) {
                                                    if (licencia.getSolicitante() != null && !licencia.getSolicitante().trim().isEmpty()) {
                                                        System.out.println("Descargando Certificado_Licencia: " + licencia.getSolicitud());
                                                        correcto = true;
                                                        loginBean.setVarious(false);
                                                        loginBean.setLicencia(licencia);

                                                        PrimeFaces.current().ajax().addCallbackParam("doit", true);

                                                        System.out.println("envía certificado descargar");
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO DOCUMENTO " + licencia.getSolicitud());
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + licencia.getSolicitud() + " NO TIENE SOLICITANTE");
                                                    }

                                                } else {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + licencia.getSolicitud() + " DEBE TENER UNA ESPECIFICACIÓN DE VENCIMIENTO DE CONTRATO");
                                                }
                                            } else {
                                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + licencia.getSolicitud() + " DEBE TENER UN CASILLERO LICENCIATARIO O UN CORREO");
                                            }

                                        } else {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + licencia.getSolicitud() + " NO TIENE LICENCIANTE");
                                        }
                                    } else {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + licencia.getSolicitud() + " NO TIENE CASILLERO");
                                    }
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + licencia.getSolicitud() + " NO TIENE RESPONSABLE ASIGNADO");
                                }
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + licencia.getSolicitud() + " NO TIENE UNA FECHA DE CONTRATO VÁLIDA");
                            }
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + licencia.getSolicitud() + " NO POSEE NÚMERO DE REGISTRO");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + licencia.getSolicitud() + " NO TIENE UN NÚMERO DE LICENCIA VÁLIDO");
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
        if (licencia != null && licencia.getId() != null) {
            Controlador c = new Controlador();
            licencia.setCasilleroSenadi(c.buscarCasilleroBySolicitud(licencia.getSolicitud()));
        }
    }

    public void buscarCasilleroLicenciatario(ActionEvent ae) {
        FacesMessage msg = null;
        if (licencia != null) {
            if (criterioOwner != null && !criterioOwner.trim().isEmpty()) {
                System.out.println("criterio owner lic: " + criterioOwner);
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

    public boolean validarFechaContrato(Date fechacontrato) {
        if (fechacontrato.getYear() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public void prepararHistorial(ActionEvent ae) {
        licencia = (LicenciaUso) licenciasDataTable.getRowData();
        if (licencia != null) {
            dialogTitle = "SEGUIMIENTO " + licencia.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(licencia.getSolicitud());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (getHistorial().trim().isEmpty()) {
                historial = "Estado actual: CERTIFICADO_LICENCIA";
            }
        }
    }

    public void downloadSelected(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedLicencias.isEmpty()) {

            String mensaje = "";

            boolean flag = false;
            for (int i = 0; i < selectedLicencias.size(); i++) {
                LicenciaUso aux = selectedLicencias.get(i);
                if (aux.getLicenciaNo() != null && !aux.getLicenciaNo().trim().isEmpty()) {
                    if (aux.getRegistro() != null && !aux.getRegistro().trim().isEmpty()) {
                        if (aux.getFechaContrato() != null && validarFechaContrato(aux.getFechaContrato())) {
                            if (aux.getResponsable() != null && !aux.getResponsable().trim().isEmpty()) {
                                if (aux.getCasilleroSenadi() != null && !aux.getCasilleroSenadi().trim().isEmpty() && !aux.getCasilleroSenadi().equals("null")) {
                                    if (aux.getLicenciante() != null && !aux.getLicenciante().trim().isEmpty() && !aux.getLicenciante().equals("null")) {
                                        if ((aux.getCasilleroSenadiLicenciatario() != null && !aux.getCasilleroSenadiLicenciatario().trim().isEmpty())
                                                || (aux.getEmail() != null && !aux.getEmail().trim().isEmpty())) {
                                            if (Operaciones.validarFecha(aux.getFechaVenceContrato()) || (aux.getVenceContrato() != null && !aux.getVenceContrato().trim().isEmpty())) {
                                                if (aux.getSolicitante() != null && !aux.getSolicitante().trim().isEmpty()) {
                                                    System.out.println("Descargando Múltiples Certificados_licencia...");
                                                    flag = true;
                                                } else {
                                                    mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE SOLICITANTE";
                                                    flag = false;
                                                    break;
                                                }
                                            } else {
                                                mensaje = "EL REGISTRO " + aux.getSolicitud() + " DEBE TENER UNA ESPECIFICACIÓN DE VENCIMIENTO DE CONTRATO";
                                                flag = false;
                                                break;
                                            }
                                        } else {
                                            mensaje = "EL REGISTRO " + aux.getSolicitud() + " DEBE TENER UN CASILLERO LICENCIATARIO O UN CORREO";
                                            flag = false;
                                            break;
                                        }
                                    } else {
                                        mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE LICENCIANTE";
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
                loginBean.setLicencias(selectedLicencias);

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

    public void verNotificacionLicenciaUso(ActionEvent ae) {
        FacesMessage msg = null;
        licencia = (LicenciaUso) licenciasDataTable.getRowData();
        if (licencia != null) {

            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionByCriterio(licencia.getSolicitud(), true);
            if (uploads.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCUENTRA UN CERTIFICADO REALIZADO PARA ESTE TRÁMITE");
            } else {

                String rutaCertCasillero = "";
                for (int i = 0; i < uploads.size(); i++) {
                    UploadNotificacion un = uploads.get(i);
                    String rutaux = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + un.getCasillero() + "/" + un.getDocumento();
                    int conf = Operaciones.validaTextoEnPdf(rutaux, "CONTRATO DE LICENCIA DE USO DE");
                    if (conf == 1) {
                        rutaCertCasillero = rutaux;
                        break;
                    }
                }
                if (!rutaCertCasillero.trim().isEmpty()) {
                    PrimeFaces.current().ajax().addCallbackParam("notit", true);
                    PrimeFaces.current().ajax().addCallbackParam("rutanot", rutaCertCasillero);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "LICENCIA USO", "VISUALIZANDO CERTIFICADO");
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
        if (licencia != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + licencia.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(licencia.getIdRenewalForm(), licencia.getSolicitud());
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

    public void prepararBusquedaCasillero(ActionEvent ae) {
        owners = new ArrayList<>();
        criterioOwner = "";
    }

    public void seleccionarCasillero(ActionEvent ae) {
        FacesMessage msg = null;
        if (licencia != null) {
            Owner own = (Owner) ownerDataTable.getRowData();
            if (own != null) {
                licencia.setCasilleroSenadiLicenciatario(own.getCasillero());
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

    public void limpiarBusquedaCasillero(ActionEvent ae) {
        owners = new ArrayList<>();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "HECHO");
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
     * @return the licencias
     */
    public List<LicenciaUso> getLicencias() {
        return licencias;
    }

    /**
     * @param licencias the licencias to set
     */
    public void setLicencias(List<LicenciaUso> licencias) {
        this.licencias = licencias;
    }

    /**
     * @return the licenciasFiltradas
     */
    public List<LicenciaUso> getLicenciasFiltradas() {
        return licenciasFiltradas;
    }

    /**
     * @param licenciasFiltradas the licenciasFiltradas to set
     */
    public void setLicenciasFiltradas(List<LicenciaUso> licenciasFiltradas) {
        this.licenciasFiltradas = licenciasFiltradas;
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
     * @return the licenciasDataTable
     */
    public UIData getLicenciasDataTable() {
        return licenciasDataTable;
    }

    /**
     * @param licenciasDataTable the licenciasDataTable to set
     */
    public void setLicenciasDataTable(UIData licenciasDataTable) {
        this.licenciasDataTable = licenciasDataTable;
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
     * @return the licencia
     */
    public LicenciaUso getLicencia() {
        return licencia;
    }

    /**
     * @param licencia the certificado to set
     */
    public void setLicencia(LicenciaUso licencia) {
        this.licencia = licencia;
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
    public List<LicenciaUso> getSelectedCertificados() {
        return selectedLicencias;
    }

    /**
     * @param selectedCertificados the selectedCertificados to set
     */
    public void setSelectedCertificados(List<LicenciaUso> selectedCertificados) {
        this.selectedLicencias = selectedCertificados;
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
     * @return the personas
     */
    public List<PersonaLicenciaUso> getPersonas() {
        return personas;
    }

    /**
     * @param personas the personas to set
     */
    public void setPersonas(List<PersonaLicenciaUso> personas) {
        this.personas = personas;
    }

}
