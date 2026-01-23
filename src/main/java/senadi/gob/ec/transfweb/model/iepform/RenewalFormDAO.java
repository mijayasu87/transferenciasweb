/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.model.iepform;

import senadi.gob.ec.transfweb.util.IepiFormDep;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import senadi.gob.ec.transfweb.model.iepdep.HallmarkForms;
import senadi.gob.ec.transfweb.model.iepdep.PatentForms;

/**
 *
 * @author michael
 */
public class RenewalFormDAO {

    public RenewalForm getRenewalFormsById(int idRenewal) {
        String query = "Select * from renewal_forms where id = " + idRenewal;
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            RenewalForm renewalForm = new RenewalForm();
            while (rs.next()) {
                renewalForm.setId(rs.getInt("id"));
                renewalForm.setFormId(rs.getInt("form_id"));
                renewalForm.setTransactionMotiveId(rs.getInt("transaction_motive_id"));
                renewalForm.setPaymentReceiptId(rs.getInt("payment_receipt_id"));
                renewalForm.setApplicationNumber(rs.getString("application_number"));
                renewalForm.setStatus(rs.getString("status"));
                renewalForm.setPowerAttorney(rs.getString("power_attorney"));
                renewalForm.setDiscountFile(rs.getString("discount_file"));
                renewalForm.setCreateDate(rs.getTimestamp("create_date"));
                renewalForm.setExpedient(rs.getString("expedient"));
                renewalForm.setExpedientDate(rs.getString("expedient_date"));
                renewalForm.setDebugId(rs.getInt("debug_id"));
                renewalForm.setApplicationDate(rs.getTimestamp("application_date"));
                renewalForm.setTransactionNumber(rs.getString("transaction_number"));
                renewalForm.setOwnerId(rs.getInt("owner_id"));
                renewalForm.setLicenseType(rs.getString("license_type"));
            }
            con.close();
            return renewalForm;
        } catch (Exception ex) {
            System.out.println("error en obtener datos renewalforms: " + ex);
            return new RenewalForm();
        }
    }

    public RenewalForm getRenewalFormsByApplication(String applicationNumber) {
        String query = "Select * from renewal_forms where application_number = '" + applicationNumber + "'";
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            RenewalForm renewalForm = new RenewalForm();
            while (rs.next()) {
                renewalForm.setId(rs.getInt("id"));
                renewalForm.setFormId(rs.getInt("form_id"));
                renewalForm.setTransactionMotiveId(rs.getInt("transaction_motive_id"));
                renewalForm.setPaymentReceiptId(rs.getInt("payment_receipt_id"));
                renewalForm.setApplicationNumber(rs.getString("application_number"));
                renewalForm.setStatus(rs.getString("status"));
                renewalForm.setPowerAttorney(rs.getString("power_attorney"));
                renewalForm.setDiscountFile(rs.getString("discount_file"));
                renewalForm.setCreateDate(rs.getTimestamp("create_date"));
                renewalForm.setExpedient(rs.getString("expedient"));
                renewalForm.setExpedientDate(rs.getString("expedient_date"));
                renewalForm.setDebugId(rs.getInt("debug_id"));
                renewalForm.setApplicationDate(rs.getTimestamp("application_date"));
                renewalForm.setTransactionNumber(rs.getString("transaction_number"));
                renewalForm.setOwnerId(rs.getInt("owner_id"));
                renewalForm.setLicenseType(rs.getString("license_type"));
            }
            con.close();
            return renewalForm;
        } catch (Exception ex) {
            System.out.println("error en obtener datos renewalforms: " + ex);
            return new RenewalForm();
        }
    }

    public FormTypes getFormTypesById(Integer id) {
        String query = "Select * from form_types where id = " + id;
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            FormTypes formTypes = new FormTypes();
            while (rs.next()) {
                formTypes.setId(rs.getInt("id"));
                formTypes.setFormId(rs.getInt("form_id"));
                formTypes.setTypeId(rs.getInt("type_id"));
            }
            con.close();
            return formTypes;
        } catch (Exception ex) {
            System.out.println("error en obtener datos form_types: " + ex);
            return new FormTypes();
        }
    }

    public Types getTypesById(Integer id) {
        String query = "Select * from types where id = " + id;
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            Types types = new Types();
            while (rs.next()) {
                types.setId(rs.getInt("id"));
                types.setParentId(rs.getInt("parent_id"));
                types.setName(rs.getString("name"));
                types.setAlias(rs.getString("alias"));
            }
            con.close();
            return types;
        } catch (Exception ex) {
            System.out.println("error en obtener datos types: " + ex);
            return new Types();
        }
    }

    public HallmarkForms getHallmarkFormsDepurada(Integer id) {
        String query = "Select * from hallmark_forms where id = ?";
        try {
            Connection con = IepiFormDep.doConnectionToDepurar();
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            HallmarkForms hallmark = new HallmarkForms();
            while (rs.next()) {
                hallmark.setId(rs.getInt("id"));
                hallmark.setOwnerId(rs.getInt("owner_id"));
                hallmark.setDenomination(rs.getString("denomination"));
                hallmark.setExpedient(rs.getString("expedient"));
                hallmark.setStatus(rs.getString("status"));
                hallmark.setCreateDate(rs.getString("create_date"));
                hallmark.setExpYear(rs.getString("exp_year"));
            }
            con.close();
            return hallmark;
        } catch (Exception ex) {
            System.out.println("error en obtener datos types: " + ex);
            return new HallmarkForms();
        }
    }

    public Integer getCasilleroByOwner(Integer idOwner) {
        String query = "Select * from lockers where owner_id = " + idOwner;
        try {
            Connection con = IepiFormDep.doConnectionToCasilleros();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            Integer casillero = 0;
            while (rs.next()) {
                casillero = rs.getInt("id");
            }
            con.close();
            return casillero;
        } catch (Exception ex) {
            System.out.println("error en obtener datos casillero: " + ex);
            return 0;
        }
    }

    public PersonRenewalName getPersonRenewalNameByIdRenewal(Integer idRenewal) {
        String query = "Select * from person_renewal_name where renewal_form_id = " + idRenewal;
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            PersonRenewalName prn = new PersonRenewalName();
            while (rs.next()) {
                prn.setRenewalFormId(rs.getInt("renewal_form_id"));
                prn.setPersonId(rs.getInt("person_id"));
                prn.setNewName(rs.getString("new_name"));
                prn.setTitleName(rs.getString("title_name"));
            }
            con.close();
            return prn;
        } catch (Exception ex) {
            System.out.println("error en obtener datos person_renewal_name: " + ex);
            return new PersonRenewalName();
        }
    }

    public List<PersonRenewal> getPersonRenewalTypeByIdRenewal(Integer idRenewal, String type) {
        String query = "Select * from person_renewal where renewal_form_id = " + idRenewal + " and type = '" + type + "'";
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            List<PersonRenewal> personsRenewal = new ArrayList<>();

            while (rs.next()) {
                PersonRenewal prn = new PersonRenewal();
                prn.setRenewalFormId(rs.getInt("renewal_form_id"));
                prn.setPersonId(rs.getInt("person_id"));
                prn.setType(rs.getString("type"));
                personsRenewal.add(prn);
            }
            con.close();
            return personsRenewal;
        } catch (Exception ex) {
            System.out.println("error en obtener datos person_renewal " + type + ": " + ex);
            return new ArrayList();
        }
    }

    public Person getPersonById(Integer id) {
        String query = "Select * from person where id = " + id;
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            Person person = new Person();
            while (rs.next()) {
                person.setId(rs.getInt("id"));
                person.setIdentificationNumber(rs.getString("identification_number"));
                person.setName(rs.getString("name"));
                person.setAddress(rs.getString("address"));
                person.setEmail(rs.getString("email"));
            }
            con.close();
            return person;
        } catch (Exception ex) {
            System.out.println("error en obtener datos person: " + ex);
            return new Person();
        }
    }

    public PaymentReceipt getPaymentReceiptById(Integer id) {
        String query = "Select * from payment_receipt where id = " + id;
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            PaymentReceipt payment = new PaymentReceipt();
            while (rs.next()) {
                payment.setId(rs.getInt("id"));
                payment.setVoucherNumber(rs.getString("voucher_number"));
                payment.setReceiptNumber(rs.getString("receipt_number"));;
                payment.setAmount(rs.getDouble("amount"));
                payment.setFecha(rs.getDate("date"));
            }
            con.close();
            return payment;
        } catch (Exception ex) {
            System.err.println("Error al obtener payment_receipt: " + ex);
            return new PaymentReceipt();
        }
    }

    public boolean updateRenewalForm(RenewalForm renewal) {
        String query = "Update renewal_forms set owner_id = " + renewal.getOwnerId() + " where application_number = '" + renewal.getApplicationNumber() + "' "
                + "and id = " + renewal.getId();
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            int n = pst.executeUpdate();

            con.close();
            if (n > 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception ex) {
            System.err.println("Error al cambiar el casillero de " + renewal.getApplicationNumber() + ": " + ex);
            return false;
        }
    }

    public PatentForms getPatentFormsDepurada(Integer id) {
        String query = "Select * from patent_forms where id = " + id;
        try {
            Connection con = IepiFormDep.doConnectionToDepurar();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            PatentForms patente = new PatentForms();
            while (rs.next()) {
                patente.setId(rs.getInt("id"));
                patente.setOwnerId(rs.getInt("owner_id"));
                patente.setTitle(rs.getString("title"));
                patente.setExpedient(rs.getString("expedient"));
                patente.setStatus(rs.getString("status"));
                patente.setCreateDate(rs.getString("create_date"));
                patente.setExpYear(rs.getString("exp_year"));
            }
            con.close();
            return patente;
        } catch (Exception ex) {
            System.out.println("error en obtener datos types: " + ex);
            return new PatentForms();
        }
    }

    public List<PersonRenewal> getPersonsRenewalByIdRenewalAndType(Integer idRenewal, String type) {
        String query = "Select * from person_renewal as pr "
                + "inner join person as p on p.id = pr.person_id "
                + "where pr.renewal_form_id = " + idRenewal + " and pr.type in (" + type + ")";
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            List<PersonRenewal> personsRenewal = new ArrayList<>();

            while (rs.next()) {
                PersonRenewal prn = new PersonRenewal();
                prn.setRenewalFormId(rs.getInt("renewal_form_id"));
                prn.setPersonId(rs.getInt("person_id"));
                prn.setType(rs.getString("type"));
                prn.setName(rs.getString("name"));
                prn.setAddress(rs.getString("address"));
                prn.setIdentificationNumber(rs.getString("identification_number"));
                prn.setIdentificationType(rs.getString("identification_type"));
                prn.setEmail(rs.getString("email"));
                personsRenewal.add(prn);
            }
            con.close();
            return personsRenewal;
        } catch (Exception ex) {
            System.out.println("error en obtener datos person_renewal " + type + ": " + ex);
            return new ArrayList();
        }
    }

    public List<PersonRenewalName> getPersonsRenewalNameByIdRenewal(Integer idRenewal) {
        String query = "Select * from person_renewal_name as prn "
                + "left join person_renewal_address as pra on pra.person_id = prn.person_id "
                + "left join person as p on p.id = prn.person_id "
                + "where prn.renewal_form_id = " + idRenewal + " and pra.renewal_form_id = " + idRenewal;
        try {
            Connection con = IepiFormDep.doConnectionToFormularios();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            List<PersonRenewalName> persons = new ArrayList<>();
            while (rs.next()) {
                PersonRenewalName person = new PersonRenewalName();
                person.setRenewalFormId(rs.getInt("renewal_form_id"));
                person.setPersonId(rs.getInt("person_id"));
                person.setNewName(rs.getString("new_name"));
                person.setTitleName(rs.getString("title_name"));
                person.setNewAddress(rs.getString("new_address"));
                person.setTitleAddress(rs.getString("title_address"));
                person.setName(rs.getString("name"));
                person.setIdentificationNumber(rs.getString("identification_number"));
                person.setIdentificationType(rs.getString("identification_type"));
                person.setAddress(rs.getString("address"));
                persons.add(person);
            }
            con.close();
            return persons;
        } catch (Exception ex) {
            System.out.println("error en obtener datos person_renewal_name: " + ex);
            return new ArrayList<>();
        }
    }
}
