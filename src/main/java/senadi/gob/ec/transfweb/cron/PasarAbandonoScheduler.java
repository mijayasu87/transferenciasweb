/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.cron;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.transaction.Transactional;
import senadi.gob.ec.transfweb.model.Abandono;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.model.Prorroga;
import senadi.gob.ec.transfweb.model.cd.CambioDomicilio;
import senadi.gob.ec.transfweb.model.cn.CambioNombre;
import senadi.gob.ec.transfweb.model.licencia.LicenciaUso;
import senadi.gob.ec.transfweb.model.licencia.SubLicenciaUso;
import senadi.gob.ec.transfweb.model.prenda.PrendaComercial;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author michael
 */
@Singleton
@Startup // para que se inicie con el servidor
public class PasarAbandonoScheduler {

    @Schedule(hour = "1", minute = "1", second = "0", persistent = false)
    @Transactional
    public void moverNotificacionesVencidas() {
        System.out.println("🕒 Scheduler ejecutado: " + Operaciones.getCurrentTimeStamp());

        int erjafe = 61;
        int reglamento = 10;
        int coa = 10;

        createAbandonosTransferencia(erjafe, reglamento, coa);
        createAbandonosCambioNombre(erjafe, coa, reglamento);
        createAbandonosCambioDomicilio(erjafe, coa, reglamento);
        createAbandonosPrendaComercial(erjafe, coa, reglamento);
        createAbandonosLicenciaUso(erjafe, coa, reglamento);
        createAbandonosSubLicenciaUso(erjafe, coa, reglamento);

        createProrrogasCambioDomicilio();
        createProrrogasCambioNombre();
        createProrrogasPrendaComercial();
        createProrrogasLicenciaUso();
        createProrrogasSubLicenciaUso();
        createProrrogasTransferencia();
    }

    public void createProrrogasTransferencia() {
        Controlador c = new Controlador();
        List<Notificacion> candidatas = c.getProrrogasCandidatasTransf();
        for (int i = 0; i < candidatas.size(); i++) {
            Notificacion notaux = candidatas.get(i);
            if (notaux.getFechaPuestaProrroga() == null || notaux.getDiasProrroga() == null) {
                continue;
            }
            LocalDate fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(notaux.getFechaPuestaProrroga(), notaux.getDiasProrroga());
            if (!LocalDate.now().isBefore(fechaLimite)) {
                Prorroga prorroga = new Prorroga();
                prorroga.setSolicitud(notaux.getSolicitud());
                prorroga.setFechaPresentacion(notaux.getFechaPresentacion());
                prorroga.setNotificacion(notaux.getNotificacion());
                prorroga.setFechaNotificacion(notaux.getFechaNotificacion());
                prorroga.setRegistro(notaux.getRegistro());
                prorroga.setFechaRegistro(notaux.getFechaRegistro());
                prorroga.setDenominacion(notaux.getDenominacion());
                prorroga.setSigno(notaux.getSigno());
                prorroga.setTitularAnterior(notaux.getTitularAnterior());
                prorroga.setTitularActual(notaux.getTitularActual());
                prorroga.setApeApodRepre(notaux.getApeApodRepre());
                prorroga.setRo(notaux.getRo());
                prorroga.setCasilleroSenadi(notaux.getCasilleroSenadi());
                prorroga.setCasilleroJudicial(notaux.getCasilleroJudicial());
                prorroga.setResponsable(notaux.getResponsable());
                prorroga.setIdentificacion(notaux.getIdentificacion());
                prorroga.setDomicilioTitularActual(notaux.getDomicilioTitularActual());
                prorroga.setFechaElaboraNotificacion(notaux.getFechaElaboraNotificacion());
                prorroga.setEmail(notaux.getEmail());
                prorroga.setFechaCertificado(notaux.getFechaCertificado());
                prorroga.setComprobante(notaux.getComprobante());
                prorroga.setCertificado(notaux.getCertificado());
                prorroga.setCertificadoEmitido(notaux.isCertificadoEmitido());
                prorroga.setNotificacionEmitida(notaux.isNotificacionEmitida());
                prorroga.setCancelado(notaux.getCancelado());
                prorroga.setSolicitante(notaux.getSolicitante());
                prorroga.setFechaPuestaProrroga(notaux.getFechaPuestaProrroga());
                prorroga.setDiasProrroga(notaux.getDiasProrroga());
                prorroga.setNumeroAlcance(notaux.getNumeroAlcance());
                prorroga.setFechaAlcance(notaux.getFechaAlcance());
                prorroga.setFechaProrroga(new Date());
                prorroga.setNumeroProrroga(c.getNextNumeroProrrogaTransf(new Date()));
                if (!c.saveProrroga(prorroga)) {
                    System.out.println("No se pudo pasar la notificación transf " + notaux.getSolicitud() + " a prórroga");
                    return;
                } else {
                    if (c.removeNotificacion(notaux)) {
                        c.saveHistorial("PRORROGA", "NOTIFICADAS", prorroga.getSolicitud(), "PASADO A", 0, "modificaciones");
                    }
                }
            }
        }
    }

