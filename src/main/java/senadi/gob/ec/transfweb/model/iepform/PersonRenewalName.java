/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.transfweb.model.iepform;

/**
 *
 * @author michael
 */
public class PersonRenewalName {
    private Integer renewalFormId;
    private Integer personId;
    private String newName;
    private String titleName;
    private String newAddress;
    private String titleAddress;
    private String name;
    private String identificationNumber;
    private String identificationType;
    private String address;

    /**
     * @return the renewalFormId
     */
    public Integer getRenewalFormId() {
        return renewalFormId;
    }

    /**
     * @param renewalFormId the renewalFormId to set
     */
    public void setRenewalFormId(Integer renewalFormId) {
        this.renewalFormId = renewalFormId;
    }

    /**
     * @return the personId
     */
    public Integer getPersonId() {
        return personId;
    }

    /**
     * @param personId the personId to set
     */
    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    /**
     * @return the newName
     */
    public String getNewName() {
        return newName;
    }

    /**
     * @param newName the newName to set
     */
    public void setNewName(String newName) {
        this.newName = newName;
    }

    /**
     * @return the titleName
     */
    public String getTitleName() {
        return titleName;
    }

    /**
     * @param titleName the titleName to set
     */
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    /**
     * @return the newAddress
     */
    public String getNewAddress() {
        return newAddress;
    }

    /**
     * @param newAddress the newAddress to set
     */
    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }

    /**
     * @return the titleAddress
     */
    public String getTitleAddress() {
        return titleAddress;
    }

    /**
     * @param titleAddress the titleAddress to set
     */
    public void setTitleAddress(String titleAddress) {
        this.titleAddress = titleAddress;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the identificationNumber
     */
    public String getIdentificationNumber() {
        return identificationNumber;
    }

    /**
     * @param identificationNumber the identificationNumber to set
     */
    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    /**
     * @return the identificationType
     */
    public String getIdentificationType() {
        return identificationType;
    }

    /**
     * @param identificationType the identificationType to set
     */
    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }
}
