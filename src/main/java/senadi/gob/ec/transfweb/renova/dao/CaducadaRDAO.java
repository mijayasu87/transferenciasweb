/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.renova.dao;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.renova.model.CaducadaRen;

/**
 *
 * @author micharesp
 */
public class CaducadaRDAO extends DAOAbstractRen<CaducadaRen> {

    public CaducadaRDAO(CaducadaRen c) {
        super(c);
    }

    public List<CaducadaRen> getCaducadasByTituloAndDenominacion(String titulo, String denominacion) {
        Query query = this.getEntityManager().createQuery("Select r from CaducadaRen r where r.registroNo = '"+titulo+"' and r.denominacion = '"+denominacion.replace("'", "''")+"'");        
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    @Override
    public List<CaducadaRen> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select c from CaducadaRen c order by c.id");
        return query.getResultList();                        
    }

}
