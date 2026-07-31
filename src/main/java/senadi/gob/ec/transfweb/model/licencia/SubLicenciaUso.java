
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.model.licencia;

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
@Table(name = "sublicencia_uso")
public class SubLicenciaUso implements Serializable {

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
    
    @Column(name = "fecha_vence_contrato")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaVenceContrato;
    
    @Column(name = "sublicenciante")
    private String sublicenciante;
    
    @Column(name = "sublicenciatario")
    private String sublicenciatario;
    
    @Column(name = "apoderado_representante_legal")
    private String apoderadoRepresentante;
    
    @Column(name = "sublicencia_no")
    private String sublicenciaNo;
    
    @Column(name = "fecha_sublicencia")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaSublicencia;
    
    @Column(name = "fecha_not_sublicencia")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNotSublicencia;
    
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
    
    @Column(name = "reg_cont_anterior_no")
    private String regContAnteriorNo;
    
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
    
    @Column(name = "abogado_patrocinador")
    private String abogadoPatrocinador;
    
    @Transient
    private Integer idRenewalForm;
    
    @Column(name = "cancelado")
    private String cancelado;
    
    @Column(name = "vence_contrato")
    private String venceContrato;
    
    @Column(name = "numero_abandono")
    private Integer numeroAbandono;
    
    @Column(name = "fecha_abandono")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaAbandono;
    
    @Column(name = "solicitante")
    private String solicitante;
    
    @Column(name = "fecha_vencimiento")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaVencimiento;
    
    @Column(name = "tipo_abandono")
    private String tipoAbandono;
    
    @Column(name = "fecha_puesta_abandono")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaPuestaAbandono;
    
    @Column(name = "abandono_notificado")
    private boolean abandonoNotificado;

    @Column(name = "fecha_puesta_prorroga")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaPuestaProrroga;

    @Column(name = "fecha_prorroga")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaProrroga;

    @Column(name = "prorroga_notificada")
    private Boolean prorrogaNotificada;

    @Column(name = "dias_prorroga")
    private Integer diasProrroga;

    @Column(name = "numero_prorroga")
    private Integer numeroProrroga;

    @Column(name = "numero_alcance")
    private String numeroAlcance;

    @Column(name = "fecha_alcance")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaAlcance;

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
     * @return the sublicenciante
     */
    public String getSublicenciante() {
        return sublicenciante;
    }

    /**
     * @param sublicenciante the sublicenciante to set
     */
    public void setSublicenciante(String sublicenciante) {
        this.sublicenciante = sublicenciante;
    }

    /**
     * @return the sublicenciatario
     */
    public String getSublicenciatario() {
        return sublicenciatario;
    }

    /**
     * @param sublicenciatario the sublicenciatario to set
     */
    public void setSublicenciatario(String sublicenciatario) {
        this.sublicenciatario = sublicenciatario;
    }


    /**
     * @return the sublicenciaNo
     */
    public String getSublicenciaNo() {
        return sublicenciaNo;
    }

    /**
     * @param sublicenciaNo the sublicenciaNo to set
     */
    public void setSublicenciaNo(String sublicenciaNo) {
        this.sublicenciaNo = sublicenciaNo;
    }

    /**
     * @return the fechaSublicencia
     */
    public Date getFechaSublicencia() {
        return fechaSublicencia;
    }

    /**
     * @param fechaSublicencia the fechaSublicencia to set
     */
    public void setFechaSublicencia(Date fechaSublicencia) {
        this.fechaSublicencia = fechaSublicencia;
    }

    /**
     * @return the fechaNotSublicencia
     */
    public Date getFechaNotSublicencia() {
        return fechaNotSublicencia;
    }

    /**
     * @param fechaNotSublicencia the fechaNotSublicencia to set
     */
    public void setFechaNotSublicencia(Date fechaNotSublicencia) {
        this.fechaNotSublicencia = fechaNotSublicencia;
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
     * @return the numeroAbandono
     */
    public Integer getNumeroAbandono() {
        return numeroAbandono;
    }

    /**
     * @param numeroAbandono the numeroAbandono to set
     */
    public void setNumeroAbandono(Integer numeroAbandono) {
        this.numeroAbandono = numeroAbandono;
    }

    /**
     * @return the fechaAbandono
     */
    public Date getFechaAbandono() {
        return fechaAbandono;
    }

    /**
     * @param fechaAbandono the fechaAbandono to set
     */
    public void setFechaAbandono(Date fechaAbandono) {
        this.fechaAbandono = fechaAbandono;
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
     * @return the tipoAbandono
     */
    public String getTipoAbandono() {
        return tipoAbandono;
    }

    /**
     * @param tipoAbandono the tipoAbandono to set
     */
    public void setTipoAbandono(String tipoAbandono) {
        this.tipoAbandono = tipoAbandono;
    }

    /**
     * @return the fechaPuestaAbandono
     */
    public Date getFechaPuestaAbandono() {
        return fechaPuestaAbandono;
    }

    /**
     * @param fechaPuestaAbandono the fechaPuestaAbandono to set
     */
    public void setFechaPuestaAbandono(Date fechaPuestaAbandono) {
        this.fechaPuestaAbandono = fechaPuestaAbandono;
    }

    /**
     * @return the abandonoNotificado
     */
    public boolean isAbandonoNotificado() {
        return abandonoNotificado;
    }

    /**
     * @param abandonoNotificado the abandonoNotificado to set
     */
    public void setAbandonoNotificado(boolean abandonoNotificado) {
        this.abandonoNotificado = abandonoNotificado;
    }

    public Date getFechaPuestaProrroga() {
        return fechaPuestaProrroga;
    }

    public void setFechaPuestaProrroga(Date fechaPuestaProrroga) {
        this.fechaPuestaProrroga = fechaPuestaProrroga;
    }

    public Date getFechaProrroga() {
        return fechaProrroga;
    }

    public void setFechaProrroga(Date fechaProrroga) {
        this.fechaProrroga = fechaProrroga;
    }

    public Boolean getProrrogaNotificada() {
        return prorrogaNotificada;
    }

    public void setProrrogaNotificada(Boolean prorrogaNotificada) {
        this.prorrogaNotificada = prorrogaNotificada;
    }

    public Integer getDiasProrroga() {
        return diasProrroga;
    }

    public void setDiasProrroga(Integer diasProrroga) {
        this.diasProrroga = diasProrroga;
    }

    public Integer getNumeroProrroga() {
        return numeroProrroga;
    }

    public void setNumeroProrroga(Integer numeroProrroga) {
        this.numeroProrroga = numeroProrroga;
    }

    public String getNumeroAlcance() {
        return numeroAlcance;
    }

    public void setNumeroAlcance(String numeroAlcance) {
        this.numeroAlcance = numeroAlcance;
    }

    public Date getFechaAlcance() {
        return fechaAlcance;
    }

    public void setFechaAlcance(Date fechaAlcance) {
        this.fechaAlcance = fechaAlcance;
    }
}

