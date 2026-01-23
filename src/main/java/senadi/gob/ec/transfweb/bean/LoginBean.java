package senadi.gob.ec.transfweb.bean;

import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;
import senadi.gob.ec.transfweb.model.Abandono;
import senadi.gob.ec.transfweb.model.Caducada;
import senadi.gob.ec.transfweb.model.CambioCasillero;
import senadi.gob.ec.transfweb.model.Desistimiento;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.model.RazonCorreccion;
import senadi.gob.ec.transfweb.model.Transferencia;
import senadi.gob.ec.transfweb.model.Usuario;
import senadi.gob.ec.transfweb.model.cd.CambioDomicilio;
import senadi.gob.ec.transfweb.model.cn.CambioNombre;
import senadi.gob.ec.transfweb.model.licencia.LicenciaUso;
import senadi.gob.ec.transfweb.model.licencia.SubLicenciaUso;
import senadi.gob.ec.transfweb.model.prenda.PrendaComercial;
import senadi.gob.ec.transfweb.util.LDAP;

@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = -2152389656664659476L;
    private String nombre;
    private String clave;
    private boolean logeado = false;

//    private Usuario usuario;
    private boolean shake;

    private Transferencia transferenciaFlotante;
    private Notificacion notificacionFlotante;
    private Desistimiento desistimientoFlotante;

    private boolean various;

    private List<Transferencia> transferenciasFlotantes;
    private List<Notificacion> notificacionesFlotantes;
    private List<Desistimiento> desistimientosFlotantes;

    private CambioNombre cambioNombre;
    private List<CambioNombre> cambiosNombre;

    private CambioDomicilio cambioDomicilio;
    private List<CambioDomicilio> cambiosDomicilio;

    private PrendaComercial prendaComercial;
    private List<PrendaComercial> prendasComerciales;

    private LicenciaUso licencia;
    private List<LicenciaUso> licencias;

    private SubLicenciaUso sublicencia;
    private List<SubLicenciaUso> sublicencias;

    private Usuario usuario;

    private boolean lectura;
    private String grupoActivo;

    private boolean allInOne;

    private CambioCasillero cambioCasillero;
    
    private RazonCorreccion razon;
    
    private Abandono abandono;
    private List<Abandono> abandonos;
    
    private Caducada caducada;
    private List<Caducada> caducadas;
    
    private int tipoTramite;

