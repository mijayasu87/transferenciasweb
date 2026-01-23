/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.renova.dao;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.renova.model.Renovacion;

/**
 *
 * @author micharesp
 */
public class RenovacionDAO extends DAOAbstractRen<Renovacion>{
    
    public RenovacionDAO(Renovacion r){
        super(r);
    }

    @Override
    public List<Renovacion> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select r from Renovacion r order by r.id");
        return query.getResultList();
    }
    
    public List<Renovacion> getRenovacionByTituloAndDenominacion(String titulo, String denominacion){
        Query query = this.getEntityManager().createQuery("Select r from Renovacion r where TRIM(r.registroNo) = '"+titulo+"' and TRIM(LOWER(r.denominacion)) = '"+denominacion.replace("'", "''")+"'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
}