    public void createProrrogasLicenciaUso() {
        Controlador c = new Controlador();
        List<LicenciaUso> candidatas = c.getProrrogasCandidatasLicencia();
        for (int i = 0; i < candidatas.size(); i++) {
            LicenciaUso notaux = candidatas.get(i);
            if (notaux.getFechaPuestaProrroga() == null || notaux.getDiasProrroga() == null) {
                continue;
            }
            LocalDate fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(notaux.getFechaPuestaProrroga(), notaux.getDiasProrroga());
            if (!LocalDate.now().isBefore(fechaLimite)) {
                notaux.setTipoEstado("PRORROGA");
                notaux.setFechaProrroga(new Date());
                notaux.setNumeroProrroga(c.getNextNumeroProrrogaLicencia(new Date()));
                if (c.updateLicenciaUso(notaux)) {
                    c.saveHistorial("PRORROGA", "NOTIFICADAS", notaux.getSolicitud(), "PASADO A", 0, "modificaciones");
                } else {
                    System.out.println("No se pudo pasar la notificación licencia " + notaux.getSolicitud() + " a prórroga");
                    return;
                }
            }
        }
    }

    public void createProrrogasSubLicenciaUso() {
        Controlador c = new Controlador();
        List<SubLicenciaUso> candidatas = c.getProrrogasCandidatasSublicencia();
        for (int i = 0; i < candidatas.size(); i++) {
            SubLicenciaUso notaux = candidatas.get(i);
            if (notaux.getFechaPuestaProrroga() == null || notaux.getDiasProrroga() == null) {
                continue;
            }
            LocalDate fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(notaux.getFechaPuestaProrroga(), notaux.getDiasProrroga());
            if (!LocalDate.now().isBefore(fechaLimite)) {
                notaux.setTipoEstado("PRORROGA");
                notaux.setFechaProrroga(new Date());
                notaux.setNumeroProrroga(c.getNextNumeroProrrogaSublicencia(new Date()));
                if (c.updateSublicenciaUso(notaux)) {
                    c.saveHistorial("PRORROGA", "NOTIFICADAS", notaux.getSolicitud(), "PASADO A", 0, "modificaciones");
                } else {
                    System.out.println("No se pudo pasar la notificación sublicencia " + notaux.getSolicitud() + " a prórroga");
                    return;
                }
            }
        }
    }

    public void createProrrogasPrendaComercial() {
        Controlador c = new Controlador();
        List<PrendaComercial> candidatas = c.getProrrogasCandidatasPrenda();
        for (int i = 0; i < candidatas.size(); i++) {
            PrendaComercial notaux = candidatas.get(i);
            if (notaux.getFechaPuestaProrroga() == null || notaux.getDiasProrroga() == null) {
                continue;
            }
            LocalDate fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(notaux.getFechaPuestaProrroga(), notaux.getDiasProrroga());
            if (!LocalDate.now().isBefore(fechaLimite)) {
                notaux.setTipoEstado("PRORROGA");
                notaux.setFechaProrroga(new Date());
                notaux.setNumeroProrroga(c.getNextNumeroProrrogaPrenda(new Date()));
                if (c.updatePrendaComercial(notaux)) {
                    c.saveHistorial("PRORROGA", "NOTIFICADAS", notaux.getSolicitud(), "PASADO A", 0, "modificaciones");
                } else {
                    System.out.println("No se pudo pasar la notificación prenda " + notaux.getSolicitud() + " a prórroga");
                    return;
                }
            }
        }
    }

