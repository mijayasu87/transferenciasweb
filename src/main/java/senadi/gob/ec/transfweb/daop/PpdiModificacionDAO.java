/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.daop;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudModificacion;


/**
 *
 * @author micharesp
 */
public class PpdiModificacionDAO extends DAOAbstractP<PpdiSolicitudModificacion> {

    public PpdiModificacionDAO(PpdiSolicitudModificacion pd){
        super(pd);
    }
    
    @Override
    public List<PpdiSolicitudModificacion> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select p from PpdiSolicitudModificacion p");
        return query.getResultList();
    }
    
    public PpdiSolicitudModificacion getPpdiSolicitudModificacionByExpedient(String expedient){
        Query query = this.getEntityManager().createQuery("Select p from PpdiSolicitudModificacion p where p.numeroExpediente = '"+expedient+"'");
        if(query.getResultList().isEmpty()){
            return new PpdiSolicitudModificacion();
        }else{
            return (PpdiSolicitudModificacion) query.getResultList().get(0);
        }
    }
    
    public PpdiSolicitudModificacion getPpdiSolicitudModificacionByNumTramite(String tramite){
        Query query = this.getEntityManager().createQuery("Select p from PpdiSolicitudModificacion p where p.numeroTramiteModificacion = '"+tramite+"'");
        if(query.getResultList().isEmpty()){
            return new PpdiSolicitudModificacion();
        }else{
            return (PpdiSolicitudModificacion) query.getResultList().get(0);
        }
    }
    
    
    
}
