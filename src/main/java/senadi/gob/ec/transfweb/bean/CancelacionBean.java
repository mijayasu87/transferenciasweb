/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.bean;

import com.jcraft.jsch.JSchException;
import java.io.IOException;
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
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.file.UploadedFile;
import senadi.gob.ec.transfweb.model.TituloCancelado;
import senadi.gob.ec.transfweb.model.iepiadm.Cpis;
import senadi.gob.ec.transfweb.modelp.PpdiResolucion;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.modelp.PpdiTitAndSigno;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import senadi.gob.ec.transfweb.renova.model.CaducadaRen;
import senadi.gob.ec.transfweb.renova.model.Desistida;
import senadi.gob.ec.transfweb.renova.model.Notificada;
import senadi.gob.ec.transfweb.renova.model.Renovacion;
import senadi.gob.ec.transfweb.util.Codekru;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.FTPFiles;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author micharesp
 */
@ManagedBean(name = "cancelacionBean")
@ViewScoped
public class CancelacionBean implements Serializable {

    private String numtitulo;
    private List<PpdiTitAndSigno> titulos;
    private List<PpdiTitAndSigno> titulosFiltrados;
    private PpdiTitAndSigno titulo;
    private UIData tituloDataTable;
    private String numRegistros;

    private boolean busqueda;

    private List<TituloCancelado> titulosCancelados;
    private List<TituloCancelado> titulosCanceladosFiltrados;
    private UIData tituloCanceladoDataTable;
    private String denominacion;

    private boolean prodvig;
    private boolean tramocdi;

    private TituloCancelado tituloCancelado;

    private UploadedFile file;

    private String rutadocscpis;

    private boolean tramantiguo;

    private String etiquetaTitulo;
    private String ejemploTitRes;

    private List<Cpis> cpis;
    private List<Cpis> cpisFiltrados;
    private UIData cpiDataTable;

    private String denominacionRegistro;

    private List<PpdiTitAndSigno> signosSinTitulo;
    private List<PpdiTitAndSigno> signosSinTituloFiltrados;
    private UIData signoSinTituloDataTable;
    private PpdiTitAndSigno signoSinTitulo;

    private String titDenoText;

    private boolean renuncia;
    private String labeltramocdi;
    private String labeladjuntar;

    private String labelResol;
    private String labelFechaR;

    private boolean tiporenuncia;

    public CancelacionBean() {

        loadTitulosCancelados();
    }

    private void loadTitulosCancelados() {
        busqueda = false;
        Controlador c = new Controlador();
        titulos = new ArrayList<>();
        titulosCanceladosFiltrados = new ArrayList<>();
        titulosCancelados = c.getTitulosCanceladosByReverso(false);
        prodvig = false;
        tramocdi = false;
        file = null;
        numtitulo = "";
        rutadocscpis = "http://administracion.propiedadintelectual.gob.ec/servicemanager/media/files/cpis/";
        tramantiguo = false;
        etiquetaTitulo = "Título: ";
    }

