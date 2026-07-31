/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.dao.cd;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.dao.DAOAbstract;
import senadi.gob.ec.transfweb.model.cd.CambioDomicilio;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author micharesp
 */
public class CambioDomicilioDAO extends DAOAbstract<CambioDomicilio> {

    public CambioDomicilioDAO(CambioDomicilio cn) {
        super(cn);
    }

    @Override
    public List<CambioDomicilio> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select c from CambioDomicilio c order by c.id");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioDomicilio> getCambiosDomicilioByTipo(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from CambioDomicilio c where c.tipoEstado = '" + tipo + "' ORDER BY c.fechaCertificado DESC, c.certificado DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<CambioDomicilio> getCambiosDomicilioNotificada(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from CambioDomicilio c where c.tipoEstado = '" + tipo + "' ORDER BY c.fechaNotificacion DESC, c.notificacion DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<CambioDomicilio> getCambiosDomicilioByCriteriaAndType(String text, String type) {
        Query query = this.getEntityManager().createQuery("Select c from CambioDomicilio c where c.tipoEstado = '" + type + "' and (c.solicitud LIKE '%" + text + "%' or c.denominacion LIKE '%" + text + "%' "
                + "or c.titularActual LIKE '%" + text + "%') ORDER BY c.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioDomicilio> getCambiosDomicilioByFechaAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from CambioDomicilio t where t.tipoEstado = '" + type + "' and (t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaCertificado BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "') ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioDomicilio> getCambiosDomicilioByFechaCertificadoAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from CambioDomicilio t where t.tipoEstado = '" + type + "' and t.fechaCertificado BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioDomicilio> getCambiosDomicilioByFechaNotificacionAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from CambioDomicilio t where t.tipoEstado = '" + type + "' and t.fechaNotificacion BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

//    public Integer getNextCambioDomicilioCertificado() {
//        Query query = this.getEntityManager().createQuery("Select c from CambioDomicilio c where c.tipoEstado = 'CERTIFICADO' and c.id = (Select MAX(c1.id) from CambioDomicilio c1 where c1.tipoEstado = 'CERTIFICADO')");
//        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
//        List<CambioDomicilio> cambios = query.getResultList();
//        if (cambios.isEmpty()) {
//            return 1;
//        } else {
//            CambioDomicilio cambio = cambios.get(0);
//            Date actual = new Date();
//
//            if (cambio.getFechaCertificado().getYear() == actual.getYear()) {
//                return cambio.getCertificado() + 1;
//            }else if(actual.getYear() > cambio.getFechaCertificado().getYear()){
//                return 1;
//            }else{
//                return -1;
//            }
//        }
//    }
    public Integer getNextCambioDomicilioCertificado() {
        Query query = this.getEntityManager().createQuery("Select c from CambioDomicilio c where c.tipoEstado = 'CERTIFICADO' ORDER BY c.fechaCertificado desc, c.certificado desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<CambioDomicilio> cambios = query.setMaxResults(300).getResultList();
        if (cambios.isEmpty()) {
            return 1;
        } else {
            CambioDomicilio cambio = cambios.get(0);
            Date actual = new Date();

            if (cambio.getFechaCertificado().getYear() == actual.getYear()) {
                return cambio.getCertificado() + 1;
            } else if (actual.getYear() > cambio.getFechaCertificado().getYear()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public boolean validarExistenciaCambioDomicilio(CambioDomicilio cambioDomicilio) {
        Query query = this.getEntityManager().createQuery("Select t from CambioDomicilio t where t.solicitud = '" + cambioDomicilio.getSolicitud() + "' and t.id != " + cambioDomicilio.getId() + "");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean existeTramite(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from CambioDomicilio t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public CambioDomicilio getCambioDomicilioBySolicitud(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from CambioDomicilio t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<CambioDomicilio> cambios = query.getResultList();
        if (cambios.isEmpty()) {
            return new CambioDomicilio();
        } else {
            return cambios.get(0);
        }
    }

    public int getNextNumeroNotificacionCD(Date fechaElaboraNotificacion) {
        Query query = this.getEntityManager().createQuery("Select n from CambioDomicilio n where n.tipoEstado = 'NOTIFICADA' and n.id = (Select MAX(n1.id) from CambioDomicilio n1 where n1.tipoEstado = 'NOTIFICADA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        CambioDomicilio c = (CambioDomicilio) query.getSingleResult();
        if (c.getNotificacion() == null) {
            return 1;
        } else {
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
        Query query = this.getEntityManager().createQuery("Select n from CambioDomicilio n where n.tipoEstado = 'NOTIFICADA' and n.id = (Select MAX(n1.id) from CambioDomicilio n1 where n1.tipoEstado = 'DESISTIDA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return 1;
        } else {
            CambioDomicilio n = (CambioDomicilio) query.getSingleResult();
            if (n.getResolucionDesistida() != null) {
                return n.getResolucionDesistida() + 1;
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

    public List<CambioDomicilio> getCambiosDomicilioByDenominacion(String denominacion) {
        Query query = this.getEntityManager().createQuery("Select t from CambioDomicilio t where t.denominacion like '%" + denominacion + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioDomicilio> getCambiosDomicilioByFecha(Date ini, Date fin) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from CambioDomicilio t where t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaCertificado BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioDomicilio> getCambiosDomicilioByTitular(String titular) {
        Query query = this.getEntityManager().createQuery("Select r from CambioDomicilio r where r.titularActual like '%" + titular + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public int getNextNumeroAbandonoCD(Date fechaElaboracion) {
        Query query = this.getEntityManager().createQuery("Select n from CambioDomicilio n where n.tipoEstado = 'ABANDONO' and n.numeroAbandono = (Select MAX(n1.numeroAbandono) from CambioDomicilio n1 where n1.tipoEstado = 'ABANDONO')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<CambioDomicilio> abandonos = query.getResultList();
        if (abandonos.isEmpty()) {
            return 1;
        } else {
            CambioDomicilio c = abandonos.get(0);

            int yearElNot = fechaElaboracion.getYear() + 1900;
            int yearNot = new Date().getYear() + 1900;

            if (yearElNot == yearNot) {
                return c.getNumeroAbandono() + 1;
            } else if (yearElNot > yearNot) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public List<CambioDomicilio> getAbandonosErjafeVencidos(int dias) {
        // Calcula la fecha límite en Java
        LocalDate fechaLimite = LocalDate.now().minusDays(dias);
        Date fechaLimiteDate = java.sql.Date.valueOf(fechaLimite);

        Query query = this.getEntityManager().createQuery("SELECT n FROM CambioDomicilio n WHERE n.tipoAbandono = :tipo AND n.fechaPuestaAbandono <= :fechaLimite");
        query.setParameter("tipo", "ERJAFE");
        query.setParameter("fechaLimite", fechaLimiteDate);
        return query.getResultList();
    }
    
    public List<CambioDomicilio> getAbandonosSinFinesSemana(int diasHabiles, String type) {
        LocalDate fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(diasHabiles);
        Date fechaLimiteDate = java.sql.Date.valueOf(fechaLimite);

        Query query = this.getEntityManager().createQuery(
                "SELECT n FROM CambioDomicilio n WHERE n.tipoAbandono = :tipo AND n.fechaPuestaAbandono <= :fechaLimite"
        );
        query.setParameter("tipo", type);
        query.setParameter("fechaLimite", fechaLimiteDate);
        return query.getResultList();
    }

    /**
     * Notificaciones que fueron establecidas "para prórroga" (tienen
     * fechaPuestaProrroga) y que aún no han sido pasadas a la pestaña de
     * prórrogas (fechaProrroga nula). El vencimiento del plazo se evalúa por
     * registro en el scheduler, ya que los días de prórroga son configurables.
     */
    public List<CambioDomicilio> getProrrogasCandidatas() {
        Query query = this.getEntityManager().createQuery(
                "SELECT n FROM CambioDomicilio n WHERE n.tipoEstado = 'NOTIFICADA' AND n.fechaPuestaProrroga IS NOT NULL AND n.fechaProrroga IS NULL"
        );
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public int getNextNumeroProrrogaCD(Date fechaElaboracion) {
        Query query = this.getEntityManager().createQuery("Select n from CambioDomicilio n where n.tipoEstado = 'PRORROGA' and n.numeroProrroga = (Select MAX(n1.numeroProrroga) from CambioDomicilio n1 where n1.tipoEstado = 'PRORROGA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<CambioDomicilio> prorrogas = query.getResultList();
        if (prorrogas.isEmpty()) {
            return 1;
        } else {
            CambioDomicilio c = prorrogas.get(0);

            int yearElPro = fechaElaboracion.getYear() + 1900;
            int yearPro = c.getFechaProrroga() != null ? c.getFechaProrroga().getYear() + 1900 : yearElPro;

            if (yearElPro == yearPro) {
                return (c.getNumeroProrroga() == null ? 0 : c.getNumeroProrroga()) + 1;
            } else if (yearElPro > yearPro) {
                return 1;
            } else {
                return -1;
            }
        }
    }

}
