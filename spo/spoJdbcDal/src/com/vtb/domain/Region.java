package com.vtb.domain;
/**
 * VtbObject "регионов"
 * 
 * @author IShafigullin
 * 
 */
public class Region extends  VtbObject {
    private static final long serialVersionUID = 3168625084056882002L;
    private ru.masterdm.compendium.domain.crm.Region reference;
    
    public Region(Integer id) {
        reference = new ru.masterdm.compendium.domain.crm.Region(id);
    }

    public Region(Integer id, String name) {
        reference = new ru.masterdm.compendium.domain.crm.Region(id, name);
    }

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof Region)) {
			return false;
		}
		Region aRegion = (Region) anObject;
        if (reference == null) {
            if (aRegion.getReference() == null) return true;
            else return false;
        } else 
            return getReference().equals(aRegion.getReference());
	}

   @Override
   public String toString() {
       if (reference == null) return "";
       return reference.toString();
   }

    public ru.masterdm.compendium.domain.crm.Region getReference() {
        return reference;
    }

    public void setReference(ru.masterdm.compendium.domain.crm.Region reference) {
        this.reference = reference;
    }

    public Integer getId() {
        return reference.getId();
    }

    public String getName() {
        return reference.getName();
    }

    public String getRating() {
        return reference.getRating();
    }

    public void setId(Integer aid) {
        reference.setId(aid);
    }

    public void setName(String name) {
        reference.setName(name);
    }

    public void setRating(String rating) {
        reference.setRating(rating);
    }
}
