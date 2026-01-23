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
import senadi.gob.ec.transfweb.model.licencia.LicenciaUso;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author micharesp
 */
public class LicenciaUsoDAO extends DAOAbstract<LicenciaUso> {

    public LicenciaUsoDAO(LicenciaUso lc) {
        super(lc);
    }

    @Override
    public List<LicenciaUso> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUso c order by c.id");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<LicenciaUso> getLicenciasUsoParaDepurar() {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUso c where c.solicitud in ('SENADI-2022-97986','SENADI-2022-98506','SENADI-2022-98027','SENADI-2022-98021','SENADI-2022-98421','SENADI-2022-98420','SENADI-2022-98402','SENADI-2022-98400','SENADI-2022-98397','SENADI-2022-98191','SENADI-2022-98409','SENADI-2022-98032','SENADI-2022-98349','SENADI-2023-7684','SENADI-2022-97850','SENADI-2022-97848','SENADI-2022-97846','SENADI-2022-97844','SENADI-2022-97842','SENADI-2022-97840','SENADI-2022-97838','SENADI-2022-97836','SENADI-2022-97834','SENADI-2022-97832','SENADI-2022-97830','SENADI-2022-97828','SENADI-2022-97822','SENADI-2022-97811','SENADI-2022-97815','SENADI-2022-97854','SENADI-2022-98522','SENADI-2022-98341','SENADI-2022-98535','SENADI-2022-98374','SENADI-2022-98533','SENADI-2022-98124','SENADI-2022-98411','SENADI-2022-98539','SENADI-2022-98433','SENADI-2022-98009','SENADI-2022-98003','SENADI-2022-98481','SENADI-2022-98034','SENADI-2022-97974','SENADI-2022-98425','SENADI-2022-98294','SENADI-2022-98528','SENADI-2022-98501','SENADI-2022-98120','SENADI-2022-98247','SENADI-2022-98244','SENADI-2022-98380','SENADI-2022-98378','SENADI-2022-98376','SENADI-2022-98238','SENADI-2022-98417','SENADI-2022-98410','SENADI-2022-98234','SENADI-2022-98524','SENADI-2022-98520','SENADI-2022-98226','SENADI-2022-98516','SENADI-2022-98089','SENADI-2022-98020','SENADI-2022-98001','SENADI-2022-97997','SENADI-2022-97971','SENADI-2022-97969','SENADI-2022-97966','SENADI-2022-97958','SENADI-2022-97944','SENADI-2022-97921','SENADI-2022-97925','SENADI-2022-97920','SENADI-2022-97933','SENADI-2022-98414','SENADI-2022-98383','SENADI-2022-98497','SENADI-2022-98393','SENADI-2022-98489','SENADI-2022-98485','SENADI-2022-98472','SENADI-2022-98220','SENADI-2022-98218','SENADI-2022-98214','SENADI-2022-98492','SENADI-2022-98405','SENADI-2022-98390','SENADI-2022-98466','SENADI-2022-98463','SENADI-2022-98170','SENADI-2022-98323','SENADI-2022-98328','SENADI-2022-98168','SENADI-2022-98316','SENADI-2022-98165','SENADI-2022-98514','SENADI-2022-98542','SENADI-2022-98569','SENADI-2022-98176','SENADI-2022-98185','SENADI-2022-98581','SENADI-2022-98183','SENADI-2022-98179','SENADI-2022-98586','SENADI-2022-98609','SENADI-2022-98174','SENADI-2022-98197','SENADI-2022-98598','SENADI-2022-98194','SENADI-2022-98596','SENADI-2022-98181','SENADI-2022-98210','SENADI-2022-98207','SENADI-2022-98264','SENADI-2022-98335','SENADI-2022-98278','SENADI-2022-98005','SENADI-2022-97982','SENADI-2022-97979','SENADI-2022-97949','SENADI-2022-98370','SENADI-2022-98282','SENADI-2022-98530','SENADI-2022-98512','SENADI-2022-98287','SENADI-2022-98499','SENADI-2022-98503','SENADI-2022-98288','SENADI-2022-98493','SENADI-2022-98147','SENADI-2022-98149','SENADI-2022-98566','SENADI-2022-98555','SENADI-2022-98350','SENADI-2022-98327','SENADI-2022-98291','SENADI-2022-98387','SENADI-2022-98241','SENADI-2022-98406','SENADI-2022-98296','SENADI-2022-98372','SENADI-2022-98476','SENADI-2022-98356','SENADI-2022-98386','SENADI-2022-98480','SENADI-2022-98271','SENADI-2022-98270','SENADI-2022-98603','SENADI-2022-98588','SENADI-2022-98205','SENADI-2022-98201','SENADI-2022-98590','SENADI-2022-98199','SENADI-2022-98464','SENADI-2022-98429','SENADI-2022-98427','SENADI-2022-98468','SENADI-2022-98102','SENADI-2022-98475','SENADI-2022-98098','SENADI-2022-98094','SENADI-2022-98487','SENADI-2023-7640','SENADI-2023-7632')");
        return query.getResultList();
    }

    public List<LicenciaUso> getLicenciasUsoByTipo(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUso c where c.tipoEstado = :tipo ORDER BY c.fechaLicencia DESC, CAST(SUBSTRING(c.licenciaNo,6) AS DECIMAL) DESC");
        query.setParameter("tipo", tipo);
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<LicenciaUso> getTerminacionesLicencia() {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUso c where c.tipoEstado = 'TERMINACION' order by c.fechaTerminacion DESC, c.terminacionNo DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<LicenciaUso> getLicenciasUsoAbandonoByTipo(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUso c where c.tipoEstado = :tipo ORDER BY c.fechaNotificacion DESC, c.numeroAbandono DESC");
        query.setParameter("tipo", tipo);
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<LicenciaUso> getLicenciasUsoNotificada(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUso c where c.tipoEstado = :tipo ORDER BY c.fechaNotificacion DESC, CAST(SUBSTRING(c.notificacion,6) AS DECIMAL) DESC");
        query.setParameter("tipo", tipo);
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<LicenciaUso> getLicenciasUsoByCriteriaAndType(String text, String type) {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUso c where c.tipoEstado = '" + type + "' and (c.solicitud LIKE '%" + text + "%' or c.denominacion LIKE '%" + text + "%' "
                + "or c.licenciante LIKE '%" + text + "%') ORDER BY c.fechaLicencia DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<LicenciaUso> getLicenciasUsoByFechaAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUso t where t.tipoEstado = '" + type + "' and (t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaLicencia BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "') ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<LicenciaUso> getLicenciasUsoByFecha(Date ini, Date fin) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUso t where t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaLicencia BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<LicenciaUso> getLicenciasUsoByFechaLicenciaAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUso t where t.tipoEstado = '" + type + "' and t.fechaLicencia BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<LicenciaUso> getLicenciasUsoByFechaNotificacionAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUso t where t.tipoEstado = '" + type + "' and t.fechaNotificacion BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public String getNextLicenciaUsoLicenciaNo() {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUso c where c.tipoEstado = 'LICENCIA' ORDER BY c.fechaLicencia desc, CAST(SUBSTRING(c.licenciaNo,6) AS DECIMAL) desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        int current_year = new Date().getYear() + 1900;
        if (query.getResultList().isEmpty()) {
            return current_year + "-1";
        } else {
            LicenciaUso lic = (LicenciaUso) query.getResultList().get(0);

            int year = Integer.parseInt(lic.getLicenciaNo().substring(0, lic.getLicenciaNo().indexOf("-")));

            if (current_year == year) {
                int num = Integer.parseInt(lic.getLicenciaNo().substring(lic.getLicenciaNo().indexOf("-") + 1));
                return year + "-" + (num + 1);
            } else {
                return current_year + "-1";
            }
        }
    }

    public int getNextLicenciaUsoResolucionNo(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUso c where c.tipoEstado = '" + tipo + "' ORDER BY c.fechaResolucion desc, c.resolucionNo desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        int current_year = new Date().getYear() + 1900;
        if (query.getResultList().isEmpty()) {
            return 1;
        } else {
            LicenciaUso lic = (LicenciaUso) query.getResultList().get(0);

            int year = lic.getFechaResolucion().getYear() + 1900;

            if (current_year == year) {
                return lic.getResolucionNo() + 1;
            } else {
                return 1;
            }
        }
    }

    public int getNextLicenciaTerminacionNo(Date fecha) {
        int year = fecha.getYear() + 1900;
        Query query = this.getEntityManager().createQuery("Select l from LicenciaUso l where l.tipoEstado = 'TERMINACION' and YEAR(l.fechaTerminacion) = :year and l.terminacionNo = (SELECT MAX(l1.terminacionNo) FROM LicenciaUso l1 WHERE l1.tipoEstado = 'TERMINACION' AND YEAR(l1.fechaTerminacion) = :year)");
        query.setParameter("year", year);
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<LicenciaUso> licencias = query.getResultList();
        if (licencias.isEmpty()) {
            return 1;
        } else {
            return licencias.get(0).getTerminacionNo() + 1;
        }
    }

    public int getNextLicenciaUsoResolucionCaducadaNo(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUso c where c.tipoEstado = :tipo ORDER BY c.fechaResolucionCaducada desc, c.resolucionCaducada desc");
        query.setParameter("tipo", tipo);
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        int current_year = new Date().getYear() + 1900;
        if (query.getResultList().isEmpty()) {
            return 1;
        } else {
            LicenciaUso lic = (LicenciaUso) query.getResultList().get(0);

            int year = lic.getFechaResolucionCaducada().getYear() + 1900;

            if (current_year == year) {
                return lic.getResolucionNo() + 1;
            } else {
                return 1;
            }
        }
    }

    public boolean validarExistenciaLicenciaUso(LicenciaUso licencia) {
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUso t where t.solicitud = '" + licencia.getSolicitud() + "' and t.id != " + licencia.getId() + "");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean existeTramiteLicenciaUso(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUso t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public LicenciaUso getLicenciaUsoBySolicitud(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUso t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<LicenciaUso> licencias = query.getResultList();

        if (licencias.isEmpty()) {
            return new LicenciaUso();
        } else {
            return licencias.get(0);
        }
    }

    public String getNextNumeroNotificacionLicencia(Date fechaElaboraNotificacion) {
        Query query = this.getEntityManager().createQuery("Select n from LicenciaUso n where n.tipoEstado = 'NOTIFICADA' and n.id = (Select MAX(n1.id) from LicenciaUso n1 where n1.tipoEstado = 'NOTIFICADA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        int current_year = new Date().getYear() + 1900;
        if (query.getResultList().isEmpty()) {
            return current_year + "-1";
        } else {
            LicenciaUso lic = (LicenciaUso) query.getResultList().get(0);

            int year = Integer.parseInt(lic.getNotificacion().substring(0, lic.getNotificacion().indexOf("-")));

            if (current_year == year) {
                int num = Integer.parseInt(lic.getNotificacion().substring(lic.getNotificacion().indexOf("-") + 1));
                return year + "-" + (num + 1);
            } else {
                return current_year + "-1";
            }
        }
    }

    public int getNextNumeroResolucionDesistimiento() {
        Query query = this.getEntityManager().createQuery("Select n from LicenciaUso n where n.tipoEstado = 'DESISTIDA' and n.id = (Select MAX(n1.id) from LicenciaUso n1 where n1.tipoEstado = 'DESISTIDA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return 1;
        } else {
            LicenciaUso n = (LicenciaUso) query.getSingleResult();
            if (n.getResolucionNo() != null) {
                return n.getResolucionNo() + 1;
            } else {
                return -1;
            }
        }
    }

    public List<LicenciaUso> getLicenciasUsoByDenominacion(String denominacion) {
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUso t where t.denominacion like '%" + denominacion + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<LicenciaUso> getLicenciasUsoByTitular(String titular) {
        Query query = this.getEntityManager().createQuery("Select r from LicenciaUso r where r.licenciante like '%" + titular + "%' "
                + "or r.licenciatario like '%" + titular + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public int getNextNumeroAbandonoLicencia(Date fechaElaboracion) {
        Query query = this.getEntityManager().createQuery("Select n from LicenciaUso n where n.tipoEstado = 'ABANDONO' and n.numeroAbandono = (Select MAX(n1.numeroAbandono) from LicenciaUso n1 where n1.tipoEstado = 'ABANDONO')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<LicenciaUso> abandonos = query.getResultList();
        if (abandonos.isEmpty()) {
            return 1;
        } else {
            LicenciaUso licencia = abandonos.get(0);

            int yearElNot = fechaElaboracion.getYear() + 1900;
            int yearNot = new Date().getYear() + 1900;

            if (yearElNot == yearNot) {
                return licencia.getNumeroAbandono() + 1;
            } else if (yearElNot > yearNot) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public List<LicenciaUso> getAbandonosErjafeVencidos(int dias) {
        // Calcula la fecha límite en Java
        LocalDate fechaLimite = LocalDate.now().minusDays(dias);
        Date fechaLimiteDate = java.sql.Date.valueOf(fechaLimite);

        Query query = this.getEntityManager().createQuery("SELECT n FROM LicenciaUso n WHERE n.tipoAbandono = :tipo AND n.fechaPuestaAbandono <= :fechaLimite");
        query.setParameter("tipo", "ERJAFE");
        query.setParameter("fechaLimite", fechaLimiteDate);
        return query.getResultList();
    }

    public List<LicenciaUso> getAbandonosSinFinesSemana(int diasHabiles, String type) {
        LocalDate fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(diasHabiles);
        Date fechaLimiteDate = java.sql.Date.valueOf(fechaLimite);

        Query query = this.getEntityManager().createQuery(
                "SELECT n FROM LicenciaUso n WHERE n.tipoAbandono = :tipo AND n.fechaPuestaAbandono <= :fechaLimite"
        );
        query.setParameter("tipo", type);
        query.setParameter("fechaLimite", fechaLimiteDate);
        return query.getResultList();
    }

}
