/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.PrimeFaces;
import org.primefaces.component.api.UIData;
import org.primefaces.model.file.UploadedFile;
import senadi.gob.ec.transfweb.model.Caducada;
import senadi.gob.ec.transfweb.model.CambioCasillero;
import senadi.gob.ec.transfweb.model.Desistimiento;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.model.Transferencia;
import senadi.gob.ec.transfweb.model.cd.CambioDomicilio;
import senadi.gob.ec.transfweb.model.cn.CambioNombre;
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepform.RenewalForm;
import senadi.gob.ec.transfweb.model.iepicas.Owner;
import senadi.gob.ec.transfweb.model.licencia.LicenciaUso;
import senadi.gob.ec.transfweb.model.licencia.SubLicenciaUso;
import senadi.gob.ec.transfweb.model.prenda.PrendaComercial;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudModificacion;
import senadi.gob.ec.transfweb.modelp.PpdiSolicitudSignoDistintivo;
import senadi.gob.ec.transfweb.util.Codekru;
import senadi.gob.ec.transfweb.util.Controlador;
import senadi.gob.ec.transfweb.util.Mailer;

/**
 *
 * @author micharesp
 */
@ManagedBean(name = "cambioCasilleroBean")
@ViewScoped
public class CambioCasilleroBean implements Serializable {

    private String criterioOwner;
    private List<Owner> owners;
    private List<Owner> ownersFiltrados;
    private Owner owner;
    private UIData ownerDataTable;

    private RenewalForm renewal;
    private boolean cambioc;
    private String confirmChangeMessage;

    private LoginBean login;
    private CambioCasillero cambioCasillero;
    private UploadedFile file;

    private String numtramite;

    private List<CambioCasillero> cambiosCasillero;
    private List<CambioCasillero> cambiosCasilleroFiltrados;
    private UIData cambiosCasilleroDataTable;

    private boolean busqueda;

    public CambioCasilleroBean() {
        loadCambiosCasillero();

    }

    private void loadCambiosCasillero() {
        Controlador c = new Controlador();
        confirmChangeMessage = "";
        login = c.getLogin();
        cambiosCasillero = c.getCambioCasilleroByEstado("REALIZADO");
        busqueda = false;
    }

