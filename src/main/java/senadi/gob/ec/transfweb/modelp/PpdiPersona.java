/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.modelp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 *
 * @author micharesp
 */
@Entity
@Table(name = "ppdi_persona", schema = "iepi_procesos")
public class PpdiPersona implements Serializable {

    private static long serialVersionUID = 1L;

    @Id
    @Column(name = "codigo_persona")
    private Integer codigoPersona;

    @Column(name = "numero_identificacion")
    private String numeroIdentificacion;

    @Column(name = "nombre_persona")
    private String nombrePersona;

    @Column(name = "direccion_persona")
    private String direccionPersona;

    @ManyToMany
    private List<PpdiSolicitudPatente> ppdiSolicitudesPatente;

    public PpdiPersona() {
        ppdiSolicitudesPatente = new ArrayList<>();

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
     * @return the codigoPersona
     */
    public Integer getCodigoPersona() {
        return codigoPersona;
    }

    /**
     * @param codigoPersona the codigoPersona to set
     */
    public void setCodigoPersona(Integer codigoPersona) {
        this.codigoPersona = codigoPersona;
    }

    /**
     * @return the numeroIdentificacion
     */
    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    /**
     * @param numeroIdentificacion the numeroIdentificacion to set
     */
    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    /**
     * @return the nombrePersona
     */
    public String getNombrePersona() {
        return nombrePersona;
    }

    /**
     * @param nombrePersona the nombrePersona to set
     */
    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }

    /**
     * @return the direccionPersona
     */
    public String getDireccionPersona() {
        return direccionPersona;
    }

    /**
     * @param direccionPersona the direccionPersona to set
     */
    public void setDireccionPersona(String direccionPersona) {
        this.direccionPersona = direccionPersona;
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
