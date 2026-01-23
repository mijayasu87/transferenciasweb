/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.util;

import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import senadi.gob.ec.transfweb.bean.LoginBean;
import senadi.gob.ec.transfweb.dao.AbandonoDAO;
import senadi.gob.ec.transfweb.dao.CaducadaDAO;
import senadi.gob.ec.transfweb.dao.CambioCasilleroDAO;
import senadi.gob.ec.transfweb.dao.DelegacionDAO;
import senadi.gob.ec.transfweb.dao.DelegadoDAO;
import senadi.gob.ec.transfweb.dao.DesistimientoDAO;
import senadi.gob.ec.transfweb.dao.HistorialDAO;
import senadi.gob.ec.transfweb.dao.ModificationScopeDAO;
import senadi.gob.ec.transfweb.dao.NotificacionDAO;
import senadi.gob.ec.transfweb.dao.ResolucionDAO;
import senadi.gob.ec.transfweb.dao.RooptionsDAO;
import senadi.gob.ec.transfweb.dao.TituloCanceladoDAO;
import senadi.gob.ec.transfweb.dao.TransferenciaDAO;
import senadi.gob.ec.transfweb.dao.UploadNotificacionDAO;
import senadi.gob.ec.transfweb.dao.cd.CambioDomicilioDAO;
import senadi.gob.ec.transfweb.dao.cn.CambioNombreDAO;
import senadi.gob.ec.transfweb.dao.licencia.LicenciaUsoDAO;
import senadi.gob.ec.transfweb.dao.licencia.SublicenciaUsoDAO;
import senadi.gob.ec.transfweb.dao.prenda.PrendaComercialDAO;
import senadi.gob.ec.transfweb.daop.PpdiModificacionDAO;
import senadi.gob.ec.transfweb.modelp.PpdiPersona;
import senadi.gob.ec.transfweb.daop.PpdiPersonaSolicitudDAO;
import senadi.gob.ec.transfweb.modelp.PpdiPersonaSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.daop.PpdiSignoDAO;
import senadi.gob.ec.transfweb.daop.PpdiSolicitudPatenteDAO;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.daop.PpdiTituloDAO;
import senadi.gob.ec.transfweb.daop.PpdiTituloPatenteDAO;
import senadi.gob.ec.transfweb.model.Abandono;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import senadi.gob.ec.transfweb.model.Caducada;
import senadi.gob.ec.transfweb.model.CambioCasillero;
import senadi.gob.ec.transfweb.model.Delegacion;
import senadi.gob.ec.transfweb.model.Delegado;
import senadi.gob.ec.transfweb.model.Desistimiento;
import senadi.gob.ec.transfweb.model.Historial;
import senadi.gob.ec.transfweb.model.ModificationScope;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.model.Resolucion;
import senadi.gob.ec.transfweb.model.Rooptions;
import senadi.gob.ec.transfweb.model.TituloCancelado;
import senadi.gob.ec.transfweb.model.Transferencia;
import senadi.gob.ec.transfweb.model.UploadNotificacion;
import senadi.gob.ec.transfweb.model.cd.CambioDomicilio;
import senadi.gob.ec.transfweb.model.cn.CambioNombre;
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepdep.PatentForms;
import senadi.gob.ec.transfweb.model.iepform.FormTypes;
import senadi.gob.ec.transfweb.model.iepform.ModificacionApp;
import senadi.gob.ec.transfweb.model.iepform.ModificacionDAO;
import senadi.gob.ec.transfweb.model.iepform.PaymentReceipt;
import senadi.gob.ec.transfweb.model.iepform.Person;
import senadi.gob.ec.transfweb.model.iepform.PersonRenewal;
import senadi.gob.ec.transfweb.model.iepform.PersonRenewalName;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepform.RenewalFormDAO;
import senadi.gob.ec.transfweb.model.iepform.Types;
import senadi.gob.ec.transfweb.model.iepiadm.Cpis;
import senadi.gob.ec.transfweb.model.iepiadm.CpisDAO;
import senadi.gob.ec.transfweb.model.iepicas.NotificationsDAO;
import senadi.gob.ec.transfweb.model.iepicas.Owner;
import senadi.gob.ec.transfweb.model.iepicas.OwnerDAO;
import senadi.gob.ec.transfweb.model.licencia.LicenciaUso;
import senadi.gob.ec.transfweb.model.licencia.SubLicenciaUso;
import senadi.gob.ec.transfweb.model.prenda.PrendaComercial;
import senadi.gob.ec.transfweb.modelp.PpdiResolucion;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudModificacion;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudPatente;
import senadi.gob.ec.transfweb.modelp.PpdiTitAndSigno;
import senadi.gob.ec.transfweb.modelp.PpdiTituloPatente;
import senadi.gob.ec.transfweb.modpat.CambioDomicilioPat;
import senadi.gob.ec.transfweb.modpat.CambioNombrePat;
import senadi.gob.ec.transfweb.modpat.HistorialPat;
import senadi.gob.ec.transfweb.modpat.LicenciaUsoPat;
import senadi.gob.ec.transfweb.modpat.SubLicenciaUsoPat;
import senadi.gob.ec.transfweb.modpat.TransferenciaPat;
import senadi.gob.ec.transfweb.modpat.dao.CambioDomicilioPatDAO;
import senadi.gob.ec.transfweb.modpat.dao.CambioNombrePatDAO;
import senadi.gob.ec.transfweb.modpat.dao.HistorialPatDAO;
import senadi.gob.ec.transfweb.modpat.dao.LicenciaUsoPatDAO;
import senadi.gob.ec.transfweb.modpat.dao.SublicenciaUsoPatDAO;
import senadi.gob.ec.transfweb.modpat.dao.TransferenciaPatDAO;
import senadi.gob.ec.transfweb.renova.dao.CaducadaRDAO;
import senadi.gob.ec.transfweb.renova.dao.DesistidaRDAO;
import senadi.gob.ec.transfweb.renova.dao.NotificadaRDAO;
import senadi.gob.ec.transfweb.renova.dao.RenovacionDAO;
import senadi.gob.ec.transfweb.renova.model.CaducadaRen;
import senadi.gob.ec.transfweb.renova.model.Desistida;
import senadi.gob.ec.transfweb.renova.model.Notificada;
import senadi.gob.ec.transfweb.renova.model.Renovacion;

/**
 *
 * @author Michael
 */
public class Controlador {