    public void createProrrogasCambioDomicilio() {
        Controlador c = new Controlador();
        List<CambioDomicilio> candidatas = c.getProrrogasCandidatasCD();
        for (int i = 0; i < candidatas.size(); i++) {
            CambioDomicilio notaux = candidatas.get(i);
            if (notaux.getFechaPuestaProrroga() == null || notaux.getDiasProrroga() == null) {
                continue;
            }
            LocalDate fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(notaux.getFechaPuestaProrroga(), notaux.getDiasProrroga());
            // Se pasa a prórroga cuando ya se cumplió el plazo (hoy no es anterior a la fecha límite)
            if (!LocalDate.now().isBefore(fechaLimite)) {
                notaux.setTipoEstado("PRORROGA");
                notaux.setFechaProrroga(new Date());
                notaux.setNumeroProrroga(c.getNextNumeroProrrogaCD(new Date()));
                if (c.updateCambioDomicilio(notaux)) {
                    c.saveHistorial("PRORROGA", "NOTIFICADAS", notaux.getSolicitud(), "PASADO A", 0, "modificaciones");
                } else {
                    System.out.println("No se pudo pasar la notificación cd " + notaux.getSolicitud() + " a prórroga");
                    return;
                }
            }
        }
    }

    public void createProrrogasCambioNombre() {
        Controlador c = new Controlador();
        List<CambioNombre> candidatas = c.getProrrogasCandidatasCN();
        for (int i = 0; i < candidatas.size(); i++) {
            CambioNombre notaux = candidatas.get(i);
            if (notaux.getFechaPuestaProrroga() == null || notaux.getDiasProrroga() == null) {
                continue;
            }
            LocalDate fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(notaux.getFechaPuestaProrroga(), notaux.getDiasProrroga());
            if (!LocalDate.now().isBefore(fechaLimite)) {
                notaux.setTipoEstado("PRORROGA");
                notaux.setFechaProrroga(new Date());
                notaux.setNumeroProrroga(c.getNextNumeroProrrogaCN(new Date()));
                if (c.updateCambioNombre(notaux)) {
                    c.saveHistorial("PRORROGA", "NOTIFICADAS", notaux.getSolicitud(), "PASADO A", 0, "modificaciones");
                } else {
                    System.out.println("No se pudo pasar la notificación cn " + notaux.getSolicitud() + " a prórroga");
                    return;
                }
            }
        }
    }

    public void createAbandonosSubLicenciaUso(int erjafe, int coa, int reglamento) {
        Controlador c = new Controlador();
        List<SubLicenciaUso> cambios = c.getAbandonosSublicErjafeVencidos(erjafe);
        createAbandonosSubLicencia(cambios, "ERJAFE");
        cambios = c.getAbandonosSublicSinFinesSemana(coa, "COA");
        createAbandonosSubLicencia(cambios, "COA");
        cambios = c.getAbandonosSublicSinFinesSemana(reglamento, "REGLAMENTO");
        createAbandonosSubLicencia(cambios, "REGLAMENTO");
    }

    public void createAbandonosLicenciaUso(int erjafe, int coa, int reglamento) {
        Controlador c = new Controlador();
        List<LicenciaUso> cambios = c.getAbandonosLicenciaErjafeVencidos(erjafe);
        createAbandonosLicencia(cambios, "ERJAFE");
        cambios = c.getAbandonosLicenciaSinFinesSemana(coa, "COA");
        createAbandonosLicencia(cambios, "COA");
        cambios = c.getAbandonosLicenciaSinFinesSemana(reglamento, "REGLAMENTO");
        createAbandonosLicencia(cambios, "REGLAMENTO");
    }

    public void createAbandonosPrendaComercial(int erjafe, int coa, int reglamento) {
        Controlador c = new Controlador();
        List<PrendaComercial> cambios = c.getAbandonosPrendaErjafeVencidos(erjafe);
        createAbandonosPrenda(cambios, "ERJAFE");
        cambios = c.getAbandonosPrendaSinFinesSemana(coa, "COA");
        createAbandonosPrenda(cambios, "COA");
        cambios = c.getAbandonosPrendaSinFinesSemana(reglamento, "REGLAMENTO");
        createAbandonosPrenda(cambios, "REGLAMENTO");
    }

