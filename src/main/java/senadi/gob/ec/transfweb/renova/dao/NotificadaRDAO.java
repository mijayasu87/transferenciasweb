/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.renova.dao;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.renova.model.Notificada;

/**
 *
 * @author micharesp
 */
public class NotificadaRDAO extends DAOAbstractRen<Notificada>{

    public NotificadaRDAO(Notificada n){
        super(n);
    }
    
    @Override
    public List<Notificada> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select n from Notificada n order by n.id");
        return query.getResultList();
    }
    
    public List<Notificada> getNotificadaByTituloAndDenominacion(String titulo, String denominacion){
        Query query = this.getEntityManager().createQuery("Select r from Notificada r where r.registroNo = '"+titulo+"' and r.denominacion = '"+denominacion.replace("'", "''")+"'");        
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
    
}
