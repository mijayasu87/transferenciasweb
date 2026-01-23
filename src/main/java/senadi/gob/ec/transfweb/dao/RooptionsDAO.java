/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.dao;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.model.Rooptions;

/**
 *
 * @author micharesp
 */
public class RooptionsDAO extends DAOAbstract<Rooptions> {

    public RooptionsDAO(Rooptions r) {
        super(r);
    }

    @Override
    public List<Rooptions> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select r from Rooptions r");
        return query.getResultList();
    }

    public List<Rooptions> buscarRoBySolicitud(String solicitud){
        Query query = this.getEntityManager().createQuery("Select r from Rooptions r where r.solicitud = '"+solicitud+"'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
}
