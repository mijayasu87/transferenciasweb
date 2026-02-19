/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author michael
 */
@Entity
@Table(name = "desistimiento")
public class Desistimiento implements Serializable {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "solicitud")
    private String solicitud;

    @Column(name = "fecha_solicitud")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaSolicitud;

    @Column(name = "resolucion")
    private String resolucion;

    @Column(name = "fecha_resolucion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaResolucion;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "fecha_titulo")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaTitulo;

    @Column(name = "fecha_vencimiento")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaVencimiento;

    @Column(name = "denominacion")
    private String denominacion;

    @Column(name = "signo")
    private String signo;

    @Column(name = "titular_anterior")
    private String titularAnterior;

    @Column(name = "titular_actual")
    private String titularActual;

    @Column(name = "abogado_patrocinador")
    private String abogadoPatrocinador;

    @Column(name = "casillero_senadi")
    private String casilleroSenadi;

    @Column(name = "casillero_judicial")
    private String casilleroJudicial;

    @Column(name = "email")
    private String email;

    @Column(name = "ro")
    private String ro;

    @Column(name = "responsable")
    private String responsable;

    @Column(name = "identificacion")
    private String identificacion;

    private String r1;
    private String r2;
    private String r3;
    private String r4;
    private String r5;
    private String r6;
    private String r7;

    @Column(name = "etiqueta")
    private String etiqueta;

    @Column(name = "comprobante")
    private String comprobante;

    @Transient
    private String rutaExpediente;

    @Transient
    private Integer idRenewalForm;
    
    @Column(name = "cancelado")
    private String cancelado;
    
    @Column(name = "solicitante")
    private String solicitante;

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
     * @return the fechaSolicitud
     */
    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * @param fechaSolicitud the fechaSolicitud to set
     */
    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
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
     * @return the fechaTitulo
     */
    public Date getFechaTitulo() {
        return fechaTitulo;
    }

    /**
     * @param fechaTitulo the fechaTitulo to set
     */
    public void setFechaTitulo(Date fechaTitulo) {
        this.fechaTitulo = fechaTitulo;
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
     * @return the titularAnterior
     */
    public String getTitularAnterior() {
        return titularAnterior;
    }

    /**
     * @param titularAnterior the titularAnterior to set
     */
    public void setTitularAnterior(String titularAnterior) {
        this.titularAnterior = titularAnterior;
    }

    /**
     * @return the titularActual
     */
    public String getTitularActual() {
        return titularActual;
    }

    /**
     * @param titularActual the titularActual to set
     */
    public void setTitularActual(String titularActual) {
        this.titularActual = titularActual;
    }

    /**
     * @return the abogadoPatrocinador
     */
    public String getAbogadoPatrocinador() {
        return abogadoPatrocinador;
    }

    /**
     * @param abogadoPatrocinador the abogadoPatrocinador to set
     */
    public void setAbogadoPatrocinador(String abogadoPatrocinador) {
        this.abogadoPatrocinador = abogadoPatrocinador;
    }

    /**
     * @return the casilleroSenadi
     */
    public String getCasilleroSenadi() {
        return casilleroSenadi;
    }

    /**
     * @param casilleroSenadi the casilleroSenadi to set
     */
    public void setCasilleroSenadi(String casilleroSenadi) {
        this.casilleroSenadi = casilleroSenadi;
    }

    /**
     * @return the casilleroJudicial
     */
    public String getCasilleroJudicial() {
        return casilleroJudicial;
    }

    /**
     * @param casilleroJudicial the casilleroJudicial to set
     */
    public void setCasilleroJudicial(String casilleroJudicial) {
        this.casilleroJudicial = casilleroJudicial;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the ro
     */
    public String getRo() {
        return ro;
    }

    /**
     * @param ro the ro to set
     */
    public void setRo(String ro) {
        this.ro = ro;
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
     * @return the identificacion
     */
    public String getIdentificacion() {
        return identificacion;
    }

    /**
     * @param identificacion the identificacion to set
     */
    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    /**
     * @return the r1
     */
    public String getR1() {
        return r1;
    }

    /**
     * @param r1 the r1 to set
     */
    public void setR1(String r1) {
        this.r1 = r1;
    }

    /**
     * @return the r2
     */
    public String getR2() {
        return r2;
    }

    /**
     * @param r2 the r2 to set
     */
    public void setR2(String r2) {
        this.r2 = r2;
    }

    /**
     * @return the r3
     */
    public String getR3() {
        return r3;
    }

    /**
     * @param r3 the r3 to set
     */
    public void setR3(String r3) {
        this.r3 = r3;
    }

    /**
     * @return the r4
     */
    public String getR4() {
        return r4;
    }

    /**
     * @param r4 the r4 to set
     */
    public void setR4(String r4) {
        this.r4 = r4;
    }

    /**
     * @return the r5
     */
    public String getR5() {
        return r5;
    }

    /**
     * @param r5 the r5 to set
     */
    public void setR5(String r5) {
        this.r5 = r5;
    }

    /**
     * @return the r6
     */
    public String getR6() {
        return r6;
    }

    /**
     * @param r6 the r6 to set
     */
    public void setR6(String r6) {
        this.r6 = r6;
    }

    /**
     * @return the r7
     */
    public String getR7() {
        return r7;
    }

    /**
     * @param r7 the r7 to set
     */
    public void setR7(String r7) {
        this.r7 = r7;
    }

    /**
     * @return the etiqueta
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * @param etiqueta the etiqueta to set
     */
    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /**
     * @return the rutaExpediente
     */
    public String getRutaExpediente() {
        return "https://registro.propiedadintelectual.gob.ec/solicitudes/media/files/renewal_forms/" + getIdRenewalForm() + "/";
    }

    /**
     * @param rutaExpediente the rutaExpediente to set
     */
    public void setRutaExpediente(String rutaExpediente) {
        this.rutaExpediente = rutaExpediente;
    }

    /**
     * @return the idRenewalForm
     */
    public Integer getIdRenewalForm() {
        return idRenewalForm;
    }

    /**
     * @param idRenewalForm the idRenewalForm to set
     */
    public void setIdRenewalForm(Integer idRenewalForm) {
        this.idRenewalForm = idRenewalForm;
    }

    /**
     * @return the comprobante
     */
    public String getComprobante() {
        return comprobante;
    }

    /**
     * @param comprobante the comprobante to set
     */
    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    /**
     * @return the cancelado
     */
    public String getCancelado() {
        return cancelado;
    }

    /**
     * @param cancelado the cancelado to set
     */
    public void setCancelado(String cancelado) {
        this.cancelado = cancelado;
    }

    /**
     * @return the solicitante
     */
    public String getSolicitante() {
        return solicitante;
    }

    /**
     * @param solicitante the solicitante to set
     */
    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }
}
