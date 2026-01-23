/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.dao;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.model.CambioCasillero;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author micharesp
 */
public class CambioCasilleroDAO  extends DAOAbstract<CambioCasillero> {

    public CambioCasilleroDAO(CambioCasillero c) {
        super(c);
    }

    @Override
    public List<CambioCasillero> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select c from CambioCasillero c");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
        
    public List<CambioCasillero> getCambioCasilleroByEstado(String estado) {
        Query query = this.getEntityManager().createQuery("Select c from CambioCasillero c where c.estado = '"+estado+"' and c.fuente = 'MODIFICACIONES'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
    
    public String nextProvidencia(){
        Query query = this.getEntityManager().createQuery("Select r from CambioCasillero r where r.id = (Select MAX(r1.id) from CambioCasillero r1 where r1.fuente = 'MODIFICACIONES')");
        List<CambioCasillero> cambios = query.getResultList();
        if(cambios.isEmpty()){
            return "001";
        }else{
            CambioCasillero cc = cambios.get(0);
            int providencia = Integer.parseInt(cc.getProvidencia()) + 1;
            if(providencia < 10){
                return "00"+providencia;
            }else if(providencia < 100){
                return "0"+providencia;
            }else{
                return providencia+"";
            }
        }
    }  
    
    public CambioCasillero getCambioCasilleroWhenNotId(CambioCasillero cambio){
        String fechaPro = Operaciones.formatDate(cambio.getFechaProvidencia());
        Query query = this.getEntityManager().createQuery("Select c from CambioCasillero c where  c.tramite = '"+cambio.getTramite()+"' "
                + "and c.fechaProvidencia = '"+fechaPro+"' and c.providencia = '"+cambio.getProvidencia()+"' and c.fuente = 'MODIFICACIONES'");
        return (CambioCasillero) query.getSingleResult();
    }
    
}
