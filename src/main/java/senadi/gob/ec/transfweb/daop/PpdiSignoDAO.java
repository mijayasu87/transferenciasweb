/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.daop;

import java.util.ArrayList;
import java.util.Date;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.modelp.PpdiResolucion;
import senadi.gob.ec.transfweb.modelp.PpdiTitAndSigno;

/**
 *
 * @author michael
 */
public class PpdiSignoDAO extends DAOAbstractP<PpdiSolicitudSignoDistintivo> {
    
    public PpdiSignoDAO(PpdiSolicitudSignoDistintivo pd) {
        super(pd);
    }
    
    @Override
    public List<PpdiSolicitudSignoDistintivo> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select p from PpdiSolicitudSignoDistintivo p");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
    
    public PpdiSolicitudSignoDistintivo getPpdiSolicitudSignoDistintivoByExpedient(String expedient) {
        Query query = this.getEntityManager().createQuery("Select p from PpdiSolicitudSignoDistintivo p where p.numeroExpediente = '" + expedient + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return new PpdiSolicitudSignoDistintivo();
        } else {
            return (PpdiSolicitudSignoDistintivo) query.getResultList().get(0);
        }
    }
    
    public PpdiSolicitudSignoDistintivo getPpdiSolicitudSignoDistintivoByCodigoSolicitud(Integer codigoSolicitud) {        
        Query query = this.getEntityManager().createQuery("Select p from PpdiSolicitudSignoDistintivo p where p.codigoSolicitudSigno = " + codigoSolicitud);
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<PpdiSolicitudSignoDistintivo> marcas = query.getResultList();
        if (marcas.isEmpty()) {
            return new PpdiSolicitudSignoDistintivo();
        } else {
            return marcas.get(0);
        }
    }
    
    public PpdiResolucion getPpdiResolucionByResolutionNumber(String resolutionNumber) {
        Query query = this.getEntityManager().createQuery("Select r from PpdiResolucion r where r.numeroResolucion = '" + resolutionNumber + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<PpdiResolucion> resoluciones = query.getResultList();
        if (resoluciones.isEmpty()) {
            return new PpdiResolucion();
        } else {
            return resoluciones.get(0);
        }
    }
    
    public List<PpdiTitAndSigno> getPpdiSingoAndTituloByDenominacion(String denominacion) {
        String sql = "SELECT pssd.numero_expediente, pssd.denominacion_signo, pssd.fecha_expediente, pssd.codigo_solicitud_signo, "
                + "                pssd.numero_tramite, pt.numero_titulo, pt.fecha_emision_documento, pt.fecha_vencimiento_titulo, "
                + "                pssd.fecha_presentacion, pssd.codigo_clasificacion_niza, pssd.estado"
                + "                FROM iepi_procesos.ppdi_solicitud_signo_distintivo AS pssd "
                + "                left JOIN iepi_procesos.ppdi_titulo_signo_distintivo AS pt on pssd.codigo_solicitud_signo = pt.codigo_solicitud_signo "
                + "                WHERE lower(pssd.denominacion_signo) like '%" + denominacion.trim().toLowerCase() + "%' and pt.numero_titulo is null "
                + "                and pssd.estado not in ('OPOSICIONES','EXAMEN DE FORMA','EXAMEN DE FONDO')";
        
        Query query = this.getEntityManager().createNativeQuery(sql);
        
        List<Object[]> resultados = query.getResultList();
        List<PpdiTitAndSigno> titsandsignos = new ArrayList<>();
        for (int i = 0; i < resultados.size(); i++) {
            Object[] objeto = resultados.get(i);
            
            PpdiTitAndSigno titandsigno = new PpdiTitAndSigno();
            titandsigno.setNumeroExpediente((String) objeto[0]);
            titandsigno.setDenominacion((String) objeto[1]);
            titandsigno.setFechaExpediente((Date) objeto[2]);
            titandsigno.setCodigoSolicitudSigno((Integer) objeto[3]);
            titandsigno.setNumTramite((String) objeto[4]);
            titandsigno.setNumTitulo((String) objeto[5]);            
            titandsigno.setFechaEmisionDocumento((Date) objeto[6]);
            titandsigno.setFechaVencimiento((Date) objeto[7]);
            titandsigno.setFechaPresentacion((Date) objeto[8]);
            titandsigno.setClasificacionNiza((Integer) objeto[9]);
            titandsigno.setEstado((String) objeto[10]);
            titsandsignos.add(titandsigno);
        }
        
        return titsandsignos;
    }
    
}
