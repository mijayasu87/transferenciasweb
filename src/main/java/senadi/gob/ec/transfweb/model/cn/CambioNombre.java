/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.model.cn;

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
@Table(name = "cambio_nombre")
public class CambioNombre implements Serializable {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "solicitud")
    private String solicitud;

    @Column(name = "fecha_presentacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaPresentacion;

    @Column(name = "certificado")
    private Integer certificado;

    @Column(name = "fecha_certificado")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaCertificado;

    @Column(name = "registro")
    private String registro;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaRegistro;

    @Column(name = "denominacion")
    private String denominacion;

    @Column(name = "signo")
    private String signo;

    @Column(name = "titular_anterior")
    private String titularAnterior;

    @Column(name = "titular_actual")
    private String titularActual;

    @Column(name = "apoderado_representante_legal")
    private String nomApodRepre;

    @Column(name = "casillero_senadi")
    private String casilleroSenadi;

    @Column(name = "casillero_judicial")
    private String casilleroJudicial;

    @Column(name = "responsable")
    private String responsable;

    @Column(name = "observacion")
    private String observacion;

    @Column(name = "identificacion")
    private String identificacion;

    @Column(name = "email")
    private String email;

    @Column(name = "comprobante")
    private String comprobante;

    @Column(name = "fecha_vence_registro")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaVenceRegistro;

    @Column(name = "tac_n_j")
    private String tac_n_j;

    @Column(name = "ape_apod_repre")
    private String apeApodRepre;

    @Column(name = "ro")
    private String ro;

    @Column(name = "domicilio_titular_actual")
    private String domicilioTitularActual;

    @Column(name = "tipo_estado")
    private String tipoEstado;

    @Column(name = "notificacion")
    private Integer notificacion;

    @Column(name = "fecha_notificacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNotificacion;
    
    @Column(name = "res_desistida")
    private Integer resolucionDesistida;
    
    @Column(name = "fecha_resdesist")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaResolucionDesistida;

    @Column(name = "res_caducada")
    private Integer resolucionCaducada;
    
    @Column(name = "fecha_rescaducada")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaResolucionCaducada;
    
    @Transient
    private Integer idRenewalForm;

    @Transient
    private String rutaExpediente;
    
    @Column(name = "certificado_emitido")
    private boolean certificadoEmitido;
    
    @Column(name = "notificacion_emitida")
    private boolean notificacionEmitida;
    
    @Column(name = "cancelado")
    private String cancelado;
    
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
     * @return the certificado
     */
    public Integer getCertificado() {
        return certificado;
    }

    /**
     * @param certificado the certificado to set
     */
    public void setCertificado(Integer certificado) {
        this.certificado = certificado;
    }

    /**
     * @return the fechaCertificado
     */
    public Date getFechaCertificado() {
        return fechaCertificado;
    }

    /**
     * @param fechaCertificado the fechaCertificado to set
     */
    public void setFechaCertificado(Date fechaCertificado) {
        this.fechaCertificado = fechaCertificado;
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
     * @return the nomApodRepre
     */
    public String getNomApodRepre() {
        return nomApodRepre;
    }

    /**
     * @param nomApodRepre the nomApodRepre to set
     */
    public void setNomApodRepre(String nomApodRepre) {
        this.nomApodRepre = nomApodRepre;
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
     * @return the tac_n_j
     */
    public String getTac_n_j() {
        return tac_n_j;
    }

    /**
     * @param tac_n_j the tac_n_j to set
     */
    public void setTac_n_j(String tac_n_j) {
        this.tac_n_j = tac_n_j;
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

    /**
     * @return the fechaVenceRegistro
     */
    public Date getFechaVenceRegistro() {
        return fechaVenceRegistro;
    }

    /**
     * @param fechaVenceRegistro the fechaVenceRegistro to set
     */
    public void setFechaVenceRegistro(Date fechaVenceRegistro) {
        this.fechaVenceRegistro = fechaVenceRegistro;
    }

    /**
     * @return the domicilioTitularActual
     */
    public String getDomicilioTitularActual() {
        return domicilioTitularActual;
    }

    /**
     * @param domicilioTitularActual the domicilioTitularActual to set
     */
    public void setDomicilioTitularActual(String domicilioTitularActual) {
        this.domicilioTitularActual = domicilioTitularActual;
    }

    /**
     * @return the notificacion
     */
    public Integer getNotificacion() {
        return notificacion;
    }

    /**
     * @param notificacion the notificacion to set
     */
    public void setNotificacion(Integer notificacion) {
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
     * @return the apeApodRepre
     */
    public String getApeApodRepre() {
        return apeApodRepre;
    }

    /**
     * @param apeApodRepre the apeApodRepre to set
     */
    public void setApeApodRepre(String apeApodRepre) {
        this.apeApodRepre = apeApodRepre;
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
     * @return the resolucionDesistida
     */
    public Integer getResolucionDesistida() {
        return resolucionDesistida;
    }

    /**
     * @param resolucionDesistida the resolucionDesistida to set
     */
    public void setResolucionDesistida(Integer resolucionDesistida) {
        this.resolucionDesistida = resolucionDesistida;
    }

    /**
     * @return the fechaResolucionDesistida
     */
    public Date getFechaResolucionDesistida() {
        return fechaResolucionDesistida;
    }

    /**
     * @param fechaResolucionDesistida the fechaResolucionDesistida to set
     */
    public void setFechaResolucionDesistida(Date fechaResolucionDesistida) {
        this.fechaResolucionDesistida = fechaResolucionDesistida;
    }

    /**
     * @return the resolucionCaducada
     */
    public Integer getResolucionCaducada() {
        return resolucionCaducada;
    }

    /**
     * @param resolucionCaducada the resolucionCaducada to set
     */
    public void setResolucionCaducada(Integer resolucionCaducada) {
        this.resolucionCaducada = resolucionCaducada;
    }

    /**
     * @return the fechaResolucionCaducada
     */
    public Date getFechaResolucionCaducada() {
        return fechaResolucionCaducada;
    }

    /**
     * @param fechaResolucionCaducada the fechaResolucionCaducada to set
     */
    public void setFechaResolucionCaducada(Date fechaResolucionCaducada) {
        this.fechaResolucionCaducada = fechaResolucionCaducada;
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
