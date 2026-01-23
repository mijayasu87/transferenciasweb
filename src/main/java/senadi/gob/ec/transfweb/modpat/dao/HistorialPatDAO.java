/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.modpat.dao;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.modpat.HistorialPat;

/**
 *
 * @author Michael Yanangómez
 */
public class HistorialPatDAO extends DAOAbstractPat<HistorialPat> {
    public HistorialPatDAO(HistorialPat h){
        super(h);
    }

    @Override
    public List<HistorialPat> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select h from HistorialPat h");
        return query.getResultList();
    }
    
    public List<HistorialPat> getHistorialBySolicitudSenadi(String solicitudSenadi){
        Query query = this.getEntityManager().createQuery("Select h from HistorialPat h where h.solicitudSenadi = '"+solicitudSenadi+"'");
        return query.getResultList();
    }
    
}
