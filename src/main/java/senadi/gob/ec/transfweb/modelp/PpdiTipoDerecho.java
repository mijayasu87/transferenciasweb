/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.modelp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Michael
 */
@Entity
@Table(name = "ppdi_tipo_derecho", schema = "iepi_procesos")
public class PpdiTipoDerecho implements Serializable {

    @Id
    @Column(name = "codigo_tipo_derecho")
    private Integer codigoTipoDerecho;

    @Column(name = "codigo_unidad")
    private Integer codigoUnidad;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "alias_tipo_derecho")
    private String aliasTipoDerecho;

    @OneToMany(mappedBy = "ppdiTipoDerecho")
    private List<PpdiSolicitudPatente> ppdiSolicitudesPatente;

    public PpdiTipoDerecho() {
        ppdiSolicitudesPatente = new ArrayList<>();
    }

    /**
     * @return the codigoTipoDerecho
     */
    public Integer getCodigoTipoDerecho() {
        return codigoTipoDerecho;
    }

    /**
     * @param codigoTipoDerecho the codigoTipoDerecho to set
     */
    public void setCodigoTipoDerecho(Integer codigoTipoDerecho) {
        this.codigoTipoDerecho = codigoTipoDerecho;
    }

    /**
     * @return the codigoUnidad
     */
    public Integer getCodigoUnidad() {
        return codigoUnidad;
    }

    /**
     * @param codigoUnidad the codigoUnidad to set
     */
    public void setCodigoUnidad(Integer codigoUnidad) {
        this.codigoUnidad = codigoUnidad;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the aliasTipoDerecho
     */
    public String getAliasTipoDerecho() {
        return aliasTipoDerecho;
    }

    /**
     * @param aliasTipoDerecho the aliasTipoDerecho to set
     */
    public void setAliasTipoDerecho(String aliasTipoDerecho) {
        this.aliasTipoDerecho = aliasTipoDerecho;
    }

    @Override
    public String toString() {
        return getNombre();
    }

    /**
     * @return the ppdiSolicitudesPatente
     */
    public List<PpdiSolicitudPatente> getPpdiSolicitudesPatente() {
        return ppdiSolicitudesPatente;
    }

    /**
     * @param ppdiSolicitudesPatente the ppdiSolicitudesPatente to set
     */
    public void setPpdiSolicitudesPatente(List<PpdiSolicitudPatente> ppdiSolicitudesPatente) {
        this.ppdiSolicitudesPatente = ppdiSolicitudesPatente;
    }
}
