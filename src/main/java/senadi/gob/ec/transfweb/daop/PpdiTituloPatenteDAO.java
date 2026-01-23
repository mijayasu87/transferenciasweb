/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.daop;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.modelp.PpdiTituloPatente;

/**
 *
 * @author micharesp
 */
public class PpdiTituloPatenteDAO extends DAOAbstractP<PpdiTituloPatente> {

    public PpdiTituloPatenteDAO(PpdiTituloPatente p) {
        super(p);
    }

    @Override
    public List<PpdiTituloPatente> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select p from PpdiTituloPatente p");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public PpdiTituloPatente getPpdiTituloPatenteByNumeroTitulo(String numeroTitulo) {
        Query query = this.getEntityManager().createQuery("Select p from PpdiTituloPatente p where p.numeroTitulo = '" + numeroTitulo + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return new PpdiTituloPatente();
        } else {
            return (PpdiTituloPatente) query.getResultList().get(0);
        }
    }

    public PpdiTituloPatente getPpdiTituloPatenteByCodigoSolicitudPatente(Integer codigoSolicitudPatente) {
        Query query = this.getEntityManager().createQuery("Select p from PpdiTituloPatente p where p.codigoSolicitudPatente = " + codigoSolicitudPatente);
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return new PpdiTituloPatente();
        } else {
            return (PpdiTituloPatente) query.getResultList().get(0);
        }
    }

}
