/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.modelp;

import java.util.Date;

/**
 *
 * @author micharesp
 */
public class PpdiTitAndSigno {
    private Integer codigoSolicitudSigno;
    private String numTitulo;
    private String numTramite;
    private String numeroExpediente;
    private Date fechaExpediente;
    private String denominacion;
    private Date fechaEmisionDocumento;
    private Date fechaVencimiento;
    private Date fechaPresentacion;
    private Integer clasificacionNiza;
    private String estado;

    /**
     * @return the codigoSolicitudSigno
     */
    public Integer getCodigoSolicitudSigno() {
        return codigoSolicitudSigno;
    }

    /**
     * @param codigoSolicitudSigno the codigoSolicitudSigno to set
     */
    public void setCodigoSolicitudSigno(Integer codigoSolicitudSigno) {
        this.codigoSolicitudSigno = codigoSolicitudSigno;
    }

    /**
     * @return the numTitulo
     */
    public String getNumTitulo() {
        return numTitulo;
    }

    /**
     * @param numTitulo the numTitulo to set
     */
    public void setNumTitulo(String numTitulo) {
        this.numTitulo = numTitulo;
    }

    /**
     * @return the numTramite
     */
    public String getNumTramite() {
        return numTramite;
    }

    /**
     * @param numTramite the numTramite to set
     */
    public void setNumTramite(String numTramite) {
        this.numTramite = numTramite;
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
     * @return the fechaEmisionDocumento
     */
    public Date getFechaEmisionDocumento() {
        return fechaEmisionDocumento;
    }

    /**
     * @param fechaEmisionDocumento the fechaEmisionDocumento to set
     */
    public void setFechaEmisionDocumento(Date fechaEmisionDocumento) {
        this.fechaEmisionDocumento = fechaEmisionDocumento;
    }

    /**
     * @return the fechaVencimiento
     */
    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    /**
     * @param fechaVencimiento the fechaVencimiento to set
     */
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    
    @Override
    public String toString(){
        return numTramite +", "+denominacion;
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
     * @return the clasificacionNiza
     */
    public Integer getClasificacionNiza() {
        return clasificacionNiza;
    }

    /**
     * @param clasificacionNiza the clasificacionNiza to set
     */
    public void setClasificacionNiza(Integer clasificacionNiza) {
        this.clasificacionNiza = clasificacionNiza;
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
}
