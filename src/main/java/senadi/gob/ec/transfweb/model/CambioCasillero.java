/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author micharesp
 */
@Entity
@Table(name = "cambio_casillero")
public class CambioCasillero implements Serializable{
    @Id
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "providencia_no")
    private String providencia;
    
    @Column(name = "fecha_providencia")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaProvidencia;
    
    @Column(name = "denominacion")
    private String denominacion;
    
    @Column(name = "titular_casillero_nuevo")
    private String titularCasilleroNuevo;
    
    @Column(name = "titular_casillero_anterior")
    private String titularCasilleroAnterior;
    
    @Column(name = "tramite")
    private String tramite;
    
    @Column(name = "nuevo_casillero")
    private String nuevoCasillero;
    
    @Column(name = "fecha_notificacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNotificacion;
    
    @Column(name = "tipo_modificacion")
    private String tipoModificacion;
    
    @Column(name = "casillero_anterior")
    private String casilleroAnterior;
    
    @Column(name = "estado")
    private String estado;
    
    @Column(name = "usuario")
    private String usuario;
    
    @Column(name = "correo_tit_anterior")
    private String correoTitularAnterior;
    
    @Column(name = "correo_tit_nuevo")
    private String correoTitularNuevo;
    
    @Column(name = "documento")
    private String documento;
    
    @Column(name = "fuente")
    private String fuente;

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the providencia
     */
    public String getProvidencia() {
        return providencia;
    }

    /**
     * @param providencia the providencia to set
     */
    public void setProvidencia(String providencia) {
        this.providencia = providencia;
    }

    /**
     * @return the fechaProvidencia
     */
    public Date getFechaProvidencia() {
        return fechaProvidencia;
    }

    /**
     * @param fechaProvidencia the fechaProvidencia to set
     */
    public void setFechaProvidencia(Date fechaProvidencia) {
        this.fechaProvidencia = fechaProvidencia;
    }

    /**
     * @return the denominacion
     */
    public String getDenominacion() {
        return denominacion;
    }

    /**
     * @param denominacion the denominacion to set
     */
    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    /**
     * @return the titularCasilleroNuevo
     */
    public String getTitularCasilleroNuevo() {
        return titularCasilleroNuevo;
    }

    /**
     * @param titularCasilleroNuevo the titularCasilleroNuevo to set
     */
    public void setTitularCasilleroNuevo(String titularCasilleroNuevo) {
        this.titularCasilleroNuevo = titularCasilleroNuevo;
    }

    /**
     * @return the titularCasilleroAnterior
     */
    public String getTitularCasilleroAnterior() {
        return titularCasilleroAnterior;
    }

    /**
     * @param titularCasilleroAnterior the titularCasilleroAnterior to set
     */
    public void setTitularCasilleroAnterior(String titularCasilleroAnterior) {
        this.titularCasilleroAnterior = titularCasilleroAnterior;
    }

    /**
     * @return the tramite
     */
    public String getTramite() {
        return tramite;
    }

    /**
     * @param tramite the tramite to set
     */
    public void setTramite(String tramite) {
        this.tramite = tramite;
    }

    /**
     * @return the nuevoCasillero
     */
    public String getNuevoCasillero() {
        return nuevoCasillero;
    }

    /**
     * @param nuevoCasillero the nuevoCasillero to set
     */
    public void setNuevoCasillero(String nuevoCasillero) {
        this.nuevoCasillero = nuevoCasillero;
    }

    /**
     * @return the fechaNotificacion
     */
    public Date getFechaNotificacion() {
        return fechaNotificacion;
    }

    /**
     * @param fechaNotificacion the fechaNotificacion to set
     */
    public void setFechaNotificacion(Date fechaNotificacion) {
        this.fechaNotificacion = fechaNotificacion;
    }

    /**
     * @return the tipoModificacion
     */
    public String getTipoModificacion() {
        return tipoModificacion;
    }

    /**
     * @param tipoModificacion the tipoModificacion to set
     */
    public void setTipoModificacion(String tipoModificacion) {
        this.tipoModificacion = tipoModificacion;
    }

    /**
     * @return the casilleroAnterior
     */
    public String getCasilleroAnterior() {
        return casilleroAnterior;
    }

    /**
     * @param casilleroAnterior the casilleroAnterior to set
     */
    public void setCasilleroAnterior(String casilleroAnterior) {
        this.casilleroAnterior = casilleroAnterior;
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
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the correoTitularAnterior
     */
    public String getCorreoTitularAnterior() {
        return correoTitularAnterior;
    }

    /**
     * @param correoTitularAnterior the correoTitularAnterior to set
     */
    public void setCorreoTitularAnterior(String correoTitularAnterior) {
        this.correoTitularAnterior = correoTitularAnterior;
    }

    /**
     * @return the correoTitularNuevo
     */
    public String getCorreoTitularNuevo() {
        return correoTitularNuevo;
    }

    /**
     * @param correoTitularNuevo the correoTitularNuevo to set
     */
    public void setCorreoTitularNuevo(String correoTitularNuevo) {
        this.correoTitularNuevo = correoTitularNuevo;
    }

    /**
     * @return the documento
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * @param documento the documento to set
     */
    public void setDocumento(String documento) {
        this.documento = documento;
    }

    /**
     * @return the fuente
     */
    public String getFuente() {
        return fuente;
    }

    /**
     * @param fuente the fuente to set
     */
    public void setFuente(String fuente) {
        this.fuente = fuente;
    }
}
