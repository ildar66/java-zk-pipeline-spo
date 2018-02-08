package com.vtb.domain;
/**
 * VtbObject "комиссий"
 * 
 * @author IShafigullin
 * 
 */
public class CommissionType extends VtbObject {
	private static final long serialVersionUID = -1973937479165237228L;
	private Integer id = null; //Код комиссий 
	private String name = null; //Имя комиссий 

	public CommissionType() {
	    super();
    }
	
	
	public CommissionType(Integer aId, String aName) {
	    super();
	    setId(aId);
        setName(aName);
    }
    
    public CommissionType(Integer aId) {
        super();
        setId(aId);
        setName("none");
    }   

	
	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof CommissionType)) {
			return false;
		}
		CommissionType aCommissionType = (CommissionType) anObject;
		return aCommissionType.getId().intValue() == getId().intValue();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("CommissionType: ");
		sb.append(getId() + "(" + getName() + ")");
		//sb.append('\n');
		//sb.append(" IsActive: ");
		//sb.append(getIsActive());

		return sb.toString();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
