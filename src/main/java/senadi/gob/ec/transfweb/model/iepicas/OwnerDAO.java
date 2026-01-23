/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.transfweb.model.iepicas;

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
public class OwnerDAO {

    public List<Owner> getOwnersByCriteria(String criteria) {
        String query = "Select o.id, o.firstname, o.lastname,o.document_type,"
                + "o.document,o.email,o.phone,o.mobile,o.type,o.status,o.legal_firstname,o.legal_lastname,"
                + "lo.id as casillero, o.law_firm "
                + "from owners o "
                + "inner join lockers as lo on o.id = lo.owner_id "
                + "where o.document like '%" + criteria + "%' or o.firstname like '%" + criteria + "%' "
                + "or o.lastname like '%" + criteria + "%' or o.email like '%" + criteria + "%' or lo.id like '" + criteria + "' order by lo.id limit 300";
        try {
            Connection con = IepiFormDep.doConnectionToCasilleros();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            List<Owner> owners = new ArrayList<>();
            while (rs.next()) {
                Owner owner = new Owner();
                owner.setId(rs.getInt("id"));
                owner.setFirstname(rs.getString("firstname"));
                owner.setLastname(rs.getString("lastname"));
                owner.setDocument_type(rs.getString("document_type"));
                owner.setDocument(rs.getString("document"));
                owner.setEmail(rs.getString("email"));
                owner.setPhone(rs.getString("phone"));
                owner.setMobile(rs.getString("mobile"));
                owner.setType(rs.getString("type"));
                owner.setLaw_firm(rs.getString("law_firm"));
                owner.setStatus(rs.getString("status"));
                owner.setLegal_firstname(rs.getString("legal_firstname"));
                owner.setLegal_lastname(rs.getString("legal_lastname"));
                owner.setCasillero(rs.getString("casillero"));
                owner.setLaw_firm(rs.getString("law_firm"));
                owners.add(owner);
            }
            con.close();
            return owners;
        } catch (Exception ex) {
            System.out.println("error en obtener datos owners: " + ex);
            return new ArrayList<>();
        }
    }

    public Owner getOwnerById(int owner_id) {
        String query = "Select o.id, o.firstname, o.lastname,o.document_type,"
                + "o.document,o.email,o.phone,o.mobile,o.type,o.law_firm,o.status,o.legal_firstname,o.legal_lastname,"
                + "lo.id as casillero "
                + "from owners o "
                + "inner join lockers as lo on o.id = lo.owner_id "
                + "where o.id = " + owner_id;
        try {
            Connection con = IepiFormDep.doConnectionToCasilleros();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            Owner owner = new Owner();
            while (rs.next()) {
                owner.setId(rs.getInt("id"));
                owner.setFirstname(rs.getString("firstname"));
                owner.setLastname(rs.getString("lastname"));
                owner.setDocument_type(rs.getString("document_type"));
                owner.setDocument(rs.getString("document"));
                owner.setEmail(rs.getString("email"));
                owner.setPhone(rs.getString("phone"));
                owner.setMobile(rs.getString("mobile"));
                owner.setType(rs.getString("type"));
                owner.setLaw_firm(rs.getString("law_firm"));
                owner.setStatus(rs.getString("status"));
                owner.setLegal_firstname(rs.getString("legal_firstname"));
                owner.setLegal_lastname(rs.getString("legal_lastname"));
                owner.setCasillero(rs.getString("casillero"));
            }
            con.close();
            return owner;
        } catch (Exception ex) {
            System.out.println("error en obtener datos owner: " + ex);
            return new Owner();
        }
    }

    public boolean existsOwner(Integer casillero) {
        String query = "Select * from lockers where id = " + casillero;
        try {
            Connection con = IepiFormDep.doConnectionToCasilleros();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            int cas = 0;
            while (rs.next()) {
                cas = rs.getInt("id");
            }
            con.close();
            if (cas != 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            System.out.println("Error al verificar casillero " + casillero + ": " + ex);
            return false;
        }
    }
}
