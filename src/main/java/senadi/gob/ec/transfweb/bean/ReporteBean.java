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
import org.primefaces.component.api.UIData;
import org.primefaces.event.TabChangeEvent;
import senadi.gob.ec.transfweb.model.Abandono;
import senadi.gob.ec.transfweb.model.Caducada;
import senadi.gob.ec.transfweb.model.Desistimiento;
import senadi.gob.ec.transfweb.model.Prorroga;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.model.ReporteModificacion;
import senadi.gob.ec.transfweb.model.Transferencia;
import senadi.gob.ec.transfweb.model.UploadNotificacion;
import senadi.gob.ec.transfweb.model.cd.CambioDomicilio;
import senadi.gob.ec.transfweb.model.cn.CambioNombre;
import senadi.gob.ec.transfweb.model.licencia.LicenciaUso;
import senadi.gob.ec.transfweb.model.licencia.SubLicenciaUso;
import senadi.gob.ec.transfweb.model.prenda.PrendaComercial;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author micharesp
 */
@ManagedBean(name = "reporteBean")
@ViewScoped
public class ReporteBean implements Serializable {

    private String tramite;
    private String criterio;
    private String denominacion;
    private String titular;

    private ReporteModificacion reporte;
    private List<ReporteModificacion> reportes;
    private List<ReporteModificacion> reportesFiltrados;

    private UIData reportesDataTable;

    private String numRegistros;
    private String exportName;

    private Date fechaInicio;
    private Date fechaFin;

    private String tipobusqueda;

    public ReporteBean() {
        numRegistros = "Número Registros Mostrados: 0";
        exportName = "modificaciones_" + Operaciones.formatDate(new Date());
        tipobusqueda = "denominacion";
    }

    public void onTabChange(TabChangeEvent event) {
        if (event.getTab().getId().equals("deno")) {
            tipobusqueda = "denominacion";
            tramite = "";
            titular = "";

        } else if (event.getTab().getId().equals("tram")) {
            tipobusqueda = "tramite";
            denominacion = "";
            titular = "";
        } else if (event.getTab().getId().equals("titul")) {
            tipobusqueda = "titular";
            denominacion = "";
            tramite = "";
        } else {
            tipobusqueda = "fecha";
            tramite = "";
            denominacion = "";
            titular = "";
        }
    }

