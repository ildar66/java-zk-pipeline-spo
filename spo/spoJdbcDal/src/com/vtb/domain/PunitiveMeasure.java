package com.vtb.domain;
/**
 * VtbObject "Штрафных санкций"
 * 
 * @author IShafigullin
 * 
 */
public class PunitiveMeasure extends VtbObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5365169036115737496L;
	private Integer id = null; //Код "Штрафных санкций"
	private String name = null; //Имя "Штрафных санкций"

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof PunitiveMeasure)) {
			return false;
		}
		PunitiveMeasure aPunitiveMeasure = (PunitiveMeasure) anObject;
		return aPunitiveMeasure.getId().intValue() == getId().intValue();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("PunitiveMeasure: ");
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

	public PunitiveMeasure(Integer aId, String aName) {
		setId(aId);
		setName(aName);
	}
	
	public PunitiveMeasure(Integer aId) {
		setId(aId);
		setName("none");
	}	

}
