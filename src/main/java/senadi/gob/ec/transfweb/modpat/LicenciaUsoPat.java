
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.modpat;

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
@Table(name = "licencia_uso")
public class LicenciaUsoPat implements Serializable {

    @Id
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "solicitud")
    private String solicitud;
    
    @Column(name = "fecha_presentacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaPresentacion;
    
    @Column(name = "denominacion")
    private String denominacion;
    
    @Column(name = "registro")
    private String registro;
    
    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaRegistro;
    
    @Column(name = "signo")
    private String signo;
    
    @Column(name = "fecha_contrato")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaContrato;
    
    @Column(name = "fecha_vencimiento_contrato")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaVenceContrato;
    
    @Column(name = "licenciante")
    private String licenciante;
    
    @Column(name = "licenciatario")
    private String licenciatario;
    
    @Column(name = "apoderado_representante")
    private String apoderadoRepresentante;
    
    @Column(name = "licencia_no")
    private String licenciaNo;
    
    @Column(name = "fecha_licencia")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaLicencia;
    
    @Column(name = "fecha_not_prov_reg")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNotProvReg;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "casillero_senadi")
    private String casilleroSenadi;
    
    @Column(name = "casillero_judicial")
    private String casilleroJudicial;

    @Column(name = "t_pais")
    private String t_pais;
    
    @Column(name = "t_ciudad")
    private String t_ciudad;
    
    @Column(name = "tipo_vigen")
    private String tipoVigen;
    
    @Column(name = "vigen")
    private String vigen;
    
    @Column(name = "tiempo")
    private String tiempo;    
    
    @Column(name = "n_j")
    private String n_j;
    
    @Column(name = "notificacion")
    private String notificacion;
    
    @Column(name = "fecha_notificacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNotificacion;
    
    @Column(name = "fecha_noti_prov_reg")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNotiProvReg;
    
    @Column(name = "reg_cont_anterior_no")
    private String regContAnteriorNo;
    
    @Column(name = "r1")
    private String r1;
    
    @Column(name = "r2")
    private String r2;
    
    @Column(name = "r3")
    private String r3;
    
    @Column(name = "r4")
    private String r4;
    
    @Column(name = "r5")
    private String r5;
    
    @Column(name = "ro")
    private String ro;

    @Column(name = "resolucion_no")
    private Integer resolucionNo;
    
    @Column(name = "fecha_resolucion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaResolucion;

    @Column(name = "responsable")
    private String responsable;
    
    @Column(name = "certificado_emitido")
    private boolean certificadoEmitido;
    
    @Column(name = "notificacion_emitida")
    private boolean notificacionEmitida;
    
    @Column(name = "comprobante")
    private String comprobante;
    
    @Column(name = "tipo_estado")
    private String tipoEstado;
    
    @Column(name = "casillero_senadi_licenciatario")
    private String casilleroSenadiLicenciatario;
    
    @Transient
    private Integer idRenewalForm;
    
    @Column(name = "cancelado")
    private String cancelado;
    
    @Column(name = "vence_contrato")
    private String venceContrato;
    
    @Column(name = "clase_internacional")
    private String claseInternacional;
    
    @Column(name = "fecha_vencimiento")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaVencimiento;

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
     * @return the fechaContrato
     */
    public Date getFechaContrato() {
        return fechaContrato;
    }

    /**
     * @param fechaContrato the fechaContrato to set
     */
    public void setFechaContrato(Date fechaContrato) {
        this.fechaContrato = fechaContrato;
    }

    /**
     * @return the fechaVenceContrato
     */
    public Date getFechaVenceContrato() {
        return fechaVenceContrato;
    }

    /**
     * @param fechaVenceContrato the fechaVenceContrato to set
     */
    public void setFechaVenceContrato(Date fechaVenceContrato) {
        this.fechaVenceContrato = fechaVenceContrato;
    }

    /**
     * @return the licenciante
     */
    public String getLicenciante() {
        return licenciante;
    }

    /**
     * @param licenciante the licenciante to set
     */
    public void setLicenciante(String licenciante) {
        this.licenciante = licenciante;
    }

    /**
     * @return the licenciatario
     */
    public String getLicenciatario() {
        return licenciatario;
    }

    /**
     * @param licenciatario the licenciatario to set
     */
    public void setLicenciatario(String licenciatario) {
        this.licenciatario = licenciatario;
    }


    /**
     * @return the licenciaNo
     */
    public String getLicenciaNo() {
        return licenciaNo;
    }

    /**
     * @param licenciaNo the licenciaNo to set
     */
    public void setLicenciaNo(String licenciaNo) {
        this.licenciaNo = licenciaNo;
    }

    /**
     * @return the fechaLicencia
     */
    public Date getFechaLicencia() {
        return fechaLicencia;
    }

    /**
     * @param fechaLicencia the fechaLicencia to set
     */
    public void setFechaLicencia(Date fechaLicencia) {
        this.fechaLicencia = fechaLicencia;
    }

    /**
     * @return the fechaNotProvReg
     */
    public Date getFechaNotProvReg() {
        return fechaNotProvReg;
    }

    /**
     * @param fechaNotProvReg the fechaNotProvReg to set
     */
    public void setFechaNotProvReg(Date fechaNotProvReg) {
        this.fechaNotProvReg = fechaNotProvReg;
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
     * @return the t_pais
     */
    public String getT_pais() {
        return t_pais;
    }

    /**
     * @param t_pais the t_pais to set
     */
    public void setT_pais(String t_pais) {
        this.t_pais = t_pais;
    }

    /**
     * @return the t_ciudad
     */
    public String getT_ciudad() {
        return t_ciudad;
    }

    /**
     * @param t_ciudad the t_ciudad to set
     */
    public void setT_ciudad(String t_ciudad) {
        this.t_ciudad = t_ciudad;
    }

    /**
     * @return the tipoVigen
     */
    public String getTipoVigen() {
        return tipoVigen;
    }

    /**
     * @param tipoVigen the tipoVigen to set
     */
    public void setTipoVigen(String tipoVigen) {
        this.tipoVigen = tipoVigen;
    }

    /**
     * @return the vigen
     */
    public String getVigen() {
        return vigen;
    }

    /**
     * @param vigen the vigen to set
     */
    public void setVigen(String vigen) {
        this.vigen = vigen;
    }

    /**
     * @return the tiempo
     */
    public String getTiempo() {
        return tiempo;
    }

    /**
     * @param tiempo the tiempo to set
     */
    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    /**
     * @return the n_j
     */
    public String getN_j() {
        return n_j;
    }

    /**
     * @param n_j the n_j to set
     */
    public void setN_j(String n_j) {
        this.n_j = n_j;
    }

    /**
     * @return the notificacion
     */
    public String getNotificacion() {
        return notificacion;
    }

    /**
     * @param notificacion the notificacion to set
     */
    public void setNotificacion(String notificacion) {
        this.notificacion = notificacion;
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
     * @return the fechaNotiProvReg
     */
    public Date getFechaNotiProvReg() {
        return fechaNotiProvReg;
    }

    /**
     * @param fechaNotiProvReg the fechaNotiProvReg to set
     */
    public void setFechaNotiProvReg(Date fechaNotiProvReg) {
        this.fechaNotiProvReg = fechaNotiProvReg;
    }

    /**
     * @return the regContAnteriorNo
     */
    public String getRegContAnteriorNo() {
        return regContAnteriorNo;
    }

    /**
     * @param regContAnteriorNo the regContAnteriorNo to set
     */
    public void setRegContAnteriorNo(String regContAnteriorNo) {
        this.regContAnteriorNo = regContAnteriorNo;
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
     * @return the resolucionNo
     */
    public Integer getResolucionNo() {
        return resolucionNo;
    }

    /**
     * @param resolucionNo the resolucionNo to set
     */
    public void setResolucionNo(Integer resolucionNo) {
        this.resolucionNo = resolucionNo;
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
     * @return the certificadoEmitido
     */
    public boolean isCertificadoEmitido() {
        return certificadoEmitido;
    }

    /**
     * @param certificadoEmitido the certificadoEmitido to set
     */
    public void setCertificadoEmitido(boolean certificadoEmitido) {
        this.certificadoEmitido = certificadoEmitido;
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
     * @return the tipoEstado
     */
    public String getTipoEstado() {
        return tipoEstado;
    }

    /**
     * @param tipoEstado the tipoEstado to set
     */
    public void setTipoEstado(String tipoEstado) {
        this.tipoEstado = tipoEstado;
    }

    public String toString() {
        return getSolicitud() + ", " + getRegistro();
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
     * @return the apoderadoRepresentante
     */
    public String getApoderadoRepresentante() {
        return apoderadoRepresentante;
    }

    /**
     * @param apoderadoRepresentante the apoderadoRepresentante to set
     */
    public void setApoderadoRepresentante(String apoderadoRepresentante) {
        this.apoderadoRepresentante = apoderadoRepresentante;
    }

    /**
     * @return the casilleroSenadiLicenciatario
     */
    public String getCasilleroSenadiLicenciatario() {
        return casilleroSenadiLicenciatario;
    }

    /**
     * @param casilleroSenadiLicenciatario the casilleroSenadiLicenciatario to set
     */
    public void setCasilleroSenadiLicenciatario(String casilleroSenadiLicenciatario) {
        this.casilleroSenadiLicenciatario = casilleroSenadiLicenciatario;
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
     * @return the venceContrato
     */
    public String getVenceContrato() {
        return venceContrato;
    }

    /**
     * @param venceContrato the venceContrato to set
     */
    public void setVenceContrato(String venceContrato) {
        this.venceContrato = venceContrato;
    }

    /**
     * @return the claseInternacional
     */
    public String getClaseInternacional() {
        return claseInternacional;
    }

    /**
     * @param claseInternacional the claseInternacional to set
     */
    public void setClaseInternacional(String claseInternacional) {
        this.claseInternacional = claseInternacional;
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
}
