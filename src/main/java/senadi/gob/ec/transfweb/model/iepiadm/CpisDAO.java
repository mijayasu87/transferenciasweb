/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.model.iepiadm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import senadi.gob.ec.transfweb.util.IepiFormDep;

/**
 *
 * @author micharesp
 */
public class CpisDAO {
    public List<Cpis> getCpisByTramiteOCDI(String criteria) {
        //String query = "Select * from cpis where current_procedure like '"+criteria+"'";
        String query = "Select * from cpis where current_procedure like '%"+criteria+"%'";
        try {
            Connection con = IepiFormDep.doConnectionToIepiAdmin();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            List<Cpis> cpis = new ArrayList<>();
            while (rs.next()) {
                Cpis cpi = new Cpis();
                cpi.setBoardMember(rs.getString("board_member"));
                cpi.setCommitteeEntryDate(rs.getDate("committee_entry_date"));
                cpi.setCpiResource(rs.getString("cpi_resource"));
                cpi.setCpiYear(rs.getInt("cpi_year"));
                cpi.setCurrentProcedure(rs.getString("current_procedure"));
                cpi.setDenomination(rs.getString("denomination"));
                cpi.setEarlierProcedure(rs.getString("earlier_procedure"));
                cpi.setHall(rs.getString("hall"));
                cpi.setLastNotificationDate(rs.getDate("last_notification_date"));
                cpi.setRecurrent(rs.getString("recurrent"));
                cpi.setResolutionDate(rs.getDate("resolution_date"));
                cpi.setResolutionNumber(rs.getString("resolution_number"));
                cpi.setResourceDate(rs.getDate("resource_date"));
                cpi.setStatus(rs.getString("status"));
                cpi.setSubject(rs.getString("subject"));
                cpi.setWrittenFile(rs.getString("written_file"));
                cpis.add(cpi);
            }
            con.close();
            return cpis;
        } catch (Exception ex) {
            System.out.println("error en obtener datos cpis: " + ex);
            return new ArrayList<>();
        }
    }
    
    public boolean updateCPI(Cpis cpi) {
        String query = "Update cpis set denomination = '" + cpi.getDenomination() + "' where current_procedure = '" + cpi.getCurrentProcedure()+ "' ";
        try {
            Connection con = IepiFormDep.doConnectionToIepiAdmin();
            PreparedStatement pst = con.prepareStatement(query);
            int n = pst.executeUpdate();

            con.close();
            if (n > 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception ex) {
            System.err.println("Error al editar denominación del cpi " + cpi.getCurrentProcedure() + ": " + ex);
            return false;
        }
    }
    
    
}
