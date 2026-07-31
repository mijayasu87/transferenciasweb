/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author Michael
 */
public class NotificacionDAO extends DAOAbstract<Notificacion> {

    public NotificacionDAO(Notificacion n) {
        super(n);
    }

    @Override
    public List<Notificacion> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select n from Notificacion n ORDER BY n.id DESC");
        return query.setMaxResults(300).getResultList();
    }
    
    public List<Notificacion> getAllNotificaciones(){
        Query query = this.getEntityManager().createQuery("Select n from Notificacion n ORDER BY n.id");
        return query.getResultList();
    }

    public boolean existeTramite(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select n from Notificacion n where n.solicitud = '" + numeroTramite + "'");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaNotificacion(Notificacion n) {
        Query query = this.getEntityManager().createQuery("Select n from Notificacion n where n.solicitud = '" + n.getSolicitud() + "' and n.id != " + n.getId() + "");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaNotificada(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select n from Notificacion n where n.solicitud = '" + solicitud + "'");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public List<Notificacion> getNotificacionByCriteria(String text) {
        Query query = this.getEntityManager().createQuery("Select n from Notificacion n where n.solicitud LIKE '%" + text + "%' or n.denominacion LIKE '%" + text + "%' "
                + "or n.titularActual LIKE '%" + text + "%' ORDER BY n.id DESC");
        return query.getResultList();
    }

    public Notificacion getNotificacionBySolicitud(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select r from Notificacion r where r.solicitud = '" + solicitud + "'");
        if (!query.getResultList().isEmpty()) {
            return (Notificacion) query.getResultList().get(0);
        } else {
            return new Notificacion();
        }
    }

    public List<Notificacion> getNotificacionesByDenominacion(String denominacion) {
        Query query = this.getEntityManager().createQuery("Select r from Notificacion r where r.denominacion like '%" + denominacion + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Notificacion> getNotificacionByFecha(Date inicio, Date fin) {
        String start = Operaciones.formatDate(inicio);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select n from Notificacion n where n.fechaPresentacion between '" + start + "' and '" + end + "' "
                + "or n.fechaCertificado between '" + start + "' and '" + end + "' or n.fechaRegistro between '" + start + "' and '" + end + "' ORDER BY n.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Notificacion> getNotificacionByFechaCertificado(Date inicio, Date fin) {
        String start = Operaciones.formatDate(inicio);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select n from Notificacion n where n.fechaCertificado between '" + start + "' and '" + end + "' ORDER BY n.id DESC");
        return query.getResultList();
    }
    
    public List<Notificacion> getNotificacionByFechaNotificacion(Date inicio, Date fin) {
        String start = Operaciones.formatDate(inicio);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select n from Notificacion n where n.fechaNotificacion between '" + start + "' and '" + end + "' ORDER BY n.id DESC");
        return query.getResultList();
    }

    /**
     * **Esperando a ver como es la funcionalidad*************
     * @param fechaElaboraNotificacion: fecha en la que se elabora la Notificación
     * @return el siguiente número de notificación
     */
    public int getNextNumeroNotificacion(Date fechaElaboraNotificacion) {
        Query query = this.getEntityManager().createQuery("Select n from Notificacion n where n.id = (Select MAX(n1.id) from Notificacion n1)");

        Notificacion n = (Notificacion) query.getSingleResult();

        int yearElNot = fechaElaboraNotificacion.getYear() + 1900;
        int yearNot = n.getFechaElaboraNotificacion().getYear() + 1900;

        if (yearElNot == yearNot) {
            return n.getNotificacion() + 1;
        } else if (yearElNot > yearNot) {
            return 1;
        } else {
            return -1;
        }
    }

    public List<String[]> getNotificadasEmitido() {
        System.out.println("---------------------Empezando el reconocimiento------------------");
        String sql = "Select t.solicitud,n.documento,n.casillero from "
                + "notificacion as t "
                + "left join notificacion_casillero as n on t.solicitud = n.solicitud "
                + "where n.estado_notificacion = 1";

        Query query = this.getEntityManager().createNativeQuery(sql);
        List<Object[]> result = query.getResultList();
        List<String[]> transferencias = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            Object[] aux = result.get(i);

            String[] transfer = {aux[0].toString(), "https://registro.propiedadintelectual.gob.ec/casilleros/media/files/" + aux[2].toString() + "/" + aux[1].toString()};
            transferencias.add(transfer);
        }

        System.out.println("--------------------Terminado el reconocimiento-----------------");
        System.out.println("Trámites encontrados: " + transferencias.size());
        return transferencias;
    }

    public List<Notificacion> getNotificacionesByTitular(String titular) {
        Query query = this.getEntityManager().createQuery("Select r from Notificacion r where r.titularAnterior like '%" + titular + "%' "
                + "or r.titularActual like '%" + titular + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Notificacion> getProrrogasCandidatas() {
        Query query = this.getEntityManager().createQuery(
                "SELECT n FROM Notificacion n WHERE n.fechaPuestaProrroga IS NOT NULL"
        );
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

}
