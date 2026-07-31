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
import senadi.gob.ec.transfweb.model.Documento;
import senadi.gob.ec.transfweb.modelp.PpdiPersona;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import senadi.gob.ec.transfweb.model.Historial;
import senadi.gob.ec.transfweb.model.TituloCancelado;
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
 * @author Michael
 */
@ManagedBean(name = "caducadacdBean")
@ViewScoped
public class CaducadaCDBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private List<CambioDomicilio> caducadas;
    private List<CambioDomicilio> caducadasFiltradas;

    private UIData caducadaDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private String mensajeCancelado;
    private boolean edicion;

    private String numRegistros;

    private CambioDomicilio caducada;
    private String historial;

    private String exportName;

    private LoginBean loginBean;

    private List<Documento> archivos;

    private List<CambioDomicilio> selectedCaducadas;

    public CaducadaCDBean() {
        loadCaducadasCD();

    }

    private void loadCaducadasCD() {
        Controlador c = new Controlador();
        caducadas = c.getCambiosDomicilioByTipo("CADUCADA");
        numRegistros = "Número Registros Mostrados: " + caducadas.size();
        exportName = "caducada_cd_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
    }

    public void buscarCaducada(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            caducadas = c.getCambiosDomicilioByCriteriaAndType(criterio.trim(), "CADUCADA");
            numRegistros = "Número Registros Mostrados: " + caducadas.size();
            if (caducadas.isEmpty()) {
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

    public void buscarCaducadasPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            caducadas = c.getCambiosDomicilioByFechaAndType(fechaInicio, fechaFin, "CADUCADA");
            numRegistros = "Número Registros Mostrados: " + caducadas.size();
            if (caducadas.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarCaducada(ActionEvent ae) {
        FacesMessage msg = null;
        caducada = (CambioDomicilio) caducadaDataTable.getRowData();
        if (caducada != null) {
            Controlador c = new Controlador();
            if (c.removeCambioDomicilio(caducada)) {
                c.saveHistorial("CADUCADA_CD", "CADUCADA_CD", caducada.getSolicitud(), "ELIMINADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                loadCaducadasCD();
                System.out.println("Caducada_cd " + caducada.getSolicitud() + " Eliminada");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CADUCADA_CD " + caducada.getSolicitud() + "ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR CADUCADA_CD");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR CADUCADA_CD");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {

        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
        caducada = (CambioDomicilio) caducadaDataTable.getRowData();
        if (caducada != null) {
            dialogTitle = "EDITAR CADUCADA_CD " + caducada.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar la Caducada: " + caducada.getSolicitud() + "?";
            Controlador c = new Controlador();
            RenewalForm rf = c.getRenewalFormsByApplicationNumber(caducada.getSolicitud());
            if (rf.getId() != null) {
                caducada.setIdRenewalForm(rf.getId());
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CADUCADA CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR CADUCADA");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararNuevo(ActionEvent ae) {
        dialogTitle = "NUEVO CADUCADA-NEGADO CAMBIO DE DOMICILIO";
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar el Nuevo Caducada-Negado?";
        caducada = new CambioDomicilio();
        caducada.setTipoEstado("CADUCADA");
        caducada.setResponsable(loginBean.getUsuario().getAlias());
        edicion = false;
        if (caducada != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void guardarCaducada(ActionEvent ae) {
        FacesMessage msg = null;
        if (caducada != null) {
            Controlador c = new Controlador();
            if (caducada.getId() != null) {
                //Editar Caducada
                if (c.validarExistenciaCambioDomicilio(caducada)) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                } else {
                    caducada.setSolicitud(caducada.getSolicitud().toUpperCase());
                    if (c.updateCambioDomicilio(caducada)) {
                        c.saveHistorial("CADUCADA_CD", "CADUCADA_CD", caducada.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CADUCADA_CD EDITADA CON ÉXITO");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR EL CADUCADA_CD");
                    }
                }
            } else {
                //Guardar Caducada
                if (c.existeCambioDomicilio(caducada.getSolicitud())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                } else {

                    boolean habilitado = true;
                    if (caducada.getDenominacion() != null && !caducada.getDenominacion().trim().isEmpty()
                            && caducada.getRegistro() != null && !caducada.getRegistro().trim().isEmpty()) {
                        if (c.existsTituloCanceladoByTituloAndDenominacion(caducada.getRegistro(), caducada.getDenominacion(), false)) {
                            TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(caducada.getRegistro(), caducada.getDenominacion());
                            // En CADUCADAS-NEGADAS se permite registrar aunque el título esté cancelado
                            caducada.setCancelado(titca.getTipoCancelacion());
                        } else {
                            habilitado = true;
                        }
                    }
                    if (habilitado) {
                        caducada.setTipoEstado("CADUCADA");
                        caducada.setSolicitud(caducada.getSolicitud().toUpperCase());
                        if (c.saveCambioDomicilio(caducada)) {
                            c.saveModificacionApp(caducada.getDenominacion(), caducada.getRegistro(), caducada.getSolicitud(), "CAMBIO DE DOMICILIO", loginBean.getNombre());
                            c.saveHistorial("CADUCADA_CD", "CADUCADA_CD", caducada.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            loadCaducadasCD();
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CADUCADA_CD GUARDADO CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL GUARDAR EL CADUCADA_CD");
                        }
                    }

                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (caducada != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + caducada.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(caducada.getIdRenewalForm(), caducada.getSolicitud());
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

    public void prepararHistorial(ActionEvent ae) {
        caducada = (CambioDomicilio) caducadaDataTable.getRowData();
        if (caducada != null) {
            dialogTitle = "SEGUIMIENTO " + caducada.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(caducada.getSolicitud());
            historial = "";
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: CADUCADA - NEGADA - CD";
            }
        }
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;

        if (caducada != null && caducada.getSolicitud() != null && !caducada.getSolicitud().trim().isEmpty()) {
//            System.out.println(transferencia.getSolicitud());
            String tramite = caducada.getSolicitud();
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

                                        caducada.setComprobante(payment.getVoucherNumber());
                                        caducada.setFechaPresentacion(rf.getApplicationDate());
//                                desistimiento.setCertificado(c.getNextNumeroCertificadoTransferencia() + "");
                                        caducada.setSigno(ttp.getAlias());
                                        caducada.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");

                                        caducada.setIdRenewalForm(rf.getId());
                                        if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                            HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                            if (hf.getId() != null) {
                                                caducada.setDenominacion(hf.getDenomination());
                                                caducada.setRegistro(hf.getExpedient());

                                                if (caducada.getRegistro() != null && !caducada.getRegistro().trim().isEmpty()) {
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(caducada.getRegistro(), caducada.getDenominacion());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        caducada.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        caducada.setTitularAnterior(titulo.getTitular());
                                                    }
                                                }
                                                if (caducada.getFechaRegistro() == null) {
                                                    caducada.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                }
                                            }
                                        } else {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                if (ps.getCodigoSolicitudSigno() != null) {
                                                    caducada.setDenominacion(ps.getDenominacionSigno());
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        caducada.setRegistro(titulo.getNumeroTitulo());
                                                        caducada.setFechaRegistro(titulo.getFechaEmisionDocumento());
//                                                certificado.setTitularAnterior(titulo.getTitular());
//                                                certificado.setTitularActual(titulo.getTitular());
//                                                certificado.setDomicilioTitularActual(titulo.get);
                                                    }
                                                }
                                                if (caducada.getTitularActual() == null || caducada.getTitularActual().trim().isEmpty()) {
                                                    PpdiPersona persona = c.getASolicitanteByCodigoSolicitud(ps.getCodigoSolicitudSigno());
                                                    if (persona.getCodigoPersona() != null) {
                                                        caducada.setTitularActual(persona.getNombrePersona());
                                                        caducada.setDomicilioTitularActual(persona.getDireccionPersona());
                                                    }
                                                }
                                            }
                                        }

                                        Person lawyer = c.getPersonRenewalByType(rf.getId(), "LAWYER");
                                        if (lawyer.getId() != null) {
                                            caducada.setNomApodRepre(lawyer.getName());
                                        }

                                        if (caducada.getTitularActual() == null || caducada.getTitularActual().trim().isEmpty()) {
                                            Person titular = c.getPersonRenewalByType(rf.getId(), "APPLICANT");
                                            if (titular.getId() != null) {
                                                caducada.setTitularActual(titular.getName());
                                                caducada.setDomicilioTitularActual(titular.getAddress());
                                            }
                                        }

                                        Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "AGENT");
                                        if (apoder.getId() != null) {
                                            caducada.setApeApodRepre(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "AGENT"));
                                        }

                                        if (caducada.getRegistro() != null && !caducada.getRegistro().trim().isEmpty()) {
                                            if (caducada.getDenominacion() != null && !caducada.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(caducada.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(caducada.getRegistro(), rf.getExpedient());
                                                        caducada.setCancelado(titca.getTipoCancelacion());
                                                        mensajeCancelado = "EL TÍTULO " + caducada.getRegistro() + " ESTÁ CANCELADO DE MANERA " + titca.getTipoCancelacion() + ". ¿DESEA REGISTRARLO DE TODAS MANERAS?";
                                                        PrimeFaces.current().ajax().addCallbackParam("cancelado", true);
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + caducada.getRegistro() + " ESTÁ CANCELADO; CONFIRME SI DESEA REGISTRARLO DE TODAS MANERAS");
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(caducada.getRegistro(), caducada.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(caducada.getRegistro(), caducada.getDenominacion());
                                                        caducada.setCancelado(titca.getTipoCancelacion());
                                                        mensajeCancelado = "EL TÍTULO " + caducada.getRegistro() + " ESTÁ CANCELADO DE MANERA " + titca.getTipoCancelacion() + ". ¿DESEA REGISTRARLO DE TODAS MANERAS?";
                                                        PrimeFaces.current().ajax().addCallbackParam("cancelado", true);
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + caducada.getRegistro() + " ESTÁ CANCELADO; CONFIRME SI DESEA REGISTRARLO DE TODAS MANERAS");
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

    public void downloadSelected(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedCaducadas.isEmpty()) {

            System.out.println("Descargando Múltiples Caducadas Cd... " + selectedCaducadas.size());

            boolean flag = true;
            Controlador c = new Controlador();
            String msj = "";
            for (int i = 0; i < selectedCaducadas.size(); i++) {
                CambioDomicilio caducadaaux = selectedCaducadas.get(i);
                if (caducadaaux.getRegistro() != null && !caducadaaux.getRegistro().trim().isEmpty()) {

                    if (caducadaaux.getSolicitante() == null || caducadaaux.getSolicitante().trim().isEmpty()) {
                        flag = false;
                        msj = "NO HAY UN SOLICITANTE VÁLIDO PARA EL TRÁMITE " + caducadaaux.getSolicitud();
                        break;
                    }
                    if (!c.validarDelegadoActivo("delegado")) {
                        flag = false;
                        msj = "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN";
                        break;
                    }
                    if (!c.validarDelegacionActivo()) {
                        flag = false;
                        msj = "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN";
                        break;
                    }
                    if (!c.validarResolucionActiva("transferencia")) {
                        flag = false;
                        msj = "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN";
                        break;
                    }
                    if (caducadaaux.getObservacion() == null || caducadaaux.getObservacion().trim().isEmpty()) {
                        flag = false;
                        msj = "NO ESTÁ INGRESADO EL MOTIVO ESPECÍFICO (Observación) " + caducadaaux.getSolicitud();
                        break;
                    }
                    if (caducadaaux.getResolucionCaducada() == null || caducadaaux.getResolucionCaducada() == 0) {
                        flag = false;
                        msj = "NO EXISTE EL NÚMERO DE RESOLUCIÓN " + caducadaaux.getSolicitud();
                        break;
                    }
                } else {
                    flag = false;
                    msj = "NO HAY UN REGISTRO VÁLIDO PARA EL TRÁMITE " + caducadaaux.getSolicitud();
                    break;
                }
            }
            if (flag) {
                loginBean.setCambiosDomicilio(selectedCaducadas);
                loginBean.setVarious(true);
                loginBean.setTipoTramite(2);//2: para cambios domicilio
                System.out.println("envía caducadas cd seleccionados a descargar");
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CADUCADAS CARGADOS PARA DESCARGA, ESPERE...");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", msj);
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN SELECCIÓN", "SELECCIONE AL MENOS UN REGISTRO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void viewCaducada() {
        System.out.println("llego por aquí");
        FacesMessage msg = null;
        caducada = (CambioDomicilio) caducadaDataTable.getRowData();
        boolean correcto = false;
        if (caducada != null) {

            if (caducada.getRegistro() != null && !caducada.getRegistro().trim().isEmpty()) {
                System.out.println("Descargando Caducada: " + caducada.getSolicitud());

                Controlador c = new Controlador();
                if (caducada.getSolicitante() == null || caducada.getSolicitante().trim().isEmpty()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO HAY UN SOLICITANTE VÁLIDO");
                } else if (!c.validarDelegadoActivo("delegado")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarDelegacionActivo()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarResolucionActiva("transferencia")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "getObservacion", "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (caducada.getObservacion() == null || caducada.getObservacion().trim().isEmpty()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO ESTÁ INGRESADO EL MOTIVO ESPECÍFICO (Observación)");
                } else if (caducada.getResolucionCaducada() == null || caducada.getResolucionCaducada() == 0) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO EXISTE EL NÚMERO DE RESOLUCIÓN");
                } else {
                    correcto = true;
                    loginBean.setCambioDomicilio(caducada);
                    loginBean.setVarious(false);
                    loginBean.setTipoTramite(2);//2: para cambios domicilio
                    PrimeFaces.current().ajax().addCallbackParam("doit", true);

                    System.out.println("envía caducada cambio domicilio descargar");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO DOCUMENTO " + caducada.getSolicitud());
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "EL REGISTRO SELECCIONADO NO TIENE NÚMERO DE REGISTRO ASIGNADO");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR LOS DATOS DEL REGISTRO SELECCIONADO");
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
     * @return the caducadas
     */
    public List<CambioDomicilio> getCaducadas() {
        return caducadas;
    }

    /**
     * @param caducadas the caducadas to set
     */
    public void setCaducadas(List<CambioDomicilio> caducadas) {
        this.caducadas = caducadas;
    }

    /**
     * @return the caducadasFiltradas
     */
    public List<CambioDomicilio> getCaducadasFiltradas() {
        return caducadasFiltradas;
    }

    /**
     * @param caducadasFiltradas the caducadasFiltradas to set
     */
    public void setCaducadasFiltradas(List<CambioDomicilio> caducadasFiltradas) {
        this.caducadasFiltradas = caducadasFiltradas;
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
     * @return the caducadaDataTable
     */
    public UIData getCaducadaDataTable() {
        return caducadaDataTable;
    }

    /**
     * @param caducadaDataTable the caducadaDataTable to set
     */
    public void setCaducadaDataTable(UIData caducadaDataTable) {
        this.caducadaDataTable = caducadaDataTable;
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

    public String getMensajeCancelado() {
        return mensajeCancelado;
    }

    public void setMensajeCancelado(String mensajeCancelado) {
        this.mensajeCancelado = mensajeCancelado;
    }

    public void descartarTramite() {
        caducada = new CambioDomicilio();
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
     * @return the caducada
     */
    public CambioDomicilio getCaducada() {
        return caducada;
    }

    /**
     * @param caducada the caducada to set
     */
    public void setCaducada(CambioDomicilio caducada) {
        this.caducada = caducada;
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
     * @return the selectedCaducadas
     */
    public List<CambioDomicilio> getSelectedCaducadas() {
        return selectedCaducadas;
    }

    /**
     * @param selectedCaducadas the selectedCaducadas to set
     */
    public void setSelectedCaducadas(List<CambioDomicilio> selectedCaducadas) {
        this.selectedCaducadas = selectedCaducadas;
    }

}