    public void createAbandonosCambioDomicilio(int erjafe, int coa, int reglamento) {
        Controlador c = new Controlador();
        List<CambioDomicilio> cambios = c.getAbandonosCDErjafeVencidos(erjafe);
        createAbandonosCD(cambios, "ERJAFE");
        cambios = c.getAbandonosCDSinFinesSemana(coa, "COA");
        createAbandonosCD(cambios, "COA");
        cambios = c.getAbandonosCDSinFinesSemana(reglamento, "REGLAMENTO");
        createAbandonosCD(cambios, "REGLAMENTO");
    }

    public void createAbandonosCambioNombre(int erjafe, int coa, int reglamento) {
        Controlador c = new Controlador();
        List<CambioNombre> cambios = c.getAbandonosCNErjafeVencidos(erjafe);
        createAbandonosCN(cambios, "ERJAFE");
        cambios = c.getAbandonosCNSinFinesSemana(coa, "COA");
        createAbandonosCN(cambios, "COA");
        cambios = c.getAbandonosCNSinFinesSemana(reglamento, "REGLAMENTO");
        createAbandonosCN(cambios, "REGLAMENTO");
    }

    public void createAbandonosTransferencia(int erjafe, int reglamento, int coa) {
        Controlador c = new Controlador();
        List<Notificacion> notificaciones = c.getAbandonosErjafeVencidos(erjafe);
        createAbandonos(notificaciones, "ERJAFE");
        notificaciones = c.getAbandonosSinFinesSemana(coa, "COA");
        createAbandonos(notificaciones, "COA");
        notificaciones = c.getAbandonosSinFinesSemana(reglamento, "REGLAMENTO");
        createAbandonos(notificaciones, "REGLAMENTO");
    }

    public void createAbandonosCD(List<CambioDomicilio> notificaciones, String type) {
        Controlador c = new Controlador();
        int n = 0;
        for (int i = 0; i < notificaciones.size(); i++) {
            CambioDomicilio notaux = notificaciones.get(i);
            notaux.setTipoAbandono(type);
            notaux.setTipoEstado("ABANDONO");
            notaux.setFechaAbandono(new Date());
            notaux.setNumeroAbandono(c.getNextNumeroAbandonoCD(new Date()));
            if (c.updateCambioDomicilio(notaux)) {
                n++;
            } else {
                System.out.println("No se pudo pasar la notificación cd " + notaux.getSolicitud() + " (" + type + ") a abandono");
                return;
            }
        }
    }

    public void createAbandonosCN(List<CambioNombre> notificaciones, String type) {
        Controlador c = new Controlador();
        int n = 0;
        for (int i = 0; i < notificaciones.size(); i++) {
            CambioNombre notaux = notificaciones.get(i);
            notaux.setTipoAbandono(type);
            notaux.setTipoEstado("ABANDONO");
            notaux.setFechaAbandono(new Date());
            notaux.setNumeroAbandono(c.getNextNumeroAbandonoCN(new Date()));
            if (c.updateCambioNombre(notaux)) {
                n++;
            } else {
                System.out.println("No se pudo pasar la notificación cn " + notaux.getSolicitud() + " (" + type + ") a abandono");
                return;
            }
        }
    }

    public void createAbandonosPrenda(List<PrendaComercial> notificaciones, String type) {
        Controlador c = new Controlador();
        int n = 0;
        for (int i = 0; i < notificaciones.size(); i++) {
            PrendaComercial notaux = notificaciones.get(i);
            notaux.setTipoAbandono(type);
            notaux.setTipoEstado("ABANDONO");
            notaux.setFechaAbandono(new Date());
            notaux.setNumeroAbandono(c.getNextNumeroAbandonoPrenda(new Date()));
            if (c.updatePrendaComercial(notaux)) {
                n++;
            } else {
                System.out.println("No se pudo pasar la notificación prenda " + notaux.getSolicitud() + " (" + type + ") a abandono");
                return;
            }
        }
    }

