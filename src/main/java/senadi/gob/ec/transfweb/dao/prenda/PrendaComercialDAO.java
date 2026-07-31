/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.dao.prenda;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.dao.DAOAbstract;
import senadi.gob.ec.transfweb.model.prenda.PrendaComercial;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author micharesp
 */
public class PrendaComercialDAO extends DAOAbstract<PrendaComercial> {

    public PrendaComercialDAO(PrendaComercial pc) {
        super(pc);
    }

    @Override
    public List<PrendaComercial> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select c from PrendaComercial c order by c.id");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<PrendaComercial> getPrendasComercialesByTipo(String tipo) {
        Query query = null;
        if (tipo.equals("LEVANTAMIENTO")) {
            query = this.getEntityManager().createQuery("Select c from PrendaComercial c where c.tipoEstado = :tipo ORDER BY c.fechaPrenda DESC, c.levantamientoPrendaNo DESC");
        } else {
            query = this.getEntityManager().createQuery("Select c from PrendaComercial c where c.tipoEstado = :tipo ORDER BY c.fechaPrenda DESC, c.prendaNo DESC");
        }
        query.setParameter("tipo", tipo);

        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<PrendaComercial> getAbandonosPrendasComercialesByTipo(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from PrendaComercial c where c.tipoEstado = :tipo ORDER BY c.fechaPrenda DESC, c.numeroAbandono DESC");
        query.setParameter("tipo", tipo);

        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<PrendaComercial> getPrendasComercialesNotificada(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from PrendaComercial c where c.tipoEstado = :tipo ORDER BY c.fechaNotificacion DESC, c.notificacion DESC");
        query.setParameter("tipo", tipo);
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<PrendaComercial> getPrendasComercialesByCriteriaAndType(String text, String type) {
        Query query = this.getEntityManager().createQuery("Select c from PrendaComercial c where c.tipoEstado = '" + type + "' and (c.solicitud LIKE '%" + text + "%' or c.denominacion LIKE '%" + text + "%' "
                + "or c.titularActual LIKE '%" + text + "%') ORDER BY c.fechaPrenda DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<PrendaComercial> getPrendasComercialesByFecha(Date ini, Date fin) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from PrendaComercial t where t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaPrenda BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<PrendaComercial> getPrendasComercialesByFechaAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from PrendaComercial t where t.tipoEstado = '" + type + "' and (t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaPrenda BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "') ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<PrendaComercial> getPrendasComercialesByFechaPrendaAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from PrendaComercial t where t.tipoEstado = '" + type + "' and t.fechaPrenda BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<PrendaComercial> getPrendasComercialesByFechaNotificacionAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from PrendaComercial t where t.tipoEstado = '" + type + "' and t.fechaNotificacion BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public Integer getNextPrendaComercialPrendaNo() {
        Query query = this.getEntityManager().createQuery("Select c from PrendaComercial c where c.tipoEstado = 'CERTIFICADO' ORDER BY c.fechaPrenda desc, c.prendaNo desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<PrendaComercial> prendas = query.setMaxResults(300).getResultList();
        if (prendas.isEmpty()) {
            return 1;
        } else {
            PrendaComercial prenda = prendas.get(0);
            Date actual = new Date();

            if (prenda.getFechaPrenda().getYear() == actual.getYear()) {
                return prenda.getPrendaNo() + 1;
            } else if (actual.getYear() > prenda.getFechaPrenda().getYear()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public Integer getNextPrendaComercialResolucionCaducada() {
        Query query = this.getEntityManager().createQuery("Select c from PrendaComercial c where c.tipoEstado = 'CADUCADA' ORDER BY c.fechaResolucionCaducada desc, c.resolucionCaducada desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<PrendaComercial> prendas = query.setMaxResults(300).getResultList();
        if (prendas.isEmpty()) {
            return 1;
        } else {
            PrendaComercial prenda = prendas.get(0);
            Date actual = new Date();

            if (prenda.getFechaResolucionCaducada().getYear() == actual.getYear()) {
                return prenda.getResolucionCaducada() + 1;
            } else if (actual.getYear() > prenda.getFechaResolucionCaducada().getYear()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public boolean validarExistenciaPrendaComercial(PrendaComercial prendaComercial) {
        Query query = this.getEntityManager().createQuery("Select t from PrendaComercial t where t.solicitud = '" + prendaComercial.getSolicitud() + "' and t.id != " + prendaComercial.getId() + "");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean existeTramite(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from PrendaComercial t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public PrendaComercial getPrendaComercialBySolicitud(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from PrendaComercial t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<PrendaComercial> prendas = query.getResultList();
        if (prendas.isEmpty()) {
            return new PrendaComercial();
        } else {
            return prendas.get(0);
        }
    }

    public int getNextNumeroNotificacionPrenda(Date fechaElaboraNotificacion) {
        Query query = this.getEntityManager().createQuery("Select n from PrendaComercial n where n.tipoEstado = 'NOTIFICADA' and n.id = (Select MAX(n1.id) from PrendaComercial n1 where n1.tipoEstado = 'NOTIFICADA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        PrendaComercial c = (PrendaComercial) query.getSingleResult();

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

    public int getNextNumeroLevantamientoPrenda(Date fechaElaboraNotificacion) {
        int yearElNot = fechaElaboraNotificacion.getYear() + 1900;

        //Query query = this.getEntityManager().createQuery("Select p from PrendaComercial p where p.tipoEstado = 'LEVANTAMIENTO' AND YEAR(p.fechaPrenda) = " + yearElNot + " and p.id = (Select MAX(n1.levantamientoPrendaNo) from PrendaComercial n1 where n1.tipoEstado = 'LEVANTAMIENTO' and YEAR(n1.fechaPrenda) = " + yearElNot);
        Query query = this.getEntityManager().createQuery("Select p from PrendaComercial p where p.tipoEstado = 'LEVANTAMIENTO' AND FUNCTION('YEAR',p.fechaPrenda) = " + yearElNot + " and p.levantamientoPrendaNo = (Select MAX(n1.levantamientoPrendaNo) from PrendaComercial n1 where n1.tipoEstado = 'LEVANTAMIENTO' and FUNCTION('YEAR',n1.fechaPrenda) = " + yearElNot+")");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<PrendaComercial> prendas = query.getResultList();
        if (query.getResultList().isEmpty()) {
            return 1;
        } else {
            return prendas.get(0).getLevantamientoPrendaNo() + 1;
        }
    }

    public int getNextNumeroResolucionDesistimiento() {
        Query query = this.getEntityManager().createQuery("Select n from PrendaComercial n where n.tipoEstado = 'DESISTIDA' and n.id = (Select MAX(n1.id) from PrendaComercial n1 where n1.tipoEstado = 'DESISTIDA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return 1;
        } else {
            PrendaComercial n = (PrendaComercial) query.getSingleResult();
            if (n.getResolucionNo() != null) {
                return n.getResolucionNo() + 1;
            } else {
                return -1;
            }
        }
    }

    public List<PrendaComercial> getPrendasComercialesByDenominacion(String denominacion) {
        Query query = this.getEntityManager().createQuery("Select t from PrendaComercial t where t.denominacion like '%" + denominacion + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<PrendaComercial> getPrendasComercialesByTitular(String titular) {
        Query query = this.getEntityManager().createQuery("Select r from PrendaComercial r where r.deudoraPrendaria like '%" + titular + "%' "
                + "or r.prendariaAcreedora like '%" + titular + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public int getNextNumeroAbandonoPrenda(Date fechaElaboracion) {
        Query query = this.getEntityManager().createQuery("Select n from PrendaComercial n where n.tipoEstado = 'ABANDONO' and n.numeroAbandono = (Select MAX(n1.numeroAbandono) from PrendaComercial n1 where n1.tipoEstado = 'ABANDONO')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<PrendaComercial> abandonos = query.getResultList();
        if (abandonos.isEmpty()) {
            return 1;
        } else {
            PrendaComercial prenda = abandonos.get(0);

            int yearElNot = fechaElaboracion.getYear() + 1900;
            int yearNot = new Date().getYear() + 1900;

            if (yearElNot == yearNot) {
                return prenda.getNumeroAbandono() + 1;
            } else if (yearElNot > yearNot) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public List<PrendaComercial> getAbandonosErjafeVencidos(int dias) {
        // Calcula la fecha límite en Java
        LocalDate fechaLimite = LocalDate.now().minusDays(dias);
        Date fechaLimiteDate = java.sql.Date.valueOf(fechaLimite);

        Query query = this.getEntityManager().createQuery("SELECT n FROM PrendaComercial n WHERE n.tipoAbandono = :tipo AND n.fechaPuestaAbandono <= :fechaLimite");
        query.setParameter("tipo", "ERJAFE");
        query.setParameter("fechaLimite", fechaLimiteDate);
        return query.getResultList();
    }

    public List<PrendaComercial> getAbandonosSinFinesSemana(int diasHabiles, String type) {
        LocalDate fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(diasHabiles);
        Date fechaLimiteDate = java.sql.Date.valueOf(fechaLimite);

        Query query = this.getEntityManager().createQuery(
                "SELECT n FROM PrendaComercial n WHERE n.tipoAbandono = :tipo AND n.fechaPuestaAbandono <= :fechaLimite"
        );
        query.setParameter("tipo", type);
        query.setParameter("fechaLimite", fechaLimiteDate);
        return query.getResultList();
    }

    public List<PrendaComercial> getProrrogasCandidatas() {
        Query query = this.getEntityManager().createQuery(
                "SELECT n FROM PrendaComercial n WHERE n.tipoEstado = 'NOTIFICADA' AND n.fechaPuestaProrroga IS NOT NULL AND n.fechaProrroga IS NULL"
        );
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public int getNextNumeroProrrogaPrenda(Date fechaElaboracion) {
        Query query = this.getEntityManager().createQuery("Select n from PrendaComercial n where n.tipoEstado = 'PRORROGA' and n.numeroProrroga = (Select MAX(n1.numeroProrroga) from PrendaComercial n1 where n1.tipoEstado = 'PRORROGA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<PrendaComercial> prorrogas = query.getResultList();
        if (prorrogas.isEmpty()) {
            return 1;
        } else {
            PrendaComercial c = prorrogas.get(0);

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
