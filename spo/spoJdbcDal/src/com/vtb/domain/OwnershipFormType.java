package com.vtb.domain;

/**
 * VtbObject "Организационно-правовые формы"
 * @author IShafigullin
 */
public class OwnershipFormType extends VtbObject {

    private static final long serialVersionUID = 1617548421634985240L;
    private ru.masterdm.compendium.domain.crm.OwnershipFormType reference;
    
   public OwnershipFormType(Integer aId, String aName) {
       reference = new ru.masterdm.compendium.domain.crm.OwnershipFormType(aId, aName); 
    }

    public OwnershipFormType(Integer aId) {
        reference = new ru.masterdm.compendium.domain.crm.OwnershipFormType(aId);
    }

    @Override
    public boolean equals(Object anObject) {
        if (anObject == null) {
            return false;
        }
        if (!(anObject instanceof OwnershipFormType)) {
            return false;
        }
        OwnershipFormType aLimitType = (OwnershipFormType) anObject;
        if (reference == null) {
            if (aLimitType.getReference() == null) return true;
            else return false;
        } else 
            return getReference().equals(aLimitType.getReference());
    }

    public String toString() {
        if (reference == null) return "";
        return reference.toString();
    }

    public ru.masterdm.compendium.domain.crm.OwnershipFormType getReference() {
        return reference;
    }

    public void setReference(
            ru.masterdm.compendium.domain.crm.OwnershipFormType reference) {
        this.reference = reference;
    }

    public Integer getCode() {
        return reference.getCode();
    }

    public Integer getId() {
        return reference.getId();
    }

    public String getName() {
        return reference.getName();
    }

    public void setCode(Integer code) {
        reference.setCode(code);
    }

    public void setId(Integer id) {
        reference.setId(id);
    }

    public void setName(String name) {
        reference.setName(name);
    }

    public String getAssociation() {
        return reference.getAssociation();
    }

    public String getAssociationStr() {
        return reference.getAssociationStr();
    }

    public void setAssociation(String association) {
        reference.setAssociation(association);
    }
}
