/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.bean;

import java.io.Serializable;
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
import senadi.gob.ec.transfweb.model.prenda.PrendaComercial;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author Michael
 */
@ManagedBean(name = "levantamientoPrendaBean")
@ViewScoped
public class LevantamientoPrendaBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private List<PrendaComercial> levantamientos;
    private List<PrendaComercial> levantamientosFiltradas;

    private UIData levantamientoDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private PrendaComercial levantamiento;

    private String estadoTemp;
    private String historial;

    private LoginBean loginBean;

    private String exportName;

    private List<Documento> archivos;

    private String rutaNotificacionCasillero;

    private List<PrendaComercial> selectedLevantamientos;

    public LevantamientoPrendaBean() {
        loadLevantamientosPrenda();
    }

    private void loadLevantamientosPrenda() {
        Controlador c = new Controlador();
        levantamientos = c.getPrendasComercialesByTipo("LEVANTAMIENTO");
        numRegistros = "Número Registros Mostrados: " + levantamientos.size();
        exportName = "levantamiento_prenda" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
    }

    public void buscarLevantamiento(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            levantamientos = c.getPrendasComercialesByCriteriaAndType(criterio, "LEVANTAMIENTO");

            numRegistros = "Número Registros Mostrados: " + levantamientos.size();
            if (levantamientos.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void validarLevantamientoPrenda(PrendaComercial prend) {
        FacesMessage msg = null;
        if (prend != null) {
            rutaNotificacionCasillero = "";
            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionBySolicitud(prend.getSolicitud(), true);
//            System.out.println("existe: "+uploads.size());
            if (!uploads.isEmpty()) {
                if (uploads.size() > 1) {
                    for (int i = 0; i < uploads.size(); i++) {
                        UploadNotificacion unaux = uploads.get(i);
                        String rutaux = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                        int conf1 = Operaciones.validaTextoEnPdf(rutaux, "LEVANTAMIENTO PRENDA COMERCIAL No");
                        int conf2 = Operaciones.validaTextoEnPdf(rutaux, "LEVANTAMIENTO INSCRIPC");
                        if (conf1 == 1 || conf2 == 1) {
                            rutaNotificacionCasillero = rutaux;
                            break;
                        }
                    }
                    if (!rutaNotificacionCasillero.trim().isEmpty()) {
                        PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                        PrimeFaces.current().ajax().addCallbackParam("view", rutaNotificacionCasillero);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "LEVANTAMIENTO CARGAD0");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UN LEVANTAMIENTO SELECCIONADO");
                    }
                } else {
                    UploadNotificacion unaux = uploads.get(0);
                    rutaNotificacionCasillero = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                    System.out.println("rutacertcas: " + rutaNotificacionCasillero);
                    PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                    PrimeFaces.current().ajax().addCallbackParam("view", rutaNotificacionCasillero);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "LEVANTAMIENTO CARGADO");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ NINGÚN LEVANTAMIENTO DEL TRÁMITE " + prend.getSolicitud());
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UN LEVANTAMIENTO SELECCIONADA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;

        if (levantamiento != null && levantamiento.getSolicitud() != null && !levantamiento.getSolicitud().trim().isEmpty()) {
//            System.out.println(transferencia.getSolicitud());
            String tramite = levantamiento.getSolicitud();
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

                                    levantamiento.setComprobante(payment.getVoucherNumber());
                                    levantamiento.setFechaPresentacion(rf.getApplicationDate());
//                                levantamiento.setCertificado(c.getNextNumeroCertificadoTransferencia() + "");
                                    levantamiento.setSigno(ttp.getAlias());
                                    levantamiento.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");

                                    levantamiento.setIdRenewalForm(rf.getId());
                                    if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                        HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                        if (hf.getId() != null) {
                                            levantamiento.setDenominacion(hf.getDenomination());
                                            levantamiento.setRegistro(hf.getExpedient());

                                            if (levantamiento.getRegistro() != null && !levantamiento.getRegistro().trim().isEmpty()) {
                                                PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(levantamiento.getRegistro(), levantamiento.getDenominacion());
                                                if (titulo.getCodigoSolicitudSigno() != null) {
                                                    levantamiento.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                    levantamiento.setTitularAnterior(titulo.getTitular());
                                                }
                                            }
                                            if (levantamiento.getFechaRegistro() == null) {
                                                levantamiento.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                            }
                                        }
                                    } else {
                                        if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                            PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                            if (ps.getCodigoSolicitudSigno() != null) {
                                                levantamiento.setDenominacion(ps.getDenominacionSigno());
                                                PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                if (titulo.getCodigoSolicitudSigno() != null) {
                                                    levantamiento.setRegistro(titulo.getNumeroTitulo());
                                                    levantamiento.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                    levantamiento.setTitularAnterior(titulo.getTitular());
                                                }

                                                if (levantamiento.getTitularAnterior() == null || levantamiento.getTitularAnterior().trim().isEmpty()) {
                                                    PpdiPersona persona = c.getASolicitanteByCodigoSolicitud(ps.getCodigoSolicitudSigno());
                                                    if (persona.getCodigoPersona() != null) {
                                                        levantamiento.setTitularAnterior(persona.getNombrePersona());
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Person titAct = c.getTitularActual(rf.getId());
                                    if (titAct.getId() != null) {
                                        levantamiento.setTitularActual(titAct.getName());
                                        levantamiento.setDomicilioTitularActual(titAct.getAddress());
                                        levantamiento.setIdentificacion(titAct.getIdentificationNumber());
                                    }

                                    Person abog = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "LAWYER");
                                    if (abog.getId() != null) {
                                        levantamiento.setAbogadoPatrocinador(abog.getName());
                                        levantamiento.setEmail(abog.getEmail());
                                    }

                                    Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "AGENT");
                                    if (apoder.getId() != null) {
                                        levantamiento.setTitApodRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "AGENT"));
                                    }

                                    Person acreed = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "BENEFICIARY");
                                    if (acreed.getId() != null) {
                                        levantamiento.setPrendariaAcreedora(acreed.getName());
                                    }
                                    Person deudP = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "APPLICANT");
                                    if (deudP.getId() != null) {
                                        levantamiento.setDeudoraPrendaria(deudP.getName());
                                    }
                                    if ((levantamiento.getRegistro() == null || levantamiento.getRegistro().trim().isEmpty()) && rf.getTransactionNumber() != null && !rf.getTransactionNumber().trim().isEmpty()) {
                                        levantamiento.setRegistro(rf.getTransactionNumber());
                                    }

                                    if (levantamiento.getRegistro() != null && !levantamiento.getRegistro().trim().isEmpty()) {
                                        if (levantamiento.getDenominacion() != null && !levantamiento.getDenominacion().trim().isEmpty()) {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                if (c.existsTituloCanceladoByTituloAndExpediente(levantamiento.getRegistro(), rf.getExpedient(), false)) {
                                                    TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(levantamiento.getRegistro(), rf.getExpedient());
                                                    if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + levantamiento.getRegistro() + " CON DENOMINACIÓN "
                                                                + levantamiento.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        levantamiento = new PrendaComercial();
                                                    } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                        levantamiento.setCancelado(titca.getTipoCancelacion());
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + levantamiento.getRegistro() + " CON DENOMINACIÓN "
                                                                + levantamiento.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + levantamiento.getRegistro() + " CON DENOMINACIÓN "
                                                                + levantamiento.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        levantamiento = new PrendaComercial();
                                                    }
                                                } else {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                }
                                            } else {
                                                if (c.existsTituloCanceladoByTituloAndDenominacion(levantamiento.getRegistro(), levantamiento.getDenominacion(), false)) {
                                                    TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(levantamiento.getRegistro(), levantamiento.getDenominacion());
                                                    if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + levantamiento.getRegistro() + " CON DENOMINACIÓN "
                                                                + levantamiento.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        levantamiento = new PrendaComercial();
                                                    } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                        levantamiento.setCancelado(titca.getTipoCancelacion());
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + levantamiento.getRegistro() + " CON DENOMINACIÓN "
                                                                + levantamiento.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + levantamiento.getRegistro() + " CON DENOMINACIÓN "
                                                                + levantamiento.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        levantamiento = new PrendaComercial();
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
                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "TRÁMITE ENCONTRADO PERO NO ES UNA PRENDA COMERCIAL, SINO " + t.getName().toUpperCase());
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

    public boolean validarFechas() {
        try {
            fechaInicio.toString();
            fechaFin.toString();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void buscarLevantamientoPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            levantamientos = c.getPrendasComercialesByFechaAndType(fechaInicio, fechaFin, "DESISTIDA");
            numRegistros = "Número Registros Mostrados: " + levantamientos.size();
            if (levantamientos.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarLevantamiento(ActionEvent ae) {
        FacesMessage msg = null;
        levantamiento = (PrendaComercial) levantamientoDataTable.getRowData();
        if (levantamiento != null) {
            Controlador c = new Controlador();
            if (c.removePrendaComercial(levantamiento)) {
//                c.saveHistorial("RENOVACIÓN", "RENOVACIÓN", renovacion.getSolicitudSenadi(), "ELIMINADO", loginBean.getUsuario().getId(), loginBean.getUsuario().getLogin());
                loadLevantamientosPrenda();
                System.out.println("Levantamiento " + levantamiento.getSolicitud() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "LEVANTAMIENTO " + levantamiento.getSolicitud() + "ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR LEVANTAMIENTO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR LEVANTAMIENTO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {

        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
        levantamiento = (PrendaComercial) levantamientoDataTable.getRowData();
        if (levantamiento != null) {
            dialogTitle = "EDITAR LEVANTAMIENTO " + levantamiento.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar el Levantamiento: " + levantamiento.getSolicitud() + "?";

            Controlador c = new Controlador();
            RenewalForm rf = c.getRenewalFormsByApplicationNumber(levantamiento.getSolicitud());
            if (rf.getId() != null) {
                levantamiento.setIdRenewalForm(rf.getId());
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "LEVANTAMIENTO CARGADO.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR LEVANTAMIENTO");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararNuevo(ActionEvent ae) {
        dialogTitle = "NUEVO LEVANTAMIENTO PRENDA";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar el Nuevo Levantamiento Prenda?";
        levantamiento = new PrendaComercial();
//        Controlador c = new Controlador();
        levantamiento.setTipoEstado("LEVANTAMIENTO");
        levantamiento.setResponsable(loginBean.getUsuario().getAlias());
        edicion = false;
        if (levantamiento != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void guardarLevantamientoPrenda(ActionEvent ae) {
        FacesMessage msg = null;
        if (levantamiento != null) {
            Controlador c = new Controlador();
            if (levantamiento.getId() != null) {
                if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {

                    if (estadoTemp.equals("CERTIFICADO")) {
                        levantamiento.setTipoEstado("CERTIFICADO");
                        levantamiento.setPrendaNo(c.getNextPrendaComercialPrendaNo());
                        levantamiento.setFechaPrenda(Operaciones.cambiarFechaADiaDado(new Date(), 5));
                    } else if (estadoTemp.equals("NOTIFICADA")) {
                        levantamiento.setTipoEstado("NOTIFICADA");
                        levantamiento.setFechaNotificacion(new Date());
                        levantamiento.setNotificacion(c.getNextNumeroNotificacionPrendaComercial(new Date()));
                    } else if (estadoTemp.equals("DESISTIDAS")) {
                        levantamiento.setTipoEstado("DESISTIDA");
                    } else {
                        levantamiento.setTipoEstado("CADUCADA");
                    }

                    if (c.updatePrendaComercial(levantamiento)) {
                        c.saveHistorial(levantamiento.getTipoEstado() + "_PRENDA", "LEVANTAMIENTO_PRENDA", levantamiento.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                        loadLevantamientosPrenda();
                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "LEVANTAMIENTO_PRENDA EDITADA CON ÉXITO");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA LEVANTAMIENTO_PRENDA");
                    }
                } else {
                    //Editar Desistimiento
                    if (c.validarExistenciaPrendaComercial(levantamiento)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                    } else {
                        levantamiento.setSolicitud(levantamiento.getSolicitud().toUpperCase());
                        if (c.updatePrendaComercial(levantamiento)) {
                            c.saveHistorial("LEVANTAMIENTO_PRENDA", "LEVANTAMIENTO_PRENDA", levantamiento.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "LEVANTAMIENTO_PRENDA EDITADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR EL LEVANTAMIENTO_PRENDA");
                        }
                    }
                }
            } else {
                //Guardar Prenda Comercial
                if (c.existePrendaComercial(levantamiento.getSolicitud())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                } else {
                    boolean habilitado = true;
                    if (levantamiento.getDenominacion() != null && !levantamiento.getDenominacion().trim().isEmpty()
                            && levantamiento.getRegistro() != null && !levantamiento.getRegistro().trim().isEmpty()) {
                        if (c.existsTituloCanceladoByTituloAndDenominacion(levantamiento.getRegistro(), levantamiento.getDenominacion(), false)) {
                            TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(levantamiento.getRegistro(), levantamiento.getDenominacion());
                            if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + levantamiento.getRegistro() + " CON DENOMINACIÓN '"
                                        + levantamiento.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                levantamiento = new PrendaComercial();
                                habilitado = false;
                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                levantamiento.setCancelado(titca.getTipoCancelacion());
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + levantamiento.getRegistro() + " CON DENOMINACIÓN '"
                                        + levantamiento.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + levantamiento.getRegistro() + " CON DENOMINACIÓN '"
                                        + levantamiento.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                levantamiento = new PrendaComercial();
                                habilitado = false;
                            }
                        } else {
                            habilitado = true;
                        }
                    }
                    if (habilitado) {
                        levantamiento.setLevantamientoPrendaNo(c.getNextNumeroLevantamientoPrenda(new Date()));
                        levantamiento.setSolicitud(levantamiento.getSolicitud().toUpperCase());
                        levantamiento.setTipoEstado("LEVANTAMIENTO");
                        if (c.savePrendaComercial(levantamiento)) {
                            c.saveModificacionApp(levantamiento.getDenominacion(), levantamiento.getRegistro(), levantamiento.getSolicitud(), "PRENDA COMERCIAL", loginBean.getNombre());
                            c.saveHistorial("LEVANTAMIENTO_PRENDA", "LEVANTAMIENTO_PRENDA", levantamiento.getSolicitud(), "NUEVO LEVANTAMIENTO PRENDA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            loadLevantamientosPrenda();
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "LEVANTAMIENTO_PRENDA GUARDADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL GUARDAR EL LEVANTAMIENTO_PRENDA");
                        }
                    }

                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void downloadSelected(ActionEvent ae) {
        System.out.println("que onda");
        FacesMessage msg = null;

        if (!selectedLevantamientos.isEmpty()) {

            System.out.println("Descargando Múltiples Levantamientos_prenda...");
            String mensaje = "";

            boolean flag = false;

            for (int i = 0; i < selectedLevantamientos.size(); i++) {
                PrendaComercial aux = selectedLevantamientos.get(i);

                if (aux.getRegistro() != null && !aux.getRegistro().trim().isEmpty()) {
                    Controlador c = new Controlador();
                    if (!c.validarDelegadoActivo("delegado")) {
                        mensaje = "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN";
                        flag = false;
                        break;
                    } else if (!c.validarDelegacionActivo()) {
                        mensaje = "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN";
                        flag = false;
                        break;
                    } else if (!c.validarResolucionActiva("transferencia")) {
                        mensaje = "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN";
                        flag = false;
                        break;
                    } else {
                        if (aux.getRegistro() != null && !aux.getRegistro().trim().isEmpty()) {
                            if (aux.getFechaContrato() != null && validarFechaContrato(aux.getFechaContrato())) {
                                if (aux.getResponsable() != null && !aux.getResponsable().trim().isEmpty()) {
                                    if (aux.getCasilleroSenadi() != null && !aux.getCasilleroSenadi().trim().isEmpty() && !aux.getCasilleroSenadi().equals("null")) {
                                        if (aux.getDeudoraPrendaria() != null && !aux.getDeudoraPrendaria().trim().isEmpty() && !aux.getDeudoraPrendaria().equals("null")) {
                                            if ((aux.getCasilleroSenadiAcreedor() != null && !aux.getCasilleroSenadiAcreedor().trim().isEmpty())
                                                    || (aux.getEmail() != null && !aux.getEmail().trim().isEmpty())) {
                                                flag = true;
                                            } else {
                                                mensaje = "EL REGISTRO " + aux.getSolicitud() + " DEBE TENER UN CASILLERO ACREEDOR O UN CORREO";
                                                flag = false;
                                                break;
                                            }
                                        } else {
                                            mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE DEUDOR PRENDARIO";
                                            flag = false;
                                            break;
                                        }
                                    } else {
                                        mensaje = "EL REGISTRO " + aux.getSolicitud() + " NO TIENE CASILLERO";
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
                    }
                } else {
                    mensaje = "EL REGISTRO SELECCIONADO NO TIENE NÚMERO DE REGISTRO ASIGNADO";
                    flag = false;
                    break;
                }
            }
            if (flag) {
                System.out.println("Descargando "+selectedLevantamientos.size()+" Levantamientos_Prenda");

                loginBean.setVarious(true);
                loginBean.setPrendasComerciales(selectedLevantamientos);

                PrimeFaces.current().ajax().addCallbackParam("doit", true);

                System.out.println("envía levantamientos prenda descargar");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO LEVANTAMIENTOS PRENDAS COMERCIALES");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN REGISTRO", mensaje);
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);              
    }

    public void prepararHistorial(ActionEvent ae) {
        levantamiento = (PrendaComercial) levantamientoDataTable.getRowData();
        if (levantamiento != null) {
            dialogTitle = "SEGUIMIENTO " + levantamiento.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(levantamiento.getSolicitud());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: LEVANTAMIENTO PRENDA";
            }
        }
    }

    public void viewLevantamiento(ActionEvent ae) {
        FacesMessage msg = null;
        levantamiento = (PrendaComercial) levantamientoDataTable.getRowData();
//        boolean correcto = false;
        if (levantamiento != null) {

            if (levantamiento.getRegistro() != null && !levantamiento.getRegistro().trim().isEmpty()) {
                Controlador c = new Controlador();
                if (!c.validarDelegadoActivo("delegado")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarDelegacionActivo()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarResolucionActiva("transferencia")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else {
                    if (levantamiento.getRegistro() != null && !levantamiento.getRegistro().trim().isEmpty()) {
                        if (levantamiento.getFechaContrato() != null && validarFechaContrato(levantamiento.getFechaContrato())) {
                            if (levantamiento.getResponsable() != null && !levantamiento.getResponsable().trim().isEmpty()) {
                                if (levantamiento.getCasilleroSenadi() != null && !levantamiento.getCasilleroSenadi().trim().isEmpty() && !levantamiento.getCasilleroSenadi().equals("null")) {
                                    if (levantamiento.getDeudoraPrendaria() != null && !levantamiento.getDeudoraPrendaria().trim().isEmpty() && !levantamiento.getDeudoraPrendaria().equals("null")) {
                                        if ((levantamiento.getCasilleroSenadiAcreedor() != null && !levantamiento.getCasilleroSenadiAcreedor().trim().isEmpty())
                                                || (levantamiento.getEmail() != null && !levantamiento.getEmail().trim().isEmpty())) {
                                            System.out.println("Descargando Levantamiento_Prenda: " + levantamiento.getSolicitud());
//                                            correcto = true;
                                            loginBean.setVarious(false);
                                            loginBean.setPrendaComercial(levantamiento);

                                            PrimeFaces.current().ajax().addCallbackParam("doit", true);

                                            System.out.println("envía certificado descargar");
                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO DOCUMENTO " + levantamiento.getSolicitud());
                                        } else {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + levantamiento.getSolicitud() + " DEBE TENER UN CASILLERO ACREEDOR O UN CORREO");
                                        }
                                    } else {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + levantamiento.getSolicitud() + " NO TIENE DEUDOR PRENDARIO");
                                    }
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + levantamiento.getSolicitud() + " NO TIENE CASILLERO");
                                }
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + levantamiento.getSolicitud() + " NO TIENE RESPONSABLE ASIGNADO");
                            }
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + levantamiento.getSolicitud() + " NO TIENE UNA FECHA DE CONTRATO VÁLIDA");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL REGISTRO " + levantamiento.getSolicitud() + " NO POSEE NÚMERO DE REGISTRO");
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

    public boolean validarFechaContrato(Date fechacontrato) {
        if (fechacontrato.getYear() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (levantamiento != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + levantamiento.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(levantamiento.getIdRenewalForm(), levantamiento.getSolicitud());
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
     * @return the levantamientos
     */
    public List<PrendaComercial> getLevantamientos() {
        return levantamientos;
    }

    /**
     * @param levantamientos the levantamientos to set
     */
    public void setLevantamientos(List<PrendaComercial> levantamientos) {
        this.levantamientos = levantamientos;
    }

    /**
     * @return the levantamientosFiltradas
     */
    public List<PrendaComercial> getLevantamientosFiltradas() {
        return levantamientosFiltradas;
    }

    /**
     * @param levantamientosFiltradas the levantamientosFiltradas to set
     */
    public void setLevantamientosFiltradas(List<PrendaComercial> levantamientosFiltradas) {
        this.levantamientosFiltradas = levantamientosFiltradas;
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
     * @return the levantamientoDataTable
     */
    public UIData getLevantamientoDataTable() {
        return levantamientoDataTable;
    }

    /**
     * @param levantamientoDataTable the levantamientoDataTable to set
     */
    public void setLevantamientoDataTable(UIData levantamientoDataTable) {
        this.levantamientoDataTable = levantamientoDataTable;
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
     * @return the levantamiento
     */
    public PrendaComercial getLevantamiento() {
        return levantamiento;
    }

    /**
     * @param levantamiento the levantamiento to set
     */
    public void setLevantamiento(PrendaComercial levantamiento) {
        this.levantamiento = levantamiento;
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
     * @return the rutaNotificacionCasillero
     */
    public String getRutaNotificacionCasillero() {
        return rutaNotificacionCasillero;
    }

    /**
     * @param rutaNotificacionCasillero the rutaNotificacionCasillero to set
     */
    public void setRutaNotificacionCasillero(String rutaNotificacionCasillero) {
        this.rutaNotificacionCasillero = rutaNotificacionCasillero;
    }

    /**
     * @return the selectedLevantamientos
     */
    public List<PrendaComercial> getSelectedLevantamientos() {
        return selectedLevantamientos;
    }

    /**
     * @param selectedLevantamientos the selectedLevantamientos to set
     */
    public void setSelectedLevantamientos(List<PrendaComercial> selectedLevantamientos) {
        this.selectedLevantamientos = selectedLevantamientos;
    }
}
