/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.modpat.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.modpat.LicenciaUsoPat;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author micharesp
 */
public class LicenciaUsoPatDAO extends DAOAbstractPat<LicenciaUsoPat> {

    public LicenciaUsoPatDAO(LicenciaUsoPat lc) {
        super(lc);
    }

    @Override
    public List<LicenciaUsoPat> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUsoPat c order by c.id desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<LicenciaUsoPat> getLicenciasUsoPatByTipo(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUsoPat c where c.tipoEstado = '" + tipo + "' ORDER BY c.fechaLicencia DESC, CAST(SUBSTRING(c.licenciaNo,6) AS DECIMAL) DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<LicenciaUsoPat> getLicenciasUsoPatNotificada(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUsoPat c where c.tipoEstado = '" + tipo + "' ORDER BY c.fechaNotificacion DESC, CAST(SUBSTRING(c.notificacion,6) AS DECIMAL) DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<LicenciaUsoPat> getLicenciasUsoPatByCriteriaAndType(String text, String type) {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUsoPat c where c.tipoEstado = '" + type + "' and (c.solicitud LIKE '%" + text + "%' or c.denominacion LIKE '%" + text + "%' "
                + "or c.licenciante LIKE '%" + text + "%') ORDER BY c.fechaLicencia DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<LicenciaUsoPat> getLicenciasUsoPatByFechaAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUsoPat t where t.tipoEstado = '" + type + "' and (t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaLicencia BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "') ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<LicenciaUsoPat> getLicenciasUsoPatByFecha(Date ini, Date fin) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUsoPat t where t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaLicencia BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<LicenciaUsoPat> getLicenciasUsoPatByFechaLicenciaAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUsoPat t where t.tipoEstado = '" + type + "' and t.fechaLicencia BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
    
    public List<LicenciaUsoPat> getLicenciasUsoPatByFechaNotificacionAndType(Date ini, Date fin, String type) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUsoPat t where t.tipoEstado = '" + type + "' and t.fechaNotificacion BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public String getNextLicenciaUsoPatLicenciaNo() {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUsoPat c where c.tipoEstado = 'LICENCIA' ORDER BY c.fechaLicencia desc, CAST(SUBSTRING(c.licenciaNo,6) AS DECIMAL) desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<LicenciaUsoPat> licencias = query.getResultList();
        int current_year = new Date().getYear() + 1900;
        if (licencias.isEmpty()) {
            return current_year + "-1";
        } else {
            LicenciaUsoPat lic = licencias.get(0);

            int year = Integer.parseInt(lic.getLicenciaNo().substring(0, lic.getLicenciaNo().indexOf("-")));

            if (current_year == year) {
                int num = Integer.parseInt(lic.getLicenciaNo().substring(lic.getLicenciaNo().indexOf("-") + 1));
                return year + "-" + (num + 1);
            } else {
                return current_year + "-1";
            }
        }
    }

    public int getNextLicenciaUsoPatResolucionNo(String tipo) {
        Query query = this.getEntityManager().createQuery("Select c from LicenciaUsoPat c where c.tipoEstado = '" + tipo + "' ORDER BY c.fechaResolucion desc, c.resolucionNo desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        
        List<LicenciaUsoPat> licencias = query.getResultList();
        
        int current_year = new Date().getYear() + 1900;
        if (licencias.isEmpty()) {
            return 1;
        } else {
            LicenciaUsoPat lic = licencias.get(0);

            int year = lic.getFechaResolucion().getYear() + 1900;

            if (current_year == year) {
                return lic.getResolucionNo() + 1;
            } else {
                return 1;
            }
        }
    }

    public boolean validarExistenciaLicenciaUsoPat(LicenciaUsoPat licencia) {
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUsoPat t where t.solicitud = '" + licencia.getSolicitud() + "' and t.id != " + licencia.getId() + "");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean existeTramiteLicenciaUsoPat(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUsoPat t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public LicenciaUsoPat getLicenciaUsoPatBySolicitud(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUsoPat t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (!query.getResultList().isEmpty()) {
            return (LicenciaUsoPat) query.getResultList().get(0);
        } else {
            return new LicenciaUsoPat();
        }
    }

    public String getNextNumeroNotificacionLicencia(Date fechaElaboraNotificacion) {
        Query query = this.getEntityManager().createQuery("Select n from LicenciaUsoPat n where n.tipoEstado = 'NOTIFICADA' and n.id = (Select MAX(n1.id) from LicenciaUsoPat n1 where n1.tipoEstado = 'NOTIFICADA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<LicenciaUsoPat> licencias = query.getResultList();
        
        int current_year = new Date().getYear() + 1900;
        if (licencias.isEmpty()) {
            return current_year + "-1";
        } else {
            LicenciaUsoPat lic = licencias.get(0);

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
        Query query = this.getEntityManager().createQuery("Select n from LicenciaUsoPat n where n.tipoEstado = 'DESISTIDA' and n.id = (Select MAX(n1.id) from LicenciaUsoPat n1 where n1.tipoEstado = 'DESISTIDA')");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return 1;
        } else {
            LicenciaUsoPat n = (LicenciaUsoPat) query.getSingleResult();
            if (n.getResolucionNo() != null) {
                return n.getResolucionNo() + 1;
            } else {
                return -1;
            }
        }
    }

    public List<LicenciaUsoPat> getLicenciasUsoPatByDenominacion(String denominacion) {
        Query query = this.getEntityManager().createQuery("Select t from LicenciaUsoPat t where t.denominacion like '%" + denominacion + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<LicenciaUsoPat> getLicenciasUsoPatByTitular(String titular) {
        Query query = this.getEntityManager().createQuery("Select r from LicenciaUsoPat r where r.licenciante like '%" + titular + "%' "
                + "or r.licenciatario like '%" + titular + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

}