    public void prepararCambioCasillero(ActionEvent ae) {
        FacesMessage message = null;
        setNumtramite("");
        renewal = new RenewalForm();
        cambioCasillero = new CambioCasillero();
        cambioc = false;
        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CAMBIO DE CASILLERO INICIADO");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void buscarTramite(ActionEvent ae) {
        System.out.println("Si llegamos 1");
        FacesMessage message = null;
        if (numtramite != null && !numtramite.trim().isEmpty()) {
            Controlador c = new Controlador();
            renewal = c.getRenewalFormsByApplicationNumber(numtramite);
            if (renewal.getId() != null) {

                Owner ownaux = c.getOwnersById(renewal.getOwnerId());

                cambioCasillero = new CambioCasillero();

                if (renewal.getDebugId() != null && renewal.getDebugId() != 0) {
                    HallmarkForms hf = c.getHallmarkFormDepurada(renewal.getDebugId());
                    if (hf.getId() != null) {
                        cambioCasillero.setDenominacion(hf.getDenomination());
                    }
                } else {
                    if (renewal.getExpedient() != null && !renewal.getExpedient().trim().isEmpty()) {
                        PpdiSolicitudSignoDistintivo ps = c.getPpdiSolicitudSignoDistintivoByExpedient(renewal.getExpedient());
                        if (ps.getCodigoSolicitudSigno() != null) {
                            cambioCasillero.setDenominacion(ps.getDenominacionSigno());
                        }
                    }
                }

                cambioCasillero.setTramite(numtramite.toUpperCase());
                cambioCasillero.setTipoModificacion(c.getTipoModificacion(cambioCasillero.getTramite()));
                cambioCasillero.setTitularCasilleroAnterior(ownaux.getFirstname() + " " + ownaux.getLastname());
                cambioCasillero.setCasilleroAnterior(ownaux.getCasillero());
                cambioCasillero.setCorreoTitularAnterior(ownaux.getEmail());

                busqueda = true;

                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "TRÁMITE ENCONTRADO");
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ EL TRÁMITE: " + numtramite);
            }

        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN NÚMERO DE TRÁMITE VÁLIDO");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void prepararBusquedaCasillero(ActionEvent ae) {
        FacesMessage msg = null;
        if (cambioCasillero != null) {
            if (cambioCasillero.getDenominacion() != null && !cambioCasillero.getDenominacion().trim().isEmpty()) {
                owners = new ArrayList<>();
                criterioOwner = "";
                PrimeFaces.current().ajax().addCallbackParam("casit", true);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "SELECCIONE EL NUEVO CASILLERO");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UNA DENOMINACIÓN VÁLIDA");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "DE INGRESAR U NNÚMERO DE TRÁMITE VÁLIDO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void viewProvidenciaCambioCasillero(ActionEvent ae) {
        FacesMessage msg = null;
        if (cambioCasillero != null && cambioCasillero.getCasilleroAnterior() != null && !cambioCasillero.getCasilleroAnterior().trim().isEmpty()) {
            if (cambioCasillero.getNuevoCasillero() != null && !cambioCasillero.getNuevoCasillero().trim().isEmpty()) {
                Controlador c = new Controlador();

                if (!c.validarDelegadoActivo("delegado")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGÚN DELEGADO ACTIVO, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarDelegacionActivo()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA DELEGACIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else if (!c.validarResolucionActiva("transferencia")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "INFORMACIÓN", "NO HAY NINGUNA RESOLUCIÓN ACTIVA, POR FAVOR INGRESE A CONFIGURACIÓN");
                } else {
                    if (cambioCasillero.getNuevoCasillero().equals(cambioCasillero.getCasilleroAnterior())) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL CASILLERO NUEVO DEBE SER DIFERENTE AL CASILLERO ANTERIOR");
                    } else {
                        if (cambioCasillero.getId() == null) {
                            cambioCasillero.setFechaNotificacion(new Date());
                            cambioCasillero.setFechaProvidencia(new Date());
                            cambioCasillero.setProvidencia(c.nextProvidenciaCambioCasillero());
                            cambioCasillero.setEstado("PENDIENTE");
                            cambioCasillero.setUsuario(login.getNombre());
                            cambioCasillero.setTramite(cambioCasillero.getTramite().trim().toUpperCase());

                            if (cambioCasillero.getDenominacion() == null || cambioCasillero.getDenominacion().trim().isEmpty()) {

                                cambioCasillero.setDenominacion("-denominación no encontrada-");
                            }
                            cambioCasillero.setFuente("MODIFICACIONES");
                            if (c.saveCambioCasillero(cambioCasillero)) {
                                cambioCasillero = c.getCambioCasilleroWhenNotId(cambioCasillero);
                                System.out.println("CambioCasillero: " + cambioCasillero.getProvidencia());
                            }
                        }

                        login.setCambioCasillero(cambioCasillero);

                        PrimeFaces.current().ajax().addCallbackParam("doit", true);

                        System.out.println("envía providencia cambio casillero descargar");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "DESCARGANDO PROVIDENCIA CAMBIO CASILLERO " + cambioCasillero.getProvidencia());
                    }
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "DEBE SELECCIONAR UN CASILLERO A ASIGNAR VÁLIDO");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "CARGE EL TRÁMITE A CAMBIAR EL CASILLERO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void limpiarDatos() {
        cambioCasillero = new CambioCasillero();
        renewal = new RenewalForm();
        owner = new Owner();
        Controlador c = new Controlador();
        cambiosCasillero = c.getCambioCasilleroByEstado("REALIZADO");
        busqueda = false;
    }

    public void cambiarCasillero() throws IOException {
        FacesMessage msg = null;
        if (renewal.getId() != null) {
            if (cambioCasillero.getId() != null) {
                Controlador c = new Controlador();
                if (!cambioCasillero.getCasilleroAnterior().equals(cambioCasillero.getNuevoCasillero())) {
                    int n = 0;
                    try {
                        n = Integer.parseInt(cambioCasillero.getNuevoCasillero());
                    } catch (NumberFormatException ex) {
                        n = 0;
                    }
                    if (c.existsOwner(n)) {
                        if (file != null) {
                            if (file.getInputStream() != null) {
                                if (uploadDocumentoToExpedient(file, renewal.getId())) {
                                    if (sendMail(cambioCasillero, file)) {

                                        renewal.setOwnerId(owner.getId());
                                        //renewal.setOwnerId(Integer.valueOf(cambioCasillero.getNuevoCasillero()));
                                        if (c.updateRenewalForm(renewal)) {

                                            PpdiSolicitudModificacion modificacion = c.getPpdiSolicitudModificacionByNumTramite(renewal.getApplicationNumber());

                                            if (modificacion.getCodigoSolicitudModificacion() != null) {
                                                modificacion.setCasilleroIepi(cambioCasillero.getNuevoCasillero());
                                                c.updatePpdiSolicitudModificacion(modificacion);
                                            }
                                            updateModificacion(renewal.getApplicationNumber(), cambioCasillero.getNuevoCasillero());
                                            cambioCasillero.setEstado("REALIZADO");
                                            cambioCasillero.setDocumento(file.getFileName());
                                            c.updateCambioCasillero(cambioCasillero);
                                            c.saveHistorial("CAMBIO CASILLERO", "CAMBIO CASILLERO", cambioCasillero.getTramite(), "CAMBIO CAS " + cambioCasillero.getCasilleroAnterior() + " -> " + cambioCasillero.getNuevoCasillero(), login.getUsuario().getId(), login.getNombre());
                                            PrimeFaces.current().ajax().addCallbackParam("hecho", true);
                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "EL TRÁMITE " + renewal.getApplicationNumber() + " HA SIDO ASIGNADO\n"
                                                    + "AL CASILLERO " + cambioCasillero.getNuevoCasillero() + " DE " + cambioCasillero.getTitularCasilleroNuevo() + " SATISFACTORIAMENTE");

                                            limpiarDatos();

                                        } else {
                                            PrimeFaces.current().ajax().addCallbackParam("hecho", false);
                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL REALIZAR EL CAMBIO DE CASILLERO INTENTE MÁS TARDE");
                                        }

                                    } else {
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE PUDO ENVIAR LA NOTIFICACIÓN A LOS CORREOS DE LOS TITULARES DE LOS CASILLEROS, POR LO TANTO NO SE PROCEDIÓ A REALIZAR EL CAMBIO DE CASILLERO");
                                    }
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HAY UN PROBLEMA CON EL EXPEDIENTE DEL TRÁMITE " + cambioCasillero.getTramite() + ", POR LO TANTO NO SE PROCEDIÓ A REALIZAR EL CAMBIO DE CASILLERO");
                                }

                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA CON EL ARCHIVO ADJUNTO");
                            }
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA CON EL ARCHIVO ADJUNTO");
                        }

                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ EL NUEVO CASILLERO");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "EL NUEVO CASILLERO DEBE SER DIFERENTE AL CASILLERO ACTUAL DEL TRÁMITE");
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "ASEGÚRESE DE HABER DESCARGADO LA PROVIDENCIA Y LUEGO DE HABER ADJUNTADO ESTE DOCUMENTO FIRMADO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ EL CERTIFICADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public boolean uploadDocumentoToExpedient(UploadedFile documento, int renewalId) throws IOException {
        if (documento != null) {
//            String pdfName = file.getFileName();
            String ruta = "/var/www/html/solicitudes/media/files/renewal_forms/" + renewalId + "/" + file.getFileName();
            Codekru cod = new Codekru(130);
            cod.copyAInputStreamToRemoteMachine(file.getInputStream(), ruta);
//                Files.copy(file.getInputStream(), fileaux.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } else {
            return false;
        }
    }

    public boolean sendMail(CambioCasillero cambio, UploadedFile documento) throws IOException {
        Mailer mailer = new Mailer();
        String htmlBody = "<br/>Estimado usuario,<br/><br/>Sírvase a encontrar la notificación adjunta.";
        String mailFrom = "casilleros@senadi.gob.ec";
        String mailTo = cambio.getCorreoTitularAnterior() + ", " + cambio.getCorreoTitularNuevo();

        if (mailer.sendEmailWithAttachDocument(documento.getInputStream(), mailFrom, mailTo, "NOTIFICACIÓN CAMBIO CASILLERO - SENADI", htmlBody, documento.getFileName())) {

            System.out.println("Correo con documento " + documento.getFileName() + " enviado a: " + mailTo);
            return true;
        } else {
            System.out.println("No se envió el correo a " + mailTo + ", documento: " + documento.getFileName());
            return false;
        }
    }

    public boolean updateModificacion(String solicitud, String casillero) {
        Controlador c = new Controlador();
        CambioDomicilio camd = c.getCambioDomicilioBySolicitud(solicitud);
        if (camd.getId() != null) {
            camd.setCasilleroSenadi(casillero);
            c.updateCambioDomicilio(camd);
        } else {
            CambioNombre cambn = c.getCambioNombreBySolicitud(solicitud);
            if (cambn.getId() != null) {
                cambn.setCasilleroSenadi(casillero);
                c.updateCambioNombre(cambn);
            } else {
                PrendaComercial prend = c.getPrendaComercialBySolicitud(solicitud);
                if (prend.getId() != null) {
                    prend.setCasilleroSenadi(casillero);
                    c.updatePrendaComercial(prend);
                } else {
                    LicenciaUso licu = c.getLicenciaUsoBySolicitud(solicitud);
                    if (licu.getId() != null) {
                        licu.setCasilleroSenadi(casillero);
                        c.updateLicenciaUso(licu);
                    } else {
                        SubLicenciaUso subl = c.getSublicenciaUsoBySolicitud(solicitud);
                        if (subl.getId() != null) {
                            subl.setCasilleroSenadi(casillero);
                            c.updateSublicenciaUso(subl);
                        } else {
                            //para indicar que se ha entregado certificado digital firmado
                            Transferencia tranaux = c.getTransferenciaBySolSenadi(solicitud);
                            if (tranaux.getId() != null) {
                                tranaux.setCasilleroSenadi(casillero);
                                c.updateTransferencia(tranaux);
                            } else {
                                //para indicar que se ha realizado una notificación digital firmado
                                Notificacion notaux = c.getNotificacionBySolSenadi(solicitud);
                                if (notaux.getId() != null) {
                                    notaux.setCasilleroSenadi(casillero);
                                    c.updateNotificacion(notaux);
                                } else {
                                    Desistimiento desaux = c.getDesistidasBySolSenadi(solicitud);
                                    if (desaux.getId() != null) {
                                        desaux.setCasilleroSenadi(casillero);
                                        c.updateDesistimiento(desaux);
                                    } else {
                                        Caducada cadaux = c.getCaducadaBySolSenadi(solicitud);
                                        if (cadaux.getId() != null) {
                                            cadaux.setCasilleroSenadi(casillero);
                                            c.updateCaducada(cadaux);
                                        } else {
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public void buscarCasillero(ActionEvent ae) {
        FacesMessage msg = null;
        if (renewal != null && renewal.getId() != null) {
            if (criterioOwner != null && !criterioOwner.trim().isEmpty()) {
                System.out.println("criterio owner: " + criterioOwner);
                Controlador c = new Controlador();
                owners = new ArrayList<>();

                owners = c.getOwnersByCriteria(criterioOwner);

                if (owners.isEmpty()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "AVISO", "NO SE ENCONTRARON RESULTADOS");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CONSULTA REALIZADA");
                }

            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "INGRESE UN CRITERIO DE BÚSQUEDA VÁLIDO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ LA MODIFICACIÓN CORRECTAMENTE");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void seleccionarCasillero(ActionEvent ae) {
        FacesMessage msg = null;
        if (cambioCasillero != null) {
            owner = (Owner) ownerDataTable.getRowData();
            if (owner.getId() != null) {
                cambioCasillero.setNuevoCasillero(owner.getCasillero());
                cambioCasillero.setTitularCasilleroNuevo(owner.getFirstname() + " " + owner.getLastname());
                cambioCasillero.setCorreoTitularNuevo(owner.getEmail());
                owners = new ArrayList<>();
                criterioOwner = "";
                cambioc = true;
                confirmChangeMessage = "SEGURO DE CAMBIAR EL CASILLERO " + cambioCasillero.getCasilleroAnterior() + " DEL TRÁMITE " + cambioCasillero.getTramite() + "\nPOR EL CASILLERO " + cambioCasillero.getNuevoCasillero() + " ?";
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CASILLERO " + owner.getCasillero() + " SELECCIONADO CORRECTAMENTE");
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "HUBO UN PROBLEMA AL SELECCIONAR EL CASILLERO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE CARGÓ EL CERTIFICADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void validarProvidencia(CambioCasillero caux) {
        FacesMessage msg = null;
        if (caux != null) {
            if (caux.getDocumento() != null && !caux.getDocumento().trim().isEmpty()) {
                Controlador c = new Controlador();

                RenewalForm rf = c.getRenewalFormsByApplicationNumber(caux.getTramite());
                if (rf.getId() != null) {

                    String rutaux = "https://registro.propiedadintelectual.gob.ec/solicitudes/media/files/renewal_forms/" + rf.getId() + "/" + caux.getDocumento();

                    System.out.println("rutaprovidencia: " + rutaux);
                    PrimeFaces.current().ajax().addCallbackParam("viewdoc", true);
                    PrimeFaces.current().ajax().addCallbackParam("view", rutaux);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "PROVIDENCIA CARGADA");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", "CASILLERO " + owner.getCasillero() + " NO SE ENCONTRÓ LA MODIFICACIÓN ASOCIADA");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "NO SE ENCONTRÓ EL DOCUMENTO");
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "ERROR", "NO SE CARGÓ CORRECTAMENTE EL REGISTRO SELECCIONADO");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return the criterioOwner
     */
    public String getCriterioOwner() {
        return criterioOwner;
    }

    /**
     * @param criterioOwner the criterioOwner to set
     */
    public void setCriterioOwner(String criterioOwner) {
        this.criterioOwner = criterioOwner;
    }

    /**
     * @return the owners
     */
    public List<Owner> getOwners() {
        return owners;
    }

    /**
     * @param owners the owners to set
     */
    public void setOwners(List<Owner> owners) {
        this.owners = owners;
    }

    /**
     * @return the ownersFiltrados
     */
    public List<Owner> getOwnersFiltrados() {
        return ownersFiltrados;
    }

    /**
     * @param ownersFiltrados the ownersFiltrados to set
     */
    public void setOwnersFiltrados(List<Owner> ownersFiltrados) {
        this.ownersFiltrados = ownersFiltrados;
    }

    /**
     * @return the owner
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * @return the ownerDataTable
     */
    public UIData getOwnerDataTable() {
        return ownerDataTable;
    }

    /**
     * @param ownerDataTable the ownerDataTable to set
     */
    public void setOwnerDataTable(UIData ownerDataTable) {
        this.ownerDataTable = ownerDataTable;
    }

    /**
     * @return the renewal
     */
    public RenewalForm getRenewal() {
        return renewal;
    }

    /**
     * @param renewal the renewal to set
     */
    public void setRenewal(RenewalForm renewal) {
        this.renewal = renewal;
    }

    /**
     * @return the cambioc
     */
    public boolean isCambioc() {
        return cambioc;
    }

    /**
     * @param cambioc the cambioc to set
     */
    public void setCambioc(boolean cambioc) {
        this.cambioc = cambioc;
    }

    /**
     * @return the confirmChangeMessage
     */
    public String getConfirmChangeMessage() {
        return confirmChangeMessage;
    }

    /**
     * @param confirmChangeMessage the confirmChangeMessage to set
     */
    public void setConfirmChangeMessage(String confirmChangeMessage) {
        this.confirmChangeMessage = confirmChangeMessage;
    }

    /**
     * @return the login
     */
    public LoginBean getLogin() {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(LoginBean login) {
        this.login = login;
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
     * @return the file
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }

    /**
     * @return the numtramite
     */
    public String getNumtramite() {
        return numtramite;
    }

    /**
     * @param numtramite the numtramite to set
     */
    public void setNumtramite(String numtramite) {
        this.numtramite = numtramite;
    }

    /**
     * @return the cambiosCasillero
     */
    public List<CambioCasillero> getCambiosCasillero() {
        return cambiosCasillero;
    }

    /**
     * @param cambiosCasillero the cambiosCasillero to set
     */
    public void setCambiosCasillero(List<CambioCasillero> cambiosCasillero) {
        this.cambiosCasillero = cambiosCasillero;
    }

    /**
     * @return the cambiosCasilleroFiltrados
     */
    public List<CambioCasillero> getCambiosCasilleroFiltrados() {
        return cambiosCasilleroFiltrados;
    }

    /**
     * @param cambiosCasilleroFiltrados the cambiosCasilleroFiltrados to set
     */
    public void setCambiosCasilleroFiltrados(List<CambioCasillero> cambiosCasilleroFiltrados) {
        this.cambiosCasilleroFiltrados = cambiosCasilleroFiltrados;
    }

    /**
     * @return the cambiosCasilleroDataTable
     */
    public UIData getCambiosCasilleroDataTable() {
        return cambiosCasilleroDataTable;
    }

    /**
     * @param cambiosCasilleroDataTable the cambiosCasilleroDataTable to set
     */
    public void setCambiosCasilleroDataTable(UIData cambiosCasilleroDataTable) {
        this.cambiosCasilleroDataTable = cambiosCasilleroDataTable;
    }

    /**
     * @return the busqueda
     */
    public boolean isBusqueda() {
        return busqueda;
    }

    /**
     * @param busqueda the busqueda to set
     */
    public void setBusqueda(boolean busqueda) {
        this.busqueda = busqueda;
    }
}
