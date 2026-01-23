/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.dao;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.model.ModificationScope;

/**
 *
 * @author michael
 */
public class ModificationScopeDAO extends DAOAbstract<ModificationScope> {

    public ModificationScopeDAO(ModificationScope t) {
        super(t);
    }

    @Override
    public List<ModificationScope> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select m from ModificationScope m order by m.scopeNumber desc");
        return query.getResultList();
    }

    public List<ModificationScope> getScopesSent() {
        Query query = this.getEntityManager().createQuery("Select m from ModificationScope m where m.status = 'ENVIADO' order by CAST(m.scopeNumber AS UNSIGNED) desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<ModificationScope> getScopesByCriterio(String criterio) {
        Query query = this.getEntityManager().createQuery("Select m from ModificationScope m where m.scopeNumber like :criterio or m.affectedApplicationNumber like :criterio order by m.scopeNumber desc");
        query.setParameter("criterio", "%" + criterio + "%");
        return query.setMaxResults(300).getResultList();
    }

    public List<ModificationScope> getScopesBySubmissionDate(Date start, Date end) {
        Calendar calIni = Calendar.getInstance();
        calIni.setTime(start);
        calIni.set(Calendar.HOUR_OF_DAY, 0);
        calIni.set(Calendar.MINUTE, 0);
        calIni.set(Calendar.SECOND, 0);
        calIni.set(Calendar.MILLISECOND, 0);

        Calendar calFin = Calendar.getInstance();
        calFin.setTime(end);
        calFin.set(Calendar.HOUR_OF_DAY, 23);
        calFin.set(Calendar.MINUTE, 59);
        calFin.set(Calendar.SECOND, 59);
        calFin.set(Calendar.MILLISECOND, 999);

        Timestamp ini = new Timestamp(calIni.getTimeInMillis());
        Timestamp fin = new Timestamp(calFin.getTimeInMillis());

        Query query = this.getEntityManager().createQuery("Select m from ModificationScope m where m.submissionDate between :ini and :fin");
        query.setParameter("ini", ini);
        query.setParameter("fin", fin);
        return query.setMaxResults(300).getResultList();
    }

}
