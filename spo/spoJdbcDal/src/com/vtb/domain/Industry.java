package com.vtb.domain;
/**
 * VtbObject "отраслей"
 * @author IShafigullin
 * 
 */
public class Industry extends VtbObject {
    private static final long serialVersionUID = 1L;
    private ru.masterdm.compendium.domain.crm.Industry reference;
    
    public Industry() {
        super();
        reference = new ru.masterdm.compendium.domain.crm.Industry();
    }

	public Industry(String aId, String aName) {
	    reference = new ru.masterdm.compendium.domain.crm.Industry(aId,aName);
    }
    
    public Industry(String aId) {
        reference = new ru.masterdm.compendium.domain.crm.Industry(aId);
    }

	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof Industry)) {
			return false;
		}
		Industry aIndustry = (Industry) anObject;
		if (reference == null) {
		    if (aIndustry.getReference() == null) return true;
		    else return false;
		} else 
		    return getReference().equals(aIndustry.getReference());
	}

   @Override
   public String toString() {
       if (reference == null) return "";
       return reference.toString();
   }

    public ru.masterdm.compendium.domain.crm.Industry getReference() {
        return reference;
    }

    public void setReference(ru.masterdm.compendium.domain.crm.Industry reference) {
        this.reference = reference;
    }

    public String getCorpBlock() {
        return reference.getCorpBlock();
    }

    public String getId() {
        return reference.getId();
    }

    public String getName() {
        return reference.getName();
    }

    public String getRating() {
        return reference.getRating();
    }

    public String getType() {
        return reference.getType();
    }

    public String getTypeStr() {
        return reference.getTypeStr();
    }

    public void setCorpBlock(String corpBlock) {
        reference.setCorpBlock(corpBlock);
    }

    public void setId(String id) {
        reference.setId(id);
    }

    public void setName(String name) {
        reference.setName(name);
    }

    public void setRating(String rating) {
        reference.setRating(rating);
    }

    public void setType(String type) {
        reference.setType(type);
    }
}
