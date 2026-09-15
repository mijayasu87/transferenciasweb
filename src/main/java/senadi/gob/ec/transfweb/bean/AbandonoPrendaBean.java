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
import javax.persistence.Column;
import javax.persistence.Temporal;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.component.api.UIData;
import senadi.gob.ec.transfweb.modelp.PpdiPersona;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import senadi.gob.ec.transfweb.model.Documento;
import senadi.gob.ec.transfweb.model.Historial;
import senadi.gob.ec.transfweb.model.Rooptions;
import senadi.gob.ec.transfweb.model.TituloCancelado;
import senadi.gob.ec.transfweb.model.UploadNotificacion;
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepform.ModificacionApp;
import senadi.gob.ec.transfweb.model.iepform.PaymentReceipt;
import senadi.gob.ec.transfweb.model.iepform.Person;
import senadi.gob.ec.transfweb.model.iepform.PersonRenewal;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepform.Types;
import senadi.gob.ec.transfweb.model.prenda.PrendaComercial;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;
import senadi.gob.ec.transfweb.util.Reusable;

/**
 *
 * @author micharesp
 */
@ManagedBean(name = "abandonoprendaBean")
@ViewScoped
public class AbandonoPrendaBean implements Serializable {

    private String criterio;

    private Date fechaInicio;
    private Date fechaFin;

    private Date fechaInicioCertificado;
    private Date fechaFinCertificado;

    private List<PrendaComercial> abandonos;
    private List<PrendaComercial> abandonosFiltradas;

    private UIData abandonoDataTable;

    private String dialogTitle;
    private String saveEdit;
    private String mensajeConfirmacion;
    private boolean edicion;

    private String numRegistros;

    private PrendaComercial abandono;

    private LoginBean loginBean;

    private String estadoTemp;
    private String historial;

    private String exportName;

    private List<String> roRazones;
    private String razon;

    private boolean roselectable;

    private List<PrendaComercial> selectedAbandonos;

    private List<Rooptions> roos;
    private UIData roDataTable;

    private String roChoose;

    private boolean separado;

    private String roshow;

    private List<Documento> archivos;

    private String rutaNotificacionCasillero;

    @Column(name = "numero_abandono")
    private Integer numeroAbandono;

    @Column(name = "fecha_abandono")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaAbandono;

    @Column(name = "solicitante")
    private String solicitante;

    @Column(name = "fecha_vencimiento")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaVencimiento;

    @Column(name = "tipo_abandono")
    private String tipoAbandono;

    @Column(name = "fecha_puesta_abandono")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaPuestaAbandono;

    private Integer diasProrroga;

    public AbandonoPrendaBean() {
        loadAbandonosPrendas();
    }

    private void loadAbandonosPrendas() {
        Controlador c = new Controlador();
        abandonos = c.getAbandonosPrendasComercialesByTipo("ABANDONO");
        numRegistros = "Número Registros Mostrados: " + abandonos.size();
        exportName = "abandono_prenda_" + Operaciones.formatDate(new Date());
        loginBean = c.getLogin();
        roRazones = Operaciones.getRazonesNotificar();
        razon = "";
        selectedAbandonos = new ArrayList<>();
    }

