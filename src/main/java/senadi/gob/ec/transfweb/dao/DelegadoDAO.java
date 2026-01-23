/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.dao;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.model.Delegado;

/**
 *
 * @author micharesp
 */
public class DelegadoDAO extends DAOAbstract<Delegado>{
    
    public DelegadoDAO(Delegado d){
        super(d);
    }

    @Override
    public List<Delegado> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select d from Delegado d where d.tipo = 'delegado'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
    
    public List<Delegado> buscarTodosByTipo(String tipo){
        Query query = this.getEntityManager().createQuery("Select d from Delegado d where d.tipo = '"+tipo+"'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
    
    public boolean validarDelegadoActivo(String tipo){
        Query query = this.getEntityManager().createQuery("Select d from Delegado d where d.estado = true and d.tipo = '"+tipo+"'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if(query.getResultList().isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    
    public Delegado getDelegadoActivo(String tipo){
        Query query = this.getEntityManager().createQuery("Select d from Delegado d where d.estado = true and d.tipo = '"+tipo+"'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if(query.getResultList().isEmpty()){
            return new Delegado();
        }else{
            return (Delegado) query.getSingleResult();
        }        
    }
    
}
