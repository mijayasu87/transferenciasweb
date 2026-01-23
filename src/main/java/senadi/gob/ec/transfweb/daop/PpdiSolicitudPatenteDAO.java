/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.daop;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudPatente;


/**
 *
 * @author micharesp
 */
public class PpdiSolicitudPatenteDAO extends DAOAbstractP<PpdiSolicitudPatente> {

    public PpdiSolicitudPatenteDAO(PpdiSolicitudPatente t) {
        super(t);
    }

    @Override
    public List<PpdiSolicitudPatente> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select p from PpdiSolicitudPatente p");
        return query.getResultList();
    }

    public PpdiSolicitudPatente getPpdiSolicitudPatenteByTramite(String tramite) {
        Query query = this.getEntityManager().createQuery("Select p from PpdiSolicitudPatente p where p.numeroTramite = '" + tramite + "'");
        List<PpdiSolicitudPatente> pats = query.getResultList();
        if (pats.isEmpty()) {
            return new PpdiSolicitudPatente();
        } else {
            return pats.get(0);
        }
    }

    public PpdiSolicitudPatente getPpdiSolicitudPatenteByExpediente(String expediente) {
        Query query = this.getEntityManager().createQuery("Select p from PpdiSolicitudPatente p where p.numeroExpediente = '" + expediente + "'");
        List<PpdiSolicitudPatente> pats = query.getResultList();
        if (pats.isEmpty()) {
            return new PpdiSolicitudPatente();
        } else {
            return pats.get(0);
        }
    }
}
