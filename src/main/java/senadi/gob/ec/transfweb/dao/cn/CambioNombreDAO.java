/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.dao.cn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.dao.DAOAbstract;
import senadi.gob.ec.transfweb.model.cn.CambioNombre;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author micharesp
 */
public class CambioNombreDAO extends DAOAbstract<CambioNombre> {

    public CambioNombreDAO(CambioNombre cn) {
        super(cn);
    }

    @Override
    public List<CambioNombre> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select c from CambioNombre c order by c.id");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioNombre> getTodosActualNotModificaciones(String solicitudes) {
        Query query = this.getEntityManager().createQuery("Select c from CambioNombre c where (c.solicitud like 'SENADI%' or c.solicitud like 'IEPI%') "
                + "and c.solicitud not in (" + solicitudes + ") order by c.id");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioNombre> getCambiosNombreByTipo(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from CambioNombre c where c.tipoEstado = '" + tipo + "' ORDER BY c.fechaCertificado DESC, c.certificado DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<CambioNombre> getCambiosNombreByDenominacion(String denominacion) {
        Query query = this.getEntityManager().createQuery("Select c from CambioNombre c where c.denominacion like '%" + denominacion + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<CambioNombre> getCambiosNombreNotificada(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from CambioNombre c where c.tipoEstado = '" + tipo + "' ORDER BY c.fechaNotificacion DESC, c.notificacion DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<CambioNombre> getCambiosNombreByCriteriaAndType(String text, String type) {
        Query query = this.getEntityManager().createQuery("Select c from CambioNombre c where c.tipoEstado = '" + type + "' and (c.solicitud LIKE '%" + text + "%' or c.denominacion LIKE '%" + text + "%' "
                + "or c.titularActual LIKE '%" + text + "%') ORDER BY c.fechaCertificado DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioNombre> getCambiosNombreByFecha(Date ini, Date fin) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from CambioNombre t where t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaCertificado BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioNombre> getCambiosNombreByFechaAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from CambioNombre t where t.tipoEstado = '" + type + "' and (t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaCertificado BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "') ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioNombre> getCambiosNombreByFechaNotificacionAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from CambioNombre t where t.tipoEstado = '" + type + "' and t.fechaNotificacion BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioNombre> getCambiosNombreByFechaCertificadoAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from CambioNombre t where t.tipoEstado = '" + type + "' and t.fechaCertificado BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public Integer getNextCambioNombreCertificado() {
        Query query = this.getEntityManager().createQuery("Select c from CambioNombre c where c.tipoEstado = 'CERTIFICADO' ORDER BY c.fechaCertificado desc, c.certificado desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<CambioNombre> cambios = query.setMaxResults(300).getResultList();
        if (cambios.isEmpty()) {
            return 1;
        } else {
            CambioNombre cambio = cambios.get(0);
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

    public boolean validarExistenciaCambioNombre(CambioNombre cambioNombre) {
        Query query = this.getEntityManager().createQuery("Select t from CambioNombre t where t.solicitud = '" + cambioNombre.getSolicitud() + "' and t.id != " + cambioNombre.getId() + "");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean existeTramite(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from CambioNombre t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public CambioNombre getCambioNombreBySolicitud(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from CambioNombre t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<CambioNombre> cambios = query.getResultList();
        if (cambios.isEmpty()) {
            return new CambioNombre();
        } else {
            return cambios.get(0);
        }
    }

//    public int getNextNumeroNotificacionCN(Date fechaElaboraNotificacion) {
//        //Query query = this.getEntityManager().createQuery("Select n from CambioNombre n where n.tipoEstado = 'NOTIFICADA' and n.id = (Select MAX(n1.id) from CambioNombre n1 where n1.tipoEstado = 'NOTIFICADA')");
//        Query query = this.getEntityManager().createQuery("Select n from CambioNombre n where n.tipoEstado = 'NOTIFICADA' and n.notificacion = (Select MAX(n1.notificacion) from CambioNombre n1 where n1.tipoEstado = 'NOTIFICADA')");
//
//        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
//        List<CambioNombre> notificadas = query.getResultList();
//        if (notificadas.isEmpty()) {
//            return 1;
//        } else {
//            CambioNombre c = notificadas.get(0);
//            int yearElNot = fechaElaboraNotificacion.getYear() + 1900;
//            int yearNot = c.getFechaNotificacion().getYear() + 1900;
//            if (yearElNot == yearNot) {
//                return c.getNotificacion() + 1;
//            } else if (yearElNot > yearNot) {
//                return 1;
//            } else {
//                return -1;
//            }
//        }
//    }
    public int getNextNumeroNotificacionCN(Date fechaElaboraNotificacion) {
        // obtenemos el año de la fecha actual
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaElaboraNotificacion);
        int yearActual = cal.get(Calendar.YEAR);

        // buscamos el máximo número de notificación del año actual
        Query query = this.getEntityManager().createQuery(
                "SELECT MAX(n.notificacion) "
                + "FROM CambioNombre n "
                + "WHERE n.tipoEstado = 'NOTIFICADA' "
                + "AND FUNCTION('year', n.fechaNotificacion) = :anio"
        );
        query.setParameter("anio", yearActual);

        Integer maxNumero = (Integer) query.getSingleResult();

        if (maxNumero == null) {
            return 1; // primera notificación del año
        } else {
            return maxNumero + 1;
        }
    }

//    public int getNextNumeroAbandonoCN(Date fechaElaboraNotificacionAbandono) {
//        Query query = this.getEntityManager().createQuery("Select n from CambioNombre n where n.tipoEstado = 'ABANDONO' and n.numeroAbandono = (Select MAX(n1.numeroAbandono) from CambioNombre n1 where n1.tipoEstado = 'ABANDONO')");
//        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
//
//        List<CambioNombre> abandonos = query.getResultList();
//        if (abandonos.isEmpty()) {
//            return 1;
//        } else {
//            CambioNombre c = abandonos.get(0);
//
//            int yearElNot = fechaElaboraNotificacionAbandono.getYear() + 1900;
//            int yearNot = new Date().getYear() + 1900;
//
//            if (yearElNot == yearNot) {
//                return c.getNumeroAbandono() + 1;
//            } else if (yearElNot > yearNot) {
//                return 1;
//            } else {
//                return -1;
//            }
//        }
//    }
    public int getNextNumeroAbandonoCN(Date fechaElaboraNotificacionAbandono) {
        // obtenemos el año de la fecha actual
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaElaboraNotificacionAbandono);
        int yearActual = cal.get(Calendar.YEAR);

        // buscamos el máximo número de abandono del año actual
        Query query = this.getEntityManager().createQuery(
                "SELECT MAX(n.numeroAbandono) "
                + "FROM CambioNombre n "
                + "WHERE n.tipoEstado = 'ABANDONO' "
                + "AND FUNCTION('year', n.fechaAbandono) = :anio"
        );
        query.setParameter("anio", yearActual);

        Integer maxNumero = (Integer) query.getSingleResult();

        if (maxNumero == null) {
            return 1; // primer abandono del año
        } else {
            return maxNumero + 1;
        }
    }

    /**
     * **Esperando a ver como es la funcionalidad*************
     */
//    public int getNextNumeroDesistimiento() {
//        Query query = this.getEntityManager().createQuery("Select n from CambioNombre n where n.tipoEstado = 'NOTIFICADA' and n.id = (Select MAX(n1.id) from CambioNombre n1 where n1.tipoEstado = 'DESISTIDA')");
//        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
//        if (query.getResultList().isEmpty()) {
//            return 1;
//        } else {
//            CambioNombre n = (CambioNombre) query.getSingleResult();
//            if (n.getResolucionDesistida() != null) {
//                return n.getResolucionDesistida() + 1;
//            } else {
//                return -1;
//            }
//        }
//    }
    public int getNextNumeroDesistimiento() {

        Date fechaElaboracionDesistimiento = new Date();
        // obtener el año actual de la fecha de elaboración
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaElaboracionDesistimiento);
        int yearActual = cal.get(Calendar.YEAR);
        
        // buscamos el máximo número de resolución desistida del año actual
        Query query = this.getEntityManager().createQuery(
                "SELECT MAX(n.resolucionDesistida) "
                + "FROM CambioNombre n "
                + "WHERE n.tipoEstado = 'DESISTIDA' "
                + "AND FUNCTION('year', n.fechaResolucionDesistida) = :anio"
        );
        query.setParameter("anio", yearActual);

        Integer maxNumero = (Integer) query.getSingleResult();
        if (maxNumero == null) {
            return 1; // primer desistimiento del año
        } else {
            return maxNumero + 1;
        }
    }

    public List<String[]> getCambiosNombreCertificadoEmitido() {
        System.out.println("---------------------Empezando el reconocimiento cambios-nombre------------------");
        String sql = "Select t.solicitud,n.documento,n.casillero from "
                + "cambio_nombre as t "
                + "left join notificacion_casillero as n on t.solicitud = n.solicitud "
                + "where n.estado_notificacion = 1";

        Query query = this.getEntityManager().createNativeQuery(sql);
        List<Object[]> result = query.getResultList();
        List<String[]> cambiosNombre = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            Object[] aux = result.get(i);

            String[] transfer = {aux[0].toString(), "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + aux[2].toString() + "/" + aux[1].toString()};
            cambiosNombre.add(transfer);
        }

        System.out.println("--------------------Terminado el reconocimiento cambios-nombre-----------------");
        System.out.println("Trámites encontrados: " + cambiosNombre.size());
        return cambiosNombre;
    }

    public List<CambioNombre> getCambiosNombreByTitular(String titular) {
        Query query = this.getEntityManager().createQuery("Select r from CambioNombre r where r.titularAnterior like '%" + titular + "%' "
                + "or r.titularActual like '%" + titular + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<CambioNombre> getAbandonosErjafeVencidos(int dias) {
        // Calcula la fecha límite en Java
        LocalDate fechaLimite = LocalDate.now().minusDays(dias);
        Date fechaLimiteDate = java.sql.Date.valueOf(fechaLimite);

        Query query = this.getEntityManager().createQuery("SELECT n FROM CambioNombre n WHERE n.tipoAbandono = :tipo AND n.fechaPuestaAbandono <= :fechaLimite");
        query.setParameter("tipo", "ERJAFE");
        query.setParameter("fechaLimite", fechaLimiteDate);
        return query.getResultList();
    }

    public List<CambioNombre> getAbandonosSinFinesSemana(int diasHabiles, String type) {
        LocalDate fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(diasHabiles);
        Date fechaLimiteDate = java.sql.Date.valueOf(fechaLimite);

        Query query = this.getEntityManager().createQuery(
                "SELECT n FROM CambioNombre n WHERE n.tipoAbandono = :tipo AND n.fechaPuestaAbandono <= :fechaLimite"
        );
        query.setParameter("tipo", type);
        query.setParameter("fechaLimite", fechaLimiteDate);
        return query.getResultList();
    }

    public List<CambioNombre> getProrrogasCandidatas() {
        Query query = this.getEntityManager().createQuery(
                "SELECT n FROM CambioNombre n WHERE n.tipoEstado = 'NOTIFICADA' AND n.fechaPuestaProrroga IS NOT NULL AND n.fechaProrroga IS NULL"
        );
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public int getNextNumeroProrrogaCN(Date fechaElaboracion) {
        Query query = this.getEntityManager().createQuery("Select n from CambioNombre n where n.tipoEstado = 'PRORROGA' and n.numeroProrroga = (Select MAX(n1.numeroProrroga) from CambioNombre n1 where n1.tipoEstado = 'PRORROGA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<CambioNombre> prorrogas = query.getResultList();
        if (prorrogas.isEmpty()) {
            return 1;
        } else {
            CambioNombre c = prorrogas.get(0);

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