    public void banderaCertificadosCDEmitidos() {
        CambioDomicilioDAO rd = new CambioDomicilioDAO(null);
        List<String[]> cambiosDomicilio = rd.getCambiosDomicilioCertificadoEmitido();

        for (int i = 0; i < cambiosDomicilio.size(); i++) {
            String[] aux = cambiosDomicilio.get(i);

            CambioDomicilio cambioDomicilio = getCambioDomicilioBySolicitud(aux[0]);
            int confirm = Operaciones.validaTextoEnPdf(aux[1], "CERTIFICADO DE CAMBIO DE DOMICILIO");
            System.out.println(confirm + ": " + aux[1]);
            if (confirm == 1) {
                cambioDomicilio.setCertificadoEmitido(true);
            } else if (confirm == 0) {
                cambioDomicilio.setNotificacionEmitida(true);
            }

            if (confirm == 1 || confirm == 0) {
                rd = new CambioDomicilioDAO(cambioDomicilio);
                try {
                    rd.update();
                    System.out.println(", " + i + ": " + cambioDomicilio.getSolicitud() + " editado");
                } catch (Exception ex) {
                    Logger.getLogger(TransferenciaDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        System.out.println("--------------------Evento terminado cambios-domicilio-----------------");
    }

    public void banderaCertificadosCNEmitidos() {
        CambioNombreDAO rd = new CambioNombreDAO(null);
        List<String[]> cambiosNombre = rd.getCambiosNombreCertificadoEmitido();

        for (int i = 0; i < cambiosNombre.size(); i++) {
            String[] aux = cambiosNombre.get(i);

            CambioNombre cambioNombre = getCambioNombreBySolicitud(aux[0]);
            int confirm = Operaciones.validaTextoEnPdf(aux[1], "CERTIFICADO DE CAMBIO DE NOMBRE DEL");
            System.out.print(confirm + ": " + aux[1]);
            if (confirm == 1) {
                cambioNombre.setCertificadoEmitido(true);
            } else if (confirm == 0) {
                cambioNombre.setNotificacionEmitida(true);
            }

            if (confirm == 1 || confirm == 0) {
                rd = new CambioNombreDAO(cambioNombre);
                try {
                    rd.update();
                    System.out.println(", " + i + ": " + cambioNombre.getSolicitud() + " editado");
                } catch (Exception ex) {
                    Logger.getLogger(TransferenciaDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        System.out.println("--------------------Evento terminado cambios-nombre-----------------");
    }

    public void banderaCertificadosEmitidos() {
        TransferenciaDAO rd = new TransferenciaDAO(null);
        List<String[]> transferencias = rd.getTransferenciasCertificadoEmitido();

        for (int i = 0; i < transferencias.size(); i++) {
            String[] aux = transferencias.get(i);

            Transferencia transferencia = getTransferenciaBySolSenadi(aux[0]);
            int confirm = Operaciones.validaTextoEnPdf(aux[1], "CERTIFICADO DE TRANSFERENCIA No.");
            System.out.print(confirm + ": " + aux[1]);
            if (confirm == 1) {
                transferencia.setCertificadoEmitido(true);
            } else if (confirm == 0) {
                transferencia.setNotificacionEmitida(true);
            }

            if (confirm == 1 || confirm == 0) {
                rd = new TransferenciaDAO(transferencia);
                try {
                    rd.update();
                    System.out.println(", " + i + ": " + transferencia.getSolicitud() + " editado");
                } catch (Exception ex) {
                    Logger.getLogger(TransferenciaDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        System.out.println("--------------------Evento terminado notificadas-----------------");
    }

    public void banderaNotificacionesEmitidos() {
        NotificacionDAO rd = new NotificacionDAO(null);
        List<String[]> transferencias = rd.getNotificadasEmitido();

        for (int i = 0; i < transferencias.size(); i++) {
            String[] aux = transferencias.get(i);

            Notificacion notificacion = getNotificacionBySolSenadi(aux[0]);
            int confirm = Operaciones.validaTextoEnPdf(aux[1], "CERTIFICADO DE TRANSFERENCIA No.");
            System.out.println(confirm + ": " + aux[1]);
            if (confirm == 1) {
                notificacion.setCertificadoEmitido(true);
            } else if (confirm == 0) {
                notificacion.setNotificacionEmitida(true);
            }

            if (confirm == 1 || confirm == 0) {
                rd = new NotificacionDAO(notificacion);
                try {
                    rd.update();
                    System.out.println(", " + i + ": " + notificacion.getSolicitud() + " editado");
                } catch (Exception ex) {
                    Logger.getLogger(NotificacionDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        System.out.println("--------------------Evento terminado notificadas-----------------");
    }

    public String buscarCasilleroBySolicitud(String solicitud) {
        RenewalForm aux = getRenewalFormsByApplicationNumber(solicitud);
        String casillero = "";
        if (aux.getId() != null) {
            casillero = getCasilleroSenadiByOwnerId(aux.getOwnerId()) + "";
        }
        return casillero;

    }

    public List<Transferencia> getTransferencias() {
        TransferenciaDAO td = new TransferenciaDAO(null);
        return td.buscarTodos();
    }

    public List<Transferencia> getAllTransferencias() {
        TransferenciaDAO td = new TransferenciaDAO(null);
        return td.getAllTransferencias();
    }

    public List<Notificacion> getNotificaciones() {
        NotificacionDAO nd = new NotificacionDAO(null);
        return nd.buscarTodos();
    }

    public List<Abandono> getAbandonos() {
        AbandonoDAO nd = new AbandonoDAO(null);
        return nd.buscarTodos();
    }

    public List<Desistimiento> getDesistimientos() {
        DesistimientoDAO rd = new DesistimientoDAO(null);
        return rd.buscarTodos();
    }

    public List<Caducada> getCaducadas() {
        CaducadaDAO rd = new CaducadaDAO(null);
        return rd.buscarTodos();
    }

    public boolean saveCambioDomicilio(CambioDomicilio cambioDomicilio) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(cambioDomicilio);
        try {
            cd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar cambio_domicilio: " + ex);
            return false;
        }
    }

    public boolean saveCambioNombre(CambioNombre cambioNombre) {
        CambioNombreDAO cd = new CambioNombreDAO(cambioNombre);
        try {
            cd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar cambio_nombre: " + ex);
            return false;
        }
    }

    public boolean saveCambioNombrePat(CambioNombrePat cambioNombre) {
        CambioNombrePatDAO cd = new CambioNombrePatDAO(cambioNombre);
        try {
            cd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar cambio_nombre_pat: " + ex);
            return false;
        }
    }

    public boolean saveLicenciaUsoPat(LicenciaUsoPat licenciaUso) {
        LicenciaUsoPatDAO ld = new LicenciaUsoPatDAO(licenciaUso);
        try {
            ld.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar licencia_uso_pat: " + ex);
            return false;
        }
    }

    public boolean saveSublicenciaUsoPat(SubLicenciaUsoPat sublicenciaUso) {
        SublicenciaUsoPatDAO ld = new SublicenciaUsoPatDAO(sublicenciaUso);
        try {
            ld.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar sublicencia_uso_pat: " + ex);
            return false;
        }
    }

    public boolean saveCambioDomicilioPat(CambioDomicilioPat cambioDomicilio) {
        CambioDomicilioPatDAO cd = new CambioDomicilioPatDAO(cambioDomicilio);
        try {
            cd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar cambio_domicilio_pat: " + ex);
            return false;
        }
    }

    public boolean saveSublicenciaUso(SubLicenciaUso licencia) {
        SublicenciaUsoDAO pd = new SublicenciaUsoDAO(licencia);
        try {
            pd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar sublicencia_uso: " + ex);
            return false;
        }
    }

    public boolean saveLicenciaUso(LicenciaUso licencia) {
        LicenciaUsoDAO pd = new LicenciaUsoDAO(licencia);
        try {
            pd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar licencia_uso: " + ex);
            return false;
        }
    }

    public boolean savePrendaComercial(PrendaComercial prendaComercial) {
        PrendaComercialDAO pd = new PrendaComercialDAO(prendaComercial);
        try {
            pd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar prenda_comercial: " + ex);
            return false;
        }
    }

    public boolean saveTransferencia(Transferencia transferencia) {
        TransferenciaDAO td = new TransferenciaDAO(transferencia);
        try {
            td.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar transferencia: " + ex);
            return false;
        }
    }

    public boolean saveTransferenciaPat(TransferenciaPat transferencia) {
        TransferenciaPatDAO td = new TransferenciaPatDAO(transferencia);
        try {
            td.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar transferencia patente: " + ex);
            return false;
        }
    }

    public boolean saveNotificacion(Notificacion notificacion) {
        NotificacionDAO nd = new NotificacionDAO(notificacion);
        try {
            nd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar notificacion: " + ex);
            return false;
        }
    }

    public boolean saveAbandono(Abandono abandono) {
        AbandonoDAO nd = new AbandonoDAO(abandono);
        try {
            nd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar abandono: " + ex);
            return false;
        }
    }

    public boolean saveDesistimiento(Desistimiento desistimiento) {
        DesistimientoDAO rd = new DesistimientoDAO(desistimiento);
        try {
            rd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar desistimiento: " + ex);
            return false;
        }
    }

    public boolean saveCaducada(Caducada caducada) {
        CaducadaDAO rd = new CaducadaDAO(caducada);
        try {
            rd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar caducada: " + ex);
            return false;
        }
    }

    public boolean updateCambioDomicilio(CambioDomicilio certificado) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(certificado);
        try {
            cd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar cambio_domicilio: " + ex);
            return false;
        }
    }

    public boolean updateCambioNombre(CambioNombre certificado) {
        CambioNombreDAO cd = new CambioNombreDAO(certificado);
        try {
            cd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar cambio_nombre: " + ex);
            return false;
        }
    }

    public boolean updateSublicenciaUso(SubLicenciaUso sublicencia) {
        SublicenciaUsoDAO ld = new SublicenciaUsoDAO(sublicencia);
        try {
            ld.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar sublicencia_uso: " + ex);
            return false;
        }
    }

    public boolean updateLicenciaUso(LicenciaUso licencia) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(licencia);
        try {
            ld.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar licencia_uso: " + ex);
            return false;
        }
    }

    public boolean updatePrendaComercial(PrendaComercial certificado) {
        PrendaComercialDAO pd = new PrendaComercialDAO(certificado);
        try {
            pd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar prenda_comercial: " + ex);
            return false;
        }
    }

    public boolean updateTransferencia(Transferencia transferencia) {
        TransferenciaDAO td = new TransferenciaDAO(transferencia);
        try {
            td.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar transferencia: " + ex);
            return false;
        }
    }

    public boolean updateNotificacion(Notificacion notificacion) {
        NotificacionDAO nd = new NotificacionDAO(notificacion);
        try {
            nd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar notificacion: " + ex);
            return false;
        }
    }

    public boolean updateAbandono(Abandono abandono) {
        AbandonoDAO nd = new AbandonoDAO(abandono);
        try {
            nd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar abandono: " + ex);
            return false;
        }
    }

    public boolean updateDesistimiento(Desistimiento desistimiento) {
        DesistimientoDAO rd = new DesistimientoDAO(desistimiento);
        try {
            rd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar desistimiento: " + ex);
            return false;
        }
    }

    public boolean updateCaducada(Caducada caducada) {
        CaducadaDAO rd = new CaducadaDAO(caducada);
        try {
            rd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar caducada: " + ex);
            return false;
        }
    }

    public boolean removeTransferencia(Transferencia transferencia) {
        TransferenciaDAO td = new TransferenciaDAO(transferencia);
        try {
            if (!td.getEntityManager().contains(transferencia)) {
                System.out.println("merge transferencia");
                transferencia = td.getEntityManager().merge(transferencia);
                td = new TransferenciaDAO(transferencia);
            }
            td.remove();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al remover transferencia: " + ex);
            return false;
        }
    }

    public boolean removeAbandono(Abandono abandono) {
        AbandonoDAO ad = new AbandonoDAO(abandono);
        try {
            if (!ad.getEntityManager().contains(abandono)) {
                System.out.println("merge abandono");
                abandono = ad.getEntityManager().merge(abandono);
                ad = new AbandonoDAO(abandono);
            }
            ad.remove();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al remover abandono: " + ex);
            return false;
        }
    }

    public boolean removeCambioDomicilio(CambioDomicilio cambioDomicilio) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(cambioDomicilio);
        try {
            if (!cd.getEntityManager().contains(cambioDomicilio)) {
                System.out.println("merge cambio_domicilio " + cambioDomicilio.getTipoEstado());
                cambioDomicilio = cd.getEntityManager().merge(cambioDomicilio);
                cd = new CambioDomicilioDAO(cambioDomicilio);
            }
            cd.remove();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al remover cambio_domicilio: " + ex);
            return false;
        }
    }

    public boolean removeCambioNombre(CambioNombre cambioNombre) {
        CambioNombreDAO cd = new CambioNombreDAO(cambioNombre);
        try {
            if (!cd.getEntityManager().contains(cambioNombre)) {
                System.out.println("merge cambio_nombre " + cambioNombre.getTipoEstado());
                cambioNombre = cd.getEntityManager().merge(cambioNombre);
                cd = new CambioNombreDAO(cambioNombre);
            }
            cd.remove();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al remover cambio_nombre: " + ex);
            return false;
        }
    }

    public boolean removeSublicenciaUso(SubLicenciaUso sublicencia) {
        SublicenciaUsoDAO ld = new SublicenciaUsoDAO(sublicencia);
        try {
            if (!ld.getEntityManager().contains(sublicencia)) {
                System.out.println("merge sublicencia_uso " + sublicencia.getTipoEstado());
                sublicencia = ld.getEntityManager().merge(sublicencia);
                ld = new SublicenciaUsoDAO(sublicencia);
            }
            ld.remove();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al remover sublicencia_uso: " + ex);
            return false;
        }
    }

    public boolean removeLicenciaUso(LicenciaUso licencia) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(licencia);
        try {
            if (!ld.getEntityManager().contains(licencia)) {
                System.out.println("merge licencia_uso " + licencia.getTipoEstado());
                licencia = ld.getEntityManager().merge(licencia);
                ld = new LicenciaUsoDAO(licencia);
            }
            ld.remove();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al remover licencia_uso: " + ex);
            return false;
        }
    }

    public boolean removePrendaComercial(PrendaComercial prendaComercial) {
        PrendaComercialDAO pd = new PrendaComercialDAO(prendaComercial);
        try {
            if (!pd.getEntityManager().contains(prendaComercial)) {
                System.out.println("merge prenda_comercial " + prendaComercial.getTipoEstado());
                prendaComercial = pd.getEntityManager().merge(prendaComercial);
                pd = new PrendaComercialDAO(prendaComercial);
            }
            pd.remove();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al remover prenda_comercial: " + ex);
            return false;
        }
    }

    public boolean removeNotificacion(Notificacion notificacion) {
        NotificacionDAO nd = new NotificacionDAO(notificacion);
        try {
            if (!nd.getEntityManager().contains(notificacion)) {
                System.out.println("merge notificacion");
                notificacion = nd.getEntityManager().merge(notificacion);
                nd = new NotificacionDAO(notificacion);
            }
            nd.remove();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al remover notificacion: " + ex);
            return false;
        }
    }

    public boolean removeDesistimiento(Desistimiento desistimiento) {
        DesistimientoDAO rd = new DesistimientoDAO(desistimiento);
        try {
            if (!rd.getEntityManager().contains(desistimiento)) {
                System.out.println("merge desistimiento");
                desistimiento = rd.getEntityManager().merge(desistimiento);
                rd = new DesistimientoDAO(desistimiento);
            }
            rd.remove();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al remover desistimiento: " + ex);
            return false;
        }
    }

    public boolean removeCaducada(Caducada caducada) {
        CaducadaDAO rd = new CaducadaDAO(caducada);
        try {
            if (!rd.getEntityManager().contains(caducada)) {
                System.out.println("merge caducada");
                caducada = rd.getEntityManager().merge(caducada);
                rd = new CaducadaDAO(caducada);
            }
            rd.remove();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al remover caducada: " + ex);
            return false;
        }
    }

    public CambioNombre getCambioNombreBySolicitud(String solicitud) {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getCambioNombreBySolicitud(solicitud);
    }

    public SubLicenciaUso getSublicenciaUsoBySolicitud(String solicitud) {
        SublicenciaUsoDAO ld = new SublicenciaUsoDAO(null);
        return ld.getSublicenciaUsoBySolicitud(solicitud);
    }

    public LicenciaUso getLicenciaUsoBySolicitud(String solicitud) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        return ld.getLicenciaUsoBySolicitud(solicitud);
    }

    public PrendaComercial getPrendaComercialBySolicitud(String solicitud) {
        PrendaComercialDAO cd = new PrendaComercialDAO(null);
        return cd.getPrendaComercialBySolicitud(solicitud);
    }

    public CambioDomicilio getCambioDomicilioBySolicitud(String solicitud) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.getCambioDomicilioBySolicitud(solicitud);
    }

    public boolean existeCambioNombre(String numeroTramite) {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.existeTramite(numeroTramite);
    }

    public boolean existePrendaComercial(String numeroTramite) {
        PrendaComercialDAO pd = new PrendaComercialDAO(null);
        return pd.existeTramite(numeroTramite);
    }

    public boolean existeSublicenciaUso(String numeroTramite) {
        SublicenciaUsoDAO ld = new SublicenciaUsoDAO(null);
        return ld.existeTramiteSubLicenciaUso(numeroTramite);
    }

    public boolean existeLicenciaUso(String numeroTramite) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        return ld.existeTramiteLicenciaUso(numeroTramite);
    }

    public boolean existeCambioDomicilio(String numeroTramite) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.existeTramite(numeroTramite);
    }

    public boolean existeTramiteTransferencia(String numeroTramite) {
        TransferenciaDAO td = new TransferenciaDAO(null);
        return td.existeTramite(numeroTramite);
    }

    public boolean existeTramiteNotificacion(String numeroTramite) {
        NotificacionDAO nd = new NotificacionDAO(null);
        return nd.existeTramite(numeroTramite);
    }

    public boolean existeTramiteAbando(String numeroTramite) {
        AbandonoDAO nd = new AbandonoDAO(null);
        return nd.existeTramite(numeroTramite);
    }

    public boolean existeTramiteDesistimiento(String numeroTramite) {
        DesistimientoDAO rd = new DesistimientoDAO(null);
        return rd.existeTramite(numeroTramite);
    }

    public boolean existeTramiteCaducada(String numeroTramite) {
        CaducadaDAO td = new CaducadaDAO(null);
        return td.existeTramite(numeroTramite);
    }

    public boolean validarExistenciaCambioDomicilio(CambioDomicilio cert) {
        CambioDomicilioDAO cn = new CambioDomicilioDAO(null);
        return cn.validarExistenciaCambioDomicilio(cert);
    }

    public boolean validarExistenciaCambioNombre(CambioNombre cert) {
        CambioNombreDAO cn = new CambioNombreDAO(null);
        return cn.validarExistenciaCambioNombre(cert);
    }

    public boolean validarExistenciaLicenciaUso(LicenciaUso licencia) {
        LicenciaUsoDAO cn = new LicenciaUsoDAO(null);
        return cn.validarExistenciaLicenciaUso(licencia);
    }

    public boolean validarExistenciaSublicenciaUso(SubLicenciaUso sublicencia) {
        SublicenciaUsoDAO cn = new SublicenciaUsoDAO(null);
        return cn.validarExistenciaSublicenciaUso(sublicencia);
    }

    public boolean validarExistenciaPrendaComercial(PrendaComercial cert) {
        PrendaComercialDAO cn = new PrendaComercialDAO(null);
        return cn.validarExistenciaPrendaComercial(cert);
    }

    public boolean validarExistenciaTransferencia(Transferencia t) {
        TransferenciaDAO td = new TransferenciaDAO(null);
        return td.validarExistenciaTransferencia(t);
    }

    public boolean validarExistenciaTransferencia(String solicitud) {
        TransferenciaDAO td = new TransferenciaDAO(null);
        return td.validarExistenciaTransferencia(solicitud);
    }

    public boolean validarExistenciaNotificacion(Notificacion n) {
        NotificacionDAO td = new NotificacionDAO(null);
        return td.validarExistenciaNotificacion(n);
    }

    public boolean validarExistenciaAbandono(Abandono n) {
        AbandonoDAO td = new AbandonoDAO(null);
        return td.validarExistenciaAbandono(n);
    }

    public boolean validarExistenciaNotificacion(String solicitud) {
        NotificacionDAO rd = new NotificacionDAO(null);
        return rd.validarExistenciaNotificada(solicitud);
    }

    public boolean validarExistenciaDesistimiento(String solicitud) {
        DesistimientoDAO rd = new DesistimientoDAO(null);
        return rd.validarExistenciaDesistimiento(solicitud);
    }

    public boolean validarExistenciaDesistimiento(Desistimiento r) {
        DesistimientoDAO rd = new DesistimientoDAO(null);
        return rd.validarExistenciaDesistimiento(r);
    }

    public boolean validarExistenciaCaducada(String solicitud) {
        CaducadaDAO rd = new CaducadaDAO(null);
        return rd.validarExistenciaCaducada(solicitud);
    }

    public boolean validarExistenciaCaducada(Caducada r) {
        CaducadaDAO rd = new CaducadaDAO(null);
        return rd.validarExistenciaCaducada(r);
    }

    public List<CambioNombre> getCambiosNombreByCriteriaAndType(String text, String type) {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getCambiosNombreByCriteriaAndType(text, type);
    }

    public List<SubLicenciaUso> getSublicenciasUsoByCriteriaAndType(String text, String type) {
        SublicenciaUsoDAO cd = new SublicenciaUsoDAO(null);
        return cd.getSublicenciasUsoByCriteriaAndType(text, type);
    }

    public List<LicenciaUso> getLicenciasUsoByCriteriaAndType(String text, String type) {
        LicenciaUsoDAO cd = new LicenciaUsoDAO(null);
        return cd.getLicenciasUsoByCriteriaAndType(text, type);
    }

    public List<PrendaComercial> getPrendasComercialesByCriteriaAndType(String text, String type) {
        PrendaComercialDAO cd = new PrendaComercialDAO(null);
        return cd.getPrendasComercialesByCriteriaAndType(text, type);
    }

    public List<CambioDomicilio> getCambiosDomicilioByCriteriaAndType(String text, String type) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.getCambiosDomicilioByCriteriaAndType(text, type);
    }

    public List<Transferencia> getTransferenciaByCriteria(String text) {
        TransferenciaDAO td = new TransferenciaDAO(null);
        return td.getTransferenciaByCriteria(text);
    }

    public List<Notificacion> getNotificaicionByCriteria(String text) {
        NotificacionDAO nd = new NotificacionDAO(null);
        return nd.getNotificacionByCriteria(text);
    }

    public List<Abandono> getAbandonoByCriteria(String text) {
        AbandonoDAO nd = new AbandonoDAO(null);
        return nd.getAbandonoByCriteria(text);
    }

    public List<Desistimiento> getDesistimientosByCriteria(String text) {
        DesistimientoDAO rd = new DesistimientoDAO(null);
        return rd.getDesistimientosByCriteria(text);
    }

    public Caducada getCaducadaBySolSenadi(String solicitud) {
        CaducadaDAO rd = new CaducadaDAO(null);
        return rd.getCaducadasBySolSenadi(solicitud);
    }

    public List<Caducada> getCaducadasByCriteria(String text) {
        CaducadaDAO rd = new CaducadaDAO(null);
        return rd.getCaducadasByCriteria(text);
    }

    public List<CambioDomicilio> getCambiosDomicilioByFechaAndType(Date ini, Date fin, String type) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.getCambiosDomicilioByFechaAndType(ini, fin, type);
    }

    public List<CambioDomicilio> getCambiosDomicilioByFecha(Date ini, Date fin) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.getCambiosDomicilioByFecha(ini, fin);
    }

    public List<CambioNombre> getCambiosNombreByFechaAndType(Date ini, Date fin, String type) {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getCambiosNombreByFechaAndType(ini, fin, type);
    }

    public List<CambioNombre> getCambiosNombreByFecha(Date ini, Date fin) {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getCambiosNombreByFecha(ini, fin);
    }

    public List<SubLicenciaUso> getSublicenciasUsoByFechaAndType(Date ini, Date fin, String type) {
        SublicenciaUsoDAO ld = new SublicenciaUsoDAO(null);
        return ld.getSublicenciasUsoByFechaAndType(ini, fin, type);
    }

    public List<SubLicenciaUso> getSublicenciasUsoByFecha(Date ini, Date fin) {
        SublicenciaUsoDAO sd = new SublicenciaUsoDAO(null);
        return sd.getSublicenciasUsoByFecha(ini, fin);
    }

    public List<LicenciaUso> getLicenciasUsoByFechaAndType(Date ini, Date fin, String type) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        return ld.getLicenciasUsoByFechaAndType(ini, fin, type);
    }

    public List<LicenciaUso> getLicenciasUsoByFecha(Date ini, Date fin) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        return ld.getLicenciasUsoByFecha(ini, fin);
    }

    public List<PrendaComercial> getPrendasComercialesByFechaAndType(Date ini, Date fin, String type) {
        PrendaComercialDAO cd = new PrendaComercialDAO(null);
        return cd.getPrendasComercialesByFechaAndType(ini, fin, type);
    }

    public List<PrendaComercial> getPrendasComercialesByFecha(Date ini, Date fin) {
        PrendaComercialDAO pd = new PrendaComercialDAO(null);
        return pd.getPrendasComercialesByFecha(ini, fin);
    }

    public List<Transferencia> getTransferenciasByFecha(Date ini, Date fin) {
        TransferenciaDAO td = new TransferenciaDAO(null);
        return td.getTransferenciaByFecha(ini, fin);
    }

    public List<CambioDomicilio> getCambiosDomicilioByFechaCertificadoAndType(Date ini, Date fin, String type) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.getCambiosDomicilioByFechaCertificadoAndType(ini, fin, type);
    }

    public List<CambioDomicilio> getCambiosDomicilioByFechaNotificacionAndType(Date ini, Date fin, String type) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.getCambiosDomicilioByFechaNotificacionAndType(ini, fin, type);
    }

