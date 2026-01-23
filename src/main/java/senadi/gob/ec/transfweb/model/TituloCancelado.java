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
import javax.persistence.Transient;

/**
 *
 * @author micharesp
 */
@Entity
@Table(name = "titulo_cancelado")
public class TituloCancelado implements Serializable {

    @Id
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "numero_titulo")
    private String numeroTitulo;
    
    @Column(name = "numero_tramite")
    private String numeroTramite;
    
    @Column(name = "expediente")
    private String expediente;
    
    @Column(name = "fecha_expediente")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaExpediente;
    
    @Column(name = "denominacion")   
    private String denominacion;
    
    @Column(name = "tipo_cancelacion")
    private String tipoCancelacion;
    
    @Column(name = "fecha_cancelacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaCancelacion;
    
    @Column(name = "usuario")
    private String usuario;
    
    @Column(name = "resolucion")
    private String resolucion;
    
    @Column(name = "fecha_resolucion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaResolucion;    
    
    @Column(name = "documento")
    private String documento;
        
    @Column(name = "producto_vigente")
    private String productosVigentes;
    
    @Column(name = "numero_tramite_ocdi")
    private String numeroTramiteOCDI;
    
    @Column(name = "reverso")
    private boolean reverso;
    
    @Column(name = "usuario_reverso")
    private String usuarioReverso;
    
    @Column(name = "fecha_reverso")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaReverso;
    
    @Column(name = "manual")
    private boolean manual;
    
    @Column(name = "tipo")
    private String tipo;
    
    @Transient
    private String denominationOCDI;
    
    @Transient
    private String tipoText;

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
     * @return the numeroTitulo
     */
    public String getNumeroTitulo() {
        return numeroTitulo;
    }

    /**
     * @param numeroTitulo the numeroTitulo to set
     */
    public void setNumeroTitulo(String numeroTitulo) {
        this.numeroTitulo = numeroTitulo;
    }

    /**
     * @return the expediente
     */
    public String getExpediente() {
        return expediente;
    }

    /**
     * @param expediente the expediente to set
     */
    public void setExpediente(String expediente) {
        this.expediente = expediente;
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
     * @return the tipoCancelacion
     */
    public String getTipoCancelacion() {
        return tipoCancelacion;
    }

    /**
     * @param tipoCancelacion the tipoCancelacion to set
     */
    public void setTipoCancelacion(String tipoCancelacion) {
        this.tipoCancelacion = tipoCancelacion;
    }

    /**
     * @return the fechaCancelacion
     */
    public Date getFechaCancelacion() {
        return fechaCancelacion;
    }

    /**
     * @param fechaCancelacion the fechaCancelacion to set
     */
    public void setFechaCancelacion(Date fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
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
    
    @Override
    public String toString(){
        return getNumeroTitulo()+", "+getExpediente()+", "+getUsuario();
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
     * @return the resolucion
     */
    public String getResolucion() {
        return resolucion;
    }

    /**
     * @param resolucion the resolucion to set
     */
    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    /**
     * @return the fechaResolucion
     */
    public Date getFechaResolucion() {
        return fechaResolucion;
    }

    /**
     * @param fechaResolucion the fechaResolucion to set
     */
    public void setFechaResolucion(Date fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
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
     * @return the productosVigentes
     */
    public String getProductosVigentes() {
        return productosVigentes;
    }

    /**
     * @param productosVigentes the productosVigentes to set
     */
    public void setProductosVigentes(String productosVigentes) {
        this.productosVigentes = productosVigentes;
    }

    /**
     * @return the numeroTramiteOCDI
     */
    public String getNumeroTramiteOCDI() {
        return numeroTramiteOCDI;
    }

    /**
     * @param numeroTramiteOCDI the numeroTramiteOCDI to set
     */
    public void setNumeroTramiteOCDI(String numeroTramiteOCDI) {
        this.numeroTramiteOCDI = numeroTramiteOCDI;
    }

    /**
     * @return the denominationOCDI
     */
    public String getDenominationOCDI() {
        return denominationOCDI;
    }

    /**
     * @param denominationOCDI the denominationOCDI to set
     */
    public void setDenominationOCDI(String denominationOCDI) {
        this.denominationOCDI = denominationOCDI;
    }

    /**
     * @return the reverso
     */
    public boolean isReverso() {
        return reverso;
    }

    /**
     * @param reverso the reverso to set
     */
    public void setReverso(boolean reverso) {
        this.reverso = reverso;
    }

    /**
     * @return the fechaReverso
     */
    public Date getFechaReverso() {
        return fechaReverso;
    }

    /**
     * @param fechaReverso the fechaReverso to set
     */
    public void setFechaReverso(Date fechaReverso) {
        this.fechaReverso = fechaReverso;
    }

    /**
     * @return the manual
     */
    public boolean isManual() {
        return manual;
    }

    /**
     * @param manual the manual to set
     */
    public void setManual(boolean manual) {
        this.manual = manual;
    }

    /**
     * @return the usuarioReverso
     */
    public String isUsuarioReverso() {
        return usuarioReverso;
    }

    /**
     * @param usuarioReverso the usuarioReverso to set
     */
    public void setUsuarioReverso(String usuarioReverso) {
        this.usuarioReverso = usuarioReverso;
    }

    /**
     * @return the tipoText
     */
    public String getTipoText() {
        if(tipoCancelacion.equals("TOTAL")){
            return "CANCELACIÓN TOTAL";
        }else if(tipoCancelacion.equals("PARCIAL")){
            return "CANCELACIÓN PARCIAL";
        }else{
            return tipoCancelacion;
        }        
    }

    /**
     * @param tipoText the tipoText to set
     */
    public void setTipoText(String tipoText) {
        this.tipoText = tipoText;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
}