//    private Employe employe;
//    private FullTimeCode fullTimeCode;
    public LoginBean() {
        shake = true;
        
    }

    public boolean estaLogeado() {
        return logeado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public void login(ActionEvent actionEvent) {
        FacesMessage msg = null;
        LDAP c = new LDAP();
        String grupo = "SC_Modificacion";
        String anexos = "SC_AnexosModificaciones";
        String tcocdi = "SC_CancelacionTitulos";
        int n = c.validarIngresoLDAPRestringido(nombre, clave, grupo);
//        int n = c.validarIngresoLDAPSinrestrinccion(nombre, clave) ? 1 : 0;
        if (n == 1) {

            grupoActivo = grupo;
            shake = false;
            logeado = true;
            lectura = false; //poner true en caso de querer editar en modo lectura

            Usuario user = c.getUsuarioByNick(nombre);
            if (user != null) {
                usuario = user;
            }
            PrimeFaces.current().ajax().addCallbackParam("estaLogeado", logeado);
            if (logeado) {

                PrimeFaces.current().ajax().addCallbackParam("view", "index.xhtml");
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenid@", nombre);

        } else {

            int an = c.validarIngresoLDAPRestringido(nombre, clave, anexos);
            if (an == 1) {
                System.out.println("Entra aquí");
                grupoActivo = anexos;
                shake = false;
                logeado = true;
                lectura = true;

                Usuario user = c.getUsuarioByNick(nombre);
                if (user != null) {
                    usuario = user;
                }
                PrimeFaces.current().ajax().addCallbackParam("estaLogeado", logeado);
                if (logeado) {

                    PrimeFaces.current().ajax().addCallbackParam("view", "uplocert.xhtml");
                }

                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Subir anexos - Bienvenid@", nombre);

            } else {

                int tc = c.validarIngresoLDAPRestringido(nombre, clave, tcocdi);
                if (tc == 1) {
                    System.out.println("Entra tcocdi");
                    grupoActivo = tcocdi;
                    shake = false;
                    logeado = true;
                    lectura = true;

                    Usuario user = c.getUsuarioByNick(nombre);
                    if (user != null) {
                        usuario = user;
                    }
                    PrimeFaces.current().ajax().addCallbackParam("estaLogeado", logeado);
                    if (logeado) {

                        PrimeFaces.current().ajax().addCallbackParam("view", "cancelacionti.xhtml");
                    }

                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cancelación Títulos - Bienvenid@", nombre);
                } else {
                    shake = true;
                    logeado = false;
                    if (n == -1) {
                        int lect = c.validarIngresoLDAPSinrestrinccion(nombre, clave) ? 1 : 0;
                        if (lect == 1) {

                            shake = false;
                            logeado = true;
                            lectura = true;

                            Usuario user = c.getUsuarioByNick(nombre);
                            if (user != null) {
                                usuario = user;
                            }
                            PrimeFaces.current().ajax().addCallbackParam("estaLogeado", logeado);
                            if (logeado) {

                                PrimeFaces.current().ajax().addCallbackParam("view", "index.xhtml");
                            }
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenid@", nombre + " - MODO LECTURA");

                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Credenciales Incorrectas");
                        }

                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Credenciales Incorrectas");
                    }
                }
            }
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void logout() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
        logeado = false;
        shake = false;
    }

    /**
     * @return the shake
     */
    public boolean isShake() {
        return shake;
    }

    /**
     * @param shake the shake to set
     */
    public void setShake(boolean shake) {
        this.shake = shake;
    }

    /**
     * @return the notificacionFlotante
     */
    public Notificacion getNotificacionFlotante() {
        return notificacionFlotante;
    }

    /**
     * @param notificacionFlotante the notificacionFlotante to set
     */
    public void setNotificacionFlotante(Notificacion notificacionFlotante) {
        this.notificacionFlotante = notificacionFlotante;
    }

    /**
     * @return the transferenciaFlotante
     */
    public Transferencia getTransferenciaFlotante() {
        return transferenciaFlotante;
    }

    /**
     * @param transferenciaFlotante the transferenciaFlotante to set
     */
    public void setTransferenciaFlotante(Transferencia transferenciaFlotante) {
        this.transferenciaFlotante = transferenciaFlotante;
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the lectura
     */
    public boolean isLectura() {
        return lectura;
    }

    /**
     * @param lectura the lectura to set
     */
    public void setLectura(boolean lectura) {
        this.lectura = lectura;
    }

    /**
     * @return the transferenciasFlotantes
     */
    public List<Transferencia> getTransferenciasFlotantes() {
        return transferenciasFlotantes;
    }

    /**
     * @param transferenciasFlotantes the transferenciasFlotantes to set
     */
    public void setTransferenciasFlotantes(List<Transferencia> transferenciasFlotantes) {
        this.transferenciasFlotantes = transferenciasFlotantes;
    }

    /**
     * @return the various
     */
    public boolean isVarious() {
        return various;
    }

    /**
     * @param various the various to set
     */
    public void setVarious(boolean various) {
        this.various = various;
    }

    /**
     * @return the notificacionesFlotantes
     */
    public List<Notificacion> getNotificacionesFlotantes() {
        return notificacionesFlotantes;
    }

    /**
     * @param notificacionesFlotantes the notificacionesFlotantes to set
     */
    public void setNotificacionesFlotantes(List<Notificacion> notificacionesFlotantes) {
        this.notificacionesFlotantes = notificacionesFlotantes;
    }

    /**
     * @return the allInOne
     */
    public boolean isAllInOne() {
        return allInOne;
    }

    /**
     * @param allInOne the allInOne to set
     */
    public void setAllInOne(boolean allInOne) {
        this.allInOne = allInOne;
    }

    /**
     * @return the desistimientoFlotante
     */
    public Desistimiento getDesistimientoFlotante() {
        return desistimientoFlotante;
    }

    /**
     * @param desistimientoFlotante the desistimientoFlotante to set
     */
    public void setDesistimientoFlotante(Desistimiento desistimientoFlotante) {
        this.desistimientoFlotante = desistimientoFlotante;
    }

    /**
     * @return the desistimientosFlotantes
     */
    public List<Desistimiento> getDesistimientosFlotantes() {
        return desistimientosFlotantes;
    }

    /**
     * @param desistimientosFlotantes the desistimientosFlotantes to set
     */
    public void setDesistimientosFlotantes(List<Desistimiento> desistimientosFlotantes) {
        this.desistimientosFlotantes = desistimientosFlotantes;
    }

    /**
     * @return the cambioNombre
     */
    public CambioNombre getCambioNombre() {
        return cambioNombre;
    }

    /**
     * @param cambioNombre the cambioNombre to set
     */
    public void setCambioNombre(CambioNombre cambioNombre) {
        this.cambioNombre = cambioNombre;
    }

    /**
     * @return the cambiosNombre
     */
    public List<CambioNombre> getCambiosNombre() {
        return cambiosNombre;
    }

    /**
     * @param cambiosNombre the cambiosNombre to set
     */
    public void setCambiosNombre(List<CambioNombre> cambiosNombre) {
        this.cambiosNombre = cambiosNombre;
    }

    /**
     * @return the cambioDomicilio
     */
    public CambioDomicilio getCambioDomicilio() {
        return cambioDomicilio;
    }

    /**
     * @param cambioDomicilio the cambioDomicilio to set
     */
    public void setCambioDomicilio(CambioDomicilio cambioDomicilio) {
        this.cambioDomicilio = cambioDomicilio;
    }

    /**
     * @return the cambiosDomicilio
     */
    public List<CambioDomicilio> getCambiosDomicilio() {
        return cambiosDomicilio;
    }

    /**
     * @param cambiosDomicilio the cambiosDomicilio to set
     */
    public void setCambiosDomicilio(List<CambioDomicilio> cambiosDomicilio) {
        this.cambiosDomicilio = cambiosDomicilio;
    }

    /**
     * @return the grupoActivo
     */
    public String getGrupoActivo() {
        return grupoActivo;
    }

    /**
     * @param grupoActivo the grupoActivo to set
     */
    public void setGrupoActivo(String grupoActivo) {
        this.grupoActivo = grupoActivo;
    }

    /**
     * @return the prendaComercial
     */
    public PrendaComercial getPrendaComercial() {
        return prendaComercial;
    }

    /**
     * @param prendaComercial the prendaComercial to set
     */
    public void setPrendaComercial(PrendaComercial prendaComercial) {
        this.prendaComercial = prendaComercial;
    }

    /**
     * @return the prendasComerciales
     */
    public List<PrendaComercial> getPrendasComerciales() {
        return prendasComerciales;
    }

    /**
     * @param prendasComerciales the prendasComerciales to set
     */
    public void setPrendasComerciales(List<PrendaComercial> prendasComerciales) {
        this.prendasComerciales = prendasComerciales;
    }

    /**
     * @return the licencia
     */
    public LicenciaUso getLicencia() {
        return licencia;
    }

    /**
     * @param licencia the licencia to set
     */
    public void setLicencia(LicenciaUso licencia) {
        this.licencia = licencia;
    }

    /**
     * @return the licencias
     */
    public List<LicenciaUso> getLicencias() {
        return licencias;
    }

    /**
     * @param licencias the licencias to set
     */
    public void setLicencias(List<LicenciaUso> licencias) {
        this.licencias = licencias;
    }

    /**
     * @return the sublicencia
     */
    public SubLicenciaUso getSublicencia() {
        return sublicencia;
    }

    /**
     * @param sublicencia the sublicencia to set
     */
    public void setSublicencia(SubLicenciaUso sublicencia) {
        this.sublicencia = sublicencia;
    }

    /**
     * @return the sublicencias
     */
    public List<SubLicenciaUso> getSublicencias() {
        return sublicencias;
    }

    /**
     * @param sublicencias the sublicencias to set
     */
    public void setSublicencias(List<SubLicenciaUso> sublicencias) {
        this.sublicencias = sublicencias;
    }

    /**
     * @return the cambioCasillero
     */
    public CambioCasillero getCambioCasillero() {
        return cambioCasillero;
    }

    /**
     * @param cambioCasillero the cambioCasillero to set
     */
    public void setCambioCasillero(CambioCasillero cambioCasillero) {
        this.cambioCasillero = cambioCasillero;
    }

    /**
     * @return the razon
     */
    public RazonCorreccion getRazon() {
        return razon;
    }

    /**
     * @param razon the razon to set
     */
    public void setRazon(RazonCorreccion razon) {
        this.razon = razon;
    }

    /**
     * @return the abandono
     */
    public Abandono getAbandono() {
        return abandono;
    }

    /**
     * @param abandono the abandono to set
     */
    public void setAbandono(Abandono abandono) {
        this.abandono = abandono;
    }

    /**
     * @return the abandonos
     */
    public List<Abandono> getAbandonos() {
        return abandonos;
    }

    /**
     * @param abandonos the abandonos to set
     */
    public void setAbandonos(List<Abandono> abandonos) {
        this.abandonos = abandonos;
    }

    /**
     * @return the caducada
     */
    public Caducada getCaducada() {
        return caducada;
    }

    /**
     * @param caducada the caducada to set
     */
    public void setCaducada(Caducada caducada) {
        this.caducada = caducada;
    }

    /**
     * @return the caducadas
     */
    public List<Caducada> getCaducadas() {
        return caducadas;
    }

    /**
     * @param caducadas the caducadas to set
     */
    public void setCaducadas(List<Caducada> caducadas) {
        this.caducadas = caducadas;
    }

    /**
     * @return the tipoTramite
     */
    public int getTipoTramite() {
        return tipoTramite;
    }

    /**
     * @param tipoTramite the tipoTramite to set
     */
    public void setTipoTramite(int tipoTramite) {
        this.tipoTramite = tipoTramite;
    }
}
