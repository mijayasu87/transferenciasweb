/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.dao.licencia;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.dao.DAOAbstract;
import senadi.gob.ec.transfweb.model.licencia.SubLicenciaUso;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author micharesp
 */
public class SublicenciaUsoDAO extends DAOAbstract<SubLicenciaUso> {

    public SublicenciaUsoDAO(SubLicenciaUso lc) {
        super(lc);
    }

    @Override
    public List<SubLicenciaUso> buscarTodos() {
        try {
            Query query = this.getEntityManager().createQuery("Select c from SubLicenciaUso c order by c.id");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query.getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<SubLicenciaUso> getSublicenciasUsoByTipo(String tipo) {
        try {
            Query query = this.getEntityManager().createQuery("Select c from SubLicenciaUso c where c.tipoEstado = :tipo ORDER BY c.fechaSublicencia DESC, CAST(SUBSTRING(c.sublicenciaNo,6) AS DECIMAL) DESC");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            query.setParameter("tipo", tipo);
            return query.setMaxResults(300).getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<SubLicenciaUso> getSublicenciasUsoNotificada(String tipo) {
        try {
            Query query = this.getEntityManager().createQuery("Select c from SubLicenciaUso c where c.tipoEstado = :tipo ORDER BY c.fechaNotificacion DESC, CAST(SUBSTRING(c.notificacion,6) AS DECIMAL) DESC");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            query.setParameter("tipo", tipo);
            return query.setMaxResults(300).getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<SubLicenciaUso> getSublicenciasUsoByCriteriaAndType(String text, String type) {
        try {
            Query query = this.getEntityManager().createQuery("Select c from SubLicenciaUso c where c.tipoEstado = '" + type + "' and (c.solicitud LIKE '%" + text + "%' or c.denominacion LIKE '%" + text + "%' "
                    + "or c.sublicenciante LIKE '%" + text + "%') ORDER BY c.fechaSublicencia DESC");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query.getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<SubLicenciaUso> getSublicenciasUsoByFecha(Date ini, Date fin) {
        try {
            String start = Operaciones.formatDate(ini);
            String end = Operaciones.formatDate(fin);
            Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUso t where t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                    + " or t.fechaSublicencia BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query.getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<SubLicenciaUso> getSublicenciasUsoByFechaAndType(Date ini, Date fin, String type) {
        try {
            String start = Operaciones.formatDate(ini);
            String end = Operaciones.formatDate(fin);
            Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUso t where t.tipoEstado = '" + type + "' and (t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                    + " or t.fechaSublicencia BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "') ORDER BY t.id DESC");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query.getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<SubLicenciaUso> getSublicenciasUsoByFechaLicenciaAndType(Date ini, Date fin, String type) {
        try {
            String start = Operaciones.formatDate(ini);
            String end = Operaciones.formatDate(fin);
            Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUso t where t.tipoEstado = '" + type + "' and t.fechaSublicencia BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query.getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<SubLicenciaUso> getSublicenciasUsoByFechaNotificacionAndType(Date ini, Date fin, String type) {
        try {
            String start = Operaciones.formatDate(ini);
            String end = Operaciones.formatDate(fin);
            Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUso t where t.tipoEstado = '" + type + "' and t.fechaNotificacion BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query.getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public String getNextSublicenciaUsoLicenciaNo() {
        try {
            Query query = this.getEntityManager().createQuery("Select c from SubLicenciaUso c where c.tipoEstado = 'SUBLICENCIA' ORDER BY c.fechaSublicencia desc, CAST(SUBSTRING(c.sublicenciaNo,6) AS DECIMAL) desc");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            int current_year = new Date().getYear() + 1900;
            List<SubLicenciaUso> subs = query.getResultList();
            if (subs.isEmpty()) {
                return current_year + "-1";
            } else {
                SubLicenciaUso lic = subs.get(0);

                int year = Integer.parseInt(lic.getSublicenciaNo().substring(0, lic.getSublicenciaNo().indexOf("-")));

                if (current_year == year) {
                    int num = Integer.parseInt(lic.getSublicenciaNo().substring(lic.getSublicenciaNo().indexOf("-") + 1));
                    return year + "-" + (num + 1);
                } else {
                    return current_year + "-1";
                }
            }
        } finally {
            this.getEntityManager().close();
        }
    }

    public int getNextSublicenciaUsoResolucionNo(String tipo) {
        try {
            Query query = this.getEntityManager().createQuery("Select c from SubLicenciaUso c where c.tipoEstado = '" + tipo + "' ORDER BY c.fechaResolucion desc, c.resolucionNo desc");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            int current_year = new Date().getYear() + 1900;
            if (query.getResultList().isEmpty()) {
                return 1;
            } else {
                SubLicenciaUso lic = (SubLicenciaUso) query.getResultList().get(0);

                int year = lic.getFechaResolucion().getYear() + 1900;

                if (current_year == year) {
                    return lic.getResolucionNo() + 1;
                } else {
                    return 1;
                }
            }
        } finally {
            this.getEntityManager().close();
        }
    }

    public boolean validarExistenciaSublicenciaUso(SubLicenciaUso licencia) {
        try {
            Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUso t where t.solicitud = '" + licencia.getSolicitud() + "' and t.id != " + licencia.getId() + "");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            if (query.getResultList().isEmpty()) {
                return false;
            } else {
                return true;
            }
        } finally {
            this.getEntityManager().close();
        }
    }

    public boolean existeTramiteSubLicenciaUso(String numeroTramite) {
        try {
            Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUso t where t.solicitud = :tramite");
            query.setParameter("tramite", numeroTramite);
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");

            if (query.getResultList().isEmpty()) {
                return false;
            } else {
                return true;
            }
        } finally {
            this.getEntityManager().close();
        }
    }

    public SubLicenciaUso getSublicenciaUsoBySolicitud(String numeroTramite) {
        try {
            Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUso t where t.solicitud = :tramite");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            query.setParameter("tramite", numeroTramite);
            List<SubLicenciaUso> sublicencias = query.getResultList();
            if (sublicencias.isEmpty()) {
                return new SubLicenciaUso();
            } else {
                return sublicencias.get(0);
            }
        } finally {
            this.getEntityManager().close();
        }
    }

    public SubLicenciaUso getSublicenciaUsoById(Integer id) {
        try {
            Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUso t where t.id = :idp");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            query.setParameter("idp", id);
            List<SubLicenciaUso> sublicencias = query.getResultList();
            if (sublicencias.isEmpty()) {
                return new SubLicenciaUso();
            } else {
                return sublicencias.get(0);
            }
        } finally {
            this.getEntityManager().close();
        }
    }

    public String getNextNumeroNotificacionSublicencia(Date fechaElaboraNotificacion) {
        try {
            Query query = this.getEntityManager().createQuery("Select n from SubLicenciaUso n where n.tipoEstado = 'NOTIFICADA' and n.id = (Select MAX(n1.id) from SubLicenciaUso n1 where n1.tipoEstado = 'NOTIFICADA')");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");

            int current_year = new Date().getYear() + 1900;
            if (query.getResultList().isEmpty()) {
                return current_year + "-1";
            } else {
                SubLicenciaUso lic = (SubLicenciaUso) query.getResultList().get(0);

                int year = Integer.parseInt(lic.getNotificacion().substring(0, lic.getNotificacion().indexOf("-")));

                if (current_year == year) {
                    int num = Integer.parseInt(lic.getNotificacion().substring(lic.getNotificacion().indexOf("-") + 1));
                    return year + "-" + (num + 1);
                } else {
                    return current_year + "-1";
                }
            }
        } finally {
            this.getEntityManager().close();
        }
    }

    public int getNextNumeroResolucionDesistimientoSubLicencia() {
        try {
            Query query = this.getEntityManager().createQuery("Select n from SubLicenciaUso n where n.tipoEstado = 'DESISTIDA' and n.id = (Select MAX(n1.id) from SubLicenciaUso n1 where n1.tipoEstado = 'DESISTIDA')");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            if (query.getResultList().isEmpty()) {
                return 1;
            } else {
                SubLicenciaUso n = (SubLicenciaUso) query.getSingleResult();
                if (n.getResolucionNo() != null) {
                    return n.getResolucionNo() + 1;
                } else {
                    return -1;
                }
            }
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<SubLicenciaUso> getSublicenciasUsoByDenominacion(String denominacion) {
        try {
            Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUso t where t.denominacion like '%" + denominacion + "%'");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query.getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<SubLicenciaUso> getSublicenciasUsoByTitular(String titular) {
        try {
            Query query = this.getEntityManager().createQuery("Select r from SubLicenciaUso r where r.sublicenciante like '%" + titular + "%' "
                    + "or r.sublicenciatario like '%" + titular + "%'");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query.getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<SubLicenciaUso> getSubLicenciasUsoAbandonoByTipo(String tipo) {
        try {
            Query query = this.getEntityManager().createQuery("Select c from SubLicenciaUso c where c.tipoEstado = :tipo ORDER BY c.fechaAbandono DESC, c.numeroAbandono DESC");
            query.setParameter("tipo", tipo);
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query.setMaxResults(300).getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public int getNextNumeroAbandonoSubLicencia(Date fechaElaboracion) {
        try {
            Query query = this.getEntityManager().createQuery("Select n from SubLicenciaUso n where n.tipoEstado = 'ABANDONO' and n.numeroAbandono = (Select MAX(n1.numeroAbandono) from SubLicenciaUso n1 where n1.tipoEstado = 'ABANDONO')");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");

            List<SubLicenciaUso> abandonos = query.getResultList();
            if (abandonos.isEmpty()) {
                return 1;
            } else {
                SubLicenciaUso sublicencia = abandonos.get(0);

                int yearElNot = fechaElaboracion.getYear() + 1900;
                int yearNot = new Date().getYear() + 1900;

                if (yearElNot == yearNot) {
                    return sublicencia.getNumeroAbandono() + 1;
                } else if (yearElNot > yearNot) {
                    return 1;
                } else {
                    return -1;
                }
            }
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<SubLicenciaUso> getAbandonosErjafeVencidos(int dias) {
        try {
            // Calcula la fecha límite en Java
            LocalDate fechaLimite = LocalDate.now().minusDays(dias);
            Date fechaLimiteDate = java.sql.Date.valueOf(fechaLimite);

            Query query = this.getEntityManager().createQuery("SELECT n FROM SubLicenciaUso n WHERE n.tipoAbandono = :tipo AND n.fechaPuestaAbandono <= :fechaLimite");
            query.setParameter("tipo", "ERJAFE");
            query.setParameter("fechaLimite", fechaLimiteDate);
            return query.getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<SubLicenciaUso> getAbandonosSinFinesSemana(int diasHabiles, String type) {
        try {
            LocalDate fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(diasHabiles);
            Date fechaLimiteDate = java.sql.Date.valueOf(fechaLimite);

            Query query = this.getEntityManager().createQuery(
                    "SELECT n FROM SubLicenciaUso n WHERE n.tipoAbandono = :tipo AND n.fechaPuestaAbandono <= :fechaLimite"
            );
            query.setParameter("tipo", type);
            query.setParameter("fechaLimite", fechaLimiteDate);
            return query.getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<SubLicenciaUso> getProrrogasCandidatas() {
        try {
            Query query = this.getEntityManager().createQuery(
                    "SELECT n FROM SubLicenciaUso n WHERE n.tipoEstado = 'NOTIFICADA' AND n.fechaPuestaProrroga IS NOT NULL AND n.fechaProrroga IS NULL"
            );
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query.getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public int getNextNumeroProrrogaSublicencia(Date fechaElaboracion) {
        try {
            Query query = this.getEntityManager().createQuery("Select n from SubLicenciaUso n where n.tipoEstado = 'PRORROGA' and n.numeroProrroga = (Select MAX(n1.numeroProrroga) from SubLicenciaUso n1 where n1.tipoEstado = 'PRORROGA')");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");

            List<SubLicenciaUso> prorrogas = query.getResultList();
            if (prorrogas.isEmpty()) {
                return 1;
            } else {
                SubLicenciaUso c = prorrogas.get(0);

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
        } finally {
            this.getEntityManager().close();
        }
    }

}