    public List<CambioNombre> getCambiosNombreByFechaCertificadoAndType(Date ini, Date fin, String type) {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getCambiosNombreByFechaCertificadoAndType(ini, fin, type);
    }

    public List<CambioNombre> getCambiosNombreByFechaNotificacionAndType(Date ini, Date fin, String type) {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getCambiosNombreByFechaNotificacionAndType(ini, fin, type);
    }

    public List<SubLicenciaUso> getSubLicenciasUsoByFechaLicenciaAndType(Date ini, Date fin, String type) {
        SublicenciaUsoDAO ld = new SublicenciaUsoDAO(null);
        return ld.getSublicenciasUsoByFechaLicenciaAndType(ini, fin, type);
    }

    public List<SubLicenciaUso> getSublicenciasUsoByFechaNotificacionAndType(Date ini, Date fin, String type) {
        SublicenciaUsoDAO ld = new SublicenciaUsoDAO(null);
        return ld.getSublicenciasUsoByFechaNotificacionAndType(ini, fin, type);
    }

    public List<LicenciaUso> getLicenciasUsoByFechaLicenciaAndType(Date ini, Date fin, String type) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        return ld.getLicenciasUsoByFechaLicenciaAndType(ini, fin, type);
    }

    public List<LicenciaUso> getLicenciasUsoByFechaNotificacionAndType(Date ini, Date fin, String type) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        return ld.getLicenciasUsoByFechaNotificacionAndType(ini, fin, type);
    }

    public List<PrendaComercial> getPrendasComercialesByFechaPrendaAndType(Date ini, Date fin, String type) {
        PrendaComercialDAO cd = new PrendaComercialDAO(null);
        return cd.getPrendasComercialesByFechaPrendaAndType(ini, fin, type);
    }

    public List<PrendaComercial> getPrendasComercialesByFechaNotificacionAndType(Date ini, Date fin, String type) {
        PrendaComercialDAO cd = new PrendaComercialDAO(null);
        return cd.getPrendasComercialesByFechaNotificacionAndType(ini, fin, type);
    }

    public List<Transferencia> getTransferenciaByFechaCertificado(Date ini, Date fin) {
        TransferenciaDAO td = new TransferenciaDAO(null);
        return td.getTransferenciaByFechaCertificado(ini, fin);
    }

    public List<Notificacion> getNotificacionesByFecha(Date ini, Date fin) {
        NotificacionDAO td = new NotificacionDAO(null);
        return td.getNotificacionByFecha(ini, fin);
    }

    public List<Abandono> getAbandonosByFecha(Date ini, Date fin) {
        AbandonoDAO td = new AbandonoDAO(null);
        return td.getAbandonoByFecha(ini, fin);
    }

    public List<Notificacion> getNotificacionByFechaCertificado(Date ini, Date fin) {
        NotificacionDAO td = new NotificacionDAO(null);
        return td.getNotificacionByFechaCertificado(ini, fin);
    }

    public List<Notificacion> getNotificacionByFechaNotificacion(Date ini, Date fin) {
        NotificacionDAO td = new NotificacionDAO(null);
        return td.getNotificacionByFechaNotificacion(ini, fin);
    }

    public List<Desistimiento> getDesistimientosByFecha(Date ini, Date fin) {
        DesistimientoDAO td = new DesistimientoDAO(null);
        return td.getDesistimientosByFecha(ini, fin);
    }

    public List<Caducada> getCaducadasByFecha(Date ini, Date fin) {
        CaducadaDAO td = new CaducadaDAO(null);
        return td.getCaducadasByFecha(ini, fin);
    }

    public RenewalForm getRenewalFormsByApplicationNumber(String applicationNumber) {
        RenewalFormDAO rd = new RenewalFormDAO();
        return rd.getRenewalFormsByApplication(applicationNumber);
    }

    public RenewalForm getRenewalFormsById(int id) {
        RenewalFormDAO rd = new RenewalFormDAO();
        return rd.getRenewalFormsById(id);
    }

    public Types getTypes(Integer id) {
        RenewalFormDAO rd = new RenewalFormDAO();
        FormTypes ft = rd.getFormTypesById(id);
        if (ft.getId() != null) {
            Types t = rd.getTypesById(ft.getTypeId());
            return t;
        } else {
            return new Types();
        }
    }

    public HallmarkForms getHallmarkFormDepurada(Integer debug_id) {
        RenewalFormDAO rd = new RenewalFormDAO();
        return rd.getHallmarkFormsDepurada(debug_id);
    }

    public Integer getNextCambioDomicilioCertificado() {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.getNextCambioDomicilioCertificado();
    }

    public Integer getNextCambioNombreCertificado() {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getNextCambioNombreCertificado();
    }

    public Integer getNextCambioNombrePatCertificado() {
        CambioNombrePatDAO cd = new CambioNombrePatDAO(null);
        return cd.getNextCambioNombreCertificado();
    }

    public Integer getNextCambioDomicilioPatCertificado() {
        CambioDomicilioPatDAO cd = new CambioDomicilioPatDAO(null);
        return cd.getNextCambioDomicilioCertificado();
    }

    public String getNextLicenciaUsoPatCertificado() {
        LicenciaUsoPatDAO cd = new LicenciaUsoPatDAO(null);
        return cd.getNextLicenciaUsoPatLicenciaNo();
    }

    public String getNextSublicenciaUsoPatCertificado() {
        SublicenciaUsoPatDAO cd = new SublicenciaUsoPatDAO(null);
        return cd.getNextSublicenciaUsoLicenciaNo();
    }

    public String getNextSublicenciaUsoNo() {
        SublicenciaUsoDAO cd = new SublicenciaUsoDAO(null);
        return cd.getNextSublicenciaUsoLicenciaNo();
    }

    public String getNextLicenciaUsoNo() {
        LicenciaUsoDAO cd = new LicenciaUsoDAO(null);
        return cd.getNextLicenciaUsoLicenciaNo();
    }

    public int getNextSublicenciaUsoResolucionNoByTipo(String tipo) {
        SublicenciaUsoDAO ld = new SublicenciaUsoDAO(null);
        return ld.getNextSublicenciaUsoResolucionNo(tipo);
    }

    public int getNextLicenciaUsoResolucionNoByTipo(String tipo) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        return ld.getNextLicenciaUsoResolucionNo(tipo);
    }

    public int getNextLicenciaTerminacionNo(Date fecha) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        return ld.getNextLicenciaTerminacionNo(fecha);
    }

    public int getNextLicenciaUsoResolucionCaducadaNo(String tipo) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        return ld.getNextLicenciaUsoResolucionCaducadaNo(tipo);
    }

    public Integer getNextPrendaComercialPrendaNo() {
        PrendaComercialDAO cd = new PrendaComercialDAO(null);
        return cd.getNextPrendaComercialPrendaNo();
    }

    public Integer getNextPrendaComercialResolucionCaducada() {
        PrendaComercialDAO pc = new PrendaComercialDAO(null);
        return pc.getNextPrendaComercialResolucionCaducada();
    }

    public Integer getNextNumeroCertificadoCD() {
        CambioDomicilioDAO td = new CambioDomicilioDAO(null);
        return td.getNextCambioDomicilioCertificado();
    }

    public Integer getNextNumeroCertificadoTransferencia() {
        TransferenciaDAO td = new TransferenciaDAO(null);
        return td.getNextNumeroCertificado();
    }

    public Integer getNextNumeroCertificadoTransferenciaPat() {
        TransferenciaPatDAO td = new TransferenciaPatDAO(null);
        return td.getNextNumeroCertificado();
    }

    public Integer getCasilleroSenadiByOwnerId(Integer ownerId) {
        RenewalFormDAO td = new RenewalFormDAO();
        return td.getCasilleroByOwner(ownerId);
    }

    public PpdiTituloSignoDistintivo getPpdiTituloSignoDistintivoByNumeroTitulo(String numeroTitulo) {
        PpdiTituloDAO pd = new PpdiTituloDAO(null);
        return pd.getPpdiTituloSignoDistintivoByNumeroTitulo(numeroTitulo);
    }

    public PpdiTituloSignoDistintivo getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(String numeroTitulo, String denominacion) {
        PpdiTituloDAO pd = new PpdiTituloDAO(null);
        return pd.getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(numeroTitulo, denominacion);
    }

    public List<PpdiTituloSignoDistintivo> getPpdiTitulosSignoDistintivoByNumeroTitulo(String numeroTitulo) {
        PpdiTituloDAO pd = new PpdiTituloDAO(null);
        return pd.getPpdiTitulosSignoDistintivoByNumeroTitulo(numeroTitulo);
    }

    public List<PpdiTitAndSigno> getTitulosAndSignos(String numTitulo) {
        PpdiTituloDAO pd = new PpdiTituloDAO(null);
        return pd.getPpdiTituloAndSigno(numTitulo);
    }

    public List<PpdiTitAndSigno> getPpdiTituloAndSignoByTituloOrDenominacion(String criterio) {
        PpdiTituloDAO pd = new PpdiTituloDAO(null);
        return pd.getPpdiTituloAndSignoByTituloOrDenominacion(criterio);
    }

    public boolean savePpdiTituloSignoDistintivo(PpdiTituloSignoDistintivo titulo) {
        PpdiTituloDAO pd = new PpdiTituloDAO(titulo);
        try {
            pd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Hubo un error al guardar el título: " + titulo.getNumeroTitulo());
            return false;
        }
    }

    public Person getTitularActual(Integer idRenewal) {
        RenewalFormDAO rd = new RenewalFormDAO();
        PersonRenewalName prn = rd.getPersonRenewalNameByIdRenewal(idRenewal);
        if (prn.getRenewalFormId() != null) {
            return rd.getPersonById(prn.getPersonId());
        } else {
            return new Person();
        }
    }

    public String getNamesPersonRenewalTextTypeByIdRenewal(Integer idRenewal, String type) {
        RenewalFormDAO rd = new RenewalFormDAO();
        List<PersonRenewal> pr = rd.getPersonRenewalTypeByIdRenewal(idRenewal, type);
        String nombres = "";
        if (!pr.isEmpty()) {
            for (int i = 0; i < pr.size(); i++) {
                nombres += rd.getPersonById(pr.get(i).getPersonId()).getName();
            }
            return nombres;
        } else {
            return nombres;
        }
    }

    public Person getPersonRenewalByType(Integer idRenewal, String type) {
        RenewalFormDAO rd = new RenewalFormDAO();
        List<PersonRenewal> pr = rd.getPersonRenewalTypeByIdRenewal(idRenewal, type);
        Person pers = new Person();

        for (int i = 0; i < pr.size(); i++) {
            pers = rd.getPersonById(pr.get(i).getPersonId());
            break;
        }
        return pers;
    }

    public Person getFirstPersonRenewalTypeByIdRenewal(Integer idRenewal, String type) {
        RenewalFormDAO rd = new RenewalFormDAO();
        List<PersonRenewal> pr = rd.getPersonRenewalTypeByIdRenewal(idRenewal, type);
        if (!pr.isEmpty()) {
            PersonRenewal pra = pr.get(0);
            return rd.getPersonById(pra.getPersonId());
        } else {
            return new Person();
        }
    }

    public List<Person> getPersonsByType(Integer idRenewal, String type) {
        RenewalFormDAO rd = new RenewalFormDAO();
        List<PersonRenewal> pr = rd.getPersonRenewalTypeByIdRenewal(idRenewal, type);
        List<Person> persons = new ArrayList<>();
        if (!pr.isEmpty()) {
            for (int i = 0; i < pr.size(); i++) {
                Person per = rd.getPersonById(pr.get(i).getPersonId());
                if (per.getId() != null) {
                    persons.add(per);
                }
            }
        }
        return persons;
    }

    public boolean saveHistorial(String estadoActual, String estadoAnterior, String solicitudSenadi, String accion, int user_id, String user) {
        Historial historial = new Historial();
        historial.setEstadoActual(estadoActual);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setSolicitudSenadi(solicitudSenadi);
        historial.setAccion(accion);
        historial.setFechaModificacion(Operaciones.getCurrentTimeStamp());
        historial.setUserId(user_id);
        historial.setUsuario(user);
        if (saveHistorial(historial)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean saveHistorialPat(String estadoActual, String estadoAnterior, String solicitudSenadi, String accion, int user_id, String user) {
        HistorialPat historial = new HistorialPat();
        historial.setEstadoActual(estadoActual);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setSolicitudSenadi(solicitudSenadi);
        historial.setAccion(accion);
        historial.setFechaModificacion(Operaciones.getCurrentTimeStamp());
        historial.setUserId(user_id);
        historial.setUsuario(user);
        if (saveHistorialPat(historial)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean saveHistorialPat(HistorialPat historial) {
        HistorialPatDAO hd = new HistorialPatDAO(historial);
        try {
            hd.persist();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al guardar Historial pat: " + ex);
            return false;
        }
    }

    public boolean saveHistorial(Historial historial) {
        HistorialDAO hd = new HistorialDAO(historial);
        try {
            hd.persist();
            return true;
        } catch (Exception ex) {
            System.out.println("Error al guardar Historial: " + ex);
            return false;
        }
    }

    public int getNextNumeroDesistimientoCD() {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.getNextNumeroDesistimiento();
    }

    public int getNextNumeroDesistimientoCN() {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getNextNumeroDesistimiento();
    }

    public int getNextNumeroResolucionDesistimientoPrenda() {
        PrendaComercialDAO pd = new PrendaComercialDAO(null);
        return pd.getNextNumeroResolucionDesistimiento();
    }

    public int getNextNumeroDesistimiento() {
        DesistimientoDAO nd = new DesistimientoDAO(null);
        return nd.getNextNumeroDesistimiento();
    }

    public int getNextNumeroNotificacionCD(Date fechaElaboraNotificacion) {
        CambioDomicilioDAO nd = new CambioDomicilioDAO(null);
        return nd.getNextNumeroNotificacionCD(fechaElaboraNotificacion);
    }

    public int getNextNumeroNotificacionCN(Date fechaElaboraNotificacion) {
        CambioNombreDAO nd = new CambioNombreDAO(null);
        return nd.getNextNumeroNotificacionCN(fechaElaboraNotificacion);
    }

    public String getNextNumeroNotificacionSublicenciaUso(Date fechaElaboraNotificacion) {
        SublicenciaUsoDAO ld = new SublicenciaUsoDAO(null);
        return ld.getNextNumeroNotificacionSublicencia(fechaElaboraNotificacion);
    }

    public int getNextNumeroAbandonoLicencia(Date fechaElaboracion) {
        LicenciaUsoDAO cd = new LicenciaUsoDAO(null);
        return cd.getNextNumeroAbandonoLicencia(fechaElaboracion);
    }

    public int getNextNumeroAbandonoSublicencia(Date fechaElaboracion) {
        SublicenciaUsoDAO cd = new SublicenciaUsoDAO(null);
        return cd.getNextNumeroAbandonoSubLicencia(fechaElaboracion);
    }

    public int getNextNumeroAbandonoPrenda(Date fechaElaboracion) {
        PrendaComercialDAO cd = new PrendaComercialDAO(null);
        return cd.getNextNumeroAbandonoPrenda(fechaElaboracion);
    }

    public int getNextNumeroAbandonoCD(Date fechaElaboracion) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.getNextNumeroAbandonoCD(fechaElaboracion);
    }

    public int getNextNumeroAbandonoCN(Date fechaElaboracion) {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getNextNumeroAbandonoCN(fechaElaboracion);
    }

    public String getNextNumeroNotificacionLicenciaUso(Date fechaElaboraNotificacion) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        return ld.getNextNumeroNotificacionLicencia(fechaElaboraNotificacion);
    }

    public int getNextNumeroNotificacionPrendaComercial(Date fechaElaboraNotificacion) {
        PrendaComercialDAO nd = new PrendaComercialDAO(null);
        return nd.getNextNumeroNotificacionPrenda(fechaElaboraNotificacion);
    }

    public int getNextNumeroLevantamientoPrenda(Date fechaElaboraNotificacion) {
        PrendaComercialDAO nd = new PrendaComercialDAO(null);
        return nd.getNextNumeroLevantamientoPrenda(fechaElaboraNotificacion);
    }

    public int getNextNumeroNotificacion(Date fechaElaboraNotificacion) {
        NotificacionDAO nd = new NotificacionDAO(null);
        return nd.getNextNumeroNotificacion(fechaElaboraNotificacion);
    }

    public int getNextNumeroAbandono(Date fechaElaboracion) {
        AbandonoDAO ad = new AbandonoDAO(null);
        return ad.getNextNumeroAbandono(fechaElaboracion);
    }

    public Transferencia getTransferenciaBySolSenadi(String solicitud) {
        TransferenciaDAO t = new TransferenciaDAO(null);
        return t.getTransferenciaBySolSenadi(solicitud);
    }

    public Abandono getAbandonoBySolSenadi(String solicitud) {
        AbandonoDAO t = new AbandonoDAO(null);
        return t.getAbandonoBySolicitud(solicitud);
    }

    public void refreshAbandono(Abandono abandono) {
        AbandonoDAO ad = new AbandonoDAO(null);
        ad.getEntityManager().refresh(abandono);
    }

    public void refreshCambioNombre(CambioNombre cambio) {
        CambioNombreDAO ad = new CambioNombreDAO(null);
        ad.getEntityManager().refresh(cambio);
    }

    public void refreshCambioDomicilio(CambioDomicilio cambio) {
        CambioDomicilioDAO ad = new CambioDomicilioDAO(null);
        ad.getEntityManager().refresh(cambio);
    }

    public void refreshPrendaComercial(PrendaComercial prenda) {
        PrendaComercialDAO pd = new PrendaComercialDAO(null);
        pd.getEntityManager().refresh(prenda);
    }

    public void refreshLicenciaUso(LicenciaUso licencia) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        ld.getEntityManager().refresh(licencia);
    }

    public void refreshSubLicenciaUso(SubLicenciaUso licencia) {
        SublicenciaUsoDAO sd = new SublicenciaUsoDAO(null);
        sd.getEntityManager().refresh(licencia);
    }

    public List<Transferencia> getTransferenciasByDenominacion(String denominacion) {
        TransferenciaDAO td = new TransferenciaDAO(null);
        return td.getTransferenciasByDenominacion(denominacion);
    }

    public List<Notificacion> getNotificacionesByDenominacion(String denominacion) {
        NotificacionDAO nd = new NotificacionDAO(null);
        return nd.getNotificacionesByDenominacion(denominacion);
    }

    public List<Desistimiento> getDesistidasByDenominacion(String denominacion) {
        DesistimientoDAO dd = new DesistimientoDAO(null);
        return dd.getDesistidasByDenominacion(denominacion);
    }

    public List<Caducada> getCaducadasByDenominacion(String denominacion) {
        CaducadaDAO cd = new CaducadaDAO(null);
        return cd.getCaducadasByDenominacion(denominacion);
    }

    public Notificacion getNotificacionBySolSenadi(String solicitud) {
        NotificacionDAO t = new NotificacionDAO(null);
        return t.getNotificacionBySolicitud(solicitud);
    }

    public Desistimiento getDesistidasBySolSenadi(String solicitud) {
        DesistimientoDAO t = new DesistimientoDAO(null);
        return t.getDesistimientosBySolicitud(solicitud);
    }

    public List<Historial> getHistorialBySolicitudSenadi(String solicitud) {
        HistorialDAO hd = new HistorialDAO(null);
        return hd.getHistorialBySolicitudSenadi(solicitud);
    }

    public PpdiSolicitudSignoDistintivo getPpdiSolicitudSignoDistintivoByExpedient(String expedient) {
        PpdiSignoDAO ps = new PpdiSignoDAO(null);
        return ps.getPpdiSolicitudSignoDistintivoByExpedient(expedient);
    }

    public PpdiSolicitudSignoDistintivo getPpdiSolicitudSignoDistintivoByCodigoSolicitud(Integer codigoSolicitud) {
        PpdiSignoDAO ps = new PpdiSignoDAO(null);
        return ps.getPpdiSolicitudSignoDistintivoByCodigoSolicitud(codigoSolicitud);
    }

    public List<PpdiTitAndSigno> getPpdiSolicitudSignoDistintivoByDenominacion(String denominacion) {
        PpdiSignoDAO ps = new PpdiSignoDAO(null);
        return ps.getPpdiSingoAndTituloByDenominacion(denominacion);
    }

    public PpdiTituloSignoDistintivo getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(Integer codigoSolicitudSigno) {
        PpdiTituloDAO pd = new PpdiTituloDAO(null);
        return pd.getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(codigoSolicitudSigno);
    }

    public int getNextNumeroMinimoTitulo() {
        PpdiTituloDAO pd = new PpdiTituloDAO(null);
        return pd.getNextNumeroMinimoTitulo();
    }

    public List<Delegado> getAllDelegados() {
        DelegadoDAO dd = new DelegadoDAO(null);
        return dd.buscarTodos();
    }

    public List<Delegado> getAllDelegadosByTipo(String tipo) {
        DelegadoDAO dd = new DelegadoDAO(null);
        return dd.buscarTodosByTipo(tipo);
    }

    public List<Delegacion> getAllDelegaciones() {
        DelegacionDAO dd = new DelegacionDAO(null);
        return dd.buscarTodos();
    }

    public List<Resolucion> getResolucionesByTipo(String tipo) {
        ResolucionDAO rd = new ResolucionDAO(null);
        return rd.getResolucionesByTipo(tipo);
    }

    public boolean saveDelegado(Delegado dele) {
        DelegadoDAO dd = new DelegadoDAO(dele);
        try {
            dd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar delegado " + ex);
            return false;
        }
    }

    public boolean saveDelegacion(Delegacion dele) {
        DelegacionDAO dd = new DelegacionDAO(dele);
        try {
            dd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar delegación " + ex);
            return false;
        }
    }

    public boolean saveResolucion(Resolucion resol) {
        ResolucionDAO rd = new ResolucionDAO(resol);
        try {
            rd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar resolución " + ex);
            return false;
        }
    }

    public boolean updateDelegado(Delegado dele) {
        DelegadoDAO dd = new DelegadoDAO(dele);
        try {
            dd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar delegado " + ex);
            return false;
        }
    }

    public boolean updateDelegacion(Delegacion dele) {
        DelegacionDAO dd = new DelegacionDAO(dele);
        try {
            dd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar delegación " + ex);
            return false;
        }
    }

    public boolean updateResolucion(Resolucion resol) {
        ResolucionDAO rd = new ResolucionDAO(resol);
        try {
            rd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar resolución " + ex);
            return false;
        }
    }

    public boolean activeADelegado(Delegado delegado, List<Delegado> delegados) {
        for (int i = 0; i < delegados.size(); i++) {
            if (!delegados.get(i).getId().equals(delegado.getId())) {
                delegados.get(i).setEstado(false);
                updateDelegado(delegados.get(i));
            }
        }
        if (updateDelegado(delegado)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean removeDelegado(Delegado dele) {
        DelegadoDAO dd = new DelegadoDAO(dele);
        try {
            dd.remove();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al remover delegado " + ex);
            return false;
        }
    }

    public boolean activeADelegacion(Delegacion delegacion, List<Delegacion> delegaciones) {
        for (int i = 0; i < delegaciones.size(); i++) {
            if (!delegaciones.get(i).getId().equals(delegacion.getId())) {
                delegaciones.get(i).setActivo(false);
                updateDelegacion(delegaciones.get(i));
            }
        }
        if (updateDelegacion(delegacion)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean removeDelegacion(Delegacion dele) {
        DelegacionDAO dd = new DelegacionDAO(dele);
        try {
            dd.remove();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al remover delegación " + ex);
            return false;
        }
    }

    public boolean activeAResolucion(Resolucion resolucion, List<Resolucion> resoluciones) {
        for (int i = 0; i < resoluciones.size(); i++) {
            if (!resoluciones.get(i).getId().equals(resolucion.getId())) {
                resoluciones.get(i).setActivo(false);
                updateResolucion(resoluciones.get(i));
            }
        }
        if (updateResolucion(resolucion)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean removeResolucion(Resolucion resol) {
        ResolucionDAO rd = new ResolucionDAO(resol);
        try {
            rd.remove();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al remover resolución: " + ex);
            return false;
        }
    }

    public boolean validarDelegadoActivo(String tipo) {
        DelegadoDAO dd = new DelegadoDAO(null);
        return dd.validarDelegadoActivo(tipo);
    }

    public boolean validarDelegacionActivo() {
        DelegacionDAO dd = new DelegacionDAO(null);
        return dd.validarDelegacionActiva();
    }

    public boolean validarResolucionActiva(String tipo) {
        ResolucionDAO rd = new ResolucionDAO(null);
        return rd.validarResolucionActiva(tipo);
    }

    public Delegado getDelegadoActivo(String tipo) {
        DelegadoDAO dd = new DelegadoDAO(null);
        return dd.getDelegadoActivo(tipo);
    }

    public Delegacion getDelegacionActiva() {
        DelegacionDAO dd = new DelegacionDAO(null);
        return dd.getDelegacionActiva();
    }

    public Resolucion getResolucionActiva(String tipo) {
        ResolucionDAO rd = new ResolucionDAO(null);
        return rd.getResolucionActiva(tipo);
    }

    public PpdiPersona getASolicitanteByCodigoSolicitud(Integer codigoSolicitud) {
        PpdiPersonaSolicitudDAO pd = new PpdiPersonaSolicitudDAO(null);
        List<PpdiPersonaSolicitudSignoDistintivo> personassolicitud = pd.getPpdiPersonaSolicitudSignoByCodigoSolicitud(codigoSolicitud);
        PpdiPersona persona = new PpdiPersona();
        if (!personassolicitud.isEmpty()) {
            Integer idpersona = 0;
            for (int i = 0; i < personassolicitud.size(); i++) {
                PpdiPersonaSolicitudSignoDistintivo ppssd = personassolicitud.get(i);
                if (ppssd.getTipoPersona().equals("SOLICITANTE")) {
                    idpersona = ppssd.getCodigoPersona();
                    break;
                }
            }
            if (idpersona != 0) {
                persona = pd.getPpdiPersonaByCodigoPersona(idpersona);
            }
        }
        return persona;
    }

    public List<Rooptions> getRosBySolicitud(String solicitud) {
        RooptionsDAO rd = new RooptionsDAO(null);
        return rd.buscarRoBySolicitud(solicitud);
    }

    public boolean saveRooptios(Rooptions ro) {
        RooptionsDAO rd = new RooptionsDAO(ro);
        try {
            rd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar ro " + ex);
            return false;
        }
    }

    public boolean removeRooptios(Rooptions ro) {
        RooptionsDAO rd = new RooptionsDAO(ro);
        try {
            rd.remove();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al remover ro " + ex);
            return false;
        }
    }

    public List<UploadNotificacion> getNotificacionesByEstado(boolean estado) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.getNotificacionesByEstado(estado);
    }

    public boolean saveUploadNotificacion(UploadNotificacion un) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(un);
        try {
            ud.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Hubo un error al guardar UploadNotificacion: " + ex);
            return false;
        }
    }

    public boolean updateUploadNotificacion(UploadNotificacion un) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(un);
        try {
            ud.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Hubo un error al editar UploadNotificacion: " + ex);
            return false;
        }
    }

    public boolean validarExistenciaUploadNotificacion(String solicitud, String documento) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.validarExistenciaUploadNotificacion(solicitud, documento);
    }

    public boolean validarExistenciaUploadDocumento(String documento, boolean estado) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.validarExistenciaUploadDocumento(documento, estado);
    }

    public boolean validarExistenciaUploadNotificacion(String solicitud, boolean estado) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.validarExistenciaUploadNotificacion(solicitud, estado);
    }

    public List<UploadNotificacion> getUploadNotificacionByCriterio(String criterio, boolean estado) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.getUploadNotificacionByCriterio(criterio, estado);
    }

    public List<UploadNotificacion> getUploadNotificacionBySolicitud(String solicitud, boolean estado) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.getUploadNotificacionBySolicitud(solicitud, estado);
    }

    public List<UploadNotificacion> getUploadNotificacionByDate(Date start, Date end, boolean estado) {
        UploadNotificacionDAO ud = new UploadNotificacionDAO(null);
        return ud.getUploadNotificacionByDate(start, end, estado);
    }

    public PaymentReceipt getPaymentReceiptById(Integer id) {
        RenewalFormDAO rd = new RenewalFormDAO();
        return rd.getPaymentReceiptById(id);
    }

    public boolean downLockerNotifications(UploadNotificacion un) {
        NotificationsDAO nd = new NotificationsDAO();
        return nd.downLockerNotifications(un);
    }

    public List<SubLicenciaUso> getSublicenciasUsoNotificada(String tipo) {
        SublicenciaUsoDAO ld = new SublicenciaUsoDAO(null);
        return ld.getSublicenciasUsoNotificada(tipo);
    }

    public List<LicenciaUso> getLicenciasUsoNotificada(String tipo) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        return ld.getLicenciasUsoNotificada(tipo);
    }

    public List<LicenciaUso> getLicenciasUsoAbandonoByTipo(String tipo) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        return ld.getLicenciasUsoAbandonoByTipo(tipo);
    }

    public List<SubLicenciaUso> getSubLicenciasUsoAbandonoByTipo(String tipo) {
        SublicenciaUsoDAO ld = new SublicenciaUsoDAO(null);
        return ld.getSubLicenciasUsoAbandonoByTipo(tipo);
    }

    public List<CambioNombre> getCambiosNombreNotificada(String tipo) {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getCambiosNombreNotificada(tipo);
    }

    public List<PrendaComercial> getPrendasComercialesNotificada(String tipo) {
        PrendaComercialDAO cd = new PrendaComercialDAO(null);
        return cd.getPrendasComercialesNotificada(tipo);
    }

    public List<CambioNombre> getCambiosNombreByTipo(String tipo) {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getCambiosNombreByTipo(tipo);
    }

    public List<CambioNombre> getCambiosNombreByDenominacion(String denominacion) {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getCambiosNombreByDenominacion(denominacion);
    }

    public List<CambioDomicilio> getCambiosDomicilioByDenominacion(String denominacion) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.getCambiosDomicilioByDenominacion(denominacion);
    }

    public List<PrendaComercial> getPrendasComercialesByDenominacion(String denominacion) {
        PrendaComercialDAO pd = new PrendaComercialDAO(null);
        return pd.getPrendasComercialesByDenominacion(denominacion);
    }

    public List<LicenciaUso> getLicenciasUsoByDenominacion(String denominacion) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        return ld.getLicenciasUsoByDenominacion(denominacion);
    }

    public List<SubLicenciaUso> getSublicenciasUsoByDenominacion(String denominacion) {
        SublicenciaUsoDAO sd = new SublicenciaUsoDAO(null);
        return sd.getSublicenciasUsoByDenominacion(denominacion);
    }

    public List<PrendaComercial> getPrendasComercialesByTipo(String tipo) {
        PrendaComercialDAO cd = new PrendaComercialDAO(null);
        return cd.getPrendasComercialesByTipo(tipo);
    }

    public List<PrendaComercial> getAbandonosPrendasComercialesByTipo(String tipo) {
        PrendaComercialDAO cd = new PrendaComercialDAO(null);
        return cd.getAbandonosPrendasComercialesByTipo(tipo);
    }

    public List<SubLicenciaUso> getSublicenciasUsoByTipo(String tipo) {
        SublicenciaUsoDAO cd = new SublicenciaUsoDAO(null);
        return cd.getSublicenciasUsoByTipo(tipo);
    }

    public List<LicenciaUso> getLicenciasUsoByTipo(String tipo) {
        LicenciaUsoDAO cd = new LicenciaUsoDAO(null);
        return cd.getLicenciasUsoByTipo(tipo);
    }

    public List<LicenciaUso> getTerminacionesLicencia() {
        LicenciaUsoDAO cd = new LicenciaUsoDAO(null);
        return cd.getTerminacionesLicencia();
    }

    public List<CambioDomicilio> getCambiosDomicilioByTipo(String tipo) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.getCambiosDomicilioByTipo(tipo);
    }

    public List<CambioDomicilio> getCambiosDomicilioNotificada(String tipo) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.getCambiosDomicilioNotificada(tipo);
    }

    public boolean anularDocumentoModificacion(UploadNotificacion un, String tramite, String user) {
        RenewalForm rf = getRenewalFormsByApplicationNumber(tramite);

        String rutadoccas = "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + un.getCasillero() + "/" + un.getDocumento();

        if (rf.getId() != null) {
            Types t = getTypes(rf.getTransactionMotiveId());
//            Types ttp = getTypes(rf.getFormId());
            if (t.getName().trim().toLowerCase().contains("domicilio")) {

                CambioDomicilio cd = getCambioDomicilioBySolicitud(tramite);
                int conf = Operaciones.validaTextoEnPdf(rutadoccas, "CERTIFICADO DE CAMBIO DE DOMICILIO");
                if (conf == 1) {
                    cd.setCertificadoEmitido(false);
                    saveHistorial("CERTIFICADO_CD", "CERTIFICADO_CD", cd.getSolicitud(), "DOC CERTIFICADO ANULADO " + un.getDocumento(), 0, user);
                    System.out.println("certificado para " + cd.getSolicitud() + " anulado");
                } else if (conf == 0) {
                    cd.setNotificacionEmitida(false);
                    saveHistorial("NOTIFICACION_CD", "NOTIFICACION_CD", cd.getSolicitud(), "DOC NOTIFICACION ANULADA " + un.getDocumento(), 0, user);
                    System.out.println("notificación para " + cd.getSolicitud() + " anulado");
                }
                if (conf == 1 || conf == 0) {
                    updateCambioDomicilio(cd);
                }

            } else if (t.getName().trim().toLowerCase().contains("cambio de nombre")) {
                CambioNombre cn = getCambioNombreBySolicitud(tramite);
                int conf = Operaciones.validaTextoEnPdf(rutadoccas, "CERTIFICADO DE CAMBIO DE NOMBRE");
                if (conf == 1) {
                    cn.setCertificadoEmitido(false);
                    saveHistorial("CERTIFICADO_CN", "CERTIFICADO_CN", cn.getSolicitud(), "DOC CERTIFICADO ANULADO " + un.getDocumento(), 0, user);
                    System.out.println("certificado para " + cn.getSolicitud() + " anulado");
                } else if (conf == 0) {
                    cn.setNotificacionEmitida(false);
                    saveHistorial("NOTIFICACION_CN", "NOTIFICACION_CN", cn.getSolicitud(), "DOC NOTIFICACION ANULADA " + un.getDocumento(), 0, user);
                    System.out.println("notificación para " + cn.getSolicitud() + " anulado");
                }
                if (conf == 1 || conf == 0) {
                    updateCambioNombre(cn);
                }

            } else if (t.getName().trim().toLowerCase().contains("prenda comercial")) {
                PrendaComercial pc = getPrendaComercialBySolicitud(tramite);
                int conf = Operaciones.validaTextoEnPdf(rutadoccas, "INSCRIPCIÓN PRENDA COMERCIAL No");
                if (conf == 1) {
                    pc.setCertificadoEmitido(false);
                    saveHistorial("INSCRIPCIÓN_PC", "INSCRIPCIÓN_PC", pc.getSolicitud(), "DOC INSCRIPCIÓN ANULADA " + un.getDocumento(), 0, user);
                    System.out.println("certificado para " + pc.getSolicitud() + " anulado");
                } else if (conf == 0) {
                    pc.setNotificacionEmitida(false);
                    saveHistorial("NOTIFICACION_PC", "NOTIFICACION_PC", pc.getSolicitud(), "DOC NOTIFICACION ANULADA " + un.getDocumento(), 0, user);
                    System.out.println("notificación para " + pc.getSolicitud() + " anulado");
                }
                if (conf == 1 || conf == 0) {
                    updatePrendaComercial(pc);
                }
            } else if (t.getName().trim().toLowerCase().contains("licencia de uso")) {
                LicenciaUso lu = getLicenciaUsoBySolicitud(tramite);
                if (lu.getId() != null) {
                    int conf = Operaciones.validaTextoEnPdf(rutadoccas, "INSCRIPCIÓN LICENCIA ");
                    if (conf == 1) {
                        lu.setCertificadoEmitido(false);
                        saveHistorial("INSCRIPCIÓN_LU", "INSCRIPCIÓN_LU", lu.getSolicitud(), "DOC INSCRIPCIÓN ANULADA " + un.getDocumento(), 0, user);
                        System.out.println("certificado para " + lu.getSolicitud() + " anulado");
                    } else if (conf == 0) {
                        lu.setNotificacionEmitida(false);
                        saveHistorial("NOTIFICACION_LU", "NOTIFICACION_LU", lu.getSolicitud(), "DOC NOTIFICACION ANULADA " + un.getDocumento(), 0, user);
                        System.out.println("notificación para " + lu.getSolicitud() + " anulado");
                    }
                    if (conf == 1 || conf == 0) {
                        updateLicenciaUso(lu);
                    }
                } else {
                    SubLicenciaUso slu = getSublicenciaUsoBySolicitud(tramite);
                    int conf = Operaciones.validaTextoEnPdf(rutadoccas, "INSCRIPCIÓN SUBLICENCIA ");
                    if (conf == 1) {
                        slu.setCertificadoEmitido(false);
                        saveHistorial("INSCRIPCIÓN_SL", "INSCRIPCIÓN_SL", slu.getSolicitud(), "DOC INSCRIPCIÓN ANULADA " + un.getDocumento(), 0, user);
                        System.out.println("certificado para " + slu.getSolicitud() + " anulado");
                    } else if (conf == 0) {
                        slu.setNotificacionEmitida(false);
                        saveHistorial("NOTIFICACION_SL", "NOTIFICACION_SL", slu.getSolicitud(), "DOC NOTIFICACION ANULADA " + un.getDocumento(), 0, user);
                        System.out.println("notificación para " + slu.getSolicitud() + " anulado");
                    }
                    if (conf == 1 || conf == 0) {
                        updateSublicenciaUso(slu);
                    }
                }
            } else {
                Transferencia tr = getTransferenciaBySolSenadi(tramite);
                if (tr.getId() != null) {
                    tr.setCertificadoEmitido(false);
                    updateTransferencia(tr);
                    saveHistorial("CERTIFICADO_TR", "CERTIFICADO_TR", tr.getSolicitud(), "DOC CERTIFICADO ANULADO " + un.getDocumento(), 0, user);
                } else {
                    Notificacion no = getNotificacionBySolSenadi(tramite);
                    updateNotificacion(no);
                    saveHistorial("NOTIFICADA_TR", "NOTIFICADA_TR", tramite, "DOC NOTIFICADA ANULADO " + un.getDocumento(), 0, user);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public List<Transferencia> getTransferenciasByTitular(String titular) {
        TransferenciaDAO td = new TransferenciaDAO(null);
        return td.getTransferenciasByTitular(titular);
    }

    public List<Desistimiento> getDesistidasByTitular(String titular) {
        DesistimientoDAO dd = new DesistimientoDAO(null);
        return dd.getDesistidasByTitular(titular);
    }

    public List<Notificacion> getNotificacionesByTitular(String titular) {
        NotificacionDAO nd = new NotificacionDAO(null);
        return nd.getNotificacionesByTitular(titular);
    }

    public List<Caducada> getCaducadasByTitular(String titular) {
        CaducadaDAO cd = new CaducadaDAO(null);
        return cd.getCaducadasByTitular(titular);
    }

    public List<CambioNombre> getCambiosNombreByTitular(String titular) {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getCambiosNombreByTitular(titular);
    }

    public List<CambioDomicilio> getCambiosDomicilioByTitular(String titular) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.getCambiosDomicilioByTitular(titular);
    }

    public List<PrendaComercial> getPrendasComercialesByTitular(String titular) {
        PrendaComercialDAO pd = new PrendaComercialDAO(null);
        return pd.getPrendasComercialesByTitular(titular);
    }

    public List<LicenciaUso> getLicenciasUsoByTitular(String titular) {
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        return ld.getLicenciasUsoByTitular(titular);
    }

    public List<SubLicenciaUso> getSublicenciasUsoByTitular(String titular) {
        SublicenciaUsoDAO ld = new SublicenciaUsoDAO(null);
        return ld.getSublicenciasUsoByTitular(titular);
    }

    public List<Owner> getOwnersByCriteria(String criteria) {
        OwnerDAO od = new OwnerDAO();
        return od.getOwnersByCriteria(criteria);
    }

    public Owner getOwnersById(int owner_id) {
        OwnerDAO od = new OwnerDAO();
        return od.getOwnerById(owner_id);
    }

    public boolean saveCambioCasillero(CambioCasillero cc) {
        CambioCasilleroDAO cd = new CambioCasilleroDAO(cc);
        try {
            cd.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar cambio de casillero");
            return false;
        }
    }

    public boolean updateCambioCasillero(CambioCasillero cc) {
        CambioCasilleroDAO cd = new CambioCasilleroDAO(cc);
        try {
            cd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar cambio de casillero");
            return false;
        }
    }

    public CambioCasillero getCambioCasilleroWhenNotId(CambioCasillero c) {
        CambioCasilleroDAO cd = new CambioCasilleroDAO(null);
        return cd.getCambioCasilleroWhenNotId(c);
    }

    public List<CambioCasillero> getCambioCasilleroByEstado(String estado) {
        CambioCasilleroDAO cd = new CambioCasilleroDAO(null);
        return cd.getCambioCasilleroByEstado(estado);
    }

    public PpdiSolicitudModificacion getPpdiSolicitudModificacionByNumTramite(String tramite) {
        PpdiModificacionDAO pd = new PpdiModificacionDAO(null);
        return pd.getPpdiSolicitudModificacionByNumTramite(tramite);
    }

    public boolean updatePpdiSolicitudModificacion(PpdiSolicitudModificacion modificacion) {
        PpdiModificacionDAO pd = new PpdiModificacionDAO(modificacion);
        try {
            pd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar ppdi_solicitud_modificacion: " + ex);
            return false;
        }
    }

    public boolean updatePpdiSolicitudSignoDistintivo(PpdiSolicitudSignoDistintivo signo) {
        PpdiSignoDAO pd = new PpdiSignoDAO(signo);
        try {
            pd.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar ppdi_solicitud_signo_distintivo: " + ex);
            return false;
        }
    }

    public boolean updateRenewalForm(RenewalForm renewal) {
        RenewalFormDAO rd = new RenewalFormDAO();
        return rd.updateRenewalForm(renewal);
    }

    public String getTipoModificacion(String solicitud) {
        Controlador c = new Controlador();
        CambioDomicilio camd = c.getCambioDomicilioBySolicitud(solicitud);
        String tipoModificacion = "";
        if (camd.getId() != null) {
            tipoModificacion = "CAMBIO DE DOMICILIO";
        } else {
            CambioNombre cambn = c.getCambioNombreBySolicitud(solicitud);
            if (cambn.getId() != null) {
                tipoModificacion = "CAMBIO DE NOMBRE";
            } else {
                PrendaComercial prend = c.getPrendaComercialBySolicitud(solicitud);
                if (prend.getId() != null) {
                    tipoModificacion = "PRENDA COMERCIAL";
                } else {
                    LicenciaUso licu = c.getLicenciaUsoBySolicitud(solicitud);
                    if (licu.getId() != null) {
                        tipoModificacion = "LICENCIA DE USO";
                    } else {
                        SubLicenciaUso subl = c.getSublicenciaUsoBySolicitud(solicitud);
                        if (subl.getId() != null) {
                            tipoModificacion = "SUBLICENCIA DE USO";
                        } else {
                            //para indicar que se ha entregado certificado digital firmado
                            Transferencia tranaux = c.getTransferenciaBySolSenadi(solicitud);
                            if (tranaux.getId() != null) {
                                tipoModificacion = "TRANSFERENCIA";
                            } else {
                                //para indicar que se ha realizado una notificación digital firmado
                                Notificacion notaux = c.getNotificacionBySolSenadi(solicitud);
                                if (notaux.getId() != null) {
                                    tipoModificacion = "NOTIFICACION - TRANSFERENCIA";
                                } else {
                                    Desistimiento desaux = c.getDesistidasBySolSenadi(solicitud);
                                    if (desaux.getId() != null) {
                                        tipoModificacion = "DESISTIMIENTO - TRANSFERENCIA";
                                    } else {
                                        Caducada cadaux = c.getCaducadaBySolSenadi(solicitud);
                                        if (cadaux.getId() != null) {
                                            tipoModificacion = "CADUCADA - TRANSFERENCIA";
                                        } else {
                                            return "RENOVACIÓN";
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return tipoModificacion;
    }

    public boolean existsOwner(Integer casillero) {
        OwnerDAO od = new OwnerDAO();
        return od.existsOwner(casillero);
    }

    public boolean tituloCanceladoExists(TituloCancelado titCancelado, boolean reverso) {
        TituloCanceladoDAO td = new TituloCanceladoDAO(null);
        return td.existsTituloCanceladoByTituloAndExpediente(titCancelado.getNumeroTitulo(), titCancelado.getExpediente(), reverso);
    }

    public LoginBean getLogin() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        LoginBean loginB = (LoginBean) session.getAttribute("loginBean");
        return loginB;
    }

    public boolean saveTituloCancelado(TituloCancelado tit) {
        TituloCanceladoDAO td = new TituloCanceladoDAO(tit);
        try {
            td.persist();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al guardar titulo_cancelado " + tit.getNumeroTitulo() + ": " + ex);
            return false;
        }
    }

    public boolean updateTituloCancelado(TituloCancelado tit) {
        TituloCanceladoDAO td = new TituloCanceladoDAO(tit);
        try {
            td.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al editar titulo_cancelado " + tit.getNumeroTitulo() + ": " + ex);
            return false;
        }
    }

    public List<Renovacion> getRenovacionesByTituloAndDenominacion(String titulo, String denominacion) {
        RenovacionDAO rd = new RenovacionDAO(null);
        return rd.getRenovacionByTituloAndDenominacion(titulo, denominacion);
    }

    public boolean updateRenovacion(Renovacion ren) {
        RenovacionDAO rd = new RenovacionDAO(ren);
        try {
            rd.update();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public List<Notificada> getNotificadasByTituloAndDenominacion(String titulo, String denominacion) {
        NotificadaRDAO rd = new NotificadaRDAO(null);
        return rd.getNotificadaByTituloAndDenominacion(titulo, denominacion);
    }

    public boolean updateNotificadaR(Notificada notr) {
        NotificadaRDAO nd = new NotificadaRDAO(notr);
        try {
            nd.update();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public List<Desistida> getDesistidasByTituloAndDenominacion(String titulo, String denominacion) {
        DesistidaRDAO rd = new DesistidaRDAO(null);
        return rd.getDesistidasByTituloAndDenominacion(titulo, denominacion);
    }

    public boolean updateDesistidaR(Desistida desist) {
        DesistidaRDAO nd = new DesistidaRDAO(desist);
        try {
            nd.update();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public List<CaducadaRen> getCaducadasRByTituloAndDenominacion(String titulo, String denominacion) {
        CaducadaRDAO rd = new CaducadaRDAO(null);
        return rd.getCaducadasByTituloAndDenominacion(titulo, denominacion);
    }

    public boolean updateCaducadaR(CaducadaRen cadr) {
        CaducadaRDAO rd = new CaducadaRDAO(cadr);
        try {
            rd.update();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public List<TituloCancelado> getTitulosCancelados() {
        TituloCanceladoDAO td = new TituloCanceladoDAO(null);
        return td.buscarTodos();
    }

    public List<TituloCancelado> getTitulosCanceladosByReverso(boolean reverso) {
        TituloCanceladoDAO td = new TituloCanceladoDAO(null);
        return td.getTitulosCanceladosByReverso(reverso);
    }

    public boolean existsTituloCanceladoByTituloAndExpediente(String numTitulo, String expediente, boolean reverso) {
        TituloCanceladoDAO td = new TituloCanceladoDAO(null);
        return td.existsTituloCanceladoByTituloAndExpediente(numTitulo, expediente, reverso);
    }

    public boolean existsTituloCanceladoByTituloAndDenominacion(String numTitulo, String denominacion, boolean reverso) {
        TituloCanceladoDAO td = new TituloCanceladoDAO(null);
        return td.existsTituloCanceladoByTituloAndDenominacion(numTitulo, denominacion, reverso);
    }

    public TituloCancelado getTituloCanceladoByTituloAndExpediente(String numTitulo, String expediente) {
        TituloCanceladoDAO td = new TituloCanceladoDAO(null);
        return td.getTituloCanceladoByTituloAndExpediente(numTitulo, expediente);
    }

    public TituloCancelado getTituloCanceladoByTituloAndDenoninacion(String numTitulo, String denominacion) {
        TituloCanceladoDAO td = new TituloCanceladoDAO(null);
        return td.getTituloCanceladoByTituloAndDenoninacion(numTitulo, denominacion);
    }

    public String nextProvidenciaCambioCasillero() {
        CambioCasilleroDAO cd = new CambioCasilleroDAO(null);
        return cd.nextProvidencia();
    }

    public List<Cpis> buscarCpisByTramiteOCDI(String numeroTramiteOCDI) {
        CpisDAO cd = new CpisDAO();
        return cd.getCpisByTramiteOCDI(numeroTramiteOCDI);
    }

    public PpdiResolucion getPpdiResolucionByResolutionNumber(String resolutionNumber) {
        PpdiSignoDAO ps = new PpdiSignoDAO(null);
        return ps.getPpdiResolucionByResolutionNumber(resolutionNumber);
    }

    public boolean updateCpi(Cpis cpi) {
        CpisDAO cd = new CpisDAO();
        return cd.updateCPI(cpi);
    }

    public File concatenarPdfDoFile(List<Object[]> pdfBytes, String nombre) throws Exception {
// Path del fichero temporal
        final String pathFile = nombre;

// Creamos el fichero pdf necesario.
        FileOutputStream file = new FileOutputStream(pathFile);

// Objeto reader para añadir los pdf.
        PdfReader reader = null;

// Objeto para concatenar los pdf.
        PdfCopyFields copy = new PdfCopyFields(file);

// tamaño total de bytes
        int tam = 0;

// Recorremos los bytes y los vamos concatenando.
        int cont = 1;
        for (Object[] pdfByte : pdfBytes) {

            tam += pdfByte.length;
            reader = new PdfReader((byte[]) pdfByte[0]);
            copy.addDocument(reader);
            System.out.println(cont++ + ": " + pdfByte[1]);
        }

        copy.close();
        file.close();

        File f = new File(pathFile);

        return f;
    }

    public List<Notificacion> getAllNotificaciones() {
        NotificacionDAO nd = new NotificacionDAO(null);
        return nd.getAllNotificaciones();
    }

    public List<Desistimiento> getAllDesistimientos() {
        DesistimientoDAO dd = new DesistimientoDAO(null);
        return dd.getAllDesistimientos();
    }

    public List<Caducada> getAllCaducadas() {
        CaducadaDAO cd = new CaducadaDAO(null);
        return cd.getAllCaducadas();
    }

    public List<CambioNombre> getAllCambiosNombre() {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.buscarTodos();
    }

    public List<CambioDomicilio> getAllCambiosDomicilio() {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.buscarTodos();
    }

    public List<PrendaComercial> getAllPrendas() {
        PrendaComercialDAO cd = new PrendaComercialDAO(null);
        return cd.buscarTodos();
    }

    public List<LicenciaUso> getAllLicenciasUso() {
        LicenciaUsoDAO cd = new LicenciaUsoDAO(null);
        return cd.buscarTodos();
    }

    public List<SubLicenciaUso> getAllSubLicenciasUso() {
        SublicenciaUsoDAO cd = new SublicenciaUsoDAO(null);
        return cd.buscarTodos();
    }

    public List<Renovacion> getAllRenovaciones() {
        RenovacionDAO rd = new RenovacionDAO(null);
        return rd.buscarTodos();
    }

    public List<Desistida> getAllDesistidaRenovaciones() {
        DesistidaRDAO rd = new DesistidaRDAO(null);
        return rd.buscarTodos();
    }

    public List<Notificada> getAllNotificadaRenovaciones() {
        NotificadaRDAO rd = new NotificadaRDAO(null);
        return rd.buscarTodos();
    }

    public List<CaducadaRen> getAllCaducadaRenovaciones() {
        CaducadaRDAO rd = new CaducadaRDAO(null);
        return rd.buscarTodos();
    }

    public void llenarTablaModificacionesFormularios() {
        List<Transferencia> transferencias = getAllTransferencias();
        List<Notificacion> notificaciones = getAllNotificaciones();
        List<Desistimiento> desistimientos = getAllDesistimientos();
        List<Caducada> caducadas = getAllCaducadas();

        List<CambioNombre> cambiosNombre = getAllCambiosNombre();
        List<CambioDomicilio> cambiosDomicilio = getAllCambiosDomicilio();
        List<PrendaComercial> prendas = getAllPrendas();
        List<LicenciaUso> licencias = getAllLicenciasUso();
        List<SubLicenciaUso> sublicencias = getAllSubLicenciasUso();

        List<Renovacion> renovaciones = getAllRenovaciones();
        List<Desistida> desistidas = getAllDesistidaRenovaciones();
        List<Notificada> notificadas = getAllNotificadaRenovaciones();
        List<CaducadaRen> caducadasr = getAllCaducadaRenovaciones();

        List<ModificacionApp> modificaciones = new ArrayList<>();

        for (int i = 0; i < transferencias.size(); i++) {
            ModificacionApp modificacion = new ModificacionApp();
            modificacion.setDenominacion(transferencias.get(i).getDenominacion());
            modificacion.setObservacion("migrado");
            modificacion.setRegistro(transferencias.get(i).getRegistro());
            modificacion.setSolicitud(transferencias.get(i).getSolicitud());
            modificacion.setTipo("TRANSFERENCIA");
            modificacion.setUsuario("migracion");
            modificacion.setFecha(new Timestamp(new Date().getTime()));
            modificaciones.add(modificacion);
        }

        for (int i = 0; i < notificaciones.size(); i++) {
            ModificacionApp modificacion = new ModificacionApp();
            modificacion.setDenominacion(notificaciones.get(i).getDenominacion());
            modificacion.setObservacion("migrado");
            modificacion.setRegistro(notificaciones.get(i).getRegistro());
            modificacion.setSolicitud(notificaciones.get(i).getSolicitud());
            modificacion.setTipo("TRANSFERENCIA");
            modificacion.setUsuario("migracion");
            modificacion.setFecha(new Timestamp(new Date().getTime()));
            modificaciones.add(modificacion);
        }

        for (Desistimiento desistimiento : desistimientos) {
            ModificacionApp modificacion = new ModificacionApp();
            modificacion.setDenominacion(desistimiento.getDenominacion());
            modificacion.setObservacion("migrado");
            modificacion.setRegistro(desistimiento.getTitulo());
            modificacion.setSolicitud(desistimiento.getSolicitud());
            modificacion.setTipo("TRANSFERENCIA");
            modificacion.setUsuario("migracion");
            modificacion.setFecha(new Timestamp(new Date().getTime()));
            modificaciones.add(modificacion);
        }

        for (Caducada caducada : caducadas) {
            ModificacionApp modificacion = new ModificacionApp();
            modificacion.setDenominacion(caducada.getDenominacion());
            modificacion.setObservacion("migrado");
            modificacion.setRegistro(caducada.getRegistro());
            modificacion.setSolicitud(caducada.getSolicitud());
            modificacion.setTipo("TRANSFERENCIA");
            modificacion.setUsuario("migracion");
            modificacion.setFecha(new Timestamp(new Date().getTime()));
            modificaciones.add(modificacion);
        }

        for (CambioNombre cambioNombre : cambiosNombre) {
            ModificacionApp modificacion = new ModificacionApp();
            modificacion.setDenominacion(cambioNombre.getDenominacion());
            modificacion.setObservacion("migrado");
            modificacion.setRegistro(cambioNombre.getRegistro());
            modificacion.setSolicitud(cambioNombre.getSolicitud());
            modificacion.setTipo("CAMBIO DE NOMBRE");
            modificacion.setUsuario("migracion");
            modificacion.setFecha(new Timestamp(new Date().getTime()));
            modificaciones.add(modificacion);
        }

        for (CambioDomicilio cambioDomicilio : cambiosDomicilio) {
            ModificacionApp modificacion = new ModificacionApp();
            modificacion.setDenominacion(cambioDomicilio.getDenominacion());
            modificacion.setObservacion("migrado");
            modificacion.setRegistro(cambioDomicilio.getRegistro());
            modificacion.setSolicitud(cambioDomicilio.getSolicitud());
            modificacion.setTipo("CAMBIO DE DOMICILIO");
            modificacion.setUsuario("migracion");
            modificacion.setFecha(new Timestamp(new Date().getTime()));
            modificaciones.add(modificacion);
        }

        for (PrendaComercial prenda : prendas) {
            ModificacionApp modificacion = new ModificacionApp();
            modificacion.setDenominacion(prenda.getDenominacion());
            modificacion.setObservacion("migrado");
            modificacion.setRegistro(prenda.getRegistro());
            modificacion.setSolicitud(prenda.getSolicitud());
            modificacion.setTipo("PRENDA COMERCIAL");
            modificacion.setUsuario("migracion");
            modificacion.setFecha(new Timestamp(new Date().getTime()));
            modificaciones.add(modificacion);
        }

        for (LicenciaUso licencia : licencias) {
            ModificacionApp modificacion = new ModificacionApp();
            modificacion.setDenominacion(licencia.getDenominacion());
            modificacion.setObservacion("migrado");
            modificacion.setRegistro(licencia.getRegistro());
            modificacion.setSolicitud(licencia.getSolicitud());
            modificacion.setTipo("LICENCIA DE USO");
            modificacion.setUsuario("migracion");
            modificacion.setFecha(new Timestamp(new Date().getTime()));
            modificaciones.add(modificacion);
        }

        for (LicenciaUso licencia : licencias) {
            ModificacionApp modificacion = new ModificacionApp();
            modificacion.setDenominacion(licencia.getDenominacion());
            modificacion.setObservacion("migrado");
            modificacion.setRegistro(licencia.getRegistro());
            modificacion.setSolicitud(licencia.getSolicitud());
            modificacion.setTipo("LICENCIA DE USO");
            modificacion.setUsuario("migracion");
            modificacion.setFecha(new Timestamp(new Date().getTime()));
            modificaciones.add(modificacion);
        }

        for (SubLicenciaUso sublicencia : sublicencias) {
            ModificacionApp modificacion = new ModificacionApp();
            modificacion.setDenominacion(sublicencia.getDenominacion());
            modificacion.setObservacion("migrado");
            modificacion.setRegistro(sublicencia.getRegistro());
            modificacion.setSolicitud(sublicencia.getSolicitud());
            modificacion.setTipo("SUBLICENCIA DE USO");
            modificacion.setUsuario("migracion");
            modificacion.setFecha(new Timestamp(new Date().getTime()));
            modificaciones.add(modificacion);
        }

        for (Renovacion renovacion : renovaciones) {
            ModificacionApp modificacion = new ModificacionApp();
            modificacion.setDenominacion(renovacion.getDenominacion());
            modificacion.setObservacion("migrado");
            modificacion.setRegistro(renovacion.getRegistroNo());
            modificacion.setSolicitud(renovacion.getSolicitudSenadi());
            modificacion.setTipo("RENOVACION");
            modificacion.setUsuario("migracion");
            modificacion.setFecha(new Timestamp(new Date().getTime()));
            modificaciones.add(modificacion);
        }

        for (Desistida desistida : desistidas) {
            ModificacionApp modificacion = new ModificacionApp();
            modificacion.setDenominacion(desistida.getDenominacion());
            modificacion.setObservacion("migrado");
            modificacion.setRegistro(desistida.getRegistroNo());
            modificacion.setSolicitud(desistida.getSolicitudSenadi());
            modificacion.setTipo("RENOVACION");
            modificacion.setUsuario("migracion");
            modificacion.setFecha(new Timestamp(new Date().getTime()));
            modificaciones.add(modificacion);
        }

        for (Notificada notificada : notificadas) {
            ModificacionApp modificacion = new ModificacionApp();
            modificacion.setDenominacion(notificada.getDenominacion());
            modificacion.setObservacion("migrado");
            modificacion.setRegistro(notificada.getRegistroNo());
            modificacion.setSolicitud(notificada.getSolicitudSenadi());
            modificacion.setTipo("RENOVACION");
            modificacion.setUsuario("migracion");
            modificacion.setFecha(new Timestamp(new Date().getTime()));
            modificaciones.add(modificacion);
        }

        for (CaducadaRen caducadaRen : caducadasr) {
            ModificacionApp modificacion = new ModificacionApp();
            modificacion.setDenominacion(caducadaRen.getDenominacion());
            modificacion.setObservacion("migrado");
            modificacion.setRegistro(caducadaRen.getRegistroNo());
            modificacion.setSolicitud(caducadaRen.getSolicitudSenadi());
            modificacion.setTipo("RENOVACION");
            modificacion.setUsuario("migracion");
            modificacion.setFecha(new Timestamp(new Date().getTime()));
            modificaciones.add(modificacion);
        }

        Collections.sort(modificaciones, new Comparator<ModificacionApp>() {
            @Override
            public int compare(ModificacionApp o1, ModificacionApp o2) {
                return o1.getSolicitud().compareToIgnoreCase(o2.getSolicitud());
            }
        });

        int size = modificaciones.size();
        System.out.println("size: " + size);

        System.out.println("**************************** inicio *****************************");
        List<String> errores = new ArrayList<>();
        for (int i = 0; i < modificaciones.size(); i++) {
            ModificacionApp modaux = modificaciones.get(i);
            if (saveModificacionApp(modaux)) {
                System.out.println((i + 1) + "/" + size + ": guardada: " + modaux.toString());
            } else {
                String error = modaux.toString();
                errores.add(error);
                System.err.println((i + 1) + "/" + size + ": error: " + modaux.toString());
            }
        }

        System.out.println("*************************** fin *********************************");

        if (!errores.isEmpty()) {
            for (String error : errores) {
                System.out.println(error);
            }
        } else {
            System.out.println("Ejecución terminada sin ningún problema");
        }

    }

    public boolean saveModificacionApp(ModificacionApp modificacion) {
        ModificacionDAO md = new ModificacionDAO();
        return md.insertModificacionApp(modificacion);
    }

    public List<RenewalForm> getLicenciasRezagoBytType(String type, String transactionalMotive) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getLicenciasRezagoBytType(type, transactionalMotive);
    }

    public List<RenewalForm> getRenewalsRezagoBytType(String type, String transactionalMotive) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getRenewalsRezagoBytType(type, transactionalMotive);
    }

    public List<RenewalForm> getRenewalsRezagoBytTypeAndCriterio(String type, String transactionalMotive, String criterio) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getRenewalsRezagoBytTypeAndCriteria(type, transactionalMotive, criterio);
    }

    public List<RenewalForm> getLicenciasRezagoBytTypeAndCriteria(String type, String transactionalMotive, String criterio) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getLicenciasRezagoBytTypeAndCriteria(type, transactionalMotive, criterio);
    }

    public List<RenewalForm> getRenewalsRezagoSublicBytTypeAndCriteria(String type, String transactionalMotive, String criterio) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getRenewalsRezagoSublicBytTypeAndCriteria(type, transactionalMotive, criterio);
    }

    public List<RenewalForm> getRenewalsRezagoBytTypeAndFecha(String type, String transactionalMotive, Date ini, Date fin) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getRenewalsRezagoBytTypeAndFecha(type, transactionalMotive, ini, fin);
    }

    public List<RenewalForm> getLicenciasRezagoBytTypeAndFecha(String type, String transactionalMotive, Date ini, Date fin) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getLicenciasRezagoBytTypeAndFecha(type, transactionalMotive, ini, fin);
    }

    public List<RenewalForm> getRenewalsRezagoSublicBytTypeAndFecha(String type, String transactionalMotive, Date ini, Date fin) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getRenewalsRezagoSublicBytTypeAndFecha(type, transactionalMotive, ini, fin);
    }

    public List<Transferencia> loadTransferenciasFromRenewals(List<RenewalForm> renewals) {
        List<Transferencia> transferencias = new ArrayList<>();
        for (int i = 0; i < renewals.size(); i++) {
            RenewalForm rf = renewals.get(i);
            Transferencia transferencia = new Transferencia();
            transferencia.setFechaPresentacion(rf.getApplicationDate());
            transferencia.setIdRenewalForm(rf.getId());
            transferencia.setSolicitud(rf.getApplicationNumber());
            transferencia.setRegistro(rf.getTransactionNumber());
            if (rf.getFormId() == 31 || rf.getFormId() == 32 || rf.getFormId() == 33) {
                transferencia.setR1("PATENTE");
            } else {
                transferencia.setR1("MARCA");
            }

            transferencias.add(transferencia);
        }
        return transferencias;
    }

    public List<CambioNombre> loadCambiosNombreFromRenewals(List<RenewalForm> renewals) {
        List<CambioNombre> cambiosNombre = new ArrayList<>();
        for (int i = 0; i < renewals.size(); i++) {
            RenewalForm rf = renewals.get(i);
            CambioNombre cambioNombre = new CambioNombre();
            cambioNombre.setFechaPresentacion(rf.getApplicationDate());
            cambioNombre.setIdRenewalForm(rf.getId());
            cambioNombre.setSolicitud(rf.getApplicationNumber());
            cambioNombre.setRegistro(rf.getTransactionNumber());
            if (rf.getFormId() == 31 || rf.getFormId() == 32 || rf.getFormId() == 33) {
                cambioNombre.setObservacion("PATENTE");
            } else {
                cambioNombre.setObservacion("MARCA");
            }

            cambiosNombre.add(cambioNombre);
        }
        return cambiosNombre;
    }

    public List<LicenciaUso> loadLicenciaUsoFromRenewals(List<RenewalForm> renewals) {
        List<LicenciaUso> licenciasUso = new ArrayList<>();
        for (int i = 0; i < renewals.size(); i++) {
            RenewalForm rf = renewals.get(i);
            LicenciaUso licenciaUso = new LicenciaUso();
            licenciaUso.setFechaPresentacion(rf.getApplicationDate());
            licenciaUso.setIdRenewalForm(rf.getId());
            licenciaUso.setSolicitud(rf.getApplicationNumber());
            licenciaUso.setRegistro(rf.getTransactionNumber());
            if (rf.getFormId() == 31 || rf.getFormId() == 32 || rf.getFormId() == 33) {
                licenciaUso.setR1("PATENTE");
            } else {
                licenciaUso.setR1("MARCA");
            }

            licenciasUso.add(licenciaUso);
        }
        return licenciasUso;
    }

    public List<SubLicenciaUso> loadSublicenciaUsoFromRenewals(List<RenewalForm> renewals) {
        List<SubLicenciaUso> sublicenciasUso = new ArrayList<>();
        for (int i = 0; i < renewals.size(); i++) {
            RenewalForm rf = renewals.get(i);
            SubLicenciaUso sublicenciaUso = new SubLicenciaUso();
            sublicenciaUso.setFechaPresentacion(rf.getApplicationDate());
            sublicenciaUso.setIdRenewalForm(rf.getId());
            sublicenciaUso.setSolicitud(rf.getApplicationNumber());
            sublicenciaUso.setRegistro(rf.getTransactionNumber());
            if (rf.getFormId() == 31 || rf.getFormId() == 32 || rf.getFormId() == 33) {
                //se utilizó n_j solo como referencia en rezago es una variable cualquiera
                //que solo guarda el tipo de la sublicencia
                sublicenciaUso.setN_j("PATENTE");
            } else {
                sublicenciaUso.setN_j("MARCA");
            }

            sublicenciasUso.add(sublicenciaUso);
        }
        return sublicenciasUso;
    }

    public List<CambioDomicilio> loadCambiosDomicilioFromRenewals(List<RenewalForm> renewals) {
        List<CambioDomicilio> cambiosDomicilio = new ArrayList<>();
        for (int i = 0; i < renewals.size(); i++) {
            RenewalForm rf = renewals.get(i);
            CambioDomicilio cambioDomicilio = new CambioDomicilio();
            cambioDomicilio.setFechaPresentacion(rf.getApplicationDate());
            cambioDomicilio.setIdRenewalForm(rf.getId());
            cambioDomicilio.setSolicitud(rf.getApplicationNumber());
            cambioDomicilio.setRegistro(rf.getTransactionNumber());
            if (rf.getFormId() == 31 || rf.getFormId() == 32 || rf.getFormId() == 33) {
                cambioDomicilio.setObservacion("PATENTE");
            } else {
                cambioDomicilio.setObservacion("MARCA");
            }

            cambiosDomicilio.add(cambioDomicilio);
        }
        return cambiosDomicilio;
    }

    public List<PrendaComercial> loadPrendasComercialesFromRenewals(List<RenewalForm> renewals) {
        List<PrendaComercial> prendasComerciales = new ArrayList<>();
        for (int i = 0; i < renewals.size(); i++) {
            RenewalForm rf = renewals.get(i);
            PrendaComercial prendaComercial = new PrendaComercial();
            prendaComercial.setFechaPresentacion(rf.getApplicationDate());
            prendaComercial.setIdRenewalForm(rf.getId());
            prendaComercial.setSolicitud(rf.getApplicationNumber());
            prendaComercial.setRegistro(rf.getTransactionNumber());
            if (rf.getFormId() == 31 || rf.getFormId() == 32 || rf.getFormId() == 33) {
                prendaComercial.setR1("PATENTE");
            } else {
                prendaComercial.setR1("MARCA");
            }

            prendasComerciales.add(prendaComercial);
        }
        return prendasComerciales;
    }

    public boolean saveModificacionApp(String denominacion, String registro, String solicitud, String tipo, String usuario) {
        ModificacionApp mapp = new ModificacionApp();
        mapp.setDenominacion(denominacion != null ? denominacion : "");
        mapp.setFecha(new Timestamp(new Date().getTime()));
        mapp.setObservacion("rezago pasado");
        mapp.setRegistro(registro);
        mapp.setSolicitud(solicitud);
        mapp.setTipo(tipo);
        mapp.setUsuario(usuario);
        mapp.setModo("MARCA");
        mapp.setActivo(true);
        return saveModificacionApp(mapp);
    }

    public PatentForms getPatentFormsDepurada(Integer debug_id) {
        RenewalFormDAO rd = new RenewalFormDAO();
        return rd.getPatentFormsDepurada(debug_id);
    }

    public PpdiTituloPatente getPpdiTituloPatenteByNumeroTitulo(String numeroTitulo) {
        PpdiTituloPatenteDAO pd = new PpdiTituloPatenteDAO(null);
        return pd.getPpdiTituloPatenteByNumeroTitulo(numeroTitulo);
    }

    public PpdiSolicitudPatente getPpdiSolicitudPatenteByTramite(String tramite) {
        PpdiSolicitudPatenteDAO pd = new PpdiSolicitudPatenteDAO(null);
        return pd.getPpdiSolicitudPatenteByTramite(tramite);
    }

    public PpdiSolicitudPatente getPpdiSolicitudPatenteByExpedient(String expedient) {
        PpdiSolicitudPatenteDAO ps = new PpdiSolicitudPatenteDAO(null);
        return ps.getPpdiSolicitudPatenteByExpediente(expedient);
    }

    public PpdiTituloPatente getPpdiTituloPatenteByCodigoSolicitudPatente(Integer codigoSolicitudPatente) {
        PpdiTituloPatenteDAO pd = new PpdiTituloPatenteDAO(null);
        return pd.getPpdiTituloPatenteByCodigoSolicitudPatente(codigoSolicitudPatente);
    }

    public String getPersonasByTipoPersona(PpdiSolicitudPatente psp, String tipoPersona) {
        List<PpdiPersona> perspat = psp.getPpdiPersonas();

        System.out.println("Estoy llegando por aquí");
        String nombres = "";
        for (int i = 0; i < perspat.size(); i++) {
            PpdiPersona pper = perspat.get(i);
//            if(pper.getTipoPersona().equals(tipoPersona)){
            nombres += pper.getNombrePersona() + ", ";
//            }
        }
        if (!nombres.isEmpty()) {
            nombres = nombres.substring(nombres.length() - 2, nombres.length());
        }
        return nombres;
    }

    public List<PersonRenewal> getPersonRenewalByIdRenewalAndType(Integer idRenewal, String type) {
        RenewalFormDAO rd = new RenewalFormDAO();
        List<PersonRenewal> pr = rd.getPersonsRenewalByIdRenewalAndType(idRenewal, type);
        return pr;
    }

    public String getNamesFromPersonRenewal(List<PersonRenewal> persons) {
        String names = "";
        for (int i = 0; i < persons.size(); i++) {
            names += persons.get(i).getName() + ", ";
        }
        if (!names.isEmpty()) {
            names = names.substring(0, names.length() - 2);
        }
        return names;
    }

    public List<PersonRenewalName> getTitularesActuales(Integer idRenewal) {
        RenewalFormDAO rd = new RenewalFormDAO();
        List<PersonRenewalName> prn = rd.getPersonsRenewalNameByIdRenewal(idRenewal);
        return prn;
    }

    /**
     * Devuelve un String con todos los nombres de la lista persons separados
     * por ', ', El valor boolean newname es para sabes si se extrae los nuevos
     * o los actuales nombres
     *
     * @param persons
     * @param newname
     * @return nombres de los titulares
     */
    public String getNamesFromPersonRenewalName(List<PersonRenewalName> persons, boolean newname) {
        String names = "";
        for (int i = 0; i < persons.size(); i++) {
            if (newname) {
                names += persons.get(i).getNewName() + ", ";
            } else {
                names += persons.get(i).getTitleName() + ", ";
            }
        }
        if (!names.isEmpty()) {
            names = names.substring(0, names.length() - 2);
        }
        return names;
    }

    /**
     * Devuelve un String con todos las direcciones de la lista persons
     * separados por ', ', El valor boolean newname es para sabes si se extrae
     * los nuevos o las actuales direcciones
     *
     * @param persons
     * @param newname
     * @return direcciones de los titulares
     */
    public String getAddressFromPersonRenewalName(List<PersonRenewalName> persons, boolean newname) {
        String address = "";
        for (int i = 0; i < persons.size(); i++) {
            if (newname) {
                address += persons.get(i).getNewAddress() + ", ";
            } else {
                address += persons.get(i).getTitleAddress() + ", ";
            }
        }
        if (!address.isEmpty()) {
            address = address.substring(0, address.length() - 2);
        }
        return address;
    }

    /**
     * Devuelve un String con todos las identificaciones de la lista persons
     * separados por ', '
     *
     * @param persons
     * @return identificaciones de los titulares
     */
    public String getIdentificationFromPersonRenewalName(List<PersonRenewalName> persons) {
        String identification = "";
        for (int i = 0; i < persons.size(); i++) {
            identification += persons.get(i).getIdentificationNumber() + ", ";
        }
        if (!identification.isEmpty()) {
            identification = identification.substring(0, identification.length() - 2);
        }
        return identification;
    }

    public String getEmailsFromPersonRenewal(List<PersonRenewal> persons) {
        String emails = "";
        for (int i = 0; i < persons.size(); i++) {
            emails += persons.get(i).getEmail() + ", ";
        }
        if (!emails.isEmpty()) {
            emails = emails.substring(0, emails.length() - 2);
        }
        return emails;
    }

    public List<ModificacionApp> getModificacionesAppByTipo(String tipo) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getModificacionesApp(tipo);
    }

    public List<ModificacionApp> getModificacionesAppByTramites(String solicides) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getModificacionesAppByTramites(solicides);
    }

    public List<CambioNombre> getTodosCambioNombreActualNotModificacionesApp(String solicitudes) {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getTodosActualNotModificaciones(solicitudes);
    }

    public ModificacionApp getModificacionApp(String solicitud) {
        ModificacionDAO md = new ModificacionDAO();
        return md.getModificacionApp(solicitud);
    }

//    public void depurarModificacionesApp() {
//
//        String sols = "SENADI-2021-5791,SENADI-2022-71342,SENADI-2022-71360,SENADI-2021-5782,SENADI-2021-5783,SENADI-2022-71385,SENADI-2021-5785,SENADI-2021-5819,SENADI-2021-5797,SENADI-2022-71425,SENADI-2024-36759,SENADI-2021-3666,SENADI-2021-76195,SENADI-2024-40192,SENADI-2021-8052,SENADI-2021-8054,SENADI-2021-8053,SENADI-2021-8032,SENADI-2021-8029,SENADI-2022-74104,SENADI-2022-74066,SENADI-2021-60111,SENADI-2021-60096,SENADI-2022-74070,SENADI-2022-74079,SENADI-2021-60120,SENADI-2022-74109,SENADI-2022-74125,SENADI-2022-92442,SENADI-2022-74369,SENADI-2022-74385,SENADI-2022-74384,SENADI-2022-74383,SENADI-2022-74382,SENADI-2022-74381,SENADI-2022-74380,SENADI-2022-74379,SENADI-2022-74378,SENADI-2022-74908,SENADI-2022-74906,SENADI-2022-74901,SENADI-2021-8051,SENADI-2022-71359,SENADI-2022-75074,SENADI-2022-75076,SENADI-2021-58300,SENADI-2021-58301,SENADI-2022-75345,SENADI-2022-75617,SENADI-2019-86591,SENADI-2022-75619,SENADI-2019-86595,SENADI-2023-19772,SENADI-2023-30667,SENADI-2023-29052,SENADI-2023-30692,SENADI-2023-30677,SENADI-2023-30686,SENADI-2023-30689,SENADI-2023-25711,SENADI-2023-34846,SENADI-2023-31603,SENADI-2022-68580,SENADI-2023-31605,SENADI-2023-64433,SENADI-2023-64429,SENADI-2023-73170,SENADI-2023-64425,SENADI-2023-49594,SENADI-2023-49593,SENADI-2023-91720,SENADI-2023-63959,SENADI-2023-25712,SENADI-2023-34849,SENADI-2022-56466,SENADI-2023-48539,SENADI-2023-64434,SENADI-2023-70077,SENADI-2023-70078,SENADI-2023-70076,SENADI-2023-67097,SENADI-2023-61855,SENADI-2023-49635,SENADI-2022-74886,SENADI-2022-74871,SENADI-2022-74864,SENADI-2022-70540,SENADI-2022-70557,SENADI-2022-70569,SENADI-2022-70591,SENADI-2022-70600,SENADI-2022-70642,SENADI-2023-65140,SENADI-2022-76723,SENADI-2022-76724,SENADI-2023-65144,SENADI-2022-76726,SENADI-2022-70579,SENADI-2023-65137,SENADI-2023-65131,SENADI-2023-65143,SENADI-2022-70660,SENADI-2022-77764,SENADI-2023-65133,SENADI-2023-65139,SENADI-2023-65148,SENADI-2023-65132";
//
//        String[] solicitudes = sols.split(",");
//
//        int cont = 0;
//        for (int i = 0; i < solicitudes.length; i++) {
//            ModificacionApp ma = getModificacionApp(solicitudes[i], true);
//            if (ma.getId() == null) {
//                CambioNombre cn = getCambioNombreBySolicitud(solicitudes[i]);
//                if (cn.getId() != null) {
//                    cont++;                    
//                    saveModificacionApp(cn.getDenominacion(),cn.getRegistro(),cn.getSolicitud(),"CAMBIO DE NOMBRE",cn.getResponsable());
//                    System.out.println(cont+": "+cn.getSolicitud());
//                }
//            }
//
//        }
//
//    }
    public void depurarLicencias() {
        String tramites = "'SENADI-2022-97986','SENADI-2022-98506','SENADI-2022-98027','SENADI-2022-98021','SENADI-2022-98421','SENADI-2022-98420','SENADI-2022-98402','SENADI-2022-98400','SENADI-2022-98397','SENADI-2022-98191','SENADI-2022-98409','SENADI-2022-98032','SENADI-2022-98349','SENADI-2023-7684','SENADI-2022-97850','SENADI-2022-97848','SENADI-2022-97846','SENADI-2022-97844','SENADI-2022-97842','SENADI-2022-97840','SENADI-2022-97838','SENADI-2022-97836','SENADI-2022-97834','SENADI-2022-97832','SENADI-2022-97830','SENADI-2022-97828','SENADI-2022-97822','SENADI-2022-97811','SENADI-2022-97815','SENADI-2022-97854','SENADI-2022-98522','SENADI-2022-98341','SENADI-2022-98535','SENADI-2022-98374','SENADI-2022-98533','SENADI-2022-98124','SENADI-2022-98411','SENADI-2022-98539','SENADI-2022-98433','SENADI-2022-98009','SENADI-2022-98003','SENADI-2022-98481','SENADI-2022-98034','SENADI-2022-97974','SENADI-2022-98425','SENADI-2022-98294','SENADI-2022-98528','SENADI-2022-98501','SENADI-2022-98120','SENADI-2022-98247','SENADI-2022-98244','SENADI-2022-98380','SENADI-2022-98378','SENADI-2022-98376','SENADI-2022-98238','SENADI-2022-98417','SENADI-2022-98410','SENADI-2022-98234','SENADI-2022-98524','SENADI-2022-98520','SENADI-2022-98226','SENADI-2022-98516','SENADI-2022-98089','SENADI-2022-98020','SENADI-2022-98001','SENADI-2022-97997','SENADI-2022-97971','SENADI-2022-97969','SENADI-2022-97966','SENADI-2022-97958','SENADI-2022-97944','SENADI-2022-97921','SENADI-2022-97925','SENADI-2022-97920','SENADI-2022-97933','SENADI-2022-98414','SENADI-2022-98383','SENADI-2022-98497','SENADI-2022-98393','SENADI-2022-98489','SENADI-2022-98485','SENADI-2022-98472','SENADI-2022-98220','SENADI-2022-98218','SENADI-2022-98214','SENADI-2022-98492','SENADI-2022-98405','SENADI-2022-98390','SENADI-2022-98466','SENADI-2022-98463','SENADI-2022-98170','SENADI-2022-98323','SENADI-2022-98328','SENADI-2022-98168','SENADI-2022-98316','SENADI-2022-98165','SENADI-2022-98514','SENADI-2022-98542','SENADI-2022-98569','SENADI-2022-98176','SENADI-2022-98185','SENADI-2022-98581','SENADI-2022-98183','SENADI-2022-98179','SENADI-2022-98586','SENADI-2022-98609','SENADI-2022-98174','SENADI-2022-98197','SENADI-2022-98598','SENADI-2022-98194','SENADI-2022-98596','SENADI-2022-98181','SENADI-2022-98210','SENADI-2022-98207','SENADI-2022-98264','SENADI-2022-98335','SENADI-2022-98278','SENADI-2022-98005','SENADI-2022-97982','SENADI-2022-97979','SENADI-2022-97949','SENADI-2022-98370','SENADI-2022-98282','SENADI-2022-98530','SENADI-2022-98512','SENADI-2022-98287','SENADI-2022-98499','SENADI-2022-98503','SENADI-2022-98288','SENADI-2022-98493','SENADI-2022-98147','SENADI-2022-98149','SENADI-2022-98566','SENADI-2022-98555','SENADI-2022-98350','SENADI-2022-98327','SENADI-2022-98291','SENADI-2022-98387','SENADI-2022-98241','SENADI-2022-98406','SENADI-2022-98296','SENADI-2022-98372','SENADI-2022-98476','SENADI-2022-98356','SENADI-2022-98386','SENADI-2022-98480','SENADI-2022-98271','SENADI-2022-98270','SENADI-2022-98603','SENADI-2022-98588','SENADI-2022-98205','SENADI-2022-98201','SENADI-2022-98590','SENADI-2022-98199','SENADI-2022-98464','SENADI-2022-98429','SENADI-2022-98427','SENADI-2022-98468','SENADI-2022-98102','SENADI-2022-98475','SENADI-2022-98098','SENADI-2022-98094','SENADI-2022-98487','SENADI-2023-7640','SENADI-2023-7632'";
        LicenciaUsoDAO ld = new LicenciaUsoDAO(null);
        List<LicenciaUso> licencias = ld.getLicenciasUsoParaDepurar();
        System.out.println(licencias.size());
        for (int i = 0; i < licencias.size(); i++) {
            LicenciaUso lic = licencias.get(i);
            SubLicenciaUso aux = new SubLicenciaUso();
            aux.setAbogadoPatrocinador("");
            aux.setApoderadoRepresentante(lic.getApoderadoRepresentante());
            aux.setCancelado(lic.getCancelado());
            aux.setCasilleroJudicial(lic.getCasilleroJudicial());
            aux.setCasilleroSenadi(lic.getCasilleroSenadi());
            aux.setCertificadoEmitido(lic.isCertificadoEmitido());
            aux.setComprobante(lic.getComprobante());
            aux.setDenominacion(lic.getDenominacion());
            aux.setEmail(lic.getEmail());
            aux.setFechaContrato(lic.getFechaContrato());
            aux.setFechaNotificacion(lic.getFechaNotificacion());
            aux.setFechaNotSublicencia(lic.getFechaNotProvReg());
            aux.setFechaPresentacion(lic.getFechaPresentacion());
            aux.setFechaRegistro(lic.getFechaRegistro());
            aux.setFechaResolucion(lic.getFechaResolucion());
            aux.setFechaSublicencia(lic.getFechaLicencia());
            aux.setFechaVenceContrato(lic.getFechaVenceContrato());
            aux.setIdRenewalForm(lic.getIdRenewalForm());
            aux.setN_j(lic.getN_j());
            aux.setNotificacion(lic.getNotificacion());
            aux.setNotificacionEmitida(lic.isNotificacionEmitida());
            aux.setRegContAnteriorNo(lic.getRegContAnteriorNo());
            aux.setRegistro(lic.getRegistro());
            aux.setResolucionNo(lic.getResolucionNo());
            aux.setResponsable(lic.getResponsable());
            aux.setRo(lic.getRo());
            aux.setSigno(lic.getSigno());
            aux.setSolicitud(lic.getSolicitud());
            aux.setSublicenciaNo(lic.getLicenciaNo());
            aux.setSublicenciante(lic.getLicenciante());
            aux.setSublicenciatario(lic.getLicenciatario());
            aux.setT_ciudad(lic.getT_ciudad());
            aux.setT_pais(lic.getT_pais());
            aux.setTiempo(lic.getTiempo());
            aux.setTipoEstado(lic.getTipoEstado());
            aux.setTipoVigen(lic.getTipoVigen());
            aux.setVenceContrato(lic.getVenceContrato());
            aux.setVigen(lic.getVigen());

            if (saveSublicenciaUso(aux)) {
                System.out.println("sub " + (i + 1) + ": " + aux.getSolicitud() + " SUBLICENCIA GUARDADA");
            } else {
                System.out.println("sub " + (i + 1) + ": " + aux.getSolicitud() + " ERROR AL GUARDAR SUBLICENCIA");
            }

        }
    }

    ///////////// SECCIÓN DEL SCHEDULER PARA ABANDONOS ////////////////////
    public List<Notificacion> getAbandonosErjafeVencidos(int dias) {
        AbandonoDAO ad = new AbandonoDAO(null);
        return ad.getAbandonosErjafeVencidos(dias);
    }

    public List<Notificacion> getAbandonosSinFinesSemana(int dias, String tipoAbandono) {
        AbandonoDAO ad = new AbandonoDAO(null);
        return ad.getAbandonosSinFinesSemana(dias, tipoAbandono);
    }

    public List<CambioNombre> getAbandonosCNErjafeVencidos(int dias) {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getAbandonosErjafeVencidos(dias);
    }

    public List<CambioNombre> getAbandonosCNSinFinesSemana(int dias, String tipoAbandono) {
        CambioNombreDAO cd = new CambioNombreDAO(null);
        return cd.getAbandonosSinFinesSemana(dias, tipoAbandono);
    }

    public List<CambioDomicilio> getAbandonosCDErjafeVencidos(int dias) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.getAbandonosErjafeVencidos(dias);
    }

    public List<CambioDomicilio> getAbandonosCDSinFinesSemana(int dias, String tipoAbandono) {
        CambioDomicilioDAO cd = new CambioDomicilioDAO(null);
        return cd.getAbandonosSinFinesSemana(dias, tipoAbandono);
    }

    public List<PrendaComercial> getAbandonosPrendaErjafeVencidos(int dias) {
        PrendaComercialDAO cd = new PrendaComercialDAO(null);
        return cd.getAbandonosErjafeVencidos(dias);
    }

    public List<PrendaComercial> getAbandonosPrendaSinFinesSemana(int dias, String tipoAbandono) {
        PrendaComercialDAO cd = new PrendaComercialDAO(null);
        return cd.getAbandonosSinFinesSemana(dias, tipoAbandono);
    }

    public List<LicenciaUso> getAbandonosLicenciaErjafeVencidos(int dias) {
        LicenciaUsoDAO cd = new LicenciaUsoDAO(null);
        return cd.getAbandonosErjafeVencidos(dias);
    }

    public List<LicenciaUso> getAbandonosLicenciaSinFinesSemana(int dias, String tipoAbandono) {
        LicenciaUsoDAO cd = new LicenciaUsoDAO(null);
        return cd.getAbandonosSinFinesSemana(dias, tipoAbandono);
    }

    public List<SubLicenciaUso> getAbandonosSublicErjafeVencidos(int dias) {
        SublicenciaUsoDAO cd = new SublicenciaUsoDAO(null);
        return cd.getAbandonosErjafeVencidos(dias);
    }

    public List<SubLicenciaUso> getAbandonosSublicSinFinesSemana(int dias, String tipoAbandono) {
        SublicenciaUsoDAO cd = new SublicenciaUsoDAO(null);
        return cd.getAbandonosSinFinesSemana(dias, tipoAbandono);
    }

    ///////////// FIN SECCIÓN DEL SCHEDULER PARA ABANDONOS ///////////////////
    /// @return /
                      
    public List<ModificationScope> getModificationScopesSent() {
        ModificationScopeDAO md = new ModificationScopeDAO(null);
        return md.getScopesSent();
    }

    public List<ModificationScope> getScopesByCriterio(String criterio) {
        ModificationScopeDAO md = new ModificationScopeDAO(null);
        return md.getScopesByCriterio(criterio);
    }

    public List<ModificationScope> getScopesBySubmissionDate(Date start, Date end) {
        ModificationScopeDAO md = new ModificationScopeDAO(null);
        return md.getScopesBySubmissionDate(start, end);
    }

    public boolean updateScope(ModificationScope scope) {
        ModificationScopeDAO md = new ModificationScopeDAO(scope);
        try {
            md.update();
            return true;
        } catch (Exception ex) {
            System.err.println("Error al actualizar el alcance de modificación " + scope.getId() + ": " + ex);
            return false;
        }
    }

}
