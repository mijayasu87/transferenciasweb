/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.modpat.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.modpat.SubLicenciaUsoPat;
import senadi.gob.ec.transfweb.util.Operaciones;


/**
 *
 * @author micharesp
 */
public class SublicenciaUsoPatDAO extends DAOAbstractPat<SubLicenciaUsoPat> {

    public SublicenciaUsoPatDAO(SubLicenciaUsoPat lc) {
        super(lc);
    }

    @Override
    public List<SubLicenciaUsoPat> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select c from SubLicenciaUsoPat c order by c.id desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<SubLicenciaUsoPat> getSublicenciasUsoByTipo(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from SubLicenciaUsoPat c where c.tipoEstado = '" + tipo + "' ORDER BY c.fechaSublicencia DESC, CAST(SUBSTRING(c.sublicenciaNo,6) AS DECIMAL) DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<SubLicenciaUsoPat> getSublicenciasUsoNotificada(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from SubLicenciaUsoPat c where c.tipoEstado = '" + tipo + "' ORDER BY c.fechaNotificacion DESC, CAST(SUBSTRING(c.notificacion,6) AS DECIMAL) DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<SubLicenciaUsoPat> getSublicenciasUsoByCriteriaAndType(String text, String type) {
        Query query = this.getEntityManager().createQuery("Select c from SubLicenciaUsoPat c where c.tipoEstado = '" + type + "' and (c.solicitud LIKE '%" + text + "%' or c.denominacion LIKE '%" + text + "%' "
                + "or c.sublicenciante LIKE '%" + text + "%') ORDER BY c.fechaSublicencia DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<SubLicenciaUsoPat> getSublicenciasUsoByFecha(Date ini, Date fin) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUsoPat t where t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaSublicencia BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<SubLicenciaUsoPat> getSublicenciasUsoByFechaAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUsoPat t where t.tipoEstado = '" + type + "' and (t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaSublicencia BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "') ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<SubLicenciaUsoPat> getSublicenciasUsoByFechaLicenciaAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUsoPat t where t.tipoEstado = '" + type + "' and t.fechaSublicencia BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
    
    public List<SubLicenciaUsoPat> getSublicenciasUsoByFechaNotificacionAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUsoPat t where t.tipoEstado = '" + type + "' and t.fechaNotificacion BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public String getNextSublicenciaUsoLicenciaNo() {
        Query query = this.getEntityManager().createQuery("Select c from SubLicenciaUsoPat c where c.tipoEstado = 'SUBLICENCIA' ORDER BY c.fechaSublicencia desc, CAST(SUBSTRING(c.sublicenciaNo,6) AS DECIMAL) desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        int current_year = new Date().getYear() + 1900;
        if (query.getResultList().isEmpty()) {
            return current_year + "-1";
        } else {
            SubLicenciaUsoPat lic = (SubLicenciaUsoPat) query.getResultList().get(0);

            int year = Integer.parseInt(lic.getSublicenciaNo().substring(0, lic.getSublicenciaNo().indexOf("-")));

            if (current_year == year) {
                int num = Integer.parseInt(lic.getSublicenciaNo().substring(lic.getSublicenciaNo().indexOf("-") + 1));
                return year + "-" + (num + 1);
            } else {
                return current_year + "-1";
            }
        }
    }

    public int getNextSublicenciaUsoResolucionNo(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from SubLicenciaUsoPat c where c.tipoEstado = '" + tipo + "' ORDER BY c.fechaResolucion desc, c.resolucionNo desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        int current_year = new Date().getYear() + 1900;
        if (query.getResultList().isEmpty()) {
            return 1;
        } else {
            SubLicenciaUsoPat lic = (SubLicenciaUsoPat) query.getResultList().get(0);

            int year = lic.getFechaResolucion().getYear() + 1900;

            if (current_year == year) {
                return lic.getResolucionNo() + 1;
            } else {
                return 1;
            }
        }
    }

    public boolean validarExistenciaSublicenciaUso(SubLicenciaUsoPat licencia) {
        Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUsoPat t where t.solicitud = '" + licencia.getSolicitud() + "' and t.id != " + licencia.getId() + "");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean existeTramiteSubLicenciaUso(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUsoPat t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public SubLicenciaUsoPat getSublicenciaUsoBySolicitud(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUsoPat t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (!query.getResultList().isEmpty()) {
            return (SubLicenciaUsoPat) query.getResultList().get(0);
        } else {
            return new SubLicenciaUsoPat();
        }
    }

    public String getNextNumeroNotificacionSublicencia(Date fechaElaboraNotificacion) {
        Query query = this.getEntityManager().createQuery("Select n from SubLicenciaUsoPat n where n.tipoEstado = 'NOTIFICADA' and n.id = (Select MAX(n1.id) from SubLicenciaUsoPat n1 where n1.tipoEstado = 'NOTIFICADA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        int current_year = new Date().getYear() + 1900;
        if (query.getResultList().isEmpty()) {
            return current_year + "-1";
        } else {
            SubLicenciaUsoPat lic = (SubLicenciaUsoPat) query.getResultList().get(0);

            int year = Integer.parseInt(lic.getNotificacion().substring(0, lic.getNotificacion().indexOf("-")));

            if (current_year == year) {
                int num = Integer.parseInt(lic.getNotificacion().substring(lic.getNotificacion().indexOf("-") + 1));
                return year + "-" + (num + 1);
            } else {
                return current_year + "-1";
            }
        }
    }

    public int getNextNumeroResolucionDesistimientoSubLicencia() {
        Query query = this.getEntityManager().createQuery("Select n from SubLicenciaUsoPat n where n.tipoEstado = 'DESISTIDA' and n.id = (Select MAX(n1.id) from SubLicenciaUsoPat n1 where n1.tipoEstado = 'DESISTIDA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return 1;
        } else {
            SubLicenciaUsoPat n = (SubLicenciaUsoPat) query.getSingleResult();
            if (n.getResolucionNo() != null) {
                return n.getResolucionNo() + 1;
            } else {
                return -1;
            }
        }
    }

    public List<SubLicenciaUsoPat> getSublicenciasUsoByDenominacion(String denominacion) {
        Query query = this.getEntityManager().createQuery("Select t from SubLicenciaUsoPat t where t.denominacion like '%" + denominacion + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<SubLicenciaUsoPat> getSublicenciasUsoByTitular(String titular) {
        Query query = this.getEntityManager().createQuery("Select r from SubLicenciaUsoPat r where r.sublicenciante like '%" + titular + "%' "
                + "or r.sublicenciatario like '%" + titular + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();

    }
}
