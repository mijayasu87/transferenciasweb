/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.model.Desistimiento;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author Michael
 */
public class DesistimientoDAO extends DAOAbstract<Desistimiento> {

    public DesistimientoDAO(Desistimiento r) {
        super(r);
    }

    @Override
    public List<Desistimiento> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select r from Desistimiento r ORDER BY r.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }
    
    
        public List<Desistimiento> getAllDesistimientos() {
        Query query = this.getEntityManager().createQuery("Select r from Desistimiento r ORDER BY r.id");
        return query.getResultList();
    }

    public boolean existeTramite(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select r from Desistimiento r where r.solicitud = '" + numeroTramite + "'");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaDesistimiento(Desistimiento r) {
        Query query = this.getEntityManager().createQuery("Select r from Desistimiento r where r.solicitud = '" + r.getSolicitud() + "' and r.id != " + r.getId() + "");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaDesistimiento(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select r from Desistimiento r where r.solicitud = '" + solicitud + "'");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public Desistimiento getDesistimientosBySolicitud(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select r from Desistimiento r where r.solicitud = '" + solicitud + "'");
        if (query.getResultList().isEmpty()) {
            return new Desistimiento();
        } else {
            return (Desistimiento) query.getResultList().get(0);
        }
    }

    public List<Desistimiento> getDesistimientosByCriteria(String text) {
        Query query = this.getEntityManager().createQuery("Select r from Desistimiento r where r.solicitud LIKE '%" + text + "%' or r.denominacion LIKE '%" + text + "%' "
                + "or r.titularActual LIKE '%" + text + "%' ORDER BY r.id DESC");
        return query.getResultList();
    }

    public List<Desistimiento> getDesistimientosByFecha(Date inicio, Date fin) {
        String start = Operaciones.formatDate(inicio);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select r from Desistimiento r where r.fechaSolicitud between '" + start + "' and '" + end + "' "
                + "or r.fechaResolucion between '" + start + "' and '" + end + "' or r.fechaTitulo between '" + start + "' and '" + end + "' ORDER BY r.id DESC");
        return query.getResultList();
    }

    /**
     * **Esperando a ver como es la funcionalidad*************
     */
    public int getNextNumeroDesistimiento() {
        Query query = this.getEntityManager().createQuery("Select n from Desistimiento n where n.id = (Select MAX(n1.id) from Desistimiento n1)");

        Desistimiento n = (Desistimiento) query.getSingleResult();
        if (n.getResolucion() != null) {
            return Integer.parseInt(n.getResolucion()) + 1;
        } else {
            return -1;
        }
    }

    public List<Desistimiento> getDesistidasByDenominacion(String denominacion) {
        Query query = this.getEntityManager().createQuery("Select r from Desistimiento r where r.denominacion like '%" + denominacion + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Desistimiento> getDesistidasByTitular(String titular) {
        Query query = this.getEntityManager().createQuery("Select r from Desistimiento r where r.titularAnterior like '%" + titular + "%' "
                + "or r.titularActual like '%" + titular + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

}