    public void buscarAbandonosPrenda(ActionEvent ae) {
        FacesMessage msg = null;
        if (criterio.trim().isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            abandonos = c.getPrendasComercialesByCriteriaAndType(criterio, "ABANDONO");
            numRegistros = "Número Registros Mostrados: " + abandonos.size();
            if (abandonos.isEmpty()) {
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

    public void validarAbandono(PrendaComercial cambion) {
        FacesMessage msg = null;
        if (cambion != null) {
            rutaNotificacionCasillero = "";
            Controlador c = new Controlador();
            List<UploadNotificacion> uploads = c.getUploadNotificacionBySolicitud(cambion.getSolicitud(), true);
            if (!uploads.isEmpty()) {
                if (uploads.size() > 1) {
                    for (int i = 0; i < uploads.size(); i++) {
                        UploadNotificacion unaux = uploads.get(i);
                        String rutaux = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                        int conf = Operaciones.validaTextoEnPdf(rutaux, "CERTIFICADO DE PRENDA COMERCIAL");
                        if (conf == 0) {
                            rutaNotificacionCasillero = rutaux;
                            break;
                        }
                    }
                    if (!rutaNotificacionCasillero.trim().isEmpty()) {
                        PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                        PrimeFaces.current().ajax().addCallbackParam("view", rutaNotificacionCasillero);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICACIÓN CARGADA");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UNA NOTIFICACIÓN SELECCIONADA");
                    }
                } else {
                    UploadNotificacion unaux = uploads.get(0);
                    rutaNotificacionCasillero = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                    System.out.println("rutanotcas: " + rutaNotificacionCasillero);
                    PrimeFaces.current().ajax().addCallbackParam("viewnotificacion", true);
                    PrimeFaces.current().ajax().addCallbackParam("view", rutaNotificacionCasillero);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NOTIFICACIÓN CARGADA");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ NINGUNA NOTIFICACIÓN DEL TRÁMITE " + cambion.getSolicitud());
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UNA NOTIFICACIÓN SELECCIONADA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
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

    public void buscarAbandonosPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas()) {
            Controlador c = new Controlador();
            abandonos = c.getPrendasComercialesByFechaAndType(fechaInicio, fechaFin, "ABANDONO");
            numRegistros = "Número Registros Mostrados: " + abandonos.size();
            if (abandonos.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarAbandonosPorFechaCertificado(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechasCertificado()) {
            Controlador c = new Controlador();
            abandonos = c.getPrendasComercialesByFechaNotificacionAndType(fechaInicioCertificado, fechaFinCertificado, "ABANDONO");
            numRegistros = "Número Registros Mostrados: " + abandonos.size();
            if (abandonos.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "NO SE ENCONTRARON RESULTADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "FECHAS INCORRECTAS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarAbandono(ActionEvent ae) {
        FacesMessage msg = null;
        abandono = (PrendaComercial) abandonoDataTable.getRowData();
        if (abandono != null) {
            Controlador c = new Controlador();
            if (c.removePrendaComercial(abandono)) {

                c.saveHistorial("ABANDONO_PRENDA", "ABANDONO_PRENDA", abandono.getSolicitud(), "ELIMINADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                loadAbandonosPrendas();
                System.out.println("Abandono " + abandono.getSolicitud() + " Eliminada");

                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANDONO " + abandono.getSolicitud() + "ELIMINADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL ELIMINAR ABANDONO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR ABANDONO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararEditar(ActionEvent ae) {
        saveEdit = "EDITAR";
        edicion = true;

        FacesMessage msg = null;
        abandono = (PrendaComercial) abandonoDataTable.getRowData();
        if (abandono != null) {
            Controlador c = new Controlador();
            abandono = c.getPrendaComercialBySolicitud(abandono.getSolicitud());
//            c.refreshPrendaComercial(abandono);

//            System.out.println("fechaaaaaaaaaA: " + abandono.getFechaPresentacion());
            roselectable = false;
            dialogTitle = "EDITAR ABANDONO PRENDA COMERCIAL " + abandono.getSolicitud();
            mensajeConfirmacion = "¿Seguro de editar la Abandono_Prenda: " + abandono.getSolicitud() + "?";
            RenewalForm rf = c.getRenewalFormsByApplicationNumber(abandono.getSolicitud());
            if (rf.getId() != null) {
                abandono.setIdRenewalForm(rf.getId());
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANDONO_PRENDA CARGADA.");
            PrimeFaces.current().ajax().addCallbackParam("peditar", true);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "PROBLEMA AL CARGAR ABANDONO_PRENDA");
            PrimeFaces.current().ajax().addCallbackParam("peditar", false);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararParaProrrogas() {
        FacesMessage msg = null;
        if (selectedAbandonos == null || selectedAbandonos.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        } else {
            diasProrroga = 10;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TRÁMITES CARGADOS");
            PrimeFaces.current().ajax().addCallbackParam("proit", true);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void paraProrrogas(ActionEvent ae) {
        FacesMessage msg = null;
        if (selectedAbandonos == null || selectedAbandonos.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "DEBE SELECCIONAR AL MENOS UN REGISTRO DE LA TABLA");
        } else if (diasProrroga == null || diasProrroga <= 0) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN NÚMERO DE DÍAS DE PRÓRROGA VÁLIDO");
        } else {
            Controlador c = new Controlador();
            int n = 0;
            for (int i = 0; i < selectedAbandonos.size(); i++) {
                PrendaComercial abaaux = selectedAbandonos.get(i);
                abaaux.setTipoEstado("PRORROGA");
                abaaux.setFechaPuestaProrroga(new Date());
                abaaux.setDiasProrroga(diasProrroga);
                abaaux.setFechaProrroga(new Date());
                if (abaaux.getNumeroProrroga() == null) {
                    abaaux.setNumeroProrroga(c.getNextNumeroProrrogaPrenda(new Date()));
                }
                if (!c.updatePrendaComercial(abaaux)) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO PASAR A PRÓRROGA EL TRÁMITE " + abaaux.getSolicitud());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    return;
                }
                c.saveHistorial("PRORROGA_PRENDA", "ABANDONO_PRENDA", abaaux.getSolicitud(), "PASADO A PRÓRROGA (" + diasProrroga + " DÍAS)", loginBean.getUsuario().getId(), loginBean.getNombre());
                n++;
            }
            if (n > 0) {
                loadAbandonosPrendas();
                PrimeFaces.current().ajax().addCallbackParam("proit", true);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "LOS TRÁMITES SELECCIONADOS PASARON A PRÓRROGA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL PASAR LOS TRÁMITES A PRÓRROGA, CONSULTE AL ADMINISTRADOR DEL SISTEMA");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararNuevo(ActionEvent ae) {
        dialogTitle = "NUEVO ABANDONO PRENDA COMERCIAL";
//        System.out.println(dialogTitle);
        saveEdit = "GUARDAR";
        mensajeConfirmacion = "¿Seguro de guardar la Nueva Abandono_Prenda?";
        abandono = new PrendaComercial();
        abandono.setTipoEstado("ABANDONO");
//        Controlador c = new Controlador();
//        abandono.setNotificacion(c.getNextNumeroNotificacionPrenda(new Date()));
        abandono.setResponsable(loginBean.getUsuario().getAlias());
        abandono.setFechaAbandono(new Date());
        edicion = false;
        roselectable = false;
        razon = "";
        if (abandono != null) {
            PrimeFaces.current().ajax().addCallbackParam("doit", true);
        }
    }

    public void guardarAbandono(ActionEvent ae) {
        FacesMessage msg = null;
        if (abandono != null) {
            Controlador c = new Controlador();
            if (abandono.getId() != null) {
                if (estadoTemp != null && !estadoTemp.trim().isEmpty()) {
                    System.out.println("TipoEstado elegido: " + estadoTemp + ", estaba en: ABANDONO_PRENDA");
                    if (estadoTemp.equals("NOTIFICADAS")) {
                        abandono.setFechaCertificado(new Date());
                        abandono.setTipoEstado("NOTIFICADA");
                        if (abandono.getNotificacion() == null) {
                            abandono.setFechaNotificacion(new Date());;
                            abandono.setNotificacion(c.getNextNumeroNotificacionPrendaComercial(abandono.getFechaNotificacion()));
                        }
                        abandono.setTipoAbandono(null);
                        abandono.setFechaAbandono(null);
                        abandono.setFechaPuestaAbandono(null);
                    }
                    if (c.updatePrendaComercial(abandono)) {
                        c.saveHistorial(abandono.getTipoEstado() + "_PRENDA", "ABANDONO_PRENDA", abandono.getSolicitud(), "PASADO A", loginBean.getUsuario().getId(), loginBean.getNombre());
                        loadAbandonosPrendas();
                        PrimeFaces.current().ajax().addCallbackParam("saved", true);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANDONO_PRENDA EDITADA CON ÉXITO");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA ABANDONO_PRENDA");
                    }

                } else {
                    //Editar Abandono
                    if (c.validarExistenciaPrendaComercial(abandono)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                    } else {

                        if (roselectable) {
                            abandono.setRo(razon);
                        }
                        abandono.setSolicitud(abandono.getSolicitud().trim().toUpperCase());
                        if (c.updatePrendaComercial(abandono)) {
                            c.saveHistorial("ABANDONO_PRENDA", "ABANDONO_PRENDA", abandono.getSolicitud(), "EDITADO", loginBean.getUsuario().getId(), loginBean.getNombre());
                            loadAbandonosPrendas();
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANDONO EDITADO CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL EDITAR LA ABANDONO");
                        }
                    }
                }

            } else {
                //Guardar Abandono_Prenda
                if (c.existePrendaComercial(abandono.getSolicitud())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "YA EXISTE UN REGISTRO CON EL MISMO NÚMERO DE SOLICITUD INGRESADO");
                } else {
                    boolean habilitado = true;
                    if (abandono.getDenominacion() != null && !abandono.getDenominacion().trim().isEmpty()
                            && abandono.getRegistro() != null && !abandono.getRegistro().trim().isEmpty()) {
                        if (c.existsTituloCanceladoByTituloAndDenominacion(abandono.getRegistro(), abandono.getDenominacion(), false)) {
                            TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(abandono.getRegistro(), abandono.getDenominacion());
                            if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN '"
                                        + abandono.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                abandono = new PrendaComercial();
                                habilitado = false;
                            } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                abandono.setCancelado(titca.getTipoCancelacion());
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN '"
                                        + abandono.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN '"
                                        + abandono.getDenominacion() + "' SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                abandono = new PrendaComercial();
                                habilitado = false;
                            }
                        } else {
                            habilitado = true;
                        }
                    }
                    if (habilitado) {
                        abandono.setNumeroAbandono(c.getNextNumeroAbandonoPrenda(new Date()));
                        abandono.setSolicitud(abandono.getSolicitud().toUpperCase());
                        abandono.setTipoEstado("ABANDONO");
                        if (c.savePrendaComercial(abandono)) {
                            c.saveModificacionApp(abandono.getDenominacion(), abandono.getRegistro(), abandono.getSolicitud(), "PRENDA COMERCIAL", loginBean.getNombre());
                            c.saveHistorial("ABANDONO_PRENDA", "ABANDONO_PRENDA", abandono.getSolicitud(), "NUEVA", loginBean.getUsuario().getId(), loginBean.getNombre());
                            loadAbandonosPrendas();
                            PrimeFaces.current().ajax().addCallbackParam("saved", true);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANDONO GUARDADA CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "INFORMACIÓN", "HUBO UN PROBLEMA AL GUARDAR LA ABANDONO");
                        }
                    }

                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /* Da la orden de visualizar el reporte, clase Informe (Webservlet)*/
    public void viewAbandonoSi(ActionEvent ae) {
        FacesMessage msg = null;
//        abandono = (Notificacion) abandonoDataTable.getRowData();
//        System.out.println("Algo tiene que hacer aquí");
        if (abandono != null) {

            Controlador c = new Controlador();

            if (!c.validarDelegadoActivo("delegado")) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else if (!c.validarDelegacionActivo()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else if (!c.validarResolucionActiva("transferencia")) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else if (!c.validarDelegadoActivo("secretaria")) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA SECRETARIA ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else {
                if (abandono.getNotificacion() != null && abandono.getNotificacion() != 0) {
                    if (!c.getRosBySolicitud(abandono.getSolicitud()).isEmpty()) {
                        abandono.setFechaNotificacion(new Date());
                        c.updatePrendaComercial(abandono);

                        System.out.println("Descargando Abandono_prenda: " + abandono.getSolicitud());

                        loginBean.setVarious(false);
                        loginBean.setPrendaComercial(abandono);

                        PrimeFaces.current().ajax().addCallbackParam("doit", true);

                        System.out.println("envía abandono_prenda descargar");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO ABANDONO_PRENDA " + abandono.getSolicitud());
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL REGISTRO DEBE TENER UN MOTIVO DE ABANDONO");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL REGISTRO DEBE TENER NÚMERO DE ABANDONO");
                }

            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "VALIDE QUE LOS DATOS DE LA ABANDONO_PRENDA SEAN CORRECTOS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /* Da la orden de visualizar el reporte, clase Informe (Webservlet)*/
    public void viewAbandonoNo(ActionEvent ae) {
        FacesMessage msg = null;
        if (abandono != null) {
            Controlador c = new Controlador();
            if (!c.validarDelegadoActivo("delegado")) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else if (!c.validarDelegacionActivo()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else if (!c.validarResolucionActiva("transferencia")) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else if (!c.validarDelegadoActivo("secretaria")) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA SECRETARIA ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
            } else {
                if (abandono.getNotificacion() != null && abandono.getNotificacion() != 0) {
                    if (!c.getRosBySolicitud(abandono.getSolicitud()).isEmpty()) {
                        System.out.println("Descargando Abandono_prenda: " + abandono.getSolicitud());

                        loginBean.setVarious(false);
                        loginBean.setPrendaComercial(abandono);

                        PrimeFaces.current().ajax().addCallbackParam("doit", true);

                        System.out.println("envía abandono_prenda descargar");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO ABANDONO " + abandono.getSolicitud());
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL REGISTRO DEBE TENER UN MOTIVO DE ABANDONO");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL REGISTRO DEBE TENER NÚMERO DE ABANDONO");
                }

            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "VALIDE QUE LOS DATOS DE LA ABANDONO_PRENDA SEAN CORRECTOS");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararHistorial(ActionEvent ae) {
        abandono = (PrendaComercial) abandonoDataTable.getRowData();
        if (abandono != null) {
            dialogTitle = "SEGUIMIENTO " + abandono.getSolicitud();
            Controlador c = new Controlador();
            List<Historial> hists = c.getHistorialBySolicitudSenadi(abandono.getSolicitud());
            setHistorial("");
            for (int i = 0; i < hists.size(); i++) {
                historial += hists.get(i).toString() + "\n";
            }

            if (historial.trim().isEmpty()) {
                historial = "Estado actual: ABANDONO_PRENDA";
            }
        }
    }

    public void buscarTramite(ActionEvent ae) {
        FacesMessage msg = null;

//        System.out.println("LLegando por aquí");
        if (abandono != null && abandono.getSolicitud() != null && !abandono.getSolicitud().trim().isEmpty()) {
//            System.out.println(transferencia.getSolicitud());
            String tramite = abandono.getSolicitud();
            Controlador c = new Controlador();
            if (c.existePrendaComercial(tramite)) {
                PrendaComercial aux = c.getPrendaComercialBySolicitud(tramite);
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
                                    if (t.getName().trim().toLowerCase().contains("prenda comercial")) {

                                        PaymentReceipt payment = c.getPaymentReceiptById(rf.getPaymentReceiptId());

                                        abandono.setComprobante(payment.getVoucherNumber());
                                        abandono.setFechaPresentacion(rf.getApplicationDate());
                                        //abandono.setCertificado(c.getNextCambioDomicilioCertificado());
                                        abandono.setSigno(ttp.getAlias());
                                        abandono.setCasilleroSenadi(c.getCasilleroSenadiByOwnerId(rf.getOwnerId()) + "");

                                        List<PersonRenewal> solicitantes = c.getPersonRenewalByIdRenewalAndType(rf.getId(), "'APPLICANT'");
                                        if (!solicitantes.isEmpty()) {
                                            abandono.setSolicitante(solicitantes.get(0).getName());
                                        }

                                        abandono.setIdRenewalForm(rf.getId());
                                        if (rf.getDebugId() != null && rf.getDebugId() != 0) {
//                                    System.out.println("debug_id: "+rf.getDebugId());
                                            HallmarkForms hf = c.getHallmarkFormDepurada(rf.getDebugId());

                                            if (hf.getId() != null) {
                                                abandono.setDenominacion(hf.getDenomination());
                                                abandono.setRegistro(hf.getExpedient());

                                                if (abandono.getRegistro() != null && !abandono.getRegistro().trim().isEmpty()) {
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(abandono.getRegistro(), abandono.getDenominacion());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        abandono.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        abandono.setTitularAnterior(titulo.getTitular());
                                                    }
                                                }
                                                if (abandono.getFechaRegistro() == null) {
                                                    abandono.setFechaRegistro(new Date(Integer.parseInt(hf.getExpYear()) - 1900, 0, 2));
                                                }
                                            }
                                        } else {
                                            if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {

                                                PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(rf.getExpedient());
                                                if (ps.getCodigoSolicitudSigno() != null) {
                                                    abandono.setDenominacion(ps.getDenominacionSigno());
                                                    PpdiTituloSignoDistintivo titulo = c.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(ps.getCodigoSolicitudSigno());
                                                    if (titulo.getCodigoSolicitudSigno() != null) {
                                                        abandono.setRegistro(titulo.getNumeroTitulo());
                                                        abandono.setFechaRegistro(titulo.getFechaEmisionDocumento());
                                                        abandono.setTitularAnterior(titulo.getTitular());
                                                    }

                                                    if (abandono.getTitularAnterior() == null || abandono.getTitularAnterior().trim().isEmpty()) {
                                                        PpdiPersona persona = c.getASolicitanteByCodigoSolicitud(ps.getCodigoSolicitudSigno());
                                                        if (persona.getCodigoPersona() != null) {
                                                            abandono.setTitularAnterior(persona.getNombrePersona());
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        Person titAct = c.getTitularActual(rf.getId());
                                        if (titAct.getId() != null) {
                                            abandono.setTitularActual(titAct.getName());
                                            abandono.setDomicilioTitularActual(titAct.getAddress());
                                            abandono.setIdentificacion(titAct.getIdentificationNumber());
                                        }

                                        Person apoder = c.getFirstPersonRenewalTypeByIdRenewal(rf.getId(), "AGENT");
                                        if (apoder.getId() != null) {
                                            abandono.setAbogadoPatrocinador(c.getNamesPersonRenewalTextTypeByIdRenewal(rf.getId(), "AGENT"));
                                        }

                                        if (abandono.getRegistro() != null && !abandono.getRegistro().trim().isEmpty()) {
                                            if (abandono.getDenominacion() != null && !abandono.getDenominacion().trim().isEmpty()) {
                                                if (rf.getExpedient() != null && !rf.getExpedient().trim().isEmpty()) {
                                                    if (c.existsTituloCanceladoByTituloAndExpediente(abandono.getRegistro(), rf.getExpedient(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndExpediente(abandono.getRegistro(), rf.getExpedient());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN "
                                                                    + abandono.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            abandono = new PrendaComercial();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            abandono.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN "
                                                                    + abandono.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN "
                                                                    + abandono.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            abandono = new PrendaComercial();
                                                        }
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DATOS CARGADOS CORRECTAMENTE");
                                                    }
                                                } else {
                                                    if (c.existsTituloCanceladoByTituloAndDenominacion(abandono.getRegistro(), abandono.getDenominacion(), false)) {
                                                        TituloCancelado titca = c.getTituloCanceladoByTituloAndDenoninacion(abandono.getRegistro(), abandono.getDenominacion());
                                                        if (titca.getId() != null && titca.getTipoCancelacion().contains("TOTAL")) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN "
                                                                    + abandono.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            abandono = new PrendaComercial();
                                                        } else if (titca.getId() != null && titca.getTipoCancelacion().contains("PARCIAL")) {
                                                            abandono.setCancelado(titca.getTipoCancelacion());
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN "
                                                                    + abandono.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO " + abandono.getRegistro() + " CON DENOMINACIÓN "
                                                                    + abandono.getDenominacion() + " SE ENCUENTRA CANCELADO DE MANERA " + titca.getTipoCancelacion() + "; CONSULTE EN EL LISTADO DE TÍTULOS CANCELADOS");
                                                            abandono = new PrendaComercial();
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
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN TRÁMITE VÁLIDO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void validaSeleccion() {
        if (roselectable) {
            roChoose = "";
            razon = "";
        } else {
            razon = "";
            roChoose = "";
        }
    }

    public void downloadSelected(ActionEvent ae) {
        FacesMessage msg = null;
        if (!selectedAbandonos.isEmpty()) {

            Controlador c = new Controlador();
            boolean band = true;
            String msj = "";
            for (int i = 0; i < selectedAbandonos.size(); i++) {
                PrendaComercial abandonoaux = selectedAbandonos.get(i);
                if (abandonoaux != null && abandonoaux.getId() != null) {
                    if (abandonoaux.getTipoAbandono() != null && !abandonoaux.getTipoAbandono().trim().isEmpty()) {
                        if (abandonoaux.getSolicitante() != null && !abandonoaux.getSolicitante().trim().isEmpty()) {
                            if (abandonoaux.getRegistro() != null && !abandonoaux.getRegistro().trim().isEmpty()) {
                                if (abandonoaux.getRo() != null && !abandonoaux.getRo().trim().isEmpty()) {
                                    if (Operaciones.validarFecha(abandonoaux.getFechaRegistro())) {
                                        List<Rooptions> roosaux = c.getRosBySolicitud(abandonoaux.getSolicitud());
                                        if (roosaux.isEmpty()) {
                                            band = false;
                                            msj = "DEBE INGRESAR UN MOTIVO DE NOTIFICACIÓN " + abandonoaux.getSolicitud();
                                            break;
                                        }
                                    } else {
                                        band = false;
                                        msj = "EL ABANDONO " + abandonoaux.getSolicitud() + " NO POSEE FECHA DE REGISTRO";
                                        break;

                                    }
                                } else {
                                    band = false;
                                    msj = "EL ABANDONO " + abandonoaux.getSolicitud() + " NO POSEE RO";
                                    break;
                                }

                            } else {
                                band = false;
                                msj = "EL ABANDONO " + abandonoaux.getSolicitud() + " NO POSEE NÚMERO DE REGISTRO";
                                break;
                            }
                        } else {
                            band = false;
                            msj = "DEBE INGRESAR UN SOLICITANTE VÁLIDO PARA EL TRÁMITE " + abandonoaux.getSolicitud();
                            break;
                        }
                    } else {
                        band = false;
                        msj = "LA SOLICITUD " + abandonoaux.getSolicitud() + " NO POSEE TIPO DE ABANDONO";
                        break;
                    }
                } else {
                    band = false;
                    msj = "NO SE CARGÓ CORRECTAMENTE LA NOTIFICACIÓN";
                    break;
                }

            }
            if (band) {
                System.out.println("Descargando Múltiples Abandonos_prenda...");
                loginBean.setPrendasComerciales(selectedAbandonos);
                loginBean.setVarious(true);
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "DESCARGA", "DESCARGANDO REGISTROS SELECCIONADOS");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN REGISTRO", msj);
            }

        } else {
            System.out.println("Sin selección...");
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN SELECCIÓN", "SELECCIONE AL MENOS UN REGISTRO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararShowRo(ActionEvent ae) {
        FacesMessage msg = null;
        if (abandono != null && abandono.getSolicitud() != null && !abandono.getSolicitud().trim().isEmpty()) {
            Controlador c = new Controlador();
            roos = c.getRosBySolicitud(abandono.getSolicitud());
            roshow = "";
            for (int i = 0; i < roos.size(); i++) {
                roshow += roos.get(i).getRo();
            }
            PrimeFaces.current().ajax().addCallbackParam("viewro", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "MOTIVO DE ABANDONO " + abandono.getSolicitud());
        } else {
            PrimeFaces.current().ajax().addCallbackParam("viewro", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SIN TRÁMITE", "INGRESE UN NÚMERO DE TRÁMITE CORRECTO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararViewRo(ActionEvent ae) {
        abandono = (PrendaComercial) abandonoDataTable.getRowData();
        if (abandono != null) {
            Controlador c = new Controlador();
            roos = c.getRosBySolicitud(abandono.getSolicitud());
            roshow = "";
            for (int i = 0; i < roos.size(); i++) {
                roshow += roos.get(i).getRo();
            }
            PrimeFaces.current().ajax().addCallbackParam("viewro", true);
        } else {
            PrimeFaces.current().ajax().addCallbackParam("viewro", false);
        }
    }

    public void agregarRo(ActionEvent ae) {
        FacesMessage msg = null;
        if (abandono != null) {

            if (roselectable && razon != null && !razon.trim().isEmpty()) {
                Rooptions ro = new Rooptions();
                ro.setRo(razon);
                ro.setFecha(new Date());
                ro.setSolicitud(abandono.getSolicitud());
                ro.setTipo("PRENDA COMERCIAL");
                Controlador c = new Controlador();
                if (c.saveRooptios(ro)) {
                    roos = c.getRosBySolicitud(abandono.getSolicitud());
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "RO GUARDADO");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GUARDAR EL RO");
                }
            } else {
                if (roChoose != null && !roChoose.trim().isEmpty()) {
                    Rooptions ro = new Rooptions();
                    ro.setRo(roChoose);
                    ro.setFecha(new Date());
                    ro.setSolicitud(abandono.getSolicitud());
                    ro.setTipo("PRENDA COMERCIAL");
                    Controlador c = new Controlador();
                    if (c.saveRooptios(ro)) {
                        roos = c.getRosBySolicitud(abandono.getSolicitud());
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "RO GUARDADO");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GUARDAR EL RO");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO GUARDAR EL RO");
                }
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ LA ABANDONO CORRECTAMENTE");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void eliminarRo(ActionEvent ae) {
        FacesMessage msg = null;
        if (abandono != null) {
            Rooptions roo = (Rooptions) roDataTable.getRowData();
            if (roo != null) {
                Controlador c = new Controlador();
                if (c.removeRooptios(roo)) {
                    roos = c.getRosBySolicitud(abandono.getSolicitud());
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "RO ELIMINADO CORRECTAMENTE");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO ELIMINAR EL RO");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE EL RO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE EL RO");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void postProcessXLS(Object document) {
        XSSFWorkbook book = (XSSFWorkbook) document;
        XSSFSheet sheet = book.getSheetAt(0);
        Controlador c = new Controlador();

        XSSFRow rowt = sheet.getRow(2);

        XSSFCell cr1 = rowt.createCell(14);
        cr1.setCellValue("Ro1");

        XSSFCell cr2 = rowt.createCell(15);
        cr2.setCellValue("Ro2");

        XSSFCell cr3 = rowt.createCell(16);
        cr3.setCellValue("Ro3");

        XSSFCell cr4 = rowt.createCell(17);
        cr4.setCellValue("Ro4");

        int lastCol = 13;
        for (int i = 0; i < abandonos.size(); i++) {
            List<Rooptions> roops = c.getRosBySolicitud(abandonos.get(i).getSolicitud());
            for (int j = 0; j < roops.size(); j++) {
                Rooptions roo = roops.get(j);
                if (roo != null) {
                    XSSFRow row = sheet.getRow(i + 3);
                    lastCol++;
                    XSSFCell rc = row.createCell(lastCol);
                    rc.setCellValue(roo.getRo());
                }
            }
            lastCol = 13;
        }
    }

    public void prepararDescarga(ActionEvent ae) {
        FacesMessage msg = null;
        abandono = (PrendaComercial) abandonoDataTable.getRowData();
        if (abandono != null && abandono.getId() != null) {
            if (abandono.getTipoAbandono() != null && !abandono.getTipoAbandono().trim().isEmpty()) {
                if (abandono.getSolicitante() != null && !abandono.getSolicitante().trim().isEmpty()) {
                    if (abandono.getRegistro() != null && !abandono.getRegistro().trim().isEmpty()) {
                        if (Operaciones.validarFecha(abandono.getFechaRegistro())) {
                            Controlador c = new Controlador();
                            List<Rooptions> roosaux = c.getRosBySolicitud(abandono.getSolicitud());
                            if (roosaux.isEmpty()) {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "DEBE INGRESAR UN MOTIVO DE NOTIFICACIÓN");
                            } else {
                                loginBean.setPrendaComercial(abandono);
                                loginBean.setVarious(false);
                                System.out.println("envía abandono descargar");
                                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "ABANDONO PREPARADO PARA DESCARGA");
                            }
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL ABANDONO " + abandono.getSolicitud() + " NO POSEE FECHA DE REGISTRO");
                        }

                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL ABANDONO " + abandono.getSolicitud() + " NO POSEE NÚMERO DE REGISTRO");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "DEBE INGRESAR UN SOLICITANTE VÁLIDO PARA EL TRÁMITE " + abandono.getSolicitud());
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "LA SOLICITUD " + abandono.getSolicitud() + " NO POSEE TIPO DE ABANDONO");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE LA NOTIFICACIÓN");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onTipoAbandonoSelectedListener() {
        System.out.println(estadoTemp);
        if (estadoTemp != null && estadoTemp.equals("NOTIFICADAS")) {
            saveEdit = "ENVIAR";
        } else {
            saveEdit = "EDITAR";
        }
    }

    public void buscarCasillero(ActionEvent ae) {
        if (abandono != null && abandono.getId() != null) {
            Controlador c = new Controlador();
            abandono.setCasilleroSenadi(c.buscarCasilleroBySolicitud(abandono.getSolicitud()));
        }
    }

    public void prepararExpediente(ActionEvent ae) {
        FacesMessage msg = null;
        if (abandono != null) {
            dialogTitle = "EXPEDIENTE - TRÁMITE " + abandono.getSolicitud();
            Reusable reusable = new Reusable();
            archivos = reusable.getRutasDeExpedienteRenewal(abandono.getIdRenewalForm(), abandono.getSolicitud());
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
     * @return the abandonos
     */
    public List<PrendaComercial> getAbandonos() {
        return abandonos;
    }

    /**
     * @param abandonos the abandonos to set
     */
    public void setAbandonos(List<PrendaComercial> abandonos) {
        this.abandonos = abandonos;
    }

    /**
     * @return the abandonosFiltradas
     */
    public List<PrendaComercial> getAbandonosFiltradas() {
        return abandonosFiltradas;
    }

    /**
     * @param abandonosFiltradas the abandonosFiltradas to set
     */
    public void setAbandonosFiltradas(List<PrendaComercial> abandonosFiltradas) {
        this.abandonosFiltradas = abandonosFiltradas;
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
     * @return the abandonoDataTable
     */
    public UIData getAbandonoDataTable() {
        return abandonoDataTable;
    }

    /**
     * @param abandonoDataTable the abandonoDataTable to set
     */
    public void setAbandonoDataTable(UIData abandonoDataTable) {
        this.abandonoDataTable = abandonoDataTable;
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
     * @return the abandono
     */
    public PrendaComercial getAbandono() {
        return abandono;
    }

    /**
     * @param abandono the abandono to set
     */
    public void setAbandono(PrendaComercial abandono) {
        this.abandono = abandono;
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
     * @return the roRazones
     */
    public List<String> getRoRazones() {
        return roRazones;
    }

    /**
     * @param roRazones the roRazones to set
     */
    public void setRoRazones(List<String> roRazones) {
        this.roRazones = roRazones;
    }

    /**
     * @return the razon
     */
    public String getRazon() {
        return razon;
    }

    /**
     * @param razon the razon to set
     */
    public void setRazon(String razon) {
        this.razon = razon;
    }

    /**
     * @return the roselectable
     */
    public boolean isRoselectable() {
        return roselectable;
    }

    /**
     * @param roselectable the roselectable to set
     */
    public void setRoselectable(boolean roselectable) {
        this.roselectable = roselectable;
    }

    /**
     * @return the selectedAbandonos
     */
    public List<PrendaComercial> getSelectedAbandonos() {
        return selectedAbandonos;
    }

    /**
     * @param selectedAbandonos the selectedAbandonos to set
     */
    public void setSelectedAbandonos(List<PrendaComercial> selectedAbandonos) {
        this.selectedAbandonos = selectedAbandonos;
    }

    /**
     * @return the roos
     */
    public List<Rooptions> getRoos() {
        return roos;
    }

    /**
     * @param roos the roos to set
     */
    public void setRoos(List<Rooptions> roos) {
        this.roos = roos;
    }

    /**
     * @return the roDataTable
     */
    public UIData getRoDataTable() {
        return roDataTable;
    }

    /**
     * @param roDataTable the roDataTable to set
     */
    public void setRoDataTable(UIData roDataTable) {
        this.roDataTable = roDataTable;
    }

    /**
     * @return the roChoose
     */
    public String getRoChoose() {
        return roChoose;
    }

    /**
     * @param roChoose the roChoose to set
     */
    public void setRoChoose(String roChoose) {
        this.roChoose = roChoose;
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
     * @return the roshow
     */
    public String getRoshow() {
        return roshow;
    }

    /**
     * @param roshow the roshow to set
     */
    public void setRoshow(String roshow) {
        this.roshow = roshow;
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
     * @return the numeroAbandono
     */
    public Integer getNumeroAbandono() {
        return numeroAbandono;
    }

    /**
     * @param numeroAbandono the numeroAbandono to set
     */
    public void setNumeroAbandono(Integer numeroAbandono) {
        this.numeroAbandono = numeroAbandono;
    }

    /**
     * @return the fechaAbandono
     */
    public Date getFechaAbandono() {
        return fechaAbandono;
    }

    /**
     * @param fechaAbandono the fechaAbandono to set
     */
    public void setFechaAbandono(Date fechaAbandono) {
        this.fechaAbandono = fechaAbandono;
    }

    /**
     * @return the solicitante
     */
    public String getSolicitante() {
        return solicitante;
    }

    /**
     * @param solicitante the solicitante to set
     */
    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    /**
     * @return the fechaVencimiento
     */
    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    /**
     * @param fechaVencimiento the fechaVencimiento to set
     */
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /**
     * @return the tipoAbandono
     */
    public String getTipoAbandono() {
        return tipoAbandono;
    }

    /**
     * @param tipoAbandono the tipoAbandono to set
     */
    public void setTipoAbandono(String tipoAbandono) {
        this.tipoAbandono = tipoAbandono;
    }

    /**
     * @return the fechaPuestaAbandono
     */
    public Date getFechaPuestaAbandono() {
        return fechaPuestaAbandono;
    }

    /**
     * @param fechaPuestaAbandono the fechaPuestaAbandono to set
     */
    public void setFechaPuestaAbandono(Date fechaPuestaAbandono) {
        this.fechaPuestaAbandono = fechaPuestaAbandono;
    }

    /**
     * @return the diasProrroga
     */
    public Integer getDiasProrroga() {
        return diasProrroga;
    }

    /**
     * @param diasProrroga the diasProrroga to set
     */
    public void setDiasProrroga(Integer diasProrroga) {
        this.diasProrroga = diasProrroga;
    }
}
