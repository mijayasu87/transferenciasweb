/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.model.prenda;

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
@Table(name = "prenda_comercial")
public class PrendaComercial implements Serializable {

    @Id
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "solicitud")
    private String solicitud;
    
    @Column(name = "fecha_presentacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaPresentacion;
    
    @Column(name = "prenda_no")
    private Integer prendaNo;
    
    @Column(name = "fecha_prenda")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaPrenda;
    
    @Column(name = "registro")
    private String registro;
    
    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaRegistro;
    
    @Column(name = "fecha_vence_registro")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaVenceRegistro;
    
    @Column(name = "denominacion")
    private String denominacion;
    
    @Column(name = "signo")
    private String signo;
    
    @Column(name = "deudora_prendaria")
    private String deudoraPrendaria;
    
    @Column(name = "prendaria_acreedora")
    private String prendariaAcreedora;
    
    @Column(name = "domicilio_titular_actual")
    private String domicilioTitularActual;
    
    @Column(name = "apoderado_representante_legal")
    private String titApodRepre;
    
    @Column(name = "notificada")
    private String notificada;
    
    @Column(name = "casillero_senadi")
    private String casilleroSenadi;
    
    @Column(name = "casillero_judicial")
    private String casilleroJudicial;
    
    @Column(name = "responsable")
    private String responsable;
    
    @Column(name = "identificacion")
    private String identificacion;
    
    @Column(name = "notificacion")
    private Integer notificacion;
    
    @Column(name = "fecha_certificado")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaCertificado;
    
    @Column(name = "fecha_notificacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNotificacion;
    
    @Column(name = "fecha_escrito")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaEscrito;
    
    @Column(name = "titular_anterior")
    private String titularAnterior;
    
    @Column(name = "titular_actual")
    private String titularActual;
    
    @Column(name = "fecha_notificacion_anterior")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNotificacionAnterior;
    
    @Column(name = "r1")
    private String r1;
    
    @Column(name = "r2")
    private String r2;
    
    @Column(name = "levantamiento_prenda_no")
    private Integer levantamientoPrendaNo;
    
    @Column(name = "resolucion_no")
    private Integer resolucionNo;
    
    @Column(name = "fecha_resolucion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaResolucion;
    
    @Column(name = "abogado_patrocinador")
    private String abogadoPatrocinador;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "ro")
    private String ro;
    
    @Column(name = "fecha_contrato")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaContrato;
    
    @Column(name = "fecha_vencimiento_contrato")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaVencimientoContrato;
    
    @Column(name = "licenciante")
    private String licenciante;
    
    @Column(name = "licenciatario")
    private String licenciatario;
    
    @Column(name = "apellido_a_r")
    private String apellido_a_r;
    
    @Column(name = "prov_reg_no")
    private String prov_reg_no;
    
    @Column(name = "fecha_not_prov_reg")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNotProvReg;
    
    @Column(name = "certificado_emitido")
    private boolean certificadoEmitido;
    
    @Column(name = "notificacion_emitida")
    private boolean notificacionEmitida;
    
    @Column(name = "comprobante")
    private String comprobante;
    
    @Column(name = "tipo_estado")
    private String tipoEstado;
    
    @Column(name = "casillero_senadi_acreedor")
    private String casilleroSenadiAcreedor;
    
    @Column(name = "cancelado")
    private String cancelado;
    
    @Transient
    private Integer idRenewalForm;

    @Transient
    private String rutaExpediente;
    
    @Transient
    private boolean adenda;
    
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
    
    @Column(name = "res_caducada")
    private Integer resolucionCaducada;
    
    @Column(name = "fecha_rescaducada")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaResolucionCaducada;
    
    @Column(name = "observacion")
    private String observacion;

    @Column(name = "abandono_notificado")
    private boolean abandonoNotificado;
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
     * @return the prendaNo
     */
    public Integer getPrendaNo() {
        return prendaNo;
    }

    /**
     * @param prendaNo the prendaNo to set
     */
    public void setPrendaNo(Integer prendaNo) {
        this.prendaNo = prendaNo;
    }

    /**
     * @return the fechaPrenda
     */
    public Date getFechaPrenda() {
        return fechaPrenda;
    }

    /**
     * @param fechaPrenda the fechaPrenda to set
     */
    public void setFechaPrenda(Date fechaPrenda) {
        this.fechaPrenda = fechaPrenda;
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
     * @return the deudoraPrendaria
     */
    public String getDeudoraPrendaria() {
        return deudoraPrendaria;
    }

    /**
     * @param deudoraPrendaria the deudoraPrendaria to set
     */
    public void setDeudoraPrendaria(String deudoraPrendaria) {
        this.deudoraPrendaria = deudoraPrendaria;
    }

    /**
     * @return the prendariaAcreedora
     */
    public String getPrendariaAcreedora() {
        return prendariaAcreedora;
    }

    /**
     * @param prendariaAcreedora the prendariaAcreedora to set
     */
    public void setPrendariaAcreedora(String prendariaAcreedora) {
        this.prendariaAcreedora = prendariaAcreedora;
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
     * @return the titApodRepre
     */
    public String getTitApodRepre() {
        return titApodRepre;
    }

    /**
     * @param titApodRepre the titApodRepre to set
     */
    public void setTitApodRepre(String titApodRepre) {
        this.titApodRepre = titApodRepre;
    }

    /**
     * @return the notificada
     */
    public String getNotificada() {
        return notificada;
    }

    /**
     * @param notificada the notificada to set
     */
    public void setNotificada(String notificada) {
        this.notificada = notificada;
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
     * @return the fechaEscrito
     */
    public Date getFechaEscrito() {
        return fechaEscrito;
    }

    /**
     * @param fechaEscrito the fechaEscrito to set
     */
    public void setFechaEscrito(Date fechaEscrito) {
        this.fechaEscrito = fechaEscrito;
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
     * @return the fechaNotificacionAnterior
     */
    public Date getFechaNotificacionAnterior() {
        return fechaNotificacionAnterior;
    }

    /**
     * @param fechaNotificacionAnterior the fechaNotificacionAnterior to set
     */
    public void setFechaNotificacionAnterior(Date fechaNotificacionAnterior) {
        this.fechaNotificacionAnterior = fechaNotificacionAnterior;
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
     * @return the levantamientoPrendaNo
     */
    public Integer getLevantamientoPrendaNo() {
        return levantamientoPrendaNo;
    }

    /**
     * @param levantamientoPrendaNo the levantamientoPrendaNo to set
     */
    public void setLevantamientoPrendaNo(Integer levantamientoPrendaNo) {
        this.levantamientoPrendaNo = levantamientoPrendaNo;
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
     * @return the fechaVencimientoContrato
     */
    public Date getFechaVencimientoContrato() {
        return fechaVencimientoContrato;
    }

    /**
     * @param fechaVencimientoContrato the fechaVencimientoContrato to set
     */
    public void setFechaVencimientoContrato(Date fechaVencimientoContrato) {
        this.fechaVencimientoContrato = fechaVencimientoContrato;
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
     * @return the apellido_a_r
     */
    public String getApellido_a_r() {
        return apellido_a_r;
    }

    /**
     * @param apellido_a_r the apellido_a_r to set
     */
    public void setApellido_a_r(String apellido_a_r) {
        this.apellido_a_r = apellido_a_r;
    }

    /**
     * @return the prov_reg_no
     */
    public String getProv_reg_no() {
        return prov_reg_no;
    }

    /**
     * @param prov_reg_no the prov_reg_no to set
     */
    public void setProv_reg_no(String prov_reg_no) {
        this.prov_reg_no = prov_reg_no;
    }

    /**
     * @return the certificadoEmitido
     */
    public boolean getCertificadoEmitido() {
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
    public boolean getNotificacionEmitida() {
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
        return rutaExpediente;
    }

    /**
     * @param rutaExpediente the rutaExpediente to set
     */
    public void setRutaExpediente(String rutaExpediente) {
        this.rutaExpediente = rutaExpediente;
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
     * @return the casilleroSenadiAcreedor
     */
    public String getCasilleroSenadiAcreedor() {
        return casilleroSenadiAcreedor;
    }

    /**
     * @param casilleroSenadiAcreedor the casilleroSenadiAcreedor to set
     */
    public void setCasilleroSenadiAcreedor(String casilleroSenadiAcreedor) {
        this.casilleroSenadiAcreedor = casilleroSenadiAcreedor;
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
     * @return the adenda
     */
    public boolean isAdenda() {
        return adenda;
    }

    /**
     * @param adenda the adenda to set
     */
    public void setAdenda(boolean adenda) {
        this.adenda = adenda;
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
     * @param resolucionCaducada the resolucionCaducada to set
     */
    public void setResolucionCaducada(Integer resolucionCaducada) {
        this.resolucionCaducada = resolucionCaducada;
    }

    /**
     * @return the resolucionCaducada
     */
    public Integer getResolucionCaducada() {
        return resolucionCaducada;
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

}