    public void buscarTitulo(ActionEvent ae) {
        FacesMessage msg = null;
//        if (numtitulo != null && !numtitulo.trim().isEmpty()) {
//            Controlador c = new Controlador();
//            titulos = c.getTitulosAndSignos(numtitulo);
        titulos = new ArrayList<>();
        titDenoText = "";
        renuncia = false;
        labeltramocdi = "Número Trámite OCDI:";
        labeladjuntar = "Adjuntar Documento Resolución:";
        labelResol = "Resolución OCDI:";
        labelFechaR = "Fecha de Resolución OCDI:";
//            busqueda = false;
//            if (titulos.isEmpty()) {
//                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS DEL TÍTULO " + numtitulo);
//                PrimeFaces.current().ajax().addCallbackParam("doit", false);
//            } else {
//                signosSinTitulo = new ArrayList<>();
//                numRegistros = titulos.size() + " Registros.";
        PrimeFaces.current().ajax().addCallbackParam("doit", true);
        //msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CONSULTA REALIZADA");
        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DIÁLOGO DE BÚSQUEDA DE TÍTULOS");
//            }
//        } else {
//            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN NÚMERO DE TÍTULO CORRECTO");
//        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void limpiarBusquedaTitulo(ActionEvent ae) {
        titulos = new ArrayList<>();
        titDenoText = "";
        numRegistros = "0 Registros.";
        titulosFiltrados = new ArrayList<>();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "HECHO");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void limpiarBusquedaSinTi(ActionEvent ae) {
        signosSinTitulo = new ArrayList<>();
        denominacionRegistro = "";
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "HECHO");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarTitulos(ActionEvent ae) {
        FacesMessage msg = null;
        if (titDenoText != null && !titDenoText.trim().isEmpty()) {
            Controlador c = new Controlador();
            titulos = c.getPpdiTituloAndSignoByTituloOrDenominacion(titDenoText);
            busqueda = false;
            if (titulos.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS");
                PrimeFaces.current().ajax().addCallbackParam("doit", false);
            } else {
//                System.out.println("Si llegamos: " + titulos.size());
                signosSinTitulo = new ArrayList<>();
                numRegistros = titulos.size() + " Registros.";
                PrimeFaces.current().ajax().addCallbackParam("doit", true);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CONSULTA REALIZADA");
            }
        } else {
            PrimeFaces.current().ajax().addCallbackParam("doit", false);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CONSULTA REALIZADA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void seleccionarCpi(ActionEvent ae) {
        FacesMessage msg = null;
        if (tituloCancelado != null) {
            Cpis cpiaux = (Cpis) cpiDataTable.getRowData();
            if (cpiaux.getCurrentProcedure() != null) {
                if (Operaciones.validarFecha(cpiaux.getResolutionDate())) {
                    if (tituloCancelado.getDenominacion().toLowerCase().trim().equals(cpiaux.getDenomination().toLowerCase().trim())) {
                        tituloCancelado.setNumeroTramiteOCDI(cpiaux.getCurrentProcedure());
                        tituloCancelado.setResolucion(cpiaux.getResolutionNumber());
                        tituloCancelado.setFechaResolucion(cpiaux.getResolutionDate());
                        tituloCancelado.setDenominationOCDI(cpiaux.getDenomination());
                        if (cpiaux.isDocument()) {
                            tituloCancelado.setDocumento(cpiaux.getDocumentPath());
                            tramocdi = true;
                        } else {
                            tramocdi = false;
                        }

                        PrimeFaces.current().ajax().addCallbackParam("selectdo", true);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CPI SELECCIONADO");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "DENOMINACIÓN", "LA DENOMINACIÓN INGRESADA '" + tituloCancelado.getDenominacion() + "', ES DISTINTA A LA DENOMINACIÓN DE LA RESOLUCIÓN SELECCIONADA");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "RESOLUCIÓN OCDI", "EL REGISTRO SELECCIONADO NO POSEE FECHA DE RESOLUCIÓN");
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL SELECCIONAR EL TRÁMITE OCDI.");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL SELECCIONAR EL TRÁMITE OCDI");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void seleccionarTitulo(ActionEvent ae) {
        FacesMessage msg = null;
        titulo = (PpdiTitAndSigno) tituloDataTable.getRowData();
        if (titulo.getCodigoSolicitudSigno() != null) {

            Controlador c = new Controlador();
            if (!c.existsTituloCanceladoByTituloAndDenominacion(titulo.getNumTitulo(), titulo.getDenominacion(), false)) {
                tituloCancelado = new TituloCancelado();
                tituloCancelado.setNumeroTitulo(titulo.getNumTitulo());
                tituloCancelado.setDenominacion(titulo.getDenominacion());
                tituloCancelado.setExpediente(titulo.getNumeroExpediente());
                tituloCancelado.setFechaExpediente(titulo.getFechaExpediente());
                tituloCancelado.setNumeroTramite(titulo.getNumTramite());

                busqueda = true;
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TÍTULO CON EXPEDIENTE" + titulo.getNumeroExpediente() + " SELECCIONADO CORRECTAMENTE");
            } else {
//                loadTitulosCancelados();
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL TÍTULO SELECCIONADO, YA SE ENCUENTRA CANCELADO");
            }

        } else {
            busqueda = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL SELECCIONAR EL TÍTULO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

//    public void buscarTramiteOCDI(ActionEvent ae) throws JSchException, IllegalAccessException, IOException {
//        FacesMessage msg = null;
//        if (tituloCancelado != null && tituloCancelado.getNumeroTramiteOCDI() != null && !tituloCancelado.getNumeroTramiteOCDI().trim().isEmpty()) {
//
//            Controlador c = new Controlador();
//            cpis = c.buscarCpisByTramiteOCDI(tituloCancelado.getNumeroTramiteOCDI().trim());
//            if (cpis.isEmpty()) {
//                tramocdi = false;
//                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRARON RESULTADOS PARA " + tituloCancelado.getNumeroTramiteOCDI());
//            } else {
//
//                int i = 0;
//                System.out.println("tamaño cpis: " + cpis.size());
//                while (cpis.get(i).getResolutionDate() == null) {
//                    i++;
//                }
//                Cpis cpi = cpis.get(i);
//                System.out.println(tituloCancelado.getDenominacion().toLowerCase().trim() + " : " + cpi.getDenomination().toLowerCase().trim());
//                tituloCancelado.setDenominacion(tituloCancelado.getDenominacion().trim());
//
//                cpi.setDenomination(cpi.getDenomination().trim());
//                if (tituloCancelado.getDenominacion().toLowerCase().trim().equals(cpi.getDenomination().toLowerCase().trim())) {
//                    String complemento = (cpi.getResolutionDate().getYear() + 1900) + "/" + Operaciones.getStringMes(cpi.getResolutionDate().getMonth() + 1) + "/";
//                    String rutaservfile = "/var/www/html/servicemanager/media/files/cpis/" + complemento;
//
//                    FTPFiles archs = new FTPFiles(131);
//                    System.out.println("---> " + rutaservfile + cpi.getWrittenFile());
//                    if (archs.validateFileExists(rutaservfile + cpi.getWrittenFile()) || archs.validateFileExists(rutaservfile + cpi.getWrittenFile().toUpperCase())) {
//                        tramocdi = true;
//                        tituloCancelado.setDocumento(rutadocscpis + complemento + cpi.getWrittenFile());
//                    } else {
//                        tramocdi = false;
//                    }
//
//                    tituloCancelado.setResolucion(cpi.getResolutionNumber());
//                    tituloCancelado.setFechaResolucion(cpi.getResolutionDate());
//                    tituloCancelado.setDenominationOCDI(cpi.getDenomination());
//
//                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
//                } else {
//                    System.out.println("----------> 5");
//                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "LA DENOMINACIÓN DEL TÍTULO INGRESADO, ES DIFERENTE A LA DE LA RESOLUCIÓN DEL OCDI: " + tituloCancelado.getDenominacion() + " : " + cpi.getDenomination());
//                }
//            }
//        } else {
//            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ EL TRÁMITE " + titulo.getNumTramite());
//        }
//        FacesContext.getCurrentInstance().addMessage(null, msg);
//    }
    public void ejecutarAccion(ActionEvent ae) throws IOException, JSchException, IllegalAccessException {
        FacesMessage msg = null;       
        if (tramantiguo) {
            if (tituloCancelado != null && tituloCancelado.getTipoCancelacion() != null && !tituloCancelado.getTipoCancelacion().trim().isEmpty()) {
                System.out.println("tipo: " + tituloCancelado.getTipoCancelacion());
                if (tituloCancelado.getTipoCancelacion().contains("PARCIAL")) {
                    if (tituloCancelado.getProductosVigentes() == null || tituloCancelado.getProductosVigentes().trim().isEmpty()) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "ESPECIFIQUE LOS PRODUCTOS QUE QUEDAN VIGENTES");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        return;
                    }
                }
                Controlador c = new Controlador();
                if (c.tituloCanceladoExists(tituloCancelado, false)) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO INGRESADO YA SE ENCUENTRA CANCELADO");
                } else {
                    if (renuncia) {
                        tituloCancelado.setTipo("VOLUNTARIO");
                        tituloCancelado.setFechaResolucion(new Date());
                    } else {
                        tituloCancelado.setTipo("NORMAL");
                    }
                    if (tramocdi) {
//                        if (tituloCancelado.getDenominacion().trim().toLowerCase().equals(tituloCancelado.getDenominationOCDI().trim().toLowerCase())) {
                        if (cancelarTitulo()) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "EL REGISTRO " + tituloCancelado.getDenominacion() + " HA SIDO CANCELADO CON ÉXITO");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "HUBO UN PROBLEMA AL CANCELAR EL REGISTRO CON DENOMINACIÓN" + tituloCancelado.getDenominacion());
                        }
//                        } else {
//                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "LA DENOMINACIÓN DEL TÍTULO INGRESADO, ES DIFERENTE A LA DE LA RESOLUCIÓN DEL OCDI");
//                        }
                    } else {
                        if (file != null) {
                            System.out.println("archivo: " + file.getFileName());
                            if (file.getInputStream() != null) {
                                if (uploadResolucionToRepositorio(file)) {
//                                    tituloCancelado.setDocumento(numtitulo);
                                    if (cancelarTitulo()) {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "EL REGISTRO " + tituloCancelado.getDenominacion() + " HA SIDO CANCELADO CON ÉXITO");
                                    } else {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "HUBO UN PROBLEMA AL CANCELAR EL REGISTRO CON DENOMINACIÓN" + tituloCancelado.getDenominacion());
                                    }
                                }
                            }
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "ADJUNTE UN DOCUMENTO CORRECTO");
                        }
                    }
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "SELECCIONE EL TIPO DE CANCELACIÓN");
            }
        } else {
            if (titulo != null && titulo.getCodigoSolicitudSigno() != null) {
                if (tituloCancelado != null && tituloCancelado.getTipoCancelacion() != null && !tituloCancelado.getTipoCancelacion().trim().isEmpty()) {

                    System.out.println("tipo: " + tituloCancelado.getTipoCancelacion());
                    if (tituloCancelado.getTipoCancelacion().contains("PARCIAL")) {
                        if (tituloCancelado.getProductosVigentes() == null || tituloCancelado.getProductosVigentes().trim().isEmpty()) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "ESPECIFIQUE LOS PRODUCTOS QUE QUEDAN VIGENTES");
                            FacesContext.getCurrentInstance().addMessage(null, msg);
                            return;
                        }
                    }
                    Controlador c = new Controlador();
                    if (c.tituloCanceladoExists(tituloCancelado, false)) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "EL TÍTULO INGRESADO YA SE ENCUENTRA CANCELADO");
                    } else {                        
                        if (renuncia) {
                            tituloCancelado.setTipo("VOLUNTARIO");
//                            tituloCancelado.setFechaResolucion(new Date());
                        } else {
                            tituloCancelado.setTipo("NORMAL");
                        }
                        if (tramocdi) {
                            if (tituloCancelado.getDenominacion().trim().toLowerCase().equals(tituloCancelado.getDenominationOCDI().trim().toLowerCase())) {
                                if (cancelarTitulo()) {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "EL TÍTULO " + titulo.getNumTitulo() + " CON EXPEDIENTE " + titulo.getNumeroExpediente() + " HA SIDO CANCELADO CON ÉXITO");
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "HUBO UN PROBLEMA AL CANCELAR EL TÍTULO " + titulo.getNumTitulo());
                                }
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "LA DENOMINACIÓN DEL TÍTULO INGRESADO, ES DIFERENTE A LA DE LA RESOLUCIÓN DEL OCDI");
                            }
                        } else {
                            if (renuncia) {
//                                tituloCancelado.setFechaResolucion(new Date());
                            }
                            if (file != null) {
                                System.out.println("archivo: " + file.getFileName());
                                if (file.getInputStream() != null) {
                                    if (uploadResolucionToRepositorio(file)) {
//                                    tituloCancelado.setDocumento(numtitulo);
                                        if (cancelarTitulo()) {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "EL TÍTULO " + titulo.getNumTitulo() + " CON EXPEDIENTE " + titulo.getNumeroExpediente() + " HA SIDO CANCELADO CON ÉXITO");
                                        } else {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CANCELAR EL TÍTULO " + titulo.getNumTitulo());
                                        }
                                    }else{
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HAY UN PROBLEMA EN LA CARGA DEL DOCUMENTO");
                                    }
                                }else{
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HAY UN PROBLEMA CON EL DOCUMENTO");
                                }
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "ADJUNTE UN DOCUMENTO CORRECTO");
                            }
                        }
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR LOS DATOS DEL TÍTULO");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR EL TÍTULO");
            }
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public boolean uploadResolucionToRepositorio(UploadedFile documento) throws IOException, JSchException, IllegalAccessException {
        if (documento != null) {
            String year = (tituloCancelado.getFechaResolucion().getYear() + 1900) + "";
            String ruta = "/var/www/html/servicemanager/media/files/cpis/" + year;
            FTPFiles arch = new FTPFiles(131);
            if (!arch.validateFolderExists(ruta)) {
                if(!arch.exeComando("mkdir " + ruta)){
                    System.out.println("Existe un problema al guardar la nueva carpeta "+year);
                    return false;
                }
            }
            String month = Operaciones.getStringMes(tituloCancelado.getFechaResolucion().getMonth() + 1);
            ruta = ruta + "/" + month;            
            if (!arch.validateFolderExists(ruta)) {
                if(!arch.exeComando("mkdir " + ruta)){
                    System.out.println("Hubo un error al crear carpeta "+ruta);
                    return false;
                }
            }
            ruta = ruta + "/" + documento.getFileName();
            Codekru cod = new Codekru(131);
            cod.copyAInputStreamToRemoteMachine(documento.getInputStream(), ruta);
            tituloCancelado.setDocumento(rutadocscpis + year + "/" + month + "/" + documento.getFileName());
            return true;
        } else {
            return false;
        }
    }

    public void tramAntiguaAction() {
        if (tramantiguo) {
            busqueda = true;
            etiquetaTitulo = "Resolución del Registro de Marca: ";
            ejemploTitRes = "Ej. SENADI_RS_OCDI_2023_123, OCDI-2023-123";
            tituloCancelado = new TituloCancelado();
            titulo = new PpdiTitAndSigno();

        } else {
            etiquetaTitulo = "Título: ";
            ejemploTitRes = "Ej. SENADI_2023_TI_123, 1234";
            tituloCancelado = new TituloCancelado();
            loadTitulosCancelados();
        }
        labeltramocdi = "Número Trámite OCDI:";
        labeladjuntar = "Adjuntar Documento Resolución:";
        labelResol = "Resolución OCDI:";
        labelFechaR = "Fecha de Resolución OCDI:";

        renuncia = false;
    }

    public void seleccionarTramSinTit(ActionEvent ae) {
        FacesMessage msg = null;
        signoSinTitulo = (PpdiTitAndSigno) signoSinTituloDataTable.getRowData();
        if (signoSinTitulo != null && signoSinTitulo.getCodigoSolicitudSigno() != null) {
            PrimeFaces.current().ajax().addCallbackParam("tramsitido", true);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "ERROR", "REGISTRO SELECCIONADO CORRECTAMENTE");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR EL REGISTRO SELECCIONADO");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public boolean cancelarTitulo() {
        if (cancelarTitulo(tituloCancelado.getTipoCancelacion())) {
            loadTitulosCancelados();
            return true;
        } else {
            return false;
        }
    }

    public boolean cancelarTitulo(String tipoCancelado) {
        Controlador c = new Controlador();
        tituloCancelado.setFechaCancelacion(new Date());
        tituloCancelado.setUsuario(c.getLogin().getNombre());
        tituloCancelado.setReverso(false);

        System.out.println("----------Título " + tipoCancelado + " cancelado-----------");
        if (tituloCancelado != null && tituloCancelado.getId() == null) {
            if (c.saveTituloCancelado(tituloCancelado)) {

                List<Renovacion> rens = c.getRenovacionesByTituloAndDenominacion(tituloCancelado.getNumeroTitulo().trim(), tituloCancelado.getDenominacion().toLowerCase().trim());
                List<Notificada> nots = c.getNotificadasByTituloAndDenominacion(tituloCancelado.getNumeroTitulo().trim(), tituloCancelado.getDenominacion());
                List<Desistida> desis = c.getDesistidasByTituloAndDenominacion(tituloCancelado.getNumeroTitulo().trim(), tituloCancelado.getDenominacion());
                List<CaducadaRen> cads = c.getCaducadasRByTituloAndDenominacion(tituloCancelado.getNumeroTitulo().trim(), tituloCancelado.getDenominacion());
                for (int i = 0; i < rens.size(); i++) {
                    Renovacion renova = rens.get(i);
                    renova.setCancelado(tipoCancelado);
                    c.updateRenovacion(renova);
                    System.out.println("CANCELADO " + tipoCancelado + ": " + renova.toString());
                }
                for (int i = 0; i < nots.size(); i++) {
                    Notificada noti = nots.get(i);
                    noti.setCancelado(tipoCancelado);
                    c.updateNotificadaR(noti);
                    System.out.println("CANCELADO " + tipoCancelado + ": " + noti.toString());
                }

                for (int i = 0; i < desis.size(); i++) {
                    Desistida desist = desis.get(i);
                    desist.setCancelado(tipoCancelado);
                    c.updateDesistidaR(desist);
                    System.out.println("CANCELADO " + tipoCancelado + ": " + desist.toString());
                }

                for (int i = 0; i < cads.size(); i++) {
                    CaducadaRen cad = cads.get(i);
                    cad.setCancelado(tipoCancelado);
                    c.updateCaducadaR(cad);
                    System.out.println("CANCELADO " + tipoCancelado + ": " + cad.toString());
                }

                System.out.println("Titulo cancelado " + tipoCancelado.toLowerCase() + "mente: " + tituloCancelado.toString() + "\n");
                busqueda = false;
                loadTitulosCancelados();
                return true;

            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void prepararVerDenominacion(ActionEvent ae) {
        FacesMessage msg = null;
        if (tramantiguo) {
            if (tituloCancelado.getDenominacion() != null && !tituloCancelado.getDenominacion().trim().isEmpty()) {
                denominacion = tituloCancelado.getDenominacion();
                System.out.println("denominacion: " + denominacion);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DENOMINACIÓN CARGADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR LA DENOMINACIÓN DE LA RESOLUCIÓN " + titulo.getNumTitulo());
            }
        } else {
            if (titulo != null && !titulo.getDenominacion().trim().isEmpty()) {
                denominacion = titulo.getDenominacion();
                System.out.println("denominacion: " + denominacion);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DENOMINACIÓN CARGADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR LA DENOMINACIÓN DEL TÍTULO " + titulo.getNumTitulo());
            }
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void prepararVerDocumentosOCDI(ActionEvent ae) throws JSchException, IllegalAccessException, IOException {
        FacesMessage msg = null;
        if (tituloCancelado != null && tituloCancelado.getNumeroTramiteOCDI() != null && !tituloCancelado.getNumeroTramiteOCDI().trim().isEmpty()) {
            if (tituloCancelado.getDenominacion() != null && !tituloCancelado.getDenominacion().trim().isEmpty()) {
                Controlador c = new Controlador();
                cpis = c.buscarCpisByTramiteOCDI(tituloCancelado.getNumeroTramiteOCDI().trim());
                if (cpis.isEmpty()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS PARA " + tituloCancelado.getNumeroTramiteOCDI());
                } else {

                    for (int i = 0; i < cpis.size(); i++) {
                        Cpis aux = cpis.get(i);
                        if (Operaciones.validarFecha(aux.getResolutionDate()) && aux.getWrittenFile() != null && aux.getWrittenFile().contains(".pdf")) {
                            String complemento = (aux.getResolutionDate().getYear() + 1900) + "/" + Operaciones.getStringMes(aux.getResolutionDate().getMonth() + 1) + "/";
                            String rutaservfile = "/var/www/html/servicemanager/media/files/cpis/" + complemento;

                            FTPFiles archs = new FTPFiles(131);
                            System.out.println("---> " + rutaservfile + aux.getWrittenFile());
                            if (archs.validateFileExists(rutaservfile + aux.getWrittenFile()) || archs.validateFileExists(rutaservfile + aux.getWrittenFile().toUpperCase())) {
                                aux.setDocumentPath(rutadocscpis + complemento + aux.getWrittenFile());
                                aux.setDocument(true);
                            } else {
                                aux.setDocument(false);
                            }
                        } else {
                            aux.setDocument(false);
                        }
                    }

                    PrimeFaces.current().ajax().addCallbackParam("viewdococdi", true);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO HAY UNA DENOMINACIÓN CARGADA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN NÚMERO TRÁMITE OCDI CORRECTO");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowEditCpi(RowEditEvent<Cpis> event) {
        FacesMessage msg = null;
        Cpis cp = event.getObject();
        if (cp != null && cp.getCurrentProcedure() != null) {
            Controlador c = new Controlador();
            if (c.updateCpi(cp)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "EDITADO", "DENOMINACIÓN EDITADA CORRECTAMENTE");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HAY UN PROBLEMA, NO SE EDITÓ LA DENOMINACIÓN");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO EDITAR LA DENOMINACIÓN");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancelCpi(RowEditEvent<Cpis> event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CANCELADO", "PROCESO CANCELADO");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void editarDenominacionTitulo() {
        FacesMessage msg = null;
        if (tramantiguo) {
            if (tituloCancelado.getNumeroTitulo() != null && !tituloCancelado.getNumeroTitulo().trim().isEmpty()) {
                if (denominacion != null && !denominacion.trim().isEmpty()) {

                    Controlador c = new Controlador();

                    PpdiResolucion pr = c.getPpdiResolucionByResolutionNumber(tituloCancelado.getNumeroTitulo());
                    PpdiSolicitudSignoDistintivo psd = c.getPpdiSolicitudSignoDistintivoByCodigoSolicitud(pr.getCodigoSolicitud());
                    if (psd.getCodigoSolicitudSigno() != null) {
                        System.out.println("1: " + psd.getDenominacionSigno() + ", 2: " + denominacion);
                        if (psd.getDenominacionSigno().equals(denominacion)) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISA", "NO HA REALIZADO NINGÚN CAMBIO EN LA DENOMINACIÓN");
                        } else {
                            psd.setDenominacionSigno(denominacion);
                            tituloCancelado.setDenominacion(psd.getDenominacionSigno());

                            if (c.updatePpdiSolicitudSignoDistintivo(psd)) {
                                PrimeFaces.current().ajax().addCallbackParam("dendoit", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DENOMINACIÓN DE LA RESOLUCIÓN " + tituloCancelado.getNumeroTitulo() + " EDITADO CORRECTAMENTE");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO EDITAR LA DENOMINACIÓN DE LA RESOLUCIÓN " + tituloCancelado.getNumeroTitulo());
                            }
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ EL TRÁMITE " + titulo.getNumTramite());
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL EDITAR LA DENOMINACIÓN DEL TÍTULO " + titulo.getNumTitulo());
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL EDITAR LA DENOMINACIÓN DEL TÍTULO " + titulo.getNumTitulo());
            }
        } else {
            if (titulo != null && titulo.getCodigoSolicitudSigno() != null) {
                if (denominacion != null && !denominacion.trim().isEmpty()) {

                    Controlador c = new Controlador();
                    PpdiSolicitudSignoDistintivo psd = c.getPpdiSolicitudSignoDistintivoByCodigoSolicitud(titulo.getCodigoSolicitudSigno());
                    if (psd.getCodigoSolicitudSigno() != null) {
                        System.out.println("1: " + psd.getDenominacionSigno() + ", 2: " + denominacion);
                        if (psd.getDenominacionSigno().equals(denominacion)) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISA", "NO HA REALIZADO NINGÚN CAMBIO EN LA DENOMINACIÓN");
                        } else {
                            psd.setDenominacionSigno(denominacion);
                            titulo.setDenominacion(denominacion);
                            tituloCancelado.setDenominacion(titulo.getDenominacion());

                            if (c.updatePpdiSolicitudSignoDistintivo(psd)) {
                                PrimeFaces.current().ajax().addCallbackParam("dendoit", true);
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DENOMINACIÓN DEL TÍTULO " + titulo.getNumTitulo() + " EDITADO CORRECTAMENTE");
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO EDITAR LA DENOMINACIÓN DEL TRÁMITE " + titulo.getNumTramite());
                            }
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ EL TRÁMITE " + titulo.getNumTramite());
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL EDITAR LA DENOMINACIÓN DEL TÍTULO " + titulo.getNumTitulo());
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL EDITAR LA DENOMINACIÓN DEL TÍTULO " + titulo.getNumTitulo());
            }
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void enableProductosV() {
        if (tituloCancelado.getTipoCancelacion().contains("PARCIAL")) {
            System.out.println("haber que vaina: " + tituloCancelado.getTipoCancelacion());
            prodvig = true;
        } else {
            prodvig = false;
        }
    }

    public void buscarResolucionSenadi(ActionEvent ae) {
        FacesMessage msg = null;
        if (tituloCancelado != null && tituloCancelado.getNumeroTitulo() != null && !tituloCancelado.getNumeroTitulo().trim().isEmpty()) {
            Controlador c = new Controlador();
            tituloCancelado.setNumeroTitulo(tituloCancelado.getNumeroTitulo().toUpperCase());
            String aux = tituloCancelado.getNumeroTitulo();
            PpdiResolucion resol = c.getPpdiResolucionByResolutionNumber(tituloCancelado.getNumeroTitulo().trim());
            boolean bandera = false;

            if (resol.getCodigoResolucion() != null) {
                bandera = true;
            } else {
                if (tituloCancelado.getNumeroTitulo().trim().contains("OCDI-")) {
                    tituloCancelado.setNumeroTitulo("SENADI_RS_" + tituloCancelado.getNumeroTitulo().trim().replace("-", "_"));
                    resol = c.getPpdiResolucionByResolutionNumber(tituloCancelado.getNumeroTitulo());
                    if (resol.getCodigoResolucion() != null) {
                        bandera = true;
                    } else {
                        tituloCancelado.setNumeroTitulo(tituloCancelado.getNumeroTitulo().replace("SENADI", "IEPI"));
                        resol = c.getPpdiResolucionByResolutionNumber(tituloCancelado.getNumeroTitulo());
                        if (resol.getCodigoResolucion() != null) {
                            bandera = true;
                        } else {
                            tituloCancelado.setNumeroTitulo(aux);
                            bandera = false;
                        }
                    }
                }
            }

            if (bandera) {
                PpdiSolicitudSignoDistintivo signo = c.getPpdiSolicitudSignoDistintivoByCodigoSolicitud(resol.getCodigoSolicitud());

                if (signo.getCodigoSolicitudSigno() != null) {
                    tituloCancelado.setManual(true);
                    tituloCancelado.setNumeroTitulo(tituloCancelado.getNumeroTitulo().toUpperCase().trim());
                    tituloCancelado.setDenominacion(signo.getDenominacionSigno());
                    tituloCancelado.setExpediente(signo.getNumeroExpediente());
                    tituloCancelado.setFechaExpediente(signo.getFechaExpediente());
                    tituloCancelado.setNumeroTramite(signo.getNumeroTramite());
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "RESOLUCIÓN CARGADA");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ LA MARCA ASOCIADA A LA RESOLUCIÓN INGRESADA");
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ LA RESOLUCIÓN " + aux);
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR LOS DATOS DE LA RESOLUCIÓN");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void renunciaVoluntaria() {
        FacesMessage msg = null;
        if (renuncia) {
            labeltramocdi = "Renuncia Voluntaria:";
            labeladjuntar = "Adjuntar Doc. Renuncia V.:";
            labelResol = "Resolución RV:";
            labelFechaR = "Fecha de Resolución RV:";
            tiporenuncia = true;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "RENUNCIA VOLUNTARA ACTIVADA");
        } else {
            labeltramocdi = "Número Trámite OCDI:";
            labeladjuntar = "Adjuntar Documento Resolución:";
            labelResol = "Resolución OCDI:";
            labelFechaR = "Fecha de Resolución OCDI:";
            tiporenuncia = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "HECHO");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);

    }

    public void validarResolucion(ActionEvent ae) {
        FacesMessage msg = null;
        if (tituloCancelado != null) {
            if (tituloCancelado.getDocumento() != null && !tituloCancelado.getDocumento().trim().isEmpty()) {
                PrimeFaces.current().ajax().addCallbackParam("viewresolucion", true);
                PrimeFaces.current().ajax().addCallbackParam("view", tituloCancelado.getDocumento());
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CERTIFICADO CARGAD0");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR EL DOCUMENTO DIGITAL");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL CARGAR EL DOCUMENTO DIGITAL");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void reversarCancelacion(ActionEvent ae) {
//        System.out.println("reversar cancelaciónnnnnnnnnnnnnnnnn");
        FacesMessage msg = null;
        TituloCancelado titc = (TituloCancelado) tituloCanceladoDataTable.getRowData();
        if (titc.getId() != null) {
            Controlador c = new Controlador();
            titc.setReverso(true);
            titc.setFechaReverso(new Date());
            titc.setUsuarioReverso(c.getLogin().getNombre());

            if (c.updateTituloCancelado(titc)) {
                if (reversoTitulo(titc)) {
                    loadTitulosCancelados();
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "LA CANCELACIÓN DEL TÍTULO " + titc.getNumeroTitulo() + " FUE REVERSADA EXITÓSAMENTE");
                } else {
                    titc.setReverso(false);
                    titc.setUsuarioReverso("");
                    c.updateTituloCancelado(titc);
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REVERSAR EL TÍTULO " + titc.getDenominacion() + " - RENOVACIONES");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO REVERSAR EL TÍTULO " + titc.getDenominacion());
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ EL REGISTRO");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public boolean reversoTitulo(TituloCancelado tit) {
        Controlador c = new Controlador();

        System.out.println("----------Reverso Título " + tit.getNumeroTitulo() + ", " + tit.getTipoCancelacion() + "-----------");
        if (tit.getId() != null) {

            List<Renovacion> rens = c.getRenovacionesByTituloAndDenominacion(tit.getNumeroTitulo(), tit.getDenominacion());
            List<Notificada> nots = c.getNotificadasByTituloAndDenominacion(tit.getNumeroTitulo(), tit.getDenominacion());
            List<Desistida> desis = c.getDesistidasByTituloAndDenominacion(tit.getNumeroTitulo(), tit.getDenominacion());
            List<CaducadaRen> cads = c.getCaducadasRByTituloAndDenominacion(tit.getNumeroTitulo(), tit.getDenominacion());
            for (int i = 0; i < rens.size(); i++) {
                Renovacion renova = rens.get(i);
                renova.setCancelado(null);
                c.updateRenovacion(renova);
                System.out.println("RENOVACIÓN CANCELACIÓN REVERSADA:" + renova.toString());
            }
            for (int i = 0; i < nots.size(); i++) {
                Notificada noti = nots.get(i);
                noti.setCancelado(null);
                c.updateNotificadaR(noti);
                System.out.println("RENO-NOTIFICACIÓN REVERSADA: " + noti.toString());
            }

            for (int i = 0; i < desis.size(); i++) {
                Desistida desist = desis.get(i);
                desist.setCancelado(null);
                c.updateDesistidaR(desist);
                System.out.println("RENO-DESISTIDA REVERSADA: " + desist.toString());
            }

            for (int i = 0; i < cads.size(); i++) {
                CaducadaRen cad = cads.get(i);
                cad.setCancelado(null);
                c.updateCaducadaR(cad);
                System.out.println("RENO-CADUCADA: " + cad.toString());
            }

            System.out.println("Cancelación de título reversado :" + tit.toString());
            busqueda = false;
            return true;

        } else {
            return false;
        }
    }

    public void cancelacionEjecucion(ActionEvent ae) {
        FacesMessage msg = null;

        loadTitulosCancelados();
        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SE HA CANCELADO LA ACTIVIDAD");

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void registrarTitulo(ActionEvent ae) {
        denominacionRegistro = "";
        signosSinTitulo = new ArrayList<>();
//        loadTitulosCancelados();
    }

    public void buscarPorDenominacion(ActionEvent ae) {
        FacesMessage msg = null;
//        System.out.println("Buscar denominación: "+denominacionRegistro);
        if (denominacionRegistro != null && !denominacionRegistro.trim().isEmpty()) {

            Controlador c = new Controlador();
            signosSinTitulo = new ArrayList<>();
            signosSinTitulo = c.getPpdiSolicitudSignoDistintivoByDenominacion(denominacionRegistro);
            if (signosSinTitulo.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS PARA " + denominacionRegistro);
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "BÚSQUEDA REALIZADA");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL REALIZAR LA BÚSQUEDA");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void guardarNuevoTitulo(ActionEvent ae) {
        FacesMessage msg = null;
        if (signoSinTitulo != null && signoSinTitulo.getCodigoSolicitudSigno() != null) {
            if (signoSinTitulo.getNumTitulo() != null && !signoSinTitulo.getNumTitulo().trim().isEmpty()) {
                Controlador c = new Controlador();
                PpdiTituloSignoDistintivo titul = new PpdiTituloSignoDistintivo();
                titul.setNumeroTitulo(signoSinTitulo.getNumTitulo());
                titul.setFechaEmisionDocumento(signoSinTitulo.getFechaEmisionDocumento());
                titul.setFechaVencimientoTitulo(signoSinTitulo.getFechaVencimiento());
                titul.setCodigoSolicitudSigno(signoSinTitulo.getCodigoSolicitudSigno());

                titul.setCodigoTituloSignoDistintivo(c.getNextNumeroMinimoTitulo());

                if (c.savePpdiTituloSignoDistintivo(titul)) {
                    PrimeFaces.current().ajax().addCallbackParam("savetitdo", true);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "EL TÍTULO " + titul.getNumeroTitulo() + " SE GUARDÓ CORRECTAMENTE");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL INTENTAR GUARDAR EL TÍTULO " + titul.getNumeroTitulo());
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN NÚMERO DE TÍTULO VÁLIDO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ CORRECTAMENTE EL TRÁMITE");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return the numtitulo
     */
    public String getNumtitulo() {
        return numtitulo;
    }

    /**
     * @param numtitulo the numtitulo to set
     */
    public void setNumtitulo(String numtitulo) {
        this.numtitulo = numtitulo;
    }

    /**
     * @return the titulos
     */
    public List<PpdiTitAndSigno> getTitulos() {
        return titulos;
    }

    /**
     * @param titulos the titulos to set
     */
    public void setTitulos(List<PpdiTitAndSigno> titulos) {
        this.titulos = titulos;
    }

    /**
     * @return the titulosFiltrados
     */
    public List<PpdiTitAndSigno> getTitulosFiltrados() {
        return titulosFiltrados;
    }

    /**
     * @param titulosFiltrados the titulosFiltrados to set
     */
    public void setTitulosFiltrados(List<PpdiTitAndSigno> titulosFiltrados) {
        this.titulosFiltrados = titulosFiltrados;
    }

    /**
     * @return the tituloDataTable
     */
    public UIData getTituloDataTable() {
        return tituloDataTable;
    }

    /**
     * @param tituloDataTable the tituloDataTable to set
     */
    public void setTituloDataTable(UIData tituloDataTable) {
        this.tituloDataTable = tituloDataTable;
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
     * @return the titulo
     */
    public PpdiTitAndSigno getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(PpdiTitAndSigno titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the busqueda
     */
    public boolean isBusqueda() {
        return busqueda;
    }

    /**
     * @param busqueda the busqueda to set
     */
    public void setBusqueda(boolean busqueda) {
        this.busqueda = busqueda;
    }

    /**
     * @return the titulosCancelados
     */
    public List<TituloCancelado> getTitulosCancelados() {
        return titulosCancelados;
    }

    /**
     * @param titulosCancelados the titulosCancelados to set
     */
    public void setTitulosCancelados(List<TituloCancelado> titulosCancelados) {
        this.titulosCancelados = titulosCancelados;
    }

    /**
     * @return the titulosCanceladosFiltrados
     */
    public List<TituloCancelado> getTitulosCanceladosFiltrados() {
        return titulosCanceladosFiltrados;
    }

    /**
     * @param titulosCanceladosFiltrados the titulosCanceladosFiltrados to set
     */
    public void setTitulosCanceladosFiltrados(List<TituloCancelado> titulosCanceladosFiltrados) {
        this.titulosCanceladosFiltrados = titulosCanceladosFiltrados;
    }

    /**
     * @return the tituloCanceladoDataTable
     */
    public UIData getTituloCanceladoDataTable() {
        return tituloCanceladoDataTable;
    }

    /**
     * @param tituloCanceladoDataTable the tituloCanceladoDataTable to set
     */
    public void setTituloCanceladoDataTable(UIData tituloCanceladoDataTable) {
        this.tituloCanceladoDataTable = tituloCanceladoDataTable;
    }

    /**
     * @return the denominacion
     */
    public String getDenominacion() {
        return denominacion;
    }

    /**
     * @param denominacion the denominacion to set
     */
    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    /**
     * @return the tituloCancelado
     */
    public TituloCancelado getTituloCancelado() {
        return tituloCancelado;
    }

    /**
     * @param tituloCancelado the tituloCancelado to set
     */
    public void setTituloCancelado(TituloCancelado tituloCancelado) {
        this.tituloCancelado = tituloCancelado;
    }

    /**
     * @return the prodvig
     */
    public boolean isProdvig() {
        return prodvig;
    }

    /**
     * @param prodvig the prodvig to set
     */
    public void setProdvig(boolean prodvig) {
        this.prodvig = prodvig;
    }

    /**
     * @return the file
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }

    /**
     * @return the tramocdi
     */
    public boolean isTramocdi() {
        return tramocdi;
    }

    /**
     * @param tramocdi the tramocdi to set
     */
    public void setTramocdi(boolean tramocdi) {
        this.tramocdi = tramocdi;
    }

    /**
     * @return the rutadocscpis
     */
    public String getRutadocscpis() {
        return rutadocscpis;
    }

    /**
     * @param rutadocscpis the rutadocscpis to set
     */
    public void setRutadocscpis(String rutadocscpis) {
        this.rutadocscpis = rutadocscpis;
    }

    /**
     * @return the tramantiguo
     */
    public boolean isTramantiguo() {
        return tramantiguo;
    }

    /**
     * @param tramantiguo the tramantiguo to set
     */
    public void setTramantiguo(boolean tramantiguo) {
        this.tramantiguo = tramantiguo;
    }

    /**
     * @return the etiquetaTitulo
     */
    public String getEtiquetaTitulo() {
        return etiquetaTitulo;
    }

    /**
     * @param etiquetaTitulo the etiquetaTitulo to set
     */
    public void setEtiquetaTitulo(String etiquetaTitulo) {
        this.etiquetaTitulo = etiquetaTitulo;
    }

    /**
     * @return the cpis
     */
    public List<Cpis> getCpis() {
        return cpis;
    }

    /**
     * @param cpis the cpis to set
     */
    public void setCpis(List<Cpis> cpis) {
        this.cpis = cpis;
    }

    /**
     * @return the cpisFiltrados
     */
    public List<Cpis> getCpisFiltrados() {
        return cpisFiltrados;
    }

    /**
     * @param cpisFiltrados the cpisFiltrados to set
     */
    public void setCpisFiltrados(List<Cpis> cpisFiltrados) {
        this.cpisFiltrados = cpisFiltrados;
    }

    /**
     * @return the cpiDataTable
     */
    public UIData getCpiDataTable() {
        return cpiDataTable;
    }

    /**
     * @param cpiDataTable the cpiDataTable to set
     */
    public void setCpiDataTable(UIData cpiDataTable) {
        this.cpiDataTable = cpiDataTable;
    }

    /**
     * @return the denominacionRegistro
     */
    public String getDenominacionRegistro() {
        return denominacionRegistro;
    }

    /**
     * @param denominacionRegistro the denominacionRegistro to set
     */
    public void setDenominacionRegistro(String denominacionRegistro) {
        this.denominacionRegistro = denominacionRegistro;
    }

    /**
     * @return the signosSinTitulo
     */
    public List<PpdiTitAndSigno> getSignosSinTitulo() {
        return signosSinTitulo;
    }

    /**
     * @param signosSinTitulo the signosSinTitulo to set
     */
    public void setSignosSinTitulo(List<PpdiTitAndSigno> signosSinTitulo) {
        this.signosSinTitulo = signosSinTitulo;
    }

    /**
     * @return the signosSinTituloFiltrados
     */
    public List<PpdiTitAndSigno> getSignosSinTituloFiltrados() {
        return signosSinTituloFiltrados;
    }

    /**
     * @param signosSinTituloFiltrados the signosSinTituloFiltrados to set
     */
    public void setSignosSinTituloFiltrados(List<PpdiTitAndSigno> signosSinTituloFiltrados) {
        this.signosSinTituloFiltrados = signosSinTituloFiltrados;
    }

    /**
     * @return the signoSinTituloDataTable
     */
    public UIData getSignoSinTituloDataTable() {
        return signoSinTituloDataTable;
    }

    /**
     * @param signoSinTituloDataTable the signoSinTituloDataTable to set
     */
    public void setSignoSinTituloDataTable(UIData signoSinTituloDataTable) {
        this.signoSinTituloDataTable = signoSinTituloDataTable;
    }

    /**
     * @return the signoSinTitulo
     */
    public PpdiTitAndSigno getSignoSinTitulo() {
        return signoSinTitulo;
    }

    /**
     * @param signoSinTitulo the signoSinTitulo to set
     */
    public void setSignoSinTitulo(PpdiTitAndSigno signoSinTitulo) {
        this.signoSinTitulo = signoSinTitulo;
    }

    /**
     * @return the ejemploTitRes
     */
    public String getEjemploTitRes() {
        return ejemploTitRes;
    }

    /**
     * @param ejemploTitRes the ejemploTitRes to set
     */
    public void setEjemploTitRes(String ejemploTitRes) {
        this.ejemploTitRes = ejemploTitRes;
    }

    /**
     * @return the titDenoText
     */
    public String getTitDenoText() {
        return titDenoText;
    }

    /**
     * @param titDenoText the titDenoText to set
     */
    public void setTitDenoText(String titDenoText) {
        this.titDenoText = titDenoText;
    }

    /**
     * @return the renuncia
     */
    public boolean isRenuncia() {
        return renuncia;
    }

    /**
     * @param renuncia the renuncia to set
     */
    public void setRenuncia(boolean renuncia) {
        this.renuncia = renuncia;
    }

    /**
     * @return the labeltramocdi
     */
    public String getLabeltramocdi() {
        return labeltramocdi;
    }

    /**
     * @param labeltramocdi the labeltramocdi to set
     */
    public void setLabeltramocdi(String labeltramocdi) {
        this.labeltramocdi = labeltramocdi;
    }

    /**
     * @return the labeladjuntar
     */
    public String getLabeladjuntar() {
        return labeladjuntar;
    }

    /**
     * @param labeladjuntar the labeladjuntar to set
     */
    public void setLabeladjuntar(String labeladjuntar) {
        this.labeladjuntar = labeladjuntar;
    }

    /**
     * @return the labelResol
     */
    public String getLabelResol() {
        return labelResol;
    }

    /**
     * @param labelResol the labelResol to set
     */
    public void setLabelResol(String labelResol) {
        this.labelResol = labelResol;
    }

    /**
     * @return the labelFechaR
     */
    public String getLabelFechaR() {
        return labelFechaR;
    }

    /**
     * @param labelFechaR the labelFechaR to set
     */
    public void setLabelFechaR(String labelFechaR) {
        this.labelFechaR = labelFechaR;
    }

    /**
     * @return the tiporenuncia
     */
    public boolean isTiporenuncia() {
        return tiporenuncia;
    }

    /**
     * @param tiporenuncia the tiporenuncia to set
     */
    public void setTiporenuncia(boolean tiporenuncia) {
        this.tiporenuncia = tiporenuncia;
    }
}
