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
import org.apache.commons.lang3.StringUtils;
import senadi.gob.ec.transfweb.model.Transferencia;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author Michael
 */
public class TransferenciaDAO extends DAOAbstract<Transferencia> {

    public TransferenciaDAO(Transferencia t) {
        super(t);
    }

    @Override
    public List<Transferencia> buscarTodos() {
        Query query = this.getEntityManager().createQuery("SELECT t FROM Transferencia T ORDER BY t.fechaCertificado DESC, t.certificado DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public List<Transferencia> getAllTransferencias() {
        Query query = this.getEntityManager().createQuery("SELECT t FROM Transferencia t order by t.id");
        return query.getResultList();
    }

    public boolean existeTramite(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from Transferencia t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaTransferencia(Transferencia t) {
        Query query = this.getEntityManager().createQuery("Select t from Transferencia t where t.solicitud = '" + t.getSolicitud() + "' and t.id != " + t.getId() + "");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaTransferencia(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select t from Transferencia t where t.solicitud = '" + solicitud + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public List<Transferencia> getTransferenciaByCriteria(String text) {
        int n = 0;
        String certificado = "";
        if (StringUtils.isNumeric(text)) {
            certificado = "or t.certificado = " + text + " ";
            System.out.println("llega por aquí");
        }

        Query query = this.getEntityManager().createQuery("Select t from Transferencia t where t.solicitud LIKE '%" + text + "%' "
                + certificado
                + "or t.denominacion LIKE '%" + text + "%' "
                + "or t.titularActual LIKE '%" + text + "%' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Transferencia> getTransferenciaByFecha(Date inicio, Date fin) {
        String start = Operaciones.formatDate(inicio);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from Transferencia t where t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaCertificado BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Transferencia> getTransferenciaByFechaCertificado(Date inicio, Date fin) {
        String start = Operaciones.formatDate(inicio);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from Transferencia t where t.fechaCertificado BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public int getNextNumeroCertificado() {
        Query query = this.getEntityManager().createQuery("Select r from Transferencia r where r.id = (Select MAX(r1.id) from Transferencia r1)");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        Transferencia r = (Transferencia) query.getSingleResult();
        return r.getCertificado() + 1;

    }

    public Transferencia getTransferenciaBySolSenadi(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select r from Transferencia r where r.solicitud = '" + solicitud + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (!query.getResultList().isEmpty()) {
            return (Transferencia) query.getResultList().get(0);
        } else {
            return new Transferencia();
        }
    }

    public List<String[]> getTransferenciasCertificadoEmitido() {
        System.out.println("---------------------Empezando el reconocimiento------------------");
        String sql = "Select t.solicitud,n.documento,n.casillero from "
                + "transferencia as t "
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

    public List<Transferencia> getTransferenciasByDenominacion(String denominacion) {
        Query query = this.getEntityManager().createQuery("Select r from Transferencia r where r.denominacion like '%" + denominacion + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<Transferencia> getTransferenciasByTitular(String titular) {
        Query query = this.getEntityManager().createQuery("Select r from Transferencia r where r.titularAnterior like '%" + titular + "%' "
                + "or r.titularActual like '%" + titular + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

}
