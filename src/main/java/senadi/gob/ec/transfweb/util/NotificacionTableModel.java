/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.util;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import senadi.gob.ec.transfweb.model.Notificacion;
import senadi.gob.ec.transfweb.model.Rooptions;

/**
 *
 * @author Michael Yanangómez
 */
public class NotificacionTableModel extends AbstractTableModel {

    String titulo[] = {"id", "solicitud", "fecha_presentacion", "notificacion", "fecha_notificacion", "registro","fecha_registro", 
        "denominacion", "signo", "titular_actual", "apoderado_representante_legal", "casillero_senadi","casillero_judicial","responsable", "rotext","fechaElaboracionN"};

    private List<Notificacion> filas;
    private Notificacion notificacion;

    public NotificacionTableModel(List<Notificacion> filas) {
        this.filas = filas;
    }

    @Override
    public int getRowCount() {
        return getFilas() != null ? getFilas().size() : 0;//retorna el numero de filas
    }

    @Override
    public int getColumnCount() {
        return titulo.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return titulo[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        setNotificacion(getFilas().get(rowIndex));

            switch (columnIndex) {
                case 0:
                    return getNotificacion().getId();
                case 1:
                    return getNotificacion().getSolicitud();                    
                case 2:
                    return Operaciones.formatDateToLarge(getNotificacion().getFechaPresentacion());                    
                case 3:
                    return getNotificacion().getNotificacion();
                case 4:
                    return Operaciones.formatDateToLarge(getNotificacion().getFechaNotificacion());                    
                case 5:
                    return getNotificacion().getRegistro();
                case 6:
                    return Operaciones.formatDateToLarge(getNotificacion().getFechaRegistro());
                case 7:
                    return getNotificacion().getDenominacion();
                case 8: 
                    return getNotificacion().getSigno();                    
                case 9:
                    return getNotificacion().getTitularActual();
                case 10:
                    return getNotificacion().getApeApodRepre();
                case 11:
                    return getNotificacion().getCasilleroSenadi();
                case 12:
                    return getNotificacion().getCasilleroJudicial();
                case 13:
                    return getNotificacion().getResponsable();
                case 14:
                    Controlador c = new Controlador();
                    List<Rooptions> ros = c.getRosBySolicitud(getNotificacion().getSolicitud());
                    String ro = "";
                    for (int i = 0; i < ros.size(); i++) {                        
                        ro+=(i+1)+".  "+ros.get(i).getRo();
                        if(i != ros.size()-1){
                            ro+="\n\n";
                        }
                    }
                    
                    return ro;
                case 15:
                    return Operaciones.formatDateToLarge(getNotificacion().getFechaElaboraNotificacion());
            }
            return null;
    }   

    /**
     * @return the filas
     */
    public List<Notificacion> getFilas() {
        return filas;
    }

    /**
     * @param filas the filas to set
     */
    public void setFilas(List<Notificacion> filas) {
        this.filas = filas;
    }

    /**
     * @return the notificacion
     */
    public Notificacion getNotificacion() {
        return notificacion;
    }

    /**
     * @param notificacion the notificacion to set
     */
    public void setNotificacion(Notificacion notificacion) {
        this.notificacion = notificacion;
    }


}
