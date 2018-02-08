package com.vtb.domain;


/**
 * VtbObject "ОКВЭД компании"
 * 
 * @author IShafigullin
 * 
 */
public class Okved extends VtbObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id = null; // id "ОКВЭД"
	private String okved = null; // код оквэд
	private String name = null; // название
	private String description = null;// описание.
	private Okved parent = null; // родитель

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof Okved)) {
			return false;
		}
		Okved aOkved = (Okved) anObject;
		return aOkved.getId().intValue() == getId().intValue();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Okved: id=");
		sb.append(getId() + "(okved=" + getOkved() + ", name=" + getName() + ")");

		// sb.append('\n');
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

	public String getOkved() {
		return okved;
	}

	public void setOkved(String name) {
		this.okved = name;
	}

	public Okved(Integer aId, String aCode, String aName) {
		setId(aId);
		setOkved(aCode);
		setName(aName);
	}

	public Okved(Integer aId) {
		setId(aId);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Okved getParent() {
		if(parent == null){
			parent = new Okved(null);
		}
		return parent;
	}

	public void setParent(Okved parent) {
		this.parent = parent;
	}

}
