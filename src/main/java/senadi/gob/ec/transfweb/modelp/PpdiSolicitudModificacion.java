/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.modelp;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author micharesp
 */
@Entity
@Table(name = "ppdi_solicitud_modificacion", schema = "iepi_procesos")
public class PpdiSolicitudModificacion implements Serializable{
    private static long serialVersionUID = 1L;
    
    @Id
    @Column(name = "codigo_solicitud_modificacion")
    private Integer codigoSolicitudModificacion;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_actual_sistema")
    private Date fechaActualSistema;
    
    @Column(name = "numero_registro_original")
    private String numeroRegistroOriginal;
    
    @Column(name = "numero_tramite_modificacion")
    private String numeroTramiteModificacion;
    
    @Column(name = "estado")
    private String estado;
    
    @Column(name = "numero_expediente")
    private String numeroExpediente;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_expediente")
    private Date fechaExpediente;
    
    @Column(name = "observacion")
    private String observacion;
    
    @Column(name = "casillero_iepi")
    private String casilleroIepi;
    
    @Column(name = "id_comprobante_deposito")
    private Integer idComprobanteDeposito;

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @param aSerialVersionUID the serialVersionUID to set
     */
    public static void setSerialVersionUID(long aSerialVersionUID) {
        serialVersionUID = aSerialVersionUID;
    }

    /**
     * @return the codigoSolicitudModificacion
     */
    public Integer getCodigoSolicitudModificacion() {
        return codigoSolicitudModificacion;
    }

    /**
     * @param codigoSolicitudModificacion the codigoSolicitudModificacion to set
     */
    public void setCodigoSolicitudModificacion(Integer codigoSolicitudModificacion) {
        this.codigoSolicitudModificacion = codigoSolicitudModificacion;
    }

    /**
     * @return the fechaActualSistema
     */
    public Date getFechaActualSistema() {
        return fechaActualSistema;
    }

    /**
     * @param fechaActualSistema the fechaActualSistema to set
     */
    public void setFechaActualSistema(Date fechaActualSistema) {
        this.fechaActualSistema = fechaActualSistema;
    }

    /**
     * @return the numeroRegistroOriginal
     */
    public String getNumeroRegistroOriginal() {
        return numeroRegistroOriginal;
    }

    /**
     * @param numeroRegistroOriginal the numeroRegistroOriginal to set
     */
    public void setNumeroRegistroOriginal(String numeroRegistroOriginal) {
        this.numeroRegistroOriginal = numeroRegistroOriginal;
    }

    /**
     * @return the numeroTramiteModificacion
     */
    public String getNumeroTramiteModificacion() {
        return numeroTramiteModificacion;
    }

    /**
     * @param numeroTramiteModificacion the numeroTramiteModificacion to set
     */
    public void setNumeroTramiteModificacion(String numeroTramiteModificacion) {
        this.numeroTramiteModificacion = numeroTramiteModificacion;
    }

    /**
     * @return the estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * @return the fechaExpediente
     */
    public Date getFechaExpediente() {
        return fechaExpediente;
    }

    /**
     * @param fechaExpediente the fechaExpediente to set
     */
    public void setFechaExpediente(Date fechaExpediente) {
        this.fechaExpediente = fechaExpediente;
    }

    /**
     * @return the observacion
     */
    public String getObservacion() {
        return observacion;
    }

    /**
     * @param observacion the observacion to set
     */
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    /**
     * @return the casilleroIepi
     */
    public String getCasilleroIepi() {
        return casilleroIepi;
    }

    /**
     * @param casilleroIepi the casilleroIepi to set
     */
    public void setCasilleroIepi(String casilleroIepi) {
        this.casilleroIepi = casilleroIepi;
    }

    /**
     * @return the idComprobanteDeposito
     */
    public Integer getIdComprobanteDeposito() {
        return idComprobanteDeposito;
    }

    /**
     * @param idComprobanteDeposito the idComprobanteDeposito to set
     */
    public void setIdComprobanteDeposito(Integer idComprobanteDeposito) {
        this.idComprobanteDeposito = idComprobanteDeposito;
    }

    /**
     * @return the numeroExpediente
     */
    public String getNumeroExpediente() {
        return numeroExpediente;
    }

    /**
     * @param numeroExpediente the numeroExpediente to set
     */
    public void setNumeroExpediente(String numeroExpediente) {
        this.numeroExpediente = numeroExpediente;
    }
}
