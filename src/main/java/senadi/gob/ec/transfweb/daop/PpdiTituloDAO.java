/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.daop;

import java.util.ArrayList;
import java.util.Date;
import senadi.gob.ec.transfweb.modelp.PpdiTituloSignoDistintivo;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.modelp.PpdiTitAndSigno;

/**
 *
 * @author michael
 */
public class PpdiTituloDAO extends DAOAbstractP<PpdiTituloSignoDistintivo> {

    public PpdiTituloDAO(PpdiTituloSignoDistintivo p) {
        super(p);
    }

    @Override
    public List<PpdiTituloSignoDistintivo> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select p from PpdiTituloSignoDistintivo p");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public PpdiTituloSignoDistintivo getPpdiTituloSignoDistintivoByNumeroTitulo(String numeroTitulo) {
        Query query = this.getEntityManager().createQuery("Select p from PpdiTituloSignoDistintivo p where p.numeroTitulo = '" + numeroTitulo + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return new PpdiTituloSignoDistintivo();
        } else {
            return (PpdiTituloSignoDistintivo) query.getResultList().get(0);
        }
    }

    //select * 
//from ppdi_titulo_signo_distintivo ptsd  
//inner join ppdi_solicitud_signo_distintivo pssd on pssd.codigo_solicitud_signo = ptsd.codigo_solicitud_signo 
//where ptsd.numero_titulo ilike '%1278%' and pssd.denominacion_signo ilike 'EXPLORER DE MARATHON'
    public PpdiTituloSignoDistintivo getPpdiTituloSignoDistintivoAndSignoByNumeroTitulo(String titulo, String denominacion) {
        String sql = "select * "
                + "from iepi_procesos.ppdi_titulo_signo_distintivo ptsd  "
                + "inner join iepi_procesos.ppdi_solicitud_signo_distintivo pssd on pssd.codigo_solicitud_signo = ptsd.codigo_solicitud_signo "
                + "where ptsd.numero_titulo ilike '"+titulo+"' and pssd.denominacion_signo ILIKE ?"; //'"+denominacion+"'"

        Query query = this.getEntityManager().createNativeQuery(sql);
        query.setParameter(1, "%"+denominacion+"%");
        
        List<Object[]> result = query.getResultList();        
        List<PpdiTituloSignoDistintivo> tits = new ArrayList<>();
        
        for (int i = 0; i < result.size(); i++) {
            Object[] aux = result.get(i);

            PpdiTituloSignoDistintivo titul = new PpdiTituloSignoDistintivo();
            titul.setCodigoSolicitudSigno((Integer) aux[0]);
            titul.setCodigoSolicitudSigno((Integer)aux[1]);
            titul.setFechaEmisionDocumento((Date)aux[2]);
            titul.setUsuarioElabora((String)aux[3]);
            titul.setUsuarioElabora((String)aux[4]);
            titul.setNumeroTitulo((String)aux[5]);
            titul.setFechaVencimientoTitulo((Date)aux[6]);
            titul.setTitular((String)aux[7]);
            
            tits.add(titul);
            break;
        }

        if(tits.isEmpty()){
            return new PpdiTituloSignoDistintivo();
        }else{
            return tits.get(0);
        }
    }

    public List<PpdiTituloSignoDistintivo> getPpdiTitulosSignoDistintivoByNumeroTitulo(String numeroTitulo) {
        Query query = this.getEntityManager().createQuery("Select p from PpdiTituloSignoDistintivo p where p.numeroTitulo = '" + numeroTitulo + "'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public PpdiTituloSignoDistintivo getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(Integer codigoSolicitudSigno) {
        Query query = this.getEntityManager().createQuery("Select p from PpdiTituloSignoDistintivo p where p.codigoSolicitudSigno = " + codigoSolicitudSigno+" order by p.fechaEmisionDocumento desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if (query.getResultList().isEmpty()) {
            return new PpdiTituloSignoDistintivo();
        } else {
            return (PpdiTituloSignoDistintivo) query.getResultList().get(0);
        }
    }

    public List<PpdiTitAndSigno> getPpdiTituloAndSigno(String numeroTitulo) {
        String sql = "SELECT pssd.numero_expediente, "
                + "pssd.denominacion_signo, pssd.fecha_expediente, pt.codigo_solicitud_signo, "
                + "pt.numero_titulo, pssd.numero_tramite, pt.fecha_emision_documento, pt.fecha_vencimiento_titulo "
                + "FROM iepi_procesos.ppdi_titulo_signo_distintivo AS pt "
                + "INNER JOIN iepi_procesos.ppdi_solicitud_signo_distintivo AS pssd on pssd.codigo_solicitud_signo = pt.codigo_solicitud_signo "
                + "WHERE lower(pt.numero_titulo) = '" + numeroTitulo.toLowerCase().trim() + "'";
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
            titandsigno.setNumTitulo((String) objeto[4]);
            titandsigno.setNumTramite((String) objeto[5]);
            titandsigno.setFechaEmisionDocumento((Date) objeto[6]);
            titandsigno.setFechaVencimiento((Date) objeto[7]);
            titsandsignos.add(titandsigno);
        }

        return titsandsignos;
    }

    public List<PpdiTitAndSigno> getPpdiTituloAndSignoByTituloOrDenominacion(String criterio) {
        String sql = "SELECT pssd.numero_expediente, "
                + "pssd.denominacion_signo, pssd.fecha_expediente, pt.codigo_solicitud_signo, "
                + "pt.numero_titulo, pssd.numero_tramite, pt.fecha_emision_documento, pt.fecha_vencimiento_titulo "
                + "FROM iepi_procesos.ppdi_titulo_signo_distintivo AS pt "
                + "INNER JOIN iepi_procesos.ppdi_solicitud_signo_distintivo AS pssd on pssd.codigo_solicitud_signo = pt.codigo_solicitud_signo "
                + "WHERE lower(pt.numero_titulo) = '" + criterio.toLowerCase().trim() + "' or pssd.denominacion_signo ilike '%" + criterio + "%'";
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
            titandsigno.setNumTitulo((String) objeto[4]);
            titandsigno.setNumTramite((String) objeto[5]);
            titandsigno.setFechaEmisionDocumento((Date) objeto[6]);
            titandsigno.setFechaVencimiento((Date) objeto[7]);
            if (titandsigno.getNumTitulo() != null && !titandsigno.getNumTitulo().isEmpty()) {
                titsandsignos.add(titandsigno);
            }

        }

        return titsandsignos;
    }

    public int getNextNumeroMinimoTitulo() {
        Query query = this.getEntityManager().createQuery("Select p from PpdiTituloSignoDistintivo p where p.codigoTituloSignoDistintivo = (Select MIN(p1.codigoTituloSignoDistintivo) from PpdiTituloSignoDistintivo p1)");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        PpdiTituloSignoDistintivo titulo = (PpdiTituloSignoDistintivo) query.getSingleResult();
        return titulo.getCodigoTituloSignoDistintivo() - 1;

    }
}
