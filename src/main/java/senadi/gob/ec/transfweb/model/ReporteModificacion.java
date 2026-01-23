/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.model;

import java.util.Date;

/**
 *
 * @author micharesp
 */
public class ReporteModificacion {

    private String solicitud;
    private String denominacion;
    private String estado;
    private String casillero;
    private Date fechaPresentacion;
    private String registro;
    private Date fechaRegistro;
    private String signo;
    private String responsable;
    
    private String numDocumento;
    private Date fechaDocumento;

    private String tipo;
    
    private boolean documentoEmitido;
    private boolean notificacionEmitida;
    
    private String rutaDocumento;
    
    private String Actor1;
    private String Actor2;
    
    private String tipoActor1;
    private String tipoActor2;

    /**
     * @return the solicitud
     */
    public String getSolicitud() {
        return solicitud;
    }

    /**
     * @param solicitud the solicitud to set
     */
    public void setSolicitud(String solicitud) {
        this.solicitud = solicitud;
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
     * @return the casillero
     */
    public String getCasillero() {
        return casillero;
    }

    /**
     * @param casillero the casillero to set
     */
    public void setCasillero(String casillero) {
        this.casillero = casillero;
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
     * @return the fechaRegistro
     */
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * @param fechaRegistro the fechaRegistro to set
     */
    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * @return the registro
     */
    public String getRegistro() {
        return registro;
    }

    /**
     * @param registro the registro to set
     */
    public void setRegistro(String registro) {
        this.registro = registro;
    }

    /**
     * @return the signo
     */
    public String getSigno() {
        return signo;
    }

    /**
     * @param signo the signo to set
     */
    public void setSigno(String signo) {
        this.signo = signo;
    }

    /**
     * @return the responsable
     */
    public String getResponsable() {
        return responsable;
    }

    /**
     * @param responsable the responsable to set
     */
    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    /**
     * @return the numDocumento
     */
    public String getNumDocumento() {
        return numDocumento;
    }

    /**
     * @param numDocumento the numDocumento to set
     */
    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    /**
     * @return the fechaDocumento
     */
    public Date getFechaDocumento() {
        return fechaDocumento;
    }

    /**
     * @param fechaDocumento the fechaDocumento to set
     */
    public void setFechaDocumento(Date fechaDocumento) {
        this.fechaDocumento = fechaDocumento;
    }

    /**
     * @return the documentoEmitido
     */
    public boolean isDocumentoEmitido() {
        return documentoEmitido;
    }

    /**
     * @param documentoEmitido the documentoEmitido to set
     */
    public void setDocumentoEmitido(boolean documentoEmitido) {
        this.documentoEmitido = documentoEmitido;
    }

    /**
     * @return the notificacionEmitida
     */
    public boolean isNotificacionEmitida() {
        return notificacionEmitida;
    }

    /**
     * @param notificacionEmitida the notificacionEmitida to set
     */
    public void setNotificacionEmitida(boolean notificacionEmitida) {
        this.notificacionEmitida = notificacionEmitida;
    }

    /**
     * @return the rutaDocumento
     */
    public String getRutaDocumento() {
        return rutaDocumento;
    }

    /**
     * @param rutaDocumento the rutaDocumento to set
     */
    public void setRutaDocumento(String rutaDocumento) {
        this.rutaDocumento = rutaDocumento;
    }

    /**
     * @return the Actor1
     */
    public String getActor1() {
        return Actor1;
    }

    /**
     * @param Actor1 the Actor1 to set
     */
    public void setActor1(String Actor1) {
        this.Actor1 = Actor1;
    }

    /**
     * @return the Actor2
     */
    public String getActor2() {
        return Actor2;
    }

    /**
     * @param Actor2 the Actor2 to set
     */
    public void setActor2(String Actor2) {
        this.Actor2 = Actor2;
    }

    /**
     * @return the tipoActor1
     */
    public String getTipoActor1() {
        return tipoActor1;
    }

    /**
     * @param tipoActor1 the tipoActor1 to set
     */
    public void setTipoActor1(String tipoActor1) {
        this.tipoActor1 = tipoActor1;
    }

    /**
     * @return the tipoActor2
     */
    public String getTipoActor2() {
        return tipoActor2;
    }

    /**
     * @param tipoActor2 the tipoActor2 to set
     */
    public void setTipoActor2(String tipoActor2) {
        this.tipoActor2 = tipoActor2;
    }

}