    public void buscarTramites(ActionEvent ae) {
        FacesMessage msg = null;
        if (tipobusqueda.equals("denominacion")) {
            if (denominacion != null && !denominacion.trim().isEmpty()) {
                System.out.println("Buscando denominación: " + denominacion);
                reportes = new ArrayList<>();
                putModificacionesIntoReporteByDenominacion();

                numRegistros = "Número Registros Mostrados: " + reportes.size();
                if (!reportes.isEmpty()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CONSULTA REALIZADA");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UNA DENOMINACIÓN VÁLIDA");
            }
        } else if (tipobusqueda.equals("tramite")) {
            if (tramite != null && !tramite.trim().isEmpty()) {
                reportes = new ArrayList<>();
                System.out.println("Buscando tramite: " + tramite);
                putModificacionesIntoReporte();

                numRegistros = "Número Registros Mostrados: " + reportes.size();
                if (!reportes.isEmpty()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CONSULTA REALIZADA");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN NÚMERO DE TRÁMITE VÁLIDO");
            }
        } else if (tipobusqueda.equals("titular")) {
            if (titular != null && !titular.trim().isEmpty()) {
                reportes = new ArrayList<>();
                System.out.println("Buscando titular: " + titular);
                putModificacionesIntoReporteByTitular();

                numRegistros = "Número Registros Mostrados: " + reportes.size();
                if (!reportes.isEmpty()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CONSULTA REALIZADA");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN TITULAR VÁLIDO");
            }
        } else {
            if (validarFechas(fechaInicio, fechaFin)) {
                System.out.println("Buscando por fecha: " + Operaciones.formatDate(fechaInicio) + " - " + Operaciones.formatDate(fechaFin));
                reportes = new ArrayList<>();
                putModificacionesIntoReporteByFecha();

                numRegistros = "Número Registros Mostrados: " + reportes.size();
                if (!reportes.isEmpty()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CONSULTA REALIZADA");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN RANGO DE FECHAS VÁLIDO");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void putModificacionesIntoReporteByTitular() {
        Controlador c = new Controlador();
        List<Transferencia> transferencias = c.getTransferenciasByTitular(titular);
        List<Notificacion> notificaciones = c.getNotificacionesByTitular(titular);
        List<Desistimiento> desistidas = c.getDesistidasByTitular(titular);
        List<Caducada> caducadas = c.getCaducadasByTitular(titular);

        List<CambioNombre> nombres = c.getCambiosNombreByTitular(titular);
        List<CambioDomicilio> domicilios = c.getCambiosDomicilioByTitular(titular);
        List<PrendaComercial> prendas = c.getPrendasComercialesByTitular(titular);
        List<LicenciaUso> licencias = c.getLicenciasUsoByTitular(titular);
        List<SubLicenciaUso> sublicencias = c.getSublicenciasUsoByTitular(titular);

        List<Abandono> abandonos = c.getAbandonosByTitular(titular);
        List<Prorroga> prorrogas = c.getProrrogasByTitular(titular);

        addsModificaciones(transferencias, notificaciones, desistidas, caducadas, nombres, domicilios, prendas, licencias, sublicencias);
        for (int i = 0; i < abandonos.size(); i++) {
            addAbandono(abandonos.get(i));
        }
        for (int i = 0; i < prorrogas.size(); i++) {
            addProrroga(prorrogas.get(i));
        }
    }

    public void buscarTramitesPorFecha(ActionEvent ae) {
        FacesMessage msg = null;
        if (validarFechas(fechaInicio, fechaFin)) {

            reportes = new ArrayList<>();
            putModificacionesIntoReporteByFecha();

            numRegistros = "Número Registros Mostrados: " + reportes.size();
            if (!reportes.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CONSULTA REALIZADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN RANGO DE FECHAS VÁLIDO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public boolean validarFechas(Date start, Date end) {
        try {
            start.toString();
            end.toString();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void buscarTramitesPorDenominacion(ActionEvent ae) {
        FacesMessage msg = null;
        if (denominacion != null && !denominacion.trim().isEmpty()) {

            reportes = new ArrayList<>();
            putModificacionesIntoReporteByDenominacion();

            numRegistros = "Número Registros Mostrados: " + reportes.size();
            if (!reportes.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CONSULTA REALIZADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UNA DENOMINACIÓN VÁLIDA");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void buscarTramitesByNumero(ActionEvent ae) {
        FacesMessage msg = null;
        if (tramite != null && !tramite.trim().isEmpty()) {
//            System.out.println("Si llegamos por aquí");

            reportes = new ArrayList<>();
            putModificacionesIntoReporte();

            numRegistros = "Número Registros Mostrados: " + reportes.size();
            if (!reportes.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CONSULTA REALIZADA");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN NÚMERO DE TRÁMITE VÁLIDO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void putModificacionesIntoReporteByFecha() {
        Controlador c = new Controlador();
        List<Transferencia> transferencias = c.getTransferenciasByFecha(fechaInicio, fechaFin);
        List<Notificacion> notificaciones = c.getNotificacionesByFecha(fechaInicio, fechaFin);
        List<Desistimiento> desistidas = c.getDesistimientosByFecha(fechaInicio, fechaFin);
        List<Caducada> caducadas = c.getCaducadasByFecha(fechaInicio, fechaFin);

        List<CambioNombre> nombres = c.getCambiosNombreByFecha(fechaInicio, fechaFin);
        List<CambioDomicilio> domicilios = c.getCambiosDomicilioByFecha(fechaInicio, fechaFin);
        List<PrendaComercial> prendas = c.getPrendasComercialesByFecha(fechaInicio, fechaFin);
        List<LicenciaUso> licencias = c.getLicenciasUsoByFecha(fechaInicio, fechaFin);
        List<SubLicenciaUso> sublicencias = c.getSublicenciasUsoByFecha(fechaInicio, fechaFin);

        List<Abandono> abandonos = c.getAbandonosByFecha(fechaInicio, fechaFin);
        List<Prorroga> prorrogas = c.getProrrogasByFecha(fechaInicio, fechaFin);

        addsModificaciones(transferencias, notificaciones, desistidas, caducadas, nombres, domicilios, prendas, licencias, sublicencias);
        for (int i = 0; i < abandonos.size(); i++) {
            addAbandono(abandonos.get(i));
        }
        for (int i = 0; i < prorrogas.size(); i++) {
            addProrroga(prorrogas.get(i));
        }
    }

    public void putModificacionesIntoReporteByDenominacion() {
        Controlador c = new Controlador();
        List<Transferencia> transferencias = c.getTransferenciasByDenominacion(denominacion);
        List<Notificacion> notificaciones = c.getNotificacionesByDenominacion(denominacion);
        List<Desistimiento> desistidas = c.getDesistidasByDenominacion(denominacion);
        List<Caducada> caducadas = c.getCaducadasByDenominacion(denominacion);

        List<CambioNombre> nombres = c.getCambiosNombreByDenominacion(denominacion);
        List<CambioDomicilio> domicilios = c.getCambiosDomicilioByDenominacion(denominacion);
        List<PrendaComercial> prendas = c.getPrendasComercialesByDenominacion(denominacion);
        List<LicenciaUso> licencias = c.getLicenciasUsoByDenominacion(denominacion);
        List<SubLicenciaUso> sublicencias = c.getSublicenciasUsoByDenominacion(denominacion);

        List<Abandono> abandonos = c.getAbandonosByDenominacion(denominacion);
        List<Prorroga> prorrogas = c.getProrrogasByDenominacion(denominacion);

        addsModificaciones(transferencias, notificaciones, desistidas, caducadas, nombres, domicilios, prendas, licencias, sublicencias);
        for (int i = 0; i < abandonos.size(); i++) {
            addAbandono(abandonos.get(i));
        }
        for (int i = 0; i < prorrogas.size(); i++) {
            addProrroga(prorrogas.get(i));
        }
    }

    public void addsModificaciones(List<Transferencia> transferencias, List<Notificacion> notificaciones,
            List<Desistimiento> desistidas, List<Caducada> caducadas, List<CambioNombre> nombres,
            List<CambioDomicilio> domicilios, List<PrendaComercial> prendas, List<LicenciaUso> licencias,
            List<SubLicenciaUso> sublicencias) {
        for (int i = 0; i < transferencias.size(); i++) {
            Transferencia taux = transferencias.get(i);
            addTransferencia(taux);
        }
        for (int i = 0; i < notificaciones.size(); i++) {
            Notificacion naux = notificaciones.get(i);
            addNotificacion(naux);
        }
        for (int i = 0; i < desistidas.size(); i++) {
            Desistimiento daux = desistidas.get(i);
            addDesistida(daux);
        }
        for (int i = 0; i < caducadas.size(); i++) {
            Caducada caux = caducadas.get(i);
            addCaducada(caux);
        }
        for (int i = 0; i < nombres.size(); i++) {
            CambioNombre caux = nombres.get(i);
            addCambioNombre(caux);
        }
        for (int i = 0; i < domicilios.size(); i++) {
            CambioDomicilio caux = domicilios.get(i);
            addCambioDomicilio(caux);
        }
        for (int i = 0; i < prendas.size(); i++) {
            PrendaComercial paux = prendas.get(i);
            addPrendaComercial(paux);
        }
        for (int i = 0; i < licencias.size(); i++) {
            LicenciaUso laux = licencias.get(i);
            addLicenciaUso(laux);
        }
        for (int i = 0; i < sublicencias.size(); i++) {
            SubLicenciaUso saux = sublicencias.get(i);
            addSublicencia(saux);
        }
    }

    public void putModificacionesIntoReporte() {
        Controlador c = new Controlador();
        Transferencia transferencia = c.getTransferenciaBySolSenadi(tramite);
        Notificacion notificacion = c.getNotificacionBySolSenadi(tramite);
        Desistimiento desistida = c.getDesistidasBySolSenadi(tramite);
        Caducada caducada = c.getCaducadaBySolSenadi(tramite);

        CambioNombre cnombre = c.getCambioNombreBySolicitud(tramite);
        CambioDomicilio cdomic = c.getCambioDomicilioBySolicitud(tramite);
        PrendaComercial pren = c.getPrendaComercialBySolicitud(tramite);
        LicenciaUso lic = c.getLicenciaUsoBySolicitud(tramite);
        SubLicenciaUso sub = c.getSublicenciaUsoBySolicitud(tramite);

        Abandono abandono = c.getAbandonoBySolicitud(tramite);
        Prorroga prorroga = c.getProrrogaBySolicitud(tramite);

        addTransferencia(transferencia);
        addNotificacion(notificacion);
        addDesistida(desistida);
        addCaducada(caducada);
        addCambioNombre(cnombre);
        addCambioDomicilio(cdomic);
        addPrendaComercial(pren);
        addLicenciaUso(lic);
        addSublicencia(sub);
        addAbandono(abandono);
        addProrroga(prorroga);
    }

    public String validarModificacion(String solicitud, String titulo, int bandera) {
        String rutaNotificacionCasillero = "";
        Controlador c = new Controlador();
        List<UploadNotificacion> uploads = c.getUploadNotificacionBySolicitud(solicitud, true);
        if (!uploads.isEmpty()) {
            if (uploads.size() > 1) {
                for (int i = 0; i < uploads.size(); i++) {
                    UploadNotificacion unaux = uploads.get(i);
                    String rutaux = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                    int conf = Operaciones.validaTextoEnPdf(rutaux, titulo);
                    if (conf == bandera) {
                        rutaNotificacionCasillero = rutaux;
                        break;
                    }
                }
                if (!rutaNotificacionCasillero.trim().isEmpty()) {
                    return rutaNotificacionCasillero;
                } else {
                    return "";
                }
            } else {
                UploadNotificacion unaux = uploads.get(0);
                rutaNotificacionCasillero = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + unaux.getCasillero() + "/" + unaux.getDocumento();
                return rutaNotificacionCasillero;
            }
        } else {
            return "";
        }
    }

    public void addTransferencia(Transferencia transferencia) {
        if (transferencia.getId() != null) {
            ReporteModificacion rep = new ReporteModificacion();
            rep.setSolicitud(transferencia.getSolicitud());
            rep.setDenominacion(transferencia.getDenominacion());
            rep.setTipo("TRANSFERENCIA");
            rep.setEstado("CERTIFICADO");
            rep.setCasillero(transferencia.getCasilleroSenadi());
            rep.setFechaPresentacion(transferencia.getFechaPresentacion());
            rep.setFechaRegistro(transferencia.getFechaRegistro());
            rep.setRegistro(transferencia.getRegistro());
            rep.setResponsable(transferencia.getResponsable());
            rep.setSigno(transferencia.getSigno());
            rep.setNumDocumento(transferencia.getCertificado() + "");
            rep.setFechaDocumento(transferencia.getFechaCertificado());

            rep.setActor1(transferencia.getTitularActual());
            rep.setActor2(transferencia.getTitularAnterior());

            rep.setTipoActor1("Titular Actual");
            rep.setTipoActor2("Titular Anterior");

            rep.setDocumentoEmitido(transferencia.isCertificadoEmitido());
            if (rep.isDocumentoEmitido()) {
                String ruta = validarModificacion(rep.getSolicitud(), "CERTIFICADO DE TRANSFERENCIA No", 1);
                if (!ruta.trim().isEmpty()) {
                    rep.setRutaDocumento(ruta);
                }
            }

            reportes.add(rep);
        }
    }

    public void addNotificacion(Notificacion notificacion) {
        if (notificacion.getId() != null) {
            ReporteModificacion rep = new ReporteModificacion();
            rep.setSolicitud(notificacion.getSolicitud());
            rep.setDenominacion(notificacion.getDenominacion());
            rep.setTipo("TRANSFERENCIA");
            rep.setEstado("NOTIFICADA");
            rep.setCasillero(notificacion.getCasilleroSenadi());
            rep.setFechaPresentacion(notificacion.getFechaPresentacion());
            rep.setFechaRegistro(notificacion.getFechaRegistro());
            rep.setRegistro(notificacion.getRegistro());
            rep.setResponsable(notificacion.getResponsable());
            rep.setSigno(notificacion.getSigno());

            rep.setNumDocumento(notificacion.getNotificacion() + "");
            rep.setFechaDocumento(notificacion.getFechaNotificacion());

            rep.setActor1(notificacion.getTitularActual());
            rep.setActor2(notificacion.getTitularAnterior());

            rep.setTipoActor1("Titular Actual");
            rep.setTipoActor2("Titular Anterior");

            rep.setDocumentoEmitido(notificacion.isNotificacionEmitida());
            if (rep.isDocumentoEmitido()) {
                String ruta = validarModificacion(rep.getSolicitud(), "CERTIFICADO DE TRANSFERENCIA No", 0);
                if (!ruta.trim().isEmpty()) {
                    rep.setRutaDocumento(ruta);
                }
            }

            reportes.add(rep);
        }
    }

    public void addDesistida(Desistimiento desistida) {
        if (desistida.getId() != null) {
            ReporteModificacion rep = new ReporteModificacion();
            rep.setSolicitud(desistida.getSolicitud());
            rep.setDenominacion(desistida.getDenominacion());
            rep.setTipo("TRANSFERENCIA");
            rep.setEstado("DESISTIDA");
            rep.setCasillero(desistida.getCasilleroSenadi());
            rep.setFechaPresentacion(desistida.getFechaSolicitud());
            rep.setFechaRegistro(desistida.getFechaTitulo());
            rep.setRegistro(desistida.getTitulo());
            rep.setResponsable(desistida.getResponsable());
            rep.setSigno(desistida.getSigno());

            rep.setActor1(desistida.getTitularActual());
            rep.setActor2(desistida.getTitularAnterior());

            rep.setTipoActor1("Titular Actual");
            rep.setTipoActor2("Titular Anterior");

            rep.setNumDocumento(desistida.getResolucion());
            rep.setFechaDocumento(desistida.getFechaResolucion());
            reportes.add(rep);
        }
    }

    public void addCaducada(Caducada caducada) {
        if (caducada.getId() != null) {
            ReporteModificacion rep = new ReporteModificacion();
            rep.setSolicitud(caducada.getSolicitud());
            rep.setDenominacion(caducada.getDenominacion());
            rep.setTipo("TRANSFERENCIA");
            rep.setEstado("CADUCADA-NEGADA");
            rep.setCasillero(caducada.getCasilleroSenadi());
            rep.setFechaPresentacion(caducada.getFechaPresentacion());
            rep.setFechaRegistro(caducada.getFechaRegistro());
            rep.setRegistro(caducada.getRegistro());
            rep.setResponsable(caducada.getResponsable());
            rep.setSigno(caducada.getSigno());

            rep.setActor1(caducada.getTitularActual());
            rep.setActor2(caducada.getTitularAnterior());

            rep.setTipoActor1("Titular Actual");
            rep.setTipoActor2("Titular Anterior");

            rep.setNumDocumento(caducada.getResolucion());
            rep.setFechaDocumento(caducada.getFechaResolucion());
            reportes.add(rep);
        }
    }

    public void addAbandono(Abandono abandono) {
        if (abandono.getId() != null) {
            ReporteModificacion rep = new ReporteModificacion();
            rep.setSolicitud(abandono.getSolicitud());
            rep.setDenominacion(abandono.getDenominacion());
            rep.setTipo("TRANSFERENCIA");
            rep.setEstado("ABANDONO");
            rep.setCasillero(abandono.getCasilleroSenadi());
            rep.setFechaPresentacion(abandono.getFechaPresentacion());
            rep.setFechaRegistro(abandono.getFechaRegistro());
            rep.setRegistro(abandono.getRegistro());
            rep.setResponsable(abandono.getResponsable());
            rep.setSigno(abandono.getSigno());

            rep.setActor1(abandono.getTitularActual());
            rep.setActor2(abandono.getTitularAnterior());

            rep.setTipoActor1("Titular Actual");
            rep.setTipoActor2("Titular Anterior");

            rep.setNumDocumento(abandono.getNumeroAbandono() + "");
            rep.setFechaDocumento(abandono.getFechaAbandono());

            rep.setDocumentoEmitido(abandono.isAbandonoNotificado());
            reportes.add(rep);
        }
    }

    public void addProrroga(Prorroga prorroga) {
        if (prorroga.getId() != null) {
            ReporteModificacion rep = new ReporteModificacion();
            rep.setSolicitud(prorroga.getSolicitud());
            rep.setDenominacion(prorroga.getDenominacion());
            rep.setTipo("TRANSFERENCIA");
            rep.setEstado("PRORROGA");
            rep.setCasillero(prorroga.getCasilleroSenadi());
            rep.setFechaPresentacion(prorroga.getFechaPresentacion());
            rep.setFechaRegistro(prorroga.getFechaRegistro());
            rep.setRegistro(prorroga.getRegistro());
            rep.setResponsable(prorroga.getResponsable());
            rep.setSigno(prorroga.getSigno());

            rep.setActor1(prorroga.getTitularActual());
            rep.setActor2(prorroga.getTitularAnterior());

            rep.setTipoActor1("Titular Actual");
            rep.setTipoActor2("Titular Anterior");

            rep.setNumDocumento(prorroga.getNumeroProrroga() + "");
            rep.setFechaDocumento(prorroga.getFechaProrroga());

            rep.setDocumentoEmitido(prorroga.getProrrogaNotificada() != null && prorroga.getProrrogaNotificada());
            reportes.add(rep);
        }
    }

    public void addCambioNombre(CambioNombre cnombre) {
        if (cnombre.getId() != null) {
            ReporteModificacion rep = new ReporteModificacion();
            rep.setSolicitud(cnombre.getSolicitud());
            rep.setDenominacion(cnombre.getDenominacion());
            rep.setTipo("CAMBIO DE NOMBRE");
            rep.setEstado(cnombre.getTipoEstado());
            rep.setCasillero(cnombre.getCasilleroSenadi());
            rep.setFechaPresentacion(cnombre.getFechaPresentacion());
            rep.setFechaRegistro(cnombre.getFechaRegistro());
            rep.setRegistro(cnombre.getRegistro());
            rep.setResponsable(cnombre.getResponsable());
            rep.setSigno(cnombre.getSigno());

            rep.setActor1(cnombre.getTitularActual());
            rep.setActor2(cnombre.getTitularAnterior());

            rep.setTipoActor1("Titular Actual");
            rep.setTipoActor2("Titular Anterior");

            if (rep.getEstado().equals("CERTIFICADO")) {
                rep.setNumDocumento(cnombre.getCertificado() + "");
                rep.setFechaDocumento(cnombre.getFechaCertificado());
            } else if (rep.getEstado().equals("NOTIFICADA")) {
                rep.setNumDocumento(cnombre.getNotificacion() + "");
                rep.setFechaDocumento(cnombre.getFechaNotificacion());
            } else if (rep.getEstado().equals("DESISTIDA")) {
                rep.setNumDocumento(cnombre.getResolucionDesistida() + "");
                rep.setFechaDocumento(cnombre.getFechaResolucionDesistida());
            } else if (rep.getEstado().equals("PRORROGA")) {
                rep.setNumDocumento(cnombre.getNumeroProrroga() + "");
                rep.setFechaDocumento(cnombre.getFechaProrroga());
            } else if (rep.getEstado().equals("ABANDONO")) {
                rep.setNumDocumento(cnombre.getNumeroAbandono() + "");
                rep.setFechaDocumento(cnombre.getFechaAbandono());
            } else {
                rep.setNumDocumento(cnombre.getResolucionCaducada() + "");
                rep.setFechaDocumento(cnombre.getFechaResolucionCaducada());
            }

            rep.setDocumentoEmitido(cnombre.isCertificadoEmitido());
            if (rep.isDocumentoEmitido()) {
                String ruta = validarModificacion(rep.getSolicitud(), "CERTIFICADO DE CAMBIO DE NOMBRE", 1);
                if (!ruta.trim().isEmpty()) {
                    rep.setRutaDocumento(ruta);
                }
            }

            reportes.add(rep);
        }
    }

    public void addCambioDomicilio(CambioDomicilio cdomic) {
        if (cdomic.getId() != null) {
            ReporteModificacion rep = new ReporteModificacion();
            rep.setSolicitud(cdomic.getSolicitud());
            rep.setDenominacion(cdomic.getDenominacion());
            rep.setTipo("CAMBIO DE DOMICILIO");
            rep.setEstado(cdomic.getTipoEstado());
            rep.setCasillero(cdomic.getCasilleroSenadi());
            rep.setFechaPresentacion(cdomic.getFechaPresentacion());
            rep.setFechaRegistro(cdomic.getFechaRegistro());
            rep.setRegistro(cdomic.getRegistro());
            rep.setResponsable(cdomic.getResponsable());
            rep.setSigno(cdomic.getSigno());

            rep.setActor1(cdomic.getTitularActual());

            rep.setTipoActor1("Titular Actual");

            if (rep.getEstado().equals("CERTIFICADO")) {
                rep.setNumDocumento(cdomic.getCertificado() + "");
                rep.setFechaDocumento(cdomic.getFechaCertificado());
            } else if (rep.getEstado().equals("NOTIFICADA")) {
                rep.setNumDocumento(cdomic.getNotificacion() + "");
                rep.setFechaDocumento(cdomic.getFechaNotificacion());
            } else if (rep.getEstado().equals("DESISTIDA")) {
                rep.setNumDocumento(cdomic.getResolucionDesistida() + "");
                rep.setFechaDocumento(cdomic.getFechaResolucionDesistida());
            } else if (rep.getEstado().equals("PRORROGA")) {
                rep.setNumDocumento(cdomic.getNumeroProrroga() + "");
                rep.setFechaDocumento(cdomic.getFechaProrroga());
            } else if (rep.getEstado().equals("ABANDONO")) {
                rep.setNumDocumento(cdomic.getNumeroAbandono() + "");
                rep.setFechaDocumento(cdomic.getFechaAbandono());
            } else {
                rep.setNumDocumento(cdomic.getResolucionCaducada() + "");
                rep.setFechaDocumento(cdomic.getFechaResolucionCaducada());
            }

            rep.setDocumentoEmitido(cdomic.isCertificadoEmitido());
            if (rep.isDocumentoEmitido()) {
                String ruta = validarModificacion(rep.getSolicitud(), "CERTIFICADO DE CAMBIO DE DOMICILIO", 1);
                if (!ruta.trim().isEmpty()) {
                    rep.setRutaDocumento(ruta);
                }
            }

            reportes.add(rep);
        }
    }

    public void addPrendaComercial(PrendaComercial pren) {
        if (pren.getId() != null) {
            ReporteModificacion rep = new ReporteModificacion();
            rep.setSolicitud(pren.getSolicitud());
            rep.setDenominacion(pren.getDenominacion());
            rep.setTipo("PRENDA COMERCIAL");
            rep.setEstado(pren.getTipoEstado());
            rep.setCasillero(pren.getCasilleroSenadi());
            rep.setFechaPresentacion(pren.getFechaPresentacion());
            rep.setFechaRegistro(pren.getFechaRegistro());
            rep.setRegistro(pren.getRegistro());
            rep.setResponsable(pren.getResponsable());
            rep.setSigno(pren.getSigno());

            rep.setActor1(pren.getDeudoraPrendaria());
            rep.setActor2(pren.getPrendariaAcreedora());

            rep.setTipoActor1("Deudor Prendario");
            rep.setTipoActor2("Acreedor Prendario");

            if (rep.getEstado().equals("CERTIFICADO")) {
                rep.setNumDocumento(pren.getPrendaNo() + "");
                rep.setFechaDocumento(pren.getFechaPrenda());
            } else if (rep.getEstado().equals("NOTIFICADA")) {
                rep.setNumDocumento(pren.getNotificacion() + "");
                rep.setFechaDocumento(pren.getFechaNotificacion());
            } else if (rep.getEstado().equals("PRORROGA")) {
                rep.setNumDocumento(pren.getNumeroProrroga() + "");
                rep.setFechaDocumento(pren.getFechaProrroga());
            } else if (rep.getEstado().equals("ABANDONO")) {
                rep.setNumDocumento(pren.getNumeroAbandono() + "");
                rep.setFechaDocumento(pren.getFechaAbandono());
            } else {
                rep.setNumDocumento(pren.getResolucionNo() + "");
                rep.setFechaDocumento(pren.getFechaResolucion());
            }

            rep.setDocumentoEmitido(pren.getCertificadoEmitido());
            if (rep.isDocumentoEmitido()) {
                String ruta = validarModificacion(rep.getSolicitud(), "INSCRIPCIÓN PRENDA COMERCIAL No", 1);
                if (!ruta.trim().isEmpty()) {
                    rep.setRutaDocumento(ruta);
                }
            }

            reportes.add(rep);
        }
    }

    public void addLicenciaUso(LicenciaUso lic) {
        if (lic.getId() != null) {
            ReporteModificacion rep = new ReporteModificacion();
            rep.setSolicitud(lic.getSolicitud());
            rep.setDenominacion(lic.getDenominacion());
            rep.setTipo("LICENCIA DE USO");
            rep.setEstado(lic.getTipoEstado());
            rep.setCasillero(lic.getCasilleroSenadi());
            rep.setFechaPresentacion(lic.getFechaPresentacion());
            rep.setFechaRegistro(lic.getFechaRegistro());
            rep.setRegistro(lic.getRegistro());
            rep.setResponsable(lic.getResponsable());
            rep.setSigno(lic.getSigno());

            rep.setActor1(lic.getLicenciante());
            rep.setActor2(lic.getLicenciatario());

            rep.setTipoActor1("Licenciante");
            rep.setTipoActor2("Licenciatario");

            if (rep.getEstado().equals("LICENCIA")) {
                rep.setNumDocumento(lic.getLicenciaNo());
                rep.setFechaDocumento(lic.getFechaLicencia());
            } else if (rep.getEstado().equals("NOTIFICADA")) {
                rep.setNumDocumento(lic.getNotificacion() + "");
                rep.setFechaDocumento(lic.getFechaNotificacion());
            } else if (rep.getEstado().equals("PRORROGA")) {
                rep.setNumDocumento(lic.getNumeroProrroga() + "");
                rep.setFechaDocumento(lic.getFechaProrroga());
            } else if (rep.getEstado().equals("ABANDONO")) {
                rep.setNumDocumento(lic.getNumeroAbandono() + "");
                rep.setFechaDocumento(lic.getFechaAbandono());
            } else {
                rep.setNumDocumento(lic.getResolucionNo() + "");
                rep.setFechaDocumento(lic.getFechaResolucion());
            }

            rep.setDocumentoEmitido(lic.isCertificadoEmitido());
            if (rep.isDocumentoEmitido()) {
                String ruta = validarModificacion(rep.getSolicitud(), "INSCRIPCIÓN LICENCIA ", 1);
                if (!ruta.trim().isEmpty()) {
                    rep.setRutaDocumento(ruta);
                }
            }

            reportes.add(rep);
        }
    }

    public void addSublicencia(SubLicenciaUso sub) {
        if (sub.getId() != null) {
            ReporteModificacion rep = new ReporteModificacion();
            rep.setSolicitud(sub.getSolicitud());
            rep.setDenominacion(sub.getDenominacion());
            rep.setTipo("SUBLICENCIA DE USO");
            rep.setEstado(sub.getTipoEstado());
            rep.setCasillero(sub.getCasilleroSenadi());
            rep.setFechaPresentacion(sub.getFechaPresentacion());
            rep.setFechaRegistro(sub.getFechaRegistro());
            rep.setRegistro(sub.getRegistro());
            rep.setResponsable(sub.getResponsable());
            rep.setSigno(sub.getSigno());

            rep.setActor1(sub.getSublicenciante());
            rep.setActor2(sub.getSublicenciatario());

            rep.setTipoActor1("Sublicenciante");
            rep.setTipoActor2("Sublicenciatario");

            if (rep.getEstado().equals("SUBLICENCIA")) {
                rep.setNumDocumento(sub.getSublicenciaNo());
                rep.setFechaDocumento(sub.getFechaSublicencia());
            } else if (rep.getEstado().equals("NOTIFICADA")) {
                rep.setNumDocumento(sub.getNotificacion() + "");
                rep.setFechaDocumento(sub.getFechaNotificacion());
            } else if (rep.getEstado().equals("PRORROGA")) {
                rep.setNumDocumento(sub.getNumeroProrroga() + "");
                rep.setFechaDocumento(sub.getFechaProrroga());
            } else if (rep.getEstado().equals("ABANDONO")) {
                rep.setNumDocumento(sub.getNumeroAbandono() + "");
                rep.setFechaDocumento(sub.getFechaAbandono());
            } else {
                rep.setNumDocumento(sub.getResolucionNo() + "");
                rep.setFechaDocumento(sub.getFechaResolucion());
            }

            rep.setDocumentoEmitido(sub.isCertificadoEmitido());
            if (rep.isDocumentoEmitido()) {
                String ruta = validarModificacion(rep.getSolicitud(), "INSCRIPCIÓN SUBLICENCIA ", 1);
                if (!ruta.trim().isEmpty()) {
                    rep.setRutaDocumento(ruta);
                }
            }

            reportes.add(rep);
        }
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
     * @return the tramite
     */
    public String getTramite() {
        return tramite;
    }

    /**
     * @param tramite the tramite to set
     */
    public void setTramite(String tramite) {
        this.tramite = tramite;
    }

    /**
     * @return the reporte
     */
    public ReporteModificacion getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(ReporteModificacion reporte) {
        this.reporte = reporte;
    }

    /**
     * @return the reportes
     */
    public List<ReporteModificacion> getReportes() {
        return reportes;
    }

    /**
     * @param reportes the reportes to set
     */
    public void setReportes(List<ReporteModificacion> reportes) {
        this.reportes = reportes;
    }

    /**
     * @return the reportesFiltrados
     */
    public List<ReporteModificacion> getReportesFiltrados() {
        return reportesFiltrados;
    }

    /**
     * @param reportesFiltrados the reportesFiltrados to set
     */
    public void setReportesFiltrados(List<ReporteModificacion> reportesFiltrados) {
        this.reportesFiltrados = reportesFiltrados;
    }

    /**
     * @return the reportesDataTable
     */
    public UIData getReportesDataTable() {
        return reportesDataTable;
    }

    /**
     * @param reportesDataTable the reportesDataTable to set
     */
    public void setReportesDataTable(UIData reportesDataTable) {
        this.reportesDataTable = reportesDataTable;
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
     * @return the titular
     */
    public String getTitular() {
        return titular;
    }

    /**
     * @param titular the titular to set
     */
    public void setTitular(String titular) {
        this.titular = titular;
    }

    /**
     * @return the tipobusqueda
     */
    public String getTipobusqueda() {
        return tipobusqueda;
    }

    /**
     * @param tipobusqueda the tipobusqueda to set
     */
    public void setTipobusqueda(String tipobusqueda) {
        this.tipobusqueda = tipobusqueda;
    }
}
