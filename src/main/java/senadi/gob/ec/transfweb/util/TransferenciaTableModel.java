/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.util;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import senadi.gob.ec.transfweb.model.Transferencia;

/**
 *
 * @author Michael Yanangómez
 */
public class TransferenciaTableModel extends AbstractTableModel {

    String titulo[] = {"cert_no", "fe_certificado", "sol_senadi", "fe_presentacion", "sign",
        "no_reg", "fe_registro", "denom", "tit_actual", "tit_anterior", "respons"};

    private List<Transferencia> filas;
    private Transferencia transferencia;

    public TransferenciaTableModel(List<Transferencia> filas) {
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
        setRenovacion(getFilas().get(rowIndex));

            switch (columnIndex) {
                case 0:
                    return getTransferencia().getCertificado()+"";
                case 1:
                    return Operaciones.formatDateToLarge(getTransferencia().getFechaCertificado());
                case 2:
                    return getTransferencia().getSolicitud();
                case 3:
                    return Operaciones.formatDateToLarge(getTransferencia().getFechaPresentacion());
                case 4:
                    return getTransferencia().getSigno();
                case 5:
                    return getTransferencia().getRegistro();
                case 6:
                    return Operaciones.formatDateToLarge(getTransferencia().getFechaRegistro());
                case 7:
                    return getTransferencia().getDenominacion();
                case 8: 
                    return getTransferencia().getTitularActual();
                case 9:
                    return getTransferencia().getTitularAnterior();
                case 10:
                    return getTransferencia().getResponsable();
                    
            }
            return null;
    }

    /**
     * @return the filas
     */
    public List<Transferencia> getFilas() {
        return filas;
    }

    /**
     * @param filas the filas to set
     */
    public void setFilas(List<Transferencia> filas) {
        this.filas = filas;
    }

    /**
     * @return the transferencia
     */
    public Transferencia getTransferencia() {
        return transferencia;
    }

    /**
     * @param transferencia the renovacion to set
     */
    public void setRenovacion(Transferencia transferencia) {
        this.transferencia = transferencia;
    }


}
