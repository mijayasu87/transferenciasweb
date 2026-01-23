/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.dao;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.model.Abandono;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author Michael
 */
public class AbandonoDAO extends DAOAbstract<Abandono> {

    public AbandonoDAO(Abandono n) {
        super(n);
    }

    @Override
    public List<Abandono> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select n from Abandono n ORDER BY n.id DESC");
        return query.setMaxResults(300).getResultList();
    }

    public List<Abandono> getAllAbandonos() {
        Query query = this.getEntityManager().createQuery("Select n from Abandono n ORDER BY n.id");
        return query.getResultList();
    }

    public boolean existeTramite(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select n from Abandono n where n.solicitud = '" + numeroTramite + "'");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaAbandono(Abandono n) {
        Query query = this.getEntityManager().createQuery("Select n from Abandono n where n.solicitud = '" + n.getSolicitud() + "' and n.id != " + n.getId() + "");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaAbandono(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select n from Abandono n where n.solicitud = '" + solicitud + "'");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public List<Abandono> getAbandonoByCriteria(String text) {
        Query query = this.getEntityManager().createQuery("Select n from Abandono n where n.solicitud LIKE '%" + text + "%' or n.denominacion LIKE '%" + text + "%' "
                + "or n.titularActual LIKE '%" + text + "%' ORDER BY n.id DESC");
        return query.getResultList();
    }

    public Abandono getAbandonoBySolicitud(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select r from Abandono r where r.solicitud = '" + solicitud + "'");
        if (!query.getResultList().isEmpty()) {
            return (Abandono) query.getResultList().get(0);
        } else {
            return new Abandono();
        }
    }

    public List<Abandono> getAbandonosByDenominacion(String denominacion) {
        Query query = this.getEntityManager().createQuery("Select r from Abandono r where r.denominacion like '%" + denominacion + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Abandono> getAbandonoByFecha(Date inicio, Date fin) {
        String start = Operaciones.formatDate(inicio);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select n from Abandono n where n.fechaPresentacion between '" + start + "' and '" + end + "' "
                + "or n.fechaCertificado between '" + start + "' and '" + end + "' or n.fechaRegistro between '" + start + "' and '" + end + "' ORDER BY n.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Abandono> getAbandonoByFechaCertificado(Date inicio, Date fin) {
        String start = Operaciones.formatDate(inicio);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select n from Abandono n where n.fechaCertificado between '" + start + "' and '" + end + "' ORDER BY n.id DESC");
        return query.getResultList();
    }

    public List<Abandono> getAbandonoByFechaAbandono(Date inicio, Date fin) {
        String start = Operaciones.formatDate(inicio);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select n from Abandono n where n.fechaNotificacion between '" + start + "' and '" + end + "' ORDER BY n.id DESC");
        return query.getResultList();
    }

    /**
     * **Esperando a ver como es la funcionalidad
     *
     *************
     * @param fechaAbandono
     * @return
     */
    public int getNextNumeroAbandono(Date fechaAbandono) {
        Query query = this.getEntityManager().createQuery("Select n from Abandono n where n.id = (Select MAX(n1.id) from Abandono n1)");

        List<Abandono> abandonos = query.getResultList();
        if (abandonos.isEmpty()) {
            return 1;
        } else {
            Abandono abandono = abandonos.get(0);

            int yearElNot = fechaAbandono.getYear() + 1900;
            int yearNot = abandono.getFechaAbandono().getYear() + 1900;

            if (yearElNot == yearNot) {

                int nextabandono = abandono.getNumeroAbandono() + 1;
                System.out.println("asignación de número de abandono " + nextabandono);
                return nextabandono;
            } else if (yearElNot > yearNot) {
                return 1;
            } else {
                return -1;
            }
        }

    }

    public List<String[]> getAbandonosEmitido() {
        System.out.println("---------------------Empezando el reconocimiento------------------");
        String sql = "Select t.solicitud,n.documento,n.casillero from "
                + "abandono as t "
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

    public List<Abandono> getAbandonoByTitular(String titular) {
        Query query = this.getEntityManager().createQuery("Select r from Abandono r where r.titularAnterior like '%" + titular + "%' "
                + "or r.titularActual like '%" + titular + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Notificacion> getAbandonosErjafeVencidos(int dias) {

        // Calcula la fecha límite en Java
        LocalDate fechaLimite = LocalDate.now().minusDays(dias);
        Date fechaLimiteDate = java.sql.Date.valueOf(fechaLimite);

        Query query = this.getEntityManager().createQuery("SELECT n FROM Notificacion n WHERE n.tipoAbandono = :tipo AND n.fechaPuestaAbandono <= :fechaLimite");
        query.setParameter("tipo", "ERJAFE");
        query.setParameter("fechaLimite", fechaLimiteDate);
        return query.getResultList();
    }

    /**
     * Funciona para coa y reglamento
     * @param diasHabiles: número de días a calcular
     * @param type: tipo de abandono (COA ó REGLAMENTO)
     * @return devuelve el listado de notificaciones que están vencidas
     */
    public List<Notificacion> getAbandonosSinFinesSemana(int diasHabiles, String type) {
        LocalDate fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(diasHabiles);
        Date fechaLimiteDate = java.sql.Date.valueOf(fechaLimite);

        Query query = this.getEntityManager().createQuery(
                "SELECT n FROM Notificacion n WHERE n.tipoAbandono = :tipo AND n.fechaPuestaAbandono <= :fechaLimite"
        );
        query.setParameter("tipo", type);
        query.setParameter("fechaLimite", fechaLimiteDate);
        return query.getResultList();
    }    

}
