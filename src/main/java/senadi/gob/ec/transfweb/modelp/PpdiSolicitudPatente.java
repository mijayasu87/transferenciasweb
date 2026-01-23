/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.modelp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author micharesp
 */
@Entity
@Table(name = "ppdi_solicitud_patente", schema = "iepi_procesos")
public class PpdiSolicitudPatente implements Serializable{
    private static long serialVersionUID = 1L;

    @Id
    @Column(name = "codigo_solicitud_patente")
    private Integer codigoSolicitudPatente;
        
    @Column(name = "numero_tramite_patente")
    private String numeroTramite;
    
    @Column(name = "numero_expediente_patente")
    private String numeroExpediente;
    
    @Column(name = "fecha_presentacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaPresentacion;
    
    @Column(name = "titulo")
    private String titulo;
    
    @Column(name = "numero_gaceta")
    private String numeroGaceta;
    
    @Column(name = "estado_gaceta")
    private String estadoGaceta;
    
    @Column(name = "draft")
    private String draft;
    
    @Column(name = "clasificacion_internacional")
    private String clasificacionInternacional;
    
    @Column(name = "fecha_terminacion_gaceta")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaTerminacionGaceta;
    
    @ManyToOne    
    @JoinColumn(name = "codigo_tipo_derecho")
    private PpdiTipoDerecho ppdiTipoDerecho;
    
    @ManyToMany(mappedBy = "ppdiSolicitudesPatente")
    private List<PpdiPersona> ppdiPersonas;

    
    public PpdiSolicitudPatente(){
        ppdiPersonas = new ArrayList<>();
    }

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
     * @return the codigoSolicitudPatente
     */
    public Integer getCodigoSolicitudPatente() {
        return codigoSolicitudPatente;
    }

    /**
     * @param codigoSolicitudPatente the codigoSolicitudPatente to set
     */
    public void setCodigoSolicitudPatente(Integer codigoSolicitudPatente) {
        this.codigoSolicitudPatente = codigoSolicitudPatente;
    }

    /**
     * @return the numeroTramite
     */
    public String getNumeroTramite() {
        return numeroTramite;
    }

    /**
     * @param numeroTramite the numeroTramite to set
     */
    public void setNumeroTramite(String numeroTramite) {
        this.numeroTramite = numeroTramite;
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

    /**
     * @return the fechaPresentacion
     */
    public Date getFechaPresentacion() {
        return fechaPresentacion;
    }

    /**
     * @param fechaPresentacion the fechaPresentacion to set
     */
    public void setFechaPresentacion(Date fechaPresentacion) {
        this.fechaPresentacion = fechaPresentacion;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the numeroGaceta
     */
    public String getNumeroGaceta() {
        return numeroGaceta;
    }

    /**
     * @param numeroGaceta the numeroGaceta to set
     */
    public void setNumeroGaceta(String numeroGaceta) {
        this.numeroGaceta = numeroGaceta;
    }

    /**
     * @return the estadoGaceta
     */
    public String getEstadoGaceta() {
        return estadoGaceta;
    }

    /**
     * @param estadoGaceta the estadoGaceta to set
     */
    public void setEstadoGaceta(String estadoGaceta) {
        this.estadoGaceta = estadoGaceta;
    }

    /**
     * @return the draft
     */
    public String getDraft() {
        return draft;
    }

    /**
     * @param draft the draft to set
     */
    public void setDraft(String draft) {
        this.draft = draft;
    }

    /**
     * @return the fechaTerminacionGaceta
     */
    public Date getFechaTerminacionGaceta() {
        return fechaTerminacionGaceta;
    }

    /**
     * @param fechaTerminacionGaceta the fechaTerminacionGaceta to set
     */
    public void setFechaTerminacionGaceta(Date fechaTerminacionGaceta) {
        this.fechaTerminacionGaceta = fechaTerminacionGaceta;
    }

    /**
     * @return the ppdiTipoDerecho
     */
    public PpdiTipoDerecho getPpdiTipoDerecho() {
        return ppdiTipoDerecho;
    }

    /**
     * @param ppdiTipoDerecho the ppdiTipoDerecho to set
     */
    public void setPpdiTipoDerecho(PpdiTipoDerecho ppdiTipoDerecho) {
        this.ppdiTipoDerecho = ppdiTipoDerecho;
    }

    /**
     * @return the ppdiPersonas
     */
    public List<PpdiPersona> getPpdiPersonas() {
        return ppdiPersonas;
    }

    /**
     * @param ppdiPersonas the ppdiPersonas to set
     */
    public void setPpdiPersonas(List<PpdiPersona> ppdiPersonas) {
        this.ppdiPersonas = ppdiPersonas;
    }

    /**
     * @return the clasificacionInternacional
     */
    public String getClasificacionInternacional() {
        return clasificacionInternacional;
    }

    /**
     * @param clasificacionInternacional the clasificacionInternacional to set
     */
    public void setClasificacionInternacional(String clasificacionInternacional) {
        this.clasificacionInternacional = clasificacionInternacional;
    }

}