    public void createAbandonosLicencia(List<LicenciaUso> notificaciones, String type) {
        Controlador c = new Controlador();
        int n = 0;
        for (int i = 0; i < notificaciones.size(); i++) {
            LicenciaUso notaux = notificaciones.get(i);
            notaux.setTipoAbandono(type);
            notaux.setTipoEstado("ABANDONO");
            notaux.setFechaAbandono(new Date());
            notaux.setNumeroAbandono(c.getNextNumeroAbandonoLicencia(new Date()));
            if (c.updateLicenciaUso(notaux)) {
                n++;
            } else {
                System.out.println("No se pudo pasar la notificación licencia " + notaux.getSolicitud() + " (" + type + ") a abandono");
                return;
            }
        }
    }

    public void createAbandonosSubLicencia(List<SubLicenciaUso> notificaciones, String type) {
        Controlador c = new Controlador();
        int n = 0;
        for (int i = 0; i < notificaciones.size(); i++) {
            SubLicenciaUso notaux = notificaciones.get(i);
            notaux.setTipoAbandono(type);
            notaux.setTipoEstado("ABANDONO");
            notaux.setFechaAbandono(new Date());
            notaux.setNumeroAbandono(c.getNextNumeroAbandonoSublicencia(new Date()));
            if (c.updateSublicenciaUso(notaux)) {
                n++;
            } else {
                System.out.println("No se pudo pasar la notificación subliencia " + notaux.getSolicitud() + " (" + type + ") a abandono");
                return;
            }
        }
    }

    public void createAbandonos(List<Notificacion> notificaciones, String type) {
        Controlador c = new Controlador();
        int n = 0;
        for (int i = 0; i < notificaciones.size(); i++) {
            Notificacion notaux = notificaciones.get(i);
            Abandono abandono = new Abandono();
            abandono.setSolicitud(notaux.getSolicitud().toUpperCase());
            abandono.setFechaPresentacion(notaux.getFechaPresentacion());
            abandono.setFechaAbandono(new Date());
            abandono.setNumeroAbandono(c.getNextNumeroAbandono(abandono.getFechaAbandono()));

            abandono.setFechaElaboraNotificacion(notaux.getFechaNotificacion());
            abandono.setNotificacion(notaux.getNotificacion());
            abandono.setFechaNotificacion(notaux.getFechaNotificacion());
            abandono.setRegistro(notaux.getRegistro());
            abandono.setFechaRegistro(notaux.getFechaRegistro());
            abandono.setDenominacion(notaux.getDenominacion());
            abandono.setSigno(notaux.getSigno());
            abandono.setTitularAnterior(notaux.getTitularAnterior());
            abandono.setTitularActual(notaux.getTitularActual());
            abandono.setApeApodRepre(notaux.getApeApodRepre());
            abandono.setRo(notaux.getRo());
            abandono.setCasilleroSenadi(notaux.getCasilleroSenadi());
            abandono.setCasilleroJudicial(notaux.getCasilleroJudicial());
            abandono.setResponsable(notaux.getResponsable());
            abandono.setIdentificacion(notaux.getIdentificacion());
            abandono.setCertificado(notaux.getCertificado() + "");
            abandono.setFechaCertificado(notaux.getFechaCertificado());
            abandono.setDomicilioTitularActual(notaux.getDomicilioTitularActual());
            abandono.setComprobante(notaux.getComprobante());
            abandono.setCertificadoEmitido(notaux.isCertificadoEmitido());
            abandono.setNotificacionEmitida(notaux.isNotificacionEmitida());
            abandono.setSolicitante(notaux.getSolicitante());
            abandono.setCancelado(notaux.getCancelado());

            abandono.setTipoAbandono(type);
            if (!c.saveAbandono(abandono)) {
                System.out.println("No se pudo pasar la notificación transf " + abandono.getSolicitud() + " (" + type + ") a abandono");
                return;
            } else {
                if (c.removeNotificacion(notaux)) {
                    c.saveHistorial("ABANDONO", "NOTIFICADAS", abandono.getSolicitud(), "PASADO A", 0, "modificaciones");
                    n++;
                }
            }
        }
    }
}
