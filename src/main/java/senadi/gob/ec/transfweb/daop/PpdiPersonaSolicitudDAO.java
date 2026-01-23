/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.daop;

import senadi.gob.ec.transfweb.modelp.PpdiPersonaSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.modelp.PpdiPersona;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author micharesp
 */
public class PpdiPersonaSolicitudDAO extends DAOAbstractP<PpdiPersonaSolicitudSignoDistintivo> {

    public PpdiPersonaSolicitudDAO(PpdiPersonaSolicitudSignoDistintivo p) {
        super(p);
    }

    @Override
    public List<PpdiPersonaSolicitudSignoDistintivo> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select p from PpdiPersonaSolicitudSignoDistintivo p");
        return query.getResultList();
    }
    
    public List<PpdiPersonaSolicitudSignoDistintivo> getPpdiPersonaSolicitudSignoByCodigoSolicitud(Integer codigoSolicitud){
        Query query = this.getEntityManager().createQuery("Select p from PpdiPersonaSolicitudSignoDistintivo p where p.codigoSolicitudSigno = "+codigoSolicitud);
        return query.getResultList();
    }
    
    public PpdiPersona getPpdiPersonaByCodigoPersona(Integer codigoPersona){
        Query query = this.getEntityManager().createQuery("Select p from PpdiPersona p where p.codigoPersona = "+codigoPersona);
        if(!query.getResultList().isEmpty()){
            return (PpdiPersona) query.getSingleResult();
        }else{
            System.err.println("No existe la persona con id: "+codigoPersona+" en ppdi_persona");
            return new PpdiPersona();
        }
        
    }

}
