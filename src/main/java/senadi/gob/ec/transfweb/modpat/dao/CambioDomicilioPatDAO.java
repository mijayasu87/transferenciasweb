/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.modpat.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.modpat.CambioDomicilioPat;
//import senadi.gob.ec.transfweb.modpat.CambioNombrePat;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author micharesp
 */
public class CambioDomicilioPatDAO extends DAOAbstractPat<CambioDomicilioPat> {

    public CambioDomicilioPatDAO(CambioDomicilioPat cd) {
        super(cd);
    }

    @Override
    public List<CambioDomicilioPat> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select c from CambioDomicilioPat c order by c.id desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioDomicilioPat> getCambiosDomicilioByTipo(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from CambioDomicilioPat c where c.tipoEstado = '" + tipo + "' ORDER BY c.fechaCertificado DESC, c.certificado DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<CambioDomicilioPat> getCambiosDomicilioByDenominacion(String denominacion) {
        Query query = this.getEntityManager().createQuery("Select c from CambioDomicilioPat c where c.denominacion like '%" + denominacion + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<CambioDomicilioPat> getCambiosDomicilioNotificada(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from CambioDomicilioPat c where c.tipoEstado = '" + tipo + "' ORDER BY c.fechaNotificacion DESC, c.notificacion DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<CambioDomicilioPat> getCambiosDomicilioByCriteriaAndType(String text, String type) {
        Query query = this.getEntityManager().createQuery("Select c from CambioDomicilioPat c where c.tipoEstado = '" + type + "' and (c.solicitud LIKE '%" + text + "%' or c.denominacion LIKE '%" + text + "%' "
                + "or c.titularActual LIKE '%" + text + "%') ORDER BY c.fechaCertificado DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioDomicilioPat> getCambiosDomicilioByFecha(Date ini, Date fin) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from CambioDomicilioPat t where t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaCertificado BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioDomicilioPat> getCambiosDomicilioByFechaAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from CambioDomicilioPat t where t.tipoEstado = '" + type + "' and (t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaCertificado BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "') ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioDomicilioPat> getCambiosDomicilioByFechaNotificacionAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from CambioDomicilioPat t where t.tipoEstado = '" + type + "' and t.fechaNotificacion BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioDomicilioPat> getCambiosDomicilioByFechaCertificadoAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from CambioDomicilioPat t where t.tipoEstado = '" + type + "' and t.fechaCertificado BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public Integer getNextCambioDomicilioCertificado() {
        Query query = this.getEntityManager().createQuery("Select c from CambioDomicilioPat c where c.tipoEstado = 'CERTIFICADO' ORDER BY c.fechaCertificado desc, c.certificado desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return 1;
        } else {
            CambioDomicilioPat r = (CambioDomicilioPat) query.getResultList().get(0);
            return r.getCertificado() + 1;
        }
    }

    public boolean validarExistenciaCambioDomicilio(CambioDomicilioPat cambioDomicilio) {
        Query query = this.getEntityManager().createQuery("Select t from CambioDomicilioPat t where t.solicitud = '" + cambioDomicilio.getSolicitud() + "' and t.id != " + cambioDomicilio.getId() + "");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean existeTramite(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from CambioDomicilioPat t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public CambioDomicilioPat getCambioDomicilioBySolicitud(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from CambioDomicilioPat t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (!query.getResultList().isEmpty()) {
            return (CambioDomicilioPat) query.getResultList().get(0);
        } else {
            return new CambioDomicilioPat();
        }

    }

    public int getNextNumeroNotificacionCD(Date fechaElaboraNotificacion) {
        Query query = this.getEntityManager().createQuery("Select n from CambioDomicilioPat n where n.tipoEstado = 'NOTIFICADA' and n.id = (Select MAX(n1.id) from CambioDomicilioPat n1 where n1.tipoEstado = 'NOTIFICADA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<CambioDomicilioPat> cambios = query.getResultList();
        if (cambios.isEmpty()) {
            return 1;
        } else {
            CambioDomicilioPat c = cambios.get(0);

            int yearElNot = fechaElaboraNotificacion.getYear() + 1900;
            int yearNot = c.getFechaNotificacion().getYear() + 1900;

            if (yearElNot == yearNot) {
                return c.getNotificacion() + 1;
            } else if (yearElNot > yearNot) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * **Esperando a ver como es la funcionalidad*************
     */
    public int getNextNumeroDesistimiento() {
        Query query = this.getEntityManager().createQuery("Select n from CambioDomicilioPat n where n.tipoEstado = 'DESISTIDA' and n.id = (Select MAX(n1.id) from CambioDomicilioPat n1 where n1.tipoEstado = 'DESISTIDA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<CambioDomicilioPat> cambios = query.getResultList();
        if (cambios.isEmpty()) {
            return 1;
        } else {
            CambioDomicilioPat camb = cambios.get(0);
            Date fechaActual = new Date();

            int yearElNot = fechaActual.getYear() + 1900;
            int yearNot = camb.getFechaResolucionDesistida().getYear() + 1900;

            if (yearElNot == yearNot) {
                return camb.getResolucionDesistida() + 1;
            } else if (yearElNot > yearNot) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public List<String[]> getCambiosDomicilioCertificadoEmitido() {
        System.out.println("---------------------Empezando el reconocimiento cambios-domicilio------------------");
        String sql = "Select t.solicitud,n.documento,n.casillero from "
                + "cambio_domicilio as t "
                + "left join notificacion_casillero as n on t.solicitud = n.solicitud "
                + "where n.estado_notificacion = 1";

        Query query = this.getEntityManager().createNativeQuery(sql);
        List<Object[]> result = query.getResultList();
        List<String[]> cambiosDomicilio = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            Object[] aux = result.get(i);

            String[] transfer = {aux[0].toString(), "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + aux[2].toString() + "/" + aux[1].toString()};
            cambiosDomicilio.add(transfer);
        }

        System.out.println("--------------------Terminado el reconocimiento cambios-domicilio-----------------");
        System.out.println("Trámites encontrados: " + cambiosDomicilio.size());
        return cambiosDomicilio;
    }

    public List<CambioDomicilioPat> getCambiosDomicilioByTitular(String titular) {
        Query query = this.getEntityManager().createQuery("Select r from CambioDomicilioPat r where r.titularAnterior like '%" + titular + "%' "
                + "or r.titularActual like '%" + titular + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public CambioDomicilioPat getCambioDomicilioByIdCambioDomicilio(Integer id) {
        Query query = this.getEntityManager().createQuery("Select c from CambioDomicilioPat c where c.id = "+id);
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return (CambioDomicilioPat) query.getSingleResult();
    }

    public int getNextNumeroResolucionCaducadaCD() {
        Query query = this.getEntityManager().createQuery("Select n from CambioDomicilioPat n where n.tipoEstado = 'CADUCADA' and n.id = (Select MAX(n1.id) from CambioDomicilioPat n1 where n1.tipoEstado = 'CADUCADA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<CambioDomicilioPat> cambios = query.getResultList();
        if (cambios.isEmpty()) {
            return 1;
        } else {
            CambioDomicilioPat camb = cambios.get(0);
            Date fechaActual = new Date();

            int yearElNot = fechaActual.getYear() + 1900;
            int yearNot = camb.getFechaResolucionCaducada().getYear() + 1900;

            if (yearElNot == yearNot) {
                return camb.getResolucionCaducada() + 1;
            } else if (yearElNot > yearNot) {
                return 1;
            } else {
                return -1;
            }
        }
    }

}
