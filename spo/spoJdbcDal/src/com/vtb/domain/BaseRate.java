package com.vtb.domain;

/**
 * VtbObject "базовых ставок"
 * 
 * @author IShafigullin
 * 
 */
public class BaseRate extends VtbObject {
	private static final long serialVersionUID = -8730905909586916883L;
	
	private Integer id = null; // id "базовых ставок"
	private Integer code = null; // Код
	private String name = null; // Имя
	private String description = null;// описание

   public BaseRate() {
       super();
   }

   public BaseRate(Integer aId, String aName) {
       super();
       setId(aId);
       setName(aName);
    }

    public BaseRate(Integer aId) {
        super();
        setId(aId);
        setName("none");
    }

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof BaseRate)) {
			return false;
		}
		BaseRate aBaseRate = (BaseRate) anObject;
		return aBaseRate.getId().intValue() == getId().intValue();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("BaseRate: ");
		sb.append(getId() + "(name=" + getName() + ", code=" + getCode()+")");

		//sb.append('\n');
		// sb.append(" IsActive: ");
		// sb.append(getIsActive());

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

}
