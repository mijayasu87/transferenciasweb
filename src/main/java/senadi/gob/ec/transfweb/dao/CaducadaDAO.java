/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.model.Caducada;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author Michael
 */
public class CaducadaDAO extends DAOAbstract<Caducada> {

    public CaducadaDAO(Caducada r) {
        super(r);
    }

    @Override
    public List<Caducada> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select r from Caducada r ORDER BY r.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<Caducada> getAllCaducadas() {
        Query query = this.getEntityManager().createQuery("Select r from Caducada r ORDER BY r.id");
        return query.getResultList();
    }

    public boolean existeTramite(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select r from Caducada r where r.solicitud = '" + numeroTramite + "'");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaCaducada(Caducada r) {
        Query query = this.getEntityManager().createQuery("Select r from Caducada r where r.solicitud = '" + r.getSolicitud() + "' and r.id != " + r.getId() + "");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaCaducada(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select r from Caducada r where r.solicitud = '" + solicitud + "'");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public Caducada getCaducadasBySolSenadi(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select r from Caducada r where r.solicitud = '" + solicitud + "'");
        if (query.getResultList().isEmpty()) {
            return new Caducada();
        } else {
            return (Caducada) query.getResultList().get(0);
        }
    }

    public List<Caducada> getCaducadasByCriteria(String text) {
        Query query = this.getEntityManager().createQuery("Select r from Caducada r where r.solicitud LIKE '%" + text + "%' or r.denominacion LIKE '%" + text + "%' "
                + "or r.titularActual LIKE '%" + text + "%' ORDER BY r.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Caducada> getCaducadasByFecha(Date inicio, Date fin) {
        String start = Operaciones.formatDate(inicio);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select r from Caducada r where r.fechaPresentacion between '" + start + "' and '" + end + "' "
                + "or r.fechaResolucion between '" + start + "' and '" + end + "' ORDER BY r.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Caducada> getCaducadasByDenominacion(String denominacion) {
        Query query = this.getEntityManager().createQuery("Select r from Caducada r where r.denominacion like '%" + denominacion + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Caducada> getCaducadasByTitular(String titular) {
        Query query = this.getEntityManager().createQuery("Select r from Caducada r where r.titularAnterior like '%" + titular + "%' "
                + "or r.titularActual like '%" + titular + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

}
