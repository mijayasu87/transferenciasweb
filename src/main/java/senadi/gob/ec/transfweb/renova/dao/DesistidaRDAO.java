/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.renova.dao;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.renova.model.Desistida;

/**
 *
 * @author micharesp
 */
public class DesistidaRDAO extends DAOAbstractRen<Desistida>{

    public DesistidaRDAO(Desistida d){
        super(d);
    }
    
    @Override
    public List<Desistida> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select d from Desistida d order by d.id");
        return query.getResultList();
    }
    
    
    public List<Desistida> getDesistidasByTituloAndDenominacion(String titulo, String denominacion) {
        Query query = this.getEntityManager().createQuery("Select r from Desistida r where r.registroNo = '"+titulo+"' and r.denominacion = '"+denominacion.replace("'", "''")+"'");        
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    
    
}
