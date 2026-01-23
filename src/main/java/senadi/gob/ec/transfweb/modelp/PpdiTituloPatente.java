/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.modelp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import senadi.gob.ec.transfweb.util.Operaciones;

/**
 *
 * @author michael
 */
@Entity
@Table(name = "ppdi_titulo_patente", schema = "iepi_procesos")
public class PpdiTituloPatente implements Serializable {

    private static long serialVersionUID = 1L;

    @Id
    @Column(name = "codigo_titulo_patente")
    private Integer codigoTituloPatente;

    @Column(name = "codigo_solicitud_patente")
    private Integer codigoSolicitudPatente;

    @Column(name = "numero_titulo")
    private String numeroTitulo;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(name = "fecha_emision_titulo")
    private Date fechaEmisionDocumento;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(name = "fecha_vencimiento")
    private Date fechaVencimientoTitulo;

    @Column(name = "usuario_elabora")
    private String usuarioElabora;    
    

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
     * @return the codigoTituloPatente
     */
    public Integer getCodigoTituloPatente() {
        return codigoTituloPatente;
    }

    /**
     * @param codigoTituloPatente the codigoTituloPatente to set
     */
    public void setCodigoTituloPatente(Integer codigoTituloPatente) {
        this.codigoTituloPatente = codigoTituloPatente;
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
     * @return the fechaVencimientoTitulo
     */
    public Date getFechaVencimientoTitulo() {
        return fechaVencimientoTitulo;
    }

    /**
     * @param fechaVencimientoTitulo the fechaVencimientoTitulo to set
     */
    public void setFechaVencimientoTitulo(Date fechaVencimientoTitulo) {
        this.fechaVencimientoTitulo = fechaVencimientoTitulo;
    }

    /**
     * @return the usuarioElabora
     */
    public String getUsuarioElabora() {
        return usuarioElabora;
    }

    /**
     * @param usuarioElabora the usuarioElabora to set
     */
    public void setUsuarioElabora(String usuarioElabora) {
        this.usuarioElabora = usuarioElabora;
    }

    @Override
    public String toString() {
        return getNumeroTitulo() + ", " + Operaciones.formatDate(getFechaEmisionDocumento());
    }

}
