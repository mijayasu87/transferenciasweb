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

/**
 *
 * @author micharesp
 */
@Entity
@Table(name = "ppdi_resolucion", schema = "iepi_procesos")
public class PpdiResolucion implements Serializable {

    private static long serialVersionUID = 1L;

    @Id
    @Column(name = "codigo_resolucion")
    private Integer codigoResolucion;

    @Column(name = "codigo_solicitud")
    private Integer codigoSolicitud;

    @Column(name = "fecha_resolucion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaResolucion;

    @Column(name = "fecha_presentacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaPresentacion;

    @Column(name = "nombre_experto")
    private String nombreExperto;

    @Column(name = "nombre_funcionario_elabora")
    private String nombreFuncionarioElabora;

    @Column(name = "nombre_secretario")
    private String nombreSecretario;

    @Column(name = "numero_resolucion")
    private String numeroResolucion;

    @Column(name = "estado_resolucion")
    private String estadoResolucion;

    @Column(name = "observacion")
    private String observacion;

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
     * @return the codigoResolucion
     */
    public Integer getCodigoResolucion() {
        return codigoResolucion;
    }

    /**
     * @param codigoResolucion the codigoResolucion to set
     */
    public void setCodigoResolucion(Integer codigoResolucion) {
        this.codigoResolucion = codigoResolucion;
    }

    /**
     * @return the codigoSolicitud
     */
    public Integer getCodigoSolicitud() {
        return codigoSolicitud;
    }

    /**
     * @param codigoSolicitud the codigoSolicitud to set
     */
    public void setCodigoSolicitud(Integer codigoSolicitud) {
        this.codigoSolicitud = codigoSolicitud;
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
     * @return the nombreExperto
     */
    public String getNombreExperto() {
        return nombreExperto;
    }

    /**
     * @param nombreExperto the nombreExperto to set
     */
    public void setNombreExperto(String nombreExperto) {
        this.nombreExperto = nombreExperto;
    }

    /**
     * @return the nombreFuncionarioElabora
     */
    public String getNombreFuncionarioElabora() {
        return nombreFuncionarioElabora;
    }

    /**
     * @param nombreFuncionarioElabora the nombreFuncionarioElabora to set
     */
    public void setNombreFuncionarioElabora(String nombreFuncionarioElabora) {
        this.nombreFuncionarioElabora = nombreFuncionarioElabora;
    }

    /**
     * @return the nombreSecretario
     */
    public String getNombreSecretario() {
        return nombreSecretario;
    }

    /**
     * @param nombreSecretario the nombreSecretario to set
     */
    public void setNombreSecretario(String nombreSecretario) {
        this.nombreSecretario = nombreSecretario;
    }

    /**
     * @return the numeroResolucion
     */
    public String getNumeroResolucion() {
        return numeroResolucion;
    }

    /**
     * @param numeroResolucion the numeroResolucion to set
     */
    public void setNumeroResolucion(String numeroResolucion) {
        this.numeroResolucion = numeroResolucion;
    }

    /**
     * @return the estadoResolucion
     */
    public String getEstadoResolucion() {
        return estadoResolucion;
    }

    /**
     * @param estadoResolucion the estadoResolucion to set
     */
    public void setEstadoResolucion(String estadoResolucion) {
        this.estadoResolucion = estadoResolucion;
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
}
