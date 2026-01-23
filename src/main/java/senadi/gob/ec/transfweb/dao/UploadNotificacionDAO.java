/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.transfweb.model.UploadNotificacion;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author micharesp
 */
public class UploadNotificacionDAO extends DAOAbstract<UploadNotificacion>{
     public UploadNotificacionDAO(UploadNotificacion t) {
        super(t);
    }

    @Override
    public List<UploadNotificacion> buscarTodos() {
        Query query = this.getEntityManager().createQuery("SELECT t FROM UploadNotificacion T ORDER BY t.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }
    
    public List<UploadNotificacion> getNotificacionesByEstado(boolean estado){
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.estado = "+estado+" and u.activo = true");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
    
    public boolean validarExistenciaUploadNotificacion(String solicitud, String documento){
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.solicitud = '"+solicitud+"' and u.documento = '"+documento+"'");
        if(query.getResultList().isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    
    public boolean validarExistenciaUploadNotificacion(String solicitud, boolean estado){
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.solicitud = '"+solicitud+"' and u.estado = "
                +estado+" and u.activo = true");
        if(query.getResultList().isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    
    public boolean validarExistenciaUploadDocumento(String documento, boolean estado){
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.documento = '"+documento+"' and u.estado = "
                +estado+" and u.activo = true");
        if(query.getResultList().isEmpty()){
            return false;
        }else{
            return true;
        }
    }    
    
    public List<UploadNotificacion> getUploadNotificacionByCriterio(String criterio, boolean estado){
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.solicitud like '%"
                +criterio+"%' and u.estado = "+estado+" and u.activo = "+estado+" order by u.id desc");
        return query.getResultList();
    }    
    
    public List<UploadNotificacion> getUploadNotificacionBySolicitud(String solicitud, boolean estado){
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.solicitud = '"+solicitud+"' "
                + "and u.estado = "+estado+" and u.activo = "+estado+" order by u.id desc");
        return query.getResultList();
    }
    
    public List<UploadNotificacion> getUploadNotificacionByDate(Date start, Date end, boolean estado){
        String ini = Operaciones.formatDate(start);
        String fin = Operaciones.formatDate(end);
        Query query = this.getEntityManager().createQuery("Select u from UploadNotificacion u where u.activo = "+estado+" and u.estado = "+estado+" and "
                + "u.fechaNotificacion BETWEEN '"+ini+"' and '"+fin+"' order by u.id desc");
        return query.getResultList();
    }
}
