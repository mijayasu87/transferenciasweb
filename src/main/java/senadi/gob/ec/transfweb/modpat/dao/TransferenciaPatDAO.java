/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.modpat.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.modpat.TransferenciaPat;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author Michael
 */
public class TransferenciaPatDAO extends DAOAbstractPat<TransferenciaPat> {

    public TransferenciaPatDAO(TransferenciaPat t) {
        super(t);
    }

    @Override
    public List<TransferenciaPat> buscarTodos() {
        Query query = this.getEntityManager().createQuery("SELECT t FROM TransferenciaPat T ORDER BY t.fechaCertificado DESC, t.certificado DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }

    public boolean existeTramite(String numeroTramite) {
        Query query = this.getEntityManager().createQuery("Select t from TransferenciaPat t where t.solicitud = '" + numeroTramite + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaTransferencia(TransferenciaPat t) {
        Query query = this.getEntityManager().createQuery("Select t from TransferenciaPat t where t.solicitud = '" + t.getSolicitud() + "' and t.id != " + t.getId() + "");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaTransferencia(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select t from TransferenciaPat t where t.solicitud = '" + solicitud + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public List<TransferenciaPat> getTransferenciaByCriteria(String text) {
        Query query = this.getEntityManager().createQuery("Select t from TransferenciaPat t where t.solicitud LIKE '%" + text + "%' or t.denominacion LIKE '%" + text + "%' "
                + "or t.titularActual LIKE '%" + text + "%' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<TransferenciaPat> getTransferenciaByFecha(Date inicio, Date fin) {
        String start = Operaciones.formatDate(inicio);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from TransferenciaPat t where t.fechaPresentacion BETWEEN '" + start + "' and '" + end + "' "
                + " or t.fechaCertificado BETWEEN '" + start + "' and '" + end + "' or t.fechaRegistro BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<TransferenciaPat> getTransferenciaByFechaCertificado(Date inicio, Date fin) {
        String start = Operaciones.formatDate(inicio);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select t from TransferenciaPat t where t.fechaCertificado BETWEEN '" + start + "' and '" + end + "' ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public int getNextNumeroCertificado() {
        Query query = this.getEntityManager().createQuery("Select r from TransferenciaPat r where r.id = (Select MAX(r1.id) from TransferenciaPat r1)");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<TransferenciaPat> transferencias = query.getResultList();
        if (transferencias.isEmpty()) {
            return 1;
        } else {
            TransferenciaPat r = transferencias.get(0);

            Date fechaActual = new Date();

            if (r.getFechaCertificado().getYear() == fechaActual.getYear()) {
                return r.getCertificado() + 1;
            } else if (r.getFechaCertificado().getYear() < fechaActual.getYear()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public TransferenciaPat getTransferenciaBySolSenadi(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select r from TransferenciaPat r where r.solicitud = '" + solicitud + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (!query.getResultList().isEmpty()) {
            return (TransferenciaPat) query.getResultList().get(0);
        } else {
            return new TransferenciaPat();
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

    public List<TransferenciaPat> getTransferenciasByDenominacion(String denominacion) {
        Query query = this.getEntityManager().createQuery("Select r from TransferenciaPat r where r.denominacion like '%" + denominacion + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<TransferenciaPat> getTransferenciasByTitular(String titular) {
        Query query = this.getEntityManager().createQuery("Select r from TransferenciaPat r where r.titularAnterior like '%" + titular + "%' "
                + "or r.titularActual like '%" + titular + "%'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
    
    

}
