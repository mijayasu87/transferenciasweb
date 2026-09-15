/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.model.Prorroga;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author Michael
 */
public class ProrrogaDAO extends DAOAbstract<Prorroga> {

    public ProrrogaDAO(Prorroga n) {
        super(n);
    }

    @Override
    public List<Prorroga> buscarTodos() {
        try {
            Query query = this.getEntityManager().createQuery("Select n from Prorroga n ORDER BY n.id DESC");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query.setMaxResults(300).getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public boolean validarExistenciaProrroga(Prorroga n) {
        try {
            Query query = this.getEntityManager().createQuery("Select n from Prorroga n where n.solicitud = '" + n.getSolicitud() + "' and n.id != " + n.getId() + "");
            return !query.getResultList().isEmpty();
        } finally {
            this.getEntityManager().close();
        }
    }

    public boolean existeTramite(String numeroTramite) {
        try {
            Query query = this.getEntityManager().createQuery("Select n from Prorroga n where n.solicitud = :tramite");
            query.setParameter("tramite", numeroTramite);
            return !query.getResultList().isEmpty();
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<Prorroga> getProrrogaByCriteria(String text) {
        try {
            Query query = this.getEntityManager().createQuery("Select n from Prorroga n where n.solicitud LIKE '%" + text + "%' or n.denominacion LIKE '%" + text + "%' "
                    + "or n.titularActual LIKE '%" + text + "%' ORDER BY n.id DESC");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query.getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public Prorroga getProrrogaBySolicitud(String solicitud) {
        try {
            Query query = this.getEntityManager().createQuery("Select r from Prorroga r where r.solicitud = :solicitud");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            query.setParameter("solicitud", solicitud);
            if (!query.getResultList().isEmpty()) {
                return (Prorroga) query.getResultList().get(0);
            } else {
                return new Prorroga();
            }
        } finally {
            this.getEntityManager().close();
        }
    }

    public Prorroga getProrrogaById(Integer id) {
        try {
            Query query = this.getEntityManager().createQuery("Select r from Prorroga r where r.id = :idp");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            query.setParameter("idp", id);
            if (!query.getResultList().isEmpty()) {
                return (Prorroga) query.getResultList().get(0);
            } else {
                return new Prorroga();
            }
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<Prorroga> getProrrogasByDenominacion(String denominacion) {
        try {
            Query query = this.getEntityManager().createQuery("Select r from Prorroga r where r.denominacion like '%" + denominacion + "%' ORDER BY r.id DESC");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query.getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<Prorroga> getProrrogaByTitular(String titular) {
        try {
            Query query = this.getEntityManager().createQuery("Select r from Prorroga r where r.titularAnterior like '%" + titular + "%' "
                    + "or r.titularActual like '%" + titular + "%' ORDER BY r.id DESC");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query.getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public List<Prorroga> getProrrogaByFecha(Date inicio, Date fin) {
        try {
            String start = Operaciones.formatDate(inicio);
            String end = Operaciones.formatDate(fin);
            Query query = this.getEntityManager().createQuery("Select n from Prorroga n where n.fechaPresentacion between '" + start + "' and '" + end + "' "
                    + "or n.fechaNotificacion between '" + start + "' and '" + end + "' or n.fechaRegistro between '" + start + "' and '" + end + "' ORDER BY n.id DESC");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query.getResultList();
        } finally {
            this.getEntityManager().close();
        }
    }

    public int getNextNumeroProrroga(Date fechaProrroga) {
        try {
            Query query = this.getEntityManager().createQuery("Select n from Prorroga n where n.numeroProrroga = (Select MAX(n1.numeroProrroga) from Prorroga n1)");
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");

            List<Prorroga> prorrogas = query.getResultList();
            if (prorrogas.isEmpty()) {
                return 1;
            } else {
                Prorroga p = prorrogas.get(0);

                int yearElPro = fechaProrroga.getYear() + 1900;
                int yearPro = p.getFechaProrroga() != null ? p.getFechaProrroga().getYear() + 1900 : yearElPro;

                if (yearElPro == yearPro) {
                    return (p.getNumeroProrroga() == null ? 0 : p.getNumeroProrroga()) + 1;
                } else if (yearElPro > yearPro) {
                    return 1;
                } else {
                    return -1;
                }
            }
        } finally {
            this.getEntityManager().close();
        }
    }

}
