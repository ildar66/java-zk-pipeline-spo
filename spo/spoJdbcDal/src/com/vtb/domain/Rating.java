package com.vtb.domain;

import java.util.Date;

public class Rating extends VtbObject {
    private static final long serialVersionUID = 1L;
    private ru.masterdm.compendium.domain.crm.Rating reference;

    public Rating() {
      super();
      reference = new ru.masterdm.compendium.domain.crm.Rating();
    }

    public boolean equals(Object anObject) {
        if (anObject == null) {
            return false;
        }
        if (!(anObject instanceof Rating)) {
            return false;
        }
        Rating aRating = (Rating) anObject;
        if (reference == null) {
            if (aRating.getReference() == null) return true;
            else return false;
        } else 
            return getReference().equals(aRating.getReference());
    }

   @Override
   public String toString() {
       if (reference == null) return "";
       return reference.toString();
   }

    
    public ru.masterdm.compendium.domain.crm.Rating getReference() {
        return reference;
    }

    public void setReference(ru.masterdm.compendium.domain.crm.Rating reference) {
        this.reference = reference;
    }

    public String getBranch() {
        return reference.getBranch();
    }

    public String getRating() {
        return reference.getRating();
    }

    public Date getrDate() {
        return reference.getrDate();
    }

    public String getRegion() {
        return reference.getRegion();
    }

    public String getType() {
        return reference.getType();
    }

    public void setBranch(String branch) {
        reference.setBranch(branch);
    }

    public void setRating(String rating) {
        reference.setRating(rating);
    }

    public void setrDate(Date date) {
        reference.setrDate(date);
    }

    public void setRegion(String region) {
        reference.setRegion(region);
    }

    public void setType(String type) {
        reference.setType(type);
    }
}
