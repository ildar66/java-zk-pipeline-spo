package com.vtb.domain;
/**
 * VtbObject "рейтингов"
 * 
 * @author IShafigullin
 * 
 */
public class RatingType extends VtbObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7617847265718332368L;
	private Integer id = null; //Код рейтингов 
	private String name = null; //Имя рейтингов 

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof RatingType)) {
			return false;
		}
		RatingType aRatingType = (RatingType) anObject;
		return aRatingType.getId().intValue() == getId().intValue();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("RatingType: ");
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

	public RatingType(Integer aId, String aName) {
		setId(aId);
		setName(aName);
	}
	
	public RatingType(Integer aId) {
		setId(aId);
		setName("none");
	}	

}
